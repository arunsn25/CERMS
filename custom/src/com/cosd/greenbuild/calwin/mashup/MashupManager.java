package com.cosd.greenbuild.calwin.mashup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cosd.greenbuild.calwin.mashup.adobe.AdobeMashupProcessor;
import com.cosd.greenbuild.calwin.mashup.cts.CTSMashupProcessor;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConfig;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

public class MashupManager implements COSDCalwinConstants {

	private static final Logger log = Logger.getLogger(MashupManager.class);

	public enum MashupResultType {
		Background, Foreground;

		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	/**
	 * Result of a mashup instigation.  Type is either Background or Forground.  If foreground,
	 * the id is for the created mashup document.  If background the id is for the saved object
	 * which contains mashup info.  This saved object will be taken and the values used during
	 * background processing.
	 *
	 * @author Andy.Taylor
	 *
	 */
	public class MashupResult {

		public final MashupResultType type;
		public final IDfId id;

		public MashupResult(MashupResultType type, IDfId bgId) {
			this.type = type;
			this.id = bgId;
		}

		@Override
		public String toString() {
			return "Mashup was " + (type == MashupResultType.Foreground ? "produced as: " + id : "saved to ID: " + id);
		}

	}

	/*
	 */

	// the one and only manager instance
	private static MashupManager instance;
	private static Map<Integer, String> namesBySection = new HashMap<Integer, String>(); // TO-DO: Remove?

	// there can be no more files than in foreground mashup
	private int maxFGDocumentsLimit = CONFIG.MASHUP_FG_MAX_COUNT_DEFAULT;
	// maximum length for foreground mashup
	private long maxFGTotalLength=CONFIG.MASHUP_FG_MAX_LENGTH_DEFAULT;
	// maximum total length of contained documents, in bytes
	private long maxTotalLength = CONFIG.MASHUP_MAX_LENGTH_DEFAULT;

	private final Map<String, String> sectionNameByNumber = new HashMap<String, String>(); // TO-DO: Remove?

	// the class to be used for mashup processing, as determined by the config.
	private final Class<? extends MashupProcessor> procClass;
	private COSDCalwinConfig config;
	private static int maxRetries=CONFIG.MASHUP_MAX_RETRIES_DEFAULT;
	private static long retryInterval=CONFIG.MASHUP_RETRY_INTERVAL_DEFAULT;

	// via getInstance only
	protected MashupManager(Class<? extends MashupProcessor> procClass) {
		this.procClass = procClass;
	}

	protected void init(COSDCalwinConfig config, IDfSession sess) {
		this.config = config;
		this.maxFGDocumentsLimit = config.getInteger(CONFIG.MASHUP_FG_MAX_COUNT, this.maxFGDocumentsLimit);
		this.maxFGTotalLength = config.getLong(CONFIG.MASHUP_FG_MAX_LENGTH, this.maxFGTotalLength);
		// maximum total length of contained documents (for both fg and bg), in bytes. 0 to disable
		this.maxTotalLength = config.getLong(CONFIG.MASHUP_MAX_LENGTH, this.maxTotalLength);
		// could have a max retries for background mashups
		this.maxRetries = config.getInteger(CONFIG.MASHUP_MAX_RETRIES, this.maxRetries);
		this.retryInterval= config.getLong(CONFIG.MASHUP_RETRY_INTERVAL, this.retryInterval);
	}

	public int getMaxDocumentsLimit() {
		return maxFGDocumentsLimit;
	}

	/**
	 * The maximum length of any mashup, be it foreground or background.  If 0, then all
	 * sizes are allowed.  The maximum length can be specified in the configuration.
	 *
	 * @see CDCRConfig
	 * @return
	 */
	public long getMaxTotalLength() {
		return maxTotalLength;
	}

	/**
	 * Intigates a background mashup by saving the mashup values to an object (cdcr_cfile_mashup).  These
	 * mashup objects will be processed by the @link {@link MashupService}
	 *
	 * @param mashup
	 * @param sess
	 * @return the id of the saved object
	 * @throws DfException
	 * @throws MaxTotalLengthExceededException if the total length of documents in the mashup exceeds the configured maximum
	 */
	public IDfId mashupBackground(MashupInfo mashup, IDfSession sess) throws DfException, MaxTotalLengthExceededException {
		mashup.validate();
		assertMaxTotalLength(mashup);
		return mashup.save(sess);
	}

