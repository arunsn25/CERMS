<%
//
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/wdk/errorhandler.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>
<dmf:html>
<dmf:head>
<dmf:webform/>
</dmf:head>
<dmf:body cssclass='contentBackground' id='attributes_calwin_case_doc' titlenlsid='MSG_TITLE' >
<dmf:form>
<dmfx:docbaseobject name="obj" configid="attributes"/>
<div class="attributeList">
<table cellpadding="0" cellspacing="0">

			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="obj" attribute="case_no"/></b>
				</td>
				<td><b>:</b>&nbsp;</td>
				<td valign="top" nowrap>
					<dmf:text name='casenumbertext' id='casenumbertext' size='7'/>				
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>


			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="obj" attribute="category"/></b>
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
					<b><dmfx:docbaseattributelabel object="obj" attribute="sub_category"/></b>
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
					<b><dmfx:docbaseattributelabel object="obj" attribute="doc_type"/></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:datadropdownlist name='doctypedddlst' id='doctypedddlst'>
						<dmf:option name="doctypeOption" value=""/>
						<dmf:dataoptionlist>
							<dmf:option datafield="title" labeldatafield="title"/>
						</dmf:dataoptionlist>
					</dmf:datadropdownlist>
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
			
			<!-- <tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="obj" attribute="data_month"/></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:datadropdownlist name='datamonthlst' id='datamonthlst'>
						<dmf:option name="datamonthOption" value=""/>
						<dmf:dataoptionlist>
							<dmf:option datafield="data_month" labeldatafield="data_month"/>
						</dmf:dataoptionlist>
					</dmf:datadropdownlist>
				</td> 
			</tr> -->
			
			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="obj" attribute="data_month"/></b>
				</td>
				<td><b>:</b>&nbsp;</td>
				<td valign="top" nowrap>
					<dmf:text name='datamonthtext' id='datamonthtext' size='7'/>			
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>

			<tr>
				<td valign="center" align="right" nowrap width="12%">
					<b><dmfx:docbaseattributelabel object="obj" attribute="provider_number"/></b>
				</td>
				<td><b>:</b></td>
				<td valign="top" nowrap>
					<dmf:text name='providernumbertext' id='providernumbertext' size="11" />
				</td> 
			</tr>

            <tr><td colspan="3" align="right" scope="row" height="10" width="100%"></td></tr>
            
			<%-- <tr><td valign="top"><dmfx:docbaseattributelist name="attrlist" object="obj" attrconfigid="attributes" inlinerefresh="false"/></td></tr> --%>
</table>
<p class="showProperties"><dmf:checkbox name='show_all' onclick='onShowAllClicked' nlsid='MSG_SHOW_ALL_PROPERTIES'/></p>
</div>
</dmf:form>
</dmf:body>
</dmf:html>
