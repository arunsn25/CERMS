<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ taglib uri="/WEB-INF/tlds/cosd/cosdform_1_0.tld" prefix="cosdf" %>
<%@ page import="com.documentum.webcomponent.library.search.result.SearchResultSet,
com.documentum.webcomponent.library.search.SearchInfo,
com.documentum.web.dragdrop.IDropTarget" %>
<%@ page import="com.documentum.web.form.Form" %>
<%@ page import="com.documentum.web.form.control.databound.Datagrid,
com.documentum.web.form.control.format.DateValueFormatter,
com.documentum.web.form.control.databound.DatagridRow,
java.util.Iterator,
com.documentum.web.form.Control,
com.documentum.web.formext.config.Context,
com.documentum.fc.client.IDfQuery,com.documentum.web.util.DfcUtils,com.documentum.fc.client.IDfCollection,
com.documentum.fc.common.DfException,com.documentum.web.common.WrapperRuntimeException,java.util.regex.Pattern" %>
<%@ page import="com.documentum.webcomponent.library.search.Search60"%>
<%@ page import="com.documentum.web.common.AccessibilityService"%>
<%@ page import="com.documentum.web.formext.control.cluster.SmartNavigationService" %>
<html>
<head>
<dmf:webform/>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/wdk/include/dynamicAction.js")%>'></script>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/wdk/include/windows.js")%>'></script>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/webcomponent/navigation/navigation.js")%>'></script>
<script>
function showQuery(strQueryId)
{
window.open('<%=Form.makeUrl(request, "/webcomponent/library/searchresultslist/searchex/showquery.jsp?queryId=")%>' + escape(strQueryId), "" + new Date().getTime(), g_strDefWinOptions);
}
var gRegisterShowQuery;
if (gRegisterShowQuery != "true")
{
gRegisterShowQuery = "true";
registerClientEventHandler(null, "showQuery", showQuery);
}


function onSelectObject(event)
{	
	var objectIds; //= "0000000000000000,"; 
	var objectId;
	var datagrid = event.datagrid;
	var selection = datagrid.selection;
	var itemCount = datagrid.getRowCount();
	//var startIndex = event.startIndex;
	//var endIndex = startIndex + event.count - 1;
	var selectedCount = 0;
	
	for (var i = 0; i < itemCount; i++)
	{
		var bSelected = selection.isSelected(i);
		if (bSelected) {
			selectedCount += 1;
		}
	}
	//alert("selectedCount is : " + selectedCount.toString());
	
	var counter = 0;
	for (var i = 0; i < itemCount; i++)
	{
		var bSelected = selection.isSelected(i);
		//alert(bSelected);
		//var checkbox = getProxyCheckbox(datagrid, i);
		//if (checkbox != null) {
			//checkbox.checked = bSelected;
		//}
		//else 
		if (bSelected) {
			//datagrid.selection.setSelected(i, false);
	 		counter += 1;
			var args = datagrid.data.getItemActionArgs(i, event.type); // grid.getFocusIndex()
	 		objectId = args[0];
			//alert(objectId);
	 		if(args != null)
			{
				// Set the 'addObjIds' hidden element with the object id selected
				
/* 				var addObjIdsSingle = document.getElementById("addObjIds");
				addObjIdsSingle.value = args[0];
				alert(addObjIdsSingle.value);
 */				
 
				var addObjIds = document.getElementById("addObjIds");
				//This loop handles the case of appending the last of the multiselect rows selected.
  		 		if ((counter == selectedCount) && (counter != 1)) {  
  		 			if (selectedCount == 2) {  // if only two rows were selected, then the last item is handled here.
  		 				objectIds += "," + objectId.toString();
  		 			} else {  // if more than two rows were selected, then the last item is handled here.
		 				objectIds += objectId.toString();
  		 			}
		 			//alert("Final: i == itemcount");
		 			//alert(objectIds);
		 			addObjIds.value = objectIds;
		 		} 
  		 	// All selected rows except the last one is handled here
  		 		else {  
		 			if (counter == 1){  // If only one row was selected, it is handled here.
			 			objectIds = objectId.toString();
			 			//alert("i=0 :: In ELSE clause");
			 			//alert(objectIds);
			 			addObjIds.value = objectIds;
		 			} 
		 		// If >2 rows were selected, then it is handled here. The case of only 2 rows being selected is handled by the "IF" clause above and the 'primary' "IF" clause
		 			else {
			 			objectIds += "," + objectId.toString() + ",";
			 			//alert("i>0 :: In ELSE clause");
			 			//alert(objectIds);
		 			}
		 		} 
			}
		}
	}
	
	
	
	
	//var grid = event.datagrid;
/* 	if (event.count == 1)
	{
 		var args = grid.data.getItemActionArgs(event.startIndex, event.type); // grid.getFocusIndex()
 		if(args != null)
		{
			// Set the 'addObjIds' hidden element with the object id selected
			var addObjIdsSingle = document.getElementById("addObjIds");
			addObjIdsSingle.value = args[0];
			//document.getElementById("argObjIdcosd").value = args[0];
			//alert(addObjIdsSingle.value);
		}
		//processSingle(args);
	} else {
		var objectIds; //= "0000000000000000,"; 
		var objectId;
		var addObjIds = document.getElementById("addObjIds");
		for (var i=0;i<event.count;i++){
			//alert("In For Loop...");
			//alert(event.count);
	 		var args = grid.data.getItemActionArgs(event.startIndex + i, event.type);
	 		objectId = args[0]; // processSingle(args);
	 		if (i == (event.count-1)){
	 			objectIds = objectIds + objectId.toString();
	 			addObjIds.value = objectIds;
	 		} else {
	 			if (i == 0){
		 			objectIds = objectId.toString() + ",";
		 			//alert("In ELSE clause");
		 			//alert(objectIds);
	 			} else {
		 			objectIds += objectId.toString() + ",";
		 			//alert("In ELSE clause");
		 			//alert(objectIds);
	 			}
	 		}
		}
		//alert(addObjIds.value);	
		//processMultiple(event, grid);
	} */
}

function processMultiple(event, grid) {
	//alert('in process multiple, count is: ' + event.count);
	var objectIds = "0000000000000000,"; 
	var objectId;
	var addObjIds = document.getElementById("addObjIds");
	for (var i=0;i<event.count;i++){
		//alert("In For Loop...");
		//alert(event.count);
 		var args = grid.data.getItemActionArgs(event.startIndex + i, event.type);
 		objectId = processSingle(args);
 		if (i == (event.count-1)){
 			objectIds = objectIds + objectId.toString();
 			addObjIds.value = objectIds;
 		} else {
 			if (i == 0){
	 			objectIds = objectId.toString() + ",";
	 			//alert("In ELSE clause");
	 			//alert(objectIds);
 			} else {
	 			objectIds += objectId.toString() + ",";
	 			//alert("In ELSE clause");
	 			//alert(objectIds);
 			}
 		}
	}
	//alert(addObjIds.value);
}


function processSingle(args){

	if(args != null)
	{
		// Set the 'addObjIds' hidden element with the object id selected
		var addObjIds = document.getElementById("addObjIds");
		addObjIds.value = args[0];
		//alert(addObjIds.value);
		//alert("In Process Single...");
		//alert(args[0]);
		return args[0];
	}
}



