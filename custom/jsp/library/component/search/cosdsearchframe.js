var strDQLStub = "";
var strDQL = "";
var casenotxt, caseno, casenumbertextvalidateobj, catdddlstobj, catdddlst, subcatdddlstobj, subcatdddlst, doctypedddlstobj, doctypedddlst;
var datefrompickerobj, datefrompicker, datetopickerobj, datetopicker, processedddlstobj, processedddlst, showoptionslinkobj, showoptionslink;
var simpleSearchViewHiddenobj, simpleSearchViewHidden, datamonthlstobj, datamonthlst, providernumbertextobj, providernumbertext;
var ssntextobj, ssntext, ssntextvalidateobj, cintextobj, cintext, cwintextobj, cwintext, checkobj, check, dorescheckobj, dorescheck;
var dtinputfromvalid, dtinputtovalid;
var ssntextorig;

// backward compatibility for indexOf method of Array object
// source: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/indexOf
if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function (searchElement /*, fromIndex */ ) {
        "use strict";
        if (this == null) {
            throw new TypeError();
        }
        var t = Object(this);
        var len = t.length >>> 0;
        if (len === 0) {
            return -1;
        }
        var n = 0;
        if (arguments.length > 1) {
            n = Number(arguments[1]);
            if (n != n) { // shortcut for verifying if it's NaN
                n = 0;
            } else if (n != 0 && n != Infinity && n != -Infinity) {
                n = (n > 0 || -1) * Math.floor(Math.abs(n));
            }
        }
        if (n >= len) {
            return -1;
        }
        var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
        for (; k < len; k++) {
            if (k in t && t[k] === searchElement) {
                return k;
            }
        }
        return -1;
    }
}

// creates the DQL string stub to be utilized as part of the search
function createDQLStub(dctmAttr, htmlElem, isHtmlElemUpperCase) {
	var __strDQLTemp__;
	if (htmlElem !== ""){
		//alert(dctmAttr + " : " + htmlElem);
		if (isHtmlElemUpperCase){
			__strDQLTemp__ = dctmAttr+"='" + htmlElem.toUpperCase() + "' and ";
		} else {
			__strDQLTemp__ = dctmAttr+"='" + htmlElem + "' and ";
		}
		strDQL = strDQL + __strDQLTemp__;
		strDQLStub = strDQLStub + __strDQLTemp__;
	}
}


//creates the DQL string stub with OR to be utilized as part of the search
//separate method created (rather than modify the above) to reduce test cases
//added to reconcile the issues with the migrated data
function createDQLORStub(dctmAttr, htmlElem, isHtmlElemUpperCase) {
	var __strDQLTemp__;
	if (htmlElem !== ""){
		//alert(dctmAttr + " : " + htmlElem);
		if (isHtmlElemUpperCase){
			__strDQLTemp__ = dctmAttr+"='" + htmlElem.toUpperCase() + "' or ";
		} else {
			__strDQLTemp__ = dctmAttr+"='" + htmlElem + "' or ";
		}
		strDQL = strDQL + __strDQLTemp__;
		strDQLStub = strDQLStub + __strDQLTemp__;
	}
}

//creates the DQL string stub to be utilized as part of the search for the SSN value
function createDQLStubForSSN(dctmAttr, htmlElem, isHtmlElemUpperCase) {
	var __strDQLTemp__;
	if (htmlElem !== ""){
		//alert(dctmAttr + " : " + htmlElem);
		if (isHtmlElemUpperCase){
			__strDQLTemp__ = dctmAttr+"='" + htmlElem.toUpperCase() + "') and ";
		} else {
			__strDQLTemp__ = dctmAttr+"='" + htmlElem + "') and ";
		}
		strDQL = strDQL + __strDQLTemp__;
		strDQLStub = strDQLStub + __strDQLTemp__;
	}
}

