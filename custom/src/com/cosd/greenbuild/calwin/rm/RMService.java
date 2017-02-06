package com.cosd.greenbuild.calwin.rm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cosd.greenbuild.calwin.dctm.utils.MultiThreadedDFCApp;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.IDfId;

/**
 * A simple server which queries for documents with a certain acl
 * (erms_default_create_acl by default) and calls Adobe LiveCycle to
 * apply Rights Management
 *
 * @author Andy.Taylor
 */
public class RMService extends MultiThreadedDFCApp {

	private final Argument aclArg;
	private final List<String> idsToProcess = new ArrayList<String>();
	private final Argument numPerBatchArg;

	public RMService() {
		super();
		this.aclArg = addArg("--acl", "erms_default_create_acl", "Documents having this acl will be matched.");
		super.numProcsArg.setDefaultValue("15");
		this.numPerBatchArg = addArg("--batch-size", "5", "Number of object id's to send to each processor.");
		log.info("pwd is: "+new File(".").getAbsolutePath());
}



	@Override
	protected int doRun(IDfSession session) throws Exception {
		RMProcessor.resetProcessing(session);
		return super.doRun(session);
	}

	@Override
	protected Processor takeAProcessor() throws Exception {
		String dql = "select r_object_id from cdcr_cfile_doc where "+
		 RMProcessor.RM_DOCUMENT_ID+"!='"+RMProcessor.RM_DOCUMENT_ID_PROCESSING+"' and " +
		 "acl_name='" + aclArg.getValue() + "' " +
		 "enable (return_top "+numPerBatchArg.getIntValue()+")";
		this.idsToProcess.clear();
		while (idsToProcess.isEmpty()) {
			log.info("Fetching objects with dql:\n" + dql);
			DfQuery q = new DfQuery(dql);
			IDfCollection coll = q.execute(session, DfQuery.DF_READ_QUERY);
			try {
				if (coll.next()) {
					do {
						IDfId objId = coll.getId("r_object_id");

						// indicate that it is being processed
						IDfPersistentObject obj = session.getObject(objId);
						obj.setString(RMProcessor.RM_DOCUMENT_ID, RMProcessor.RM_DOCUMENT_ID_PROCESSING);
						obj.fetch(null);
						obj.save();

						this.idsToProcess.add("" + objId);
					} while (coll.next());
				}
			} finally {
				coll.close();
			}
			if (idsToProcess.isEmpty()) {
				synchronized (Thread.currentThread()) {
					//System.out.println("No results, sleeping for 1m");
					Thread.sleep(60 * 1000);
				}
			}
		}

		return super.takeAProcessor();
	}

	@Override
	protected void onShutdown() {
	//	super.onShutdown();
	}

	@Override
	protected Processor createAProcessor() {
		return new RMProcessor();
	}

	@Override
	protected boolean processWith(Processor proc) throws Exception {
		RMProcessor rmProc = (RMProcessor) proc;
		rmProc.process(new ArrayList<String>(idsToProcess), null, session);
		return true;
	}

	public static void main(String[] args) {
		new RMService().run(args);
	}
}
