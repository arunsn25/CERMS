<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<dmf:html>
<dmf:head>
<dmf:webform/>
</dmf:head>
<dmf:body cssclass='contentBackground' topmargin="0" leftmargin="0" marginheight="0" marginwidth="0"
showdialogevent='true' id='deletedocument' height='230' width='400'>
<dmf:form autofocusneeded='true'>
<table cellpadding="0" cellspacing="0" border="0">

<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<td width="100%" colspan=4>
<dmf:label name='info_msg' nlsid='MSG_DELETE_CALWINMESSAGE' style="{font-family:Trebuchet MS, Verdana, GillSans, Arial;color: #333333;font-size: 13px;line-height: 14px;vertical-align: center; font-weight: normal;text-decoration: none}" />
</td>
</tr>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<td width="100%" colspan=4>
<dmf:label name='info_msg' nlsid='MSG_DELETE_CALWINWARNING' style="{font-family:Trebuchet MS, Verdana, GillSans, Arial;color: red;font-size: 13px;line-height: 14px;vertical-align: center; font-weight: bold;text-decoration: none}" />
</td>
</tr>

<dmf:panel name="assemblyoptions">
<fieldset style="border:0px">
<tr>
<td class="fieldlabel" colspan="2"><legend><dmf:label nlsid="MSG_ASSEMBLY"/>:</legend></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deleteonlyassembly" group="group1" nlsid="MSG_DELETE_ASSEMBLY_ONLY"/></td>
</tr>
</fieldset>
<tr>
<td height="10" class="spacer" colspan="2" >&nbsp;</td>
</tr>
</dmf:panel>
<dmf:panel name="linkoptions" visible="false">
<fieldset style="border:0px">
<tr>
<td class="fieldlabel" colspan="2"><legend><dmf:label nlsid="MSG_LINKS"/>:</legend></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deleteonlyfilelink" group="group1"/></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deletereference" group="group1"/></td>
</tr>
<tr>
<td height="10" class="spacer" colspan="2" >&nbsp;</td>
</tr>
</fieldset>
</dmf:panel>
<dmf:panel name="versionoptions" visible="false">
<fieldset style="border:0px">
<tr>
<td colspan="2" class="fieldlabel"><legend><dmf:label nlsid="MSG_VERSION"/>:</legend></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deleteselectedversion" group="group1" nlsid="MSG_DELETE_SELECTED_VERSION"/></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deleteallversions" group="group1" nlsid="MSG_DELETE_ALL_VERSIONS" /></td>
</tr>
<tr>
<td height="10" class="spacer" colspan="2" >&nbsp;</td>
</tr>
</fieldset>
</dmf:panel>
<dmf:panel name="vdmoptions">
<fieldset style="border:0px">
<tr>
<td colspan="2" width="100%" class="fieldlabel"><legend><dmf:label nlsid="MSG_VIRTUAL_DOCUMENTS"/>:</legend></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deletefilenotdescendents" group="group2" value="true" nlsid="MSG_DELETE_FILE_NOT_DESCENDENTS"/></td>
</tr>
<tr>
<td></td>
<td><dmf:radio name="deletefileanddescendents" group="group2" value="false" nlsid="MSG_DELETE_FILE_AND_DESCENDENTS"/></td>
</tr>
</fieldset>
</dmf:panel>
<tr>
<td class="fieldlabel" colspan="2"><dmf:label name="deletereplicamessage"/></td>
</tr>
<tr>
<td class="fieldlabel" colspan="2"><dmf:label name="deleteversionmessage"/></td>
</tr>
<% // %>
<tr>
<td height="10" class="spacer" colspan="2">&nbsp;</td>
</tr>
</table>
<dmfx:docbaseobject name="object"/>
</dmf:form>
</dmf:body>
</dmf:html>
