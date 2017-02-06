package com.cosd.greenbuild.calwin.web.library.contentxfer.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.SessionState;
import com.documentum.web.formext.session.SessionManagerHttpBinding;

/**
 * 
 * ******************************************************************************************
 * File Name: ExportNoPolicyDownloadContentStream.java 
 * Description: This class is application server side implemention to 
 *                  download a document without LiveCycle policy attached.
 * Author: 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class ExportNoPolicyDownloadContentStream extends HttpServlet{
	
	
	public ExportNoPolicyDownloadContentStream()
    {
    }

	/**
	 * handle with http get method
	 * @param req request
	 * @param res response
	 * 
	 */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
    	DfLogger.debug(this, "Enter doGet", null, null);
    	getContent(req, res);
    	DfLogger.debug(this, "Exit doGet", null, null);
    }

    /**
	 * handle with http post method
	 * @param req request
	 * @param res response
	 * 
	 */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
    	DfLogger.debug(this, "EnterMSWordDownloadContentStream", null, null);
        getContent(req, res);
        DfLogger.debug(this, "ExitMSWordDownloadContentStream", null, null);
    }

    /**
     * get document content to be download
     * @param request
     * @param response
     */
    private void getContent(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IllegalArgumentException
    {
    	DfLogger.debug(this, "Enter getContent", null, null);
    	
        String strObjectId;
        String strFormat;
        String strPageNumber;
        String strPageModifier;
        String strResultMSG;
        IDfSessionManager sessionManager;
        IDfSession dfSession = null;
        strObjectId = request.getParameter("objectId");
        String contentType = null;;
        ExportNoPolicyDocumentStreamingContent docContent = null;
        File file = null;
        IDfFormat format = null;
        String strMimeType = null;
        
        if(strObjectId == null || strObjectId.length() == 0) {
        	throw new IllegalArgumentException("Parameter objectId is mandatory");
        }
            
        //strFormat = (String)SessionState.getAttribute("FORMAT");
        strFormat = request.getParameter("format");
        strFormat = "pdf";
        strPageNumber = request.getParameter("pageNumber");
        strPageModifier = request.getParameter("pageModifier");
        //strResultMSG = request.getParameter("resultMSG");
                
        sessionManager = SessionManagerHttpBinding.getSessionManager();
        try
        {
            dfSession = sessionManager.getSession(SessionManagerHttpBinding.getCurrentDocbase());
            
    		// trigger export
    		MashupManager mgr = MashupManager.getInstance(dfSession);
    		String strXmlResponse = mgr.export(new DfId(strObjectId), dfSession);
    		System.out.println("Output from LiveCycle Export Service ::: " + strXmlResponse);
    		int a = strXmlResponse.indexOf("http");
    		//int b = strXmlResponse.indexOf("</outDocPDF>");
    		int b = strXmlResponse.indexOf("</docRMPDF>");
    		strResultMSG = strXmlResponse.substring(a, b);
            
            
            IDfSysObject sysObject = (IDfSysObject)dfSession.getObject(new DfId(strObjectId));
            if(sysObject == null) {
            	 throw new IllegalArgumentException("Object " + strObjectId + " does not exist");
            } 
            contentType = sysObject.getContentType();
            
            docContent = new ExportNoPolicyDocumentStreamingContent(sysObject);
            // get document content to be download
            //file = docContent.getFilePath(strFormat, strPageNumber, strPageModifier);
            file = docContent.getFilePath(strFormat, strPageNumber, strResultMSG);
            DfLogger.debug(this, "Absolute path of file: " + file.getAbsolutePath(), null, null);
            DfLogger.debug(this, "Size of the file: " + file.length(), null, null);
            
            System.out.println("Absolute path of file: " + file.getAbsolutePath());
            System.out.println("Size of the file: " + file.length());
            
            
            if (file != null) {
				if (strFormat != null && strFormat.length() > 0) {
					format = dfSession.getFormat(strFormat);
				} else {
					format = sysObject.getFormat();
				}
				strMimeType = format.getMIMEType();
				if (strMimeType == null || strMimeType.length() == 0) {
					strMimeType = "application/octet-stream";
				}
				docContent.downloadFile(response, file, contentType, strMimeType);
			}
        } catch (DfException err) {
			DfLogger.error(this, err.getMessage(), null, err);
		} catch (IOException err) {
			DfLogger.error(this, err.getMessage(), null, err);
		} finally {
			try {
				if (dfSession != null) {
					sessionManager.release(dfSession);
				}
			} catch (Exception exception) {
				DfLogger.debug(this, "Failed to close IDfSession object: " + exception.getMessage(), null, exception);
			}
			try {
				if (file != null) {
					file.delete();
				}
			} catch (Exception err) {
				DfLogger.error(this, err.getMessage(), null, err);
			}
		}
        DfLogger.debug(this, "Exit getContent", null, null);
    }
}
