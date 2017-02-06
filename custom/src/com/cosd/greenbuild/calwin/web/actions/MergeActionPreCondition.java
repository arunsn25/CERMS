package com.cosd.greenbuild.calwin.web.actions;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.SessionState;
import com.documentum.web.common.reporting.Cell;
import com.documentum.web.common.reporting.ColumnInfo;
import com.documentum.web.common.reporting.ITableDataProvider;
import com.documentum.web.common.reporting.MetaData;
import com.documentum.web.common.reporting.Row;
import com.documentum.web.formext.action.IActionExecution;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;

/**
 * 
 * ******************************************************************************************
 * File Name: MergeActionPreCondition.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class MergeActionPreCondition implements IActionPrecondition, IActionExecution {

	private static final long serialVersionUID = 1L;

	private static final String params[] = new String[] { "objectId" };

	public String[] getRequiredParams() {
		return params;
	}

	/*
	 * Checks number of result objects. Enable mashup buttons if there are at least 1 object in the resultset.
	 * 
	 */
	public boolean queryExecute(String actionName,
			IConfigElement configElement,
			ArgumentList argumentList,
			Context context,
			Component component) {
		//DfLogger.debug(this, "MergeAction Pre-Condition Start", null, null);

/*		if (component instanceof ITableDataProvider) {
			ITableDataProvider dp = (ITableDataProvider) component;
			MetaData metadata = dp.getMetaData();

			ArrayList<String> columnNameList = new ArrayList<String>();
			List<ColumnInfo> columnInfoList = metadata.getAllColumnInfo();
			for (int count = 0; count < columnInfoList.size(); count++) {
				ColumnInfo colInfo = columnInfoList.get(count);
				String columnName = colInfo.getName();
				columnNameList.add(columnName);
			}

			Iterator<Row> rowIterator = dp.rowIterator(columnNameList);
			while (rowIterator.hasNext()) {
				Row rowObject = rowIterator.next();
				if (rowObject != null) {
					Cell<?> objectIDCell = rowObject.getCell("r_object_id");
					if (objectIDCell != null) {
						return true;
					}
				}
			}
			return false;

		}*/

		// Enable this action only for Advanced Search views. Disable for Simple Search.
		Boolean enable = true;
/*		String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
		System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
		if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
			enable=false;
		}*/		
		return enable;
	}

	public boolean execute(String actionName,
			IConfigElement configElement,
			ArgumentList argumentList,
			Context context,
			Component component,
			Map map) {
		return true;
	}
}
