package com.cosd.greenbuild.calwin.web.library.mashup;

import java.util.Locale;

import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.common.DfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Password;
import com.documentum.web.formext.component.DialogContainer;
import com.documentum.webcomponent.library.messages.MessageService;

public class MashupTypeContainer extends DialogContainer implements COSDCalwinConstants {

	public class BadPasswordException extends Exception {

		private static final long serialVersionUID = 1L;

		public final String nlsId;
		public final String paramStr;

		public BadPasswordException(String nlsid) {
			this(nlsid, null);
		}

		public BadPasswordException(String nlsid, String paramStr) {
			this.nlsId = nlsid;
			this.paramStr = paramStr;
		}

	}

	private static final String params[] = new String[] { "objectId" };

	private static final long serialVersionUID = 1L;

	private String objectId;

	@Override
	public void onInit(ArgumentList argList) {
		super.onInit(argList);

		MessageService.clear(this);
		this.objectId = argList.get("objectId");
	}

	@Override
	public void onOk(Control con, ArgumentList al) {
		MashupTypeComponent typeComponent = (MashupTypeComponent) getContainedComponent();
		Label errorLabel = (Label) typeComponent.getControl("error_message", Label.class);
		String result = null;
		try {
			//System.out.println("Export with passwords");
			String op = ((Password) typeComponent.getControl("passwordOpen")).getValue();
			validatePassword(op);
			String ep = ((Password) typeComponent.getControl("passwordEdit")).getValue();
			validatePassword(ep);
			if (op.equals(ep))
				throw new BadPasswordException("MSG_ERROR_SAME", null);

			String msg = getNlsClass().getString("MSG_CHOOSE_BUSY", Locale.getDefault());
			// trigger export
			MashupManager mgr = MashupManager.getInstance(getDfSession());
			result = mgr.export(new DfId(objectId), op, ep, getDfSession()); // IDfId nonRMId	
			//System.out.println("Output from LiveCycle Export Service ::: " + result);
/*			DfLogger.info(this, "Created non-rm passworded mashup: " + nonRMId + " from: " + objectId, null, null);
			objectId = "" + nonRMId;
*/
			/* now done by LiveCycle
			// audit the export
			String[] additionalInfo = { "Saved By=[" + getDfSession().getLoginUserName() + "]"};
			new CDCRUtils().createAudit(getDfSession(), objectId, additionalInfo, CDCRConstants.AUDIT_EVENT_MASHUP_SAVE);
			*/



		} catch (BadPasswordException e) {
			String msg = getString(e.nlsId) + (e.paramStr == null ? "" : ": " + e.paramStr);
			errorLabel.setLabel(msg);
			return;

		} catch (Throwable e) {
			MessageService.addMessage(this, "MSG_MASHUP_FAILED");
			e.printStackTrace();
			super.onOk(con, al);
			return;
		}


		
		
		// do standard export action
		ArgumentList mashupArgs = new ArgumentList();
		mashupArgs.add("objectId", objectId);
		mashupArgs.add("resultMSG", result);
		addComponentNestedArgs(mashupArgs);
		setReturnValue("objectId", objectId);
		setReturnValue("resultMSG", result);
		super.onOk(con, al);
	}

	private void validatePassword(String toCheck) throws BadPasswordException {
		if (toCheck == null || toCheck.trim().length() == 0) {
			throw new BadPasswordException("MSG_ERROR_PASSWORD", null);
		}
		// the following 4 chars are not allowed in Export's password field.
		char[] badChars = "<>'\"".toCharArray();
		for (char c : badChars) {
			if (toCheck.indexOf(c) > -1)
				throw new BadPasswordException("MSG_ERROR_CHAR", "" + c);
		}

	}

	public void onAlternativeCancel(Control con, ArgumentList al) {
		super.setComponentReturn();
	}

	public String[] getRequiredParams() {
		return null;
	}

}
