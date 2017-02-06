/*
******************************************************************
*   File Name: DateInputTag.java
*   Description: This renders the custom calwin dateinput tag.
*   			 This file is based on a copy of com.documentum.
*                web.form.control.DateInputTag. The Documentum 
*                DateInputTag class cannot be extended here because
*                some of the methods to be overridden are defined as 
*                static
*   Author: Arun Shankar – HP 
*   Creation Date: 21-Apr-2013 
*   Version: 1.0
******************************************************************
*/
package com.cosd.greenbuild.calwin.controls;

import com.documentum.web.form.control.*;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfTime;
import com.documentum.nls.NlsResourceBundle;
import com.documentum.web.common.*;
import com.documentum.web.form.Control;
import com.documentum.web.form.Form;
import com.documentum.web.util.DateUtil;
import com.documentum.web.util.SafeHTMLString;
import java.awt.Dimension;
import java.io.IOException;
import java.util.*;
import javax.servlet.jsp.JspWriter;

/**
* ControlTag class of Calwin dateinput control.
* Developed based on com.documentum.web.form.control.DateInputTag
* 
* @author Arun Shankar
* @version 1.0
*/
public class DateInputTag extends StringInputControlTag
{
    /* 
     * Copied from com.documentum.web.form.control.DateInputTag 
     * Changed class type from static to protected
     */
    protected class DateFormatTokenizer
    {

        public boolean hasMoreTokens()
        {
            return m_strNextToken.length() > 0;
        }

        public String nextToken()
        {
            String strToken = m_strNextToken;
            m_strNextToken = "";
            int len = m_strDateFormat.length();
            if(m_index < len)
            {
                char ch = m_strDateFormat.charAt(m_index);
                do
                {
                    m_strNextToken += ch;
                    m_index++;
                } while(m_index < len && m_strDateFormat.charAt(m_index) == ch);
            }
            return strToken;
        }

        private String m_strDateFormat;
        private String m_strNextToken;
        private int m_index;

