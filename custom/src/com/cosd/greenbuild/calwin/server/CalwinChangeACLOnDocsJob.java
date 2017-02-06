package com.cosd.greenbuild.calwin.server;
/**
 *******************************************************************************************
 *
 * Project	  	  CalWIN        
 * Description    CERMS
 * Created by     Arun Shankar
 * Created on     May 24, 2013
 ********************************************************************************************
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.*;
import javax.mail.internet.*;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

public class CalwinChangeACLOnDocsJob extends CalwinServerMethodJMS
{
	IDfSession m_dfSession = null;
	private static final String ELIGIBILITY_ACL_NAME = "calwin_eligibility_acl";
	private static final String ADOPTION_ACL_NAME = "calwin_adoption_acl";
	private static final String FC_KG_ACL_NAME = "calwin_fc_kg_acl";
 	private static final String ELIGIBILITY_CONF_ACL_NAME = "calwin_eligibility_conf_acl";
	private static final String ADOPTION_CONF_ACL_NAME = "calwin_adoption_conf_acl";
	private static final String FC_KG_CONF_ACL_NAME = "calwin_fc_kg_conf_acl";
	protected String m_docbaseName = null;
	protected String m_userName = null;
	protected String m_loginTicket = null;
	protected String m_domain = null;
	 
	IDfSessionManager sessMgr = null;
	 
	private String sCSID = "";
	private String sKinGAP = "";
	private String sFosterCare = "";
	private String sAdoptAsst = "";
	private String sConfidentiality = "";
	private String sAClName = "";
	private String sProgramInfo = "";
	private String idsToEmail = "";
	
	private String m_DBConnection;
	private String m_DBUser;
	private String m_DBPassword;
	 
	 // Default parameters passed by invocation of job
	 private static final String USER_K = "user_name";
	 private static final String DOCBASE_K = "docbase_name";
	 private static final String PASSWORD_K = "password";
	 private static final String DOMAIN_K = "domain";
	 
	 public int execute(Map params, PrintWriter arg1) throws Exception {
		  
		  initParams(params);
		  sessMgr = login();		  
		  
		  // Implement your business logic here..
		  if (sessMgr != null)
		  {
/*			  m_dfSession = sessMgr.newSession(DOCBASE_K);*/
			  m_dfSession = sessMgr.newSession(m_docbaseName);
			  executeJobs();
		  }
		  //........
		  return 0;
		 }
	 
	 protected void initParams(Map params) throws Exception
	 {
	  Set keys = params.keySet();
	  Iterator iter = keys.iterator();
	  while (iter.hasNext())
	  {
	   String key = (String) iter.next();

	   if( (key == null) || (key.length() == 0) )
	   {
	    continue;
	   }
	   String []value = (String[])params.get(key);

	   if ( key.equalsIgnoreCase(USER_K) ) {
		   m_userName = (value.length > 0) ? value[0] : "";
	   }
	    if ( key.equalsIgnoreCase(DOCBASE_K) ) {
	    	m_docbaseName = (value.length > 0) ? value[0] : "";
	    }
	     if ( key.equalsIgnoreCase(PASSWORD_K) ) {
	    	 m_loginTicket = (value.length > 0) ? value[0] : "";
	     }
	      if ( key.equalsIgnoreCase(DOMAIN_K) ) {
	    	  m_domain = (value.length > 0) ? value[0] : "";
	      }
	    }
	 }

	protected void executeJobs() throws DfException // IDfSession session
	{
	      getCaseNumberInfoFromCalwinDB();
	}
	
	private void getCaseNumberInfoFromCalwinDB(){
		
	    try {
	        Class.forName("oracle.jdbc.driver.OracleDriver");
	      } catch (ClassNotFoundException e) {
	        //System.out.println("error");
	      }
	      Connection connection = null;
	      try {

	        connection = DriverManager.getConnection(m_DBConnection, m_DBUser, m_DBPassword);
	      } catch (SQLException e) {
	        //System.out.println(e.getMessage());
	      }
	      ResultSet rs = null;
	      Statement stmt = null;
	      if (connection != null) {
	        try {
	          stmt = connection.createStatement();
	          // CaseId = 0408357
/*	          rs = stmt.executeQuery("select * from MVGET_CS_ATTRIB");*/
	          rs = stmt.executeQuery("select * from MVGET_CS_ATTRIB where cs_id in ('1B00Z74','0630363','1B00Z70 ')");
	          
	          while (rs.next()) 
	          {
	        	sCSID = rs.getString("CS_ID");
	            sKinGAP = rs.getString("KINGAP");
	            sFosterCare = rs.getString("FOSTERCARE");
	            sAdoptAsst = rs.getString("ADOPTASST");
	            sConfidentiality = rs.getString("CONF");
	        	if (sConfidentiality.equalsIgnoreCase("Y"))
	        	{
	        		sAClName = "calwin_eligibility_conf_acl";
	        	} else
	        	{
	        		sAClName = "calwin_eligibility_acl";
	        	}
		        if (sKinGAP.equalsIgnoreCase("Y")) {
		        	sProgramInfo = "KG";
		        	if (sConfidentiality.equalsIgnoreCase("Y"))
		        	{
		        		sAClName = "calwin_fc_kg_conf_acl";
		        	} else{
		        		sAClName = "calwin_fc_kg_acl";
		        	}
		        }
		        if (sFosterCare.equalsIgnoreCase("Y")) {
		        	sProgramInfo = "FC";
		        	if (sConfidentiality.equalsIgnoreCase("Y"))
		        	{
		        		sAClName = "calwin_fc_kg_conf_acl";
		        	} else
		        	{
		        		sAClName = "calwin_fc_kg_acl";
		        	}
		        }
		        if (sAdoptAsst.equalsIgnoreCase("Y")) {
		        	sProgramInfo = "AA";
		        	if (sConfidentiality.equalsIgnoreCase("Y"))
		        	{
		        		sAClName = "calwin_adoption_conf_acl";
		        	} else
		        	{
		        		sAClName = "calwin_adoption_acl";
		        	}
		        }
		        boolean sFlag = compareCaseNumberWithDCTM();
		        if (sFlag)
		        {
		        	//updateCaseNumberInfoInDCTM();
		        	idsToEmail = idsToEmail + sCSID;
		        }
		        
	          }
	        	emailUpdatedCaseNumberInfoInDCTM();
	   
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	        //System.out.println("getCaseNumberInfoFromCalwinDB done");
	      }
		
	}
	
	private boolean compareCaseNumberWithDCTM()
	{
		boolean bReturn = false;
		
		try {
			IDfSysObject obj = (IDfSysObject) m_dfSession.getObjectByQualification("calwin_case_doc where case_no='" + sCSID + "'");
			if (obj != null)
				bReturn = true;
		} catch (DfException e) {
			e.printStackTrace();
		}
		
		return bReturn;
	}
	
	private void emailUpdatedCaseNumberInfoInDCTM()
	{
		Properties props = new Properties();
        //set the smtp host in the properties
	props.put("mail.smtp.host", "mail.sdcounty.ca.gov");
  //	props.put("mail.debug", "true");
	Session session = Session.getDefaultInstance(props, null);
  //	session.setDebug(true);
	    // create a message
	    Message msg = new MimeMessage(session);
            //set the from address
		String sUserName;
		try {
			sUserName = m_dfSession.getLoginUserName();
			String m_sFromAddress = getEmailAddress(sUserName);
			msg.setFrom(new InternetAddress(m_sFromAddress));
		} catch (DfException e) {
			e.printStackTrace();
		}
        //added to filter out the exclusion list
		catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}


/*	    InternetAddress[] address = new InternetAddress[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
              address[i] = new InternetAddress();
              address[i].setAddress(((UserDetail)userList.get(i)).getUserEmailAddress());
              address[i].setPersonal(((UserDetail)userList.get(i)).getUserName());
        }
	    msg.setRecipients(Message.RecipientType.TO, address);*/

	    String m_sToAddress = "cosd_documentsolutionsteam@hp.com";
	    try {
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_sToAddress));

		    msg.setSubject("CalWIN invalid case numbers");
		    //msg.setSentDate(new Date());
	      // Associate html body to the message
	      msg.setContent("The following are the invalid case numbers: " + idsToEmail.trim(), "text/html");
	      //set the priority and other properties of the mail message
	      //msg.addHeader("X-Priority", priority);
	      msg.addHeader("Content-Transfer-Encoding","7bit");
	      msg.addHeader("charset","iso-8859-1");
	      //send the message
		    Transport.send(msg);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getEmailAddress(String sUserName) throws DfException
	{
		String sReurnValue = null;
		IDfQuery query = new DfQuery();
		query.setDQL("select user_address from dm_user where user_name='"+sUserName+"'");
		IDfCollection coll = query.execute(m_dfSession,DfQuery.DF_READ_QUERY);
		while(coll.next())
		{
			sReurnValue=coll.getString("user_address");
		}
		coll.close();
		
		return sReurnValue;
	}
	
	
	 protected IDfSessionManager login() throws DfException
	 {
	  if (m_docbaseName == null || m_userName == null  )
	   return null;

	  // now login
	  IDfClient dfClient = DfClient.getLocalClient();

	  if (dfClient != null)
	  {
	   IDfLoginInfo li = new DfLoginInfo();
	   li.setUser(m_userName);
	   li.setPassword(m_loginTicket);
	   if(m_domain!=null)
	    li.setDomain(m_domain);

	   IDfSessionManager sessionMgr = dfClient.newSessionManager();
	   sessionMgr.setIdentity(m_docbaseName, li);
	   sessionMgr.authenticate(m_docbaseName);
	   return sessionMgr;
	  }
	  return null;
	 }
	 
		protected void logout(IDfSession session) throws DfException
		{
			if (sessMgr != null && session != null)
			{
				sessMgr.release(session);
				sessMgr.clearIdentities();
				sessMgr = null;
			}
		}
	
}