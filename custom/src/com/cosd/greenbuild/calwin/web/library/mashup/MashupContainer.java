package com.cosd.greenbuild.calwin.web.library.mashup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cosd.greenbuild.calwin.mashup.MashupInfo;
import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.cosd.greenbuild.calwin.mashup.MaxFGDocumentsExceededException;
import com.cosd.greenbuild.calwin.mashup.MaxFGTotalLengthExceededException;
import com.cosd.greenbuild.calwin.mashup.MaxTotalLengthExceededException;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.utils.CDCRUtils;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Button;
import com.documentum.web.form.control.Checkbox;
import com.documentum.web.form.control.DropDownList;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.Datagrid;
import com.documentum.web.form.control.databound.DatagridRow;
import com.documentum.web.formext.action.ActionService;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.component.DialogContainer;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.webcomponent.library.messages.MessageService;

public class MashupContainer extends DialogContainer implements COSDCalwinConstants, IActionPrecondition {

	private static final long serialVersionUID = 1L;
	private String cdcrnum = null;
	private Label lError = null;
	private Component comp = null;
	private final String sLoginName = null;
	//	private ArrayList<String> alIDs = null;
	//	private ArrayList<String> alTypes = null;
	//	private static int iCount_limit = 0;
	//	private static float fSize_limit = 0;
	//	private static float fMax_Size_limit = 0;
	private CDCRUtils cdcrUtils = null;
	private DropDownList Days_to_Expire = null;
	//	private static String sMashupMode = null;
	//	private static String adobeServerAddress = null;
	//	private String mashupObjectName = null;
	//	private String sPassword = null;
	//	private String m_IDs = null;
	//	private String m_Types = null;
	private final String m_TotalSize = null;

	//	private HashMap mapDocumentTypes = null;

	@Override
	public void onInit(ArgumentList argList) {
		super.onInit(argList);

		MessageService.clear(this);

		cdcrnum = (String) SessionState.getAttribute(SESSION_CDCR_NUMBER);

		cdcrUtils = new CDCRUtils();
		comp = getContainedComponent();
		lError = (Label) comp.getControl("error_message", Label.class);
		Days_to_Expire = (DropDownList) comp.getControl("days_to_expire", DropDownList.class);

		if (cdcrnum == null)
			lError.setLabel(getString("MSG_ERROR_NO_CDCR_NUMBER"));
	}

	public void onTest(Button b, ArgumentList al) {
		ArgumentList mashupArgs = new ArgumentList();
		mashupArgs.add("inline", "false");
		ActionService.execute("soms_mashup_type", mashupArgs, this.getContext(), this, null);
	}

