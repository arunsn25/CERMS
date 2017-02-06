package com.cosd.greenbuild.calwin.mashup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfTime;

/**
 * Information about a mashup, which is a collection of documents which are
 * assembled into one larger document. This class is used to hold info required
 * for mashup creation.
 *
 * @author Andy.Taylor
 *
 */
public class MashupInfo implements COSDCalwinConstants {

	/** attribute holding expiration date */
	public static final String ATTR_RETENTION_DATE = "a_retention_date";
	public static final String ATTR_SUBJECT = "subject";
	/** repeating ids of documents involved in mashup */
	public static final String ATTR_MASHUP_IDS = "mashup_ids";
	/** boolean attribute, if true comments will be removed from mashups */
	public static final String ATTR_REMOVE_COMMENTS = "remove_comments";
	/** boolean attribute, if true bookmarks will be removed */
	public static final String ATTR_REMOVE_BOOKMARKS = "remove_bookmarks";
	public static final String ATTR_LOG_ENTRY = "log_entry";
	/** user that instigated the mashup */
	public static final String ATTR_MASHUP_OWNER = "mashup_owner";
	/** the cdcr number across which mashup documents span */
	//public static final String ATTR_CDCR_NUMBER = "cdcr_number"; // TO-DO: Change 
	public static final String ATTR_CDCR_NUMBER = "case_no";
	/** the processing state of the mashup, one of the PROCESS_STATE_* constants */
	public static final String ATTR_PROCESS_STATE = "process_state";
	/** the number of retries for processing the mashup */
	public static final String ATTR_RETRY_COUNT = "retry_count";
	/**
	 * the section (or unique sections, in CSV format) for documents in this
	 * mashup. Used in auditing.
	 */
	public static final String ATTR_SECTION = "section";
	/**
	 * was this mashup created as a simple mashup, not via the Advanced Mashup
	 * page. ie via the View Documents or Entire Section buttons. Simple mashups
	 * are not shown in the Mashup tab.
	 */
	public static final String ATTR_IS_SIMPLE = "is_simple";
	/**
	 * Is this mashup scheduled for removal? If true, the mashup will not be
	 * visible in the Mashup tab and will be hard removed when the
	 * a_retention_date passes.
	 */
	public static final String ATTR_REMOVE_DOCUMENT = "remove_document";

	/** mashups which are currently being processed */
	public static final String PROCESS_STATE_RUNNING = "running";
	/** mashups that have been retried for MAX_RETRIES and have failed each time */
	public static final String PROCESS_STATE_ERROR = "error";
	/** background mashups that have not yet been started */
	public static final String PROCESS_STATE_UNPROCESSED = "unprocessed";
	/** mashups that have been successfully finished */
	public static final String PROCESS_STATE_FINISHED = "finished";
	/** a mashup that has been saved in order to be processed in the foreground */
	public static final String PROCESS_STATE_FOREGROUND = "foreground";
	/** only retry a failed mashup after this date */
	public static final String ATTR_RETRY_AFTER = "retry_after";

	private static final Logger log = Logger.getLogger(MashupInfo.class);
	private static final SimpleDateFormat SDF_TITLE = new SimpleDateFormat("MMddyyyy_HHmmss");

	/**
	 * Information about a document in a mashup
	 *
	 * @author Andy.Taylor
	 *
	 */
	public class MashupDocument {

		public final IDfId id;
		public final String type;
		//public transient final String typeName;
		//public final int section;
		//public transient final String sectionName;
		public transient final long length;

		public MashupDocument(IDfId id, IDfSession session) throws DfException {
			this.id = id;
			IDfDocument doc = (IDfDocument) session.getObject(id);
			this.type = doc.getString("doc_type"); // TO-DO: Remove
			//this.typeName = MashupManager.lookupNameForCode(this.type, session);
			//this.section = doc.getInt("section"); // TO-DO: Remove
			//this.sectionName = MashupManager.lookupNameForCode(this.section, session);
			this.length = doc.getLong("r_content_size");
			totalLength += this.length;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof MashupDocument))
				return false;

			MashupDocument that = (MashupDocument) obj;
			boolean same = isEqual(id, that.id, "id");
			same &= isEqual(type, that.type, "type");
			//same &= isEqual(section, that.section, "section"); // TO-DO: Remove

