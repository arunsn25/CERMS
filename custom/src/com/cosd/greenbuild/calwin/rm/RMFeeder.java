package com.cosd.greenbuild.calwin.rm;

import java.util.ArrayList;
import java.util.List;

import com.cosd.greenbuild.calwin.dctm.utils.MultiThreadedDFCApp;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

public class RMFeeder extends MultiThreadedDFCApp {

	int procCount = 1;

	public static void main(String[] args) {
		new RMFeeder().run(args);
	}

	private final Argument numPerBatchArg;
	private final Argument cdcrNumArg;
	private final Argument cdcrExceptArg;
	private int numPerBatch;
	private final List<String> cdcrNums = new ArrayList<String>();
	private int doneCount = 0;
	private final Argument forceArg;
	private String currCDCRNum;
	private String cdcrNum;
	private boolean processingAll=false;

	public RMFeeder() {
		super();
		this.numPerBatchArg = addArg("--batch-size", "5", "Number of object id's to send to each processor.");
		this.cdcrNumArg = addArg(
				"--cdcr-num",
				"Single or span of cdcr numbers to run.  If a span is given a start and end cdcr number (inclusive) and will run for each cdcr-number.  CDCR number spans are in the format X12345-Y23456.  RM would be applied for all numbers from X12345-X99999 and Y00000-Y23456.  Can also be in CSV format for multi batches, ie C12345-C12348,Z00001-Z00003");
		this.cdcrExceptArg = addArg("--cdcr-except", "Do not include these CDCR numbers when processing.  In same format as "
				+ cdcrNumArg.getName());
		this.forceArg = addArg("--force", "Force reprocessing of documents which may have already been processed.");
		this.forceArg.setIsSingleton();

		super.numProcsArg.setDefaultValue("15");
	}

	@Override
	protected void onShutdown() {
		// just shutdown.
		// super.onShutdown();
	}

	@Override
	protected int doRun(IDfSession session) throws Exception {
		// reset start time, will start on first successful assignment
		super.startTime = 0;
		this.numPerBatch = numPerBatchArg.getIntValue();

		if (cdcrNumArg.wasProvided()) {
			parseSpanNumbers(cdcrNumArg.getValue().split(","), cdcrNums, false);
			log.info("Will process: " + cdcrNumArg.getValue() + ", " + cdcrNums.size() + " cdcr numbers");
		} else {
			log.info("Will process all non-rm documents.");
			this.processingAll=true;
		}

		if (cdcrExceptArg.wasProvided()) {
			int b4=cdcrNums.size();
			parseSpanNumbers(cdcrExceptArg.getValue().split(","), cdcrNums, true);
			log.info("Will skip: " + cdcrExceptArg.getValue() + ", " + (b4-cdcrNums.size()) + " cdcr numbers");
		}

		RMProcessor.resetProcessing(session);

		return super.doRun(session);

	}

	/**
	 * Adds cdcrnumbers to cue
	 *
	 * @param split
	 *            spans of cdcr numbers in C12345-C324456 format
	 */
	private void parseSpanNumbers(String[] spans, List<String> numList, boolean remove) {
		for (String span : spans) {
			span=span.toUpperCase();
			String[] split = span.split("-");
			if (split.length < 2) {
				if (!remove)
					numList.add(split[0]);
				else
					numList.remove(split[0]);
				return;
			}

			try {
				String prefix1 = split[0].substring(0, 1).toUpperCase();
				int suffix1 = Integer.parseInt(split[0].substring(1));
				String prefix2 = split[1].substring(0, 1).toUpperCase();
				int suffix2 = Integer.parseInt(split[1].substring(1));
				if (!prefix1.equals(prefix2))
					throw new IllegalArgumentException("Only similar prefixes are currently supported,ie C12345-C23456, "
							+ prefix1 + "!=" + prefix2 + " in " + cdcrNumArg.getValue());
				if (suffix2 < suffix1)
					throw new IllegalArgumentException("Expected 2nd value to be greater than first in " + span);
				for (int i = suffix1; i <= suffix2; i++) {
					String cdcrNum = prefix1 + padRight("" + i, "0", 5);
					if (!remove)
						numList.add(cdcrNum);
					else
						numList.remove(cdcrNum);
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Expected format C12345-C12367 for --cdcr-num-span, but was given: "
						+ cdcrNumArg.getValue());
			}
		}

	}

