package com.cosd.greenbuild.calwin.web.library.attributes;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.DataDropDownList;
import com.documentum.web.form.control.databound.DataProvider;
import com.documentum.web.util.DfcUtils;
import com.documentum.webcomponent.library.attributes.Attributes;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;

/**
 * 
 * ******************************************************************************************
 * File Name: AttributesCalwinCaseType.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class AttributesCalwinCaseType extends Attributes implements COSDCalwinConstants
{

	private Text gCaseNumberTextCtrl = (Text) getControl("casenumbertext", Text.class);
	private Text gProviderNumberTextCtrl = (Text) getControl("providernumbertext", Text.class);
	private DataDropDownList gCatDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
	private DataDropDownList gSubcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
	private DataDropDownList gDoctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
	//private DataDropDownList gDatamonthLstCtrl = (DataDropDownList) getControl("datamonthlst", DataDropDownList.class);
	private Text gDatamonthTextCtrl = (Text) getControl("datamonthtext", Text.class);
	private String objectId;
	private IDfSysObject sysObj;

	public void onInit(ArgumentList args) {
		super.onInit(args);
		objectId = args.get("objectId");
		try {
			sysObj = (IDfSysObject)getDfSession().getObject(new DfId(objectId));
		} catch (DfException e) {
			e.printStackTrace();
		}
		initControls(args);
	}
	
	private void initControls(ArgumentList args) {
		try {
			String strCategory = sysObj.getString("category");
			String strSubcategory = sysObj.getString("sub_category");
			String strDocType = sysObj.getString("doc_type");

			setCategoryDDDList(args);
			if ("".equals(strCategory) && strCategory.length()==0) {
				setSubcategoryDDDList(args);
				setDocumentTypesDDDList(args);
			} else if ("".equals(strSubcategory) && strSubcategory.length()==0) {
				initSubcatDoctypeDDDList(getCatSubcatDoctypeTextFromValueID(strCategory,true,false), strSubcategory);
			} else {
				initSubcatDoctypeDDDList(getCatSubcatDoctypeTextFromValueID(strCategory,true,false), getCatSubcatDoctypeTextFromValueID(strSubcategory,false,true));
			}
			//setDataMonthDDDList(args);
		
			gCaseNumberTextCtrl.setValue(sysObj.getString("case_no").toUpperCase()); //Change for ticket 100-02-12808502
			gCatDDDLstCtrl.setValue(getCatSubcatDoctypeTextFromValueID(strCategory,true,false));
			gSubcatDDDLstCtrl.setValue(getCatSubcatDoctypeTextFromValueID(strSubcategory,false,true));
			gDoctypeDDDLstCtrl.setValue(getCatSubcatDoctypeTextFromValueID(strDocType,false,false));
			gProviderNumberTextCtrl.setValue(sysObj.getString("provider_number"));
			//gDatamonthLstCtrl.setValue(sysObj.getString("data_month"));
			gDatamonthTextCtrl.setValue(sysObj.getString("data_month"));
		} catch (DfException e) {
			e.printStackTrace();
		}
	}
	
	private String getCatSubcatDoctypeTextFromValueID(String valId, Boolean isCategory, Boolean isSubcategory)
	{
		String dqlQueryString = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and value_id='" + valId + "'";
		String title = null;
		if (isCategory)
		{
			dqlQueryString = "select title from calwin_admin_case where is_category=1 and value_id='" + valId + "'";
		} 
		if (isSubcategory)
		{
			dqlQueryString = "select title from calwin_admin_case where is_subcategory=1 and value_id='" + valId + "'";
		}
		IDfCollection iCollection = null;
		IDfQuery query = DfcUtils.getClientX().getQuery();
		query.setDQL(dqlQueryString);
		try {
			iCollection = query.execute(getDfSession(), 0);
			for (; iCollection.next() == true;) {
				title = iCollection.getString("title");
			}
		} catch (DfException e) {
			throw new WrapperRuntimeException("Query Failed...", e);
		} finally {
			try {
				if (iCollection != null) {
			iCollection.close();
				}
			} catch (DfException e) {}
		}
		return title;
	}

	private void initSubcatDoctypeDDDList(String aStrCategory, String aStrSubcategory) {
		StringBuffer querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE + aStrCategory + "')");
		querySubCat.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		gSubcatDDDLstCtrl.getDataProvider().setDfSession(getDfSession());
		gSubcatDDDLstCtrl.getDataProvider().setQuery(querySubCat.toString());
		
		StringBuffer queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_1 + aStrCategory + "')");
		if (aStrSubcategory == null) { /* This is done to avoid NullPointerExceptions in cases where Adobe LiveCycle assigns null to aStrSubcategory */
			aStrSubcategory = "";
		}
		if (!("".equals(aStrSubcategory)) && (aStrSubcategory.length()!=0)) { // if subcategory field is not blank, ensure that it is taken into account when determining the resulting document_type
			queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_2 + aStrSubcategory + "')");
		}
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		gDoctypeDDDLstCtrl.getDataProvider().setDfSession(getDfSession());
		gDoctypeDDDLstCtrl.getDataProvider().setQuery(queryDoctype.toString());
	}

	public void setCategoryDDDList(ArgumentList args) {
		gCatDDDLstCtrl.getDataProvider().setDfSession(getDfSession());
		gCatDDDLstCtrl.getDataProvider().setQuery(COSDCalwinConstants.GET_DEFAULT_CATEGORY_QUERY);
	}

	public void setSubcategoryDDDList(ArgumentList args) {
		gSubcatDDDLstCtrl.getDataProvider().setDfSession(getDfSession());
		gSubcatDDDLstCtrl.getDataProvider().setQuery(COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY);
	}

	public void setDocumentTypesDDDList(ArgumentList args) {
		gDoctypeDDDLstCtrl.getDataProvider().setDfSession(getDfSession());
		gDoctypeDDDLstCtrl.getDataProvider().setQuery(COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY);
	}
	
