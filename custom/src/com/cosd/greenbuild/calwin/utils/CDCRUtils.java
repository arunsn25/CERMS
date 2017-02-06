package com.cosd.greenbuild.calwin.utils;

import java.text.MessageFormat;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfAuditTrailManager;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.tools.RegistryPasswordUtils;

public class CDCRUtils implements COSDCalwinConstants {

	/**
	 * Encrypt a password. This does not seem to be roundtrip, ie:
	 *
	 * <pre>
	 * aPass.equals(getDecrypedPassword(getEncryptedPassword(aPass))) is false
	 * </pre>
	 *
	 * which is wierd. For round trip crypting, try {@link Base64}
	 *
	 * @param password
	 * @return
	 * @throws DfException
	 */
	public String getEncryptedPassword(String password) throws DfException {
		// DfLogger.debug(this,"Actual Password String is ["+password+"]",null,null);
		String encryptedPassword = RegistryPasswordUtils.encrypt(password);
		// System.out.println("Encrypted Password String is ["+encryptedPassword+"]");
		DfLogger.debug(this, "Encrypted Password String is [" + encryptedPassword + "]", null, null);
		return encryptedPassword;
	}

	/**
	 * Decrypt a password. This does not seem to be roundtrip, ie:
	 *
	 * <pre>
	 * aPass.equals(getDecrypedPassword(getEncryptedPassword(aPass))) is false
	 * </pre>
	 *
	 * which is wierd. For round trip crypting, try {@link Base64}
	 *
	 * @param encryptedPassword
	 * @return
	 * @throws DfException
	 */
	public String getDecryptedPassword(String encryptedPassword) throws DfException {
		DfLogger.debug(this, "Encrypted Password String is [" + encryptedPassword + "]", null, null);
		String decryptedPassword = RegistryPasswordUtils.decrypt(encryptedPassword);
		// System.out.println("Decrypted Password String is ["+decryptedPassword+"]");
		// DfLogger.debug(this,"Decrypted Password String is ["+decryptedPassword+"]",null,null);
		return decryptedPassword;
	}

	public void createAudit(IDfSession session, IDfId objectId, String additionalInfo[], String eventName) throws DfException {
		createAudit(session,""+objectId,additionalInfo,eventName);
	}
	public void createAudit(IDfSession session, String objectID, String additionalInfo[], String eventName) throws DfException {
		try {
			IDfAuditTrailManager auditMgr = session.getAuditTrailManager();
			auditMgr.createAudit(new DfId(objectID), eventName, additionalInfo, null);
			DfLogger.debug(this, "Audit info created for [" + objectID + "] event name=[" + eventName + "]", null, null);
		} catch (DfException e) {
			DfLogger.error(this, "Error creating audit for [" + objectID + "] event [" + eventName + "]", null, e);
		}
	}

	/*
	 * Query docbase and get all documents for given CDCR number and section.
	 */
	public String getEntireSectionObjects(IDfSession session, String CDCRNumber, String section) throws DfException {
		String entireSectionObjs = null;

		DfLogger.debug(this, "In getEntireSectionObjects method. Incoming cdcr number=[" + CDCRNumber + "] section=[" + section
				+ "]", null, null);

		int iBackfile = Integer.parseInt(section) + 1000;
		String sDocument_type = iBackfile + "";
		Object[] objArgs = { CDCRNumber, section, sDocument_type, CDCRNumber, section, sDocument_type };

		// BPH havs Document_Subtype, so we need a special DQL.
		String sDQL = null;
		if (section.equals("21000")) {
			sDQL = GET_BPH_DOCUMENTS_QRY;
		} else {
			sDQL = GET_ALL_SECTION_DOCUMENTS_QRY;
		}

		MessageFormat form = new MessageFormat(sDQL);
		String query = form.format(objArgs);

		// System.out.println(query);
		DfLogger.debug(this, "Final Query [" + query + "]", null, null);
		IDfQuery idfQry = new DfQuery();
		idfQry.setDQL(query);
		IDfCollection coll = idfQry.execute(session, DfQuery.DF_READ_QUERY);
		while (coll.next()) {
			String objID = coll.getString("r_object_id");
			if (entireSectionObjs == null) {
				entireSectionObjs = objID;
			} else {
				entireSectionObjs = entireSectionObjs + ";" + objID;
			}
		}
		coll.close();

		DfLogger.debug(this, "entireSectionObjs [" + entireSectionObjs + "]", null, null);
		return entireSectionObjs;
	}

	// check if a folder exists or not.
	public static boolean isFolderExist(IDfSession session, String cdcrNumber) throws DfException {
		boolean bFolderExist = false;
		IDfQuery query = new DfQuery();
		query.setDQL(FIND_CDCR_FOLDER_QRY + QUERY_COLUMN_MARKER + cdcrNumber + QUERY_COLUMN_MARKER);
		IDfCollection coll = query.execute(session, DfQuery.DF_READ_QUERY);
		if (coll.next()) {
			bFolderExist = true;
		}
		coll.close();

		return bFolderExist;
	}

	// check if this CDCR number exists in CODB
	public static boolean isCDCRNumberExistInCODB(IDfSession session, String cdcrNumber) throws DfException {
		boolean bCDCRNumberExist = false;
		Object[] objArgs = { cdcrNumber };
		MessageFormat form = new MessageFormat(GET_CODB_INFO_QRY);
		String SDQL = form.format(objArgs);
		IDfQuery query = new DfQuery();
		query.setDQL(SDQL);
		IDfCollection coll = query.execute(session, DfQuery.DF_READ_QUERY);
		if (coll.next()) {
			bCDCRNumberExist = true;
		}
		coll.close();
		return bCDCRNumberExist;
	}

}