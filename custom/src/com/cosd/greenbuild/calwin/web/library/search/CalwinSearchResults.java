package com.cosd.greenbuild.calwin.web.library.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import com.cosd.hhsa.calwin.common.StringValidationUtils;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.search.IDfExpressionSet;
import com.documentum.fc.client.search.IDfQueryBuilder;
import com.documentum.fc.client.search.IDfQueryProcessor;
import com.documentum.fc.client.search.IDfResultsManipulator;
import com.documentum.fc.client.search.IDfResultsSet;
import com.documentum.fc.client.search.IDfSimpleAttrExpression;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfValue;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.env.EnvironmentService;
import com.documentum.web.form.Control;
import com.documentum.web.form.Form;
import com.documentum.web.form.FormActionReturnListener;
import com.documentum.web.form.IReturnListener;
import com.documentum.web.form.control.Button;
import com.documentum.web.form.control.DateInput;
import com.documentum.web.form.control.Hidden;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.ListBox;
import com.documentum.web.form.control.Option;
import com.documentum.web.form.control.Panel;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.DFCQueryDataHandler;
import com.documentum.web.form.control.databound.DataProvider;
import com.documentum.web.form.control.databound.Datagrid;
import com.documentum.web.form.control.databound.DatagridRow;
import com.documentum.web.formext.action.ActionService;
import com.documentum.web.formext.action.CallbackDoneListener;
import com.documentum.web.formext.component.Component;
//import com.documentum.web.formext.component.Prompt;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.control.action.ActionControl;
import com.documentum.web.formext.control.action.ActionMultiselect;
import com.documentum.web.formext.docbase.ObjectCacheUtil;
import com.documentum.web.util.DfcUtils;
import com.documentum.webtop.app.AppSessionContext;
import com.documentum.webtop.webcomponent.search.Search60;
import com.documentum.webtop.webcomponent.search.SearchEx;

import com.documentum.webcomponent.library.search.SearchInfo;
//import com.documentum.webcomponent.library.search.ClusterResultSet;
import com.documentum.webcomponent.library.search.SearchResultSet;


