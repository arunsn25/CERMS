<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ page import="com.documentum.web.form.Form" %>
<dmf:html>
<dmf:head>
   <dmf:webform/>
   <script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/wdk/include/dynamicAction.js")%>'></script>
   <%-- <dmf:title><dmf:label label='Remove'/></dmf:title> --%>   
</dmf:head>
<dmf:body marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0' bgcolor='#BDBDBD'>
<dmf:form>	
<table border='0' cellpadding='0' cellspacing='0' width='100%'>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<td>
	<dmf:label name='removedoclabel' nlsid='MSG_REMOVEDOC' style="{COLOR: red; FONT-SIZE: small}" />
</td>	
</tr>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
</table>
	
</dmf:form>
</dmf:body>

</dmf:html>
