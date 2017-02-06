package com.cosd.greenbuild.calwin.mashup.cts;

import java.io.IOException;

import com.cosd.greenbuild.calwin.mashup.MashupProcessor;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

public class CTSMashupProcessor extends MashupProcessor {

	public CTSMashupProcessor() {
		super();
	}

	@Override
	protected DfId doMashup() throws IOException {
		throw new RuntimeException("cts mashup is not yet implemented");
	}

	@Override
	protected String doExport(IDfId id, String openPass, String permsPass, IDfSession session) throws IOException { // IDfId
		throw new RuntimeException("cts mashup is not yet implemented");
	}

	@Override
	protected String doExport(IDfId id, IDfSession session) throws IOException {
		throw new RuntimeException("cts mashup is not yet implemented");
	}

	/* this is the old method which used cts
	public boolean execute(String actionName, IConfigElement configElement,
						   ArgumentList argumentList,Context context, Component component, Map map) 
	{

		DfLogger.debug(this,"MashupAction Execute Called",null,null);
		String objectID = argumentList.get("objectId");
		String requestType = argumentList.get("requestType");
		DfLogger.debug(this,"Action Name ["+actionName+"] ObjectID Passed ["+objectID+"] requestType ["+requestType+"]",null,null);
		
		try
		{
			CDCRUtils cdcrUtils = new CDCRUtils();
			Vector <String> objectVector = cdcrUtils.getObjectAsVector(objectID);
			IDfSession session = component.getDfSession();
			
			//perform mash-up if 2 or more objects are selected for viewing
			if(objectVector.size() >=2)
			{
				String sourceObjectID = (String) objectVector.get(0);
				
				String sectionName = null;
				
				IDfSysObject sysObj = (IDfSysObject) session.getObject(new DfId(sourceObjectID));
				String cdcrNumber = sysObj.getString("cdcr_number");
				int section = sysObj.getInt("section");
				
				IDfQuery query = new DfQuery();
				query.setDQL(SECTION_LOOKUP_OBJECT_QRY+section+LOOKUP_FOLDER_LOCATION_QRY);
				
				IDfCollection coll = query.execute(session,DfQuery.DF_READ_QUERY);
				if(coll.next())
				{
					sectionName = (String) coll.getString("title");
				}
				coll.close();
				
				String mashupObjectName = cdcrNumber+"_"+sectionName;
				
				String mashupOwner = session.getLoginUserName();

				HashMap mashupMap = new HashMap();
				mashupMap.put("object_name", mashupObjectName);
				mashupMap.put("cdcr_number", cdcrNumber);
				mashupMap.put("section", sectionName);
				mashupMap.put("mashupOwner", mashupOwner);
				mashupMap.put("days_to_expire", "2");
				mashupMap.put("IDF_SESSION", session);
				
				String[] additionalInfo = new String[2];
				additionalInfo[0] = "CDCR Number=["+cdcrNumber+"]";
				additionalInfo[1] = "Section=["+sectionName+"]";
				
				String sEventName = null;
				if(requestType != null && requestType.equals(MASHUP_ALL_REQUESTED))
				{
					sEventName =  MASHUP_ALL_AUDIT_EVENT;
				}
				else if(requestType != null && requestType.equals(MASHUP_SELECTED_REQUESTED))
				{
					sEventName = MASHUP_SELECTED_AUDIT_EVENT;
				}
				else
				{
					sEventName = MASHUP_AUDIT_EVENT;
				}
				
	//				boolean bAsyncMashup = false;
	//				
	//				IPreferenceStore preferenceStore = PreferenceService.getPreferenceStore();
	//				try
	//				{   // if the boolean value does not exist, ignore it
	//					bAsyncMashup = preferenceStore.readBoolean("erms.async_mashup");
	//				}
	//				catch(NullPointerException np) {}
	//
	//		        if (bAsyncMashup)
	//		        {
	//		    		cdcrUtils.requestMashup(mashupMap, objectID, additionalInfo, sEventName);
	//					MessageService.addMessage(component, "MSG_SUBMIT_SUCCESS");
	//		        }
	//		        else
	//		        {
		        	String sMashupMode = "adts";
		        	String adobeServerAddress = null;
		        	//Get erms_adobe_asembler_ws config object and get mashup_mode for mashup
		        	// if the config object has not been set, default to ADTS
					HashMap<String, String> configMap = cdcrUtils.getConfigObject(session, GET_ADOBE_ASEMBLER_CONFIG_QRY);
					if (configMap != null)
					{
						adobeServerAddress = configMap.get(CONFIG_ADOBE_WS_SERVER);
						sMashupMode = configMap.get("mashup_mode");
					}
					
					String resultObjectID = null;
					
					if (sMashupMode.equals("adts"))
					{
						MashupService mashupService = new MashupService(session.getDocbaseName(),mashupMap);
						resultObjectID = mashupService.mashup((String)objectVector.get(0), objectVector);
					}
					else
					{
						resultObjectID = cdcrUtils.AdobeMashup(mashupMap, objectID.replaceAll(";", "|"), "", adobeServerAddress, sEventName, "", "");
					}
					
					DfLogger.debug(this,"Result object ID is ["+resultObjectID+"]",null,null);
					if(resultObjectID != null)
					{
						if (sMashupMode.equals("adts"))
						{
						cdcrUtils.createAudit(session, resultObjectID, additionalInfo, sEventName);
						}

						// set expiration date
						IDfSysObject Object = (IDfSysObject)session.getObject(new DfId(resultObjectID));
						IDfTime targetTime = cdcrUtils.getTargetTime("2");
						Object.setTime("a_retention_date", targetTime);
						Object.setString("remove_document", "1");
						Object.save();
						
						ArgumentList mashupArgs = new ArgumentList();
						mashupArgs.add("objectId", resultObjectID);
						mashupArgs.add("contentType", "pdf");
						mashupArgs.add("inline", "false");
						mashupArgs.add("launchViewer", "true");
						//ActionService.execute("taskspaceview", mashupArgs, context, component, null);
						ActionService.execute("view", mashupArgs, context, component, null);
					}
					else
					{
						MessageService.addMessage(component, "MSG_MASHUP_FAILED");
					}
	//		        }
			}
			else if(objectVector.size() == 1)
			{
				//call default OOB view operation
				ArgumentList mashupArgs = new ArgumentList();
				mashupArgs.add("objectId", (String)objectVector.get(0));
				mashupArgs.add("contentType", "pdf");
				mashupArgs.add("inline", "false");
				mashupArgs.add("launchViewer", "true");
				//ActionService.execute("taskspaceview", mashupArgs, context, component, null);
				ActionService.execute("view", mashupArgs, context, component, null);
				
			}
			else
			{
				MessageService.addMessage(component, "MSG_MASHUP_SELECT_TWO_OR_MORE");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			DfLogger.error(this, "Failed to create mashup. Reason ["+e.getMessage()+"]", null, null);
			MessageService.addMessage(component, "MSG_MASHUP_FAILED");
		}
		
		return true;
	}

	public Vector<String> getObjectAsVector(String objectID) {
		Vector<String> v = new Vector<String>();
		StringTokenizer strTok = new StringTokenizer(objectID, ";");

		while (strTok.hasMoreTokens()) {
			String s1 = strTok.nextToken();
			if (v.contains(s1) == false) {
				v.add(s1);
			}
		}

		return v;
	}
	*/
}
