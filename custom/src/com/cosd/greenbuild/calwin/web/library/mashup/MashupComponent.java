package com.cosd.greenbuild.calwin.web.library.mashup;


import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.cosd.greenbuild.calwin.mashup.MashupInfo;
import com.cosd.greenbuild.calwin.mashup.MashupManager;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfTime;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Checkbox;
import com.documentum.web.form.control.DateInput;
import com.documentum.web.form.control.DropDownList;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Option;
import com.documentum.web.form.control.Panel;
import com.documentum.web.form.control.Text;
import com.documentum.web.form.control.databound.Datagrid;
import com.documentum.web.form.control.databound.DatagridRow;
import com.documentum.web.form.control.databound.TableResultSet;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.control.docbase.format.DocsizeValueFormatter;

public class MashupComponent extends Component implements COSDCalwinConstants {
	private static final long serialVersionUID = 1L;
	public static DocsizeValueFormatter FMT_DOC_SIZE = new DocsizeValueFormatter();
	public static DecimalFormat FMT_ZERO_PAD = new DecimalFormat("0000000000000000000000000");
	private static final int[] SECTIONS = { 1000, 3000, 5000, 7000, 9000, 11000, 13000, 15000, 17000, 19000, 21000, 23000, 25000,
			27000, 29000, 31000, 33000, 35000, 37000 };
	private static final SimpleDateFormat SDF_QUERY_DATE = new SimpleDateFormat("MM/dd/yyyy");

	private final Checkbox[] checkboxes = new Checkbox[19];
	private DateInput fromDate = null;
	private DateInput toDate = null;
	private String cdcrNum = null;
	private Label lError = null;

	private static Hashtable m_section_lookup = null;
	private static Hashtable m_subtype_lookup = null;
	private Checkbox checkboxDocDate = null;
	private Datagrid dg = null;
	// maximum length (fg or bg) from config
	private long maxLen;
	// show maximum length, true if max len is not 0
	private boolean showMax;
	// if true, all result checkboxes will be selected
	private boolean selectAll = false;
	// index of row currently being added to results
	private int rowCount;
	private boolean bIsLevel0;

	@Override
	public void onInit(ArgumentList argList) {
		DfLogger.debug(this, "In MashupComponent onInit", null, null);
		super.onInit(argList);

		try {

			// init controls that need to restore the saved values here.
			this.fromDate = (DateInput) getControl("from_date", DateInput.class);
			this.toDate = (DateInput) getControl("to_date", DateInput.class);
			this.checkboxDocDate = (Checkbox) getControl("use_doc_date", Checkbox.class);
			this.lError = (Label) getControl("error_message", Label.class);
			this.lError.setLabel("");
			this.cdcrNum = (String) SessionState.getAttribute(SESSION_CDCR_NUMBER);

			Panel errorPanel = (Panel) getControl("errorPanel", Panel.class);

			// Section and subtype lookup tables are used to get the strings
			// from int values.
			if (m_section_lookup == null) {
				m_section_lookup = initLookup(GET_SECTIONS_QRY);
				m_subtype_lookup = initLookup(GET_SUBTYPES_QRY);
			}

			dg = (Datagrid) getControl("dgDocList", Datagrid.class);
			dg.getDataProvider().setDfSession(getDfSession());

			// Init blank checkboxes on the page.
			for (int i = 0; i < 19; i++)
				checkboxes[i] = (Checkbox) getControl("checkbox" + i, Checkbox.class);

			// init other controls

			Text fileName = (Text) getControl("filename", Text.class);
			fileName.setValue(MashupInfo.generateDefaultTitle(cdcrNum));
			errorPanel.setVisible(false);

			Label lCDCRNumber = (Label) getControl("cdcr_number", Label.class);
			lCDCRNumber.setLabel(cdcrNum);

			Checkbox checkbox16 = (Checkbox) getControl("checkbox16", Checkbox.class);

			// only users in specified roles can see visit checkbox
			checkbox16.setVisible(false);
			this.bIsLevel0 = true;
			String sLoginName = getDfSession().getLoginUserName();
			IConfigElement element = this.lookupElement("visit-roles");
			Iterator<IConfigElement> roleElems = element.getChildElements();
			while (roleElems.hasNext()) {
				IConfigElement curr = roleElems.next();
				if ("ROLE".equalsIgnoreCase(curr.getName())) {
					String currRole = curr.getValue();
					IDfGroup gVisit = getDfSession().getGroup(currRole);

					boolean isInGroup = gVisit.isUserInGroup(sLoginName);
					checkbox16.setVisible(isInGroup);
					bIsLevel0 = !isInGroup;
					// if in any specified group, don't show visit.
					if (isInGroup)
						break;
				}
			}

			// populate Days to Expire dropdown list
			DropDownList daysToExpire = (DropDownList) getControl("days_to_expire", DropDownList.class);
			addOneOption(daysToExpire, 10);
			addOneOption(daysToExpire, 30);
			addOneOption(daysToExpire, 60);
			addOneOption(daysToExpire, 90);

			try {
				this.maxLen = MashupManager.getInstance(getDfSession()).getMaxTotalLength();
				this.showMax = (this.maxLen != 0);
			} catch (DfException e) {
				DfLogger.warn(this, "Unexpected error getting total length, ignored.", null, e);
				e.printStackTrace();
			}
			Label maxSizeLbl = (Label) getControl("disp_size_max", Label.class);
			maxSizeLbl.setVisible(showMax);
			Label msgMaxSize = (Label) getControl("msgMaxSize", Label.class);
			msgMaxSize.setVisible(showMax);
			if (showMax) {
				String maxLenStr = formatLength(maxLen);
				maxSizeLbl.setLabel(maxLenStr);
				Label maxSizeNum = (Label) getControl("maxSize", Label.class);
				maxSizeNum.setLabel("" + maxLen);
			}

		} catch (DfException e) {
			DfLogger.error(this, "Error on init.", null, e);
			lError.setLabel(getString("MSG_MASHUP_FAILED"));
			e.printStackTrace();
		}
	}

