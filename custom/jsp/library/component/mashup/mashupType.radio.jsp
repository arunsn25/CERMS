<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.formext.component.Component"%>
<%@ page import="com.cdcr.soms.web.library.export.ExportComponent"%>

<dmf:html>
<dmf:head>
<script type="text/javascript">
function showPasswords() {
	var shown=document.getElementById('exportPass').checked;
	var passDiv=document.getElementById('passwordsDiv');
	if (shown) {
		passDiv.style.display='block';
		var inp=document.getElementById('passwordOpen');
		inp.select();
		inp.focus();
		validatePasswords();
	} else {
		passDiv.style.display='none';
		document.getElementById('exportBtn').disabled=false;
	}
}

function onPasswordKeypress(event) {
	validatePasswords();
}

function validatePasswords() {
	var passOpen=document.getElementById('passwordOpen').value;
	var passOpenConfirm=document.getElementById('passwordOpenConfirm').value;
	var oMsg=validatePassword(passOpen,passOpenConfirm,'open');
	if (!oMsg)
		oMsg='';
	var oErrDiv=document.getElementById('openErrDiv');
	oErrDiv.innerHTML=oMsg;
		
	var passEdit=document.getElementById('passwordEdit').value;
	var passEditConfirm=document.getElementById('passwordEditConfirm').value;
	var	eMsg=validatePassword(passEdit,passEditConfirm,'edit');
	if (!eMsg && passOpen==passEdit)
		eMsg=document.getElementById('MSG_ERROR_SAME').innerHTML;
	if (!eMsg)
		eMsg='';
	var editErrDiv=document.getElementById('editErrDiv');
	editErrDiv.innerHTML=eMsg;

	var validated=oMsg==''&&eMsg=='';
	document.getElementById('exportBtn').disabled=!validated;
	return validated;
}

var badChars='<>\'"';
function validatePassword(pass,confirm,type) {
	if (pass==''&&confirm=='')
		return document.getElementById('MSG_ERROR_PASSWORD').innerHTML;
	
	for (var i=0;i<badChars.length;i++) {
		var toCheck=badChars.substring(i,i+1);
		if (pass.indexOf(toCheck)>-1) 
			return document.getElementById('MSG_ERROR_CHAR').innerHTML;
	}
	if (pass!=confirm)
		return document.getElementById('MSG_ERROR_CONFIRM').innerHTML;
			
}

function hookEvent(element,eventName,handler) {
	if (element.attachEvent)
		element.attachEvent('on'+eventName,handler);
	else if (element.addEventListener)
		element.addEventListener(eventName,handler,false);
	else
		element['on'+eventName]=handler;
}
</script>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/wdk/include/dynamicAction.js")%>'></script>
</dmf:head>
<dmf:body marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
<dmf:form>
</dmf:form>
<!-- error messages from nls, used via javascript -->
<dmf:label id="MSG_ERROR_PASSWORD" nlsid="MSG_ERROR_PASSWORD" style="display:none"/>
<dmf:label id="MSG_ERROR_SAME" nlsid="MSG_ERROR_SAME" style="display:none"/>
<dmf:label id="MSG_ERROR_CHAR" nlsid="MSG_ERROR_CHAR" style="display:none"/>
<dmf:label id="MSG_ERROR_CONFIRM" nlsid="MSG_ERROR_CONFIRM" style="display:none"/>

<dmf:label nlsid="MSG_TYPE_HEADER"/>
<p/>
<dmf:radio name='exportRM' nlsid="MSG_EXPORT_RM" runatclient="True" 
  onclick="showPasswords" id="exportRM" group="exportType" value="True" style="font-weight: bold"
/>:
<dmf:label nlsid="MSG_EXPORT_RM_DESC"/>
<br/>
<dmf:radio name='exportPass' nlsid="MSG_EXPORT_PASSWORD" runatclient="True"
  onclick="showPasswords" id="exportPass" group="exportType"  style="font-weight: bold"
/>:
<dmf:label nlsid="MSG_EXPORT_PASSWORD_DESC"/>
<br/>
<div style="display: none; margin-left: 40px" id="passwordsDiv">
<table cellpadding="0" cellspacing="0" style="margin-top: 5px">
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_OPEN"/></td>
<td>
<dmf:password name='passwordOpen' id="passwordOpen" size='52'/>
<script> // attach keypress js event
hookEvent(document.getElementById('passwordOpen'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_OPEN_CONFIRM"/></td>
<td>
<dmf:password name='passwordOpenConfirm' id="passwordOpenConfirm" size='52' />
<script> // attach keypress js event
hookEvent(document.getElementById('passwordOpenConfirm'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td></td>
<td style="padding-bottom: 15px">
<div id="openErrDiv" style="color: red"></div>
</td>
</tr>
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_EDIT"/></td>
<td>
<dmf:password name='passwordEdit' id="passwordEdit" size='52' />
<script> // attach keypress js event
hookEvent(document.getElementById('passwordEdit'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_EDIT_CONFIRM"/></td>
<td>
<dmf:password name='passwordEditConfirm' id="passwordEditConfirm" size='52' />
<script> // attach keypress js event
hookEvent(document.getElementById('passwordEditConfirm'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td></td>
<td style="padding-bottom: 15px">
<div id="editErrDiv" style="color: red"></div>
</td>
</tr>
</table>
</div>
</dmf:body>
</dmf:html>
