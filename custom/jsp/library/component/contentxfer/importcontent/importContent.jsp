<%--
--%>
<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.common.AccessibilityService"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<%@ taglib uri="/WEB-INF/tlds/cosd/cosdform_1_0.tld" prefix="cosdf" %>
<dmf:html>
<dmf:head>
<dmf:webform validation="false"/>
<script type="text/javascript">
registerClientEventHandler(null, "onDisplayWarning", DisplayWarning);
function DisplayWarning(msg)
{
alert(msg);
}
</script>
<script language='JavaScript1.2' src='<%=Form.makeUrl(request,"/webcomponent/xforms/scripts/dojo/dojo.js")%>'></script>
<script type="text/javascript">
dojo.addOnLoad(function(){  
dojo.query(".dateTime > input").forEach(function(node, index, arr){
if(index %2 == 1) {
node.style.visibility = "hidden";
}
});
});
</script>
</dmf:head>
<dmf:body cssclass='contentBackground'>
<dmf:form autofocusneeded='true'>
<table border="0" cellpadding="0" cellspacing="0">




<dmfx:clientenvpanel environment="appintg">
<tr>
<td valign="top"><dmfx:docbaseicon size='32' name="obj_icon"/></td>
<td width="10" class="spacer">&nbsp;</td>
<td scope="row" valign="top">
<dmf:label name='location' cssclass='textSmallDisabled'/>
<span style="font-size: 1px;"><dmf:image src='images/space.gif'/><center></center></span>
<dmf:label name="file" cssclass='titleTextDark' />
</td>
</tr>
<tr><td height='12' colspan='3'><dmf:image src='images/space.gif'/></td></tr>
</dmfx:clientenvpanel>
<dmfx:clientenvpanel environment="appintg" reversevisible="true" >
<tr>
<td class="fieldlabel rightAlignment">
<%--      Current local file name--%>
<dmf:label nlsid="MSG_FILE"/>
</td>
<td class="defaultcolumnspacer">:&nbsp;</td>
<td><dmf:label name="filename" style="{COLOR: blue; FONT-SIZE: small}"/></td>
</tr>
</dmfx:clientenvpanel>
<%-- Object name--%>
<%-- <tr>
<td class="fieldlabel rightAlignment">
<b><dmf:label cssclass="defaultDocbaseAttributeStyle" nlsid="MSG_NAME"/></td>
<td class="defaultcolumnspacer">:&nbsp;</td>
<td nowrap valign="top">
<dmfx:clientenvpanel environment='appintg' reversevisible='true' >
<dmf:text name="attribute_object_name" id="attribute_object_name" defaultonenter="true" cssclass="defaultDocbaseAttributeStyle" size="51" tooltipnlsid="MSG_NAME" autocompleteid="DBAttr_object_name"/>
&nbsp;<dmf:requiredfieldvalidator name="validator" controltovalidate="attribute_object_name" nlsid="MSG_MUST_HAVE_NAME"/>
<dmf:utf8stringlengthvalidator name="attribute_object_name_lengthValidator" controltovalidate="attribute_object_name" maxbytelength="255" nlsid="MSG_NAME_TOO_LONG"/>
</dmfx:clientenvpanel>
<dmfx:clientenvpanel environment='appintg'>
<dmf:text name="attribute_object_name" cssclass="" size="51" tooltipnlsid="MSG_NAME" autocompleteid="DBAttr_object_name" />
<dmf:requiredfieldvalidator name="validator" controltovalidate="attribute_object_name" nlsid="MSG_MUST_HAVE_NAME" />
<dmf:regexpvalidator name="validator" controltovalidate="attribute_object_name" expression=".{1,255}" nlsid="MSG_NAME_TOO_LONG"/>
</dmfx:clientenvpanel>
<dmfx:docbaseattributeproxy name="docbaseobjectnameproxy" object="docbaseObj" controltorepresent="attribute_object_name"  attribute="object_name"/>
</td>
</tr> --%>
<%--        Object type selection--%>
<%-- <tr>
<td class="fieldlabel rightAlignment">
<dmf:label cssclass="defaultDocbaseAttributeStyle" nlsid="MSG_TYPE"/></td>
<td class="defaultcolumnspacer">:&nbsp;</td>
<td class="leftAlignment"><dmf:datadropdownlist width="270" name="objectTypeList" cssclass="defaultDocbaseAttributeStyle" onselect="onSelectType" tooltipnlsid="MSG_TYPE">
<dmf:dataoptionlist>
<dmf:option datafield="type_name" labeldatafield="label_text"/>
</dmf:dataoptionlist>
</dmf:datadropdownlist>
</td>
</tr> --%>
<%--    Format selection --%>
<%-- <tr>
<td class="fieldlabel rightAlignment">
<dmf:label nlsid="MSG_FORMAT"/></td>
<td class="defaultcolumnspacer">:&nbsp;</td>
<td><dmf:datadropdownlist width="270" name="formatList" tooltipnlsid="MSG_FORMAT">
<dmf:dataoptionlist>
<dmf:option datafield="name" labeldatafield="description"/>
</dmf:dataoptionlist>
</dmf:datadropdownlist>
<dmfx:docbaseattributeproxy name="docbaseobjectformatproxy" object="docbaseObj" controltorepresent="formatList"  attribute="a_content_type"/>
</td>
</tr>
<dmf:panel name="unknownFormatInfoLabelPanel" >
<tr>
<td class="fieldlabel rightAlignment"></td>
<td class="defaultcolumnspacer"></td>
<td><dmf:label name='unknownFormatWarningLabel' nlsid='MSG_ENFORCE_SELECT_FORMAT' cssclass="validatorMessageStyle"/></td>
</tr>
</dmf:panel> --%>
<%-- Continue edit checkbox --%>
<%-- <dmfx:clientenvpanel environment="appintg">
<dmfx:clientenvpanel environment="msoutlook" reversevisible="true">
<tr>
<td class="fieldlabel" align="right"></td>
<td class="defaultcolumnspacer"></td>
<td class="fieldlabel">
<dmf:checkbox name="checkboxContinueEdit" nlsid="MSG_CONTINUE_EDIT" value="true"/>
</td>
</tr>
</dmfx:clientenvpanel>
</dmfx:clientenvpanel> --%>
<%--      Other attributes--%>
<dmfx:docbaseobject name="docbaseObj"/>