/*	private void setDataMonthDDDList(ArgumentList args){
		gDatamonthLstCtrl.getDataProvider().setDfSession(getDfSession());
		gDatamonthLstCtrl.getDataProvider().setQuery(COSDCalwinConstants.GET_DATA_MONTH_QUERY);
	}*/
	
	public void onChangeCategoryDDDList(DataDropDownList catDDDLstCtrl, ArgumentList argumentList)
	{
		//DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		//DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		
		//System.out.println("catDDDLstCtrl.getValue(): " + catDDDLstCtrl.getValue());
		
		String catDDDLstVal = catDDDLstCtrl.getValue();
		String subcatDDDLstVal = gSubcatDDDLstCtrl.getValue();
		StringBuffer querySubCat;
		StringBuffer queryDoctype;
		DataProvider catDDDLstDP = catDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = gSubcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = gDoctypeDDDLstCtrl.getDataProvider();
		
		//querySubCat = new StringBuffer("select distinct cosd_subcategory from dm_dbo.cosd_adminscreen where cosd_subcategory!='null' and cosd_category='");
		querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE);
		querySubCat.append(catDDDLstVal + "')");
		querySubCat.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		subcatDDDLstDP.setDfSession(getDfSession());
		
		//queryDoctype = new StringBuffer("select distinct cosd_documenttype from dm_dbo.cosd_adminscreen where cosd_documenttype!='null' and cosd_category='");
		queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_1);
		queryDoctype.append(catDDDLstVal + "')");
