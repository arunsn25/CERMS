package com.cosd.greenbuild.calwin.web.library.mashup;

import com.cosd.greenbuild.calwin.web.actions.ThirdPartyView;
import com.documentum.webcomponent.library.contenttransfer.export.UcfExportContainer;

/**
 * A custom export container called from mashup export button
 * after providing password which does not show the Export Successful message.
 * The display of this message seems to cause a crash on IE7 for
 * 2nd and subsequent calls.  
 * 
 * @see ThirdPartyView#onReturn(com.documentum.web.form.Form, java.util.Map)
 * @author Andy.Taylor
 *
 */
public class ExportNoMessageContainer extends UcfExportContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExportNoMessageContainer() {
		super();
	}

	@Override
	protected void addFinalSuccessMessage() {
		// overridden to not display message.
	}
}
