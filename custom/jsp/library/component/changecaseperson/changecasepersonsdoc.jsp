<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.formext.component.Component"%>
<%@ page import="com.cosd.greenbuild.calwin.web.library.changecasepersons.ChangeCasePersonsDocComponent"%>
<%@ page import="com.documentum.web.form.control.databound.Datagrid"%>
<%@ page import="com.documentum.web.form.control.databound.DatagridRow,
com.documentum.web.form.control.format.DateValueFormatter"%>
<%@ page import="com.documentum.web.form.Control"%>
<%@ page import="com.documentum.web.form.control.Checkbox"%>
<%@ page import="java.util.Iterator"%>

<dmf:html>
<dmf:head>
<dmf:webform />
<% 
ChangeCasePersonsDocComponent compt = (ChangeCasePersonsDocComponent) pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
%>
<%-- <script type="text/javascript" src='<%=Form.makeUrl(request, "/wdk/include/dynamicAction.js")%>'></script> --%>
<script type="text/javascript">
//Pop Up warning Message for Multiple Selection
 //registerClientEventHandler(null, "warn_msg", WarnMsg);
 //registerClientEventHandler(null, "eform_warn_msg", eformWarnMsg);
var multiselectGroupTbl = new Array(); 
var assignErrorMsg = "Please select only one Case Person record";
function WarnMsg(msg)
{
  alert(msg);	
}
function eformWarnMsg(msg)
{
  alert(msg);	
}
<%-- function unCheck() 
{
	var val = <%=compt.getERMSCheckBox()%>;
	var count = <%=compt.getButtonFlag()%>;
	var bodyControls = window.document.all;
	var element = null;
	var elemName = null;

	if(val)
	{
		//var bodyControls = window.document.all; 
 		for(var i = 0 ; i < bodyControls.length ; ++i) 
 		{ 
     		element = bodyControls.item(i);
    		elemName = element.name;                       
     		if(elemName != null)                              
     		{
          		if((elemName.toLowerCase().indexOf("ermscheck")!=-1)) 
          		{
			 		element.checked = false;
          		}
     		}     		
 		}
 		// Clear the selected checkbox values in the add / type section...
 		<%
 		if (compt.getERMSCheckBox())
 			compt.clearFields();
 		%>	 		
	}
	else
	{
 		for(var j = 0 ; j < bodyControls.length ; ++j) 
 		{ 
     		element = bodyControls.item(j);
    		elemName = element.name;                       
     		if(elemName != null)                              
     		{
     			if(element.type == "button")
         		{
          			if((elemName == "AddtypesContainer_add_0") && count == 1 ) 
          			{
          				element.disabled = "disabled";
          			}
          			else
          			{
          				element.disabled = "";
              		}
         		}
     		}
 		}
 		// Reset the flag to true so that the check boxes are unchecked as soon as the page refreshes...
 		<%
 		compt.setERMSCheckBox(true);
 		%>
	}  
} --%>

function flagErrorMsg(){
	var errFlag = <%=compt.getErrorFlag()%>;
	if (errFlag) {
		//errorMessage();
		errorMessage(assignErrorMsg);
	}
}

function errorMessage(errMsg) {
<%-- 	var errMsg = <%=compt.errMsg%>; --%>
	//alert("Please select only one Case Person record");
	alert(errMsg);
}

function onSelectObject(event)
{	
	var objectIds; //= "0000000000000000,"; 
	var objectId;
	var name;
	var dob;
	var ssn;
	var cin;
	var cwin;
	var datagrid = event.datagrid;
	var selection = datagrid.selection;
	var itemCount = datagrid.getRowCount();
	//var startIndex = event.startIndex;
	//var endIndex = startIndex + event.count - 1;
	var selectedCount = 0;
	var addObjIds = document.getElementById("addObjIds");
	var isErrorMsgSet=false;
	
	for (var i = 0; i < itemCount; i++)
	{
		var bSelected = selection.isSelected(i);
		if (bSelected) {
			selectedCount += 1;
		}
	}
	
	if (selectedCount==0){
		addObjIds.value="";
	}
	
	
	var counter = 0;
	for (var i = 0; i < itemCount; i++)
	{
		var bSelected = selection.isSelected(i);
		if (bSelected) {
			//datagrid.selection.setSelected(i, false);
	 		counter += 1;
			var args = datagrid.data.getItemActionArgs(i, event.type); // grid.getFocusIndex()
	 		objectId = args[0];
			name = args[2];
			dob = args[3]; 
			ssn = args[4]; 
			cin = args[5]; 
			cwin = args[6];
	 		if(args != null)
			{
	 			objectIds = name + ";" + dob + ";" + ssn + ";" + cin + ";" + cwin + ";" + counter;
	 			//alert(objectIds);
	 			addObjIds.value = objectIds;
	 			
	 			if (counter > 1 && isErrorMsgSet==false) {
<%-- 	 				var errMsg = <%=compt.errMsg%>;
	 				alert("Please select only one Case Person record");
	 				//alert(errMsg); --%>
	 				errorMessage(assignErrorMsg);
	 				isErrorMsgSet=true;
	 			}
			}
		}
	}
}

	
</script>
</dmf:head>
<body marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0' onLoad="flagErrorMsg()"> <%-- onLoad="unCheck()" --%>
<% 
//ChangeCasePersonsDocComponent compt = (ChangeCasePersonsDocComponent) pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
%>
<%-- <TABLE width="100%" cellpadding='0' cellspacing='0' border='0'>
 <TR>
	<TD width='25%' nowrap><dmf:label nlsid="MSG_SECTION_TYPE" cssclass="fieldlabel"/></TD>
	<TD valign='top'><dmf:dropdownlist name="section_type" onselect="populateDocumentType"/></TD>
 </TR>
  <TR>
	<TD width='25%' nowrap><dmf:label nlsid="MSG_DOCUMENT_TYPE" cssclass="fieldlabel"/></TD>
	<TD valign='top'><dmf:dropdownlist name="document_type" onselect='populateDocument_SubType' /></TD>
  </TR>
