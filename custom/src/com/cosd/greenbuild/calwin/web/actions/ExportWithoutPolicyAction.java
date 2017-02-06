package com.cosd.greenbuild.calwin.web.actions;

import java.util.Iterator;
import java.util.Map;

import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.formext.action.IActionExecution;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.docbase.ObjectCacheUtil;
import com.documentum.web.formext.role.RoleService;
import com.documentum.webcomponent.library.messages.MessageService;

/**
 * 
 * ******************************************************************************************
 * File Name: ExportWithoutPolicyAction.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class ExportWithoutPolicyAction implements IActionPrecondition, IActionExecution
{
	private static final long serialVersionUID = 1L;
	
	private static final String params[] = new String[] {"objectId"}; 

	public String[] getRequiredParams() 
	{
		return params;
	}

	public boolean queryExecute(String actionName, IConfigElement configElement,
			ArgumentList argumentList, Context context, Component component) 
	{
		boolean enable = true;
		//String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
		String m_strDocbaseObjectId = argumentList.get("objectId");
		/*			IDfSysObject sysObj = (IDfSysObject) component.getDfSession().getObject(new DfId(objectID));
					String format = sysObj.getString("a_content_type");
					// CR000073, don't enable 'Remove' button for Backfile and eForm.
					if(sysObj.isCheckedOut() || format.equals("eform_pdf"))
					{
						enable = false;
					}
					else
					{
						int iDocumentType = sysObj.getInt("document_type");
						int iSection = sysObj.getInt("section");
						if (iDocumentType - iSection == 1000)
						{
							enable = false;
						}
					}*/
		
		
		IDfSession dfSession = component.getDfSession();
		
		if (m_strDocbaseObjectId != null) { // && CalwinSearchFrame.strNLSSimpleSearch.equals(strCurrSrcView)) {
			try {
				//Try to get the object
				IDfSysObject sysObject = (IDfSysObject) ObjectCacheUtil.getObject(dfSession, m_strDocbaseObjectId);
				String fldSysObjectId = sysObject.getFolderId(0).getId();
				IDfSysObject fldSysObject = (IDfSysObject) ObjectCacheUtil.getObject(dfSession, fldSysObjectId);

				if (fldSysObjectId.startsWith("0b")) {
				String strCaseType = fldSysObject.getString("case_type");
				String strCaseConfidentiality = "True".equalsIgnoreCase(fldSysObject.getString("case_confidentiality")) ? "secured" : "non-secured";
				
/*				String strCaseType = sysObject.getString("case_type");
				String strCaseConfidentiality = sysObject.getString("case_confidentiality");*/

				
/*				String strRoleRequired = null;
				if ("eligibility".equalsIgnoreCase(strCaseType))
				{
					if ("non-secured".equalsIgnoreCase(strCaseConfidentiality))
					{
						strRoleRequired = "calwin_export_eligibility";
						if ((strRoleRequired != null) && (RoleService.isUserAssignedRole(null, strRoleRequired, argumentList, context)))
							enable=true;
					}
					if ("secured".equalsIgnoreCase(strCaseConfidentiality))
					{
						strRoleRequired = "calwin_export_eligibility_conf";
						if ((strRoleRequired != null) && (RoleService.isUserAssignedRole(null, strRoleRequired, argumentList, context)))
							enable=true;
					}
				}*/
				
/*				enable = isUserEligible("calwin_export_eligibility",strCaseType,"eligibility",strCaseConfidentiality,"non-secured",argumentList, context);
				enable = isUserEligible("calwin_export_eligibility_conf",strCaseType,"eligibility",strCaseConfidentiality,"secured",argumentList, context);
				enable = isUserEligible("calwin_export_adoption",strCaseType,"adoption",strCaseConfidentiality,"non-secured",argumentList, context);
				enable = isUserEligible("calwin_export_adoption_conf",strCaseType,"adoption",strCaseConfidentiality,"secured",argumentList, context);
				enable = isUserEligible("calwin_export_fosterkingap",strCaseType,"fosterkingap",strCaseConfidentiality,"non-secured",argumentList, context);
				enable = isUserEligible("calwin_export_fosterkingap_conf",strCaseType,"fosterkingap",strCaseConfidentiality,"secured",argumentList, context);*/
				
				if (configElement != null) {
					Iterator<?> iterator = configElement.getChildElements("role");
					while (iterator.hasNext()) {
						IConfigElement roleConfigElement = (IConfigElement) iterator.next();
						String strRoleSeq = roleConfigElement.getValue();
						String strRoleCompare = strRoleSeq.substring(0, strRoleSeq.indexOf(","));
						String strCaseTypeCompare = strRoleSeq.substring(strRoleSeq.indexOf(",")+1, strRoleSeq.lastIndexOf(","));
						String strCaseConfCompare = strRoleSeq.substring(strRoleSeq.lastIndexOf(",")+1);

						enable = isUserEligible(strRoleCompare,strCaseType,strCaseTypeCompare,strCaseConfidentiality,strCaseConfCompare,argumentList, context);
						if (enable)
							break;
						
					}
				}				
				} else {
					enable = false;
				}
			}
			catch (DfObjectNotFoundException e) {
				DfLogger.error(this, "Object ID not found: " + m_strDocbaseObjectId, null, e);
				throw new WrapperRuntimeException("Object ID not found: " + m_strDocbaseObjectId, e);
			}
			catch (DfException e) {
				DfLogger.error(this, "Problem with the Object ID: " + m_strDocbaseObjectId, null, e);
				throw new WrapperRuntimeException("Problem with the Object ID: " + m_strDocbaseObjectId, e);
			}
		}
		
		
		
		
		
/*		System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
		if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
			enable=false;
		}
		System.out.println("In action precondition. enable Value is : " + enable);*/
		return enable;
	}
	
	
	private boolean isUserEligible(String lstrRoleRequired, 
			String lstrCaseType, String lstrCompareCaseType, 
			String lstrCaseConfidentiality, String lstrCompareCaseConfidentiality, 
			ArgumentList args, Context ctx) 
	{
		Boolean isEligible = false;
		if (lstrCompareCaseType.equalsIgnoreCase(lstrCaseType))
		{
			if (lstrCompareCaseConfidentiality.equalsIgnoreCase(lstrCaseConfidentiality))
			{
				if ((lstrRoleRequired != null) && (RoleService.isUserAssignedRole(null, lstrRoleRequired, args, ctx)))
					isEligible = true;
			}
		}
		return isEligible;
	}
	
	public boolean execute(String actionName,
			IConfigElement configElement,
			ArgumentList argumentList,
			Context context,
			Component component,
			Map map) {
		return true;
	}
	

}