<%-- Arun Start --%>

<table cellpadding="0" cellspacing="0">

			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmf:label name='casenumberlabel' cssclass='defaultLabelStyle' label='Case Number' /></b>
				</td>
				<td><b>:</b>&nbsp;</td>
				<td>
				<table>
				<tr>
				<td valign="top" nowrap>
					<dmf:text name='casenumbertext' id='casenumbertext' size='7'/>				
				</td>
				<td align="left">
					<b><dmf:label name='casenumbererrorlabel' style="{COLOR:red}" label='Error: Case Number cannot be empty' /></b>				
				</td>
				</tr> 
				</table>
				</td>
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
			
			
			
			<%-- <tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="docbaseObj" attribute="case_no"/></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmfx:docbaseattributevalue id='casenumbertext' object="docbaseObj" attribute="case_no" cssclass="defaultLabelStyle" size="8"/>
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr> --%>


			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmf:label name='categorylabel' cssclass='defaultLabelStyle' label='Category' /></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:datadropdownlist name='catdddlst' id='catdddlst' onselect='onChangeCategoryDDDList'>
						<dmf:option value="" name="catOption" />
						<dmf:dataoptionlist>
							<dmf:option datafield="title" labeldatafield="title"/>
						</dmf:dataoptionlist>
					</dmf:datadropdownlist>
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
			
			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmf:label name='subcategorylabel' cssclass='defaultLabelStyle' label='Subcategory' /></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:datadropdownlist name='subcatdddlst' id='subcatdddlst' onselect='onChangeSubCategoryDDDList'>
						<dmf:option name="subcatOption" value=""/>
						<dmf:dataoptionlist>
							<dmf:option datafield="title" labeldatafield="title"/>
						</dmf:dataoptionlist>
					</dmf:datadropdownlist>
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
			
			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmf:label name='doctypelabel' cssclass='defaultLabelStyle' label='Document Type' /></b>
				</td>
				<td><b>:</b></td>
				<td>
				<table>
				<tr>
				<td valign="top" nowrap>
					<dmf:datadropdownlist name='doctypedddlst' id='doctypedddlst' onselect='onChangeDoctypeDDDList'>
						<dmf:option name="doctypeOption" value=""/>
						<dmf:dataoptionlist>
							<dmf:option datafield="title" labeldatafield="title"/>
						</dmf:dataoptionlist>
					</dmf:datadropdownlist>
				</td> 
				<td align="left">
					<b><dmf:label name='doctypeerrorlabel' style="{COLOR:red}" label='Error: Document Type cannot be empty' /></b>				
				</td>
				</tr> 
				</table>
				</td>
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>

			<%-- <tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="docbaseObj" attribute="case_confidentiality"/></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmfx:docbaseattributevalue id='caseconfctrl' object="docbaseObj" attribute="case_confidentiality" cssclass="defaultLabelStyle" size="50"/>
				</td>
			</tr> --%>
			
			<%-- <tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmf:label name='caseconflabel' cssclass='defaultLabelStyle' label='Case Confidentiality' /></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:dropdownlist name='caseconfctrl' id='caseconfctrl'>
						<dmf:option label="NON-SECURED" value="NON-SECURED"/>
						<dmf:option label="SECURED" value="SECURED"/>
					</dmf:dropdownlist>
				</td>
			</tr> --%>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
			
			<%-- <tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="docbaseObj" attribute="case_type"/></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmfx:docbaseattributevalue id='casetypectrl' object="docbaseObj" attribute="case_type" cssclass="defaultLabelStyle" size="50"/>
				</td> 
			</tr> --%>
			
			<%-- <tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmf:label name='casetypelabel' cssclass='defaultLabelStyle' label='Case Type' /></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:dropdownlist name='casetypectrl' id='casetypectrl'>
						<dmf:option label="Eligibility" value="eligibility"/>
						<dmf:option label="Adoption" value="adoption"/>
						<dmf:option label="FosterCare - KinGap" value="fosterkingap"/>
					</dmf:dropdownlist>
				</td>
			</tr> --%>
			
			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="docbaseObj" attribute="received_date"/></b>
				</td>
				<td><b>:</b></td>
				<td>
				<table>
				<tr>
				<td valign="top" nowrap>
					<dmf:datetime name="datefrompicker" datafield="received_date"/>
				</td>
				<td align="left">
					<b><dmf:label name='datetimeerrorlabel' style="{COLOR:red}" label='Error: date cannot be greater than today.' /></b>				
				</td>
				</tr>
				</table>
				</td> 
			</tr>
			
            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
			