</script>
</head>
<body class='contentBackground' marginheight='0' marginwidth='0'
topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
<dmfx:dragdrop/>
<dmf:form>
<%
Search60 form = (Search60)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
Datagrid datagrid = ((Datagrid)form.getControl(Search60.CONTROL_GRID, Datagrid.class));
%>
<table class='headerBackground' cellspacing='0' cellpadding='0' border='0' width="100%" style='display:none'> <%-- headerBackground --%>
<tr>
<td colspan="23">
<table cellspacing=0 cellpadding=0 border=0 width="100%">
<tr valign="middle">
<td class="leftAlignment" height="30">
<div>
<dmf:label name='<%=Search60.CONTROL_QUERY_TITLE%>' cssclass='webcomponentTitle'/>
</div>
<div class="floatLeftAlignment">
<dmf:image name='<%=Search60.CONTROL_PROCESSING_SEARCH%>' id='<%=Search60.CONTROL_PROCESSING_SEARCH%>'
onclick="onClickStatus" alttext='<%=form.getString("MSG_PROCESSING_TIP")%>'/>
</div>
<%--query desc--%>
<div style="vertical-align: top; padding: 6px 50px 0; margin-left: -50px;">
<dmf:label id='<%=Search60.CONTROL_RESULT_COUNT%>' name='<%=Search60.CONTROL_RESULT_COUNT%>'
cssclass='drilldownLabel'/>
<dmf:stringlengthformatter maxlen='<%=Search60.LABEL_MAXLEN%>'>
<dmf:label id='<%=Search60.CONTROL_QUERY_DESCRIPTION%>'
name='<%=Search60.CONTROL_QUERY_DESCRIPTION%>' cssclass='drilldownLabel'/>
</dmf:stringlengthformatter>
</div>
</td>
<td>
<%-- <dmfx:actionbutton name='changeproc1' nlsid='MSG_MEMBERS' action='properties' showifdisabled='false'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
</dmfx:actionbutton> --%>
</td>
<td valign="middle" class="rightAlignment" nowrap>
<table cellspacing='0' cellpadding='3' border='0' class='searchResultsBackground floatRightAlignment'>
<tr align="center">
<%--stop search--%>
<td width="54">
<div >
<%if(!AccessibilityService.isAllAccessibilitiesEnabled()){%>
<a href="#">
<dmf:image name='<%=Search60.CONTROL_START_STOP_SEARCH%>'
id='<%=Search60.CONTROL_START_STOP_SEARCH%>' cssclass='actionimage'/>
</a>
<%}%>
<a id="stopStartLink" class="drilldownLabel" href="#">
<nobr id="stopStartBr"><%=form.getString("MSG_STOP_SEARCH")%>
</nobr>
</a>
</div>
</td>
<td>
<% if(!AccessibilityService.isAllAccessibilitiesEnabled())
{ %>
<dmfx:actionimage action='<%=Search60.EDIT_SEARCH_ACTION%>'
name='<%=Search60.CONTROL_EDIT_SEARCH_ACTION%>'
showifinvalid='false' showifdisabled='false' srcdisabled='' showlabel='true'
nlsid='MSG_EDIT' cssclass='drilldownLabel'
src='<%=Search60.ICON_EDIT_SEARCH%>'
alttext='<%=form.getString("MSG_REVISE")%>'
tooltipnlsid='MSG_REVISE'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% }
else
{ %>
<dmfx:actionimage action='<%=Search60.EDIT_SEARCH_ACTION%>'
name='<%=Search60.CONTROL_EDIT_SEARCH_ACTION%>'
showifinvalid='false' showifdisabled='false' showlabel='true'
nlsid='MSG_EDIT' cssclass='drilldownLabel' showimage="false"
src='<%=Search60.ICON_EDIT_SEARCH%>'
alttext='<%=form.getString("MSG_REVISE")%>'
tooltipnlsid='MSG_REVISE'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% } %>
</td>
<td>
<% if(!AccessibilityService.isAllAccessibilitiesEnabled())
{ %>
<dmfx:actionimage action='<%=Search60.EDIT_TEMPLATE_ACTION%>'
name='<%=Search60.CONTROL_EDIT_TEMPLATE_ACTION%>'
showifinvalid='false' showifdisabled='false' srcdisabled='' showlabel='true'
nlsid='MSG_EDIT' cssclass='drilldownLabel'
src='<%=Search60.ICON_EDIT_SEARCH%>'
alttext='<%=form.getString("MSG_REVISE")%>'
tooltipnlsid='MSG_REVISE'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% }
else
{ %>
<dmfx:actionimage action='<%=Search60.EDIT_TEMPLATE_ACTION%>'
name='<%=Search60.CONTROL_EDIT_TEMPLATE_ACTION%>'
showifinvalid='false' showifdisabled='false' srcdisabled='' showlabel='true'
nlsid='MSG_EDIT' cssclass='drilldownLabel' showimage="false"
src='<%=Search60.ICON_EDIT_SEARCH%>'
alttext='<%=form.getString("MSG_REVISE")%>'
tooltipnlsid='MSG_REVISE'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% } %>
</td>
<td>
<%--save search--%>
<% if(!AccessibilityService.isAllAccessibilitiesEnabled())
{ %>
<dmfx:actionimage action='<%=Search60.SAVE_SEARCH_ACTION%>'
name='<%=Search60.CONTROL_SAVE_SEARCH_ACTION%>'
nlsid='MSG_SAVE_SEARCH' cssclass='drilldownLabel' showlabel='true'
src='<%=Search60.ICON_SAVE_SEARCH%>'
alttext='<%=form.getString("MSG_SAVE_SEARCH_TIP")%>'
tooltipnlsid='MSG_SAVE_SEARCH_TIP'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% }
else
{ %>
<dmfx:actionimage action='<%=Search60.SAVE_SEARCH_ACTION%>'
name='<%=Search60.CONTROL_SAVE_SEARCH_ACTION%>'
nlsid='MSG_SAVE_SEARCH' cssclass='drilldownLabel' showlabel='true'
src='<%=Search60.ICON_SAVE_SEARCH%>' showimage="false"
alttext='<%=form.getString("MSG_SAVE_SEARCH_TIP")%>'
tooltipnlsid='MSG_SAVE_SEARCH_TIP'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% } %>
</td>
<td>
<%--save search as template--%>
<% if(!AccessibilityService.isAllAccessibilitiesEnabled())
{ %>
<dmfx:actionimage action='savesearchtemplate'
name='<%=Search60.CONTROL_SAVE_SEARCH_TEMPLATE_ACTION%>'
showifinvalid='false' showifdisabled='false' srcdisabled='' showlabel='true'
nlsid='MSG_SAVE_SEARCH_TEMPLATE' cssclass='drilldownLabel'
src='<%=Search60.ICON_SAVE_SEARCH_TEMPLATE%>'
alttext='<%=form.getString("MSG_SAVE_SEARCH_TEMPLATE_TIP")%>'
tooltipnlsid='MSG_SAVE_SEARCH_TEMPLATE_TIP'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% }
else
{ %>
<dmfx:actionimage action='savesearchtemplate'
name='<%=Search60.CONTROL_SAVE_SEARCH_TEMPLATE_ACTION%>'
showifinvalid='false' showifdisabled='false' srcdisabled='' showlabel='true'
nlsid='MSG_SAVE_SEARCH_TEMPLATE' cssclass='drilldownLabel' showimage="false"
src='<%=Search60.ICON_SAVE_SEARCH_TEMPLATE%>'
alttext='<%=form.getString("MSG_SAVE_SEARCH_TEMPLATE_TIP")%>'
tooltipnlsid='MSG_SAVE_SEARCH_TEMPLATE_TIP'>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>'
contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
<% } %>
</td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
<dmf:panel name="<%=Search60.CLUSTER_BREADCRUMB_PANEL%>">
<tr class='contentBackground'>
<td colspan='23' height='1' class='spacer'></td>
</tr>
<tr>
<td colspan=23 height="24">
<table width=100% border=0 cellspacing=0 cellpadding=0>
<tr>
<td>
<table border=0 cellspacing=0 cellpadding=0>
<tr>
<td nowrap>&nbsp;
<dmf:link name="<%=Search60.CONTROL_LINK_ALLRESULTS%>" onclick='onClickAllResults'
cssclass='drilldownLabel'/>
</td>
<td nowrap>&nbsp;
<dmf:label name="<%=Search60.FST_SEPARATOR_LABEL%>" cssclass="drilldownLabel"/>
</td>
<dmf:panel name="<%=Search60.SUB_LINK_PANEL%>">
<td nowrap>&nbsp;
<dmf:link name="<%=Search60.CONTROL_LINK_CLUSTER%>" onclick="onClickClusterLink"
cssclass='drilldownLabel'/>
</td>
<td nowrap>&nbsp;
<dmf:label name="<%=Search60.SND_SEPARATOR_LABEL%>" cssclass="drilldownLabel"/>
</td>
</dmf:panel>
<td nowrap>&nbsp;
<dmf:label name="<%=Search60.CLUSTER_LABEL%>" cssclass="drilldownLabel"/>
</td>
</tr>
</table>
</td>
<td nowrap class="rightAlignment" >&nbsp;&nbsp;</td>
</tr>
</table>
</td>
</tr>
</dmf:panel>
</table>
<dmfx:actionmultiselect name='multi' selectionargs='objectId,<%=SearchInfo.ENTRYID_PARAM%>,<%=SearchInfo.QUERYID_PARAM%>'
selectiongroupargs='<%=SearchResultSet.ATTR_DOCBASE_ID%>'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
<dmfx:argument name='a_content_type' contextvalue='a_content_type'/>
<dmf:argument name='startworkflowId' value='startworkflow'/>
<dmf:argument name='<%=SearchInfo.ENTRYID_PARAM%>' datafield='<%=SearchResultSet.ATTR_KEY%>'/>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>' contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
<%-- <dmf:datagrid name='<%=Search60.CONTROL_GRID%>' paged='true' preservesort='false' width='100%' cellspacing='0' cellpadding='0' bordersize='0' rowselection="true" fixedheaders="true" focus="true" > --%>
<dmf:datagrid name='<%=Search60.CONTROL_GRID%>' paged='true' preservesort='false' width='100%' cellspacing='0' cellpadding='0' bordersize='0' rowselection="true" fixedheaders="true" focus="true" >
<tr class='pagerBackground'>
<td colspan="40" height="24">
<table width=100% border=0 cellspacing=0 cellpadding=0>
<tr>
<dmf:panel name='<%=Search60.CONTROL_HIGHLIGHT%>' visible='false' >
<td nowrap class="leftAlignment">&nbsp;</td>
<td nowrap class="leftAlignment">
<dmf:image name='<%=Search60.CONTROL_HIGHLIGHT_BUTTON%>' alttext='<%=form.getString("MSG_TOGGLEHIGHLIGHT_TIP")%>' onclick="onToggleHighlight" height='16' />
</td>
</dmf:panel>
<td>
<table width="100%">
<tr>
<td style="float:left"><dmf:label nlsid='MSG_SHOW_ITEMS'/>&nbsp;<dmf:datapagesize name='<%=Search60.CONTROL_DATAPAGESIZE%>' preference='application.display.classic' tooltipnlsid='MSG_SHOW_ITEMS' />&nbsp;</td>
<%-- <td style="float:right">&nbsp;&nbsp;&nbsp;&nbsp;</td> --%>
<td style="float:left" ><dmf:datapaging name='<%=Search60.CONTROL_PAGER%>' gotopageclass='doclistPager'/></td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td colspan='23' height='1' class='spacer'>&nbsp;</td>
</tr>
<%
if (AccessibilityService.isAllAccessibilitiesEnabled())
{
%>
<table width=100% border=0 cellspacing=0 cellpadding=0 summary='Data'>
<%
}
%>
<%-- <dmf:paneset name="mainPaneset" rows="*,20" > --%>
<%-- <dmf:pane name="scrollingcontent" overflow="auto"> --%>
<tr class='colHeaderBackground' align='center'>
<dmf:datagridTh scope='col' nowrap="true" cssclass='doclistcheckbox leftAlignment'>
<% if  (datagrid.getDataProvider().getResultsCount() >0 ) {%>
<dmfx:actionmultiselectcheckall cssclass='doclistbodyDatasortlink'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
<dmfx:argument name='folderId' contextvalue='folderId'/>
<dmfx:argument name='a_content_type' contextvalue='a_content_type'/>
<dmf:argument name='<%=SearchInfo.ENTRYID_PARAM%>' datafield='<%=SearchResultSet.ATTR_KEY%>'/>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>' contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionmultiselectcheckall>
<% } %>
</dmf:datagridTh>
<dmf:datagridTh scope='col' nowrap="true" cssclass='doclistlocicon leftAlignment'>
<dmf:datasortimage name='sortimg' datafield='r_lock_owner' cssclass='doclistbodyDatasortlink' reversesort='true' image='icons/sort/sortByLock.gif' visible='false'/>
</dmf:datagridTh>
<dmf:celllist name='<%=Search60.CONTROL_COLUMNS_CELLLIST%>'>
<dmf:celltemplate field='object_name'>
<dmf:datagridTh scope='col' nowrap="true" cssclass='doclistfilenamedatagrid objectlistheaderspacing leftAlignment' resizable="true">
<dmf:datasortlink name='sort1' datafield='object_name' cssclass='doclistbodyDatasortlink'/>
</dmf:datagridTh>
<dmf:datagridRowModifier>
<dmf:datagridTh scope='col' align='center' nowrap="true" cssclass='spacer'>
<dmf:image name='prop' nlsid='MSG_PROPERTIES' src='images/space.gif'/>
</dmf:datagridTh>
</dmf:datagridRowModifier>
</dmf:celltemplate>
<dmf:celltemplate field='attachment_count'>
<dmf:datagridTh scope='col' align='center' cssclass='doclisticon'>
<dmf:datasortimage name='attachmentcount' datafield='CURRENT' cssclass='doclistbodyDatasortlink'
image='icons/indicator/Attachmentheader_16.gif'/>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate field='score'>
<dmf:datagridTh scope='col' cssclass='doclistfilenamedatagrid leftAlignment' resizable="true">
<nobr><dmf:datasortlink name='sortScore' datafield='score' cssclass='doclistbodyDatasortlink' reversesort="true" mode='numeric'/></nobr>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate field='topic_status'>
<dmf:datagridTh scope='col' cssclass='doclisticon leftAlignment'>
<dmf:datasortimage name='sorttopic' datafield='topic_status' cssclass='doclistbodyDatasortlink'
image='icons/sort/sortByDisc.gif'/>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate field='room_status'>
<dmf:datagridTh scope='col' align='center' cssclass='doclisticon'>
<dmf:datasortimage name='sortroom' datafield='room_status' cssclass='doclistbodyDatasortlink'
image='icons/sort/sortByRoom.gif'/>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate field='title'>
<dmf:datagridTh scope='col' cssclass='doclistfilenamedatagrid leftAlignment' resizable="true">
<dmf:datasortlink name='sort2' datafield='title' cssclass='doclistbodyDatasortlink'/>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate field='authors'>
<dmf:datagridTh scope='col' cssclass='doclistfilenamedatagrid leftAlignment' resizable="true">
<dmf:datasortlink name='sort3' datafield='authors' cssclass='doclistbodyDatasortlink'/>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate type='number'>
<dmf:datagridTh scope='col' cssclass='doclistfilenamedatagrid leftAlignment' resizable="true">
<nobr><dmf:datasortlink name='sort4' datafield='CURRENT' mode='numeric' cssclass='doclistbodyDatasortlink'/></nobr>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate type='date'>
<dmf:datagridTh scope='col' cssclass='doclistfilenamedatagrid' resizable="true" align='center'> <!--  leftAlignment -->
<nobr><dmf:datasortlink name='sort5' datafield='CURRENT' mode='numeric' cssclass='doclistbodyDatasortlink'/></nobr>
</dmf:datagridTh>
</dmf:celltemplate>
<dmf:celltemplate field='summary'>
</dmf:celltemplate>
<dmf:celltemplate>
<dmf:datagridTh scope='col' cssclass='doclistfilenamedatagrid'  resizable="true" align='center'> <!-- leftAlignment -->
<nobr><dmf:datasortlink name='sort6' datafield='CURRENT' cssclass='doclistbodyDatasortlink'/></nobr>
</dmf:datagridTh>
</dmf:celltemplate>
</dmf:celllist>
<dmf:datagridTh valign="middle" cssclass="doclisticon">
<dmf:image name='<%=Search60.ICON_COLUMNS_PREF%>' src="icons/columnprefs_16.gif" nlsid="MSG_COLUMN_PREFERENCES" onclick="onClickColumnsPrefs" visible='false'>
<dmf:argument name="usemodalpopup" value="true"/>
<dmf:argument name="modalpopupwindowsize" value="large"/>
<dmf:argument name="refreshparentwindow" value="onok"/>
</dmf:image>
</dmf:datagridTh>
<dmf:datagridTh width="99%">&nbsp;</dmf:datagridTh>
</tr>

