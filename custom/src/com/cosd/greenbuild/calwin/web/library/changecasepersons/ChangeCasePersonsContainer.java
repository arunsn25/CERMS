package com.cosd.greenbuild.calwin.web.library.changecasepersons;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Button;
import com.documentum.webcomponent.library.multiargdialogcontainer.MultiArgDialogContainer;

public class ChangeCasePersonsContainer extends MultiArgDialogContainer {

	private static final long serialVersionUID = 1L;
	
/*    private void onDeselect(Control button, ArgumentList args)
    {
    	setStrDeSelect(true);
		if ((canCommitChanges()) && (onCommitChanges())) {
			if (getTopForm().getCallerForm() != null) {
				setComponentReturn();
			}
		}
    }*/

	public void onOk(Control button, ArgumentList args)
	  {
		  Button buttonDeselect = (Button)button;
		  if (buttonDeselect.getLabel().equalsIgnoreCase(getString("MSG_DESELECT")))
				  setStrDeSelect(true);
		  super.onOk(button, args);
	  }
    
	public static Boolean getStrDeSelect() {
		return strDeSelect;
	}

	public static void setStrDeSelect(Boolean bVal) {
		strDeSelect = bVal;
	}

	private static Boolean strDeSelect = false;
	
}
