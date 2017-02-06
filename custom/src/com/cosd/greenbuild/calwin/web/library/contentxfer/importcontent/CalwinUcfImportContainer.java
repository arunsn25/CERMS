package com.cosd.greenbuild.calwin.web.library.contentxfer.importcontent;

/*
******************************************************************
*   File Name: CalwinUcfImportContainer.java
*   Description: This Container class extends the OOTB container class
*   			 to assign values to custom attributes 
*   			 and link document to appropriate folder.
*   Author: Arun Shankar - HPE, LLC
*******************************************************************
*/

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import CalWINClient.CalWINClient;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.utils.COSDCalwinUtils;
import com.cosd.greenbuild.calwin.utils.CalwinWSConfig;
import com.cosd.greenbuild.calwin.web.library.changecasepersons.CalwinCasePerson;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfTime;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.contentxfer.ContentTransferException;
import com.documentum.web.contentxfer.JobAdapter;
import com.documentum.web.contentxfer.common.FileUploadUtil;
import com.documentum.web.contentxfer.impl.ImportService;
import com.documentum.web.form.Control;
import com.documentum.web.form.Form;
import com.documentum.web.form.Util;
import com.documentum.web.form.control.Button;
import com.documentum.web.form.control.DateTime;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.DataDropDownList;
import com.documentum.web.form.control.databound.DataProvider;
import com.documentum.webcomponent.common.WebComponentErrorService;
import com.documentum.webcomponent.library.contenttransfer.importcontent.UcfImportContainer;
import com.documentum.webcomponent.library.messages.MessageService;

public class CalwinUcfImportContainer extends UcfImportContainer {

	private static final long serialVersionUID = 1L;
	private static final String POLICY_NAME = "calwin_systemaccessstaff_acl";
	private List m_objectIdIter = null;
	
	/*
	 * Extended WDK Lifecycle method
	 */
	public void onInit(ArgumentList args)
	{
		Label pdfWarnTxtCtrl = (Label) getControl("calwinpdfwarningtext", Label.class);		
		pdfWarnTxtCtrl.setVisible(false);
		pdfWarnTxtCtrl.setEnabled(false);
		
		((Button)getOkButtonControl(true)).setEnabled(false);
		String folderId = null;
		try {
			folderId=getUserFolderId();
			args.replace("objectId", folderId);
		} catch (DfException e) {
			DfLogger.debug(this, "Exception in CalwinUcfImportContainer.", null, e);
            throw new WrapperRuntimeException("Exception in CalwinUcfImportContainer.", e);
		}
		caseNoArr = new ArrayList();
		categoryArr = new ArrayList();
		subcategoryArr = new ArrayList();
		doctypeArr = new ArrayList();
		receivedDateArr = new ArrayList();
		super.onInit(args);
	}
	
	/**
     * getUserFolderId(), called to get current user`s folder id.
     *
     * @return	String <folder id> of the current user
     */
    public String getUserFolderId() throws DfException {
        try {
            IDfSession session = getDfSession();
            IDfUser iuser = session.getUser(null);
            String userFolder = iuser.getDefaultFolder();
            IDfFolder fldr = session.getFolderByPath(userFolder);
            IDfId id = fldr.getObjectId();
            String folderId = id.toString();
            return folderId;
        } catch (Exception e) {
            DfLogger.debug(this, "Exception in CalwinUcfImportContainer.", null, e);
            throw new WrapperRuntimeException("Exception in CalwinUcfImportContainer.", e);
        }
    }
	
	/**
	 * @param args
	 */
	private void initDDDListControls(ArgumentList args) 
	{
		setCategoryDDDList(args);
		setSubcategoryDDDList(args);
		setDocumentTypesDDDList(args);
	}
	
	/**
	 * @param args
	 */
	public void setCategoryDDDList(ArgumentList args) 
	{
		DataDropDownList strengthList = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		strengthList.getDataProvider().setDfSession(getDfSession());
		strengthList.getDataProvider().setQuery(getCategoryQuery());
	}

	/**
	 * @param args
	 */
	public void setSubcategoryDDDList(ArgumentList args) 
	{
		DataDropDownList strengthList = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		strengthList.getDataProvider().setDfSession(getDfSession());
		strengthList.getDataProvider().setQuery(getSubCatQuery());
	}

	/**
	 * @param args
	 */
	public void setDocumentTypesDDDList(ArgumentList args) 
	{
		DataDropDownList doctypeList = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		doctypeList.getDataProvider().setDfSession(getDfSession());
		doctypeList.getDataProvider().setQuery(getDocTypeQuery());
	}

	
	private String getCategoryQuery() 
	{
		String query = COSDCalwinConstants.GET_DEFAULT_CATEGORY_QUERY;
		return query;
	}
	
	private String getSubCatQuery() 
	{
		String query = COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY;
		return query;
	}

	private String getDocTypeQuery() 
	{
		String query = COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY;
		return query;
	}
	
	/**
	 * Event Handler for Category control drop down list
	 * @param catDDDLstCtrl
	 * @param argumentList
	 */
	public void onChangeCategoryDDDList(DataDropDownList catDDDLstCtrl, ArgumentList argumentList)
	{
		
		DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		
		String catDDDLstVal = catDDDLstCtrl.getValue();
		String subcatDDDLstVal = subcatDDDLstCtrl.getValue();
		StringBuffer querySubCat;
		StringBuffer queryDoctype;
		DataProvider catDDDLstDP = catDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = doctypeDDDLstCtrl.getDataProvider();
		
		querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE);
		querySubCat.append(catDDDLstVal + "')");
		querySubCat.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		subcatDDDLstDP.setDfSession(getDfSession());
		
		queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_1);
		queryDoctype.append(catDDDLstVal + "')");
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		doctypeDDDLstDP.setDfSession(getDfSession());
		
		// if category is not blank AND (subcategory is blank OR subcategory is not blank)
		if ((catDDDLstVal != "") && (catDDDLstVal.length()!=0)) 
		{		
			subcatDDDLstDP.setQuery(querySubCat.toString());  // refresh the subcategory field as it depends on category field
			doctypeDDDLstDP.setQuery(queryDoctype.toString()); // refresh the doctype field as it depends on category field
		}
		// if category is blank
		else 
		{   
			catDDDLstDP.setQuery(getCategoryQuery()); // refresh category field, as it has been made blank
			subcatDDDLstDP.setQuery(getSubCatQuery()); // refresh subcategory field, as it is dependent on the category field and hence refreshed to default
			doctypeDDDLstDP.setQuery(getDocTypeQuery()); // refresh documenttype field, as it is dependent on the category field and hence refreshed to default
		}		
		
	}
	
	/**
	 * Event Handler for SubCategory control drop down list
	 * @param subcatDDDLstCtrl
	 * @param argumentList
	 */
	public void onChangeSubCategoryDDDList(DataDropDownList subcatDDDLstCtrl, ArgumentList argumentList)
	{
	
		DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		
		String catDDDLstVal = catDDDLstCtrl.getValue();
		String subcatDDDLstVal = subcatDDDLstCtrl.getValue();
		StringBuffer queryCat;
		StringBuffer queryDoctype;
		DataProvider catDDDLstDP = catDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = doctypeDDDLstCtrl.getDataProvider();
		
		queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_1);
		queryDoctype.append(subcatDDDLstVal + "')");
		
		// if category field is not blank, ensure that it is taken into account when determining the resulting document_type
		if ((catDDDLstVal != "") && (catDDDLstVal.length()!=0)) 
		{
			queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_2);
			queryDoctype.append(catDDDLstVal + "')");
		}
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_ORDER_BY);
		doctypeDDDLstDP.setDfSession(getDfSession());
		
		// if subcategory is not blank AND (category is blank OR category is not blank)	
		if ((subcatDDDLstVal != "") && (subcatDDDLstVal.length()!=0)) 
		{	 
			doctypeDDDLstDP.setQuery(queryDoctype.toString());
		} 
		// if subcategory is blank
		else 
		{
			// if category is blank
			if (("".equals(catDDDLstVal)) && (catDDDLstVal.length()==0)) 
			{
				subcatDDDLstDP.setQuery(getSubCatQuery());  // refresh the subcategory field when set to blank
				doctypeDDDLstDP.setQuery(getDocTypeQuery());  // refresh the doctype if the category field value is blank
			} 
			// if category is not blank
			else 
			{
				queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL);
				queryDoctype.append(catDDDLstVal + "')");
				queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL_ORDER_BY);
				doctypeDDDLstDP.setQuery(queryDoctype.toString());   // the doctype field values are based on the category field value
			}
		}		
		
	}
	
	/* 
	 * Extended framework method
	 */
	protected void handleOnReturnFromProgressSuccess(Form form, Map map, JobAdapter job) {
		List ids = null;
		COSDCalwinUtils util = new COSDCalwinUtils();
		String currId = null;
		String currType = null;

		Text caseNoCtrl = null;
		DataDropDownList catDDDLstCtrl = null;
		DataDropDownList subcatDDDLstCtrl = null;
		DataDropDownList doctypeDDDLstCtrl = null;

		IDfSysObject newObject = null;
		IDfSession session = getDfSession();

		try {
			IDfClient client = DfClient.getLocalClient();
			IDfSessionManager sessMgr = session.getSessionManager();


			if(job.getService() instanceof ImportService) 
			{
				ImportService service = (ImportService) job.getService();
				ids = service.getNewObjectIds();
				Iterator listIter = ids.iterator();
				List objList = new ArrayList();
				while(listIter.hasNext()) {
					String id = listIter.next().toString();
					if(!id.equals("0000000000000000")) {
						IDfSysObject sysObj = (IDfSysObject) session.getObject(new DfId(id));
						objList.add(sysObj);
					}
				}
				Iterator objIter = objList.iterator();
				Integer i = 0;
				while(objIter.hasNext()) 
				{

					String caseType = "";

					/* Nick Doolin - BreakFix 100-02-12686966 */
					String strAclName = "";
					String caseConf = "";
					/* BreakFix 100-02-12686966 */

					String caseNo = ((String)caseNoArr.get(i)).toUpperCase().trim(); // AS 08/15/2016: remove trailing white space

					/* Nick Doolin - BreakFix 100-02-12686966 */
					IDfSysObject sysObj = (IDfSysObject)session.getObjectByQualification("calwin_case_folder where case_no ='" + caseNo + "'" );

					Boolean folderExist = false;
					if(sysObj != null)
					{
						strAclName = sysObj.getACLName();
						caseConf = sysObj.getString("case_confidentiality");
						caseType = sysObj.getString("case_type");
						if ("calwin_edocimport_acl".equalsIgnoreCase(strAclName))
						{
							strAclName = getACLFromCaseTypeAndConfidentiality(strAclName,caseType,caseConf);
						}
						folderExist = true;
					} else
					{
						/* BreakFix 100-02-12686966 */
						CalwinWSConfig calWSConfig = new CalwinWSConfig();
						CalWINClient calWSClient = calWSConfig.CalwinWSInfo();
						String result2 = calWSClient.getCaseProgramInfoFromCaseId(caseNo);
						List<String> c1 = getProgramInfoFromXML(result2);

						/* Nick Doolin - BreakFix 100-02-12686966 */
						caseConf = c1.get(1);
						/* BreakFix 100-02-12686966 */

						for (int x = 0; x < c1.size(); x++) {
							String y = c1.get(x);
							if ("AA".equalsIgnoreCase(y))
								caseType = "AA";
							if ("FC".equalsIgnoreCase(y))
								caseType = "FC";
							if ("KG".equalsIgnoreCase(y))
								caseType = "KG";
						}
						if ("".equalsIgnoreCase(caseType))
							caseType = "Eligibility";

						/* Nick Doolin - BreakFix 100-02-12686966 */
						strAclName = getACLToBeApplied(caseConf, caseType);
						/* BreakFix 100-02-12686966 */

						caseConf = ("N".equalsIgnoreCase(caseConf))?"NON-SECURED":"SECURED";
						if ("FC".equalsIgnoreCase(caseType) || "KG".equalsIgnoreCase(caseType)){
							caseType = "FosterCare - KinGap";
						}
						if ("AA".equalsIgnoreCase(caseType)){
							caseType = "Adoption";
						}

					}

					if (isCaseFolderNew()) /* CHANGE #2: Arun - CERMS UAT Change - 10/07/2015 */ /* CHANGE #1: Nick Doolin - BreakFix 100-02-12686966 */
					{
						createCaseFolder(session, caseNo, caseConf, caseType, strAclName);
					}



					newObject = (IDfSysObject) objIter.next();
					currId = newObject.getObjectId().getId();
					currType = newObject.getTypeName();

					// set case_number, category,subcategory,doctype here...
					newObject.setString("case_no", caseNoArr.get(i).toString().toUpperCase());
					newObject.setString("category", util.getCatSubcatDoctypeValueID(categoryArr.get(i).toString(),true,false,session));
					newObject.setString("sub_category", util.getCatSubcatDoctypeValueID(subcategoryArr.get(i).toString(),false,true,session));
					newObject.setString("doc_type", util.getCatSubcatDoctypeValueID(doctypeArr.get(i).toString(),false,false,session));
					newObject.setBoolean("new_document",true);
					newObject.setString("case_confidentiality", caseConf);
					newObject.setString("case_type", caseType);
					IDfTime dateReceivedDate = new DfTime(receivedDateArr.get(i).toString(),IDfTime.DF_TIME_PATTERN1);
					newObject.setTime("received_date", dateReceivedDate);

					IDfACL m_acl = null;	
					try 
					{
						m_acl = (IDfACL)session.getObjectByQualification("dm_acl where object_name='" + strAclName + "'");
					} 
					catch (DfException e) 
					{
						e.printStackTrace();
					}
					newObject.setACL(m_acl);
					newObject.setACLDomain(m_acl.getDomain());
					newObject.setOwnerName("adobe_lc_user");

					IDfId defaultFldrId = newObject.getFolderId(0);
					newObject.link("/" + EIMPORT_CABINET_NAME + "/"+caseNo);
					newObject.unlink(defaultFldrId.getId());

					newObject.saveLock();

					recordEntryForRM(currId,m_acl,session);
					i++;
				}
				if(objList != null) {
					//setReturnValue(NEW_OBJECT_IDS, objList);
					//setObjectNames(objectNames);
				}
			}

		} catch (ContentTransferException e) {
			throw new WrapperRuntimeException("Import Failed", e);
		} catch (DfException e) {
			throw new WrapperRuntimeException("Import Failed", e);
		}
		super.handleOnReturnFromProgressSuccess(form, map, job);
	}
	
	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#addFinalSuccessMessage()
	 */
	protected void addFinalSuccessMessage()
	{
		MessageService.addMessage(this, "MSG_OPERATION_SUCCESSFUL");
	}

	/**
	 * @param sess
	 * @param caseNumber
	 * @param caseConf
	 * @param caseType
	 * @param strAclName
	 * @return
	 */
	private IDfId createCaseFolder(IDfSession sess, String caseNumber, String caseConf, String caseType, String strAclName)
	{
		IDfId fldId = null;
		
	     try {
		   	 IDfFolder newFldr = (IDfFolder) sess.newObject("calwin_case_folder");  // creates a new folder of said type
		     newFldr.setObjectName(caseNumber);  // sets the object name to the case number
		     newFldr.setString("case_type",caseType);  // sets the case type
		     newFldr.setString("case_no",caseNumber);  // sets the case number
		     newFldr.setString("case_confidentiality", caseConf);  // sets the case confidentiality attribute
		     newFldr.link("/" + EIMPORT_CABINET_NAME);  // links the folder to the cabinet
		     IDfACL m_acl = null;	
		     strAclName = "calwin_edocimport_acl";
		     try 
		     {
		    	 m_acl = (IDfACL)sess.getObjectByQualification("dm_acl where object_name='" + strAclName + "'");
		     } 
		     catch (DfException e) 
		     {
		    	 e.printStackTrace();
		     }
		     newFldr.setACL(m_acl);
		     newFldr.setACLDomain(m_acl.getDomain());
		     newFldr.save();

			fldId = newFldr.getObjectId();
		} catch (DfException e) {
			e.printStackTrace();
		}
	     
		return fldId;
		
	}

	/**
	 * @param strCaseConf
	 * @param strCaseType
	 * @return
	 */
	private String getACLToBeApplied(String strCaseConf, String strCaseType) 
	{
		
		String strAclName = null;
		
		if ("N".equalsIgnoreCase(strCaseConf) && "Eligibility".equalsIgnoreCase(strCaseType))
			strAclName = "calwin_eligibility_acl";
		if ("N".equalsIgnoreCase(strCaseConf) && "AA".equalsIgnoreCase(strCaseType))
			strAclName = "calwin_adoption_acl";
		if ("N".equalsIgnoreCase(strCaseConf) && ("FC".equalsIgnoreCase(strCaseType) || "KG".equalsIgnoreCase(strCaseType)))
			strAclName = "calwin_fc_kg_acl";
		if ("Y".equalsIgnoreCase(strCaseConf) && "Eligibility".equalsIgnoreCase(strCaseType))
			strAclName = "calwin_eligibility_conf_acl";
		if ("Y".equalsIgnoreCase(strCaseConf) && "AA".equalsIgnoreCase(strCaseType))
			strAclName = "calwin_adoption_conf_acl";
		if ("Y".equalsIgnoreCase(strCaseConf) && ("FC".equalsIgnoreCase(strCaseType) || "KG".equalsIgnoreCase(strCaseType)))
			strAclName = "calwin_fc_kg_conf_acl";
	
		
		return strAclName;
	}

	/**
	 * @param objId
	 * @param aclName
	 * @param sess
	 */
	private void recordEntryForRM(String objId, IDfACL aclName, IDfSession sess) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		   
		try {
			IDfDocument sysObj = (IDfDocument) sess.newObject("calwin_case_mashup");
			sysObj.setString("mashup_owner", sess.getLoginUserName());
			sysObj.setString("policy_name", aclName.getObjectName());
			sysObj.setString("request_type", "one-off");
			sysObj.setString("policy_set", "CalWINS");
			sysObj.setId("mashup_ids", new DfId(objId));
			sysObj.setObjectName("CalWIN-OneOff_"+dateFormat.format(date));
			sysObj.link("/CalWIN-Mashup");
			sysObj.save();
		} catch (DfException e) {
			e.printStackTrace();
		}
		
	}
	
	  /* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#getEventNames()
	 */
	public String[] getEventNames()
	  {
	    return Util.concatarray(super.getEventNames(), new String[] { "onsetcatsubcatdoctype" });
	  }

	  /* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#getEventHandlerMethod(java.lang.String)
	 */
	public String getEventHandlerMethod(String strEvent)
	  {
	    String strMethod = null;
	    if (strEvent.equals("onsetcatsubcatdoctype"))
	    {
	      strMethod = "onsetcatsubcatdoctype";
	    }
	    else
	    {
	      strMethod = super.getEventHandlerMethod(strEvent);
	    }
	    return strMethod;
	  }

	  /* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#getEventHandler(java.lang.String)
	 */
	public Control getEventHandler(String strEvent)
	  {
	    Control handler = null;
	    if (strEvent.equals("onsetcatsubcatdoctype"))
	    {
	      handler = this;
	    }
	    else
	    {
	      handler = super.getEventHandler(strEvent);
	    }
	    return handler;
	  }
	
	/**
	 * Event Handler for capturing Category/Subcategory/Doctype control values
	 * @param control
	 * @param arg
	 */
	public void onsetcatsubcatdoctype(Control control, ArgumentList arg){
	    strCalwinCaseNo = arg.get("calwincaseno");
	    String strCalwinCategory = arg.get("calwincategory");
	    String strCalwinSubcategory = arg.get("calwinsubcategory");
	    String strCalwinDoctype = arg.get("calwindoctype");
	    String strCalwinCaseconf = arg.get("calwincaseconf");
	    String strCalwinCasetype = arg.get("calwincasetype");
	    String strReceivedDate = arg.get("receivedDate");

	    caseNoArr.add(strCalwinCaseNo);
	    categoryArr.add(strCalwinCategory);
	    subcategoryArr.add(strCalwinSubcategory);
	    doctypeArr.add(strCalwinDoctype);
	    receivedDateArr.add(strReceivedDate);
	}
	
	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#onPrevPage()
	 */
	public boolean onPrevPage()
	{
		setNextCounter(NextCounter - 1);
		return super.onPrevPage();
	}
	
	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#onNextPage()
	 */
	public boolean onNextPage()
	{
		/* Arun - BreakFix 100-02-12663697 */
		Label docTypeErrLbl = (Label)getContainedComponent().getControl("doctypeerrorlabel", Label.class);
		docTypeErrLbl.setVisible(false);
		/* BreakFix 100-02-12663697 */

		setNextCounter(NextCounter + 1);
		if ((getNextCounter() > getNextMaxCounter()) || (getNextMaxCounter()==0)){
			setNextMaxCounter(NextCounter);
		}

		Text caseNoCtrl = (Text) getContainedComponent().getControl("casenumbertext");
		DateTime dateFromPickerCtrl = (DateTime) getContainedComponent().getControl("datefrompicker");

		/* Arun - BreakFix 100-02-12663697 */
		DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getContainedComponent().getControl("doctypedddlst", DataDropDownList.class);
		//doctypeDDDLstCtrl.getValue();
		/* BreakFix 100-02-12663697 */

		Label caseNumberErrLbl = (Label)getContainedComponent().getControl("casenumbererrorlabel", Label.class);
		caseNumberErrLbl.setVisible(false);
		Label datetimeErrLbl = (Label)getContainedComponent().getControl("datetimeerrorlabel", Label.class);
		datetimeErrLbl.setVisible(false);

		if (dateFromPickerCtrl != null){
			Date ctrlDate = dateFromPickerCtrl.toDate();
			Date today = new Date();
			if (ctrlDate.compareTo(today) > 0) {
				datetimeErrLbl.setVisible(true);
				return false;
			}
		}

		/* Nick Doolin - BreakFix 100-02-12686966 */
		if (caseNoCtrl != null) 
		{
			IDfSysObject sysObj = null;
			int iPerm = 0;
			/* BreakFix 100-02-12686966 */

			strCalwinCaseNo = caseNoCtrl.getValue().toUpperCase().trim(); // AS 08/15/2016: remove trailing white space
			/* Nick Doolin - BreakFix 100-02-12686966 */
			String strAclName = "";
			try
			{
				sysObj = (IDfSysObject)getDfSession().getObjectByQualification("calwin_case_folder where case_no ='" + strCalwinCaseNo + "'" );
			}
			catch(DfException ex)
			{
				ex.printStackTrace();
			}
			if(sysObj != null)
			{
				try 
				{
					strAclName = sysObj.getACLName();
					if ("calwin_edocimport_acl".equalsIgnoreCase(strAclName))
					{
						String caseTypeForDerivingACL = sysObj.getString("case_type");
						String caseConfForDerivingACL = sysObj.getString("case_confidentiality");
						strAclName = getACLFromCaseTypeAndConfidentiality(strAclName,caseTypeForDerivingACL,caseConfForDerivingACL);
					}
				} catch (DfException e) {
					e.printStackTrace();
				}
			} else
			{
				/* BreakFix 100-02-12686966 */
				String caseType = "";
				CalwinWSConfig calWSConfig = new CalwinWSConfig();
				CalWINClient calWSClient = calWSConfig.CalwinWSInfo();
				String result2 = calWSClient.getCaseProgramInfoFromCaseId(strCalwinCaseNo);
				List<String> c1 = getProgramInfoFromXML(result2);

				if (!("0".equalsIgnoreCase(c1.get(0))))
				{
					caseNumberErrLbl.setLabel("Error: Please provide a valid case number.");
					caseNumberErrLbl.setVisible(true);
					return false;
				}				

				String caseConf = c1.get(1);

				for (int x = 0; x < c1.size(); x++) {
					String y = c1.get(x);
					if ("AA".equalsIgnoreCase(y))
						caseType = "AA";
					if ("FC".equalsIgnoreCase(y))
						caseType = "FC";
					if ("KG".equalsIgnoreCase(y))
						caseType = "KG";
				}
				if ("".equalsIgnoreCase(caseType))
					caseType = "Eligibility";

				/* Nick Doolin - BreakFix 100-02-12686966 */
				strAclName = getACLToBeApplied(caseConf, caseType);
			}
			/* BreakFix 100-02-12686966 */
			try {
				IDfACL acl = (IDfACL) getDfSession().getObjectByQualification("dm_acl where object_name="+"'"+strAclName+"'");
				if(acl!=null)
				{
					iPerm = acl.getPermit("");
				}
			} catch (DfException e) {
				e.printStackTrace();
			}

			/* Arun - BreakFix 100-02-12663697 */
			String doctypeDDDLstVal = doctypeDDDLstCtrl.getValue();
			if (doctypeDDDLstVal.length()==0 || "".equals(doctypeDDDLstVal))
			{
				docTypeErrLbl.setLabel("Error: Document Type cannot be empty");
				docTypeErrLbl.setVisible(true);
				return false;
			}
			/* BreakFix 100-02-12663697 */	

			if (strCalwinCaseNo.length()==0){
				caseNumberErrLbl.setLabel("Error: Case Number cannot be empty");
				caseNumberErrLbl.setVisible(true);
				return false;
			}

			if ((!hasUserPermissionOnCaseFolder())){ // || (iPerm < 6)
				caseNumberErrLbl.setLabel("Error: You do not have sufficient permission to import documents for this case number.");
				caseNumberErrLbl.setVisible(true);
				return false;
			}
			if (isUserNotInImportGroup()){
				caseNumberErrLbl.setLabel("Error: You do not have sufficient permission to import documents for this case number.");
				caseNumberErrLbl.setVisible(true);
				return false;
			}
			return super.onNextPage();
		} else {
			return super.onNextPage();
		}
	}
	
	private boolean isUserNotInImportGroup(){
		boolean bUserNotInImportGroup = true;
		try {
			String dqlGrpQuery = "dm_group where group_name='"+ IMPORT_GROUP_NAME +"'";
			IDfGroup idfGrp = (IDfGroup)getDfSession().getObjectByQualification(dqlGrpQuery);
			if (idfGrp.isUserInGroup(getDfSession().getUser("").getUserName()))
			{
				bUserNotInImportGroup = false;
			}
		} catch (DfException e) {
			e.printStackTrace();
		}
		return bUserNotInImportGroup;
	}
	
	private boolean hasUserPermissionOnCaseFolder()
	{
		boolean hasUserPermission = true;
		try {
			if (!isCaseFolderNew()){
				String dqlFldQuery = "calwin_case_folder where object_name='" + strCalwinCaseNo + "' and any r_folder_path='/" + EIMPORT_CABINET_NAME + "/" + strCalwinCaseNo + "'";
				IDfFolder idfFldr = (IDfFolder)getDfSession().getObjectByQualification(dqlFldQuery);
				if (idfFldr.getPermit() < IDfACL.DF_PERMIT_WRITE) {
					hasUserPermission = false;
				}
			}
		} catch (DfException e) {
			e.printStackTrace();
		}
		return hasUserPermission;
	}
	
	private boolean isCaseFolderNew(){
		boolean bNew = false;
		try {	
			String dqlFldQuery = "calwin_case_folder where object_name='" + strCalwinCaseNo + "' and any r_folder_path='/" + EIMPORT_CABINET_NAME + "/" + strCalwinCaseNo + "'";
			IDfFolder idfFldr = (IDfFolder)getDfSession().getObjectByQualification(dqlFldQuery);
			if (idfFldr == null){
				bNew = true;
			}
		} catch (DfException e) {
			e.printStackTrace();
		}
		return bNew;
	}
	
	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#processOnNextPageFromFileSelection()
	 */
	protected boolean processOnNextPageFromFileSelection(){
		super.processOnNextPageFromFileSelection();
		Collection files = getUploadedFilesFromRequest();
	    StringBuffer maliciousFileExts;
	    Label pdfWarnTxtCtrl = (Label) getControl("calwinpdfwarningtext", Label.class);
	    pdfWarnTxtCtrl.setEnabled(false);
	    if (((maliciousFileExts = findMaliciousFileExts1(files)) != null) && (maliciousFileExts.length() > 0))
	    {
	      Object[] params = { maliciousFileExts };
	      setReturnError("MSG_MALICIOUS_FILE_SELECTED", params, null);
	      WebComponentErrorService.getService().setNonFatalError(this, "MSG_MALICIOUS_FILE_SELECTED", params, null);
	      pdfWarnTxtCtrl.setEnabled(true);
	      pdfWarnTxtCtrl.setVisible(true);
	      if (files.size() < 1) {
	        return false;
	      }
	      return false;
	    }
	    return true;
	}
	
	  /**
	 * @param files
	 * @return
	 */
	protected StringBuffer findMaliciousFileExts1(Collection files)
	  {
	    int count = 0; StringBuffer errorMsg = new StringBuffer();
	    for (Iterator iter = files.iterator(); iter.hasNext(); )
	    {
	      ImportFile file = (ImportFile)iter.next();

	      String extn = FileUploadUtil.extractExtension(file.getFileName());
	      if (!isFilePDFFormat(extn, file.getFilePath()))
	      {
	        errorMsg.append("." + extn);
	        if (count < files.size() - 1)
	          errorMsg.append(",");
	        iter.remove();
	      }
	      count++;
	    }
	    return errorMsg;
	  }
	  
	  /**
	 * @param extn
	 * @param filePath
	 * @return
	 */
	public static boolean isFilePDFFormat(String extn, String filePath)
	  {
	    if ("pdf".equalsIgnoreCase(extn))
	    {
	      return true;
	    }
	    return false;
	  }
	  
	  /* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContentContainer#onOk(com.documentum.web.form.Control, com.documentum.web.common.ArgumentList)
	 */
	public void onOk(Control button, ArgumentList args)
	{
		// AS: 06/24/2016
		// re-initialize the static fields
		setNextCounter(0);
		setNextMaxCounter(0);
		CalwinImportContent.setbMaintainState(true);
		CalwinImportContent.setiMaxCounterForMaintainState(0);
		CalwinImportContent.setbDocTypeChanged(false);

		boolean isCasePresent = true;

		Text caseNoCtrl = (Text) getContainedComponent().getControl("casenumbertext");
		//strCalwinCaseNo = caseNoCtrl.getValue();
		DateTime dateFromPickerCtrl = (DateTime) getContainedComponent().getControl("datefrompicker");

		/* Arun - BreakFix 100-02-12663697 */
		DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getContainedComponent().getControl("doctypedddlst", DataDropDownList.class);
		/* BreakFix 100-02-12663697 */

		Label caseNumberErrLbl = (Label)getContainedComponent().getControl("casenumbererrorlabel", Label.class);
		caseNumberErrLbl.setVisible(false);
		Label datetimeErrLbl = (Label)getContainedComponent().getControl("datetimeerrorlabel", Label.class);
		datetimeErrLbl.setVisible(false);

		/* Arun - BreakFix 100-02-12663697 */
		Label docTypeErrLbl = (Label)getContainedComponent().getControl("doctypeerrorlabel", Label.class);
		docTypeErrLbl.setVisible(false);
		/* BreakFix 100-02-12663697 */

		Date ctrlDate = null;
		Date today = null;


		IDfSysObject sysObj = null;
		int iPerm = 0;
		/* BreakFix 100-02-12686966 */

		strCalwinCaseNo = caseNoCtrl.getValue().toUpperCase().trim(); // AS 08/15/2016: remove trailing white space
		/* Nick Doolin - BreakFix 100-02-12686966 */
		String strAclName = "";
		try
		{
			sysObj = (IDfSysObject)getDfSession().getObjectByQualification("calwin_case_folder where case_no ='" + strCalwinCaseNo + "'" );
		}
		catch(DfException ex)
		{
			ex.printStackTrace();
		}
		if(sysObj != null)
		{
			try 
			{
				strAclName = sysObj.getACLName();
				if ("calwin_edocimport_acl".equalsIgnoreCase(strAclName))
				{
					String caseTypeForDerivingACL = sysObj.getString("case_type");
					String caseConfForDerivingACL = sysObj.getString("case_confidentiality");
					strAclName = getACLFromCaseTypeAndConfidentiality(strAclName,caseTypeForDerivingACL,caseConfForDerivingACL);
				}
			} catch (DfException e) {
				e.printStackTrace();
			}
		} else
		{
			String caseType = "";
			CalwinWSConfig calWSConfig = new CalwinWSConfig();
			CalWINClient calWSClient = calWSConfig.CalwinWSInfo();
			String result2 = calWSClient.getCaseProgramInfoFromCaseId(strCalwinCaseNo);
			List<String> c1 = getProgramInfoFromXML(result2);

			if (!("0".equalsIgnoreCase(c1.get(0))))
			{
				isCasePresent = false;
			}

			String caseConf = c1.get(1);

			for (int x = 0; x < c1.size(); x++) {
				String y = c1.get(x);
				if ("AA".equalsIgnoreCase(y))
					caseType = "AA";
				if ("FC".equalsIgnoreCase(y))
					caseType = "FC";
				if ("KG".equalsIgnoreCase(y))
					caseType = "KG";
			}
			if ("".equalsIgnoreCase(caseType))
				caseType = "Eligibility";			

			strAclName = getACLToBeApplied(caseConf, caseType);
		}

		try {
			IDfACL acl = (IDfACL) getDfSession().getObjectByQualification("dm_acl where object_name="+"'"+strAclName+"'");
			if(acl!=null)
			{
				iPerm = acl.getPermit("");
			}

		} catch (DfException e) {
			e.printStackTrace();
		}

		if (dateFromPickerCtrl != null){
			ctrlDate = dateFromPickerCtrl.toDate();
			today = new Date();
		}

		if (ctrlDate.compareTo(today) > 0) {
			datetimeErrLbl.setVisible(true);
		}  /* Arun - BreakFix 100-02-12663697 */ else if (doctypeDDDLstCtrl.getValue().length()==0 || "".equals(doctypeDDDLstCtrl.getValue()))
		{
			docTypeErrLbl.setLabel("Error: Document Type cannot be empty");
			docTypeErrLbl.setVisible(true);
		} /* BreakFix 100-02-12663697 */ else if (strCalwinCaseNo.length()==0 && "".equals(strCalwinCaseNo)) {
			caseNumberErrLbl.setLabel("Error: Case Number cannot be empty");
			caseNumberErrLbl.setVisible(true);
		} else if (!isCasePresent)
		{
			caseNumberErrLbl.setLabel("Error: Please provide a valid case number.");
			caseNumberErrLbl.setVisible(true);
		} else if ((!hasUserPermissionOnCaseFolder())){ //  || (iPerm < 6)
			caseNumberErrLbl.setLabel("Error: You do not have sufficient permission to import documents for this case number.");
			caseNumberErrLbl.setVisible(true);
		} else if (isUserNotInImportGroup()){
			caseNumberErrLbl.setLabel("Error: You do not have sufficient permission to import documents for this case number.");
			caseNumberErrLbl.setVisible(true);
		} else {
			super.onOk(button, args);
		}
	}
	  
	  
	  /**
	 * @param strAclName
	 * @param caseTypeForDerivingACL
	 * @param caseConfForDerivingACL
	 * @return
	 */
	private String getACLFromCaseTypeAndConfidentiality(String strAclName, String caseTypeForDerivingACL, String caseConfForDerivingACL)
	  {
		  String aclName = "";
		  
		  //Case Type Values
		  String strEligAclName = "Eligibility";
		  String strFC_KGAclName = "FosterCare - KinGap";
		  String strFCKGAclName = "fosterkingap";
		  String strAdoptionAclName = "adoption";
		  
		  //Case Confidentiality Values
		  String strCaseConfNS = "NON-SECURED";
		  String strCaseConfSecure = "SECURED";
		  String strCaseConfFalse = "False";
		  String strCaseConfTrue = "True";
		  
		  if (strEligAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfNS.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_eligibility_acl";
		  }
		  if (strEligAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfFalse.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_eligibility_acl";
		  }
		  if (strEligAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfSecure.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_eligibility_conf_acl";
		  }
		  if (strEligAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfTrue.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_eligibility_conf_acl";
		  }
		  if (strFC_KGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfNS.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_acl";
		  }
		  if (strFC_KGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfFalse.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_acl";
		  }
		  if (strFC_KGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfSecure.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_conf_acl";
		  }
		  if (strFC_KGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfTrue.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_conf_acl";
		  }
		  if (strFCKGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfNS.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_acl";
		  }
		  if (strFCKGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfFalse.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_acl";
		  }
		  if (strFCKGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfSecure.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_conf_acl";
		  }
		  if (strFCKGAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfTrue.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_fc_kg_conf_acl";
		  }
		  if (strAdoptionAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfNS.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_adoption_acl";
		  }
		  if (strAdoptionAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfFalse.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_adoption_acl";
		  }
		  if (strAdoptionAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfSecure.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_adoption_conf_acl";
		  }
		  if (strAdoptionAclName.equalsIgnoreCase(caseTypeForDerivingACL) && strCaseConfTrue.equalsIgnoreCase(caseConfForDerivingACL)){
			  aclName = "calwin_adoption_conf_acl";
		  }
		  return aclName;
	  }
	  
	  