<dmf:hidden name='addObjIds' id='addObjIds'/>
<dmf:label name='searchlimitcontrollabel' id='searchlimitcontrollabel' style="{COLOR:red;align:left;font-size:200%}" label='There are more than 350 documents for this case number. Please use the Received Date to narrow down your results.' />

<dmf:datagridRow tooltipdatafield='object_name' cssclass='defaultDatagridRowStyle' altclass="defaultDatagridRowAltStyle">
<dmf:datagridRowTd height='24' nowrap="true" cssclass="doclistcheckbox">
<dmf:panel datafield="<%=SearchResultSet.ATTR_IS_CONNECTED%>">
<dmfx:actionmultiselectcheckbox name='check' value='false' cssclass='actions'>
<dmf:argument name='objectId' datafield='r_object_id'/>
<dmf:argument name='type' datafield='r_object_type'/>
<dmf:argument name='lockOwner' datafield='r_lock_owner'/>
<dmf:argument name='ownerName' datafield='owner_name'/>
<dmf:argument name='contentSize' datafield='r_full_content_size'/>
<dmf:argument name='contentType' datafield='a_content_type'/>
<dmf:argument name="isVirtualDoc" datafield='r_is_virtual_doc'/>
<dmf:argument name="linkCount" datafield='r_link_cnt'/>
<dmf:argument name='startworkflowId' value='startworkflow'/>
<dmf:argument name='workflowRuntimeState' value='-1'/>
<dmf:argument name='isReference' datafield='i_is_reference'/>
<dmf:argument name='isReplica' datafield='i_is_replica'/>
<dmf:argument name='assembledFromId' datafield='r_assembled_from_id'/>
<dmf:argument name='isFrozenAssembly' datafield='r_has_frzn_assembly'/>
<dmf:argument name='<%=SearchResultSet.ATTR_DOCBASE_ID%>' datafield='<%=SearchResultSet.ATTR_DOCBASE_ID%>'/>
<dmf:argument name='<%=SearchInfo.ENTRYID_PARAM%>' datafield='<%=SearchResultSet.ATTR_KEY%>'/>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>' contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
<dmf:argument name='topicStatus' datafield='topic_status'/>
<dmf:argument name='isRemoved' datafield='soft_delete'/>
</dmfx:actionmultiselectcheckbox>
</dmf:panel>
</dmf:datagridRowTd>

					<dmf:datagridRowEvent eventname="select" eventhandler="onSelectObject" runatclient="true">
						<dmf:argument name='objectId' datafield='r_object_id'/>
						<dmf:argument name='type' datafield='r_object_type'/>
						<dmf:argument name='isFolder' datafield='isfolder'/>
						<dmf:argument name='objectName' datafield='object_name'/>
						<%-- <dmf:argument name='case_number' datafield='case_no'/> --%>
					</dmf:datagridRowEvent>



