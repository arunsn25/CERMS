<%////
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ taglib uri="/WEB-INF/tlds/dmfxsearch_1_0.tld" prefix="dmfxs" %>
<%@ taglib uri="/WEB-INF/tlds/cosd/cosdform_1_0.tld" prefix="cosdf" %>
<%@ page import="com.documentum.web.formext.control.docbase.search.RepositorySearch,
com.documentum.web.formext.control.docbase.search.SearchAttribute,
com.documentum.webcomponent.library.advsearch.AdvSearchEx,
com.documentum.web.form.IParams,
com.documentum.web.form.Form,com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame,com.documentum.web.util.JavascriptUtil,
com.documentum.fc.client.IDfQuery,com.documentum.web.util.DfcUtils,com.documentum.fc.client.IDfCollection,
com.documentum.fc.common.DfException,com.documentum.web.common.WrapperRuntimeException,
com.documentum.web.formext.session.SessionManagerHttpBinding,
com.documentum.fc.client.IDfSession,
com.documentum.fc.client.IDfSessionManager"%>
<dmf:html>
<%
String hrSpacerHeight = "1";
String spacerHeight = "6";

//
%>
<%! String m_DisplayTable = null; %>
<dmf:head>
<dmf:webform/>
<%
CalwinSearchFrame advsearch = (CalwinSearchFrame)pageContext.getAttribute(IParams.FORM,PageContext.REQUEST_SCOPE);
%>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/wdk/include/windows.js")%>'></script>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request, "/wdk/include/dynamicAction.js")%>'></script>
<script src='<%=Form.makeUrl(request, "/custom/jsp/library/component/search/cosdsearchframe.js")%>' type="text/javascript"></script>
<script src='<%=Form.makeUrl(request, "/custom/jsp/library/component/search/jquery-1.10.2.js")%>'></script>
<script>
function <%=JavascriptUtil.namespaceScriptlet(request, "executeSavedSearch()")%>
{
	<dmf:postserverevent handlerid='<%=advsearch.getElementName()%>' handlermethod='executeSavedSearch'/>
}
registerClientEventHandler(null, '<%=JavascriptUtil.namespaceScriptlet(request, "executeSavedSearch")%>', <%=JavascriptUtil.namespaceScriptlet(request, "executeSavedSearch")%>);

// JS function "onClickSearch()" removed from here

function onCheckboxClick(){
	var checkobjfn = document.getElementById("check");
	var checkfn = checkobjfn.value;
	//alert("checkbox value before: " + checkobjfn.value);
 	if (checkfn == '') {
		checkobjfn.value = 'true';
	} else {
		checkobjfn.value = '';
	}
	//alert("checkbox value after: " + checkobjfn.value);
 	getfocus();
}

function onDoresCheckboxClick(){
	var dorescheckobjfn = document.getElementById("dorescheck");
	var dorescheckfn = dorescheckobjfn.value;
	//alert("dores checkbox value before: " + dorescheckobjfn.value);
 	if (dorescheckfn == '') {
		dorescheckobjfn.value = 'true';
	} else {
		dorescheckobjfn.value = '';
	}
	//alert("checkbox value after: " + dorescheckobjfn.value);
	getfocus();
}

function getfocus()
{
document.getElementById('casenumbertext').focus();
}

function onSearchTextFocus ()
{
/* var ctrlText = document.getElementById("casenumbertext");
if (ctrlText != null)
{
ctrlText.style.color = "#000"
if (ctrlText.value == "Enter Case Number; Press Enter")
{
ctrlText.value = "";
}
var browserFramePathExpr = getAbsoluteFramePath("browser");
if (browserFramePathExpr != null && eval(browserFramePathExpr) != null)
{
setAutoCompleteTargetFrame(ctrlText, "browser")
}
else
{
setAutoCompleteTargetFrame(ctrlText, "content")
}
}
 */}

/* function onSearchTextFocus2 ()
{
var ctrlText = document.getElementById("casenametext");
if (ctrlText != null)
{
ctrlText.style.color = "#000"
if (ctrlText.value == "Display Case Name")
{
ctrlText.value = "";
}
var browserFramePathExpr = getAbsoluteFramePath("browser");
if (browserFramePathExpr != null && eval(browserFramePathExpr) != null)
{
setAutoCompleteTargetFrame(ctrlText, "browser")
}
else
{
setAutoCompleteTargetFrame(ctrlText, "content")
}
}
} */

