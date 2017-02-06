package com.cosd.greenbuild.calwin.mashup;

import java.io.IOException;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConfig;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants.CONFIG;
import com.cosd.greenbuild.calwin.utils.MultiThreadedApp.Processor;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

public abstract class MashupProcessor extends Processor {

	/** refresh config every this many millis, used for long running processors */
	private static final long CONFIG_REFRESH_INTERVAL = 10 * 60 * 1000;
	protected MashupInfo mashup;
	protected IDfSession sess;
	private DfId mashupId;
	protected COSDCalwinConfig config;
	// when the config was last read, if over config read interval, will be refreshed
	private long lastConfigRead;
	// time between retries in ms, can be override in config: CDCRConstants.CONFIG_MASHUP_RETRY_INTERVAL
	private long retryInterval=CONFIG.MASHUP_RETRY_INTERVAL_DEFAULT;

	/**
	 *  is only created by MashupManager
	 *  @see MashupManager#createMashupProcessor(IDfSession)
	 */
	protected MashupProcessor() {
		super();
	}

	/**
	 * Override as required to configure
	 * @param config
	 * @param sess
	 */
	public final void init(COSDCalwinConfig config, IDfSession sess) {
		this.config = config;
		this.lastConfigRead = System.currentTimeMillis();
		this.retryInterval=config.getLong(CONFIG.MASHUP_RETRY_INTERVAL,retryInterval);
		doInit(config, sess);
	}

	protected void doInit(COSDCalwinConfig config, IDfSession sess) {
		// override as required
	}

	public Thread performMashup(MashupInfo info, IDfSession sess) throws DfException {
		// re-read config for long running
		if (System.currentTimeMillis() - lastConfigRead > CONFIG_REFRESH_INTERVAL) {
			config.refresh(sess);
			this.lastConfigRead = System.currentTimeMillis();
		}
		//info.validate(); // To-Do: Change?/Remove?
		//System.out.println(getProcessorId() + ") Processing " + getMashup().getSourceObject().getObjectId());
		this.mashup = info;
		this.sess = sess;
		return super.process();
	}

	@Override
	protected final void doProcessing() throws Exception {
		try {
			IDfSysObject loadedFrom = mashup.getSourceObject();

			String msg = getId() + ") Performing mashup of " + mashup.getTitle() + "("+loadedFrom.getObjectId()+")";
			log.info(msg);
			//System.out.println(msg);

			this.mashupId = doMashup();

			assertId(mashupId);

			loadedFrom = (IDfSysObject) sess.getObject(mashupId);
			mashup.setSourceObject(loadedFrom);
			msg=getProcessorId() + ") Mashup was completed successfully, resulting in: " + mashupId;
			//System.out.println(msg);
			log.info(msg);

			loadedFrom.setString(MashupInfo.ATTR_PROCESS_STATE, MashupInfo.PROCESS_STATE_FINISHED);
			loadedFrom.save();

		} catch (Exception e) {
			System.err.println(getProcessorId() + ") Mashup failed");
			// handle the error, incrementing the error count and setting the retry after
			mashup.onError(e);
		}
	}

	private void assertId(IDfId mashupId) throws IOException {
		if (("" + mashupId).replace("0", "").length() == 0)
			throw new IOException("Unknown exception performing mashup, found 0000000000000000.  Check mashup server logs.");
	}

	protected abstract DfId doMashup() throws IOException;

	/**
	 * Gets the mashup id for the last mashup.
	 * @return
	 * @throws IllegalStateException if no mashup was done or previous mashup had error.
	 */
	public IDfId getMashupId() throws IllegalStateException {
		if (mashupId == null) {
			if (error != null)
				throw new IllegalStateException("The mashup failed with an error, so the id is not available. Previous error was:",
						error);
			else
				throw new IllegalStateException("A mashup has not yet been done");
		}
		return mashupId;
	}

	/**
	 * Save the existing mashup, stripping rights management and applying the password.
	 *
	 * @param id
	 * @param openPass
	 * @param permsPass
	 * @param session
	 * @return the id of the saved document
	 * @throws IOException
	 */
	public final String export(IDfId id, String openPass, String permsPass, IDfSession session) throws IOException { // IDfId
		if (openPass == null)
			throw new NullPointerException("The open password must not be null");
		if (permsPass == null)
			throw new NullPointerException("The permissions password must not be null");
		if (openPass.equals(permsPass))
			throw new IllegalArgumentException("The passwords must not be the same.");

		String result = doExport(id, openPass, permsPass, session);//IDfId objId
		//assertId(objId);
		return result; //objId
	}
	
	public final String export(IDfId id, IDfSession session) throws IOException { // IDfId
		String result = doExport(id, session);
		return result;
	}
	
	
	protected abstract String doExport(IDfId id, String openPass, String permsPass, IDfSession session) throws IOException; // IDfId

	protected abstract String doExport(IDfId id, IDfSession session) throws IOException; // IDfId
	
	public MashupInfo getMashup() {
		return mashup;
	}

}