<dmf:datagridRowTd nowrap="true" cssclass="doclistlocicon">
<dmfx:docbaselockicon datafield='r_lock_owner' size='16' visible='false'/>
</dmf:datagridRowTd>
<dmf:celllist>
<dmf:celltemplate field='provider_number'>
<dmf:datagridRowTd nowrap="true" scope='row' cssclass='doclistfilenamedatagrid'>
<dmf:termshighlightingformatter datafield='<%=SearchResultSet.ATTR_THH%>' cssclass='termshighlight' separator='<%=SearchResultSet.THH_SEPARATOR%>'  visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmf:stringlengthformatter maxlen='32'>
<dmf:panel datafield='r_object_id' reversevisible='true' >
<%-- <dmfx:docbaseicon formatdatafield='a_content_type' type='dm_document'/> --%>
<dmf:panel datafield='<%=SearchResultSet.ATTR_URL%>'>
<dmf:datagridRowEvent eventname="dblclick">
<%-- <dmf:open datafield='object_name' uridatafield='<%=SearchResultSet.ATTR_URL%>' target='' sendcontext='false'/> --%>
<dmf:open datafield='provider_number' uridatafield='<%=SearchResultSet.ATTR_URL%>' target='' sendcontext='false'/>
</dmf:datagridRowEvent>
</dmf:panel>
<dmf:panel datafield='<%=SearchResultSet.ATTR_URL%>' reversevisible='true'>
<%-- <dmfx:actionlink action='view'  datafield='object_name' name='viewExtResult' showifinvalid="true" > --%>
<dmfx:actionlink action='view'  datafield='provider_number' name='viewExtResult' showifinvalid="true" >
<dmf:argument name='<%=SearchInfo.ENTRYID_PARAM%>' datafield='<%=SearchResultSet.ATTR_KEY%>'/>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>' contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
<dmf:argument name='type' datafield='r_object_type'/>
</dmfx:actionlink>
</dmf:panel>
</dmf:panel>
<dmf:panel datafield='r_object_id'>
<%-- <dmfx:dragdropregion datafield='object_name' enableddroppositions="<%=IDropTarget.DROP_POSITION_OVER%>" ondrop='onDrop' dragenabled='true' overlayAsDiv="true"> --%>
<dmfx:dragdropregion datafield='provider_number' enableddroppositions="<%=IDropTarget.DROP_POSITION_OVER%>" ondrop='onDrop' dragenabled='true' overlayAsDiv="true">
<dmf:argument name='objectId' datafield='r_object_id'/>
<dmf:argument name='lockOwner' datafield='r_lock_owner'/>
<%-- <dmfx:docbaseicon formatdatafield='a_content_type' typedatafield='r_object_type' linkcntdatafield='r_link_cnt' isvirtualdocdatafield='r_is_virtual_doc' assembledfromdatafield='r_assembled_from_id' isfrozenassemblydatafield='r_has_frzn_assembly' isreplicadatafield='i_is_replica' isreferencedatafield='i_is_reference' size='16'/> --%>
<dmf:datagridRowEvent eventname="dblclick">
<%-- <dmf:link name='doclnk' onclick='onClickDocbaseObject' datafield='object_name' runatclient='true'> --%>
<dmf:link name='doclnk' onclick='onClickDocbaseObject' datafield='provider_number' runatclient='true'>
<dmf:argument name='objectId' datafield='r_object_id'/>
<dmf:argument name='type' datafield='r_object_type'/>
<dmf:argument name="isVirtualDoc" datafield='r_is_virtual_doc'/>
<dmf:argument name='assembledFromId' datafield='r_assembled_from_id'/>
<dmf:argument name="linkCount" datafield='r_link_cnt'/>
</dmf:link>
</dmf:datagridRowEvent>
</dmfx:dragdropregion>
</dmf:panel>
</dmf:stringlengthformatter>
</dmf:termshighlightingformatter>
</dmf:datagridRowTd>
<dmf:datagridRowModifier>
<dmf:datagridRowTd align="center" valign="middle">
<dmfx:actionimage name='propact' nlsid='MSG_PROPERTIES' action='properties' src='icons/info.gif' showifdisabled='false'>
<dmf:argument name='objectId' datafield='r_object_id'/>
<dmf:argument name='type' datafield='r_object_type'/>
<dmf:argument name='<%=SearchInfo.ENTRYID_PARAM%>' datafield='<%=SearchResultSet.ATTR_KEY%>'/>
<dmfx:argument name='<%=SearchInfo.QUERYID_PARAM%>' contextvalue='<%=SearchInfo.QUERYID_PARAM%>'/>
</dmfx:actionimage>
</dmf:datagridRowTd>
</dmf:datagridRowModifier>
</dmf:celltemplate>
<dmf:celltemplate field='score'>
<dmf:datagridRowTd cssclass='doclistfilenamedatagrid'>
<dmf:panel datafield='score'>
<dmf:numberformatter type='percent'>
<dmf:label datafield='score'/>
</dmf:numberformatter>
</dmf:panel>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field="attachment_count">
<dmf:datagridRowTd nowrap="true" cssclass='doclisticon'>
<dmf:msgattachmenticon>
<dmf:argument name='attachmentcount' datafield='attachment_count'/>
</dmf:msgattachmenticon>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='message_importance'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<dmf:messageimportanceformatter lownlsid="MSG_IMPORTANCE_LOW" normalnlsid="MSG_IMPORTANCE_NORMAL"  hinlsid="MSG_IMPORTANCE_HIGH">
<dmf:label datafield='message_importance'/>
</dmf:messageimportanceformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='<%=SearchResultSet.ATTR_LOCATION%>'>
<dmf:datagridRowTd cssclass='doclistfilenamedatagrid' nowrap="true">
<dmf:termshighlightingformatter datafield='<%=SearchResultSet.ATTR_THH%>' cssclass='termshighlight' separator='<%=SearchResultSet.THH_SEPARATOR%>'  visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmf:stringlengthformatter maxlen='32'>
<dmf:panel datafield='<%=SearchResultSet.ATTR_LOCATION_URL%>'>
<dmf:open datafield='<%=SearchResultSet.ATTR_SOURCE%>' uridatafield='<%=SearchResultSet.ATTR_LOCATION_URL%>' target='' sendcontext='false'/>
</dmf:panel>
<dmf:panel datafield='<%=SearchResultSet.ATTR_LOCATION_FOLDERPATH%>'>
<dmf:link name='loclnk' onclick='onClickContainer' datafield='<%=SearchResultSet.ATTR_LOCATION_FOLDERPATH%>'>
<dmf:argument name='objectId' datafield='r_object_id'/>
</dmf:link>
</dmf:panel>
<dmf:panel datafield='<%=SearchResultSet.ATTR_LOCATION_FOLDERPATH%>' reversevisible='true'>
<dmf:panel datafield='<%=SearchResultSet.ATTR_LOCATION_URL%>' reversevisible='true'>
<dmf:label datafield='<%=SearchResultSet.ATTR_SOURCE%>'/>
</dmf:panel>
</dmf:panel>
</dmf:stringlengthformatter>
</dmf:termshighlightingformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field="topic_status">
<dmf:datagridRowTd cssclass='doclisticon'>
<dmfx:topicstatus name='status'  nlsid='MSG_NO_COMMENTS' action='showtopicaction'  src='icons/none.gif' height='16' width='16' showifdisabled='false' >
<dmf:argument name='objectId' datafield='r_object_id'/>
<dmf:argument name='type' datafield='r_object_type'/>
<dmf:argument name='topicStatus' datafield='topic_status'/>
<dmfx:argument name='folderId' contextvalue='objectId'/>
</dmfx:topicstatus>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='room_status' >
<dmf:datagridRowTd cssclass='doclisticon'>
<dmfx:governedicon name='room' action='view' src='icons/none.gif' height='16' width='16'  >
<dmf:argument name='objectId' datafield='r_object_id'/>
<dmf:argument name='governing' datafield='room_status'/>
<dmf:argument name='type' value='dmc_room'/>
</dmfx:governedicon>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='title'>
<dmf:datagridRowTd cssclass='doclistfilenamedatagrid'>
<dmf:termshighlightingformatter datafield='<%=SearchResultSet.ATTR_THH%>' cssclass='termshighlight' separator='<%=SearchResultSet.THH_SEPARATOR%>'  visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmfx:folderexclusionformatter datafield='r_object_type'>
<dmf:htmlsafetextformatter>
<dmf:label datafield='title'/>
</dmf:htmlsafetextformatter>
</dmfx:folderexclusionformatter>
</dmf:termshighlightingformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='authors'>
<dmf:datagridRowTd cssclass='doclistfilenamedatagrid'>
<dmf:termshighlightingformatter datafield='<%=SearchResultSet.ATTR_THH%>' cssclass='termshighlight' separator='<%=SearchResultSet.THH_SEPARATOR%>'  visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmfx:folderexclusionformatter datafield='r_object_type'>
<dmf:htmlsafetextformatter>
<dmf:label datafield='authors'/>
</dmf:htmlsafetextformatter>
</dmfx:folderexclusionformatter>
</dmf:termshighlightingformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='r_version_label'>
<dmf:datagridRowTd cssclass='doclistfilenamedatagrid'>
<dmfx:folderexclusionformatter datafield='r_object_type'>
<dmf:htmlsafetextformatter>
<dmf:label datafield='r_version_label'/>
</dmf:htmlsafetextformatter>
</dmfx:folderexclusionformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='a_content_type'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<dmf:termshighlightingformatter datafield='<%=SearchResultSet.ATTR_THH%>' cssclass='termshighlight' separator='<%=SearchResultSet.THH_SEPARATOR%>'  visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmf:stringlengthformatter maxlen='14'>
<dmfx:docformatvalueformatter>
<dmf:label datafield='CURRENT'/>
</dmfx:docformatvalueformatter>
</dmf:stringlengthformatter>
</dmf:termshighlightingformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='r_full_content_size'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<dmfx:docsizevalueformatter datafield='r_object_type'>
<dmf:label datafield='r_full_content_size'/>
</dmfx:docsizevalueformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='r_current_state'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<dmfx:policystatenameformatter datafield='r_policy_id'>
<dmf:label datafield='r_current_state'/>
</dmfx:policystatenameformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='new_document'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<%-- <dmf:booleanformatter truenlsid='NEW_TRUEVALUE' falsenlsid='NEW_FALSEVALUE'>
<dmf:label datafield='new'/>
</dmf:booleanformatter> --%>