function onInitPage ()
{
/* var text = document.getElementById("casenumbertext");
var text2 = document.getElementById("casenametext");
if (text)
{
if (text.value == "")
{
text.style.color = "#999";
text.value = "Enter Case Number; Press Enter";
}
text.onfocus = onSearchTextFocus;
text.onblur = function()
{
if (text.value == "")
{
text.value = "Enter Case Number; Press Enter";
text.style.color = "#999";
}
}
}
callBlur(text);
if (text2)
{
if (text2.value == "")
{
text2.style.color = "#999";
text2.value = ""; //Display Case Name
}
text2.onfocus = onSearchTextFocus2;
text2.onblur = function()
{
if (text2.value == "")
{
text2.value = ""; //Display Case Name
text2.style.color = "#999";
}
}
}
callBlur(text2);*/
/* 	var catDDDList = document.getElementById("catdddlst"); // tableList
	//If the user has selected a value and the blank option exists
	alert(catDDDList.value);
	alert(catDDDList.value != "");
	if ((catDDDList.value != "")){ // &&(catDDDList.options[0].value=="")
		//Remove the blank option
		//tableList.options[0]=null;
		catDDDList.options[1]=null;
		alert(catDDDList.options[0].value);
	} */
}
function callBlur (text)
{
var f = function()
{
text.blur();
}
setTimeout(f, 10);
}

</script>



</dmf:head>
<dmf:body cssclass='contentBackground' marginheight='0' marginwidth='0'
topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0' onload='onInitPage();'>
<dmf:form>

<dmf:hidden name='simpleSearchViewHiddenCtrl' id='simpleSearchViewHiddenCtrl'/>
<dmf:hidden name='advSearchViewHiddenCtrl' id='advSearchViewHiddenCtrl'/>
<dmf:hidden name='calwinSearchQueryHiddenCtrl' id='calwinSearchQueryHiddenCtrl'/>
<dmf:hidden name='calwinCategoryHiddenCtrl' id='calwinCategoryHiddenCtrl'/>
<dmf:hidden name='calwinSubcategoryHiddenCtrl' id='calwinSubcategoryHiddenCtrl'/>
<dmf:hidden name='calwinDoctypeHiddenCtrl' id='calwinDoctypeHiddenCtrl'/>

<div class='webtopTitlebarBackground'> <%-- class='defaultCOSDLogo ' height='25' width='100%' align="right" --%>
<table>
<tr>
<td align="left" width="110">&nbsp;&nbsp;
	<img src='<%=request.getContextPath()%>/custom/theme/documentum/images/HHSALogo.jpg'/>
</td>
<td style="float:right" width="300%">
<table width="118%">
<tr>
<%-- <td style="position:absolute;top:8px;right:100px"> --%>
<td style="position:absolute;top:8px;right:100px">
	<b><dmf:label name='usernameloginlabel' label='User: ' /></b> <%-- cssclass='defaultLabelStyle'  --%>
&nbsp;
	<b><dmf:label name='usernamelabel' id='usernamelabel'/></b>
</td>
<%-- <td style="position:absolute;top:5px;right:5px" > --%>
<td style="position:absolute;top:5px;right:5px">
	<%-- <dmfx:actionbutton name='logout' action='logout' nlsid='MSG_LOGOUT' tooltipnlsid='MSG_LOGOUT_TIP' /> --%> <%-- cssclass="floatRightAlignment"  --%>
	<dmfx:actionbutton name='logout' action='calwinsearchlogout' nlsid='MSG_LOGOUT' tooltipnlsid='MSG_LOGOUT_TIP' />
</td>
</tr>
</table>
</td>
</tr>
</table>
</div>

<table cellpadding='1' cellspacing='1' > <%-- class='defaultCOSDLogo' --%>

<%-- <tr style='display:none'>
<td height='10' class='webtopTitlebarBackground' width='100%'>
</td>
</tr> --%>


<%-- <tr>
<td>
<table> --%>


<tr>
<td class="spacer" height="10">&nbsp;</td>
<td></td><td></td>
</tr>

<tr>

<td class="leftAlignment" valign=top nowrap></td>
<%-- <td>&nbsp;</td> --%>

