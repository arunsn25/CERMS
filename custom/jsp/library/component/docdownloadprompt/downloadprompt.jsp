<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ page import="com.documentum.web.form.Form" %>
<%@ page import="com.documentum.web.common.SessionState" %>
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
<td width="100%" colspan=4>
<dmf:label name='info_msg' nlsid='MSG_ENABLE_REMOVEPOLICY_MESSAGE' style="{font-family:Trebuchet MS, Verdana, GillSans, Arial;color: #333333;font-size: 13px;line-height: 14px;vertical-align: center; font-weight: normal;text-decoration: none}" />
</td>
</tr>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<%-- <td>
<dmf:image name='icon' src='/wdk/theme/documentum/icons/prompt/info.gif'/>
</td> --%>
<td class="spacer">&nbsp;</td>
<td>
 <dmf:label name='message' nlsid='MSG_MESSAGE' style="{font-family:Trebuchet MS, Verdana, GillSans, Arial;color: red;font-size: 12px;line-height: 14px;vertical-align: center; font-weight: bold;text-decoration: none}" />
</td>
<td>
		<%
/* 		     String sHref = null;
		     String sObjId = null;
		     String sFormat = null;
		     String sObjectNm = null;
		     try
		     {
		    	 System.out.println("In component - Session State OBJECTID: " + SessionState.getAttribute("OBJECTID"));
			     if ( SessionState.getAttribute("OBJECTID") != null )
			     {
			     	sObjId = (String)SessionState.getAttribute("OBJECTID");
			     	
			     	sHref = request.getContextPath() + "/component/exportwithoutpolicy?objectId=" + sObjId;
			     }
			     if ( SessionState.getAttribute("FORMAT") != null )
			     {
			     	sFormat = (String)SessionState.getAttribute("FORMAT");
			     	sHref = sHref + "&format=" + sFormat;
			     }
			     sObjectNm = (String)SessionState.getAttribute("OBJECT_NAME");
			 }
			 catch( Exception ee)
			 {
			 
			 }
			 finally
			 {
			 	SessionState.removeAttribute("OBJECTID");
			 	SessionState.removeAttribute("OBJECT_NAME");
			 } */
		%>
			 <%-- <a href="<%=sHref %>"> Download document </a> --%>
			 <%-- <input type="button" value="OK" onclick="parent.window.close();window.open('<%=sHref %>')" style="{font-family:Trebuchet MS, Verdana, GillSans, Arial;color: #333333;font-size: 12px;line-height: 14px;vertical-align: center; font-weight: normal;text-decoration: none}" /> --%> 
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
