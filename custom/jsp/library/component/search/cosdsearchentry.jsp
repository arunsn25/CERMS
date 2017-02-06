<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ page import="com.documentum.web.form.Form" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN"
"http://www.w3.org/TR/html4/frameset.dtd">
<html>
<head>
<dmf:webform/>
<title><dmf:label nlsid="MSG_TITLE"/></title>
<script type='text/javascript' src='<%=Form.makeUrl(request, "/wdk/include/timeoutControl.js")%>'></script>
<script type='text/javascript' src='<%=Form.makeUrl(request, "/wdk/include/unsavedChanges.js")%>'></script>
<script type='text/javascript'>
function onUnload()
{
promptIfUnsavedChangesExist();
manageTimeout(frames["timeoutcontrol"]);
}
</script>
</head>

<dmf:frameset rows='0,29,71' border='0' framespacing='0' frameborder='false' onunload='onUnload()'> <%-- 0,35,*,21 ; 5,23,67,5 ; 5,23,72, ; 2,38,60; 5,23,72 --%>
<dmf:frame nlsid="MSG_SYSTEM_USE" name='timeoutcontrol' src='/wdk/timeoutcontrol.jsp' marginwidth='0' marginheight='0' scrolling='no' noresize='true'/>
<%-- <dmf:frame nlsid="MSG_CLASSICVIEW" name='framebkgrndtop' src="/custom/jsp/FrameBackgrounds.jsp" scrolling="no" noresize="true"/> --%>
<dmf:frame nlsid="MSG_SEARCHBAR" name='titlebar' src='/component/cosdsearchframe' scrolling='yes' noresize='true'/>  
<dmf:frame nlsid="MSG_SEARCHVIEW" name='emptyfile' src='/custom/jsp/library/component/search/EmptyFile.jsp' scrolling='no' noresize='true' />
<%-- <dmf:frame nlsid="MSG_CLASSICVIEW" name='framebkgrndbtm' src="/custom/jsp/FrameBackgrounds.jsp" scrolling="no" noresize="true"/> --%>
</dmf:frameset>

</html>
