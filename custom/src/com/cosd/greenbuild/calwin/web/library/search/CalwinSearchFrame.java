package com.cosd.greenbuild.calwin.web.library.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.documentum.nls.NlsResourceBundle;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.LocaleService;
import com.documentum.web.common.SessionState;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Button;
import com.documentum.web.form.control.Checkbox;
import com.documentum.web.form.control.DateInput;
import com.documentum.web.form.control.DropDownList;
import com.documentum.web.form.control.Hidden;
import com.documentum.web.form.control.Link;
import com.documentum.web.form.control.Option;
import com.documentum.web.form.control.Panel;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.databound.DataDropDownList;
import com.documentum.web.form.control.databound.DataProvider;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.ConfigService;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.config.IConfigLookup;
import com.documentum.web.formext.config.IPreferenceStore;
import com.documentum.web.formext.config.PreferenceService;
import com.documentum.web.util.DfcUtils;


/**
 * 
 * ******************************************************************************************
 * File Name: CalwinSearchFrame.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class CalwinSearchFrame extends Component {

	public void onInit(ArgumentList args)
	{
		super.onInit(args);
        Hidden simpleSearchViewHiddenCtrl = (Hidden)getControl0("simpleSearchViewHiddenCtrl", true, Hidden.class);
        simpleSearchViewHiddenCtrl.setValue(strNLSSimpleSearch);
        Hidden advSearchViewHiddenCtrl = (Hidden)getControl0("advSearchViewHiddenCtrl", true, Hidden.class);
        advSearchViewHiddenCtrl.setValue(strNLSAdvSearch);
		//showHideOptions(m_bShowOptions);  // Change for CERMS-II Release-b
        showHideOptions(true);  // Change for CERMS-II Release-b
		initDDDListControls(args);
		setUserNameLabel(args);
		IPreferenceStore preferenceStore = PreferenceService.getPreferenceStore();
		preferenceStore.writeString("hhsaappname", "calwinsearch");
		
		IConfigElement calwinSearchConfigElement = lookupElement(COSDCalwinConstants.CALWIN_SEARCH_QUERY);
		String strCalwinSearchAttrs = calwinSearchConfigElement.getChildElement(COSDCalwinConstants.CALWIN_SEARCH_ATTRS).getValue();
		String strCalwinSearchType = calwinSearchConfigElement.getChildElement(COSDCalwinConstants.CALWIN_SEARCH_TYPE).getValue();
		String strCalwinSearchQuery = "select " + strCalwinSearchAttrs + " from " + strCalwinSearchType + " where ";
		Hidden calwinSearchQueryHiddenCtrl = (Hidden)getControl0("calwinSearchQueryHiddenCtrl", true, Hidden.class);
        calwinSearchQueryHiddenCtrl.setValue(strCalwinSearchQuery);
	}
	
	private void initDDDListControls(ArgumentList args) 
	{
		setCategoryDDDList(args);
		setSubcategoryDDDList(args);
		setDocumentTypesDDDList(args);
		setDataMonthDDDList(args);
	}
	
	private void setUserNameLabel(ArgumentList args) 
	{
		try 
		{
			Label usernamelbl = (Label)getControl("usernamelabel", Label.class);
			usernamelbl.setLabel(getDfSession().getLoginInfo().getUser());
		} catch (DfException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void setCategoryDDDList(ArgumentList args) 
	{
		DataDropDownList strengthList = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		strengthList.getDataProvider().setDfSession(getDfSession());
		strengthList.getDataProvider().setQuery(getCategoryQuery());
	}

	public void setSubcategoryDDDList(ArgumentList args) 
	{
		DataDropDownList strengthList = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		strengthList.getDataProvider().setDfSession(getDfSession());
		strengthList.getDataProvider().setQuery(getSubCatQuery());
	}

	public void setDocumentTypesDDDList(ArgumentList args) 
	{
		DataDropDownList doctypeList = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		doctypeList.getDataProvider().setDfSession(getDfSession());
		doctypeList.getDataProvider().setQuery(getDocTypeQuery());
	}
	
	private void setDataMonthDDDList(ArgumentList args)
	{
		DropDownList datamonthList = (DropDownList) getControl(CONTROL_DATAMONTHLST, DropDownList.class);
		datamonthList.setOptions(getDataMonthList(2006));
	}
	
	
    public List<Option> getDataMonthList(int startyear)
    {

		ArrayList<Option> datamonths = new ArrayList<Option>();
        Option emptyOption = new Option();
        emptyOption.setLabel("");
        emptyOption.setValue("");
        datamonths .add(emptyOption);
        
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int imonth = localCalendar.get(Calendar.MONTH);
        int iyear = localCalendar.get(Calendar.YEAR);
        Integer year = new Integer(iyear);

        for (Integer i = new Integer(imonth); i.intValue() > 0; i--)
        {
        	String mnth = i < 10 ? "0" + i.toString() : i.toString();
            Option opt = new Option();
            opt.setLabel(mnth + "/" + year);
            opt.setValue(mnth + "/" + year);
            datamonths.add(opt);
        }
        for (Integer j = new Integer(year) - 1; j.intValue() > (startyear - 1); j--)
        {
            for (Integer i = new Integer(12); i.intValue() > 0; i--)
            {
                String mnth = i < 10 ? "0" + i.toString() : i.toString();
                Option opt = new Option();
                opt.setLabel(mnth + "/" + j);
                opt.setValue(mnth + "/" + j);
                datamonths.add(opt);
            }
        }

        return datamonths;
    }

	
	private String getCategoryQuery() 
	{
		String query = COSDCalwinConstants.GET_DEFAULT_CATEGORY_QUERY;
		return query;
	}
	
	private String getSubCatQuery() 
	{
		String query = COSDCalwinConstants.GET_DEFAULT_SUBCATEGORY_QUERY;
		return query;
	}

	private String getDocTypeQuery() 
	{
		String query = COSDCalwinConstants.GET_DEFAULT_DOCTYPE_QUERY;
		return query;
	}
	
	private String getDataMonthQuery() 
	{
		String query = COSDCalwinConstants.GET_DATA_MONTH_QUERY;
		return query;
	}
	
	public void onChangeCategoryDDDList(DataDropDownList catDDDLstCtrl, ArgumentList argumentList)
	{
		DataDropDownList subcatDDDLstCtrl = (DataDropDownList) getControl("subcatdddlst", DataDropDownList.class);
		DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		
		String catDDDLstVal = catDDDLstCtrl.getValue();
		String subcatDDDLstVal = subcatDDDLstCtrl.getValue();
		String doctypeDDDLstVal = doctypeDDDLstCtrl.getValue();
		
		String strQuery = "select value_id from calwin_admin_case where is_category=1 and title='";
		Hidden calwinCategoryHiddenCtrl = (Hidden)getControl0("calwinCategoryHiddenCtrl", true, Hidden.class);
		calwinCategoryHiddenCtrl.setValue(getValueId(catDDDLstVal, strQuery).toString());
		
		//String strQuery1 = "select value_id from calwin_admin_case where is_subcategory=1 and title='";
		Hidden calwinSubcategoryHiddenCtrl = (Hidden)getControl0("calwinSubcategoryHiddenCtrl", true, Hidden.class);
		calwinSubcategoryHiddenCtrl.setValue("");
		
		//String strQuery2 = "select value_id from calwin_admin_case where is_subcategory=0 and is_category=0 and title='";
		Hidden calwinDoctypeHiddenCtrl = (Hidden)getControl0("calwinDoctypeHiddenCtrl", true, Hidden.class);
		calwinDoctypeHiddenCtrl.setValue("");
		
		StringBuffer querySubCat;
		StringBuffer queryDoctype;
		DataProvider catDDDLstDP = catDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = doctypeDDDLstCtrl.getDataProvider();
		
		querySubCat = new StringBuffer(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE);
		querySubCat.append(catDDDLstVal + "')");
		querySubCat.append(COSDCalwinConstants.GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		subcatDDDLstDP.setDfSession(getDfSession());
		
		queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_1);
		queryDoctype.append(catDDDLstVal + "')");
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_ORDER_BY);
		doctypeDDDLstDP.setDfSession(getDfSession());
		
		// if category is not blank AND (subcategory is blank OR subcategory is not blank)
		if ((catDDDLstVal != "") && (catDDDLstVal.length()!=0)) 
		{		
			subcatDDDLstDP.setQuery(querySubCat.toString());  // refresh the subcategory field as it depends on category field
			doctypeDDDLstDP.setQuery(queryDoctype.toString()); // refresh the doctype field as it depends on category field
		}
		// if category is blank
		else 
		{   
			catDDDLstDP.setQuery(getCategoryQuery()); // refresh category field, as it has been made blank
			subcatDDDLstDP.setQuery(getSubCatQuery()); // refresh subcategory field, as it is dependent on the category field and hence refreshed to default
			doctypeDDDLstDP.setQuery(getDocTypeQuery()); // refresh documenttype field, as it is dependent on the category field and hence refreshed to default
			subcatDDDLstCtrl.setValue("");
			doctypeDDDLstCtrl.setValue("");
		}		
		
	}
	

	public void onChangeSubCategoryDDDList(DataDropDownList subcatDDDLstCtrl, ArgumentList argumentList)
	{
		DataDropDownList catDDDLstCtrl = (DataDropDownList) getControl("catdddlst", DataDropDownList.class);
		DataDropDownList doctypeDDDLstCtrl = (DataDropDownList) getControl("doctypedddlst", DataDropDownList.class);
		
		String catDDDLstVal = catDDDLstCtrl.getValue();
		String subcatDDDLstVal = subcatDDDLstCtrl.getValue();
		String doctypeDDDLstVal = doctypeDDDLstCtrl.getValue();
		
		String strQuery = "select value_id from calwin_admin_case where is_subcategory=1 and title='";
		Hidden calwinSubcategoryHiddenCtrl = (Hidden)getControl0("calwinSubcategoryHiddenCtrl", true, Hidden.class);
		calwinSubcategoryHiddenCtrl.setValue(getValueId(subcatDDDLstVal, strQuery).toString());
		
		//String strQuery1 = "select value_id from calwin_admin_case where is_subcategory=0 and is_category=0 and title='";
		Hidden calwinDoctypeHiddenCtrl = (Hidden)getControl0("calwinDoctypeHiddenCtrl", true, Hidden.class);
		calwinDoctypeHiddenCtrl.setValue("");
		
		StringBuffer queryCat;
		StringBuffer queryDoctype;
		DataProvider catDDDLstDP = catDDDLstCtrl.getDataProvider();
		DataProvider subcatDDDLstDP = subcatDDDLstCtrl.getDataProvider();
		DataProvider doctypeDDDLstDP = doctypeDDDLstCtrl.getDataProvider();
		
		queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_1);
		queryDoctype.append(subcatDDDLstVal + "')");
		
		// if category field is not blank, ensure that it is taken into account when determining the resulting document_type
		if ((catDDDLstVal != "") && (catDDDLstVal.length()!=0)) 
		{
			queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_2);
			queryDoctype.append(catDDDLstVal + "')");
		}
		queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_ORDER_BY);
		doctypeDDDLstDP.setDfSession(getDfSession());
		
		// if subcategory is not blank AND (category is blank OR category is not blank)	
		if ((subcatDDDLstVal != "") && (subcatDDDLstVal.length()!=0)) 
		{	 
			doctypeDDDLstDP.setQuery(queryDoctype.toString());
		} 
		// if subcategory is blank
		else 
		{
			// if category is blank
			if (("".equals(catDDDLstVal)) && (catDDDLstVal.length()==0)) 
			{
				subcatDDDLstDP.setQuery(getSubCatQuery());  // refresh the subcategory field when set to blank
				doctypeDDDLstDP.setQuery(getDocTypeQuery());  // refresh the doctype if the category field value is blank
			} 
			// if category is not blank
			else 
			{
				queryDoctype = new StringBuffer(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL);
				queryDoctype.append(catDDDLstVal + "')");
				queryDoctype.append(COSDCalwinConstants.GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL_ORDER_BY);
				doctypeDDDLstDP.setQuery(queryDoctype.toString());   // the doctype field values are based on the category field value
			}
			doctypeDDDLstCtrl.setValue("");
		}		
		
	}	

	public void onChangeDoctypeDDDList(DataDropDownList doctypeDDDLstCtrl, ArgumentList argumentList)
	{
		String doctypeDDDLstVal = doctypeDDDLstCtrl.getValue();
		String strQuery = "select value_id from calwin_admin_case where is_category=0 and is_subcategory=0 and title='";
		Hidden calwinDoctypeHiddenCtrl = (Hidden)getControl0("calwinDoctypeHiddenCtrl", true, Hidden.class);
		calwinDoctypeHiddenCtrl.setValue(getValueId(doctypeDDDLstVal, strQuery).toString());
	}
	
	private Integer getValueId(String strTitle, String strQuery) {
		IDfCollection iCollection = null;
		Integer intValueID = 0;
		IDfQuery query = DfcUtils.getClientX().getQuery();
		//String queryString = "select value_id from calwin_admin_case where is_category=1 and title='" + strCategoryTitle + "'";
		String queryString = strQuery + strTitle + "'";
		query.setDQL(queryString);

		try {
			iCollection = query.execute(getDfSession(), 0);
			for (; iCollection.next() == true;) {
				intValueID = iCollection.getInt("value_id");
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
		
		return intValueID;
		
	}
	
	public void onClickClear(Button button, ArgumentList args)
	{
		((Text)getControl("casenumbertext",Text.class)).setValue("");
		((DateInput)getControl("datefrompicker",DateInput.class)).clear();	
		((DateInput)getControl("datetopicker",DateInput.class)).clear();
		((DropDownList)getControl("catdddlst",DropDownList.class)).setValue("");
		Hidden calwinCategoryHiddenCtrl = (Hidden)getControl0("calwinCategoryHiddenCtrl", true, Hidden.class);
		calwinCategoryHiddenCtrl.setValue("");
		((DropDownList)getControl("subcatdddlst",DropDownList.class)).setValue("");
		Hidden calwinSubcategoryHiddenCtrl = (Hidden)getControl0("calwinSubcategoryHiddenCtrl", true, Hidden.class);
		calwinSubcategoryHiddenCtrl.setValue("");
		((DropDownList)getControl("doctypedddlst",DropDownList.class)).setValue("");
		Hidden calwinDoctypeHiddenCtrl = (Hidden)getControl0("calwinDoctypeHiddenCtrl", true, Hidden.class);
		calwinDoctypeHiddenCtrl.setValue("");
		((DropDownList)getControl("processedddlst",DropDownList.class)).setValue("");
		if (CalwinSearchFrame.strNLSSimpleSearch.equals((SessionState.getAttribute("CurrentSearchView")))) 
		{
			((Text)getControl("ssntext",Text.class)).setValue("");
			((Text)getControl("cintext",Text.class)).setValue("");
			((Text)getControl("cwintext",Text.class)).setValue("");
			((Text)getControl("providernumbertext",Text.class)).setValue("");
			((Checkbox)getControl("check",Checkbox.class)).setValue(false);
			((DropDownList)getControl(CONTROL_DATAMONTHLST,DropDownList.class)).setValue("");
			
		}
	}
	
	public void onClickRemoveDoc(Button button, ArgumentList args){
		
	}
	
	public void onClickUnRemoveDoc(Button button, ArgumentList args){
		
	}	

	public void onClickChangeProcessedStatus(Button button, ArgumentList args){
		
	}
	
    public void onShowOptions(Link showOptions, ArgumentList arg)
    {
        m_bShowOptions = !m_bShowOptions;
        showHideOptions(m_bShowOptions);
    }
    
	protected void showHideCaseNameOptions(boolean bShow) 
	{
        Panel panelOptions = (Panel)getControl0("caseNameOptionsPanel", true, Panel.class);
        panelOptions.setVisible(bShow);
        Panel panelSearchBtn = (Panel)getControl0("searchbtnpanel", true, Panel.class);
        panelSearchBtn.setVisible(bShow);
        clearErrorMessage();
        
	}

	protected void showHideOptions(boolean bShow)
    {
        Panel panelOptions = (Panel)getControl0(CONTROL_ADVSEARCHOPTIONSPANEL, true, Panel.class);
        panelOptions.setVisible(bShow);
        Panel dataMonthPanel = (Panel)getControl0(CONTROL_DATAMONTHPANEL, true, Panel.class);
        dataMonthPanel.setVisible(bShow);        
        clearErrorMessage();
        Link showOptions = (Link)getControl0("advSearch", true, Link.class);
        showOptions.setLabel(getShowHideOptionsLabel(bShow));
        showOptions.setLabel(""); // Change for CERMS-II Release-b
        SessionState.setAttribute("CurrentSearchView", getShowHideOptionsLabel(bShow));
    }
    
    protected Control getControl0(String name, boolean create, Class cl)
    {
        return create ? getControl(name, cl) : getControl(name);
    }
    
    protected void clearErrorMessage()
    {
        Panel panelError = (Panel)getControl0("errorMessagePanel", true, Panel.class);
        panelError.setVisible(false);
        Label labelError = (Label)getControl0("errorMessage", true, Label.class);
        labelError.setLabel("");
    }
    
    protected String getShowHideOptionsLabel(boolean bShow)
    {
        return bShow ? (new StringBuilder()).append(getString("MSG_CLOSE_OPTIONS")).toString() : (new StringBuilder()).append(getString("MSG_OPEN_OPTIONS")).toString();  // .append("[-] ")   .append("[+] ")
    }

	

	private boolean m_bShowOptions=false;
	private boolean m_bCaseNameEntered=false;
	protected final static String BLANK_STRING = "";
	private static final long serialVersionUID = 1L;

	public static final String CONTROL_ADVSEARCHERRMSG = "errorMessage";
	// Search Field Controls
	public static final String CONTROL_CASENUMBERTXT = "casenumbertext";
	public static final String CONTROL_CATEGORYDDL = "catdddlst";	
	public static final String CONTROL_SUBCATEGORYDDL = "subcatdddlst";
	public static final String CONTROL_DOCTYPEDDL = "doctypedddlst";
	public static final String CONTROL_DATEFROMPICKERDT = "datefrompicker_date";
	public static final String CONTROL_DATETOPICKERDT = "datetopicker_date";
	public static final String CONTROL_PROCESSEDDL = "processedddlst";
	public static final String CONTROL_SHOWADVSEARCH = "advSearch";
	public static final String CONTROL_DATAMONTHLST = "datamonthlst";
	public static final String CONTROL_SSNTXT = "ssntext";
	public static final String CONTROL_CINTXT = "cintext";
	public static final String CONTROL_CWINTXT = "cwintext";
	public static final String CONTROL_CHECKCHK = "check";
	// Search Field Validation Controls
	public static final String CONTROL_SSNVALIDATETXT = "ssntextvalidate";
	// Search Field Panels
	public static final String CONTROL_ADVSEARCHOPTIONSPANEL = "advSearchOptionsPanel";
	public static final String CONTROL_DATAMONTHPANEL = "dataMonthPanel";
	public static final String CONTROL_PROVIDERNUMBERPANEL = "providerNumberPanel";
	public static final String CONTROL_ADVSEARCHERRMSGPANEL = "errorMessagePanel";
	public static final String CONTROL_CASENAMEOPTIONSPANEL = "caseNameOptionsPanel";
	public static final String CONTROL_CASENAMELABELPANEL = "caseNameLabelPanel";
	// Search Control Array
	private static final String[] searchControlArray = {CONTROL_CASENUMBERTXT, CONTROL_CATEGORYDDL, CONTROL_SUBCATEGORYDDL, CONTROL_DOCTYPEDDL, CONTROL_DATEFROMPICKERDT, CONTROL_DATETOPICKERDT, CONTROL_PROCESSEDDL,
		CONTROL_SHOWADVSEARCH, CONTROL_DATAMONTHLST, CONTROL_SSNTXT, CONTROL_CINTXT, CONTROL_CWINTXT, CONTROL_CHECKCHK, CONTROL_SSNVALIDATETXT};
	// Search Control List
	private static List<Control> srchCtrlList = new ArrayList<Control>();
	// Map for search control values
	private static Map<Control, String> searchControlByValues = new HashMap<Control, String>();
	// NLS Strings	
	public static final NlsResourceBundle nlsAdvSearch = new NlsResourceBundle("com.cosd.greenbuild.calwin.web.library.search.COSDSimpleSearchFrame");
	public static final String strNLSAdvSearch = CalwinSearchFrame.nlsAdvSearch.getString("MSG_OPEN_OPTIONS", LocaleService.getLocale());
	public static final String strNLSSimpleSearch = CalwinSearchFrame.nlsAdvSearch.getString("MSG_CLOSE_OPTIONS", LocaleService.getLocale());
}