<%
String srciconimgbool = datagrid.getDataProvider().getDataField("new_document");
String srciconimg;
if (srciconimgbool.equals("0"))
{
%>
<dmf:label label=' '/>
<%
	//srciconimg = "";
}
else
{
	//srciconimg = "/custom/theme/documentum/icons/calwin_case_doc/74.png";
%>
<dmf:image name='sortisremove' src='/custom/theme/documentum/icons/calwin_case_doc/74.png' />
<%
}
%>
<%-- <dmf:datasortimage name='sortisremove' datafield='new' cssclass='doclistbodyDatasortlink' reversesort='true' image='<%= srciconimg %>'/> --%>
<%-- <dmf:image name='sortisremove' src='<%= srciconimg %>' /> --%>

</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='applicant_lname'> <%-- Using "last_name" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the last_name attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
String strFirstName = datagrid.getDataProvider().getDataField("applicant_fname");
String strMiddleName = datagrid.getDataProvider().getDataField("applicant_mname");
String strLastName = datagrid.getDataProvider().getDataField("applicant_lname");
String strSuffix = datagrid.getDataProvider().getDataField("applicant_suffix");
String strComputedName="";
if ((strFirstName.length()>0) || (strLastName.length()>0)) {
/* 	if (strMiddleName != "") {
		strComputedName = strLastName + ", " + strFirstName + " " + strMiddleName;
	} else {
		strComputedName = strLastName + ", " + strFirstName;
	} */
	strComputedName = strLastName;
	if (strSuffix != "") {
		strComputedName = strComputedName + " " + strSuffix;
	}
	strComputedName = strComputedName + ", " + strFirstName;
	if (strMiddleName != "") {
		strComputedName = strComputedName + " " + strMiddleName;
	}
}
%>