	@Override
	public void onRender() {
		// the row checkboxes are not maintained over column sort.
		// get them and store the values, then set the values again
		// after render.
		// this is annoying, but there does not seem to be a way to
		// persist the values across refresh.
		List<Integer> checkedIds = new ArrayList<Integer>();
		Datagrid dg = (Datagrid) getControl("dgDocList");
		for (DatagridRow curr : dg.getDatagridRows()) {
			Iterator<Control> rowControls = curr.getContainedControls();
			int idx = -1;
			boolean checked = false;
			while (rowControls.hasNext()) {
				Control currControl = rowControls.next();
				if ("check".equals(currControl.getName())) {
					checked = ((Checkbox) currControl).getValue();
					if (!checked)
						break;
				}
				if ("Index".equals(currControl.getName())) {
					idx = Integer.parseInt(((Label) currControl).getLabel().trim());
					break;
				}
			}
			if (checked)
				checkedIds.add(idx);
		}
		super.onRender();
		Label lbl = ((Label) getControl("selectedIds"));
		if (lbl != null) {
			String selIds = "";
			if (selectAll) {
				selIds = "all";
				this.selectAll = false;
			} else if (!checkedIds.isEmpty()) {
				selIds = "|";
				for (Integer currId : checkedIds) {
					selIds += currId + "|";
				}
			}
			lbl.setLabel(selIds);
		}

		if (lError.getLabel() == null || lError.getLabel().length() == 0)
			lError.setLabel(getString("MSG_ERROR_CLICK_FIND"));

	}

	private void addOneOption(DropDownList daysToExpire, int days) {
		Option option = new Option();
		option.setLabel(days + " Days");
		option.setValue("" + days);
		daysToExpire.addOption(option);
	}