// Declarations and initializations
function initSearchControls(){
	casenotxt = document.getElementById("casenumbertext");
	caseno = casenotxt.value;
	casenumbertextvalidateobj = document.getElementById("casenumbertextvalidate");
	//catdddlstobj = document.getElementById("catdddlst");
	catdddlstobj = document.getElementById("calwinCategoryHiddenCtrl");
	catdddlst = catdddlstobj.value;
	//subcatdddlstobj = document.getElementById("subcatdddlst");
	subcatdddlstobj = document.getElementById("calwinSubcategoryHiddenCtrl");
	subcatdddlst = subcatdddlstobj.value;
	//doctypedddlstobj = document.getElementById("doctypedddlst");
	doctypedddlstobj = document.getElementById("calwinDoctypeHiddenCtrl");
	doctypedddlst = doctypedddlstobj.value;
	datefrompickerobj = document.getElementById("datefrompicker_date");
	datefrompicker = datefrompickerobj.value;
	datetopickerobj = document.getElementById("datetopicker_date");
	datetopicker = datetopickerobj.value;
	processedddlstobj = document.getElementById("processedddlst");
	processedddlst = processedddlstobj.value;
	showoptionslinkobj = document.getElementById("showoptionslink");
	//showoptionslink = showoptionslinkobj.title; // Change for CERMS-II Release-b
	simpleSearchViewHiddenobj = document.getElementById("simpleSearchViewHiddenCtrl");
	simpleSearchViewHidden = simpleSearchViewHiddenobj.value;
	advSearchViewHiddenobj = document.getElementById("advSearchViewHiddenCtrl");
	advSearchViewHidden = advSearchViewHiddenobj.value;
	//showoptionslink = "Advanced Search"; // Change for CERMS-II Release-b
	calwinSearchQueryHiddenobj = document.getElementById("calwinSearchQueryHiddenCtrl");
	calwinSearchQueryHidden = calwinSearchQueryHiddenobj.value;
	//if (showoptionslink == simpleSearchViewHidden) { // Change for CERMS-II Release-b
		dorescheckobj = document.getElementById("dorescheck");
		dorescheck = dorescheckobj.value;
		datamonthlstobj = document.getElementById("datamonthlst");
		datamonthlst = datamonthlstobj.value;
		providernumbertextobj = document.getElementById("providernumbertext");
		providernumbertext = providernumbertextobj.value;
		ssntextobj = document.getElementById("ssntext");
		ssntext = ssntextobj.value;
		ssntextvalidateobj = document.getElementById("ssntextvalidate");
		cintextobj = document.getElementById("cintext");
		cintext = cintextobj.value;
		cwintextobj = document.getElementById("cwintext");
		cwintext = cwintextobj.value;
		checkobj = document.getElementById("check");
		check = checkobj.value;
	//} // Change for CERMS-II Release-b
}


// validate date control
function validateDate(htmlDateElement, isFrom){
	var datepickerindex = htmlDateElement.indexOf("/");
	var monthsubstr = htmlDateElement.substring(0,datepickerindex);
	var datepickerlastindex = htmlDateElement.lastIndexOf("/");
	var yearsubstr = htmlDateElement.substring(datepickerlastindex+1);
	var bIsLeap = isleap(yearsubstr);
	var datesubstr = htmlDateElement.substring(datepickerindex+1,datepickerlastindex);
	var datepickerlen = htmlDateElement.length;
	var monthArray = new Array("4","6","9","11","04","06","09");
	var month31Array = new Array("1","3","5","7","8","10","12","01","03","05","07","08");
	var monthLeapArray = new Array("2","02");

	if (datepickerindex>2 || monthsubstr>12 || isNumber(monthsubstr)==false){
		setErrorForDate(isFrom);
	}
	if (datepickerlastindex != (datepickerlen-5)) {
		setErrorForDate(isFrom);
	}
	if (datesubstr.length>2 || (datesubstr>31 && month31Array.indexOf(monthsubstr)>-1) || isNumber(datesubstr)==false) {
		setErrorForDate(isFrom);
	}
	if (datesubstr.length>2 || (datesubstr>30 && monthArray.indexOf(monthsubstr)>-1) || isNumber(datesubstr)==false) {
		setErrorForDate(isFrom);
	}
	if (datesubstr.length>2 || (bIsLeap && monthLeapArray.indexOf(monthsubstr)>-1 && datesubstr>29)) {
		setErrorForDate(isFrom);
	}
	if (datesubstr.length>2 || ((bIsLeap=="false") && monthLeapArray.indexOf(monthsubstr)>-1 && datesubstr>28)) {
		setErrorForDate(isFrom);
	}
	if (isNumber(yearsubstr)==false || yearsubstr<1800){
		setErrorForDate(isFrom);
	}

}

// checks that the type is a number
function isNumber(o) {
	  return ! isNaN (o-0) && o !== null && o.replace(/^\s\s*/, '') !== "" && o !== false;
}