<dmf:label label='<%= strComputedName %>'/>
</dmf:datagridRowTd>
</dmf:celltemplate>


<dmf:celltemplate field='category'> <%-- Using "last_name" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the last_name attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
String strDocType = datagrid.getDataProvider().getDataField("doc_type");
String strCategory = datagrid.getDataProvider().getDataField("category");
//String strSubCategory = datagrid.getDataProvider().getDataField("sub_category");
String strComputedName="";
//if ((Pattern.matches("[a-zA-Z]+", strDocType)))
//{
IDfCollection iCollection = null;
IDfQuery query = DfcUtils.getClientX().getQuery();
//String queryString = "select title from calwin_admin_case where is_category=1 and value_id in (select root_id from calwin_admin_case where is_category=0 and is_subcategory=0 and value_id='" + strDocType + "')";
String queryString = "select title from calwin_admin_case where is_category=1 and value_id='" + strCategory + "'";
query.setDQL(queryString);

try {
	iCollection = query.execute(form.getDfSession(), 0);
	for (; iCollection.next() == true;) {
		strComputedName = iCollection.getString("title");
	}
} catch (DfException e) {
	throw new WrapperRuntimeException("Query Failed...", e);
} finally {
	try {
		if (iCollection != null) {
	iCollection.close();
		}
	} catch (DfException e) {}
}
//}
%>

<dmf:label label='<%= strComputedName %>'/>
</dmf:datagridRowTd>
</dmf:celltemplate>


<dmf:celltemplate field='sub_category'> <%-- Using "last_name" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the last_name attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
String strDocType = datagrid.getDataProvider().getDataField("doc_type");
String strSubCategory = datagrid.getDataProvider().getDataField("sub_category");
String strComputedName="";
//if ((Pattern.matches("[a-zA-Z]+", strDocType)))
//{
IDfCollection iCollection = null;
IDfQuery query = DfcUtils.getClientX().getQuery();
//String queryString = "select title from calwin_admin_case where is_subcategory=1 and value_id in (select parent_id from calwin_admin_case where is_category=0 and is_subcategory=0 and value_id='" + strDocType + "')";
String queryString = "select title from calwin_admin_case where is_subcategory=1 and value_id='" + strSubCategory + "'";
query.setDQL(queryString);

try {
	iCollection = query.execute(form.getDfSession(), 0);
	for (; iCollection.next() == true;) {
		strComputedName = iCollection.getString("title");
	}
} catch (DfException e) {
	throw new WrapperRuntimeException("Query Failed...", e);
} finally {
	try {
		if (iCollection != null) {
	iCollection.close();
		}
	} catch (DfException e) {}
}
//}
%>

<dmf:label label='<%= strComputedName %>'/>
</dmf:datagridRowTd>
</dmf:celltemplate>


<dmf:celltemplate field='doc_type'> <%-- Using "last_name" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the last_name attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
String strDocType = datagrid.getDataProvider().getDataField("doc_type");
String strComputedName="";
//if ((Pattern.matches("[a-zA-Z]+", strDocType)))
//{
IDfCollection iCollection = null;
IDfQuery query = DfcUtils.getClientX().getQuery();
String queryString = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and value_id='" + strDocType + "'";
query.setDQL(queryString);

try {
	iCollection = query.execute(form.getDfSession(), 0);
	for (; iCollection.next() == true;) {
		strComputedName = iCollection.getString("title");
	}
} catch (DfException e) {
	throw new WrapperRuntimeException("Query Failed...", e);
} finally {
	try {
		if (iCollection != null) {
	iCollection.close();
		}
	} catch (DfException e) {}
}
//}
%>

<dmf:label label='<%= strComputedName %>'/>
</dmf:datagridRowTd>
</dmf:celltemplate>



<dmf:celltemplate field='received_date'> <%-- Using "received_date" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the received_date attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
DateValueFormatter formatter = new DateValueFormatter();
String strReceivedDate = formatter.format(datagrid.getDataProvider().getDataField("received_date"));
String strComputedDate = "";
if (!("&nbsp;".equals(strReceivedDate))) {
	String strComputedDateIntermediate = (strReceivedDate.split(" "))[0];
	//String strComputedDate = strComputedDateIntermediate;
	String[] strComputedDateIntermediateSplit = strComputedDateIntermediate.split("/");
	String strComputedDateMonth = strComputedDateIntermediateSplit[0];
	if (strComputedDateIntermediateSplit[0].length() == 1){
		strComputedDateMonth = "0" + strComputedDateIntermediateSplit[0];
	}
	String strComputedDateDay = strComputedDateIntermediateSplit[1];
	if (strComputedDateIntermediateSplit[1].length() == 1){
		strComputedDateDay = "0" + strComputedDateIntermediateSplit[1];
	}
	String strComputedDateYear = strComputedDateIntermediateSplit[2];
	strComputedDate = strComputedDateMonth + "/" + strComputedDateDay + "/" + strComputedDateYear;
}
%>
<dmf:label label='<%= strComputedDate %>'/>


</dmf:datagridRowTd>
</dmf:celltemplate>

<dmf:celltemplate field='dob'> <%-- Using "dob" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the dob attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
DateValueFormatter formatter = new DateValueFormatter();
String strDOBDate = formatter.format(datagrid.getDataProvider().getDataField("dob"));
String strDOBComputedDate = "";
if (!("&nbsp;".equals(strDOBDate))) {
	String strDOBComputedDateIntermediate = (strDOBDate.split(" "))[0];
	//String strComputedDate = strComputedDateIntermediate;
	String[] strDOBComputedDateIntermediateSplit = strDOBComputedDateIntermediate.split("/");
	String strDOBComputedDateMonth = strDOBComputedDateIntermediateSplit[0];
	if (strDOBComputedDateIntermediateSplit[0].length() == 1){
		strDOBComputedDateMonth = "0" + strDOBComputedDateIntermediateSplit[0];
	}
	String strDOBComputedDateDay = strDOBComputedDateIntermediateSplit[1];
	if (strDOBComputedDateIntermediateSplit[1].length() == 1){
		strDOBComputedDateDay = "0" + strDOBComputedDateIntermediateSplit[1];
	}
	String strDOBComputedDateYear = strDOBComputedDateIntermediateSplit[2];
	
	strDOBComputedDate = strDOBComputedDateMonth + "/" + strDOBComputedDateDay + "/" + strDOBComputedDateYear;
}
%>
<dmf:label label='<%= strDOBComputedDate %>'/>

