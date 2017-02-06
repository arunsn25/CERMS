package com.cosd.greenbuild.calwin.mashup;

import java.util.Date;

//import com.cosd.greenbuild.calwin.dctm.utils.DFCApplication;
import com.cosd.greenbuild.calwin.dctm.utils.MultiThreadedDFCApp;
import com.cosd.greenbuild.calwin.utils.Application;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.utils.CDCRUtils;
import com.documentum.fc.client.DfIdNotFoundException;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

/**
 * The background mashup processor. It's a standalone application, which creates
 * a number of idle mashup processors and repeatedly discovers cdcr_cfile_mashup
 * objects which have not been processed. When a mashup needing processing is
 * discovered, then an idle processor is taken, the id assigned and processing
 * is done asynchronously by the processor. The next mashup is taken and
 * assigned to another idle processor. This continues until all processors are
 * in use or there are no more mashups to be processed. Once a processor
 * finishes processing, then the processor removes itself from the busy queue
 * and back into the idle queue. The application is then notified, which will
 * then reuse the newly idle processor.
 * <p/>
 * See the {@link MultiThreadedDFCApp} which provides the logic for queues and
 * threading, the {@link DFCApplication} which provides an active documentum
 * session and the {@link Application} which facilitates command line
 * applications.
 *
 * @author Andy.Taylor
 *
 */