        public DateFormatTokenizer(String strDateFormat)
        {
            m_strDateFormat = strDateFormat;
            m_index = 0;
            nextToken();
        }
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public DateInputTag()
    {
        m_strWidth = null;
        m_strOnselect = null;
        m_strDay = null;
        m_strMonth = null;
        m_strYear = null;
        m_strFromYear = null;
        m_strToYear = null;
        m_strDateSelector = null;
        m_strDateFormat = null;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void release()
    {
        super.release();
        m_strDateFormat = null;
        m_strWidth = null;
        m_strOnselect = null;
        m_strDay = null;
        m_strMonth = null;
        m_strYear = null;
        m_strFromYear = null;
        m_strToYear = null;
        m_strDateSelector = null;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setWidth(String strWidth)
    {
        m_strWidth = strWidth;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setToyear(String strToYear)
    {
        m_strToYear = strToYear;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setFromyear(String strFromYear)
    {
        m_strFromYear = strFromYear;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setYear(String strYear)
    {
        m_strYear = strYear;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setMonth(String strMonth)
    {
        m_strMonth = strMonth;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setDay(String strDay)
    {
        m_strDay = strDay;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setOnselect(String strOnselect)
    {
        m_strOnselect = strOnselect;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setDateselector(String strDateSelector)
    {
        m_strDateSelector = strDateSelector;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    public void setDateformat(String strDateFormat)
    {
        m_strDateFormat = strDateFormat;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    protected Class getControlClass()
    {
        return com.documentum.web.form.control.DateInput.class;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    protected boolean isDatafieldHandlerClass(Class cl)
    {
        return cl.equals(com.documentum.web.form.control.DateInputTag.class);
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    protected void setControlProperties(Control control)
    {
        super.setControlProperties(control);
        DateInput input = (DateInput)control;
        if(m_strDateFormat != null)
            if(m_strDateFormat.equalsIgnoreCase("short"))
                input.setDateFormat(3);
            else
            if(m_strDateFormat.equalsIgnoreCase("medium"))
                input.setDateFormat(2);
            else
            if(m_strDateFormat.equalsIgnoreCase("long"))
                input.setDateFormat(1);
            else
                throw new WrapperRuntimeException("dateinput tag 'dateformat' attribute value must be 'short', 'medium' or 'long'");
        if(m_strDay != null && m_strDay.length() > 0)
            input.setDay(Integer.valueOf(m_strDay).intValue());
        if(m_strMonth != null && m_strMonth.length() > 0)
            input.setMonth(Integer.valueOf(m_strMonth).intValue());
        if(m_strYear != null && m_strYear.length() > 0)
            input.setYear(Integer.valueOf(m_strYear).intValue());
        if(m_strDateSelector != null)
            if(m_strDateSelector.equalsIgnoreCase("fields"))
                input.setDateSelector(0);
            else
            if(m_strDateSelector.equalsIgnoreCase("calendar"))
                input.setDateSelector(1);
            else
                throw new WrapperRuntimeException("dateinput tag 'dateselector' attribute value must be 'basic' or 'calendar'");
        if(m_strFromYear != null)
            input.setFromYear(Integer.valueOf(m_strFromYear).intValue());
        if(m_strToYear != null)
            input.setToYear(Integer.valueOf(m_strToYear).intValue());
        if(m_strWidth != null)
            input.setWidth(Integer.valueOf(m_strWidth).intValue());
        if(m_strOnselect != null)
            input.setEventHandler("onselect", m_strOnselect, null);
        if(getDatafield() != null && isDatafieldHandlerClass(com.documentum.web.form.control.DateTimeTag.class))
        {
            String strResult = resolveDatafield(getDatafield());
            if(strResult != null)
            {
                Date date = new Date((new Long(strResult)).longValue());
                DfTime dfTime = new DfTime(date);
                input.setYear(dfTime.getYear());
                input.setMonth(dfTime.getMonth());
                input.setDay(dfTime.getDay());
            }
        }
        if(input.getYear() != -1 && input.getMonth() != -1 && input.getDay() != -1 && !input.isValidDate())
            throw new WrapperRuntimeException("Day, month, and year do not make valid date");
        String strValue = input.toDateString(3);
        if(strValue == null)
        {
            strValue = input.getValue();
            if(strValue == null)
                strValue = getString("MSG_DATE");
        }
        setValue(strValue);
        input.setValue(strValue);
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    protected void renderStart(JspWriter jspwriter)
        throws IOException
    {
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    protected void renderEnd(JspWriter out)
        throws IOException
    {
        DateInput dateInput = (DateInput)getControl();
        if(dateInput.isVisible())
        {
            int width = dateInput.getWidth();
            if(width == 0)
                width = 150;
            renderDate(out, dateInput, width);
        }
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */
    protected void renderDate(JspWriter out, DateInput dateInput, int width)
        throws IOException
    {
        if(dateInput.getDateSelectorType() == 0 || AccessibilityService.isAllAccessibilitiesEnabled())
            renderBasic(out, dateInput, width);
        else
            renderCalendar(out, dateInput, width);
    }

    /* 
     * Based on a copy from com.documentum.web.form.control.DateInputTag 
     * Modified to include the calwin javascript file*/
    /**
    * Render calendar using calwin popupCalendar.js
    */ 
    protected void renderCalendar(JspWriter out, DateInput dateInput, int width)
        throws IOException
    {
        DfLogger.debug(this, "Enter renderCalendar DateInput=" + dateInput.getValue() + " "
                + "width=" + Integer.toString(width), null, null);
 
        if(dateInput.isVisible())
        {
            StringBuffer buf = new StringBuffer(256);
            String title = dateInput.getToolTip();
            String strClass = dateInput.getCssClass();
            String strStyle = dateInput.getCssStyle();
            IThemeResolver themeResolver = BrandingService.getThemeResolver();
            String strDropDownImage = themeResolver.getResourcePath(dateInput.isEnabled() ? "images/date/dropdown.gif" : "images/date/dropdown_disabled.gif", getForm().getPageContext(), false);
            String strPrevMonthImage = themeResolver.getResourcePath("images/date/prevmonth.gif", getForm().getPageContext(), false);
            String strNextMonthImage = themeResolver.getResourcePath("images/date/nextmonth.gif", getForm().getPageContext(), false);
            Dimension imageExtents = ImageService.getDimensions(strDropDownImage, getForm().getPageContext());
            Calendar calendar = Calendar.getInstance(LocaleService.getLocale());
            int firstDayOfWeek = calendar.getFirstDayOfWeek();
            StringBuffer dateFormatBuf = new StringBuffer();
            renderJavaScriptString(dateFormatBuf, getDateFormat(3));
            String strShortDateFormat = dateFormatBuf.toString();
            dateFormatBuf.delete(0, dateFormatBuf.length());
            renderJavaScriptString(dateFormatBuf, getDateFormat(2));
            String strMediumDateFormat = dateFormatBuf.toString();
            dateFormatBuf.delete(0, dateFormatBuf.length());
            renderJavaScriptString(dateFormatBuf, getDateFormat(1));
            String strLongDateFormat = dateFormatBuf.toString();
            // COSD - AS - 03/19/2013 - Replace the string buffer to load custom javascript file
            //buf.append("<script src='").append(Form.makeUrl(getForm().getPageContext().getRequest(), "wdk/include/popupCalendar.js")).append("' type='text/javascript'></script>");
            buf.append("<script src='").append(Form.makeUrl(getForm().getPageContext().getRequest(), "custom/include/popupCalendar.js")).append("' type='text/javascript'></script>");            
            buf.append("<input type='text' ").append(renderNameAndId(getControl(), "date"));
            if(strClass != null)
                buf.append(" class='").append(strClass).append("'");
            if(strStyle != null)
                buf.append(" style='").append(strStyle).append("'");
            renderTabIndex(buf);
            if(title != null)
                buf.append(" title=\"").append(SafeHTMLString.escapeAttribute(title)).append("\"");
            
            //COSD - AS - 03-Mar-2013
            //Remove setting default value to 'date'
            String strDate = dateInput.toDateString(dateInput.getDateFormat());
/*            if(strDate == null || strDate.length() == 0)
            {
                strDate = dateInput.getValue();
                if(strDate == null || strDate.length() == 0)
                {
                    strDate = getString("MSG_DATE");
                    setValue(strDate);
                    dateInput.setValue(strDate);
                    dateInput.updateOldValue();
                }
            }*/
            buf.append(" value='").append(formatAttribute(strDate)).append("'");
            buf.append(" style='width:").append(Math.max(95, width - (int)imageExtents.getWidth())).append("px'");
            if(!dateInput.isEnabled())
                buf.append(" disabled='true'");
            renderEventArg(buf, "onchange", "onselect");
            buf.append("/>");
            buf.append("<img name='").append(dateInput.getElementName("date")).append("_anchor' src='").append(strDropDownImage).append("' width=").append((int)imageExtents.getWidth()).append(" height=").append((int)imageExtents.getHeight());
            if(dateInput.isEnabled())
            {
                buf.append(" alt='").append(title == null ? "" : title).append(" ").append(getString("MSG_CALANDAR")).append("'");
                buf.append(" onclick=\"popupCalendar('").append(getForm().getTopForm().getElementName()).append("','").append(dateInput.getElementName("date")).append("','").append(toStringFromStringArray(getShortWeekdays())).append("','").append(toStringFromStringArray(getShortMonths())).append("','").append(toStringFromStringArray(getMonths())).append("',").append(firstDayOfWeek - 1).append(",").append(dateInput.getDateFormat()).append(",'").append(strShortDateFormat).append("','").append(strMediumDateFormat).append("','").append(strLongDateFormat).append("','").append(getString("MSG_PREV_MONTH")).append("','").append(strPrevMonthImage).append("','").append(getString("MSG_NEXT_MONTH")).append("','").append(strNextMonthImage).append("','").append(getString("MSG_MULTIPLE_VALUES")).append("');\"");
            }
            buf.append(" onmousemove=\"onMouseOut('").append("');\"");
            buf.append("/>");
            out.write(buf.toString());
        }

        DfLogger.debug(this, "Exit renderCalendar", null, null);        
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */  
    protected void renderBasic(JspWriter out, DateInput dateInput, int width)
        throws IOException
    {
        if(dateInput.isVisible())
        {
            StringBuffer buf = new StringBuffer(1024);
            for(DateFormatTokenizer tokenizer = new DateFormatTokenizer(getDateFormat(3)); tokenizer.hasMoreTokens();)
            {
                String strToken = tokenizer.nextToken();
                if(strToken.equalsIgnoreCase("d") || strToken.equalsIgnoreCase("dd"))
                    renderDropdown(buf, dateInput, dateInput.getDay(), "day", "MSG_DAY", 1, 31, width / 3);
                else
                if(strToken.equalsIgnoreCase("m") || strToken.equalsIgnoreCase("mm"))
                    renderDropdown(buf, dateInput, dateInput.getMonth(), "month", "MSG_MONTH", 1, 12, width / 3);
                else
                if(strToken.equalsIgnoreCase("yy") || strToken.equalsIgnoreCase("yyyy"))
                    renderDropdown(buf, dateInput, dateInput.getYear(), "year", "MSG_YEAR", dateInput.getFromYear(), dateInput.getToYear(), width / 3);
                else
                    buf.append(strToken);
            }

            out.print(buf.toString());
        }
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */   
    protected void renderDropdown(StringBuffer buf, DateInput dateInput, int value, String strProperty, String strDescId, int nFrom, int nTo, 
            int width)
    {
        String title = dateInput.getToolTip();
        if(title != null)
            title = title + " " + getString(strDescId);
        else
            title = getString(strDescId);
        buf.append("<select ").append(renderNameAndId(dateInput, strProperty));
        String strCssClass = dateInput.getCssClass();
        if(strCssClass != null)
        {
            strCssClass = convertCssClass(strCssClass);
            buf.append(" class=\"").append(strCssClass).append("\"");
        }
        if(dateInput.getCssStyle() != null)
            buf.append(" style='").append(dateInput.getCssStyle()).append('\'');
        if(title != null)
            buf.append(" title=\"").append(SafeHTMLString.escapeAttribute(title)).append("\"");
        buf.append(" size=0");
        if(!dateInput.isEnabled())
            buf.append(" disabled='true'");
        buf.append(" style='width:").append(Math.max(65, width)).append("px'");
        renderEventArg(buf, "onchange", "onselect");
        buf.append(">");
        buf.append("<option value=''>").append(getString(strDescId)).append("</option>");
        for(int i = nFrom; i <= nTo; i++)
        {
            buf.append("<option value='").append(i).append("'");
            if(value == i)
                buf.append(" SELECTED");
            buf.append(">").append(i).append("</option>");
        }

        buf.append("</select>");
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */   
    protected static String getString(String stringId)
    {
        java.util.Locale locale = LocaleService.getLocale();
        Map stringsCache = (Map)m_stringCache.get(locale);
        if(stringsCache == null)
        {
            stringsCache = new HashMap(17);
            m_stringCache.put(locale, stringsCache);
        }
        String strResult = (String)stringsCache.get(stringId);
        if(strResult == null)
        {
            strResult = m_resourceBundle.getString(stringId, locale);
            stringsCache.put(stringId, strResult);
        }
        return strResult;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag */   
    protected static String getDateFormat(int dateFormat)
    {
        java.util.Locale locale = LocaleService.getLocale();
        String strDateFormat = DateUtil.getDateFormatPattern4DigitYear(dateFormat, locale);
        strDateFormat = strDateFormat.replace('M', 'm');
        return strDateFormat;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag 
     * Changed from static to protected
     */   
    protected Calendar parseDate(String strDate)
    {
        Calendar calendar = null;
        int day = -1;
        int month = -1;
        int year = -1;
        String strUpperDate = strDate.toUpperCase();
        String months[] = getShortMonths();
        boolean allDigit = true;
        int j = 0;
        do
        {
            if(j >= months[0].length())
                break;
            if(!Character.isDigit(months[0].charAt(j)))
            {
                allDigit = false;
                break;
            }
            j++;
        } while(true);
        if(!allDigit)
        {
            int i = 0;
            do
            {
                if(i >= months.length)
                    break;
                if(strUpperDate.indexOf(months[i].toUpperCase()) != -1)
                {
                    month = i + 1;
                    break;
                }
                i++;
            } while(true);
        }
        if(month == -1)
        {
            months = getMonths();
            allDigit = true;
            int m = 0;
            do
            {
                if(m >= months[0].length())
                    break;
                if(!Character.isDigit(months[0].charAt(m)))
                {
                    allDigit = false;
                    break;
                }
                m++;
            } while(true);
            if(!allDigit)
            {
                int i = 0;
                do
                {
                    if(i >= months.length)
                        break;
                    if(strUpperDate.indexOf(months[i].toUpperCase()) != -1)
                    {
                        month = i + 1;
                        break;
                    }
                    i++;
                } while(true);
            }
        }
        if(month != -1)
        {
            int index = 0;
            DateFormatTokenizer tokenizer = new DateFormatTokenizer(getDateFormat(2));
            do
            {
                if(!tokenizer.hasMoreTokens())
                    break;
                String strToken = tokenizer.nextToken();
                if(strToken.equalsIgnoreCase("d") || strToken.equalsIgnoreCase("dd"))
                {
                    index = indexOfDigit(strDate, index);
                    if(index == -1)
                        break;
                    day = parseInt(strDate, index);
                    index += String.valueOf(day).length() + 1;
                    continue;
                }
                if(!strToken.equalsIgnoreCase("yy") && !strToken.equalsIgnoreCase("yyyy"))
                    continue;
                index = indexOfDigit(strDate, index);
                if(index == -1)
                    break;
                int numDigit = 0;
                for(int i = index; i < strDate.length() && Character.isDigit(strDate.charAt(i)); i++)
                    numDigit++;

                if(numDigit == 2 || numDigit == 4)
                {
                    year = parseInt(strDate, index);
                    index += String.valueOf(year).length() + 1;
                }
            } while(true);
        } else
        {
            int index = 0;
            DateFormatTokenizer tokenizer = new DateFormatTokenizer(getDateFormat(3));
            do
            {
                if(!tokenizer.hasMoreTokens())
                    break;
                String strToken = tokenizer.nextToken();
                if(strToken.equalsIgnoreCase("d") || strToken.equalsIgnoreCase("dd"))
                {
                    index = indexOfDigit(strDate, index);
                    if(index == -1)
                        break;
                    day = parseInt(strDate, index);
                    index += String.valueOf(day).length() + 1;
                    continue;
                }
                if(strToken.equalsIgnoreCase("m") || strToken.equalsIgnoreCase("mm") || strToken.equalsIgnoreCase("mmm"))
                {
                    index = indexOfDigit(strDate, index);
                    if(index == -1)
                        break;
                    month = parseInt(strDate, index);
                    index += String.valueOf(month).length() + 1;
                    continue;
                }
                if(!strToken.equalsIgnoreCase("yy") && !strToken.equalsIgnoreCase("yyyy"))
                    continue;
                index = indexOfDigit(strDate, index);
                if(index == -1)
                    break;
                int numDigit = 0;
                for(int i = index; i < strDate.length() && Character.isDigit(strDate.charAt(i)); i++)
                    numDigit++;

                if(numDigit == 2 || numDigit == 4)
                {
                    year = parseInt(strDate, index);
                    index += String.valueOf(year).length() + 1;
                }
            } while(true);
        }
        if(day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 0)
        {
            if(year < 45)
                year += 2000;
            else
            if(year < 100)
                year += 1900;
            calendar = Calendar.getInstance(LocaleService.getLocale());
            calendar.set(year, month - 1, day);
            calendar.setTime(calendar.getTime());
            if(calendar.get(5) != day || calendar.get(2) + 1 != month || calendar.get(1) != year)
                calendar = null;
        }
        return calendar;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag
     * Changed from static to protected
     */    
    protected int indexOfDigit(String strString, int indexFrom)
    {
        for(int i = indexFrom; i < strString.length(); i++)
            if(Character.isDigit(strString.charAt(i)))
                return i;

        return -1;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag
     * Changed from static to protected
     */
    protected int parseInt(String strString, int indexFrom)
    {
        int result = 0;
        int i = indexFrom;
        do
        {
            if(i >= strString.length())
                break;
            char ch = strString.charAt(i);
            if(!Character.isDigit(ch))
                break;
            result = result * 10 + Character.getNumericValue(ch);
            i++;
        } while(true);
        return result;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag
     * Changed from static to protected
     */    
    protected String toStringFromStringArray(String array[])
    {
        StringBuffer buf = null;
        for(int i = 0; i < array.length; i++)
            if(buf == null)
                buf = new StringBuffer(array[i]);
            else
                buf.append(",").append(array[i]);

        if(buf == null)
            return null;
        else
            return buf.toString();
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag
     * Changed from static to protected
     */    
    protected String[] getShortWeekdays()
    {
        java.util.Locale locale = LocaleService.getLocale();
        String days[] = DateUtil.getShortWeekdays(locale);
        return days;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag
     * Changed from static to protected
     */    
    protected String[] getShortMonths()
    {
        java.util.Locale locale = LocaleService.getLocale();
        String months[] = DateUtil.getShortMonths(locale);
        return months;
    }

    /* 
     * Copied from com.documentum.web.form.control.DateInputTag
     * Changed from static to protected
     */    
    protected String[] getMonths()
    {
        java.util.Locale locale = LocaleService.getLocale();
        String months[] = DateUtil.getMonths(locale);
        return months;
    }
    
    private static NlsResourceBundle m_resourceBundle = new NlsResourceBundle("com.documentum.web.form.control.DateTimeNlsProp"); // com.cosd.controls.DateTimeNlsProp
    private static Map m_stringCache = new HashMap(7);
    private static final String NEXTMONTH_IMAGE = "images/date/nextmonth.gif";
    private static final String PREVMONTH_IMAGE = "images/date/prevmonth.gif";
    private static final String DROPDOWN_IMAGE = "images/date/dropdown.gif";
    private static final String DROPDOWN_DISABLED_IMAGE = "images/date/dropdown_disabled.gif";
    private String m_strWidth;
    private String m_strOnselect;
    private String m_strDay;
    private String m_strMonth;
    private String m_strYear;
    private String m_strFromYear;
    private String m_strToYear;
    private String m_strDateSelector;
    private String m_strDateFormat;
    private static final int MIN_CALENDAR_WIDTH = 95;
    private static final int MIN_DROPDOWN_WIDTH = 65;

}