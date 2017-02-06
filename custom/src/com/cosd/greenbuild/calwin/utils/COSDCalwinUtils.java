package com.cosd.greenbuild.calwin.utils;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.util.DfcUtils;

public class COSDCalwinUtils {

	public String getCatSubcatDoctypeTextFromValueID(String valId, Boolean isCategory, Boolean isSubcategory, IDfSession sess)
	{
		String dqlQueryString = COSDCalwinConstants.DOCTYPE_TITLE_FROM_VALUE_ID_QUERY + valId + "'";
		String title = null;
		if (isCategory)
		{
			dqlQueryString = COSDCalwinConstants.CATEGORY_TITLE_FROM_VALUE_ID_QUERY + valId + "'";
		} 
		if (isSubcategory)
		{
			dqlQueryString = COSDCalwinConstants.SUBCATEGORY_TITLE_FROM_VALUE_ID_QUERY + valId + "'";
		}
		IDfCollection iCollection = null;
		IDfQuery query = DfcUtils.getClientX().getQuery();
		query.setDQL(dqlQueryString);
		try {
			iCollection = query.execute(sess, 0);
			for (; iCollection.next() == true;) {
				title = iCollection.getString("title");
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
		return title;
	}
	
	
	public String getCatSubcatDoctypeValueID(String title, Boolean category, Boolean subcategory, IDfSession sess){
		
		String dqlQueryString = COSDCalwinConstants.DOCTYPE_VALUE_ID_QUERY + title +"'";
		Integer valueId = 0;
		if (category)
		{
			dqlQueryString = COSDCalwinConstants.CATEGORY_VALUE_ID_QUERY + title +"'";
		}
		if (subcategory)
		{
			dqlQueryString = COSDCalwinConstants.SUBCATEGORY_VALUE_ID_QUERY + title +"'";
		}
		IDfCollection iCollection = null;
		IDfQuery query = DfcUtils.getClientX().getQuery();
		query.setDQL(dqlQueryString);
		try {
			iCollection = query.execute(sess, 0);
			for (; iCollection.next() == true;) {
				valueId = iCollection.getInt("value_id");
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
		return valueId.toString();
	}
	
}
