package com.cosd.greenbuild.calwin.web.actions;

import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.*;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.docbase.ObjectCacheUtil;

/**
 * 
 * ******************************************************************************************
 * File Name: DeleteDocPrecondition.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class DeleteDocPrecondition
 implements IActionPrecondition
{

 public DeleteDocPrecondition()
 {
 }

 public String[] getRequiredParams()
 {
     return (new String[] {
         "objectId"
     });
 }

 public boolean queryExecute(String strAction, IConfigElement config, ArgumentList arg, Context context, Component component)
 {
     boolean bExecute = false;
	 //String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
     String m_strDocbaseObjectId = arg.get("objectId");

	 
/*	boolean bAccessibilityOn = AccessibilityService.isAllAccessibilitiesEnabled();
     String strComponentId = component.getComponentId(bAccessibilityOn);
     if(strComponentId.equals("vdmlist") || strComponentId.equals("vdmliststreamline"))
         return bExecute;*/
/*     String strLockOwner = arg.get("lockOwner");
     if(strLockOwner == null)
     {
         String strObjectId = arg.get("objectId");
         if(strObjectId != null)
         {
             IDfSession dfSession = component.getDfSession();
             try
             {
                 IDfSysObject sysobj = (IDfSysObject)dfSession.getObject(new DfId(strObjectId));
                 if(sysobj != null)
                     strLockOwner = sysobj.getLockOwner();
             }
             catch(DfException e)
             {
                 throw new WrapperRuntimeException("Failed to get lock owner", e);
             }
         }
     }
     if(strLockOwner == null || strLockOwner.length() == 0)
         bExecute = true;*/
     
     
		IDfSession dfSession = component.getDfSession();
		int iPermitValueOnObject = 0;
		
		if (m_strDocbaseObjectId != null) { // && CalwinSearchFrame.strNLSSimpleSearch.equals(strCurrSrcView)) {
			try {
				//Try to get the object
				IDfSysObject sysObject = (IDfSysObject) ObjectCacheUtil.getObject(dfSession, m_strDocbaseObjectId);

			     String strLockOwner = arg.get("lockOwner");
			     if(strLockOwner == null)
			     {
			         strLockOwner = sysObject.getLockOwner();
			     }
			     if(strLockOwner == null || strLockOwner.length() == 0)
			         bExecute = true;
				
				//Get the object's permissions
				iPermitValueOnObject = sysObject.getPermit();
				//If the permissions is not Delete permission
				if (iPermitValueOnObject < IDfACL.DF_PERMIT_DELETE)
				{
					bExecute=false;
				}

			}
			catch (DfObjectNotFoundException e) {
				DfLogger.error(this, "Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, null, e);
				throw new WrapperRuntimeException("Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, e);
			}
			catch (DfException e) {
				DfLogger.error(this, "Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, null, e);
				throw new WrapperRuntimeException("Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, e);
			}
		}
     
     

/*		System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
		if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
			bExecute=false;
		}*/
     
     return bExecute;
 }
}

