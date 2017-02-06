package com.cosd.greenbuild.calwin.web.library.changecasepersons;


import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import CalWINClient.CalWINClient;

import com.cosd.greenbuild.calwin.utils.CalwinWSConfig;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.nls.NlsResourceBundle;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.LocaleService;
import com.documentum.web.formext.component.Component;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Button;
import com.documentum.web.form.control.DropDownList;
import com.documentum.web.form.control.Hidden;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Option;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.DFCQueryDataHandler;
import com.documentum.web.form.control.databound.Datagrid;
import com.documentum.web.form.control.databound.TableResultSet;

/**
 * 
 * ******************************************************************************************
 * File Name: ChangeCasePersonsDocComponent.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class ChangeCasePersonsDocComponent extends Component
{

	
	public void onInit(ArgumentList argList)
	{
		DfLogger.debug(this,"In ChangeCasePersonsDocComponent onInit",null,null);
		super.onInit(argList);
		errMsg = this.getNlsClass().getString("MSG_ERROR_FLAG", LocaleService.getLocale());
		vals = argList.getValues("objectId");
		for (String item:vals){
			try {
				sysObj = (IDfSysObject) getDfSession().getObject(new DfId(item));
			} catch (DfException e) {
				e.printStackTrace();
			}
		}
		
		populateGrid();
	}
	
	private ResultSet getResultSetFromExternalTable() {
		DataSource dataSource = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String SQL = "select FIRSTNAME,MI,LASTNAME,NM_SUFX,DOB,SSN,CIN,CWIN from MVGET_CS_FAM where cs_id='";
		try {
			/*Context context = (Context) new InitialContext().lookup("java:/comp/env");*/ 	//For Tomcat
			Context context = (Context) new InitialContext();						 		//For WebLogic
			dataSource = (DataSource) context.lookup("jdbc/CalWINExtDS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
    	String strVal="";
		try {
			strVal = sysObj.getString("case_no");
		} catch (DfException e1) {
			e1.printStackTrace();
		}
		
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(SQL+strVal+"'");
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resultSet;
	}

	public void populateGrid()
	{
		bDataFromRegTable = true;
		try
		{
			datagrid = (Datagrid) getControl("docgrid",Datagrid.class);
			addObjIdsCtrl = (Hidden) getControl("addObjIds",Hidden.class);
		}
		catch(Exception ex)
		{
			if(datagrid == null)
			{
				datagrid = (Datagrid)createControl("docgrid",Datagrid.class);
			}
		}
		
		datagrid = (Datagrid) getControl(CONTROL_GRID,Datagrid.class);
			String result = null;
			CalwinWSConfig calWSConfig = new CalwinWSConfig();
			CalWINClient calWSClient = calWSConfig.CalwinWSInfo();
/*	    	CalWINClient cl = new CalWINClient("ustlscosd300",
		            "1526",
		            "clwncisp2",
		            "calwin_cis_iface",
		            "imed_abc_48785",
		            "usfsin01.folsom.calwin.eds.com",
		            "7232",
		            "sdg01",
		            "erms1234",
		            "1234567890AB",
		            "3C",
		            "37");*/
			String strName = "";
			String strDOB = "";
			String strSSN = "";
			String strCIN = "";
			String strCWIN = "";	    	
			String columnNames[] = {"name","dob", "ssn", "cin", "cwin"}; 
			TableResultSet rs = new TableResultSet(columnNames);				
			
	    	try {
				result = calWSClient.getExtendedCaseInfoFromCaseId(sysObj.getString("case_no"));
				List<CalwinCasePerson> c = getInfoFromXML(result);
				
				for (int i = 0; i < c.size(); i++) {

					CalwinCasePerson cAL = c.get(i);
					
					//String applicant_fname = "Carver-Sobhapratuvishna"; // <return values from the webservice call>
					String applicant_fname = cAL.getFirstname();
					//String applicant_mname = "M"; //<return values from the webservice call>
					String applicant_mname = cAL.getMiddlename();
					//String applicant_lname = "Mahmerugarawam"; //<return values from the webservice call>
					String applicant_lname = cAL.getLastname();
					//String applicant_suffix = "Jr"; //<return values from the webservice call>
					String applicant_suffix = cAL.getSuffix();
					strName = applicant_lname + " " + applicant_suffix + ", " + applicant_fname + " " + applicant_mname;
					
					//strDOB = "04/06/1984"; //<return values from the webservice call>
					strDOB = cAL.getDob();
					//strSSN = "827467465"; // format SSN; <return values from the webservice call>
					strSSN = cAL.getSsn();
					//strCIN = "IRYNBFYHT"; //<return values from the webservice call>
					strCIN = cAL.getCin();
					//strCWIN = "324657887"; //<return values from the webservice call>
					strCWIN = cAL.getCwin();
					String rowNames[] = {strName, strDOB, strSSN, strCIN, strCWIN};
					rs.add(rowNames);					
		
					datagrid.getDataProvider().setScrollableResultSet(rs);

				}
				rs.close();
			} catch (DfException e) {
				e.printStackTrace();
			}	
	}

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
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
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
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
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

    public boolean onCommitChanges()
    {
    	DfLogger.debug(this,"ChangeCasePersonsDocComponent onCommitChanges()-----start",null,null);    	
    	
    	Boolean bEnable = false;
    	
		try
		{
/*			String ssnTextVal = ssnText.getValue();
	        String dobTextVal = dobText.getValue();
	        String cinTextVal = cinText.getValue();
	        String cwinTextVal = cwinText.getValue();*/
			
			String[] strLastNameValTemp;
			String strLastNameVal="";
			String strSuffix="";
			String strFirstMiddleNameVal="";
			String strMiddleNameVal="";
			String strFirstNameVal="";
			String strDOBVal="";
			String strSSNVal="";
			String strCINVal="";
			String strCWINVal="";
			int intCounter=0;
			String strAddObjIds = addObjIdsCtrl.getValue();
			//ChangeCasePersonsContainer changeCasePersonsContainer = new ChangeCasePersonsContainer();
			Boolean bDeselect = ChangeCasePersonsContainer.getStrDeSelect();
			if (!("".equals(strAddObjIds)) && !(strAddObjIds.length()==0) && !bDeselect) {
				String[] strArrAddObjIds = strAddObjIds.split(";");
				String strNameVal = strArrAddObjIds[0];
				strDOBVal = strArrAddObjIds[1];
				strSSNVal = strArrAddObjIds[2];
				strCINVal = strArrAddObjIds[3];
				strCWINVal = strArrAddObjIds[4];
				intCounter = Integer.parseInt(strArrAddObjIds[5]);
				
				String[] strArrNameVal = strNameVal.split(",");
				strLastNameValTemp = strArrNameVal[0].split("\\s+");
				strLastNameVal = strLastNameValTemp[0];
				if (strLastNameValTemp.length>1)
					strSuffix = strLastNameValTemp[1];
				strFirstMiddleNameVal = strArrNameVal[1];
				if (strFirstMiddleNameVal.substring(strFirstMiddleNameVal.length()-3, strFirstMiddleNameVal.length()-2) == " ") {
					strMiddleNameVal = strFirstMiddleNameVal.substring(strFirstMiddleNameVal.length()-2);
					strFirstNameVal = strFirstMiddleNameVal.substring(0, strFirstMiddleNameVal.length()-3);
				} else {
					strFirstNameVal = strFirstMiddleNameVal;
				}
			}
			
			if (intCounter==1 || bDeselect) {
				bEnable=true;
			
				sysObj.setString("applicant_lname", strLastNameVal);
				sysObj.setString("applicant_suffix", strSuffix);
				sysObj.setString("applicant_mname", strMiddleNameVal);
				sysObj.setString("applicant_fname", strFirstNameVal);				
				sysObj.setString("ssn", strSSNVal);
				sysObj.setString("dob", strDOBVal);
				sysObj.setString("cin_no", strCINVal);
				sysObj.setString("cwin_no", strCWINVal);
				sysObj.save();
			} else {
				setErrorFlag(true);
			}
			ChangeCasePersonsContainer.setStrDeSelect(false);
		}
		catch(DfException dfe)
		{
			DfLogger.error(this, "DfException raised in onCommitChanges of ChangeCasePersonsDocComponent: "+dfe.getMessage(), null, null);
		}
		return bEnable;
        	
    }
    
    public Boolean getErrorFlag() {
    	return bErrorFlag;
    }
    
    public void setErrorFlag(Boolean bFlag) {
    	bErrorFlag = bFlag;
    }
	
	private static final long serialVersionUID = 1L;
	private String vals[];
	private IDfSysObject sysObj;
	Datagrid datagrid = null;
	Hidden addObjIdsCtrl = null;
	public static final String POPULATE_DATAGRID_DQL = "select applicant_fname,applicant_mname,applicant_lname,applicant_suffix,dob,ssn,cin,cwin from dm_dbo.hhsa_calwin_caseperson_test2 where case_number='";
	public static final String CONTROL_GRID = "docgrid";
	public static Boolean bDataFromRegTable;
	private Boolean bErrorFlag = false;
	public String errMsg;
	
}
