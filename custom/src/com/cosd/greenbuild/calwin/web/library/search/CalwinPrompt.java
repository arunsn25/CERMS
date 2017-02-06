package com.cosd.greenbuild.calwin.web.library.search;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.component.Prompt;

/**
 * 
 * ******************************************************************************************
 * File Name: CalwinPrompt.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class CalwinPrompt extends Prompt {

	private static final long serialVersionUID = 1L;

	public void onInit(ArgumentList args)
    {
    	super.onInit(args);
    	setModal(true);
    }
	
}
