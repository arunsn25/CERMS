<%@ page import="com.documentum.web.formext.component.DialogContainer"%>
<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ page import="com.documentum.web.common.AccessibilityService"%>
<dmf:html>
<dmf:head>
<dmf:webform/>
<dmf:title><dmf:label nlsid='MSG_TITLE'/>&nbsp;:&nbsp;<dmf:label nlsid='MSG_OBJECT'/></dmf:title>
<% if (AccessibilityService.isAllAccessibilitiesEnabled()) { %>
<script type='text/javascript'>
if(window.frameElement)
{
window.frameElement.title="<dmf:label nlsid='MSG_TITLE'/>:<dmf:label nlsid='MSG_OBJECT'/>";
}
</script>
<% }  %>
</dmf:head>
<dmf:body id="modal" marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
<dmf:form>
<dmf:paneset name="mainPaneset" rows="59,*,45" cssclass='defaultPanesetBackground'
minheight="370" minwidth="400" toppadding="0" bottompadding="0">
<dmf:pane name="headerareaPane" overflow="hidden">
<dmfx:fragment src='modal/modalContainerStart.jsp'/>
<span class="dialogTitle dialogTitleTopMargin">
<dmf:panel name='<%=DialogContainer.NLSIDTITLEPANEL%>'>
<dmf:label nlsid='MSG_TYPE_TITLE'/>:&nbsp;
</dmf:panel>
<dmf:panel name='<%=DialogContainer.NLSIDSUBTITLEPANEL%>'>
<dmf:label cssclass='dialogFileName' nlsid='MSG_OBJECT'/>
</dmf:panel>
<dmf:panel name='<%=DialogContainer.PARAMTITLEPANEL%>'>
<dmf:label cssclass='dialogTitle' name='<%=DialogContainer.TITLE%>'/>:&nbsp;
</dmf:panel>
<dmf:panel name='<%=DialogContainer.PARAMSUBTITLEPANEL%>'>
<dmf:label cssclass='dialogFileName' name='<%=DialogContainer.SUBTITLE%>'/>:&nbsp;
</dmf:panel>
</span>
<dmfx:fragment src='modal/modalNavbarStart.jsp'/>
&nbsp;
<dmfx:fragment src='modal/modalNavbarEnd.jsp'/>
</dmf:pane>
<dmf:paneset name="contentareaPaneset" cols="18,*,18" cssclass="contentBackground">
<dmf:pane name="leftcolumn" overflow="hidden" printable="false">
<dmfx:fragment src='modal/modalEdgesStart.jsp'/>
</dmf:pane>
<dmf:pane name="scrollingcontent" overflow="auto">
<dmfx:fragment src='modal/modalContentGutterStart.jsp'/>
<dmfx:containerinclude/>
<dmfx:fragment src='modal/modalContentGutterEnd.jsp'/>
</dmf:pane>
<dmf:pane name="rightcolumn" overflow="hidden" printable="false">
<dmfx:fragment src='modal/modalEdgesEnd.jsp'/>
</dmf:pane>
</dmf:paneset>
<dmf:pane name="buttonareaPane" overflow="hidden">
<dmfx:fragment src='modal/modalButtonbarStart.jsp'/>
<dmf:button name='ok' cssclass="buttonLink" nlsid='MSG_CHOOSE' onclick='onOk'
height='16' tooltipnlsid="MSG_OK_TIP" id="exportBtn"/>

<!-- 
<dmf:button name='testDlgs' cssclass="buttonLink" nlsid='MSG_TEST' onclick='onTest'
height='16' tooltipnlsid="MSG_OK_TIP"/>
 -->
<dmf:button name='cancel' cssclass='buttonLink' nlsid='MSG_CANCEL' onclick='onAlternativeCancel'
height='16' tooltipnlsid="MSG_CANCEL_TIP"/>
<!-- 
<dmf:button name='cancel' cssclass='buttonLink' nlsid='MSG_CANCEL' onclick='onCancel'
height='16' tooltipnlsid="MSG_CANCEL_TIP"/>
<dmf:button name='close' cssclass="buttonLink" nlsid='MSG_CLOSE' onclick='onClose'
height='16' tooltipnlsid="MSG_CLOSE_TIP" />
 -->
<dmfx:fragment src='modal/modalButtonbarEnd.jsp'/>
<dmfx:fragment src='modal/modalContainerEnd.jsp'/>
</dmf:pane>
</dmf:paneset>
</dmf:form>
</dmf:body>
</dmf:html>