	public void onRefresh(Control con, ArgumentList al) {
		// generate the DQL, base on the freom/to dates, and the checkbox.
		String sDateCondition = getDateCondition();
		if (sDateCondition == null)
			return;

		// collect section values
		int alSections[] = new int[19];
		int iCount = 0;
		for (int i = 0; i < 19; i++) {
			if (checkboxes[i].getValue()) {
				// if the user is in level0 role and the current checkbox is
				// Visiting, skip it
				if (bIsLevel0 && i == 16)
					continue;

				alSections[iCount++] = SECTIONS[i];
			}
		}

		long totalSize = 0;

		try {
			String[] columnHeaders = new String[] { "Index", "ID", "ScanDate", "Section", "DocType", "DocSubType", "DocDate",
					"DispSize", "Size" };
			TableResultSet rs = new TableResultSet(columnHeaders);
			this.rowCount = 0;
			// because backfiles sort on scan_date, all others sort on
			// document_date. backfile's document_type is section + 10000 for
			// every section have Backfiles (Except Alert/DSH/EX). We can't use
			// just one DQL, have to do one DQL for each section.
			for (int i = 0; i < iCount; i++)
				totalSize += getEntireSectionObjects(alSections[i] + "", sDateCondition, rs);

			Label doclist_count = (Label) getControl("doclist_count", Label.class);
			doclist_count.setLabel("" + rowCount);
			DocsizeValueFormatter docSizeValueFormatter = new DocsizeValueFormatter();
			String totalSizeStr = docSizeValueFormatter.format("" + totalSize);
			Label disp_size_total = (Label) getControl("disp_size_total", Label.class);
			disp_size_total.setLabel(totalSizeStr);
			Label size_total = (Label) getControl("size_total", Label.class);
			size_total.setLabel("" + totalSize);

			dg.getDataProvider().setScrollableResultSet(rs);

			// indicate there are new results. this causes all checkboxes to be
			// checked in onload js
			// (unlike a refresh on column header change, when they should
			// remain as they were)
			this.selectAll = true;

			// at least one PDF id should be there
			String msg = "";
			if (rowCount == 0)
				msg = getString("MSG_ERROR_NO_OBJECT");
			lError.setLabel(msg);

		} catch (Throwable t) {
			DfLogger.error(this, "Error fetching documents for mashup hitlist.", null, t);
			lError.setLabel(getString("MSG_MASHUP_FAILED"));
		}
	}

	private String getDateCondition() {
		// get From and To dates, mare sure To date is greater than From date.
		String sDateCondition = null;

		String sFrom = fromDate.getValue();
		String sTo = toDate.getValue();

		// no dates selected
		if (sFrom == null || sTo == null) {
			sDateCondition = "";
		} else if (sFrom.equals("Date") && sTo.equals("Date")) {
			sDateCondition = "";
		} else if (sFrom.equals("Date") || sTo.equals("Date")) {
			lError.setLabel(getString("MSG_ERROR_MISSING_DATE"));
			return null;
		} else {
			try {
				// construct DQL date condition here
				Calendar cFrom = convertToDate(sFrom);
				Calendar cTo = convertToDate(sTo);

				if (!cFrom.before(cTo) && !cFrom.equals(cTo)) {
					lError.setLabel(getString("MSG_ERROR_DATE"));
					return null;
				}

				boolean bUseDocDate = checkboxDocDate.getValue();
				if (bUseDocDate) {
					sDateCondition = " and c.document_date>=DATE('" + sFrom
							+ "','mm/dd/yyyy') AND c.document_date<DATEADD(day,1,DATE('" + sTo + "','mm/dd/yyyy')) ";
				} else {
					sDateCondition = " and c.scan_date>=DATE('" + sFrom + "','mm/dd/yyyy') AND c.scan_date<DATEADD(day,1,DATE('"
							+ sTo + "','mm/dd/yyyy')) ";
				}
			} catch (ParseException e) {
				lError.setLabel(getString("MSG_ERROR_MISSING_DATE"));
				return null;
			}

		}

		return sDateCondition;
	}