			return same;
		}

		@Override
		public String toString() {
/*			return "Document id: " + id + "\n\ttype: " + type + " (" + typeName + ")\n\tsection: " + section + " (" + sectionName
					+ ")\n\tlength: " + length + " bytes"; */ // TO-DO: Change
			return "Document id: " + id;
		}

	}

	private String cdcrNumber;
	private String mashupOwner;
	// private String openPassword;
	// private String permissionsPassword;
	private String auditType = AUDIT_EVENT_MASHUP;
	private final List<MashupDocument> docInfos = new ArrayList<MashupDocument>();
	private transient long totalLength = 0;
	private String title;
	private boolean removeBookmarks = false;
	private boolean removeComments = false;
	private Date expiryDate;
	private String comments;

	private IDfSysObject sourceObject;
	private String state = PROCESS_STATE_UNPROCESSED;
	// assume advanced mashup, simple mashup calls setIsSimple(true)
	private boolean isSimple = false;

	/**
	 * Create a new mashup info for the given cdcrNumber and using the owner
	 * from the given session.
	 *
	 * @param sess
	 * @throws DfException
	 */
	public MashupInfo(String cdcrNumber, IDfSession sess) throws DfException {
		super();
		this.cdcrNumber = cdcrNumber;
		this.mashupOwner = sess.getLoginUserName();

		setExpiryDays(2);
	}

	/**
	 * Create a new MashupInfo object from the values stored in the given
	 * object.
	 *
	 * @param obj
	 * @throws DfException
	 */
	public MashupInfo(IDfSysObject sysObj) throws DfException {
		this.sourceObject = sysObj;
		// read from sys object
		load(sysObj);
	}

	/**
	 * Gets the object (cdcr_cfile_mashup) that this object was loaded from (via
	 * a previous call to {@link #save(IDfSession)}). If this was not loaded
	 * from a saved object, the source will be null.
	 *
	 * @return
	 */
	public IDfSysObject getSourceObject() {
		return sourceObject;
	}

	/**
	 * Loads the values from the given sysobject, as previously persisted during
	 * save
	 *
	 * @see #save()
	 * @param obj
	 * @throws DfException
	 */
	private void load(IDfSysObject sysObj) throws DfException {
		// NOTE if this object extended cdcr_cfile_mashup, then the values
		// could be read directly
		this.title = sysObj.getObjectName();
		this.cdcrNumber = sysObj.getString(ATTR_CDCR_NUMBER);
		this.mashupOwner = sysObj.getString(ATTR_MASHUP_OWNER);
		this.auditType = sysObj.getString(ATTR_LOG_ENTRY);
		this.removeBookmarks = sysObj.getBoolean(ATTR_REMOVE_BOOKMARKS);
		this.removeComments = sysObj.getBoolean(ATTR_REMOVE_COMMENTS);

		int numDocs = sysObj.getValueCount(ATTR_MASHUP_IDS);
		for (int i = 0; i < numDocs; i++) {
			IDfId id = sysObj.getRepeatingId(ATTR_MASHUP_IDS, i);
			MashupDocument info = new MashupDocument(id, sysObj.getSession());
			docInfos.add(info);
		}

		this.comments = sysObj.getString(ATTR_SUBJECT);
		this.state = sysObj.getString(ATTR_PROCESS_STATE);
		this.isSimple = sysObj.getBoolean(ATTR_IS_SIMPLE);

		// get expiration date
		IDfTime tt = sysObj.getTime(ATTR_RETENTION_DATE);
		if (tt != null)
			expiryDate = tt.getDate();

	}

	/**
	 * Save this mashup info to an object, returning the id of the created. This
	 * is used for background processing. Once mashupinfo is saved, it will be
	 * picked up by the background processor and processed. After processing,
	 * the status will be changed so that processing is not repeated.
	 *
	 * @param session
	 *
	 * @return
	 * @throws DfException
	 */
	public IDfId save(IDfSession session) throws DfException {

		// NOTE this could be done with an overridden type. MashupInfo could
		// extend IDfDocument and be registered for cdcr_cfile_mashup. This
		// would allow for more direct persistence, but would require
		// registration in a dar. It's probably the way to go, but for now it's using the
		// object indirectly
		IDfSysObject sysObj = (IDfSysObject) session.newObject(CDCR_CFILE_MASHUP);
		sysObj.setObjectName(getTitle()); //To-DO: Change
		sysObj.setString(ATTR_CDCR_NUMBER, cdcrNumber); //To-DO: Change
		sysObj.setString(ATTR_MASHUP_OWNER, mashupOwner);
		sysObj.setOwnerName("adobe_lc_user");
		//sysObj.setString(ATTR_LOG_ENTRY, auditType); //To-DO: Remove
		//sysObj.setBoolean(ATTR_REMOVE_BOOKMARKS, removeBookmarks); //To-DO: Remove
		//sysObj.setBoolean(ATTR_REMOVE_COMMENTS, removeComments); //To-DO: Remove
		
		// total length not persisted, calculated
		// sysObj.setInt("total_length", totalLength);

		// passwords not saved, only provided during realtime (fg) save
		// sysObj.setString("password_open", openPassword != null ?
		// Base64.encode(openPassword.getBytes()) : null);
		// sysObj.setString("password_perms", permissionsPassword != null ?
		// Base64.encode(permissionsPassword.getBytes()) : null);

		List<Integer> uniqueSections = new ArrayList<Integer>();
		for (int i = 0; i < docInfos.size(); i++) {
			MashupDocument info = docInfos.get(i);
			sysObj.setRepeatingId(ATTR_MASHUP_IDS, i, info.id);

			//if (!uniqueSections.contains(info.section)) //TO-DO: Remove
				//uniqueSections.add(info.section); //TO-DO: Remove

			// when saving, we don't need the type and section, as that can be
			// reconstructed
			// (and might have changed between save and mashup)
			// sysObj.setRepeatingInt("mashup_types", i, info.type);
			// sysObj.setRepeatingInt("mashup_sections", i, info.section);
		}

		 //TO-DO: Remove - STARTS
/*		String secStr = "";
		for (int i = 0; i < uniqueSections.size(); i++) {
			int sec = uniqueSections.get(i);
			if (secStr.length() > 0)
				secStr += ",";
			secStr += sec;
		}
		 
		sysObj.setString(ATTR_SECTION, secStr);*/
		//TO-DO: Remove - ENDS
		
		//sysObj.setString(ATTR_SUBJECT, this.comments); //TO-DO: Remove

		// set expiration date
		//sysObj.setTime(ATTR_RETENTION_DATE, new DfTime(expiryDate)); //TO-DO: Remove?

		sysObj.setString(ATTR_PROCESS_STATE, state);
		sysObj.setInt(ATTR_RETRY_COUNT, 0);
		sysObj.setBoolean(ATTR_IS_SIMPLE, isSimple);

		sysObj.link(MASHUPUP_CABINET);
		sysObj.save();

		this.sourceObject = sysObj;

		IDfId objId = sysObj.getObjectId();

		/* probably not needed.  redundant as logged by livecycle on create
		CDCRUtils utils = new CDCRUtils();
		String[] additionalInfo = new String[] { "Owner=[" + mashupOwner + "]", "CDCR Number=[" + cdcrNumber + "]",
				"Section=[" + secStr + "]","object_name=["+getTitle()+"" };
		utils.createAudit(session, objId, additionalInfo, AUDIT_EVENT_MASHUP_CREATE);
		*/

		return objId;
	}

	/**
	 * Sets the comments which are saved in a background mashup. Comments are
	 * not saved when foreground mashups are done.
	 *
	 * @param comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Implicitly set the mashup owner. When constructed via
	 * MashupInfo(String,IDfSession) the mashup owner is taken from the session
	 * and this method need not be called.
	 *
	 * @param owner
	 */
	public void setMashupOwner(String owner) {
		this.mashupOwner = owner;
	}

	/**
	 * Add a cdcr_cfile_doc document with the given id to the mashup. The
	 * document is fetched and the values retrieved in order to add the
	 * document.
	 *
	 * @param id
	 *            id of document to add
	 * @param session
	 * @throws DfException
	 */
	public void addDocument(IDfId id, IDfSession session) throws DfException {
		this.docInfos.add(new MashupDocument(id, session));
		// regenerate title if it was previously generated
		if (title != null && title.startsWith(cdcrNumber))
			title = null;
	}

	/**
	 * this is commented, as passwords are now only set during export.
	 *
	 * Set the open and permissions password for the document. The two passwords
	 * may not be the same.
	 *
	 * @param openPassword
	 *            password required for open
	 * @param permissionsPassword
	 *            password required for advanced features.
	 * @throws IllegalArgumentException
	 *             if the passwords are the same public void setPasswords(String
	 *             openPassword, String permissionsPassword) throws
	 *             IllegalArgumentException { if
	 *             (openPassword.equals(permissionsPassword)) throw new
	 *             IllegalArgumentException
	 *             ("The passwords must not be the same."); this.openPassword =
	 *             openPassword; this.permissionsPassword = permissionsPassword;
	 *             }
	 */

	/**
	 * Ensures that all mashup info has been provided.
	 *
	 * @throws IllegalArgumentException
	 */
	public void validate() throws IllegalArgumentException {
		assertNotNull(cdcrNumber, "cdcrNumber");
		assertNotNull(mashupOwner, "mashupOwner");
		// assertNotNull(openPassword, "openPassword");
		// assertNotNull(permissionsPassword, "permissionsPassword");
		assertNotNull(auditType, "auditType");
		if (docInfos.isEmpty())
			throw new IllegalArgumentException("Expected info to be provided for one or more documents.");
	}

	private void assertNotNull(String value, String name) {
		if (value == null)
			throw new IllegalArgumentException("Expected " + name + " to be assigned.");

	}

	public String getCDCRNumber() {
		return cdcrNumber;
	}

	/*
	 * public String getOpenPassword() { return openPassword; }
	 *
	 * public String getPermissionsPassword() { return permissionsPassword; }
	 */

	public String getAuditType() {
		return auditType;
	}

	/**
	 *
	 * @return the total length of all documents added to the mashup
	 */
	public long getTotalLength() {
		return totalLength;
	}

	/**
	 * Gets the owner (user name) of the mashup, typically this is the user that
	 * requested the mashup.
	 *
	 * @return the mashup owner
	 */
	public String getOwner() {
		return mashupOwner;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		if (title != null)
			return title;

		this.title=generateDefaultTitle(cdcrNumber);
		return title;
	}

	/**
	 * Call to remove or keep bookmarks when performing mashup. By default
	 * bookmarks are removed.
	 *
	 * @param removeBookmarks
	 *            true to remove bookmarks when mashing
	 */
	public void setRemoveBookmarks(boolean removeBookmarks) {
		this.removeBookmarks = removeBookmarks;
	}

	/**
	 *
	 * @return true if bookmarks are to be removed
	 */
	public boolean removeBookmarks() {
		return removeBookmarks;
	}

	/**
	 * Call to remove or keep comments in the mashup document. By default
	 * comments are removed
	 *
	 * @param removeComments
	 *            true to remove comments
	 */
	public void setRemoveComments(boolean removeComments) {
		this.removeComments = removeComments;
	}

	/**
	 * @return true if comments will be removed
	 */
	public boolean removeComments() {
		return removeComments;
	}

	public List<MashupDocument> getDocumentsInfo() {
		return docInfos;
	}

	/**
	 * Set the absolute date when the mashup document should expire. Defaults to
	 * today+2days.
	 *
	 * @param expiryDate
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Set to expire in a given number of days. Will expire just before midnight
	 * in this many days
	 *
	 * @param numDays
	 *            number of days to retain
	 */
	public void setExpiryDays(int numDays) {
		// by default, have the document expire at midnight in two days.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, numDays);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		setExpiryDate(cal.getTime());
	}

	/**
	 * @return the date when the mashup expires.
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	@Override
	public String toString() {
		String docStr = "";
		for (MashupDocument curr : docInfos) {
			String t = "\t" + curr.toString();
			t = t.replace("\n", "\n\t");
			docStr += t + "\n";
		}

		return "Mashup:\n\tcdcr_number: " + cdcrNumber + "\n\towner: " + mashupOwner + "\n\taudit: " + auditType + "\n\ttitle: "
				+ getTitle() + "\n\tremoveBookmarks: " + removeBookmarks + "\n\tremoveComments: " + removeComments
				+ "\n\texpires: " + expiryDate + "\n\t" + docInfos.size() + " Documents:\n" + docStr; // TO-DO: Change
	}

	/**
	 * Overridden to ensure equality during testing.
	 *
	 * <pre>
	 * IDfId id=info.save(session);
	 * new MashupInfo(session.getObject(id)).equals(info)
	 * &circ;-- should always be true
	 * </pre>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MashupInfo))
			return false;

		MashupInfo that = (MashupInfo) obj;

		if (docInfos.size() != that.docInfos.size())
			return false;

		boolean same = isEqual(auditType, that.auditType, "auditType");
		same &= isEqual(cdcrNumber, that.cdcrNumber, "cdcrNumber");
		same &= isEqual(mashupOwner, that.mashupOwner, "mashupOwner");
		// same &= isEqual(openPassword, that.openPassword);
		// same &= isEqual(permissionsPassword, that.permissionsPassword);
		same &= isEqual(title, that.title, "title");
		same &= isEqual(removeBookmarks, that.removeBookmarks, "removeBookmarks");
		same &= isEqual(removeComments, that.removeComments, "removeComments");
		same &= isEqual("" + expiryDate, "" + that.expiryDate, "expiryDate");

		for (int i = 0; i < docInfos.size() && same; i++)
			same &= docInfos.get(i).equals(that.docInfos.get(i));

		return same;
	}

	private boolean isEqual(Object thisObj, Object thatObj, String name) {
		boolean isEqual = (thisObj == null && thatObj == null) || (thisObj != null && thisObj.equals(thatObj));
		if (!isEqual)
			log.warn("Not equal for field: " + name + " this: " + thisObj + " that: " + thatObj);
		return isEqual;
	}

	public void setState(String state) {
		this.state = state;
	}

	void setSourceObject(IDfSysObject obj) {
		this.sourceObject = obj;

	}

	public void setIsSimple(boolean isSimple) {
		this.isSimple = isSimple;
	}

	/**
	 * Was this mashup a simple mashup (ie not created via advanced mashup
	 * page). Simple mashups are not shownin mashup tab.
	 *
	 * @return
	 */
	public boolean isSimple() {
		return isSimple;
	}

	/**
	 * Sets the title to be section format, which is C1234_Section. The default
	 * title format is SOMS_HHMMDD
	 */
	public void setTiteToSectionFormat() {
		title = cdcrNumber + "_";
		List<String> sects = new ArrayList<String>();
		for (MashupDocument curr : docInfos) {
/*			if (!sects.contains(curr.sectionName))
				sects.add(curr.sectionName);*/
		}

		for (int i = 0; i < sects.size(); i++) {
			title += sects.get(i);
			if (i + 1 < sects.size())
				title += "+";
		}
	}

	public static String generateDefaultTitle(String cdcrNumber) {
		//return "ERMS_" + cdcrNumber.toUpperCase() + "_" + SDF_TITLE.format(new Date()); // TO-DO: Change
		return "CalWIN_" + cdcrNumber.toUpperCase() + "_" + SDF_TITLE.format(new Date());
	}

	public void onError(Throwable e) {
		IDfSysObject obj = getSourceObject();
		if (obj == null)
			log.error("Expected an object at this point, not reacting to error", e);
		else
			handleError(obj, e);
	}

	public static void handleError(IDfSysObject loadedFrom, Throwable e) {
		log.warn("Mashup failed!", e);
		e.printStackTrace(System.err);
		System.err.println("Mashup failed");
		e.printStackTrace();
		try {
			int retryCount = loadedFrom.getInt(MashupInfo.ATTR_RETRY_COUNT);
			if (retryCount > MashupManager.getMaxRetries()) {
				loadedFrom.setString(MashupInfo.ATTR_PROCESS_STATE, MashupInfo.PROCESS_STATE_ERROR);
				String msg = "Mashup failed too many times (" + MashupManager.getMaxRetries() + ") for saved mashup: "
						+ loadedFrom.getObjectName() + " (" + loadedFrom.getTypeName() + ").  No longer will be retried.";
				log.error(msg);
				System.err.println(msg);
			} else {

				long currTime = System.currentTimeMillis();
				try {
					// get the current time from the content server, as it may
					// be
					// different from the current time.
					IDfSession sess = loadedFrom.getSession();
					DfQuery q = new DfQuery("select date(now) as current_time from dm_document enable (return_top 1)");
					IDfCollection coll = q.execute(sess, DfQuery.DF_READ_QUERY);
					try {
						if (coll.next()) {
							IDfTime time = coll.getTime("current_time");
							currTime = time.getDate().getTime();
						}
					} finally {
						coll.close();
					}
				} catch (Throwable t) {
					log.warn("Error getting current time from server, using local time", t);
				}

				Date timeout = new Date(currTime + MashupManager.getRetryInterval());
				String msg = "Mashup failed for the (" + retryCount + ") time for saved mashup: " + loadedFrom.getObjectName()
						+ " (" + loadedFrom.getTypeName() + ").  Will be after: " + timeout;
				log.warn(msg);
				System.err.println(msg);

				loadedFrom.setString(MashupInfo.ATTR_PROCESS_STATE, MashupInfo.PROCESS_STATE_UNPROCESSED);
				loadedFrom.setTime(MashupInfo.ATTR_RETRY_AFTER, new DfTime(timeout));

			}
			// occasional wierd DM_SYSOBJECT_E_VERSION_MISMATCH error if fetch
			// not done.
			loadedFrom.fetch(null);
			loadedFrom.save();
		} catch (Exception ee) {
			log.warn("Error saving after run.", ee);
		}
	}

}