	private void assertMaxTotalLength(MashupInfo mashup) throws MaxTotalLengthExceededException {
		if (maxTotalLength > 0 && mashup.getTotalLength() > maxTotalLength)
			throw new MaxTotalLengthExceededException("Maximum length of " + maxTotalLength + " exceeded ("
					+ mashup.getTotalLength() + ")");
	}

	/**
	 * Performs a mashup immediately.  When called, the mashup will be attempted
	 * directly.  This is fine for small mashups, but not larger ones.
	 * <p/>
	 * The preferred use is to call {@link MashupManager#mashup(MashupInfo, IDfSession)}
	 * which will call perform either an immediate for background mashup according
	 * to document size.
	 *
	 * @param info information for the mashup
	 * @param sess
	 * @return the id of the created mashup document.
	 * @throws IOException if something goes wrong during mashup
	 * @throws DfException
	 * @throws MaxTotalLengthExceededException maximum total length of contained documents.
	 * @throws MaxFGDocumentsExceededException maximum number of contained documents for foreground mashup exceeded, try background mashup
	 * @throws MaxFGTotalLengthExceededException maximum total length of contained documents exceeded for forground mashup, try background mashup
	 */
	public MashupProcessor mashupForeground(MashupInfo info, IDfSession sess) throws IOException, DfException,
			MaxTotalLengthExceededException, MaxFGDocumentsExceededException, MaxFGTotalLengthExceededException {
		assertMaxTotalLength(info);

		if (maxFGDocumentsLimit > 0 && info.getDocumentsInfo().size() > maxFGDocumentsLimit)
			throw new MaxFGDocumentsExceededException("Maximum number of documents exceeded " + maxFGDocumentsLimit + " ("
					+ info.getDocumentsInfo().size() + ")");
		if (info.getTotalLength() > maxFGTotalLength)
			throw new MaxFGTotalLengthExceededException("Maximum length of " + maxTotalLength + " exceeded ("
					+ info.getTotalLength() + ")");

		info.setState(MashupInfo.PROCESS_STATE_FOREGROUND);
		info.save(sess);
		log.debug("Was: " + info.getSourceObject().getTypeName() + " is: " + info.getSourceObject().getObjectId());
		MashupProcessor proc = createMashupProcessor(sess);
		proc.performMashup(info, sess);
		log.debug("Is: " + info.getSourceObject().getTypeName() + " is: " + info.getSourceObject().getObjectId());
		return proc;
	}

	public IDfId mashupForegroundAndWait(MashupInfo info, IDfSession sess) throws IOException, InterruptedException, DfException,
			MaxTotalLengthExceededException, MaxFGDocumentsExceededException, MaxFGTotalLengthExceededException {
		MashupProcessor proc = mashupForeground(info, sess);
		proc.getThread().join();
		if (proc.getError() != null)
			throw new RuntimeException("Error when processing mashup: " + info.getSourceObject().getObjectId(), proc.getError());
		return proc.getMashupId();
	}

	public MashupProcessor createMashupProcessor(IDfSession session) {
		try {
			MashupProcessor proc = procClass.newInstance();
			proc.init(config, session);
			return proc;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error constructing a " + procClass.getName(), e);
		}
	}

	/** commented as the total size is no longer used to auto switch between foreground and background
	 * Mashup the given info.  If the length of the mashup is over a certain limit (as
	 * set in the configuration), then a background mashup is performed.  If it's below
	 * the limit, then a mashup is performed in the foreground.  The returned result
	 * contains the type of mashup that was done (Forground/Background).  If foreground,
	 * the MashupResult.id which is returned is the id of the created mashup document.  If background,
	 * the MashupResult.id is the id of the saved object containing mashup information.  This
	 * information is taken by the background mashup processor ({@link MashupServer}) to
	 * perform background mashups.
	 *
	 * @param info
	 * @param sess
	 * @return result of mashup.  MashupResult.type indicates the type of mashup and MashupResult.id is the created object according to type.
	 * @throws IOException if something happens during forground mashup
	 * @throws DfException  if something happens persisting object for background mashup
	 * @throws InterruptedException
	public MashupResult mashup(MashupInfo info, IDfSession sess) throws IOException, DfException, InterruptedException {
		boolean isBGMashup = false;
		// is bg mashup if total length is over max length
		isBGMashup |= info.getTotalLength() > ma;

		// check sizes
		if (isBGMashup) {
			// if bigger than max, do a background
			IDfId bgId = mashupBackground(info, sess);
			return new MashupResult(MashupResultType.Background, bgId);
		} else {
			IDfId fgId = mashupForgroundAndWait(info, sess);
			return new MashupResult(MashupResultType.Foreground, fgId);
		}

	}
	 */