	/**
	 * Query docbase and gets all documents for given CDCR number and section.
	 * Relevant values are then added as a row to the given result set.
	 *
	 * @param section
	 *            section to query for
	 * @param sDateCondition
	 *            date condition affecting the query
	 * @param rs
	 *            results set to add results to
	 * @return the total content length of all documents in the given section
	 * @throws DfException
	 *             on error
	 */
	public long getEntireSectionObjects(String section, String sDateCondition, TableResultSet rs) throws DfException {
		int iBackfile = Integer.parseInt(section) + 1000;
		String sDocument_type = iBackfile + "";

		// BPH sorts on doc_subtype, too. We have to do a seperate sepcial DQL
		// here.
		String sDQL = null;
		String indexVal = "";
		//System.out.println("Section Val*********************: "+section);
		if (section.equals("21000")) {
			sDQL = EXPORT_BPH_QRY;
			DfLogger.info(this, "Using BPH Query", null, null);
		} else {
			sDQL = EXPORT_QRY;
		}

		// create DQL here
		Object[] objArgs = { cdcrNum, section, sDocument_type, sDateCondition, cdcrNum, section, sDocument_type, sDateCondition };
		MessageFormat form = new MessageFormat(sDQL);
		String query = form.format(objArgs);

		long sectionSize = 0;

		// System.out.println(query);
		IDfQuery idfQry = new DfQuery();
		idfQry.setDQL(query);
		IDfCollection coll = idfQry.execute(getDfSession(), DfQuery.DF_READ_QUERY);
		try {
			while (coll.next()) {
				String objId = coll.getString("r_object_id");
				DfTime docDate = (DfTime) coll.getTime("document_date");
				String currSection = (String) m_section_lookup.get(coll.getString("section"));
				String documentTypeName = coll.getString("title");
				//String documentSubType = (String) m_subtype_lookup.get(coll.getString("document_subtype"));
				String documentSubType = null;
				if (section.equals("21000"))
					documentSubType = coll.getString("subtypetitle");
				else
					documentSubType = "";
				DfTime scanDate = (DfTime) coll.getTime("scan_date");
				long contentSize = coll.getLong("r_content_size");

				if (section.equals("Exception")) {
					documentTypeName = "Exception Document";
					documentSubType = " ";
				}

				this.rowCount++;
				// prepare the row that will be added to the result set
				// the row values have to be strings, otherwise there is a
				// comparator failure on sort
				List<Object> tableRow = new ArrayList<Object>();

				// the order of how the array is initialized is important.
				// the index has to match the desired columns
				// Following line is updated from tableRow.add(""+rowCount); as the index value should be numeric sort and not string sort. 
				tableRow.add(rowCount);
				tableRow.add(objId);
				tableRow.add(scanDate == null || scanDate.getDate() == null ? "" : FMT_ZERO_PAD.format(scanDate.getDate()
						.getTime()));
				tableRow.add(currSection);
				tableRow.add(documentTypeName);
				tableRow.add(documentSubType);
				tableRow
						.add(docDate == null || docDate.getDate() == null ? "" : FMT_ZERO_PAD.format(docDate.getDate().getTime()));

				tableRow.add(FMT_DOC_SIZE.format("" + contentSize));

				// these columns are used for sorting Size and Index, not
				// visible to end users.
				tableRow.add(FMT_ZERO_PAD.format(contentSize));
				// tableRow.add(""+(this.rowCount+ 10001));

				// finally, add the row to the table result set
				rs.add(tableRow);

				sectionSize += contentSize;
			}
		} finally {
			coll.close();
		}
		return sectionSize;

	}

	private Calendar convertToDate(String sInput) throws ParseException {
		try {
			Calendar c1 = Calendar.getInstance();
			SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
			c1.setTime(sdf.parse(sInput));
			/*StringTokenizer st = new StringTokenizer(sInput, "/");
			String sMonth = st.nextToken();
			String sDay = st.nextToken();
			String sYear = st.nextToken();

			int iMonth = Integer.parseInt(sMonth) - 1;
			int iDay = Integer.parseInt(sDay);
			int iYear = Integer.parseInt(sYear);

			c1.set(iYear, iMonth, iDay);
			 */
			if(!sdf.format(c1.getTime()).equals(sInput)) {
				throw new Exception();
			}
			return c1;
		} catch (Exception e) {
			throw new ParseException("Error parsing: " + sInput + ".  Not in MM/dd/yyyy format.", 0);
		}
	}

	private static final DecimalFormat FMT_LEN = new DecimalFormat("#,##0.#");

	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;

	/**
	 * Formats a length of bytes to string representation. ie 1024 == 1K
	 *
	 * @param value
	 * @return
	 */
	public static String formatLength(final long value) {
		final long[] dividers = new long[] { T, G, M, K, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
		if (value < 0)
			throw new IllegalArgumentException("Invalid file size: " + value);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = formatLength(value, divider, units[i]);
				break;
			}
		}
		return result;
	}

	private static String formatLength(final long value, final long divider, final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return FMT_LEN.format(result) + unit;
	}

	// create look uphashtables for Section and document subtype.
	public Hashtable initLookup(String sDQL) throws DfException {
		Hashtable targetTable = new Hashtable();
		// if subtype is 0, put '' there.
		targetTable.put("0", " ");

		IDfQuery idfQry = new DfQuery();
		idfQry.setDQL(sDQL);
		IDfCollection coll = idfQry.execute(getDfSession(), DfQuery.DF_READ_QUERY);
		while (coll.next()) {
			String sID = coll.getValueAt(0).asString();
			String sValue = coll.getValueAt(1).asString();

			targetTable.put(sID, sValue);
		}
		coll.close();

		return targetTable;
	}

	public void onClearDate(Control con, ArgumentList al) {
		fromDate.clear();
		toDate.clear();
		// hide any date errors, by resetting to default
		this.lError.setLabel(getString("MSG_ERROR_CLICK_FIND"));
		// refresh results, as dates may have changed
		onRefresh(con, al);
	}

}