public class MashupService extends MultiThreadedDFCApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MashupService().run(args);
	}

	private MashupInfo nextInfo;
	private final Argument retryArg;
	// time to wait in ms before re-query when no mashups are found.
	private int retryInterval = 69 * 1000;
	private final CDCRUtils utils = new CDCRUtils();

	public MashupService() {
		super();
		this.retryArg = addArg("--retryInterval", "" + retryInterval, "Time in ms between queries for unprocessed mashups.");
	}

	@Override
	protected Processor createAProcessor() {
		try {
			return MashupManager.getInstance(session).createMashupProcessor(session);
		} catch (DfException e) {
			throw new RuntimeException("Error creating mashup.");
		}
	}

	@Override
	protected int doRun(IDfSession session) throws Exception {

		this.retryInterval = retryArg.getIntValue();

		log.info("Trimming");
		trimExpired();
		log.info("Resetting");
		resetMashups();

		return super.doRun(session);
	}

	private void resetMashups() throws DfException {
		// reset any mashups with state of running.
		String dql = "select r_object_id from cdcr_cfile_mashup where " + MashupInfo.ATTR_PROCESS_STATE + "='"
				+ MashupInfo.PROCESS_STATE_RUNNING + "'";
		DfQuery q = new DfQuery(dql);
		IDfCollection coll = q.execute(session, DfQuery.READ_QUERY);
		int count = 0;
		try {
			while (coll.next()) {
				try {
					IDfId curr = coll.getId("r_object_id");
					IDfPersistentObject obj = session.getObject(curr);
					obj.setString(MashupInfo.ATTR_PROCESS_STATE, MashupInfo.PROCESS_STATE_UNPROCESSED);
					obj.save();
					count++;
				} catch (DfException e) {
					e.printStackTrace();
				}
			}
		} finally {
			coll.close();
		}

		if (count > 0)
			log.info("Reset " + count + " mashups.");
	}

	@Override
	protected Processor takeAProcessor() throws Exception {
		IDfSysObject nextObj = takeNextSavedMashup(session);
		if (nextObj == null) {

			if (log.isDebugEnabled())
				log.debug("No background mashups found.  Waiting...");
			while (nextObj == null) {
				// try trim of expired, and if not deleted max then sleep
				if (!trimExpired()) {

					// no documents to mash, try again in a minute
					Thread.sleep(retryInterval);
				}
				if (!running)
					break;
				nextObj = takeNextSavedMashup(session);
				if (log.isDebugEnabled()&&nextObj==null)
					log.debug("Still no mashups to perform.");
			}
		}
		// shutdown when null
		if (nextObj == null) {
			System.exit(0);
			return null;
		}

		try {
			int retryCount = nextObj.getInt(MashupInfo.ATTR_RETRY_COUNT);
			retryCount++;
			nextObj.setString(MashupInfo.ATTR_PROCESS_STATE, MashupInfo.PROCESS_STATE_RUNNING);
			nextObj.setInt(MashupInfo.ATTR_RETRY_COUNT, retryCount);
			// occasional wierd DM_SYSOBJECT_E_VERSION_MISMATCH error if fetch
			// not done.
			nextObj.fetch(null);
			nextObj.save();

			this.nextInfo = new MashupInfo(nextObj);
			return super.takeAProcessor();
		} catch (Throwable e) {
			// handle the error, incrementing the retry count and setting the
			// retry after.
			log.error("Error loading document from: " + nextObj.getObjectId(), e);
			MashupInfo.handleError(nextObj, e);

			// try again
			return takeAProcessor();
		}
	}

	/**
	 *
	 * @return true if maximum number of objects deleted
	 * @throws DfException
	 */
	private boolean trimExpired() throws DfException {
		String trimDQL = "select r_object_id, " + MashupInfo.ATTR_RETENTION_DATE + " from cdcr_cfile_mashup where "
				+ MashupInfo.ATTR_RETENTION_DATE + "<DATE(NOW) and "+MashupInfo.ATTR_RETENTION_DATE+" is not nulldate";
		log.debug("Fetching mashups to delete with query: " + trimDQL);
		DfQuery q = new DfQuery(trimDQL);
		IDfCollection coll = q.execute(session, DfQuery.DF_EXEC_QUERY);
		int count = 0;
		try {
			if (coll.next()) {
				do {
					IDfId currId = coll.getId("r_object_id");
					Date expiredAt = coll.getTime(MashupInfo.ATTR_RETENTION_DATE).getDate();

					try {
						IDfDocument currObj = (IDfDocument) session.getObject(currId);
						if (currObj != null) {
							try {
								String objName = currObj.getObjectName();
								String owner = currObj.getString("mashup_owner");

								String[] additionalInfo = { "Object Name=[" + objName + "]", "Owner=[" + owner + "]",
										"Expired=[" + expiredAt + "]", };
								utils.createAudit(session, "" + currId, additionalInfo, COSDCalwinConstants.AUDIT_EVENT_MASHUP_DELETE);
								currObj.destroy();
							} catch (DfException e) {
								log.warn("Error deleting expired mashup: " + currId, e);
							}
						}
						count++;
						if (count >= 50) {
							log.debug("More objects still to delete");
							break;
						}
					} catch (DfIdNotFoundException e) {
						log.warn("Object for id  ["+currId+"] not found to delete.  Ignoring.");
					}
				} while (coll.next());
				log.info("Deleted " + count + " expired mashups.");
			} else if (log.isDebugEnabled())
				log.debug("No expired mashups to delete.");

		} finally {
			coll.close();
		}
		return count >= 50;
	}

	@Override
	protected void onShutdown() {
		// don't wait, just finish
		// super.onShutdown();
		running = false;
	}

	@Override
	protected boolean processWith(Processor proc) throws Exception {
		// System.out.println("Beginnning processing...");
		// have the processor process the mashup
		// calls adobe and does a foreground mashup, waiting for completion\

		MashupProcessor mp = (MashupProcessor) proc;
		mp.performMashup(nextInfo, session);
		nextInfo = null;
		return true; // keep going
	}

	public IDfSysObject takeNextSavedMashup(IDfSession sess) throws DfException {
		// this should use specific declared variables, ie
		// String
		// dql="select r_object_id from cdcr_cfile_mashup where process_state='unprocessed' order by r_creation_date";
		String dql = "select r_object_id from cdcr_cfile_mashup where " + MashupInfo.ATTR_PROCESS_STATE + "!='"
				+ MashupInfo.PROCESS_STATE_FINISHED + "' and " + MashupInfo.ATTR_PROCESS_STATE + "!='"
				+ MashupInfo.PROCESS_STATE_ERROR + "' and " + MashupInfo.ATTR_PROCESS_STATE + "!='"
				+ MashupInfo.PROCESS_STATE_RUNNING + "' and " + MashupInfo.ATTR_PROCESS_STATE + "!='"
				+ MashupInfo.PROCESS_STATE_FOREGROUND + "' and (" + MashupInfo.ATTR_RETRY_AFTER + " is null or "
				+ MashupInfo.ATTR_RETRY_AFTER + "<DATE(NOW)) " + "order by r_creation_date desc enable(return_top 1)";
		log.debug("Querying for mashups with:\n" + dql);
		DfQuery q = new DfQuery(dql);
		IDfCollection coll = q.execute(sess, DfQuery.EXEC_QUERY);
		try {
			if (!coll.next()) {
				log.debug("Nothing to process");
				return null;
			}
			IDfId id = coll.getId("r_object_id");
			IDfSysObject obj = (IDfSysObject) sess.getObject(id);
			if (log.isDebugEnabled())
				log.debug("Will process: " + obj.getTypeName() + " with id: " + id);
			return obj;
		} finally {
			coll.close();
		}
	}

}
