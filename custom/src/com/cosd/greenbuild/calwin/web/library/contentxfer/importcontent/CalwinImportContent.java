package com.cosd.greenbuild.calwin.web.library.contentxfer.importcontent;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import CalWINClient.CalWINClient;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.utils.CalwinWSConfig;
import com.cosd.greenbuild.calwin.web.library.changecasepersons.CalwinCasePerson;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.form.Form;
import com.documentum.web.form.control.DateTime;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.DataDropDownList;
import com.documentum.web.form.control.databound.DataProvider;
import com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContent;

public class CalwinImportContent extends ImportContent {
	
	private static final long serialVersionUID = 1L;
	
	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContent#onInit(com.documentum.web.common.ArgumentList)
	 */
	public void onInit(ArgumentList arg)
	  {
		  super.onInit(arg);
		  initDDDListControls(arg);
		  Label caseNumberErrLbl = (Label)getControl("casenumbererrorlabel", Label.class);
		  caseNumberErrLbl.setVisible(false);
		  Label datetimeErrLbl = (Label)getControl("datetimeerrorlabel", Label.class);
		  datetimeErrLbl.setVisible(false);
		  
		  /* Arun - BreakFix 100-02-12663697 */
		  Label docTypeErrLbl = (Label)getControl("doctypeerrorlabel", Label.class);
		  docTypeErrLbl.setVisible(false);
		  /* BreakFix 100-02-12663697 */
		  
		  DateTime dtValue =   (DateTime) getControl("datefrompicker", DateTime.class);
		  DfTime dfTime = new DfTime(new java.util.Date());
		  dtValue.setYear(dfTime.getYear());
		  dtValue.setMonth(dfTime.getMonth());
		  dtValue.setDay(dfTime.getDay());
		  dtValue.setHour(dfTime.getHour());
		  dtValue.setMinute(dfTime.getMinutes());
		  dtValue.setSecond(dfTime.getSeconds());
		  
		  strDoctypeDDDLstVal = null;
    }

	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContent#onRender()
	 */
	// AS: 06/24/2016
	public void onRender()
	{
		int iNextMaxCounter = CalwinUcfImportContainer.getNextMaxCounter(); // stores the highest number of Next button clicks before the Previous button is clicked 
		int iNextCounter = CalwinUcfImportContainer.getNextCounter(); // field that increments on clicking Next button and decrements on Previous button

		// when user hits Next button for indexing a new document, make sure the bMaintainState is set to true
		// bMaintainState can become false when user hits previous button but does not make any changes and then clicks on Next button
		// 
		if (iNextMaxCounter > iMaxCounterForMaintainState){
			setbMaintainState(true);
		}
		
		// if (iNextCounter == iNextMaxCounter) --> set to true when, for example, when user hits Previous after 3rd page and comes back to 3rd page.
		//    isbMaintainState() --> always true when doctype control value is changed; set to false when previous button is clicked
		//    isbDocTypeChanged() --> false when Next button is clicked first time, else true because doctype is always changed as it is a mandatory field
		if ((iNextCounter == iNextMaxCounter) && isbMaintainState() && isbDocTypeChanged()){
			iMaxCounterForMaintainState = iNextMaxCounter;
			setCatSubcatForDoctype(); // resets the category and subcategory value based on the chosen Doctype value
		} else {
			// When previous button is clicked OR when next button does not match the highest "Next Button" count, OR
			// when Next button is clicked first time
			setbMaintainState(false); // bMaintainState() is set to false
		}
		
		super.onRender();
		
	}
	
	/* 
	 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContent#onRenderEnd()
	 */
	// AS: 06/24/2016
	public void onRenderEnd()
	{
		super.onRenderEnd();

		// update states of category and subcategory control. 
		// this is to make sure that state saved in memory after explicit selection does not get propagated further... 
		// ...if, on the next screen, category and subcategory get populated implictly by choosing the doctype
		DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		catDDDLstCtrl.updateStateFromRequest();
		subcatDDDLstCtrl.updateStateFromRequest();
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
		 * @param catDDDLstCtrl
		 * @param argumentList
		 */
		public void onChangeCategoryDDDList(DataDropDownList catDDDLstCtrl, ArgumentList argumentList)
		{
			// AS: 06/24/2016
			setbDocTypeChanged(false); // so that the onRender() method does not call setCatSubcatForDoctype() method
			
			DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
			DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
			strDoctypeDDDLstVal = null;
			
			String catDDDLstVal = catDDDLstCtrl.getValue();
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
				subcatDDDLstCtrl.setValue("");
				doctypeDDDLstDP.setQuery(getDocTypeQuery()); // refresh documenttype field, as it is dependent on the category field and hence refreshed to default
				doctypeDDDLstCtrl.setValue("");
			}		
			
		}
		
