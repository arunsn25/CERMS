package com.cosd.greenbuild.calwin.web.library.changecasestatus;

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

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.component.Component;
import com.documentum.web.form.Control;

public class ChangeCaseStatusComponent extends Component
{

	
	public void onInit(ArgumentList argList)
	{
		DfLogger.debug(this,"In ChangeCaseStatusComponent onInit",null,null);
		super.onInit(argList);
		//objectID = (String) argList.get("objectId");
		vals = argList.getValues("objectId");
	}

	
    public boolean onCommitChanges()
    {
    	DfLogger.debug(this,"ChangeCaseStatusComponent onCommitChanges()-----start",null,null);    	
    	
		try
		{
			for (String item:vals){
				
				IDfSysObject sysObj = (IDfSysObject) getDfSession().getObject(new DfId(item));
				Boolean currStatus = sysObj.getBoolean("new_document");
				//System.out.println(item + " : " + currStatus);
				sysObj.setBoolean("new_document", !currStatus);
				sysObj.save();
				DfLogger.debug(this,"ChangeCaseStatusComponent onCommitChanges() - Selected object ["+ item + "]: is marked for change case status",null,null);
			}
			
		}
		catch(DfException dfe)
		{
			DfLogger.error(this, "DfException raised in onCommitChanges of ChangeCaseStatusComponent: "+dfe.getMessage(), null, null);
		}
		return true;
        	
    }
    
	
	private static final long serialVersionUID = 1L;
	//private String objectID = null;
	private String vals[];


}
