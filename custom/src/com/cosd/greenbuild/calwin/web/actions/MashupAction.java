package com.cosd.greenbuild.calwin.web.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cosd.greenbuild.calwin.mashup.MashupInfo;
import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.cosd.greenbuild.calwin.mashup.MaxFGDocumentsExceededException;
import com.cosd.greenbuild.calwin.mashup.MaxFGTotalLengthExceededException;
import com.cosd.greenbuild.calwin.mashup.MaxTotalLengthExceededException;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.formext.action.ActionService;
import com.documentum.web.formext.action.IActionExecution;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.webcomponent.library.messages.MessageService;

/**
 * 
 * ******************************************************************************************
 * File Name: MashupAction.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class MashupAction implements IActionExecution, IActionPrecondition, COSDCalwinConstants {

	private static final long serialVersionUID = 1L;

	private static final String params[] = new String[] { "objectId" };

	private static final Logger log = Logger.getLogger(MashupAction.class);

	public String[] getRequiredParams() {
		return params;
	}

	public boolean queryExecute(String arg0, IConfigElement arg1, ArgumentList arg2, Context arg3, Component arg4) {
		// Enable this action only for Advanced Search views. Disable for Simple Search.
		Boolean enable = true;
		String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
		//System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
		if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
			enable=false;
		}		
		return enable;
	}

	public boolean execute(String actionName,
			IConfigElement configElement,
			ArgumentList argumentList,
			Context context,
			Component component,
			Map map) {
		//String objectIDs = argumentList.get("objectId");
		//List<String> oids = parseOids(objectIDs);

		String mashUpObjectIDsToBePassed = (String) SessionState.getAttribute("OBJECTID_FOR_MASHUP");
		List<String> oids = parseOids(mashUpObjectIDsToBePassed);
		String strCalwinActionView = (String)SessionState.getAttribute("calwinActionView");
		
		// if no ids, error
		if (oids.isEmpty()) {
			MessageService.addMessage(component, "MSG_MASHUP_FAILED");
			return true;
		}

		// if one id, redirect to viewer
		// NOTE: there is movement to perform a mashup even if there is only one document,
		//       as it forces creation of bookmarks.  This obviously has performance impact,
		//       especially for large documents.  If the documents were guarenteed to contain
		//       bookmarks, then a simple redirect to view would be ok.  Implementation TBD
/*		if (oids.size() == 1) {
			viewId(oids.get(0), context, component);
			return true;
		}*/
		
		if (oids.size() == 1) 
		{
			if ("exportWithoutPolicy".equals(strCalwinActionView))
			{
				exportId(oids.get(0), context, component);
				SessionState.setAttribute("calwinActionView", "");
			} 
			else 
			{
				viewId(oids.get(0), context, component);
			}
			return true;
		}

		// multiple ids perform mashup
		IDfSession session = component.getDfSession();

		// get cdcr_number from first id
		try {
			IDfSysObject sysObj = (IDfSysObject) session.getObject(new DfId(oids.get(0)));
			//String cdcrNumber = sysObj.getString("cdcr_number"); //TO-DO: Change
			String caseNumber = sysObj.getString("case_no"); 

			MashupInfo mashup = new MashupInfo(caseNumber, session);
			mashup.setIsSimple(true);
			for (String idStr : oids)
				mashup.addDocument(new DfId(idStr), session);
			//mashup.setTiteToSectionFormat(); //TO-DO: Remove?

			//TO-DO: Remove - STARTS
/*			String requestType = argumentList.get("requestType");
			if (requestType != null && requestType.equals(MASHUP_ALL_REQUESTED))
				mashup.setAuditType(AUDIT_EVENT_MASHUP_ALL);
			else if (requestType != null && requestType.equals(MASHUP_SELECTED_REQUESTED))
				mashup.setAuditType(AUDIT_EVENT_MASHUP_SELECTED);*/
			//TO-DO: Remove - ENDS
			
			IDfId mashId = MashupManager.getInstance(session).mashupForegroundAndWait(mashup, session);
			
			
			if ("exportWithoutPolicy".equals(strCalwinActionView)){
				exportId("" + mashId, context, component);
				SessionState.setAttribute("calwinActionView", "");
			} else {
				viewId("" + mashId, context, component);
			}

		} catch (MaxTotalLengthExceededException e) {
			log.warn("Error during mashup", e);
			MessageService.addMessage(component, "MSG_ERROR_MAX_SIZE_LIMIT");
		} catch (MaxFGDocumentsExceededException e) {
			log.warn("Error during mashup", e);
			MessageService.addMessage(component, "MSG_ERROR_COUNT_LIMIT");
		} catch (MaxFGTotalLengthExceededException e) {
			log.warn("Error during mashup", e);
			MessageService.addMessage(component, "MSG_ERROR_SIZE_LIMIT");
		} catch (Throwable t) {
			log.error("Error during mashup", t);
			MessageService.addMessage(component, "MSG_MASHUP_FAILED");
		}

		return true;
	}

	private void viewId(String idStr, Context context, Component component) {
		//call default OOB view operation
		ArgumentList mashupArgs = new ArgumentList();
		mashupArgs.add("objectId", idStr);
		mashupArgs.add("contentType", "pdf");
		mashupArgs.add("inline", "false");
		mashupArgs.add("launchViewer", "true");
		ActionService.execute("view", mashupArgs, context, component, null);
	}
	
	private void exportId(String idStr, Context context, Component component) {
		//call default OOB export operation
		ArgumentList mashupArgs = new ArgumentList();
		mashupArgs.add("objectId", idStr);
		ActionService.execute("export", mashupArgs, context, component, null);
	}

	private List<String> parseOids(String objectIDs) {
		String[] oidAry = objectIDs.split(",");
		List<String> result = new ArrayList<String>();
		for (String curr : oidAry) {
			if (!result.contains(curr))
				result.add(curr);
		}
		return result;
	}


}