</table>

<%-- Arun Ends --%>

<dmfx:clientenvpanel environment="msoutlook" reversevisible="true">
<dmf:panel name="oleScanPanel">
<tr>
<td class="fieldlabel rightAlignment" nowrap>
<dmf:label nlsid="MSG_OLE_SCAN_ENABLE"/>
</td>
<td class="defaultcolumnspacer">:&nbsp;</td>
<td class="leftAlignment">
<dmf:checkbox name='oleScanEnable' tooltipnlsid="MSG_OLE_SCAN_ENABLE"/><dmf:label nlsid="MSG_OLE_SCAN_ENABLE_DESCRIPTION"/>
</td>
</tr>
</dmf:panel>
</dmfx:clientenvpanel>
</table>
<%--  BOCS write option --%>
<dmf:panel name="bocswriteoption">
<dmf:panel name="bocswriteoptiontop">
<div style="margin-top: 0px;border-bottom: 1px solid #333;clear:both"></div>
<p class="inlinehelpmessage inlinehelppara"><dmf:label align="left" nlsid="MSG_BOCS_WRITE_OPTION_NOTICE" /></p>
</dmf:panel>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<br>
<td class="fieldlabel" align="right" valign="top" scope="row" nowrap>
<dmf:label nlsid="MSG_BOCS_WRITE_OPTION"/>
</td>
<td class="defaultcolumnspacer" align="left" valign="top">:&nbsp;</td>
<td align="left" valign="top">
<p class="radiopara"><span class="radio"><dmf:radio name="bocssyncwrite" group="group2" tooltipnlsid="MSG_BOCS_SYNC_WRITE"/></span><span class="radiolabel"><dmf:label nlsid="MSG_BOCS_SYNC_WRITE"/></p>
<p class="inlinehelpmessage inlinehelppara" style="padding-left:21px"><dmf:label nlsid="MSG_BOCS_SYNC_WRITE_HELP_MESSAGE"/></p>
<p class="radiopara"><span class="radio"><dmf:radio name="bocsasyncwrite" group="group2" tooltipnlsid="MSG_BOCS_ASYNC_WRITE"/></span><span class="radiolabel"><dmf:label nlsid="MSG_BOCS_ASYNC_WRITE"/></p>
<p class="inlinehelpmessage inlinehelppara" style="padding-left:21px"><dmf:label nlsid="MSG_BOCS_ASYNC_WRITE_HELP_MESSAGE"/></p>
</td>
</tr>
</table>
<dmf:panel name="bocswriteoptionbottom">
<div style="margin-top: 0px;border-bottom: 1px solid #333;clear:both"></div>
</dmf:panel>
</dmf:panel>
</dmf:form>
</dmf:body>
</dmf:html>
