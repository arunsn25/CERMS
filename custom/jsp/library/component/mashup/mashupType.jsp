<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.formext.component.Component"%>

<dmf:html>
<dmf:head>
<script type="text/javascript">
function showPasswords() {
	var passDiv=document.getElementById('passwordsDiv');
	passDiv.style.display='block';
	var inp=document.getElementById('passwordOpen');
	inp.select();
	inp.focus();
	validatePasswords();
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
		eMsg=document.getElementById('ERROR_PASSWORD_SAME').innerHTML;
	if (!eMsg)
		eMsg='';
	var editErrDiv=document.getElementById('editErrDiv');
	editErrDiv.innerHTML=eMsg;

	var validated=oMsg==''&&eMsg=='';
	var exportBtn=document.getElementById('exportBtn');
	if (exportBtn)
		exportBtn.disabled=!validated;
	return validated;
}

var badChars='<>\'"';
function validatePassword(pass,confirm,type) {
	if (pass==''&&confirm=='')
		return document.getElementById('ERROR_PASSWORD_MISSING').innerHTML;
	
	for (var i=0;i<badChars.length;i++) {
		var toCheck=badChars.substring(i,i+1);
		if (pass.indexOf(toCheck)>-1) 
			return document.getElementById('ERROR_PASSWORD_CHAR').innerHTML;
	}
	if (pass!=confirm)
		return document.getElementById('ERROR_PASSWORD_CONFIRM').innerHTML;

	if (!/\d/.test(pass))
		return document.getElementById('ERROR_PASSWORD_DIGIT').innerHTML;
	if (!/[a-z]/.test(pass))
		return document.getElementById('ERROR_PASSWORD_LOWER').innerHTML;
		
	if (!/[A-Z]/.test(pass))
		return document.getElementById('ERROR_PASSWORD_UPPER').innerHTML;

	if (pass.length<8)
		return document.getElementById('ERROR_PASSWORD_SHORT').innerHTML;
	
	if (pass.length>20)
		return document.getElementById('ERROR_PASSWORD_LONG').innerHTML;

	return null;
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
<dmf:label id="ERROR_PASSWORD_MISSING" nlsid="ERROR_PASSWORD_MISSING" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_CHAR" nlsid="ERROR_PASSWORD_CHAR" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_SAME" nlsid="ERROR_PASSWORD_SAME" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_CONFIRM" nlsid="ERROR_PASSWORD_CONFIRM" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_DIGIT" nlsid="ERROR_PASSWORD_DIGIT" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_LOWER" nlsid="ERROR_PASSWORD_LOWER" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_UPPER" nlsid="ERROR_PASSWORD_UPPER" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_SHORT" nlsid="ERROR_PASSWORD_SHORT" style="display:none"/>
<dmf:label id="ERROR_PASSWORD_LONG" nlsid="ERROR_PASSWORD_LONG" style="display:none"/>

<dmf:label nlsid="MSG_PASSWORDS"/>
<p/>
<div style="margin-left: 40px" id="passwordsDiv">
<table cellpadding="0" cellspacing="0" style="margin-top: 5px">
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_OPEN"/></td>
<td>
<dmf:password name='passwordOpen' id="passwordOpen" size='20'/>
<script> // attach keypress js event
hookEvent(document.getElementById('passwordOpen'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_OPEN_CONFIRM"/></td>
<td>
<dmf:password name='passwordOpenConfirm' id="passwordOpenConfirm" size='20' />
<script> // attach keypress js event
hookEvent(document.getElementById('passwordOpenConfirm'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td></td>
<td style="padding-bottom: 15px">
<div id="openErrDiv" style="color: red; height: 10px"></div>
</td>
</tr>
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_EDIT"/></td>
<td>
<dmf:password name='passwordEdit' id="passwordEdit" size='20' />
<script> // attach keypress js event
hookEvent(document.getElementById('passwordEdit'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td style="text-align: right; padding-right: 5px;"><dmf:label nlsid="MSG_PASSWORD_EDIT_CONFIRM"/></td>
<td>
<dmf:password name='passwordEditConfirm' id="passwordEditConfirm" size='20' />
<script> // attach keypress js event
hookEvent(document.getElementById('passwordEditConfirm'),'keyup',onPasswordKeypress);
</script>
</td>
</tr>
<tr>
<td></td>
<td style="padding-bottom: 15px">
<div id="editErrDiv" style="color: red; height: 10px"></div>
</td>
</tr>
</table>
</div>
<div id="helpDiv">
Password Requirements:
<ul style="margin-top: 5px; margin-bottom: 5px;">
<li>must contain at least one digit from 0-9</li>
<li>must contain at least one lowercase character</li>
<li>must contain at least one uppercase character</li>
<li>must be at least 8 characters in length and no more than 20 characters</li>
</ul>
</div>
<script>
var passOpen=document.getElementById('passwordOpen');
passOpen.focus();
passOpen.select();
validatePasswords();
</script>
</dmf:body>
</dmf:html>
