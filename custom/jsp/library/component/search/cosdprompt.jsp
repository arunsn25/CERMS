<%@ page import="com.documentum.web.common.AccessibilityService" %>
<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<html>
<head>
<dmf:webform/>
<title><dmf:label nlsid='MSG_TITLE'/></title>
</head>
<body id="modal" marginheight='0' marginwidth='0'
topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0' onload='javascript:beginModal()' onunload='javascript:endModal()' showdialogevent=true>
<dmf:form autofocusneeded='true'>
<dmf:paneset name="mainPaneset" rows="59,*,45" cssclass='defaultPanesetBackground'
toppadding="0" bottompadding="0" leftpadding="70" rightpadding="70" minheight="300" minwidth="300">
<dmf:pane name="headerareaPane" overflow="hidden">
<dmfx:fragment src='modal/modalContainerStart.jsp'/>
<dmf:label name='title' cssclass='dialogTitle' nlsid='MSG_TITLE'/>
<dmfx:fragment src='modal/modalNavbarStart.jsp'/>
<dmf:label cssclass='doclistFolderPath' name="docbasePath"/>
<dmfx:fragment src='modal/modalNavbarEnd.jsp'/>
</dmf:pane>
<dmf:paneset name="contentareaPaneset" cols="18,*,18" cssclass="contentBackground">
<dmf:pane name="leftcolumn" overflow="hidden" printable="false">
<dmfx:fragment src='modal/modalEdgesStart.jsp'/>
</dmf:pane>
<dmf:pane name="scrollingcontent" overflow="auto">
<dmfx:fragment src='modal/modalContentGutterStart.jsp'/>
<table border="0" cellpadding="0" cellspacing="0" width='100%'>
<tr>
<td valign="top">
<% if (AccessibilityService.isAllAccessibilitiesEnabled())
{ %>
<dmf:button name='infobtn' onclick="doNothing" src="icons/info.gif" tooltipnlsid="MSG_MESSAGE" runatclient="true" />
<script type="text/javascript"> function doNothing(){ return false;} </script>
<% } %>
<dmf:image name='icon'/>
</td>
<td width="5" class="spacer">&nbsp;</td>
<td valign="top">
<dmf:label name='message' nlsid='MSG_MESSAGE'/>
</td>
</tr>
<tr>
<td></td>
<td colspan="2">
<br><dmf:checkbox name='dontshowagain' nlsid='MSG_DONTSHOWAGAIN'/>
</td>
</tr>
</table>
<dmfx:fragment src='modal/modalContentGutterEnd.jsp'/>
</dmf:pane>
<dmf:pane name="rightcolumn" overflow="hidden" printable="false">
<dmfx:fragment src='modal/modalEdgesEnd.jsp'/>
</dmf:pane>
</dmf:paneset>
<dmf:pane name="buttonareaPane" overflow="hidden">
<dmfx:fragment src='modal/modalButtonbarStart.jsp'/>
<dmf:button name='continue' cssclass="buttonLink" nlsid='MSG_CONTINUE' onclick='onContinue'
height='16'/>
<dmf:button name='ok' cssclass="buttonLink" nlsid='MSG_OK' onclick='onOk'
height='16'/>
<dmf:button name='cancel' cssclass="buttonLink" nlsid='MSG_CANCEL' onclick='onCancel'
height='16'/>
<dmf:button name='yes' cssclass="buttonLink" nlsid='MSG_YES' onclick='onYes'
height='16'/>
<dmf:button name='no' cssclass="buttonLink" nlsid='MSG_NO' onclick='onNo'
height='16'/>
<dmf:button name='yestoall' cssclass="buttonLink" nlsid='MSG_YESTOALL' onclick='onYesToAll'
height='16'/>
<dmf:button name='notoall' cssclass="buttonLink" nlsid='MSG_NOTOALL' onclick='onNoToAll'
height='16'/>
<dmfx:fragment src='modal/modalButtonbarEnd.jsp'/>
<dmfx:fragment src='modal/modalContainerEnd.jsp'/>
</dmf:pane>
</dmf:paneset>
</dmf:form>
<body>
</html>
