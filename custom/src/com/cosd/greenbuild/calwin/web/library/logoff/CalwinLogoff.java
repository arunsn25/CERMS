package com.cosd.greenbuild.calwin.web.library.logoff;

/**
 ******************************************************************************************
 *
 * Confidential Property of COSD.
 * (c) Copyright COSD, 2013.
 * All Rights reserved.
 * May not be used without prior written agreement
 *
 *******************************************************************************************
 *
 * Project		  CERMS
 * Description    CERMS Greenbuild
 * Created by     Arun Shankar
 * Created on     March 22, 2013
 ********************************************************************************************
 */

import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.config.IPreferenceStore;
import com.documentum.web.formext.config.PreferenceService;
import com.documentum.web.form.Control;
import com.documentum.web.form.Form;
import com.documentum.web.formext.session.Logoff;

public class CalwinLogoff extends Logoff
{

	private static final long serialVersionUID = 1L;

	public void onInit(ArgumentList args) {
		super.onInit(args);
		IPreferenceStore preferenceStore = PreferenceService.getPreferenceStore();
		preferenceStore.writeString("hhsaappname", " ");
	}
	
	  public String getForwardUrl()
	  {
	    return "cosdsearchentry";
	  }
	  
	  public void onControlInitialized(Form form, Control control)
	  {
	    String name = control.getName();
	    if ("defaultPagePanel".equals(name))
	    {
	      control.setVisible(false);
	    }

	    if ("forwardAfterLogoffPanel".equals(name))
	    {
	      control.setVisible(true);
	    }

/*	    if ("closeAfterLogoffPanel".equals(name))
	    {
	      control.setVisible(this.m_afterLogoff == 1);
	    }*/
	  }
	
}