	private String padRight(String current, String padWith, int toLength) {
		if (current.length() >= toLength)
			return current;
		String result = current;
		for (int i = 0; i < (toLength - current.length()); i++)
			result = padWith + result;
		return result;
	}

	@Override
	protected Processor createAProcessor() {
		return new RMProcessor();
	}

	@Override
	protected boolean processWith(Processor proc) throws DfException {
		IDfCollection coll;
		if (processingAll) {
			coll = execQuery(null);
		} else if (cdcrNum == null) { // if finished last cdcrNum, start next
			if (cdcrNums.isEmpty()) {
				log.info("Past end.  Finishing.");
				return false;
			}
			this.cdcrNum = cdcrNums.remove(0);
			log.info("Now processing: "+cdcrNum);
			coll = execQuery(cdcrNum);
		} else if (cdcrNum!=null){ // continue current cdcrNum
			coll = execQuery(cdcrNum);
		} else {
			System.err.println("Unhandled condition!");
			return false;
		}

		if (!coll.next()) {
			coll.close();
			if (processingAll) {
				log.info("Finished all documents");
				returnProcessor(proc);
				return false;
			}
			// no more nums, done
			if (cdcrNums.isEmpty()) {
				log.info("Finished all documents");
			} else {
				// finished section, return processor and return to keep going with
				// next section
				log.info("Finished processing: " + cdcrNum);
			}
			returnProcessor(proc);
			cdcrNum = null;
			// finished if no more nums
			return !cdcrNums.isEmpty();
		}
		// if this is the first successful assignment, consider started.
		if (startTime == 0)
			startTime = System.currentTimeMillis();

		RMProcessor rmProc = (RMProcessor) proc;
		List<String> batchIds = new ArrayList<String>();
		try {
			do {
				IDfId currId = coll.getId("r_object_id");

				// indicate that it is being processed
				IDfPersistentObject obj = session.getObject(currId);
				obj.setString(RMProcessor.RM_DOCUMENT_ID, RMProcessor.RM_DOCUMENT_ID_PROCESSING);
				obj.save();

				batchIds.add(currId.toString());
			} while (coll.next());
		} finally {
			coll.close();
		}
		if (batchIds.size()>numPerBatch)
			//System.out.println("Wierd, there are "+batchIds.size());

		rmProc.process(batchIds, cdcrNum, session);

		return true;
	}

	@Override
	public synchronized void returnProcessor(Processor processor) {
		RMProcessor rmProc = (RMProcessor) processor;
		if (rmProc.idsToApply.size() > 0) {
			this.doneCount += rmProc.idsToApply.size();
			double ratePerSec = (doneCount) / ((System.currentTimeMillis() - startTime) / 1000.0);
			double ratePerSecTrim = ((int) (ratePerSec * 100.0)) / 100.0;
			log.info("Now finished: " + doneCount + " at " + ratePerSecTrim + " objects/second");
		}
		super.returnProcessor(processor);
	}

	private IDfCollection execQuery(String cdcrNum) throws DfException {
		if (cdcrNum != null && !cdcrNum.equals(this.currCDCRNum)) {
			String msg = "Now processing " + cdcrNum;
			log.info(msg);
			this.currCDCRNum = cdcrNum;
		} else if (log.isDebugEnabled()) {
			String msg = "Querying for "
					+ (cdcrNum == null ? "all unprocessed documents" : "more unprocessed documents in " + cdcrNum);
			log.debug(msg);
		}
		// does this redo rm docs... query for rmpdf
		// verify socket timeout
		String dql = "select r_object_id from cdcr_cfile_doc where section!=0 and section is not NULLINT and "+RMProcessor.RM_DOCUMENT_ID+" is nullstring ";
		if (!this.forceArg.wasProvided())
			dql += "and (rights_status IS NULLINT or rights_status < 4) ";
		if (cdcrNum != null)
			dql += "and cdcr_number='" + cdcrNum + "' ";
		// always exclude eforms
		dql += "and is_eform=0 ";
		// skip documents without scandate
		dql+="and scan_date is not nulldate ";
		// only not checked out
		dql += "and r_lock_owner=' ' ";
		// order by create
		dql += "order by r_creation_date";
		// only return a small number, as collection expires for many documents
		dql += " enable (return_top " + numPerBatch + ")";
		log.info("Fetching documents with DQL:\n"+dql);
		DfQuery q = new DfQuery(dql);
		return q.execute(session, DfQuery.DF_EXEC_QUERY);
	}

}