</dmf:datagridRowTd>
</dmf:celltemplate>


<dmf:celltemplate field='ssn'> <%-- Using "ssn" as a placeholder so that sorting will work. In effect, the values displayed are computed results and not the values of the ssn attribute --%>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>

<%
String strSSN = datagrid.getDataProvider().getDataField("ssn");
String strSSNComputed = strSSN;
if (!("&nbsp;".equals(strSSN))) 
{
	if (strSSN.length()==9) 
	{
		strSSNComputed = String.format("%s-%s-%s", strSSN.substring(0, 3), strSSN.substring(3,5), strSSN.substring(5));
	} 
}
%>
<dmf:label label='<%= strSSNComputed %>'/>

</dmf:datagridRowTd>
</dmf:celltemplate>


<dmf:celltemplate field='soft_delete'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<%
String srciconimgbool = datagrid.getDataProvider().getDataField("soft_delete");
String srciconimg;
if (srciconimgbool.equals("0"))
{
%>
<dmf:label label=' ' />
<%
	//srciconimg = "";
}	
else
{
	//srciconimg = "/custom/theme/documentum/icons/calwin_case_doc/68.png";
%>
<dmf:image name='sortisremove' src='/custom/theme/documentum/icons/calwin_case_doc/68.png' />
<%	
}
%>
<%-- <dmf:datasortimage name='sortisremove' datafield='is_remove' cssclass='doclistbodyDatasortlink' reversesort='true' image='<%= srciconimg %>'/> --%>
<%-- <dmf:image name='sortisremove' src='<%= srciconimg %>' /> --%>
</dmf:datagridRowTd>
</dmf:celltemplate>

<%-- START: CERMS breakfix: ticket # --%>
<dmf:celltemplate field='scan_operator'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid' align="center">
<%
String strScanLocation = datagrid.getDataProvider().getDataField("scan_location");
String strScanOperator = datagrid.getDataProvider().getDataField("scan_operator");
String strComputedName = datagrid.getDataProvider().getDataField("r_creator_name");
if ("".equalsIgnoreCase(strScanLocation) || "EI".equalsIgnoreCase(strScanLocation)) 
{
%>
<dmf:label label='<%= strComputedName %>'/>
<%} else { %>
<dmf:label label='<%= strScanOperator %>'/>
<%} %>
</dmf:datagridRowTd>
</dmf:celltemplate>

<dmf:celltemplate field='scan_date'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid' align="center">
<%
String strScanLocation = datagrid.getDataProvider().getDataField("scan_location");
if ("".equalsIgnoreCase(strScanLocation) || "EI".equalsIgnoreCase(strScanLocation)) 
{
	
	DateValueFormatter formatter = new DateValueFormatter();
	String strDOBDate = formatter.format(datagrid.getDataProvider().getDataField("r_creation_date"));
	String strDOBComputedDate = "";
	if (!("&nbsp;".equals(strDOBDate))) {
		String strDOBComputedDateIntermediate = (strDOBDate.split(" "))[0];
		//String strComputedDate = strComputedDateIntermediate;
		String[] strDOBComputedDateIntermediateSplit = strDOBComputedDateIntermediate.split("/");
		String strDOBComputedDateMonth = strDOBComputedDateIntermediateSplit[0];
		if (strDOBComputedDateIntermediateSplit[0].length() == 1){
			strDOBComputedDateMonth = "0" + strDOBComputedDateIntermediateSplit[0];
		}
		String strDOBComputedDateDay = strDOBComputedDateIntermediateSplit[1];
		if (strDOBComputedDateIntermediateSplit[1].length() == 1){
			strDOBComputedDateDay = "0" + strDOBComputedDateIntermediateSplit[1];
		}
		String strDOBComputedDateYear = strDOBComputedDateIntermediateSplit[2];
		
		strDOBComputedDate = strDOBComputedDateMonth + "/" + strDOBComputedDateDay + "/" + strDOBComputedDateYear;
	}
	
%>
<dmf:label label='<%= strDOBComputedDate %>'/>
<%} else { 
	DateValueFormatter formatter = new DateValueFormatter();
	String strScanDate = formatter.format(datagrid.getDataProvider().getDataField("scan_date"));
	String strScanComputedDate = "";
	if (!("&nbsp;".equals(strScanDate))) {
		String strScanComputedDateIntermediate = (strScanDate.split(" "))[0];
		//String strComputedDate = strComputedDateIntermediate;
		String[] strScanComputedDateIntermediateSplit = strScanComputedDateIntermediate.split("/");
		String strScanComputedDateMonth = strScanComputedDateIntermediateSplit[0];
		if (strScanComputedDateIntermediateSplit[0].length() == 1){
			strScanComputedDateMonth = "0" + strScanComputedDateIntermediateSplit[0];
		}
		String strScanComputedDateDay = strScanComputedDateIntermediateSplit[1];
		if (strScanComputedDateIntermediateSplit[1].length() == 1){
			strScanComputedDateDay = "0" + strScanComputedDateIntermediateSplit[1];
		}
		String strScanComputedDateYear = strScanComputedDateIntermediateSplit[2];
		
		strScanComputedDate = strScanComputedDateMonth + "/" + strScanComputedDateDay + "/" + strScanComputedDateYear;
	}
%>
	<dmf:label label='<%= strScanComputedDate %>'/>
<%} %>
</dmf:datagridRowTd>
</dmf:celltemplate>

<%-- END: CERMS breakfix: ticket # --%>


<%-- START: CERMS II post go-live fixes --%>
<dmf:celltemplate field='scan_location'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid' align="center">

<%
String strScanLocation = datagrid.getDataProvider().getDataField("scan_location");
String strComputedName="";
if ("NONE".equalsIgnoreCase(strScanLocation)) {
	strComputedName = "CC";
}
if ("".equalsIgnoreCase(strScanLocation)) {
	strComputedName = "EI";
}
if ("".equalsIgnoreCase(strComputedName)) {
%>
<dmf:label datafield='scan_location'/>
<%} else { %>
<dmf:label label='<%= strComputedName %>'/>
<%} %>
</dmf:datagridRowTd>
</dmf:celltemplate>
<%-- END: CERMS II post go-live fixes --%>

