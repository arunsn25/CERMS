package com.cosd.greenbuild.calwin.web.actions;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.form.Form;
import com.documentum.web.form.IReturnListener;
import com.documentum.web.formext.action.ActionService;
import com.documentum.web.formext.action.IActionExecution;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.component.Prompt;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.role.RoleService;

/**
 * 
 * ******************************************************************************************
 * File Name: ThirdPartyView.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class ThirdPartyView implements IActionExecution, IActionPrecondition, COSDCalwinConstants, IReturnListener {

	private static final long serialVersionUID = 1L;

	private static final String params[] = new String[] { "objectId" };

	private Component srcComponent;

	private Context srcContext;

	public String[] getRequiredParams() {
		return params;
	}

	public boolean queryExecute(String actionName,
			IConfigElement config,
			ArgumentList argumentList,
			Context context,
			Component component) {
		/** cr76: show only for specified roles */
		// as modified from com.documentum.web.formext.action.GenericRolePrecondition
/*		if (config != null) {
			Iterator iterator = config.getChildElements("role");
			while (iterator.hasNext()) {
				IConfigElement roleConfigElement = (IConfigElement) iterator.next();
				String strRoleRequired = roleConfigElement.getValue();
				// if user does not have the role, hide
				if ((strRoleRequired != null) && (!RoleService.isUserAssignedRole(null, strRoleRequired, argumentList, context)))
					return false;
			}
		}*/
		/** /cr76 */

		// Enable this action only for Advanced Search views. Disable for Simple Search.
		Boolean enable = true;
		String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
		//System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
		if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
			enable=false;
		}		
		return enable;
	}

	/*
	 * Action service to execute OOB export action which allows save the 
	 * file in users local file system.
	 * 
	 */
	public boolean execute(String actionName,
			IConfigElement config,
			ArgumentList argumentList,
			Context context,
			Component component,
			Map map) {
		String objectID = argumentList.get("objectId");
		//IDfAuditTrailManager auditMgr = component.getDfSession().getAuditTrailManager();
		ArgumentList mashupArgs = new ArgumentList();
		mashupArgs.add("objectId", objectID);
		//				ActionService.execute("export", mashupArgs, context, component, null);
		mashupArgs.add("component", "MashupTypeComponent");
		//System.out.println("Jumping to MashupTypeContainer");

		// this calls the password chooser component, which calls this.onReturn(Form,Map) when done
		this.srcComponent = component;
		this.srcContext = context;
		//component.setComponentNested("MashupTypeContainer", mashupArgs, context, this);
/*		mashupArgs.add("format", "pdf");
		component.setComponentNested("getcontent_msdoc", mashupArgs, context, this);*/
		
/*		try {
			// trigger export
			MashupManager mgr = MashupManager.getInstance(component.getDfSession());
			String strXmlResponse = mgr.export(new DfId(objectID), "Password$1", "bot.win.net-942", component.getDfSession()); // IDfId nonRMId
			System.out.println("Output from LiveCycle Export Service ::: " + strXmlResponse);
			int a = strXmlResponse.indexOf("http");
			int b = strXmlResponse.indexOf("</outDocPDF>");
			String strResultMSG = strXmlResponse.substring(a, b);
			viewContent(objectID, strResultMSG, context, component);
		} catch (DfException err) {
			DfLogger.error(this, err.getMessage(), null, err);
		} catch (IOException err) {
			DfLogger.error(this, err.getMessage(), null, err);
		}*/
		
		IDfSession dfSession = null;
		IDfSysObject sysObj = null;

		
		try{
			
		dfSession = component.getDfSession();
		sysObj = (IDfSysObject) dfSession.getObject(new DfId(objectID));
		ArgumentList args = new ArgumentList();
		args.add(Prompt.ARG_TITLE, "Information");
		args.add(Prompt.MESSAGE, "Please click to download the file..." );
		args.add(Prompt.ARG_ICON, Prompt.ICON_INFO);
		// -------------- SET SESSION STATE START-----------------
		SessionState.setAttribute("OBJECTID", objectID);
		SessionState.setAttribute("OBJECT_NAME", sysObj.getObjectName());
		SessionState.setAttribute("MSWORD_DOWNLOAD_POSTFIX", "_clean");
		// -------------- SET SESSION STATE END-----------------
		args.add(Prompt.ARG_BUTTON, new String[] { Prompt.OK });
		component.setComponentNested("msdocdownloadprompt", args, context, null);
		DfLogger.debug(this, "Exit execute of CleanCopyAction returning true", null, null);
		
	} catch (Exception ioe) {
			DfLogger.error(getClass().getName(), "Exception occurs: ", null, ioe);
			return false;
		}
		
		return true;
	}
	
	private void viewContent(String idStr, String strResultMSG, Context context, Component component) {
		ArgumentList getcontentArgs = new ArgumentList();
		getcontentArgs.add("objectId", idStr);
		getcontentArgs.add("format", "pdf");
		getcontentArgs.add("resultMSG", strResultMSG);
		ActionService.execute("COSDGetContent", getcontentArgs, context, component, null);
	}

	
	/**
	 * Called on return from password dialog.  Calls export.
	 */
	public void onReturn(Form paramForm, Map paramMap) {
		ArgumentList mashupArgs = new ArgumentList();
		Object oid = paramMap.get("objectId");
		String strXmlResponse = (String) paramMap.get("resultMSG");
		String result = null;
		String resultStatus = null;
		
		int a = strXmlResponse.indexOf("http");
		int b = strXmlResponse.indexOf("</outDocPDF>");


		result = strXmlResponse.substring(a, b);
		
		if (oid != null) {
			mashupArgs.add("objectId", "" + oid);
			mashupArgs.add("format", "pdf");
			mashupArgs.add("resultMSG", result);
			// old way.  On IE7, works once but fails on subsequent tries.
			// has something to do with MessageService.addMessage
			//		ActionService.execute("export", mashupArgs, srcContext, srcComponent, null);

			//		Context ctx = new Context();
			//		ActionService.execute("exportNoMessage", mashupArgs, ctx, null, null);

			// call overriden export action, which calls overriden export component which does 
			// not show the 'Export Successful' message.
			
/*			ActionService.execute("exportNoMessage", mashupArgs, srcContext, srcComponent, null);*/
			
			//srcComponent.setComponentNested("getcontent_msdoc", mashupArgs, srcContext, new IReturnListener(){ public void onReturn(Form form, Map map){}});
		} // else no object id, probably it was cancelled.
	}

}