/*		if ((subcatDDDLstVal != "") && (subcatDDDLstVal.length()!=0)) { // if subcategory field is not blank, ensure that it is taken into account when determining the resulting document_type
			queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_2);
			queryDoctype.append(subcatDDDLstVal + "')");
		}*/
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		doctypeDDDLstDP.setDfSession(getDfSession());
			
		if ((catDDDLstVal != "") && (catDDDLstVal.length()!=0)) {		// if category is not blank AND (subcategory is blank OR subcategory is not blank)
			//System.out.println("IF CLAUSE: querySubCat" + querySubCat);
			subcatDDDLstDP.setQuery(querySubCat.toString());  // refresh the subcategory field as it depends on category field
			//System.out.println("IF CLAUSE: queryDoctype" + queryDoctype);
			doctypeDDDLstDP.setQuery(queryDoctype.toString()); // refresh the doctype field as it depends on category field
		} else {   // if category is blank
			//System.out.println("ELSE CLAUSE: querySubCat" + CDCRConstants.GET_DEFAULT_SUBCATEGORY_QUERY);
			//System.out.println("ELSE CLAUSE: queryDocType" + CDCRConstants.GET_DEFAULT_DOCTYPE_QUERY);
			catDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_CATEGORY_QUERY); // refresh category field, as it has been made blank
			subcatDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY); // refresh subcategory field, as it is dependent on the category field and hence refreshed to default
			doctypeDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY); // refresh documenttype field, as it is dependent on the category field and hence refreshed to default
		}		
		
	}
	
	public void onChangeSubCategoryDDDList(DataDropDownList subcatDDDLstCtrl, ArgumentList argumentList)
	{
		//DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		//DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		
		//System.out.println("subcatDDDLstCtrl.getValue(): " + subcatDDDLstCtrl.getValue());
		
		String catDDDLstVal = gCatDDDLstCtrl.getValue();
		String subcatDDDLstVal = subcatDDDLstCtrl.getValue();
		StringBuffer queryCat;
		StringBuffer queryDoctype;
		DataProvider catDDDLstDP = gCatDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = gDoctypeDDDLstCtrl.getDataProvider();
		
		//queryCat = new StringBuffer("select distinct cosd_category from dm_dbo.cosd_adminscreen where cosd_category!='null' and cosd_subcategory='");
/*		queryCat = new StringBuffer(COSDCalwinConstants.GET_CATEGORY_QUERY_ON_SUBCATEGORY_CHANGE);
		queryCat.append(subcatDDDLstVal + "')");
		catDDDLstDP.setDfSession(getDfSession());*/
		
		//queryDoctype = new StringBuffer("select distinct cosd_documenttype from dm_dbo.cosd_adminscreen where cosd_documenttype!='null' and cosd_subcategory='");
		queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_1);
		queryDoctype.append(subcatDDDLstVal + "')");
		if ((catDDDLstVal != "") && (catDDDLstVal.length()!=0)) { // if category field is not blank, ensure that it is taken into account when determining the resulting document_type
			queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_2);
			queryDoctype.append(catDDDLstVal + "')");
		}
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_ORDER_BY);
		doctypeDDDLstDP.setDfSession(getDfSession());
			
		if ((subcatDDDLstVal != "") && (subcatDDDLstVal.length()!=0)) {	 // if subcategory is not blank AND (category is blank OR category is not blank)
			//System.out.println("IF CLAUSE: queryCat" + queryCat);
			//catDDDLstDP.setQuery(queryCat.toString());   // User does not care about what the category values are; since s/he has either already selected the category or is directly going to the subcategory field
			//System.out.println("IF CLAUSE: queryDoctype" + queryDoctype);
			doctypeDDDLstDP.setQuery(queryDoctype.toString());
		} else {  // if subcategory is blank
			//System.out.println("ELSE CLAUSE: queryCat" + CDCRConstants.GET_DEFAULT_CATEGORY_QUERY);
			//System.out.println("ELSE CLAUSE: queryDocType" + CDCRConstants.GET_DEFAULT_DOCTYPE_QUERY);
			//catDDDLstDP.setQuery(getCategoryQuery());   // User does not care about what the category values are; since s/he has either already selected the category or is directly going to the subcategory field
			if (("".equals(catDDDLstVal)) && (catDDDLstVal.length()==0)) { // if category is blank
				subcatDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY);  // refresh the subcategory field when set to blank; data from cosd_subcategory_order table
				doctypeDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY);  // refresh the doctype if the category field value is blank; data from cosd_documenttype_order table
			} else { // if category is not blank
				//queryDoctype = new StringBuffer("select distinct cosd_documenttype from dm_dbo.cosd_adminscreen where cosd_documenttype!='null' and cosd_category='");
				queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL);
				queryDoctype.append(catDDDLstVal + "')");
				queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL_ORDER_BY);
				doctypeDDDLstDP.setQuery(queryDoctype.toString());   // the doctype field values are based on the category field value; data from cosdadminscreen_order table
			}
		}
	}	

	// TODO: Confirm this method is not used and delete it.
	public void onChangeDocumentTypeDDDList(DataDropDownList doctypeDDDLstCtrl, ArgumentList argumentList)
	{
		//DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		//DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		
		//System.out.println("doctypeDDDLstCtrl.getValue(): " + doctypeDDDLstCtrl.getValue());
		//System.out.println("subcatDDDLstCtrl.getValue(): " + gSubcatDDDLstCtrl.getValue());
		//System.out.println("catDDDLstCtrl.getValue(): " + gCatDDDLstCtrl.getValue());
		
		String catDDDLstVal = gCatDDDLstCtrl.getValue();
		String subcatDDDLstVal = gSubcatDDDLstCtrl.getValue();
		String doctypeDDDLstVal = doctypeDDDLstCtrl.getValue();
		StringBuffer queryCat;
		StringBuffer querySubCat;
		StringBuffer queryDoctype;
		StringBuffer queryCatSubcat;
		DataProvider catDDDLstDP = gCatDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = gSubcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = doctypeDDDLstCtrl.getDataProvider();
		
		//queryCatSubcat = new StringBuffer("select distinct cosd_category, cosd_subcategory from dm_dbo.cosd_adminscreen where cosd_subcategory!='null' and cosd_documenttype='");
		//queryCatSubcat.append(doctypeDDDLstVal + "'");
		queryCat = new StringBuffer(COSDCalwinConstants.GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE);
		queryCat.append(doctypeDDDLstVal + "')");
		querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE);
		querySubCat.append(doctypeDDDLstVal + "')");
		catDDDLstDP.setDfSession(getDfSession());
		subcatDDDLstDP.setDfSession(getDfSession());
		
			
		 /*We only care about the following conditions: (A) Document Type is not blank AND category is blank AND subcategory is blank, (B) Document Type is blank AND category is blank AND subcategory is blank
		 * We are NOT concerned with any of the following conditions: (A) Category is NOT blank, (B) Subcategory is NOT blank, (C) Category AND subcategory are not blank. The reason being that is either category
		 * or subcategory are not blank, then it implies that the user has selected the same. 
		 */  
		if ((("".equals(catDDDLstVal)) && (catDDDLstVal.length()==0)) && ((subcatDDDLstVal == "") && (subcatDDDLstVal.length()==0))) { 
			if ((doctypeDDDLstVal != "") && (doctypeDDDLstVal.length()!=0)) {		// if document type is not blank AND subcategory is blank AND category is not blank
				//System.out.println("IF CLAUSE: queryCategorytype" + queryCat);
				catDDDLstDP.setQuery(queryCat.toString()); // refresh the category field as it depends on category field
				//System.out.println("IF CLAUSE: querySubCat" + querySubCat);
				subcatDDDLstDP.setQuery(querySubCat.toString());  // refresh the subcategory field as it depends on category field
			} else {   // if document type is blank AND subcategory is blank AND category is not blank
				//System.out.println("ELSE CLAUSE: querySubCat" + COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY);
				//System.out.println("ELSE CLAUSE: queryCat" + COSDCalwinConstants.GET_DEFAULT_CATEGORY_QUERY);
				//System.out.println("ELSE CLAUSE: queryDocType" + COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY);
				catDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_CATEGORY_QUERY); // refresh category field, as it has been made blank
				subcatDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY); // refresh subcategory field, as it is dependent on the category field and hence refreshed to default
				doctypeDDDLstDP.setQuery(COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY); // refresh documenttype field, as it is dependent on the category field and hence refreshed to default
			}
		}
		
	}
	
	public boolean onCommitChanges() {
		//super.onCommitChanges();
		Boolean rc = false;
		String subCatCtrlVal = gSubcatDDDLstCtrl.getValue();
		String catCtrlVal = gCatDDDLstCtrl.getValue();
		String doctypeCtrlVal = gDoctypeDDDLstCtrl.getValue();
        if(getIsValid())
        {
        	//DocbaseObject docbaseObj = (DocbaseObject)getControl("obj");
        	try {
/*                if(FolderUtil.isFolderType(docbaseObj.getObjectId()) && (docbaseObj.hasValueChanged("object_name") || docbaseObj.hasValueChanged("owner_name")))
                    FolderUtil.clearFolderCache();*/
        		//sysObj = (IDfSysObject)getDfSession().getObject(new DfId(objectId));
				sysObj.setString("case_no", gCaseNumberTextCtrl.getValue().toUpperCase());  //Change for ticket 100-02-12808502
				//sysObj.setString("data_month", gDatamonthLstCtrl.getValue());
				sysObj.setString("data_month", gDatamonthTextCtrl.getValue());
				sysObj.setString("provider_number", gProviderNumberTextCtrl.getValue());
/*        		sysObj.setString("category", gCatDDDLstCtrl.getValue());
				sysObj.setString("sub_category", gSubcatDDDLstCtrl.getValue());
				sysObj.setString("doc_type", gDoctypeDDDLstCtrl.getValue());*/
				if ("".equals(catCtrlVal) && catCtrlVal.isEmpty())
				{
					sysObj.setString("category", catCtrlVal);
				} else {
					sysObj.setString("category", getCatSubcatDoctypeValueID(catCtrlVal,true,false));
				}
        		if ("".equals(subCatCtrlVal) && subCatCtrlVal.isEmpty())
				{
					sysObj.setString("sub_category", subCatCtrlVal);
				} else {
					sysObj.setString("sub_category", getCatSubcatDoctypeValueID(subCatCtrlVal,false,true));
				}
				if ("".equals(doctypeCtrlVal) && doctypeCtrlVal.isEmpty())
				{
					sysObj.setString("doc_type", doctypeCtrlVal);
				} else {
					sysObj.setString("doc_type", getCatSubcatDoctypeValueID(doctypeCtrlVal,false,false));
				}
        		sysObj.save();
                //docbaseObj.save();
				rc = true;
			} catch (DfException e) {
				e.printStackTrace();
			}
        }
		
		return rc;
	}
	
	private String getCatSubcatDoctypeValueID(String title, Boolean category, Boolean subcategory){
		
		String dqlQueryString = COSDCalwinConstants.DOCTYPE_VALUE_ID_QUERY + title +"'";
		Integer valueId = null;
		if (category)
		{
			dqlQueryString = COSDCalwinConstants.CATEGORY_VALUE_ID_QUERY + title +"'";
		}
		if (subcategory)
		{
			dqlQueryString = COSDCalwinConstants.SUBCATEGORY_VALUE_ID_QUERY + title +"'";
		}
		IDfCollection iCollection = null;
		IDfQuery query = DfcUtils.getClientX().getQuery();
		query.setDQL(dqlQueryString);
		try {
			iCollection = query.execute(getDfSession(), 0);
			for (; iCollection.next() == true;) {
				valueId = iCollection.getInt("value_id");
			}
		} catch (DfException e) {
			throw new WrapperRuntimeException("Query Failed...", e);
		} finally {
			try {
				if (iCollection != null) {
			iCollection.close();
				}
			} catch (DfException e) {}
		}
		return valueId.toString();
	}
	
	
	private static final long serialVersionUID = 1L;

}