<dmf:celltemplate type='date'>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<dmf:datevalueformatter type='short'>
<dmf:label datafield='CURRENT'/>
</dmf:datevalueformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
<dmf:celltemplate field='summary'>
</dmf:celltemplate>
<dmf:celltemplate>
<dmf:datagridRowTd nowrap="true" cssclass='doclistfilenamedatagrid'>
<dmf:termshighlightingformatter datafield='<%=SearchResultSet.ATTR_THH%>' cssclass='termshighlight' separator='<%=SearchResultSet.THH_SEPARATOR%>'  visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmf:label datafield='CURRENT'/>
</dmf:termshighlightingformatter>
</dmf:datagridRowTd>
</dmf:celltemplate>
</dmf:celllist>
<dmf:datagridRowTd>&nbsp;</dmf:datagridRowTd>
<dmf:datagridRowTd width="99%">&nbsp;</dmf:datagridRowTd>
<dmf:panel datafield="summary">
<dmf:datagridRowBreak/>
<dmf:celllist>
<dmf:celltemplate field="summary">
<td colspan="40" class="additionalRow">
<dmf:termshighlightingformatter
datafield='<%=SearchResultSet.ATTR_THH%>'
cssclass='termshighlight'
separator='<%=SearchResultSet.THH_SEPARATOR%>'
visible='<%=String.valueOf(form.isHighlightActive())%>'>
<dmf:stringlengthformatter maxlen='130'>
<dmf:label datafield='summary'/>
</dmf:stringlengthformatter>
</dmf:termshighlightingformatter>
</td>
</dmf:celltemplate>
</dmf:celllist>
</dmf:panel>
</dmf:datagridRow>
<dmf:nodataRow>
<td colspan=23 height=24>
&nbsp;&nbsp;<dmf:label name='<%=Search60.CONTROL_NODATA%>' id='<%=Search60.CONTROL_NODATA%>'/>
</td>
</dmf:nodataRow>
<%-- <dmf:datagridFooter> --%>
<div id='footer' class="datagridDesktopUIFooter" > <%-- class="datagridDesktopUIFooter" style='position:fixed;overflow:hidden;width:100%;left:0px;bottom:0px;' --%>
<table> <%--  border='0' width='100%' cellspacing='0' cellpadding='0' class='defaultDatagridStyle' --%>
<tr>
<dmf:panel name='searchbtnpanel'>
<table class="webtopTitlebarBackground" width="100%">
<tr>
<td align="right">
<table cellspacing='0' cellpadding='0' border='0' >
<tr>
<td>
<input type="button" value="" style='visibility:hidden'/>
</td>
<td>
<dmfx:actionbutton name='editindexfields' nlsid='MSG_PROPERTIES' action='properties' showifdisabled='true' dynamic='singleselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='removedocsearch' nlsid='MSG_REMOVEDOC' action='remove_document' showifdisabled='true' dynamic='multiselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='unremovedocsearch' nlsid='MSG_UNREMOVEDOC' action='unremove_document' showifdisabled='true' dynamic='multiselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='changeprocessedstatus' nlsid='MSG_CHANGEPROCESSEDSTATUS' action='changeprocessed_document' showifdisabled='true' dynamic='multiselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='changecasename' nlsid='MSG_CHANGECASENAME' action='changecasepersons_document' showifdisabled='true' dynamic='singleselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='delete' nlsid='MSG_DELETE' action='delete' showifdisabled='true' dynamic='multiselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='mashup' nlsid='MSG_MASHUP' action='soms_merge_selected_documents' showifdisabled='true' dynamic='multiselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='savewithoutpolicy' nlsid='MSG_SAVEWITHOUTPOLICY' action='exportWithoutPolicy' showifdisabled='true' dynamic='multiselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
<dmfx:argument name='type' contextvalue='type'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
<td>
<dmfx:actionbutton name='import' nlsid='MSG_IMPORT' action='calwinimport' showifdisabled='true' showifinvalid='true' dynamic='genericnoselect'>
<dmfx:argument name='objectId' contextvalue='objectId'/>
</dmfx:actionbutton>
</td>
<td width=5>
</td>
</tr>
</table>
</td>
</tr>
</table>
</dmf:panel>
</tr>
</table>
</div>
<%-- </dmf:datagridFooter> --%>


<%-- </dmf:pane>
</dmf:paneset> --%>
<%
if (AccessibilityService.isAllAccessibilitiesEnabled())
{
%>
</table>
<%
}
%>
</dmf:datagrid>
</dmfx:actionmultiselect>






<script type="text/javascript">
var updateStatusCall;
function initInlineCall ()
{
notifyPollingInitiation()
updateStatusCall = setInterval("updateSearchStatus()", 1000);
var startStopSearchImg = wdk.dom.get('<%=Search60.CONTROL_START_STOP_SEARCH%>');
var startStopSearchLink = document.getElementById("stopStartLink");
if ( startStopSearchImg != null)
{
startStopSearchImg.onclick = null;
startStopSearchImg.onclick = onStopSearch;
}
startStopSearchLink.onclick = onStopSearch;
}
function updateSearchStatus ()
{
var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
prefs.setCallback(renderSearchStatus);
postInlineServerEvent(null, prefs, null, null, "onUpdateSearchStatus", null, null);
}
function renderSearchStatus (data)
{
var showResults = data['<%=Search60.JSON_SHOW_RESULTS%>'];
var isCompleted = data['<%=Search60.JSON_IS_COMPLETED%>'];
if (showResults)
{
var noDataLabel = document.getElementById('<%=Search60.CONTROL_NODATA%>');
if(noDataLabel != null)
{
showSearchResults();
}
else
{
fireClientEvent("onExecuteQuery", '<%=form.getSearchInfo().getQueryId()%>','<%=SmartNavigationService.isContentClusteringServiceAvailable()%>');
}
}
else
{
if (isCompleted)
{
clearInterval(updateStatusCall);
var processingImg = wdk.dom.get('<%=Search60.CONTROL_PROCESSING_SEARCH%>');
if (processingImg !=null)
{
processingImg.src = '<%=Form.makeUrl(request, "/wdk/theme/documentum/"+Search60.ICON_SEARCH_DONE)%>';
}
var startStopSearchImg = wdk.dom.get('<%=Search60.CONTROL_START_STOP_SEARCH%>');
if(startStopSearchImg!=null)
{
startStopSearchImg.src = '<%=Form.makeUrl(request, "/wdk/theme/documentum/"+Search60.ICON_START_SEARCH)%>';
}
document.getElementById("stopStartBr").innerHTML = "<%=form.getString("MSG_RESTART_SEARCH")%>";
var alttext = '<%=form.getString("MSG_RESTART_SEARCH_TIP")%>';
var startStopSearchLink = document.getElementById("stopStartLink");
startStopSearchLink.alt = alttext;
if(startStopSearchImg!=null)
{
startStopSearchImg.alt = alttext;
startStopSearchImg.onclick = null;
startStopSearchImg.onclick = onRestartSearch;
}
startStopSearchLink.onclick = onRestartSearch;
}
else
{
var startStopSearchImg = wdk.dom.get('<%=Search60.CONTROL_START_STOP_SEARCH%>');
var startStopSearchLink = document.getElementById("stopStartLink");
var alttext = '<%=form.getString("MSG_STOP_SEARCH_TIP")%>';
document.getElementById("stopStartBr").innerHTML = '<%=form.getString("MSG_STOP_SEARCH")%>';
if(startStopSearchImg !=null)
{
startStopSearchImg.onclick = null;
startStopSearchImg.onclick = onStopSearch;
startStopSearchImg.alt = alttext;
}
startStopSearchLink.onclick = onStopSearch;
startStopSearchLink.alt = alttext;
}
var resultsCount = data['<%=Search60.JSON_RESULTS%>'];
wdk.dom.get('<%=Search60.CONTROL_RESULT_COUNT%>').innerHTML = resultsCount;
var desc = data['<%=Search60.JSON_DESCRIPTION%>'];
wdk.dom.get('<%=Search60.CONTROL_QUERY_DESCRIPTION%>').innerHTML = desc;
}
unlock();
if (isCompleted)
{
notifyPollingComplete();
}
}
function notifyPollingInitiation()
{
if (window.suspendTestEvents)
{
window.suspendTestEvents();
}
}
function notifyPollingComplete()
{
if (window.resumeTestEvents)
{
window.resumeTestEvents();
}
}
function showSearchResults ()
{
clearInterval(updateStatusCall);
unlock();
notifyPollingComplete();
<dmf:postserverevent handlermethod='onShowResults'/>
}
function onStopSearch ()
{
clearInterval(updateStatusCall);
unlock();
notifyPollingComplete();
<dmf:postserverevent handlermethod='onStopSearch'/>
}
function onRestartSearch ()
{
clearInterval(updateStatusCall);
unlock();
notifyPollingComplete();
<dmf:postserverevent handlermethod='onRestartSearch' />
}
function unlock ()
{
if (isEventPostingLocked())
{
releaseEventPostingLock();
}
}
</script>
</dmf:form>
<script>initInlineCall();</script>
</body>
</html>