/*		private boolean isCaseInfoPresent() {

			String result,result2 = null;
			Boolean bPresent = true;
			CalwinWSConfig calWSConfig = new CalwinWSConfig();
			CalWINClient calWSClient = calWSConfig.CalwinWSInfo();
			result2 = calWSClient.getCaseProgramInfoFromCaseId(strCalwinCaseNo.toUpperCase());
			List<String> c1 = getProgramInfoFromXML(result2);
			if ("0".equalsIgnoreCase(c1.get(0)))
				bPresent=false;
			
			return bPresent;
		}*/
		
	    /**
	     * @param result
	     * @return
	     */
	    private ArrayList<String> getProgramInfoFromXML(String result) {
	    	String xmlProlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	    	result = xmlProlog + result;
	    	ArrayList<String> pgmInfo = new ArrayList<String>();
	    	   try {
	    		   
	    		   DocumentBuilder builder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();  
	    		   Document document = builder.parse(new InputSource(new StringReader(result.toString())));         
	    		   Element root = document.getDocumentElement();  
	    		                           
	    		   NodeList children = root.getChildNodes();  
	  				pgmInfo.add(getTextValue(root,"ReturnCode"));
	  				pgmInfo.add(getTextValue(root,"Confidentiality"));
	    		   
	    			//get a nodelist of elements
   		  				NodeList nl = root.getElementsByTagName("Programs");
	    		  		if(nl != null && nl.getLength() > 0) {
	    		  			for(int i = 0 ; i < nl.getLength();i++) {
	    		  				
	    		  				Element el = (Element)nl.item(i);
	    		  				
	    		  				NodeList pnl = el.getElementsByTagName("Program");

	    	    		  		if(pnl != null && pnl.getLength() > 0) {
	    	    		  			for(int j = 0 ; j < pnl.getLength();j++) {
	    	    		  				
	    	    		  				Element pel = (Element)pnl.item(j);
			    		  				pgmInfo.add(pel.getFirstChild() == null ? "": pel.getFirstChild().getNodeValue());
			    		  				
	    	    		  			}
	    	    		  		}
	    		  			}
	    		  		}        
	    		     } catch (org.xml.sax.SAXException e) {  
	    		                  e.printStackTrace(); // or throw a Exception  
	    		     } catch (java.io.IOException e) {  
	    		                  e.printStackTrace(); // or throw a Exception  
	    		     } catch (javax.xml.parsers.ParserConfigurationException e) {  
	    		              e.printStackTrace(); // or throw a Exception  
	    		     }
	    	return pgmInfo;
		}
		
		
	    /**
	     * @param result
	     * @return
	     */
	    private ArrayList<CalwinCasePerson> getInfoFromXML(String result) {
	    	String xmlProlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	    	result = xmlProlog + result;
	    	ArrayList<CalwinCasePerson> myEmpls = new ArrayList<CalwinCasePerson>();
	    	   try {
	    		   
	    		   DocumentBuilder builder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();  
	    		   Document document = builder.parse(new InputSource(new StringReader(result.toString())));         
	    		   Element root = document.getDocumentElement();  
	    		                           
	    		   NodeList children = root.getChildNodes();  
	    		   
	    			//get a nodelist of elements
	    		  		NodeList nl = root.getElementsByTagName("CasePersons");
	    		  		if(nl != null && nl.getLength() > 0) {
	    		  			for(int i = 0 ; i < nl.getLength();i++) {

	    		  				//get the employee element
	    		  				Element el = (Element)nl.item(i);
	    		  				
	    		  				NodeList casePersonChildNodes = el.getChildNodes();
	    		    		   
	    		  				NodeList pnl = el.getElementsByTagName("Person");

	    	    		  		if(pnl != null && pnl.getLength() > 0) {
	    	    		  			for(int j = 0 ; j < pnl.getLength();j++) {
	    	    		  				
	    	    		  				Element pel = (Element)pnl.item(j);
	    	    		  				
			    		  				//get the Employee object
			    		  				CalwinCasePerson e = getCalwinCasePerson(pel);
			
			    		  				//add it to list
			    		  				myEmpls.add(e);
			    		  				
	    	    		  			}
	    	    		  		}
	    		  			}
	    		  		}        
	    		     } catch (org.xml.sax.SAXException e) {  
	    		                  e.printStackTrace(); // or throw a Exception  
	    		     } catch (java.io.IOException e) {  
	    		                  e.printStackTrace(); // or throw a Exception  
	    		     } catch (javax.xml.parsers.ParserConfigurationException e) {  
	    		              e.printStackTrace(); // or throw a Exception  
	    		     }
	    	return myEmpls;
		}
	    
		/**
		 * @param empEl
		 * @return
		 */
		private CalwinCasePerson getCalwinCasePerson(Element empEl) {
			
			//for each <employee> element get text or int values of 
			//name ,id, age and name
			String firstname = getTextValue(empEl,"FirstName");
			String lastname = getTextValue(empEl,"LastName");
			String middlename = getTextValue(empEl,"MiddleName");
			String cwin = getTextValue(empEl,"CWIN");
			String ssn = getTextValue(empEl,"SSN");
			String suffix = getTextValue(empEl,"Suffix");
			String dob = getTextValue(empEl,"DOB");
			String cin = getTextValue(empEl,"CIN");
			
			//Create a new Employee with the value read from the xml nodes
			CalwinCasePerson e = new CalwinCasePerson(firstname,lastname,middlename,cwin,ssn,suffix,dob,cin);
			
			return e;
		}
		
		/**
		 * @param ele
		 * @param tagName
		 * @return
		 */
		private String getTextValue(Element ele, String tagName) {
			String textVal = "";
			NodeList nl = ele.getElementsByTagName(tagName);
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				textVal = el.getFirstChild() == null ? "": el.getFirstChild().getNodeValue();
			}

			return textVal;
		}
		
		public static int getNextCounter() {
			return NextCounter;
		}

		/**
		 * @param next
		 */
		public static void setNextCounter(int next) {
			NextCounter = next;
		}

		public static int getNextMaxCounter() {
			return NextMaxCounter;
		}

		/**
		 * @param nextMax
		 */
		public static void setNextMaxCounter(int nextMax) {
			NextMaxCounter = nextMax;
		}
		
		/* 
		 * Extended method from com.documentum.web.formext.component.DialogContainer#onCancelChanges()
		 */
		public boolean onCancelChanges() {
			// AS: 06/24/2016
			// re-initialize the static fields
			setNextCounter(0);
			setNextMaxCounter(0);
			CalwinImportContent.setbMaintainState(true);
			CalwinImportContent.setiMaxCounterForMaintainState(0);
			CalwinImportContent.setbDocTypeChanged(false);
			return super.onCancelChanges();
		}
		
	public ArrayList caseNoArr; 
	public ArrayList categoryArr, subcategoryArr, doctypeArr, receivedDateArr; //, caseconfArr, casetypeArr;
	private String strCalwinCaseNo;
	private static int NextCounter = 0, NextMaxCounter = 0;
	private final String IMPORT_GROUP_NAME = "edocimport";
	private final String EIMPORT_CABINET_NAME = "eImport"; //CalWIN
	
}