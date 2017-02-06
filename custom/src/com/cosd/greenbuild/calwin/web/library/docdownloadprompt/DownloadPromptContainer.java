package com.cosd.greenbuild.calwin.web.library.docdownloadprompt;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.formext.component.DialogContainer;

/**
 * 
 * ******************************************************************************************
 * File Name: DownloadPromptContainer.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class DownloadPromptContainer extends DialogContainer {
	

	private static final long serialVersionUID = 1L;

	public void onInit(ArgumentList arg)
    {
        super.onInit(arg);
        String objectID = arg.get("objectId");
        downloadPromptObjectID = objectID;
		// -------------- SET SESSION STATE START-----------------
		SessionState.setAttribute("OBJECTID", objectID);
/*		SessionState.setAttribute("OBJECT_NAME", "Download");
		SessionState.setAttribute("MSWORD_DOWNLOAD_POSTFIX", "_clean");*/
		// -------------- SET SESSION STATE END-----------------

    }
    
    public static String downloadPromptObjectID;

}