</TABLE> --%>

<%-- <TABLE width="100%" cellpadding='0' cellspacing='0' border='0'>
<TR>
	<TD width='20%' nowrap><dmf:label name='title' nlsid="MSG_DOCUMENT_SUBTYPE_TITLE" cssclass="fieldlabel"/></TD>
</TR>
</TABLE> --%>
<dmf:form>
<%
ChangeCasePersonsDocComponent form = (ChangeCasePersonsDocComponent)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
Datagrid datagrid = ((Datagrid)form.getControl(ChangeCasePersonsDocComponent.CONTROL_GRID, Datagrid.class));
%>
<%-- <div style="overflow:auto; height: 300px; white-space:nowrap"> --%>
<dmf:datagrid name="<%=ChangeCasePersonsDocComponent.CONTROL_GRID%>" paged="true" rowselection="true" pagesize="10">

 	<dmf:datagridTh>
		<dmf:datasortlink label="Name" column="LASTNAME" name="Name" />
	</dmf:datagridTh> 
	<dmf:datagridTh>
		<dmf:datasortlink label="DOB" column="dob" name="DOB" />
	</dmf:datagridTh>
	<dmf:datagridTh>
		<dmf:datasortlink label="SSN" column="ssn" name="SSN" mode="numeric" />
	</dmf:datagridTh>
	<dmf:datagridTh>
		<dmf:datasortlink label="CIN" column="cin" name="CIN" />
	</dmf:datagridTh>
	<dmf:datagridTh>
		<dmf:datasortlink label="CWIN" column="cwin" name="CWIN" />
	</dmf:datagridTh>
	
<dmf:hidden name='addObjIds' id='addObjIds'/>
	
<dmf:datagridRow align='center'>

					<dmf:datagridRowEvent eventname="select" eventhandler="onSelectObject" runatclient="true">
						<dmf:argument name='objectId' datafield='r_object_id'/>
						<dmf:argument name='type' datafield='r_object_type'/>
						<%-- <dmf:argument name='name' datafield='name'/> --%>
						
 						<% if (!ChangeCasePersonsDocComponent.bDataFromRegTable) 
						{
						%>
							<dmf:argument name='name' datafield='name'/>
						<% 
						} else 
						{ %>
							<%
							%>
							<%-- <dmf:argument name='name' value='<%= strComputedName %>'/> --%>
							<dmf:argument name='name' datafield='name'/>
						<% 
						} 
						%>
						
						<dmf:argument name='dob' datafield='dob'/>
						<dmf:argument name='ssn' datafield='ssn'/>
						<dmf:argument name='cin' datafield='cin'/>
						<dmf:argument name='cwin' datafield='cwin'/>
					</dmf:datagridRowEvent>
 
 	<dmf:datagridRowTd nowrap='true'>
<%-- 		<dmf:checkbox name='ermscheck' id="ermschkbox" value='false' >
				<dmf:argument name='chkboxId' datafield='r_object_id' />
		</dmf:checkbox> --%>

		<% if (!ChangeCasePersonsDocComponent.bDataFromRegTable) 
		{
		%>
		<dmf:label datafield='name'/>
		<% 
		} else 
		{ %>
		<%
		String strName = datagrid.getDataProvider().getDataField("name");
		String strdob = datagrid.getDataProvider().getDataField("dob");
		String strSSN = datagrid.getDataProvider().getDataField("ssn");
		String strCIN = datagrid.getDataProvider().getDataField("cin");
		String strCWIN = datagrid.getDataProvider().getDataField("cwin");
		
		%>
		<dmf:label label='<%= strName %>'/>
		<%-- <dmf:argument name='name' datafield='name'/> --%>
		<% } %>
	</dmf:datagridRowTd>
	<dmf:datagridRowTd>
		<% if (!ChangeCasePersonsDocComponent.bDataFromRegTable) 
		{
		%>
		<dmf:label datafield='dob'/>
		<% 
		} else 
		{ %>
		<%
		%>
		<%-- <dmf:label label='<%= strDOBComputedDate %>'/> --%>
		<dmf:label datafield='dob'/>
		<% } %>
	</dmf:datagridRowTd>
	<dmf:datagridRowTd>
		<dmf:label datafield="ssn"/>
	</dmf:datagridRowTd>
	<dmf:datagridRowTd>
		<dmf:label datafield="cin"/>
	</dmf:datagridRowTd>
	<dmf:datagridRowTd>
		<dmf:label datafield="cwin"/>
	</dmf:datagridRowTd>
</dmf:datagridRow>
	<td class="rightAlignment"><dmf:datapaging name="gridPagers" style="color:red;" ></dmf:datapaging></td>
	<%-- <td class="rightAlignment"><dmf:datapagesize name="pageSize" pagesizevalues="5,10,25"></dmf:datapagesize></td> --%>
</dmf:datagrid>
<%-- </div> --%>
</dmf:form>
</body>
</dmf:html>