		/**
		 * @param subcatDDDLstCtrl
		 * @param argumentList
		 */
		public void onChangeSubCategoryDDDList(DataDropDownList subcatDDDLstCtrl, ArgumentList argumentList)
		{
			// AS: 06/24/2016
			setbDocTypeChanged(false); // so that the onRender() method does not call setCatSubcatForDoctype() method
			
			DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
			DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
			
			String catDDDLstVal = catDDDLstCtrl.getValue();
			String subcatDDDLstVal = subcatDDDLstCtrl.getValue();
			StringBuffer queryDoctype;
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
		
		/**
		 * @param doctypeDDDLstCtrl
		 * @param argumentList
		 */
		public void onChangeDoctypeDDDList(DataDropDownList doctypeDDDLstCtrl, ArgumentList argumentList)
		{
			// AS: 06/24/2016
			setbDocTypeChanged(true); // set to true when the doctype control value is selected
			setbMaintainState(true); // set to true when the doctype control value is selected
			
			DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
			DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
			ArgumentList args = new ArgumentList();
			
			String doctypeDDDLstVal = doctypeDDDLstCtrl.getValue();
			strDoctypeDDDLstVal = doctypeDDDLstVal;
			StringBuffer queryCat;
			StringBuffer querySubCat,querySubCat1;
			DataProvider catDDDLstDP = catDDDLstCtrl.getDataProvider();
			DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
			
			// if doctype is blank
			if (("".equals(doctypeDDDLstVal)) && (doctypeDDDLstVal.length()==0)) 
			{
				subcatDDDLstDP.setDfSession(getDfSession());
				subcatDDDLstDP.setQuery(getSubCatQuery());  // refresh the subcategory field when set to blank
				catDDDLstDP.setDfSession(getDfSession());
				catDDDLstDP.setQuery(getCategoryQuery());  // refresh the doctype if the category field value is blank
				catDDDLstCtrl.setValue(getCatSubcatQueryExec(getCategoryQuery(),getDfSession()));
				subcatDDDLstCtrl.setValue(getCatSubcatQueryExec(getSubCatQuery(),getDfSession()));
			} else 
			{
				queryCat = new StringBuffer(COSDCalwinConstants.GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE);
				queryCat.append(doctypeDDDLstVal + "')");
				queryCat.append(COSDCalwinConstants.GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE_ORDER_BY);
				setCategoryDDDList(args);
				catDDDLstCtrl.setValue(getCatSubcatQueryExec(queryCat.toString(),getDfSession()));
				
				querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE);
				querySubCat.append(doctypeDDDLstVal + "')");
				querySubCat.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE_ORDER_BY);
				querySubCat1 = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE);
				querySubCat1.append(catDDDLstCtrl.getValue() + "')");
				querySubCat1.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
				subcatDDDLstDP.setDfSession(getDfSession());
				subcatDDDLstDP.setQuery(querySubCat1.toString());
				subcatDDDLstCtrl.setValue(getCatSubcatQueryExec(querySubCat.toString(),getDfSession()));
			}
		}
		
		  /**
		 * resets the category and subcategory value based on the chosen Doctype value
		 */
		private void setCatSubcatForDoctype() {
			  
			// AS: 06/24/2016
			  setbDocTypeChanged(true); // so that the onRender() method does call setCatSubcatForDoctype() method the next time around
			  
				DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
				DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
				ArgumentList args = new ArgumentList();
				
				if (catDDDLstCtrl != null) {
					String doctypeDDDLstVal = strDoctypeDDDLstVal;
					StringBuffer queryCat;
					StringBuffer querySubCat,querySubCat1;
					DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
					
					// if doctype is NOT blank
					if (doctypeDDDLstVal != null) {
						queryCat = new StringBuffer(COSDCalwinConstants.GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE);
						queryCat.append(doctypeDDDLstVal + "')");
						queryCat.append(COSDCalwinConstants.GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE_ORDER_BY);
						setCategoryDDDList(args);
						catDDDLstCtrl.setValue(getCatSubcatQueryExec(queryCat.toString(),getDfSession()));

						querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE);
						querySubCat.append(doctypeDDDLstVal + "')");
						querySubCat.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE_ORDER_BY);
						querySubCat1 = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE);
						querySubCat1.append(catDDDLstCtrl.getValue() + "')");
						querySubCat1.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
						subcatDDDLstDP.setDfSession(getDfSession());
						subcatDDDLstDP.setQuery(querySubCat1.toString());
						subcatDDDLstCtrl.setValue(getCatSubcatQueryExec(querySubCat.toString(),getDfSession()));
					}
				}
		}
		
		/**
		 * @param dqlQueryString
		 * @param sess
		 * @return
		 */
		private String getCatSubcatQueryExec(String dqlQueryString, IDfSession sess){
			String value = "";
			IDfCollection iCollection = null;
			IDfQuery query = new DfQuery();
			query.setDQL(dqlQueryString);
			try {
				iCollection = query.execute(sess, 0);
				for (; iCollection.next() == true;) {
					value = iCollection.getString("title");
				}
			} catch (DfException e) {

			} finally {
				try {
					if (iCollection != null) {
				iCollection.close();
					}
				} catch (DfException e) {}
			}
			return value;
		}
		
		private boolean isCaseInfoPresent() {

			String result = null;
			Boolean bPresent = true;
			Text caseNoCtrl = (Text) getControl("casenumbertext", Text.class);
			CalwinWSConfig calWSConfig = new CalwinWSConfig();
			CalWINClient calWSClient = calWSConfig.CalwinWSInfo();
			result = calWSClient.getExtendedCaseInfoFromCaseId(caseNoCtrl.getValue());
			List<CalwinCasePerson> c = getInfoFromXML(result);
			if (c.size()>0)
				bPresent=false;
			
			return bPresent;
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


		/* 
		 * Extended method from com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContent#onCommitChanges()
		 */
		public boolean onCommitChanges()
		{
			super.onCommitChanges();
			
			// AS: 06/24/2016
			// re-initialize the static fields
			setbMaintainState(true);
			iMaxCounterForMaintainState = 0;
			setbDocTypeChanged(false);
			
			Text caseNoCtrl = (Text) getControl("casenumbertext", Text.class);
			DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
			DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
			DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
			DateTime recvdDateCtrl =   (DateTime) getControl("datefrompicker", DateTime.class);
			
			String caseNo = caseNoCtrl.getValue().toUpperCase();
			String category = catDDDLstCtrl.getValue();
			String subcategory = subcatDDDLstCtrl.getValue();
			String documentType = doctypeDDDLstCtrl.getValue();
			String receivedDate = recvdDateCtrl.getValue();
			
		    ArgumentList eventArgs = new ArgumentList();
		    eventArgs.add("calwincaseno", caseNo);
		    eventArgs.add("calwincategory", category);
		    eventArgs.add("calwinsubcategory", subcategory);
		    eventArgs.add("calwindoctype", documentType);
		    eventArgs.add("receivedDate", receivedDate);
		    Form form = getForm();
		    form.fireEvent("onsetcatsubcatdoctype", eventArgs);
			
			return true;
		}
		
		// AS: 06/24/2016
		public static boolean isbMaintainState() {
			return bMaintainState;
		}

		/**
		 * @param bMaintainState
		 */
		// AS: 06/24/2016
		public static void setbMaintainState(boolean bMaintainState) {
			CalwinImportContent.bMaintainState = bMaintainState;
		}
		
		// AS: 06/24/2016
		public static int getiMaxCounterForMaintainState() {
			return iMaxCounterForMaintainState;
		}

		/**
		 * @param iMaxCounterForMaintainState
		 */
		// AS: 06/24/2016
		public static void setiMaxCounterForMaintainState(int iMaxCounterForMaintainState) {
			CalwinImportContent.iMaxCounterForMaintainState = iMaxCounterForMaintainState;
		}

		// AS: 06/24/2016
		public static boolean isbDocTypeChanged() {
			return bDocTypeChanged;
		}

		/**
		 * @param bDocTypeChanged
		 */
		// AS: 06/24/2016
		public static void setbDocTypeChanged(boolean bDocTypeChanged) {
			CalwinImportContent.bDocTypeChanged = bDocTypeChanged;
		}


		private static String strDoctypeDDDLstVal;

		/*		Bean to control the manual maintenance of the state of category and subcategory controls whenever the doctype control value gets set before category and subcategory. 
		 *      This bean is needed because without explicit selection of values for the category and subcategory controls, OOTB ComboContainer value propagation does not work.
		 *      Doctype control always gets explicitly selected as it is a mandatory field.
		 *      NOTE - if values are selected from the drop down list for each control explicitly, then OOTB value propagation works*/
		private static boolean bMaintainState = true; // AS: 06/24/2016
		
		// this field is needed to store value of CalwinUcfImportContainer.getNextMaxCounter() for scenarios where user hits previous button but does not make any changes and then clicks on Next
		// see onRender() method for logic
		private static int iMaxCounterForMaintainState = 0; // AS: 06/24/2016
		
		//field to check whether doctype control value has been changed
		private static boolean bDocTypeChanged = false; // AS: 06/24/2016

}