/**
 * 
 * ******************************************************************************************
 * File Name: CalwinSearchResults.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class CalwinSearchResults extends Search60
{

	public CalwinSearchResults(){
		cosd_datagrid = null;
	}
	
	public void onInit(ArgumentList args){
		
		super.onInit(args);
		dqlQuery = args.get("query");
		Label searchlimitcontrollabel = (Label)getControl("searchlimitcontrollabel", Label.class);
		searchlimitcontrollabel.setVisible(false);
		cosd_datagrid = (Datagrid)getControl(Search60.CONTROL_GRID, Datagrid.class);
	}
	
	public void onClickRemoveDoc(Button button, ArgumentList args){
		
		Hidden hidId = (Hidden)getControl("addObjIds",Hidden.class);
		String robjectId = hidId.getValue();
		//args.replace("objectId", robjectId);
		
		arrRObjectIDs = robjectId.split(",");
		for(String item : arrRObjectIDs) {
			args.replace("objectId", item);
			ActionService.execute("remove_document", args, getContext(), this, null);
		}
	}
	
	public void onClickUnRemoveDoc(Button button, ArgumentList args){
		
	}	

	public void onClickChangeProcessedStatus(Button button, ArgumentList args){
		
		showMaxObjectsPrompt();
	}
	
	/**
	 * Display a Warning Prompt
	 */
	public void showMaxObjectsPrompt () {

		DfLogger.debug(this, "Enter showMaxObjectsPrompt", null, null);

		ArgumentList args = new ArgumentList();
		
		//String[] params = {String.valueOf(maxDocuments)};
		//Set the Prompt Title
		args.add(CalwinPrompt.ARG_TITLE, "Change Processed Status Prompt"); //getString("MSG_PROMPT_TITLE")
		//Set the Prompt Message
		args.add(CalwinPrompt.ARG_MESSAGE, "You are about to change the processed status for the selected documents. Do you wish to continue?"); //getString("MSG_MAX_DOCUMENTS_EXCEEDED", params)
		//Include the "Stop" warning icon
		args.add(CalwinPrompt.ARG_ICON, CalwinPrompt.ICON_STOP);
		//Include the "Continue" button
		//args.add(Prompt.ARG_BUTTON, new String[]{Prompt.CANCEL});
		args.add(CalwinPrompt.ARG_BUTTON,	new String[]{CalwinPrompt.YES, CalwinPrompt.NO});

		setComponentNested("cosdprompt", args, getContext(), new FormActionReturnListener(this, "onReturnFromPromptInput"));

		
		//Refresh any applicable data
		getTopForm().setRefreshDataRequired(true, true);
		
		DfLogger.debug(this, "Exit showMaxObjectsPrompt", null, null);
		
	}
	
	public void onReturnFromPromptInput(Form form, Map map)
	{
	//	check whether the confirmation prompt has returned
		String strButton = (String)map.get(CalwinPrompt.RTN_BUTTON);
		if (strButton != null && strButton.equals(CalwinPrompt.YES))
		{
			//If confirmed, allow super event handler to execute 
			//If not, do nothing
			setChangeProcess();
		}
	}
	
	public void setChangeProcess() {
		Hidden hidId = (Hidden)getControl("addObjIds",Hidden.class);
		String robjectId = hidId.getValue();
		
		arrRObjectIDs = robjectId.split(",");
		for(String item : arrRObjectIDs){
			IDfSysObject dfSysObject;
			try {
				dfSysObject = (IDfSysObject)ObjectCacheUtil.getObject (getDfSession(),item);
				Boolean m_processedCurrentValue = dfSysObject.getBoolean("new");
				dfSysObject.setBoolean("new", !m_processedCurrentValue);
				dfSysObject.save();
			} catch (DfException e) {
				DfLogger.error(this, e.getMessage() , null, e);
				throw new WrapperRuntimeException(e.getMessage(),e);
			}
			catch (Exception ee) {
				DfLogger.error(this, ee.getMessage() , null, ee);
				throw new WrapperRuntimeException(ee.getMessage(),ee);
			}			
		}
		
		
	}
	
    public void onControlInitialized(Form form, Control control)
    {
        if(control instanceof ActionControl)
        {
        	
        	if(getSelectedObjectIds() != null && !getSelectedObjectIds().isEmpty()){
        		for(String item : getSelectedObjectIds()) {
		            List objectList = new ArrayList(1);
		            objectList.add(item);
		            if(getResultSet().getResultsCount() > 0) {
		            	getResultSet().updateObjectRows(objectList, Collections.<String, String> emptyMap());
		            }
		         }
				//new SearchResultSet(new SearchInfo()).prepare();
				cosd_datagrid.getDataProvider().refresh(getResultSet());
        	}
        	
 
        
	
        }
    }
	
	public void onaction(ActionControl actionControl, ArgumentList argList)
	{
		strAction = argList.get(ActionControl.EVENT_ACTIONARG);
		

		
		if (("soms_merge_selected_documents".equals(strAction))) {
			callMashupAction(actionControl, argList);
		}
		
		if (("exportWithoutPolicy".equals(strAction))) {
			SessionState.setAttribute("calwinActionView", "exportWithoutPolicy");
			callMashupAction(actionControl, argList);
		}
 else {
			super.onaction(actionControl, argList);
		}
	}
	
	private void callMashupAction(ActionControl actionControl, ArgumentList argList) 
	{
		String objectIDToBePassed = null;
		String objectIDToBePassedTemp = null;
		if (actionControl instanceof ActionMultiselect) {
			DfLogger.debug(this, "It is a multi select action", null, null);

			ActionMultiselect actionMultiSelect = (ActionMultiselect) actionControl;

			String selectedArgsIndex = argList.get(ActionMultiselect.ARGUMENT_SELECTION);
			ArgumentList selectedItems[] = actionMultiSelect.getMultiselectItemArgs();

			for (int count = 0; count < selectedItems.length; count++) {
				if (selectedArgsIndex.charAt(count) == '1') {
					ArgumentList argListTemp = selectedItems[count];
					if (objectIDToBePassed == null) {
						objectIDToBePassed = argListTemp.get("objectId");
						objectIDToBePassedTemp = objectIDToBePassed;
					} else {
						objectIDToBePassed = objectIDToBePassed + "," + argListTemp.get("objectId");
					}
				}
			}
		}

		DfLogger.debug(this, "List of Objects Selected [" + objectIDToBePassed + "]", null, null);

		ArgumentList mashupArgs = new ArgumentList();
		SessionState.setAttribute("OBJECTID_FOR_MASHUP", objectIDToBePassed);
		//objectIDToBePassed = "0900cb1380003bfb";
		objectIDToBePassed = objectIDToBePassedTemp;
		mashupArgs.add("objectId", objectIDToBePassed);

		ActionService.execute("soms_mashup_content", mashupArgs, getContext(), this, null);
		
		

	}
	
	public void onDocumentActReturn(String strActionArgs, boolean bSuccess, Map map) {
		for(String item : arrRObjectIDs) {
            List objectList = new ArrayList(1);
            objectList.add(item);
            if(getResultSet().getResultsCount() > 0)
                getResultSet().updateObjectRows(objectList, Collections.<String, String> emptyMap());
		}
		
		cosd_datagrid.getDataProvider().refresh(getResultSet());
	}
	
    public void initAttributes() {
    	List<String> mandatoryAttrs = getAttributesManager().getMandatory();
    	mandatoryAttrs.add("computedName");
    	mandatoryAttrs.add("computedDate");
    	getAttributesManager().setMandatory(mandatoryAttrs);
    	super.initAttributes();
    }
	
    
    public void onRender() {

    	super.onRender();
    	Label searchlimitcontrollabel = (Label)getControl("searchlimitcontrollabel", Label.class);
    	if(getResultSet().getResultsCount() >= 350){
    		searchlimitcontrollabel.setVisible(true);    		
    	}
    	//System.out.println("Count: " + getResultSet().getResultsCount());
    	
    }
    
    private String dqlQuery;
    private Datagrid cosd_datagrid;
    private String[] arrRObjectIDs;
    private String strAction;
    private static final String strSearchActionControls = "|remove_document|unremove_document|changeprocessed_document";
	
}