<td class="rightAlignment" valign=middle nowrap>
							<b><dmf:label name='casenumberlabel'
								cssclass='defaultLabelStyle' label='Case Number' /></b></td>
								<td>&nbsp;<dmf:text name='casenumbertext' id='casenumbertext' size='7' defaultonenter='true' focus="true"/><b><dmf:label name='casenumbertextvalidate' id='casenumbertextvalidate' style="{COLOR:red;align:left;display:none}" label='Enter a valid case number' /></b><%-- <dmf:button name='findcasename' cssclass='buttonLink' nlsid='MSG_CLEAR' onclick="onShowCaseNameOptions" default='true' visible='false'/> --%>
</td>
<%--<td>&nbsp;</td> --%>
<%--<td id='hhsacasenumbervalidate' style="display:none">
	<b><dmf:label name='casenumbertextvalidate' id='casenumbertextvalidate' style="{COLOR:red;align:left;display:none}" label='Case Number cannot be empty' /></b>
</td> --%>
<dmf:panel name='<%=CalwinSearchFrame.CONTROL_CASENAMELABELPANEL%>'>
<%-- <td class="rightAlignment" valign=middle nowrap>
							<b><dmf:label name='casenamelabel'
								cssclass='defaultLabelStyle' label='Case Persons' /></b></td>
								<td>&nbsp;
											<dmf:dropdownlist name='casenameddl' id='casenameddl'>
											</dmf:dropdownlist>

</td> --%>
</dmf:panel>
<%-- <td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td class="rightAlignment" valign=middle nowrap><b><dmf:label name='usernameloginlabel' cssclass='defaultLabelStyle' label='User: ' /></b></td>
<td>&nbsp;<b><dmf:label name='usernamelabel' id='usernamelabel'/></b></td> --%>

</tr>

<dmf:panel name='<%=CalwinSearchFrame.CONTROL_CASENAMEOPTIONSPANEL%>'>
<tr>

<td class="rightAlignment" valign=middle nowrap></td>
<!-- <td>&nbsp;</td> -->

<td class="rightAlignment" valign=middle nowrap><b><dmf:label name='categorylabel' cssclass='defaultLabelStyle' label='Category' /></b></td>
<td>&nbsp;<dmf:datadropdownlist name='catdddlst' id='catdddlst' onselect='onChangeCategoryDDDList'>
				<dmf:option value="" name="catOption" />
				<dmf:dataoptionlist>
					<dmf:option datafield="title" labeldatafield="title"/>
				</dmf:dataoptionlist>
			</dmf:datadropdownlist>
</td>							

<td>&nbsp;&nbsp;&nbsp;</td>

<td class="rightAlignment" valign=middle nowrap><b><dmf:label name='subcategorylabel' cssclass='defaultLabelStyle' label='SubCategory' /></b></td>
<td>&nbsp;<dmf:datadropdownlist name='subcatdddlst' id='subcatdddlst' onselect='onChangeSubCategoryDDDList'>
				<dmf:option name="subcatOption" value=""/>
				<dmf:dataoptionlist>
					<dmf:option datafield="title" labeldatafield="title"/>
				</dmf:dataoptionlist>
			</dmf:datadropdownlist>
</td>

<td>&nbsp;</td>

<td>
<table>
<tr>
<td class="rightAlignment" valign=middle nowrap><b><dmf:label name='doctypelabel' cssclass='defaultLabelStyle' label='Document Type' /></b></td>
<td>&nbsp;<dmf:datadropdownlist name='doctypedddlst' id='doctypedddlst' onselect='onChangeDoctypeDDDList'>
				<dmf:option name="doctypeOption" value=""/>
				<dmf:dataoptionlist>
					<dmf:option datafield="title" labeldatafield="title"/>
				</dmf:dataoptionlist>
			</dmf:datadropdownlist>
</td>
</tr>
</table>
</td>

