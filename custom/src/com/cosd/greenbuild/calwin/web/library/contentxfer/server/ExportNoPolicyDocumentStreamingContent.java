package com.cosd.greenbuild.calwin.web.library.contentxfer.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.SessionState;


/**
 * 
 * ******************************************************************************************
 * File Name: ExportNoPolicyDocumentStreamingContent.java 
 * Description: download team copy or clean copy to user's machine
 * Author 					Arun Shankar - HP.
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class ExportNoPolicyDocumentStreamingContent {

	public IDfSysObject m_sysObject = null;

	public ExportNoPolicyDocumentStreamingContent(IDfSysObject sysObject) {
		m_sysObject = sysObject;
	}

	/**
	 * get file new name
	 * @param fileContent original file
	 * @return
	 */
	private String getRenamedFileName(String fileName, String contentType) {
		DfLogger.debug(this, "Enter getRenamedFileName", null, null);
		DfLogger.debug(this, "Before rename: " + fileName, null, null);
		if (fileName == null || fileName.length() == 0) {
			return null;
		}
		String newName = null;
		String postfix = null;

		String copyPostfix = null;
		try
		{
			postfix = getMSWordPostfix(contentType);
			copyPostfix = (String)SessionState.getAttribute("MSWORD_DOWNLOAD_POSTFIX");
			int lastDotIndex = fileName.lastIndexOf(".");
			if (lastDotIndex != -1 && (".doc".equalsIgnoreCase(fileName.substring(lastDotIndex)) || ".docx".equalsIgnoreCase(fileName.substring(lastDotIndex)) || ".rtf".equalsIgnoreCase(fileName.substring(lastDotIndex)))) {
				newName = fileName.substring(0, lastDotIndex) + copyPostfix + fileName.substring(lastDotIndex);
			} else {
				newName = fileName + copyPostfix + "." + postfix;
			}
		}
		catch(Exception ee)
		{
			DfLogger.debug(this, "Exception inside  getRenamedFileName: " + ee.getMessage(), null, null);
		}
		DfLogger.debug(this, "After rename: " + fileName, null, null);
		DfLogger.debug(this, "Exit getRenamedFileName", null, null);
		return newName;
	}

	/**
	 * 
	 * @param format content type of document
	 * @return MS word postfix, e.g. doc, docx, if null, format is not a ms word format
	 */
	public String getMSWordPostfix(String format) {
		DfLogger.debug(this, "Enter getMSWordPostfix", null, null);

		String postfix = null;
		if (format == null || "".equals(format.trim())) {
			DfLogger.debug(this, "Exit getMSWordPostfix", null, null);
			return null;
		}
		String[] msWordFormats = { "mswm1", "msww", "msw3", "msw", "mswm", "msw6", "msw6template", "msw12", "msw8", "msw12me", "msw12metemplate", "msw12template", "msw8template" };
		for (int i = 0; i < msWordFormats.length; i++) {
			if (msWordFormats[i].equalsIgnoreCase(format)) {
				if (format.startsWith("msw12")) {
					postfix = "docx";
				} else {
					postfix = "doc";
				}
				break;
			}
		}
		DfLogger.debug(this, "Exit getMSWordPostfix", null, null);
		return postfix;
	}

	/**
	 * Return the file path for the object based on the format, page and
	 * mofifier info
	 * 
	 * @param strFormat
	 * @param strPageNumber
	 * @param strPageModifier
	 * @return the path on the server
	 * @throws DfException
	 */
	public File getFilePath(String strFormat, String strPageNumber, String strPageModifier) throws DfException {
		DfLogger.debug(this, "Enter getFilePath", null, null);
		IDfFormat format = null;
		String strFilename = null;
		// use IDfSysobject::getFileEx2() only if strFormat is provided.
		// Otherwise, it might crash the application server
		
		//Create a temp file
		File tempGetFile = new File(System.getProperty("java.io.tmpdir") + m_sysObject.getObjectId() + Calendar.getInstance().getTimeInMillis());
		
		if (strFormat != null && strFormat.length() > 0) {
			int nPageNumber = 0;
			if (strPageNumber != null && strPageNumber.length() > 0) {
				try {
					nPageNumber = Integer.parseInt(strPageNumber.trim());
				} catch (NumberFormatException n) {
					throw new IllegalArgumentException("GetContentServlet: Invalid page number -- " + strPageNumber);
				}
			}
			try {
				
				// Open the file in the user's specified format
				//strFilename = m_sysObject.getFileEx2(tempGetFile.getAbsolutePath(), strFormat, nPageNumber, strPageModifier, false);
				URL myURL = new URL(strPageModifier);
				InputStream is = myURL.openStream();
			    ByteArrayOutputStream bos = new ByteArrayOutputStream();
			    int next = is.read();
			    while (next > -1) {
			      bos.write(next);
			      next = is.read();
			    }
			    bos.flush();
			    byte[] result = bos.toByteArray();
			    is.close();
			    bos.close();
			    System.out.println("byte array length: " + result.length);
			    
			    FileOutputStream fos = new FileOutputStream(tempGetFile);
			    fos.write(result);
			    fos.close();

			} /*catch (DfException err) {
				DfLogger.error(this, "Failed to get file content", null, err);
			}*/ catch (IOException e) {
				e.printStackTrace();
			}
		}
		DfLogger.debug(this, "strFilename" + strFilename, null, null);
/*		if (strFilename == null) {
			DfLogger.debug(this, "strFilename is null", null, null);
			// Open the file in the native format
			format = m_sysObject.getFormat();
			if (format != null) {
				strFilename = m_sysObject.getFile(tempGetFile.getAbsolutePath());
			}
		}*/
		
		DfLogger.debug(this, "Path of tempGetFile: " + tempGetFile.getAbsolutePath(), null, null);
		DfLogger.debug(this, "Size of tempGetFile: " + tempGetFile.length(), null, null);
		
		DfLogger.debug(this, "Exit getFilePath", null, null);
		return tempGetFile;
	}
	
	
	/**
	 * This method downloads the content file to end user's file system using
	 * browser.
	 * @param response
	 * @param fileContent
	 * @param contentType
	 * @param strMimeType
	 * @throws IOException
	 */
	public void downloadFile(HttpServletResponse response, File fileContent, String contentType, String strMimeType) throws IOException {
		DfLogger.debug(this, "Enter downloadFile", null, null);
		FileInputStream fileInputStream = null;
		OutputStream responseOutputStream = null;
		byte buffer[] = null;
		int nTotalWritten = 0;
		String sRenamedFileNm = null;

		try {
			sRenamedFileNm = getRenamedFileName(m_sysObject.getObjectName(), contentType);
			DfLogger.debug(this, "sRenamedFileNm: " + sRenamedFileNm, null, null);

			response.setContentType(strMimeType);
			response.setContentLength((int) fileContent.length());
			//response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName(sRenamedFileNm) + "\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "test.pdf" + "\"");
			
			fileInputStream = new FileInputStream(fileContent);
			
			responseOutputStream = response.getOutputStream();
			buffer = new byte[2048];
			nTotalWritten = 0;
			for (int nRead = fileInputStream.read(buffer); nRead >= 0;) {
				responseOutputStream.write(buffer, 0, nRead);
				nTotalWritten += nRead;
				nRead = fileInputStream.read(buffer);
				responseOutputStream.flush();
			}

			responseOutputStream.flush();
			fileInputStream.close();
		} catch (Exception ee) {
			DfLogger.error(this, ee.getMessage(), null, ee);
			
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				} 
			} catch (Exception err) {
				DfLogger.error(this, "Failed to close fileInputStream", null, err);
			}
			try {
				if (fileContent != null) {
					fileContent.delete();
				}
			} catch (Exception err) {
				DfLogger.error(this, err.getMessage(), null, err);
			}
		}
		DfLogger.debug(this, "Exit downloadFile", null, null);
	}
	
	/**
	 * Encode file name
	 * @throws UnsupportedEncodingException 
	 *
	 */
	private String encodeFileName(String fileName) throws UnsupportedEncodingException{
		DfLogger.debug(this, "Enter encodeFileName", null, null);
		DfLogger.debug(this, "Before encoding: " + fileName , null, null);
		
		fileName = getRealFileName(fileName);
		
		DfLogger.debug(this, "After removing invalid chars:  " + fileName , null, null);
		
		fileName = URLEncoder.encode(fileName, "UTF-8");
		fileName = replace(fileName, "+", "%20", -1);
		
		DfLogger.debug(this, "Length of filename:" + fileName.length() , null, null);
		DfLogger.debug(this, "After encoding: " + fileName, null, null);
		DfLogger.debug(this, "Exit encodeFileName", null, null);
		
		return fileName;
	}
	
	
    /**
     * This method removes non acceptable chars from file
     * Reference Information: GDMS Release 3.7, CR-0603
     * @param fileNM
     * @return String
     */
    private String getRealFileName(String fileNM)
    {
    	StringBuffer retFileNmBuff = new StringBuffer() ;
    	char ch;
    	DfLogger.debug(this, "Enter getRealFileName", null, null);
    	if (fileNM == null || fileNM.length() == 0 )
    		return retFileNmBuff.toString();
    	
    	for ( int ii = 0; ii < fileNM.length(); ii++ )
    	{
    		ch = fileNM.charAt(ii);
    		if ( ch == '\\' || ch == '/' ||  ch == '|' ||  ch == ':' ||  ch == '<' ||  ch == '>' || ch== '*' || ch == '?' || ch == '\"')
    			continue;
    		
    		retFileNmBuff.append(ch);
    	}
    	DfLogger.debug(this, "Exit getRealFileName", null, null);
    	return retFileNmBuff.toString();
    }
    
    
	/**
	 * Replace the searh string with replacement in the string.
	 * @param text
	 * @param searchString
	 * @param replacement
	 * @param max
	 * @return
	 */
	public String replace(String text, String searchString, String replacement, int max) {
		DfLogger.debug(this, "Enter replace", null, null);
		if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}

		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = (increase < 0 ? 0 : increase);
		increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
		StringBuffer buf = new StringBuffer(text.length() + increase);

		while (end != -1) {
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = text.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		DfLogger.debug(this, "Exit replace", null, null);
		return buf.toString();
	}
	
	/**
	 * Check if string is empty.
	 * @param str
	 * @return
	 */
	private boolean isEmpty(String str) {
		DfLogger.debug(this, "Enter/Exit isEmpty", null, null);
		return str == null || str.length() == 0;
	}

	
}
