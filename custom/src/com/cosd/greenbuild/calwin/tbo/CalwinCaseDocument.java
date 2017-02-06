package com.cosd.greenbuild.calwin.tbo;

import java.util.Date;

import com.documentum.fc.client.DfDocument;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfBusinessObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

public class CalwinCaseDocument extends DfDocument implements IDfBusinessObject {

	
	private static final String VENDOR_DESC = "";
	private static final String VERSION 	= "1.0";
	private static final String CASE_CONFIDENTIALITY = "case_confidentiality";
	private static final String CASE_TYPE = "case_type";
	private String g_caseConfVal = "";
	private String g_strAclName = "";
	
	public String getVendorString() {
		return VENDOR_DESC;
	}

	public String getVersion() {
		return VERSION;
	}

	public boolean isCompatible(String arg0) {
		return true;
	}

	public boolean supportsFeature(String arg0) {
		return true;
	}

	public void init()
	{		
		try
		{
			super.init();			
			g_caseConfVal = getString(CASE_CONFIDENTIALITY);
		}
		catch(DfException dfEx)
		{
			DfLogger.error(this, "Exception Raised in TBO Init: "+dfEx.getMessage(), null, null);
		}		
	}
	
	protected void doSave(boolean arg0, String arg1, Object[] arg2) throws DfException
	{
		if (isDirty())
		{
			if (!"".equals(g_caseConfVal) && !g_caseConfVal.isEmpty())
			{
				String strCaseConf = getString(CASE_CONFIDENTIALITY);
				String strCaseType = getString(CASE_TYPE);

				if (!g_caseConfVal.equalsIgnoreCase(strCaseConf))
				{
					IDfACL m_acl = aclToUse(strCaseConf, strCaseType);
					IDfSysObject newObj = (IDfSysObject) getSession().getObject(getObjectId());
					newObj.setACL(m_acl);
					newObj.setACLDomain(m_acl.getDomain());
					//newObj.save();
					DfLogger.debug(this, "Applied ACL: "+ m_acl.getObjectName(), null, null);
				}
				super.doSave(arg0, arg1, arg2);
				createRMrecord();
			}
		} 
		else 
		{
			super.doSave(arg0, arg1, arg2);
		}
	}
	
	private void createRMrecord() {
		try {
			IDfSysObject sysObj = (IDfSysObject)getSession().newObject("calwin_case_mashup");
			sysObj.setObjectName("one-off" + new Date());
			sysObj.setString("policy_name", g_strAclName);
			sysObj.setString("request_type", "one-off");
			sysObj.setString("mashup_owner", getModifier());
			sysObj.setString("policy_set", "CalWIN");
			sysObj.setRepeatingId("mashup_ids", 0, getObjectId());
			sysObj.setBoolean("is_simple", true);
			sysObj.link("/CalWIN-Mashup");
			sysObj.save();
		} catch (DfException e) {
			e.printStackTrace();
		}
	}

	private IDfACL aclToUse(String strCaseConfidentiality, String strCaseType)
	{
		
/*		if ("eligibility".equalsIgnoreCase(strCaseType) && "non-secured".equalsIgnoreCase(strCaseConfidentiality)) 
		{
			g_strAclName = "calwin_eligibility_acl";
		}*/

		if ("eligibility".equalsIgnoreCase(strCaseType) && "secured".equalsIgnoreCase(strCaseConfidentiality)) 
		{
			g_strAclName = "calwin_eligibility_conf_acl";
		}
		
/*		if ("adoption".equalsIgnoreCase(strCaseType) && "non-secured".equalsIgnoreCase(strCaseConfidentiality)) 
		{
			g_strAclName = "calwin_adoption_acl";
		}*/
		
		if ("adoption".equalsIgnoreCase(strCaseType) && "secured".equalsIgnoreCase(strCaseConfidentiality)) 
		{
			g_strAclName = "calwin_adoption_conf_acl";
		}
		
/*		if ((strCaseType.indexOf("oster")>=0) && "non-secured".equalsIgnoreCase(strCaseConfidentiality)) // "fostercarekingap".equalsIgnoreCase(strCaseType)
		{
			g_strAclName = "calwin_fc_kg_acl";
		}*/
		
		if ((strCaseType.indexOf("oster")>=0) && "secured".equalsIgnoreCase(strCaseConfidentiality))  // "fostercarekingap".equalsIgnoreCase(strCaseType)
		{
			g_strAclName = "calwin_fc_kg_conf_acl";
		}
		
		IDfACL tempACL = null;	
		try 
		{
			tempACL = (IDfACL)getSession().getObjectByQualification("dm_acl where object_name='" + g_strAclName + "'");
		} 
		catch (DfException e) 
		{
			e.printStackTrace();
		}
		
		return tempACL;	
	}
	
		
}
