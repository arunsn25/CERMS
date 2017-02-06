<%--
--%>
<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.common.AccessibilityService"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<dmf:html>
<dmf:head>
<dmf:webform/>
<dmf:title><dmf:label  nlsid='MSG_TITLE_IMPORT_FILE_SELECTION'/>:<dmf:label nlsid='MSG_OBJECT'/></dmf:title>
</dmf:head>
<dmf:body id="modal" marginheight='0' marginwidth='12'
topmargin='0' bottommargin='0' leftmargin='12' rightmargin='0'>
<dmf:form autofocusneeded='true'>
<dmf:paneset name="mainPaneset" rows="59,*,45" cssclass='defaultPanesetBackground'
minheight="390" minwidth="550" toppadding="0" bottompadding="0">
<dmf:pane name="headerareaPane" overflow="hidden">
<dmfx:fragment src='modal/modalContainerStart.jsp'/>
<span class="dialogTitle"><dmf:label nlsid='MSG_TITLE_IMPORT_FILE_SELECTION'/>:&nbsp;
<dmf:label cssclass='dialogFileName' nlsid='MSG_OBJECT'/></span>
<dmfx:fragment src='modal/modalNavbarStart.jsp'/>
&nbsp;
<dmfx:fragment src='modal/modalNavbarEnd.jsp'/>
</dmf:pane>
<dmf:paneset name="contentareaPaneset" cols="18,*,18" cssclass="contentBackground">
<dmf:pane name="leftcolumn" overflow="hidden">
<dmfx:fragment src='modal/modalEdgesStart.jsp'/>
</dmf:pane>
<dmf:pane name="scrollingcontent" overflow="auto">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><dmf:label nlsid="MSG_SELECTED_FILES_FOLDERS"/>:</b>
<dmfx:fragment src='modal/modalContentGutterStart.jsp'/>

&nbsp;&nbsp;&nbsp;&nbsp;<b><dmf:label name="calwinpdfmsgtext" style="{COLOR:blue;font-size:x-small}" label="NOTE: Only PDF files can be imported."/></b>
<br/>
&nbsp;&nbsp;&nbsp;&nbsp;<b><dmf:label name="calwinpdfmsgtext" style="{COLOR:blue;font-size:x-small}" label="Non-PDF files will be ignored on clicking the NEXT button."/></b>
<br/>
&nbsp;&nbsp;&nbsp;&nbsp;<b><dmf:label name="calwinpdfwarningtext" style="{COLOR:red;font-size:small}" label="Only files of PDF format can be imported."/></b>

<dmf:activexautoactivate>
<dmf:fileselectorapplet name="fileselector" id="fileselector" height="250" width="400" folderselectmode="tree" accessibilityalertsneeded='<%=AccessibilityService.isAllAccessibilitiesEnabled()%>' folderselect='false'/>
</dmf:activexautoactivate>
<dmf:panel name="oleScanPanel">
<table cellspacing=0 cellpadding=0 border=0>
<tr><td>&nbsp;</td></tr>
<tr>
<td class="leftAlignment" valign="top" style="padding-left: 10px">
<dmf:checkbox name='oleScanEnable'/><dmf:label nlsid="MSG_FS_OLE_SCAN_ENABLE_DESCRIPTION"/>
</td>
</tr>
</table>
</dmf:panel>
<dmfx:fragment src='modal/modalContentGutterEnd.jsp'/>
</dmf:pane>
<dmf:pane name="rightcolumn" overflow="hidden">
<dmfx:fragment src='modal/modalEdgesEnd.jsp'/>
</dmf:pane>
</dmf:paneset>
<dmf:pane name="buttonareaPane" overflow="hidden">
<dmfx:fragment src='modal/modalButtonbarStart.jsp'/>
<dmf:button name='prev' style='color:#000000' cssclass="buttonLink" nlsid='MSG_PREV' onclick='onPrev'
height='16' tooltipnlsid='MSG_PREV_TIP'/>
<dmf:button name='next' cssclass='buttonLink' nlsid='MSG_NEXT' onclick='onNext'
height='16' tooltipnlsid='MSG_NEXT_TIP'/>
<dmf:button name='ok' default="true" cssclass="buttonLink" nlsid='MSG_OK' onclick='onOk'
height='16' tooltipnlsid='MSG_OK_TIP'/>
<dmf:button name='cancel' cssclass='buttonLink' nlsid='MSG_CANCEL' onclick='onCancel'
height='16' tooltipnlsid='MSG_CANCEL_TIP'/>
<dmfx:fragment src='modal/modalButtonbarEnd.jsp'/>
<dmfx:fragment src='modal/modalContainerEnd.jsp'/>
</dmf:pane>
</dmf:paneset>
</dmf:form>
</dmf:body>
</dmf:html>