<td>&nbsp;</td>
<td>&nbsp;</td>


		<%
				String strComputedName="";		
				IDfCollection iCollection = null;
				IDfQuery query = DfcUtils.getClientX().getQuery();
				String queryString = "select r_object_id from dm_cabinet where object_name = 'CalWIN'";
				query.setDQL(queryString);
				
				 IDfSessionManager sessionManager = SessionManagerHttpBinding.getNewDfSessionManager();
				 sessionManager.flushSessions();
				      IDfSession dfSession = null;
			
				          
		  
				
				try {
				sessionManager.beginTransaction(); 
				dfSession = sessionManager.getSession(SessionManagerHttpBinding.getCurrentDocbase());
					iCollection = query.execute(dfSession, 0);
					for (; iCollection.next() == true;) {
						strComputedName = iCollection.getString("r_object_id");
					}
					sessionManager.commitTransaction(); 
				} catch (DfException e) {
					throw new WrapperRuntimeException("Query Failed...", e);
				} finally {
					try {
						if (iCollection != null) {
					iCollection.close();
						}
						
					if (sessionManager.isTransactionActive()) 
					{ 
					sessionManager.abortTransaction(); 
					} 
					if (dfSession != null) 
					{ 
					//release the IDfSessionManager 
					sessionManager.release(dfSession);
					sessionManager.flushSessions();
					} 
		
					} catch (DfException e) {}
				}
				%>


<td class="fieldlabel leftAlignment" nowrap height="20" colspan="1">
		<dmfx:actionlink  name='test'   label='Import'    action='calwinimport'   >  
		  <dmf:argument name='objectId' value='<%= strComputedName %>' />  
		  <dmfx:argument name='type' value='dm_sysobject'/>  
		  <dmfx:argument name='objectId' contextvalue='objectId'/>  
		
		</dmfx:actionlink>
</td>

</tr>

<tr>

<td class="rightAlignment" valign=middle nowrap></td>

<td class="rightAlignment" valign=middle nowrap>
	<b>
		<dmf:label name='receiveddatelabel' cssclass='defaultLabelStyle' label='Received Date' />&nbsp;&nbsp;&nbsp;&nbsp;
		<dmf:label name='datefromlabel' cssclass='defaultLabelStyle' label='From' />
	</b>
</td>
<td>&nbsp;<cosdf:cosddateinput name='datefrompicker' id='datefrompicker' runatclient="true" onselect="getfocus"/>
<%-- <dmf:label name='dtinputfromvalidate' id='dtinputfromvalidate' style="{COLOR:red;display:none}" label='Date should be of the following format: MM/DD/YYYY' /> --%></td>


<td>&nbsp;</td>


<td class="rightAlignment" valign=middle nowrap>
	<b>
		<dmf:label name='datetolabel' cssclass='defaultLabelStyle' label='To' />
		</b>
</td>
<td>&nbsp;<cosdf:cosddateinput name='datetopicker' id='datetopicker' runatclient="true" onselect="getfocus"/> <%-- width='60' --%>
<%-- <dmf:label name='dtinputtovalidate' id='dtinputtovalidate' style="{COLOR:red;display:none}" label='Date should be of the following format: MM/DD/YYYY' /> --%>
<%-- <dmf:label name='dtinputcomparevalidate' id='dtinputcomparevalidate' style="{COLOR:red;display:none}" label='Choose a date later than the FROM date' /></td> --%>


<td>&nbsp;</td>

<td>
<table>
<tr>
<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b>
		<dmf:label name='processedlabel' cssclass='defaultLabelStyle' label='DocStatus' />
	</b>
</td>
<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;
	<dmf:dropdownlist name='processedddlst' id='processedddlst' runatclient="true" onselect="getfocus">
		<dmf:option value='ALL' label='ALL' />
		<dmf:option value='No' label='Processed' />
		<dmf:option value='Yes' label='Unprocessed' />
	</dmf:dropdownlist>
</td>

							
<dmf:panel name='<%=CalwinSearchFrame.CONTROL_DATAMONTHPANEL%>'>						
<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b>
		<dmf:label name='datamonthlabel' cssclass='defaultLabelStyle' label='Data Month' />
	</b>
</td>

<td>&nbsp;<%--<dmf:datadropdownlist name='datamonthlst' id='datamonthlst'>
				<dmf:option name="datamonthOption" value=""/>
				<dmf:dataoptionlist>
					<dmf:option datafield="data_month" labeldatafield="data_month"/>
				</dmf:dataoptionlist>
			</dmf:datadropdownlist> --%>
			<dmf:dropdownlist name='datamonthlst' id='datamonthlst' runatclient="true" onselect="getfocus"/>
