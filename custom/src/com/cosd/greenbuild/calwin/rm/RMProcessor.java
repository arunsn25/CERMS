package com.cosd.greenbuild.calwin.rm;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.cosd.greenbuild.calwin.adobe.AdobeRESTClient;
import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConfig;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.utils.MultiThreadedApp.Processor;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

public class RMProcessor extends Processor implements COSDCalwinConstants {

	public static final String RM_DOCUMENT_ID_PROCESSING = "processing";
	public static final String RM_DOCUMENT_ID = "rm_document_id";
	List<String> idsToApply = new ArrayList<String>();
	private IDfSession session;
	private String cdcrNum;

	public RMProcessor() {
		super();
	}

	public Thread process(List<String> ids, String cdcrNum, IDfSession session) {
		this.idsToApply.clear();
		this.idsToApply.addAll(ids);
		this.session = session;
		this.cdcrNum = cdcrNum;
		return super.process();
	}

	@Override
	protected void doProcessing() throws Exception {
		callAdobe();
		resetUnprocessed();
	}

	private void resetUnprocessed() throws DfException {
		for (String curr : idsToApply) {
			try {
				IDfPersistentObject obj = session.getObject(new DfId(curr));
				String rmDocId = obj.getString(RM_DOCUMENT_ID);
				if (rmDocId.equals(RM_DOCUMENT_ID_PROCESSING)) {
					obj.setString(RM_DOCUMENT_ID, null);
					log.warn("In previous run, did not apply RM to object: " + curr+".   Resetting and will be reprocessed.");
					obj.fetch(null);
					obj.save();
				}
			} catch (DfException e) {
				log.warn("Error resetting: " + curr + ", ignoring.", e);
			}
		}
	}

	private void callAdobe() throws InterruptedException, RemoteException, IOException, DfException {
		if (idsToApply.isEmpty())
			return;

		String idCSV = "";
		for (String curr : idsToApply) {
			if (idCSV.length() != 0)
				idCSV += ",";
			idCSV += curr;
		}

		AdobeRESTClient client = null;
		try {
			COSDCalwinConfig config = MashupManager.getConfig(session);
			String appName = config.get(CONFIG.APP_RM, CONFIG.APP_RM_DEFAULT);
			String methodName = config.get(CONFIG.METHOD_SET_RM_POLICY, CONFIG.METHOD_SET_RM_POLICY_DEFAULT);
			client = new AdobeRESTClient(appName, methodName, config);
			client.addParameter("inStrDocIDs", idCSV);
			client.addParameter("dmThreadName", Thread.currentThread().getName());
			log.info(id + ") Processing " + idsToApply.size() + " ids in " + cdcrNum + " via: " + client.getURL());
			String result = client.invoke();
			if (result == null || !result.trim().toUpperCase().equals("TRUE"))
				throw new IOException("Error on processor " + getId() + ".  Check server side log.\n" + client);
		} catch (IOException e) {
			log.error("Error calling adobe with client:\n" + client);
			throw e;
		}
		// System.out.println("Finished:\n"+client);
	}

	/**
	 * Reset documents that are being processed. Call before processing to
	 * ensure any interrupted documents are reprocessed.
	 *
	 * @param session
	 * @throws DfException
	 */
	public static void resetProcessing(IDfSession session) throws DfException {
		String dql = "select r_object_id from cdcr_cfile_doc where " + RMProcessor.RM_DOCUMENT_ID + "='"
				+ RMProcessor.RM_DOCUMENT_ID_PROCESSING + "'";
		log.debug("Resetting processing documents with:\n" + dql);
		DfQuery q = new DfQuery(dql);
		int count = 0;
		long start = System.currentTimeMillis();
		IDfCollection coll = q.execute(session, DfQuery.DF_READ_QUERY);
		try {
			if (coll.next()) {
				do {
					IDfId objId = coll.getId("r_object_id");
					try {
						// indicate that it is being processed
						IDfPersistentObject obj = session.getObject(objId);
						obj.setString(RMProcessor.RM_DOCUMENT_ID, null);
						obj.fetch(null);
						obj.save();

						count++;
					} catch (DfException e) {
						log.warn("Error resetting: " + objId+".  Ignoring.", e);
					}
				} while (coll.next());
			}
		} finally {
			coll.close();
			log.info("Reset " + count + " documents in " + (System.currentTimeMillis() - start) + " ms");
		}

	}

}
