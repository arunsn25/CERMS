package com.cosd.greenbuild.calwin.web.actions;

import java.util.Map;

import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.formext.action.IActionExecution;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.webcomponent.library.messages.MessageService;

/**
 * 
 * ******************************************************************************************
 * File Name: ChangeCasePersonsDocumentAction.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class ChangeCasePersonsDocumentAction implements IActionPrecondition, IActionExecution
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
		String objectID = argumentList.get("objectId");
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
					String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
					//System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
					if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
						enable=false;
					}
					//System.out.println("In action precondition. enable Value is : " + enable);
					return enable;
	}
	
	public boolean execute(String actionName, IConfigElement configElement,
			   ArgumentList argumentList,Context context, Component component, Map map) 
	{	
		String objectID = argumentList.get("objectId");
		DfLogger.debug(this,"ChangeCasePersonsDocumentAction Execute Called. Object selected ["+objectID+"]",null,null);
/*		try
		{
			IDfSysObject sysObj = (IDfSysObject) component.getDfSession().getObject(new DfId(objectID));
			sysObj.setBoolean("is_remove", true);
			sysObj.save();
			DfLogger.debug(this,"Selected object ["+objectID+"] is marked for soft delete",null,null);
			
			MessageService.addMessage(component, "MSG_DOCUMENT_REMOVED_SUCCESS");
			
		}
		catch(DfException dfe)
		{
			dfe.printStackTrace();
			DfLogger.error(this,"Failed to mark selected ["+objectID+"] object for soft delete",null,null);
			
			MessageService.addMessage(component, "MSG_DOCUMENT_REMOVED_FAILED");
		}*/
		return true;
	}

}