</td>

				<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<b>
						<dmf:label name='doresdoclabel' cssclass='defaultLabelStyle' label='Include DoReS Documents' />
					</b>
				</td>

				<td>&nbsp;
					<dmf:checkbox name='dorescheck' id='dorescheck' onclick='onDoresCheckboxClick' runatclient='true' value='true' />
				</td>
</dmf:panel>

</tr>
</table>
</td>

<td>&nbsp;</td>


<td>
<table class="rightAlignment" cellspacing='4' cellpadding='0' border='0'>
	<tr>
		<td>
			<dmf:button name='submitsearch' cssclass='buttonLink' nlsid='MSG_SEARCH' onclick="onClickSearch" runatclient="true" height='16' tooltipnlsid="MSG_SEARCH_TIP" default='true' /> <!-- default='true' -->
		</td>
		<td width=5>
		</td>
		<td>
			<dmf:button name='clearsearch' cssclass='buttonLink' nlsid='MSG_CLEAR' onclick="onClickClear" height='16' tooltipnlsid="MSG_CLEAR_TIP"/>
		</td>
		<td width=5>
		</td>
	</tr>
</table>
</td>

<td class="fieldlabel leftAlignment" nowrap height="20" colspan="1">
	<dmf:link name='<%=CalwinSearchFrame.CONTROL_SHOWADVSEARCH%>' id='showoptionslink' onclick='onShowOptions'/>
</td>

</tr>
</dmf:panel>


<tr id='hhsadatevalidate' style="display:none">
	<td class="rightAlignment" valign=middle nowrap></td>
	<td>&nbsp;</td>
	<td>
		<b><dmf:label name='dtinputfromvalidate' id='dtinputfromvalidate' style="{COLOR:red;display:none}" label='Enter valid Date: MM/DD/YYYY' /></b>
	</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>
		<b><dmf:label name='dtinputtovalidate' id='dtinputtovalidate' style="{COLOR:red;display:none}" label='Enter valid Date: MM/DD/YYYY' /></b>
		<b><dmf:label name='dtinputcomparevalidate' id='dtinputcomparevalidate' style="{COLOR:red;display:none}" label='Choose a date later than the FROM date' /></b>
	</td>
</tr>

<dmf:panel name='<%=CalwinSearchFrame.CONTROL_ADVSEARCHOPTIONSPANEL%>'>
<tr>
	<td class="leftAlignment" valign=top nowrap></td>

	<td class="rightAlignment" valign=middle nowrap>
		<b>
			<dmf:label name='providernumberlabel' cssclass='defaultLabelStyle' label='Provider Number' />
		</b>
	</td>
	
	<td>&nbsp;
		<dmf:text name='providernumbertext' id='providernumbertext' size="11" defaultonenter='true'  />

	<td>&nbsp;</td>


	<td class="rightAlignment" valign=middle nowrap>
		<b>
			<dmf:label name='ssnlabel' cssclass='defaultLabelStyle' label='SSN' />
		</b>
	</td>

	<script>


$(document).ready(function () {
       $('#ssntext').keyup(function() {
          var val = this.value.replace(/\D/g, '');
          var newVal = '';
          if(val.length > 4) {
             this.value = val;
          }
          if((val.length > 3) && (val.length < 6)) {
             newVal += val.substr(0, 3) + '-';
             val = val.substr(3);
          }
          if (val.length > 5) {
             newVal += val.substr(0, 3) + '-';
             newVal += val.substr(3, 2) + '-';
             val = val.substr(5);
           }
           newVal += val;
           this.value = newVal;
           
           if(newVal.length != 0 || newVal.length != 11){
           document.getElementsByName('CalwinSearchFrame_submitsearch_0')[0].disabled = true;
           
           }
           if (newVal.length > 10 || newVal.length == 0 )
           {
           
                      document.getElementsByName('CalwinSearchFrame_submitsearch_0')[0].disabled = false;

           }
        });
        });

