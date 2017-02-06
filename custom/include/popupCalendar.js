var isNav = (navigator.appName == "Netscape");
var isMac = (navigator.platform.toLowerCase().indexOf("mac")!=-1)
var isMacIE = isMac&&!isNav;
var CAL_CELL_WIDTH = 28;
var CAL_CELL_HEIGHT = 16;
var CAL_FLIPPER_WIDTH = 19;
var CAL_CAPTION_HEIGHT = 19;
var CAL_ZINDEX = 999;
var CAL_LONG_DATE = 1;
var CAL_MEDIUM_DATE = 2;
var CAL_SHORT_DATE = 3;
var CLASS_CAL                = "calendar";
var CLASS_CAL_CAPTION        = "calendar_caption";
var CLASS_CAL_MONTH_FLIPPER  = "calendar_month_flipper";
var CLASS_CAL_DAY            = "calendar_day";
var CLASS_CAL_CELL           = "calendar_cell";
var CLASS_CAL_CELL_SELECTED  = "calendar_cell_selected";
var CLASS_CAL_CELL_TODAY     = "calendar_cell_today";
var CLASS_CAL_CELL_LOWLIGHT  = "calendar_cell_lowlight";
var pcPopupTimer = null;
var pcPopupDIV = null;
var nlsMultiValueString = null;
function popupCalendar(formId, inputId, strDays, strShortMonths, strLongMonths, firstDayOfWeek,
dateFormat, strShortDateFormat, strMediumDateFormat, strLongDateFormat,
strPrevMonthText, strPrevMonthImage, strNextMonthText, strNextMonthImage, strMultiValue)
{
hidePopup();
if (typeof requestInProgress != 'undefined' && requestInProgress){
return false;
}
pcPopupDIV = window.document.createElement("DIV");
pcPopupDIV.m_formId = formId;
pcPopupDIV.m_inputId = inputId;
pcPopupDIV.m_strDays = toArray(strDays);
pcPopupDIV.m_strShortMonths = toArray(strShortMonths);
pcPopupDIV.m_strLongMonths = toArray(strLongMonths);
pcPopupDIV.m_firstDayOfWeek = firstDayOfWeek;
pcPopupDIV.m_dateFormat = dateFormat;
pcPopupDIV.m_strShortDateFormat = strShortDateFormat;
pcPopupDIV.m_strMediumDateFormat = strMediumDateFormat;
pcPopupDIV.m_strLongDateFormat = strLongDateFormat;
pcPopupDIV.m_strPrevMonthImage = strPrevMonthImage;
pcPopupDIV.m_strPrevMonthText = strPrevMonthText;
pcPopupDIV.m_strNextMonthImage = strNextMonthImage;
pcPopupDIV.m_strNextMonthText = strNextMonthText;
nlsMultiValueString = strMultiValue;
pcPopupDIV.m_todaysDate = getTodaysDate();
pcPopupDIV.m_inputDate = getInputDate();
if (pcPopupDIV.m_inputDate == null)
{
pcPopupDIV.m_currentMonth = new Date(getYear(pcPopupDIV.m_todaysDate), pcPopupDIV.m_todaysDate.getMonth(), 1);
}
else
{
pcPopupDIV.m_currentMonth = new Date(getYear(pcPopupDIV.m_inputDate), pcPopupDIV.m_inputDate.getMonth(), 1);
}
pcPopupDIV.className = CLASS_CAL;
pcPopupDIV.onmouseover = onMouseOver;
if ((g_clientInfo.isBrowser(ClientInfo.MSIE)))
{
pcPopupDIV.onmouseleave = onMouseOut;
}
else
{
pcPopupDIV.onmouseout = onMouseOut;
}
pcPopupDIV.style.width = CAL_CELL_WIDTH * 7 + (g_clientInfo.isBrowser(ClientInfo.NETSCAPE) ? 0 : 2) + "px";
pcPopupDIV.style.height = CAL_CELL_HEIGHT * 7 + CAL_CAPTION_HEIGHT + (g_clientInfo.isBrowser(ClientInfo.NETSCAPE) ? 0 : 2) + "px";
with (pcPopupDIV.style)
{
position = "absolute";
visibility = "visible";
overflow = "hidden";
zIndex = CAL_ZINDEX;
borderStyle = "solid";
borderWidth = 1;
cursor = "pointer";
}
// set position relative to the input control's right button image
var parentObj = document.images[inputId + "_anchor"];
var topPos        = 0;
var leftPos       = 0;
var scrollTopPos  = 0;
var scrollLeftPos = 0;
topPos = parentObj.height+3;
// don't consider body's offsets (which are 0 anyway) or body's
do
{
topPos        += parentObj.offsetTop;
leftPos       += parentObj.offsetLeft;
scrollTopPos  += parentObj.scrollTop;
scrollLeftPos += parentObj.scrollLeft;
parentObj = parentObj.offsetParent;
} while (parentObj != null && parentObj != document.body);
topPos  -= scrollTopPos;
leftPos -= scrollLeftPos;
var edgeSpace = (g_clientInfo.isBrowser(ClientInfo.MSIE)) ? 4 : 18;
var windowTopEdge  = document.body.scrollTop;
var windowHeight   = document.body.clientHeight;
var windowLeftEdge = document.body.scrollLeft;
var windowWidth    = document.body.clientWidth;
var windowBottomEdge = (windowTopEdge  + windowHeight) - edgeSpace;
var windowRightEdge  = (windowLeftEdge + windowWidth)  - edgeSpace;
var bottomEdge = topPos + parseInt(pcPopupDIV.style.height);
var rightEdge = leftPos + parseInt(pcPopupDIV.style.width);
if (bottomEdge > windowBottomEdge)
{
var adjustUp = document.images[inputId + "_anchor"].height + parseInt(pcPopupDIV.style.height) + 6;
topPos -= (adjustUp-40); //topPos  -= adjustUp; (Updated by Arun)
bottomEdge -= adjustUp;
leftPos += (adjustUp/4); //Added by Arun
}
if (rightEdge > windowRightEdge)
{
leftPos -= (rightEdge - windowRightEdge);
}
if (leftPos < windowLeftEdge)
{
leftPos = windowLeftEdge + 2;
}
pcPopupDIV.style.left = leftPos + "px";
pcPopupDIV.style.top  = topPos  + "px";
pcPopupDIV.id = "CalContainer";
renderCalendar();
hideIntersectingSelects(leftPos, topPos, rightEdge, bottomEdge);
window.document.body.appendChild(pcPopupDIV);
}
function onPrevMonth()
{
var month = pcPopupDIV.m_currentMonth.getMonth();
var year = getYear(pcPopupDIV.m_currentMonth);
month -= 1;
if (month < 0)
{
month = 11;
year -= 1;
}
pcPopupDIV.m_currentMonth = new Date(year, month, 1);
renderCalendar();
if(g_clientInfo.isPlatform(ClientInfo.MACOS) && g_clientInfo.isBrowser(ClientInfo.MSIE))
return false;
}
function onNextMonth()
{
var month = pcPopupDIV.m_currentMonth.getMonth();
var year = getYear(pcPopupDIV.m_currentMonth);
month += 1;
if (month > 11)
{
month = 0;
year += 1;
}
pcPopupDIV.m_currentMonth = new Date(year, month, 1);
renderCalendar();
if(g_clientInfo.isPlatform(ClientInfo.MACOS) && g_clientInfo.isBrowser(ClientInfo.MSIE))
return false;
}
function onClickDay()
{
setInputDate(this.m_date);
hidePopup();
}
function onSelectDay()
{
this.className = CLASS_CAL_CELL_SELECTED;
if(typeof pcPopupDIV.childNodes != 'undefined' && pcPopupDIV.childNodes)
{
for (var i = 0; i < pcPopupDIV.childNodes.length; i++)
{
var elementDIV = pcPopupDIV.childNodes[i];
if (elementDIV != this && elementDIV.className == CLASS_CAL_CELL_SELECTED)
{
elementDIV.className = elementDIV.m_strBaseClass;
}
}
}
}
function renderCalendar()
{
while (pcPopupDIV.childNodes.length > 0)
{
pcPopupDIV.removeChild(pcPopupDIV.childNodes[0]);
}
var currentMonth = pcPopupDIV.m_currentMonth;
var width = parseInt(pcPopupDIV.style.width) + (g_clientInfo.isBrowser(ClientInfo.NETSCAPE) ? 2 : 0);
var height = parseInt(pcPopupDIV.style.height) + (g_clientInfo.isBrowser(ClientInfo.NETSCAPE) ? 2 : 0);
var gasketDIV = renderElement(0, 0, width, height, CLASS_CAL_CAPTION, "<iframe frameborder=0 width="+ width +" height="+ height +"></iframe>")
gasketDIV.style.borderStyle = "solid";
gasketDIV.style.borderWidth = 1;
gasketDIV.style.borderColor = "#333";
var strMonthAndYear = pcPopupDIV.m_strLongMonths[ currentMonth.getMonth() ] + " " + getYear(currentMonth);
renderElement(CAL_FLIPPER_WIDTH, 0, width - CAL_FLIPPER_WIDTH, CAL_CAPTION_HEIGHT, CLASS_CAL_CAPTION, strMonthAndYear);
renderFlipperElement(0, 0, CAL_FLIPPER_WIDTH, CAL_CAPTION_HEIGHT, pcPopupDIV.m_strPrevMonthText, pcPopupDIV.m_strPrevMonthImage, onPrevMonth);
renderFlipperElement(width - CAL_FLIPPER_WIDTH - 1, 0, width - 1, CAL_CAPTION_HEIGHT, pcPopupDIV.m_strNextMonthText, pcPopupDIV.m_strNextMonthImage, onNextMonth);
for (var i = 0; i < 7; i++)
{
var xPos = i * CAL_CELL_WIDTH;
var yPos = CAL_CAPTION_HEIGHT;
var strDay = pcPopupDIV.m_strDays[ (i + pcPopupDIV.m_firstDayOfWeek) % 7 ];
renderElement(xPos, yPos, xPos + CAL_CELL_WIDTH, yPos + CAL_CELL_HEIGHT, CLASS_CAL_DAY, strDay);
}
var nPrevDays = (currentMonth.getDay() - pcPopupDIV.m_firstDayOfWeek) % 7;
if (nPrevDays < 0)
{
nPrevDays += 7;
}
var millisecsInHour = 60000 * 60;
var millisecsInDay = millisecsInHour * 24;
var day = currentMonth.getTime() - (nPrevDays * millisecsInDay) + millisecsInHour;
for (var i = 0; i < (7*6); i++)
{
var date = new Date(day);
var xPos = (i % 7) * CAL_CELL_WIDTH;
var yPos = Math.floor(i / 7) * CAL_CELL_HEIGHT + CAL_CELL_HEIGHT + CAL_CAPTION_HEIGHT;
renderDateElement(xPos, yPos, xPos + CAL_CELL_WIDTH, yPos + CAL_CELL_HEIGHT, date);
day += millisecsInDay;
}
}
function renderDateElement(leftPos, topPos, rightPos, bottomPos, date)
{
var strBaseClass = CLASS_CAL_CELL;
if (onSameDay(date, pcPopupDIV.m_todaysDate))
{
strBaseClass = CLASS_CAL_CELL_TODAY;
}
else if (date.getMonth() != pcPopupDIV.m_currentMonth.getMonth())
{
strBaseClass = CLASS_CAL_CELL_LOWLIGHT;
}
var strClass = strBaseClass;
if (pcPopupDIV.m_inputDate != null)
{
if (onSameDay(date, pcPopupDIV.m_inputDate))
{
strClass = CLASS_CAL_CELL_SELECTED;
}
}
var elementDIV = renderElement(leftPos, topPos, rightPos, bottomPos, strClass, date.getDate());
elementDIV.m_date = date;
elementDIV.m_strBaseClass = strBaseClass;
elementDIV.onmousedown = onSelectDay;
elementDIV.onclick = onClickDay;
}
function renderFlipperElement(leftPos, topPos, rightPos, bottomPos, strAltText, strImage, fnOnClick)
{
var strHtml = "<img src='" + strImage + "' alt='" + strAltText + "' width=19 height=19>";
var elementDIV = renderElement(leftPos, topPos, rightPos, bottomPos, CLASS_CAL_MONTH_FLIPPER, strHtml);
elementDIV.onmousedown = fnOnClick;
}
function renderElement(leftPos, topPos, rightPos, bottomPos, strClass, strHtml)
{
var elementDIV = document.createElement("DIV");
elementDIV.className = strClass;
elementDIV.innerHTML = strHtml;
elementDIV.align = "center";
with (elementDIV.style)
{
position = "absolute";
visibility = "inherit";
overflow = "hidden";
zIndex = CAL_ZINDEX + 1;
left = leftPos + "px";
top = topPos + "px";
width = rightPos - leftPos + "px";
height = bottomPos - topPos + "px";
}
pcPopupDIV.appendChild(elementDIV);
return elementDIV;
}
function onMouseOver()
{
showPopup(true);
}
function onMouseOut(e)
{
e = window.event || e;
var to = e.toElement || e.relatedTarget;
if (e.type == "mousemove" && !to) return;
if ((isInside(this, to) == false))
{
showPopup(false);
}
}
function isInside(container, elem)
{
var bIsInside = false;
while (elem != null)
{
if (elem == container)
{
bIsInside = true;
break;
}
else
{
elem = elem.parentNode;
}
}
return bIsInside;
}
function DateFormatTokenizer(strDateFormat)
{
this.m_strDateFormat = strDateFormat;
this.m_index = 0;
nextToken(this);
}
function hasMoreTokens(tokenizer)
{
return (tokenizer.m_nextToken != "");
}
function nextToken(tokenizer)
{
var token = tokenizer.m_nextToken;
tokenizer.m_nextToken = "";
var len = tokenizer.m_strDateFormat.length;
if (tokenizer.m_index < len)
{
var ch = tokenizer.m_strDateFormat.charAt(tokenizer.m_index);
do
{
tokenizer.m_nextToken += ch;
tokenizer.m_index++;
} while (tokenizer.m_index < len && tokenizer.m_strDateFormat.charAt(tokenizer.m_index) == ch);
}
return token;
}
function getInputDate()
{
var day = -1;
var month = -1;
var year = -1;
var strDate = document.forms[pcPopupDIV.m_formId].elements[pcPopupDIV.m_inputId].value;
if (strDate != "")
{
var dayIdx = -1;
var monthIdx = -1;
var yearIdx = -1;
var strUpperDate = strDate.toUpperCase();
var shortIsAllDigit = true;
var longIsAllDigit = true;
for (var j = 0; j < pcPopupDIV.m_strShortMonths[0].length; j++)
{
var ch = pcPopupDIV.m_strShortMonths[0].charAt(j);
if (ch < '0' || ch > '9')
{
shortIsAllDigit = false;
break;
}
}
for (var j = 0; j < pcPopupDIV.m_strLongMonths[0].length; j++)
{
var ch = pcPopupDIV.m_strLongMonths[0].charAt(j);
if (ch < '0' || ch > '9')
{
longIsAllDigit = false;
break;
}
}
for (var i = 0; i < 12; i++)
{
if (shortIsAllDigit == false)
{
monthIdx = strUpperDate.indexOf(pcPopupDIV.m_strShortMonths[i].toUpperCase());
}
if (monthIdx == -1 )
{
if (longIsAllDigit == false)
{
monthIdx = strUpperDate.indexOf(pcPopupDIV.m_strLongMonths[i].toUpperCase());
}
}
if (monthIdx != -1)
{
month = i + 1;
break;
}
}
if (month != -1)
{
var index = 0;
var tokenizer = new DateFormatTokenizer(pcPopupDIV.m_strMediumDateFormat);
while (hasMoreTokens(tokenizer))
{
var strToken = nextToken(tokenizer);
if (strToken == "d" || strToken == "dd")
{
index = indexOfDigit(strDate, index);
if (index == -1)
{
break;
}
else if (strDate.charAt(index) == '0')
{
index +=1;
}
day = parseInt(strDate.substring(index));
index += ("" + day).length + 1;
}
else if (strToken == "yy" || strToken == "yyyy")
{
index = indexOfDigit(strDate, index);
if (index == -1)
{
break;
}
var numDigit = 0;
for (var j = index; j < strDate.length; j++)
{
var ch = strDate.charAt(j);
if (ch >= '0' && ch <='9')
{
numDigit += 1;
}
else
{
break;
}
}
if (numDigit == 2 || numDigit == 4)
{
year = parseInt(strDate.substring(index),10);
index += ("" + year).length + 1;
}
}
}
}
else
{
var index = 0;
var tokenizer = new DateFormatTokenizer(pcPopupDIV.m_strShortDateFormat);
while (hasMoreTokens(tokenizer))
{
var strToken = nextToken(tokenizer);
if (strToken == "d" || strToken == "dd")
{
index = indexOfDigit(strDate, index);
if (index == -1)
{
break;
}
else if (strDate.charAt(index) == '0')
{
index +=1;
}
day = parseInt(strDate.substring(index));
index += ("" + day).length + 1;
}
else if (strToken == "m" || strToken == "mm" || strToken == "mmm")
{
index = indexOfDigit(strDate, index);
if (index == -1)
{
break;
}
else if (strDate.charAt(index) == '0')
{
index +=1;
}
month = parseInt(strDate.substring(index));
index += ("" + month).length + 1;
}
else if (strToken == "yy" || strToken == "yyyy")
{
index = indexOfDigit(strDate, index);
if (index == -1)
{
break;
}
var numDigit = 0;
for (var j = index; j < strDate.length; j++)
{
var ch = strDate.charAt(j);
if (ch >= '0' && ch <='9')
{
numDigit += 1;
}
else
{
break;
}
}
if (numDigit == 2 || numDigit == 4)
{
year = parseInt(strDate.substring(index),10);
index += ("" + year).length + 1;
}
}
}
}
if (day < 1 || day > 31 || month < 1 || month > 12 || year < 0)
{
return null;
}
if (year < 45)
{
year += 2000;
}
else if (year < 100)
{
year += 1900;
}
return new Date(year, month - 1, day);
}
return null;
}
function setInputDate(date)
{
var strDate = "";
var strDateFormat;
if (pcPopupDIV.m_dateFormat == CAL_SHORT_DATE)
{
strDateFormat = pcPopupDIV.m_strShortDateFormat;
}
else if (pcPopupDIV.m_dateFormat == CAL_MEDIUM_DATE)
{
strDateFormat = pcPopupDIV.m_strMediumDateFormat;
}
else if (pcPopupDIV.m_dateFormat == CAL_LONG_DATE)
{
strDateFormat = pcPopupDIV.m_strLongDateFormat;
}
var tokenizer = new DateFormatTokenizer(strDateFormat);
while (hasMoreTokens(tokenizer))
{
strToken = nextToken(tokenizer);
if (strToken == "d")
{
strDate += date.getDate();
}
else if (strToken == "dd")
{
if (date.getDate() < 10)
{
strDate += "0";
}
strDate += date.getDate();
}
else if (strToken == "ddd")
{
strDate += pcPopupDIV.m_strDays[date.getDay()];
}
else if (strToken == "m")
{
strDate += date.getMonth() + 1;
}
else if (strToken == "mm")
{
if (date.getMonth() + 1 < 10)
{
strDate += "0";
}
strDate += date.getMonth() + 1;
}
else if (strToken == "mmm")
{
strDate += pcPopupDIV.m_strShortMonths[date.getMonth()];
}
else if (strToken == "mmmm")
{
strDate += pcPopupDIV.m_strLongMonths[date.getMonth()];
}
else if (strToken == "yyyy")
{
strDate += getYear(date);
}
else if (strToken == "yy")
{
strDate += ("" + getYear(date)).substring(2);
}
else
{
strDate += strToken;
}
}
var oldDate = getInputDate();
var textCtrl = document.forms[pcPopupDIV.m_formId].elements[pcPopupDIV.m_inputId];
//  var multiValueString = document.forms[pcPopupDIV.m_formId].getElementsByName("multiValuedString");
if (typeof(nlsMultiValueString) != "undefined" && textCtrl.value == nlsMultiValueString)
{
var time = document.forms[pcPopupDIV.m_formId].elements[pcPopupDIV.m_inputId].name;
var pos = time.lastIndexOf("date");
var timeprefix = time.substr(0,pos);
var timeCtrlName = timeprefix.concat("time").concat(time.substring(pos+4));
var timeCtrl = document.forms[pcPopupDIV.m_formId].elements[timeCtrlName];
if (timeCtrl == undefined)
{
var hourCtrlName = timeprefix.concat("hour").concat(time.substring(pos+4));
var minuteCtrlName = timeprefix.concat("minute").concat(time.substring(pos+4));
var secondCtrlName = timeprefix.concat("second").concat(time.substring(pos+4));
var hourCtrl = document.forms[pcPopupDIV.m_formId].elements[hourCtrlName];
var minuteCtrl = document.forms[pcPopupDIV.m_formId].elements[minuteCtrlName];
var secondCtrl = document.forms[pcPopupDIV.m_formId].elements[secondCtrlName];
}
//timeCtrl.value = "10:46:47 PM";
}
if (typeof(nlsMultiValueString) != "undefined" && strDate != nlsMultiValueString)
{
if (timeCtrl){
timeCtrl.disabled = false;
timeCtrl.enabled = true;
}
else if(hourCtrl && minuteCtrl && secondCtrl){
hourCtrl.disabled = false;
hourCtrl.enabled = true;
minuteCtrl.disabled = false;
minuteCtrl.enabled = true;
secondCtrl.disabled = false;
secondCtrl.enabled = true;
}
}
textCtrl.value = strDate;
// fire on change event as IE doesn't do this when the value is changed
if (oldDate != date && textCtrl.onchange != "undefined" && textCtrl.onchange != null)
{
textCtrl.onchange();
}
}
function getTodaysDate()
{
var now = new Date();
var today = new Date(getYear(now), now.getMonth(), now.getDate());
return today;
}
function indexOfDigit(strString, indexFrom)
{
if (indexFrom == null)
{
indexFrom = 0;
}
for (j = indexFrom; j < strString.length; j++)
{
var ch = strString.charAt(j);
if (ch >= '0' && ch <='9')
{
return j;
}
}
return -1;
}
function toArray(strValues)
{
var values = [];
var iFrom = 0;
var iTo = 0;
while (true)
{
iTo = strValues.indexOf(",", iFrom)
if (iTo == -1)
{
values[values.length] = strValues.substring(iFrom);
break;
}
else
{
values[values.length] = strValues.substring(iFrom, iTo);
iFrom = iTo + 1;
}
}
return values;
}
function showPopup(bShow)
{
if (pcPopupTimer != null)
{
clearTimeout(pcPopupTimer);
pcPopupTimer = null;
}
if (bShow == false)
{
pcPopupTimer = setTimeout("hidePopup()", 750);
}
}
function hidePopup()
{
if (pcPopupDIV != null)
{
document.body.removeChild(pcPopupDIV);
pcPopupDIV = null;
showHiddenIntersectingSelects();
}
}
function getYear(date)
{
var year = date.getFullYear();
return year;
}
function onSameDay(date1, date2)
{
return (date1.getDate()  == date2.getDate() &&
date1.getMonth() == date2.getMonth() &&
getYear(date1)   == getYear(date2));
}
function HiddenSelectCtrl(element)
{
this.m_element = element;
this.m_visibility = element.style.visibility;
}
function hideIntersectingSelects(left, top, right, bottom)
{
pcHiddenSelectCtrls = [];
for (var j = 0; j < document.forms.length; j++)
{
var form = document.forms[j];
for (var i = 0; i < form.elements.length; i++)
{
var element = form.elements[i];
if (element.type && element.type.indexOf("select") == 0)
{
var parentObj = element;
var leftPos       = 0;
var topPos        = 0;
var scrollTopPos  = 0;
var scrollLeftPos = 0;
do
{
leftPos       += parentObj.offsetLeft;
topPos        += parentObj.offsetTop;
scrollTopPos  += parentObj.scrollTop;
scrollLeftPos += parentObj.scrollLeft;
parentObj = parentObj.offsetParent;
} while (parentObj != null && parentObj != document.body);
topPos  -= scrollTopPos;
leftPos -= scrollLeftPos;
var rightPos = leftPos + element.offsetWidth;
var bottomPos = topPos + element.offsetHeight;
if ( left < rightPos && top < bottomPos && right > leftPos && bottom > topPos)
{
pcHiddenSelectCtrls[pcHiddenSelectCtrls.length] = new HiddenSelectCtrl(element);
element.style.visibility = "hidden";
}
}
}
}
}
function showHiddenIntersectingSelects()
{
if (typeof(pcHiddenSelectCtrls) != "undefined")
{
for (i = 0; i < pcHiddenSelectCtrls.length; i++)
{
var element = pcHiddenSelectCtrls[i];
element.m_element.style.visibility = element.m_visibility;
}
}
}
function alertDate(date)
{
alert(date.getDate() + "/" + (date.getMonth() + 1) + "/" + getYear(date) + " (dd/mm/yy)");
}