	@Override
	public void onOk(Control con, ArgumentList al) {
		IDfSession session = getDfSession();
		MashupManager mgr = null;

		try {
			mgr = MashupManager.getInstance(session);
			String cdcrnum = (String) SessionState.getAttribute(SESSION_CDCR_NUMBER);
			MashupInfo mashup = new MashupInfo(cdcrnum, session);
			//			System.out.println("comp is: " + comp);
			//			System.out.println("remove_comments is: " + comp.getControl("remove_comments", Checkbox.class));
			mashup.setAuditType(AUDIT_EVENT_MASHUP_ADVANCED);
			Checkbox removeCommentsCB = (Checkbox) comp.getControl("remove_comments", Checkbox.class);
			mashup.setRemoveComments(removeCommentsCB.getValue());
			Checkbox removeBookmarksCB = (Checkbox) comp.getControl("remove_bookmarks", Checkbox.class);
			mashup.setRemoveBookmarks(removeBookmarksCB.getValue());
			Text fileNameT = (Text) comp.getControl("filename", Text.class);
			mashup.setTitle(fileNameT.getValue());

			// set to expire X days in future (at midnight via setExpiryDays)
			int numDays = Integer.parseInt(Days_to_Expire.getValue());
			mashup.setExpiryDays(numDays);

			MashupComponent mashupComponent=(MashupComponent) comp;
			Datagrid dg = (Datagrid) mashupComponent.getControl("dgDocList", Datagrid.class);
			List<String> ids = getAllIDsFromDatagrid(dg);
			if (ids.isEmpty()) {
				lError.setLabel(getString("MSG_ERROR_NO_OBJECT"));
				return;
			}

			for (String idStr : ids)
				mashup.addDocument(new DfId(idStr), session);

			//System.out.println("Mashup is:\n" + mashup);

			Checkbox backgroud_mashup = (Checkbox) comp.getControl("backgroud_mashup", Checkbox.class);
			if (backgroud_mashup.getValue()) {
				mgr.mashupBackground(mashup, session);
				//MessageService.addMessage(this, "MSG_SUBMIT_SUCCESS");
				//MessageService.addMessage(null, "MSG_SUBMIT_SUCCESS");
				//				MessageService.addMessage(getTopForm().getCallerForm(), strMessagePropId)
				//				MessageService.addMessage(NlsResourceClass.getResource(this.getClass().getName()), "MSG_SUBMIT_SUCCESS", null, null);
				ArgumentList mashupArgs = new ArgumentList();
				super.onOk(con, al);

				setComponentJump("mashupSuccess", mashupArgs, getContext());
				//ActionService.execute("mashupSuccess", mashupArgs, getContext(), this, null);

			} else {
				IDfId mashId = mgr.mashupForegroundAndWait(mashup, session);
				viewPDF(mashId);
				super.onOk(con, al);
			}

		} catch (MaxTotalLengthExceededException e) {
			lError.setLabel(getString("MSG_ERROR_MAX_SIZE_LIMIT"));
			return;
		} catch (MaxFGDocumentsExceededException e) {
			lError.setLabel(getString("MSG_ERROR_COUNT_LIMIT"));
			return;
		} catch (MaxFGTotalLengthExceededException e) {
			lError.setLabel(getString("MSG_ERROR_SIZE_LIMIT"));
			return;
		} catch (Exception t) {
			lError.setLabel(getString("MSG_MASHUP_FAILED"));
			DfLogger.error(this, "Unexpected error performing mashup", null, t);
			t.printStackTrace();
			return;
		}

	}

	public List<String> getAllIDsFromDatagrid(Datagrid dg) {
		// id's sorted by original display index
		Map<Integer, String> sortedIds = new TreeMap<Integer, String>();
		dg.getDataProvider().setDfSession(getDfSession());

		// get all the rows
		Iterator gridControls = dg.getContainedControls();
		while (gridControls.hasNext()) {
			Control gridRow = (Control) gridControls.next();
			if ((gridRow.getClass()).equals(DatagridRow.class)) {
				// get all the columns
				boolean checked = false;
				int index=-1;
				String id=null;

				Iterator gridcoloumns = gridRow.getContainedControls();
				while (gridcoloumns.hasNext()&&(index==-1||id==null)) {
					Control control = (Control) gridcoloumns.next();
					String name = control.getElementName();
					//					System.out.println(" name::"+name);
					if (control instanceof Checkbox) {
						checked = ((Checkbox) control).getValue();
						if (!checked)
							break;
					} else if (checked && name.startsWith("body_Index_")) {
						// the ID column has values like 1,2,3,..... So we need to subtract 1 when used as index, which starts at 0.
						Label labelIdx = (Label) control;
						index=Integer.parseInt(labelIdx.getLabel());
					} else if (checked && name.startsWith("body_ID_")) {
						Label labelID = (Label) control;
						id=labelID.getLabel();
					}
				}
				if (checked)
					sortedIds.put(index,id);

			}
		}

		return new ArrayList<String>(sortedIds.values());
	}

	private void viewPDF(IDfId mashId) {
		ArgumentList mashupArgs = new ArgumentList();
		mashupArgs.add("objectId", "" + mashId);
		mashupArgs.add("contentType", "pdf");
		mashupArgs.add("inline", "false");
		mashupArgs.add("launchViewer", "true");
		ActionService.execute("view", mashupArgs, this.getContext(), this, null);
	}

	public String[] getRequiredParams() {
		return new String[] {};
	}

	public boolean queryExecute(String paramString,
			IConfigElement paramIConfigElement,
			ArgumentList paramArgumentList,
			Context paramContext,
			Component component) {
		boolean enabled=true;
		if (component != null) {
			// only show if there are docs for the selected cdcr_number
			// Set in SearchComponent#onSearchTrigger
			Integer numDocs=(Integer) SessionState.getAttribute(SESSION_CDCR_NUMBER_COUNT);
//			System.out.println("Num cfiles from session: "+numDocs);
			enabled=(numDocs!=null&&numDocs>0);
		}
		return enabled;
	}

}