$(document).ready(function () {
  //called when key is pressed in textbox
  $("#ssntext").keypress(function (e) {
     //if the letter is not digit then display error and don't type anything
     if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
        //display error message
        $("#errmsg").html("Digits Only").show().fadeOut("slow");
               return false;
    }
   });
   
    $("#ssntext").attr('maxlength','11');
  
    
    
});
</script>
	
	<td>&nbsp;
		<dmf:text name='ssntext' id='ssntext' size="11" maxlength='11' defaultonenter='true' />
		<%-- <b><dmf:label name='ssntextvalidate' id='ssntextvalidate' style="{COLOR:red;display:none}" label='SSN should be of the following format: xxx-xx-xxx' /></b> --%>

	<td>&nbsp;</td>


	<td>
		<table>
			<tr>

				<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<b>
						<dmf:label name='cinlabel' cssclass='defaultLabelStyle' label='CIN' />
					</b>
				</td>
				
				<td>&nbsp;&nbsp;&nbsp;&nbsp;
					<dmf:text name='cintext' id='cintext' size="9" defaultonenter='true' />
				</td>

				<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<b>
						<dmf:label name='cwinlabel' cssclass='defaultLabelStyle' label='CWIN' />
					</b>
				</td>
				
				<td>&nbsp;
					<dmf:text name='cwintext' id='cwintext' size="9" defaultonenter='true' />
				</td>

				<td class="rightAlignment" valign=middle nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<b>
						<dmf:label name='inclremovedoclabel' cssclass='defaultLabelStyle' label='Include Removed Documents' />
					</b>
				</td>

				<td>&nbsp;
					<dmf:checkbox name='check' id='check' onclick='onCheckboxClick' runatclient='true' />
				</td>

			</tr>
		</table>
	</td>

	<td>&nbsp;</td>

</tr>
</dmf:panel>


<tr id='hhsassnvalidate' style="display:none">
	<td class="rightAlignment" valign=middle nowrap></td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>
		<b><dmf:label name='ssntextvalidate' id='ssntextvalidate' style="{COLOR:red;display:none}" label='Enter SSN as: xxx-xx-xxxx' /></b>
	</td>
</tr>



<%-- <tr>
	<td class="leftAlignment" valign=top nowrap></td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<dmf:panel name='moreoptionslinkpanel'>
	</dmf:panel>
</tr> --%>


</table>

<%-- <dmf:panel name='<%=AdvSearchFrameCOSD.CONTROL_ADVSEARCHERRMSGPANEL%>' visible='false'>
	<div class='loginerrorspacing'>
		<dmf:label name='<%=AdvSearchFrameCOSD.CONTROL_ADVSEARCHERRMSG%>' style='{COLOR: #FF0000}'/>
	</div>
</dmf:panel> --%>

<dmf:panel name='searchbtnpanel'>
<table style='display:none'>
<tr>



<td>
<table class="rightAlignment" cellspacing='4' cellpadding='0' border='0'>
<tr>
<td>
<dmf:button name='submitsearch' id = 'submitsearch' cssclass='buttonLink' nlsid='MSG_SEARCH' onclick="onClickSearch" runatclient="true" height='16' tooltipnlsid="MSG_SEARCH_TIP" /> <!-- default='true'  -->
</td>
<td width=5>
</td>
<td>
<dmf:button name='clearsearch' cssclass='buttonLink' nlsid='MSG_CLEAR' onclick="onClickClear" height='16' tooltipnlsid="MSG_CLEAR_TIP"/>
</td>
<td width=5>
</td>
<td>
<dmf:button name='removedocsearch' cssclass='buttonLink' nlsid='MSG_REMOVEDOC' onclick="onClickRemoveDoc" height='16' tooltipnlsid="MSG_REMOVEDOC_TIP" /> <!-- default='true'  -->
</td>
<td width=5>
</td>
<td>
<dmf:button name='unremovedocsearch' cssclass='buttonLink' nlsid='MSG_UNREMOVEDOC' onclick="onClickUnRemoveDoc" height='16' tooltipnlsid="MSG_UNREMOVEDOC_TIP" /> <!-- default='true' -->
</td>
<td width=5>
</td>
<td>
<dmf:button name='changeprocessedstatus' cssclass='buttonLink' nlsid='MSG_CHANGEPROCESSEDSTATUS' onclick="onClickChangeProcessedStatus" height='16' tooltipnlsid="MSG_CHANGEPROCESSEDSTATUS_TIP" /> <!-- default='true' -->
</td>
<td width=5>
</td>
</tr>
</table>
</td>
</tr>
</table>
</dmf:panel>
</dmf:form>
</dmf:body>
</dmf:html>
