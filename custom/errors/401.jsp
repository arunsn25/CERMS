<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
// add to session, which is used to log users who had 401s
// this is read in SearchComponent.onInit
//HttpSession sess=request.getSession();
//sess.setAttribute("had401","yes");


// construct redirect url
String strContextPath = request.getContextPath();
String query=request.getQueryString();
String redir=strContextPath+"/component/cosdsearchentry"+(query!=null?"?"+query:"");
%>
<html>
<title>Session Expired...</title>
<head>
<link rel="stylesheet" href="<%= strContextPath %>/webtop/theme/documentum/css/webtop.css" type="text/css">
<style>
#redirMsg {
  text-align: center;
}
a {
  text-decoration: none;
}
</style>
<script>
function doOnLoad() {
  // show the redirect url after timeout
  setTimeout("showDiv()",5000);
  //top.location.href='<%= redir %>';
}
function showDiv() {
	var msgDiv=document.getElementById('redirMsg');
	msgDiv.style.display='';
}
</script>
</head>
<body id="modalSmall" onload="doOnLoad()">
<div id="redirMsg" style="display: none">
Your session has expired. Please refresh the browser.
</div>
<noscript>
JavaScript is disabled.  Please enable JavaScript and login
manually on the <a href="<%= redir %>">login page</a>.
</noscript>
</body>
</html>