	/**
	 * Gets the mashup manager.  The session is only used when the instance is
	 * initially created and is used to fetch configuration.
	 * @param sess
	 * @return
	 * @throws DfException
	 */
	public static MashupManager getInstance(IDfSession sess) throws DfException {
		// mode hardcoded to adobe for now.
		String mode = "adobe";

		COSDCalwinConfig config = getConfig(sess);
		String mm = mode == null ? config.get(CONFIG.MASHUP_MODE, CONFIG.MASHUP_MODE_ADOBE) : mode;
		Class<? extends MashupProcessor> procClass;
		if (mm != null && mm.equals(CONFIG.MASHUP_MODE_ADTS))
			procClass = CTSMashupProcessor.class;
		else
			procClass = AdobeMashupProcessor.class;
		MashupManager ti = new MashupManager(procClass);
		ti.init(config, sess);
		return ti;
		/* always create a new one.  this prevents value change requiring restart.
		 * dfc should manage caching of config object
			if (instance == null) {
				// mode hardcoded to adobe for now.
				String mode = "adobe";

				CDCRConfig config = getConfig(sess);
				String mm = mode == null ? config.get(CONFIG_MODE) : mode;
				Class<? extends MashupProcessor> procClass;
				if (mm != null && mm.equals(CONFIG_MODE_ADTS))
					procClass = CTSMashupProcessor.class;
				else
					procClass = AdobeMashupProcessor.class;
				MashupManager ti = new MashupManager(procClass);
				ti.init(config, sess);
				instance = ti;
			}
			return instance;
			*/
	}

	public static COSDCalwinConfig getConfig(IDfSession sess) throws DfException {
		return COSDCalwinConfig.getConfig(CONFIG_NAME_ADOBE_ASSEMBLER, sess);
	}

	public String getSectionNameForSection(String section) { // TO-DO: Remove?
		return sectionNameByNumber.get(section);
	}

	/**
	 * Utility to resolve the name for the given code via the cdcr_lookup_value table.
	 * Primary codes which may be resolved include sections and document_types.
	 *
	 * @param code code to resolve
	 * @return name corresponding to code as resolved via cdcr_lookup_value
	 * @throws DfException
	 */
	public static String lookupNameForCode(int code, IDfSession sess) throws DfException { // TO-DO: Remove?
		String val = namesBySection.get(code);
		if (val == null) {
			DfQuery q = new DfQuery(SECTION_LOOKUP_OBJECT_QRY + code);
			IDfCollection coll = q.execute(sess, IDfQuery.EXEC_QUERY);
			try {
				if (coll.next()) {
					val = coll.getString("title");
					namesBySection.put(code, val);
				}
			} finally {
				coll.close();
			}
		}
		return val;
	}

	/**
	 * Saves the existing mashup document to a new document with the given username and
	 * password applied.
	 * @param id id of document to export
	 * @param openPass password to open the document (clear)
	 * @param permsPass password to change permissions (clear)
	 * @param session
	 * @throws IOException
	 */
	public String export(IDfId id, String openPass, String permsPass, IDfSession session) throws IOException { // IDfId
		MashupProcessor p = createMashupProcessor(session);
		return p.export(id, openPass, permsPass, session);
	}
	
	public String export(IDfId id, IDfSession session) throws IOException { // IDfId
		MashupProcessor p = createMashupProcessor(session);
		return p.export(id, session);
	}
	

	/**
	 * NOTE: only valid after {@link #getInstance(IDfSession)} has been called once
	 * @return the number of times a document should be retried
	 */
	public static int getMaxRetries() {
		return maxRetries;
	}

	/**
	 * NOTE: only valid after {@link #getInstance(IDfSession)} has been called once
	 * @return the retry in ms before mashup retries
	 */
	public static long getRetryInterval() {
		return retryInterval;
	}
}