// checks if it is a leap year
// source: http://www.codeproject.com/Articles/21308/Is-it-Leap-Year-Using-Javascript
// returns False, if not leap
function isleap(yr){
 if ((parseInt(yr)%4) == 0){
  if (parseInt(yr)%100 == 0){
    if (parseInt(yr)%400 != 0){
    	return "false";
    }
    if (parseInt(yr)%400 == 0){
    	return "true";
    }
  }
  if (parseInt(yr)%100 != 0){
    return "true";
  }
 }
 if ((parseInt(yr)%4) != 0){
    return "false";
 }
}

// set validation errors
function setErrorForDate(isFrom){
	if (isFrom){
		setErrorDateFromPicker();
	} else {
		setErrorDateToPicker();
	}
}

function setErrorDateFromPicker(){
	dtinputfromvalidate.style.display='block';
	hhsadatevalidate.style.display='block';
	dtinputfromvalid = false;
}

function setErrorDateToPicker(){
	dtinputtovalidate.style.display='block';
	hhsadatevalidate.style.display='block';
	dtinputtovalid = false;
}

// OnClickSearch Event Handler method
function onClickSearch ()
{
	var flagS = false;
	var contentPage = eval(getAbsoluteFramePath("emptyfile"));
	var ssntextvalid = true;
	dtinputfromvalid = true;
	dtinputtovalid = true;
	var casenumbertextvalid = true;

	if (contentPage != null)
	{
		dtinputfromvalidate.style.display='none';
		dtinputtovalidate.style.display='none';
		dtinputcomparevalidate.style.display='none';
		hhsadatevalidate.style.display='none';
		hhsassnvalidate.style.display='none';
		//hhsacasenumbervalidate.style.display='none';

		initSearchControls();

		strDQLStub="1=1 and ";
		//strDQL = "select object_name,r_modify_date,r_object_id,r_object_type,r_lock_owner,owner_name,r_link_cnt,r_is_virtual_doc,r_content_size,a_content_type,i_is_reference,r_assembled_from_id,r_has_frzn_assembly,a_compound_architecture,i_is_replica,r_policy_id,new_document,case_no,received_date,category,sub_category,doc_type,data_month,provider_number,applicant_lname,applicant_mname,applicant_fname,dob,ssn,cin_no,cwin_no from calwin_case_doc where ";
		strDQL = calwinSearchQueryHidden;
		// case_number='" + casenotxt.value + "' and category='" + catdddlstobj.value + "' and subcategory='" + subcatdddlstobj.value + "' and document_type='" + doctypedddlstobj.value + "'";
		//if ((caseno.length<7) && (showoptionslink == advSearchViewHidden)) { // Change for CERMS-II Release-b
		//if ((caseno.length<7)) { // Change for CERMS-II Release-b
			//casenumbertextvalidateobj.style.display='block'; // Change for CERMS-II Release-b
			//hhsacasenumbervalidate.style.display='block';
			//casenumbertextvalid = false; // Change for CERMS-II Release-b
		//} // Change for CERMS-II Release-b

		if(caseno != "" )
		{
			flagS = true;
			createDQLStub("case_no", caseno, true);
		}
		createDQLStub("case_no", caseno, true);
		if (catdddlst != 0)
		{
			flagS = true;
			createDQLStub("category", catdddlst, false);
		}
		if (subcatdddlst != 0)
		{
			flagS = true;
			createDQLStub("sub_category", subcatdddlst, false);
		}
		if (doctypedddlst != 0)
		{
			flagS = true;
			createDQLStub("doc_type", doctypedddlst, false);
		}
		if (datefrompicker != "" && datefrompicker != "Date")
		{
			flagS = true;
			var strDQLDateFrom = "received_date>=date('" + datefrompicker + "') and ";
			strDQL = strDQL + strDQLDateFrom;
			strDQLStub = strDQLStub + strDQLDateFrom;
			validateDate(datefrompicker, true);
		}
		if (datetopicker != "" && datetopicker != "Date")
		{
			flagS = true;
			//var strDQLDateTo = "received_date<=DATEADD(day,0,date('" + datetopicker + "')) and ";
				var strDQLDateTo = "received_date<=date('" + datetopicker + "') and ";
				strDQL = strDQL + strDQLDateTo;
				strDQLStub = strDQLStub + strDQLDateTo;
				validateDate(datetopicker, false);
				if (datefrompicker != "" && datefrompicker != "Date") {
					var datefrompickersplt = datefrompicker.split('/');
					var datetopickersplt = datetopicker.split('/');
					var dtdatefrompicker = new Date();
					var dtdatetopicker = new Date();
					dtdatefrompicker.setFullYear(datefrompickersplt[2],datefrompickersplt[0]-1,datefrompickersplt[1]);
					dtdatetopicker.setFullYear(datetopickersplt[2],datetopickersplt[0]-1,datetopickersplt[1]);
					if (dtdatefrompicker > dtdatetopicker) {
						dtinputcomparevalidate.style.display='block';
						hhsadatevalidate.style.display='block';
						dtinputtovalid = false;
					}
				}

		}
		if (processedddlst != "" && processedddlst != "ALL")
		{
			processedddlst = (processedddlst=="Yes")?1:0
			createDQLStub("new_document", processedddlst, false);
		}

		//if (showoptionslink == advSearchViewHidden) { // Change for CERMS-II Release-b
			//strDQL = strDQL + "soft_delete='0' and "; // Change for CERMS-II Release-b
		//} // Change for CERMS-II Release-b

		//if (showoptionslink == simpleSearchViewHidden) { // Change for CERMS-II Release-b
		if(datamonthlst != "")
		    {
				flagS = true;
				createDQLStub("data_month", datamonthlst, false);
			}
		if(providernumbertext != "")
		  {
			flagS = true;
			createDQLStub("provider_number", providernumbertext, false);
		 }
			if (ssntext != "")
			{
				if ((ssntext.length!=11) || (ssntext.charAt(3)!="-") || (ssntext.charAt(6)!="-")) {
					ssntextvalidateobj.style.display='block';
					hhsassnvalidate.style.display='block';
					ssntextvalid = false;
				}
				ssntextorig = ssntext;
				//alert("ssntextorig: " + ssntextorig);
				strDQL = strDQL + "("; // Change for CERMS-II Release-b
				strDQLStub = strDQLStub + "("; // Change for CERMS-II Release-b
				createDQLORStub("ssn", ssntextorig, false);
				ssntext = ssntext.slice(0, 3) + ssntext.slice(4, 6) + ssntext.slice(7);
				//alert("ssntext: " + ssntext);
				//createDQLStub("ssn", ssntext, false); // Change for CERMS-II Release-b
				createDQLStubForSSN("ssn", ssntext, false); // Change for CERMS-II Release-b
				flagS = true;
			}
			if(cintext != "")
						{
						flagS = true;
						createDQLStub("cin_no", cintext, true); // Change for CERMS-II Release-b - Post Go-Live fixes
						}
						if(cwintext != "")
						{
						flagS = true;
						createDQLStub("cwin_no", cwintext, false);
			}

			// this is used because the strDQL before this append ends with the "and" operator
			// and hence it becomes efficient to do a check as to whether an "OR" or "AND" logical DQL operator is needed
			// for the upcoming append to this string.
			strDQL = strDQL + "1=1";

		/* 	check = (check=="")?0:1;
			if (check==0){
				strDQL = strDQL + " and ";
		 		strDQL = strDQL + "soft_delete='" + check + "'";
			} else {
				strDQL = strDQL + " or ";
				strDQL = strDQL + "(" + strDQLStub + "soft_delete='" + check + "')";
			}*/
		 	check = (check=="")?0:1;
		 	dorescheck = (dorescheck=="")?0:1;
		 	if (check==0 && dorescheck==0){
				strDQL = strDQL + " and ";
		 		strDQL = strDQL + "soft_delete='" + check + "'";
		 		strDQL = strDQL + " and dores='" + dorescheck + "'";
			} //else {
				//strDQL = strDQL + " or ";
				//strDQL = strDQL + "(" + strDQLStub + "soft_delete='" + check + "' and dores='" + dorescheck + "')";
			//}
		 	else if (check==0 && dorescheck==1){
				strDQL = strDQL + " and soft_delete='0' or ";
				strDQL = strDQL + "(" + strDQLStub + "soft_delete='" + check + "' and dores='" + dorescheck + "')";
			} else if (check==1 && dorescheck==0){
				strDQL = strDQL + " and dores='0' or ";
				strDQL = strDQL + "(" + strDQLStub + "soft_delete='" + check + "' and dores='" + dorescheck + "')";
			} else {
			  strDQL = strDQL + " or ";
			  strDQL = strDQL + "(" + strDQLStub + "soft_delete='" + check + "' and dores='" + dorescheck + "')";
		    }
		/* 	if (check==0 && dorescheck==1){
		 		var strDQLSoftDel = "soft_delete='" + check + "'";
				//strDQL = strDQL + " and ";
		 			//strDQL = strDQL + "soft_delete='" + check + "'";
				//strDQL = strDQL + strDQLSoftDel;
				strDQLStub = strDQLStub + strDQLSoftDel;
				strDQL = strDQL + " or ";
				strDQL = strDQL + "(" + strDQLStub + " and dores='" + dorescheck + "')";
			}
		 	if (check==1 && dorescheck==0){
		 		var strDQLDores = "dores='" + dorescheck + "'";
				//strDQL = strDQL + " and ";
		 			//strDQL = strDQL + "dores='" + dorescheck + "'";
				//strDQL = strDQL + strDQLDores;
				strDQLStub = strDQLStub + strDQLDores;
				strDQL = strDQL + " or ";
				strDQL = strDQL + "(" + strDQLStub + " and soft_delete='" + check + "')";
			}
		 	if (check==1 && dorescheck==1){
				strDQL = strDQL + " or ";
				strDQL = strDQL + "(" + strDQLStub + "soft_delete='" + check + "' and dores='" + dorescheck + "')";
			}*/

		 	strDQL = strDQL + " and ";
		//} // Change for CERMS-II Release-b

		strDQL = strDQL + "1=1"; // this is needed to complete the DQL string, if user searches using only simple search.
		strDQL = strDQL + " order by received_date desc, category, sub_category, doc_type ENABLE (OPTIMIZE_TOP 350, SQL_DEF_RESULT_SET 350, UNCOMMITTED_READ)";
		//alert(strDQL);

		//if ((showoptionslink == advSearchViewHidden) && casenumbertextvalid && dtinputtovalid && dtinputfromvalid) { // Change for CERMS-II Release-b
		//if (casenumbertextvalid && dtinputtovalid && dtinputfromvalid) { // Change for CERMS-II Release-b
			//hhsacasenumbervalidate.style.display='none';
			//casenumbertextvalidateobj.style.display='none'; // Change for CERMS-II Release-b
			//dtinputfromvalidate.style.display='none'; // Change for CERMS-II Release-b
			//dtinputtovalidate.style.display='none'; // Change for CERMS-II Release-b
			//dtinputcomparevalidate.style.display='none'; // Change for CERMS-II Release-b
			//hhsadatevalidate.style.display='none'; // Change for CERMS-II Release-b
			//hhsassnvalidate.style.display='none'; // Change for CERMS-II Release-b
			//postComponentJumpEvent(null, "cosdsearch", "emptyfile", "queryType", "dql", "query", strDQL); // Change for CERMS-II Release-b
			//postComponentJumpEvent(null, "searchcontainer", "emptyfile", "component", "search", "queryType", "dql", "query", strDQL);
		//} // Change for CERMS-II Release-b
		//if ((showoptionslink == simpleSearchViewHidden) && ssntextvalid && dtinputtovalid && dtinputfromvalid) { // Change for CERMS-II Release-b
		//if (casenumbertextvalid && ssntextvalid && dtinputtovalid && dtinputfromvalid) { // Change for CERMS-II Release-b
		if (ssntextvalid && dtinputtovalid && dtinputfromvalid) { // Change for CERMS-II Release-b
			ssntextvalidateobj.style.display='none';
			//hhsacasenumbervalidate.style.display='none';
			casenumbertextvalidateobj.style.display='none';
			dtinputfromvalidate.style.display='none';
			dtinputtovalidate.style.display='none';
			dtinputcomparevalidate.style.display='none';
			hhsadatevalidate.style.display='none';
			hhsassnvalidate.style.display='none';

			if(flagS)
			{
		    	postComponentJumpEvent(null, "cosdsearchfullui", "emptyfile", "queryType", "dql", "query", strDQL);
			}
			//postComponentJumpEvent(null, "searchcontainer", "emptyfile", "component", "searchfullui", "queryType", "dql", "query", strDQL);
		}
		//postComponentJumpEvent(null, "searchcontainer", "emptyfile", "component", "search", "queryType", "dql", "query", strDQL);
		//if (typeof text.autoComplete != "undefined" && text.autoComplete != null)
		//{
		// add the search string to client-side's auto-complete suggestions
		//text.autoComplete.addEntry(strValue);
		//var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		//prefs.setCallback("onUpdateACCallBack");
		//postInlineServerEvent(null, prefs, null, null, "onUpdateAutoCompleteData", null, null);
	}
//}
//}
}