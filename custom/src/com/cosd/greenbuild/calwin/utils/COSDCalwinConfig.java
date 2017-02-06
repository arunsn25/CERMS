package com.cosd.greenbuild.calwin.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;

/**
	 * When this method is used, the config will be cached in the VM for a NAMED_CACHE_TIME
	 * (default 5m), which increases performance.  The implication of this is that it
	 * will take at most 5m for new values to be read.
 *
 * @author Andy.Taylor
 *
 */
public class COSDCalwinConfig implements COSDCalwinConstants {

	public static final Logger log = Logger.getLogger(COSDCalwinConfig.class);

	/** Maximum time that a config should be cached.  A new config will be fetched and parsed if time is longer than this */
	public static final long MAX_CACHE_TIME = 5*60*1000;
	public static final String CONFIG_KEYVALUE_SEPERATOR = "=";

	public class MissingConfigurationEntryException extends Exception {

		private static final long serialVersionUID = 1L;

		public MissingConfigurationEntryException(String entryName) {
			super("Missing configuration entry: " + entryName);
			log.warn("Missing configuration entry: " + entryName + " in keywords of object as fetched with: \n" + query);
		}
	}

	private static Map<String,COSDCalwinConfig> configByDQL=new HashMap<String, COSDCalwinConfig>();

	private final Map<String, String> configMap = new HashMap<String, String>();;
	private final String query;
	private long lastFetch=0;

	protected COSDCalwinConfig(String dql, IDfSession sess) throws DfException {

		this.query = dql;

		refresh(sess);
	}

	/**
	 * Query the docbase and get and parse a config object
	 * @param session
	 * @param query
	 * @return
	 * @throws DfException
	 */
	/**
	 * Get int value of configuration entry with given name
	 *
	 * @param name value is returned for the configuration entry of this name
	 * @return integer value of named configuration entry.
	 * @throws NumberFormatException if the configuration value is not an integer
	 * @throws MissingConfigurationEntryException if the configuration entry does not exist
	 */
	public Integer getInteger(String name) throws NumberFormatException, MissingConfigurationEntryException {
		return Integer.parseInt(get(name));
	}

	/**
	 * Get the integer value of the named configuration entry, or the default value if the configuration
	 * entry does not exist
	 *
	 * @param name name of config entry value to return
	 * @param defaultVal value to return if config entry is not defined
	 * @return entry value
	 * @throws NumberFormatException if the config value is not an integer
	 */
	public int getInteger(String name, int defaultVal) throws NumberFormatException {
		String v = get(name, "" + defaultVal);
		return Integer.parseInt(v);
	}

	/**
	 * Get long value of configuration entry with given name
	 *
	 * @param name value is returned for the configuration entry of this name
	 * @return integer value of named configuration entry.
	 * @throws NumberFormatException if the configuration value is not an long
	 * @throws MissingConfigurationEntryException if the configuration entry does not exist
	 */
	public Long getLong(String name) throws NumberFormatException, MissingConfigurationEntryException {
		return Long.parseLong(get(name));
	}

	/**
	 * Get the long value of the named configuration entry, or the default value if the configuration
	 * entry does not exist
	 *
	 * @param name name of config entry value to return
	 * @param defaultVal value to return if config entry is not defined
	 * @return entry value
	 * @throws NumberFormatException if the config value is not an long
	 */
	public long getLong(String name, long defaultVal) {
		String v = get(name, "" + defaultVal);
		return Long.parseLong(v);
	}

	/**
	 * Gets the value of the configuration entry with the given name.  If the entry does
	 * not exist, then a MissingConfigurationEntryException is thrown.  To prevent the throwing
	 * of the exception, call {@link #get(String, String)} with a default value (which may be null).
	 *
	 * @param name name of configuration entry value to retrieve
	 * @return value of the named configuration entry
	 * @throws MissingConfigurationEntryException if there is no entry with the given name
	 */
	public String get(String name) throws MissingConfigurationEntryException {
		String v = configMap.get(name);
		if (v == null)
			throw new IllegalArgumentException("Missing configuration entry: " + name + "\nas loaded with: " + query);
		return v;
	}

	/**
	 * Gets the value of the configuration entry with the given name.  If there is no named value, the default
	 * value is returned.
	 *
	 * @param name value for the entry of this name is returned
	 * @param defaultVal the default value to return if no entry was specified
	 * @return the value of the named configuration entry
	 */
	public String get(String name, String defaultVal) {
		if (!configMap.containsKey(name))
			return defaultVal;
		return configMap.get(name);
	}

	/**
	 * Creates a new config helper which fetches configuration from the named object
	 * within the config directory (/CDCR-Config/config).
	 * <p/>
	 * NOTE: if no config is found, and empty config is created and returned.  To handle the
	 * non-existance of a config, use {@link #exists(String, IDfSession)}
	 *
	 * @param configObjectName name of the object in the config directory containing config.
	 * 	Typically one of the CONFIG_NAME_* constants
	 * @param sess
	 * @throws DfException
	 */
	public static COSDCalwinConfig getConfig(String configObjectName, IDfSession sess) throws DfException {
		Object[] objArgs = { configObjectName };
		MessageFormat form = new MessageFormat(GET_CONFIG_QRY);
		String dql = form.format(objArgs);
		return getConfigByDQL(dql,sess);
	}

	/**
	 * Creates a config helper which gets the configuration from the object specified by the
	 * given dql.  DQL should return an r_object_id.
	 * <p/>
	 * NOTE: if no config is found, and empty config is created and returned.  To handle the
	 * non-existance of a config, use {@link #exists(String, IDfSession)}
	 *
	 * @param dql
	 * @param sess
	 * @return
	 * @throws DfException
	 */
	public static COSDCalwinConfig getConfigByDQL(String dql, IDfSession session) throws DfException {
		// NOTE configByDQL put done in refresh(IDfSession)
		COSDCalwinConfig cfg=configByDQL.get(dql);
		if (cfg==null)
			cfg=new COSDCalwinConfig(dql,session);
		else if (System.currentTimeMillis()-cfg.loadedAt()>MAX_CACHE_TIME)
			cfg.refresh(session);
		return cfg;
	}

	/**
	 *
	 * @return the last time the config values were fetched.  Call refresh to force a refresh.
	 */
	public long loadedAt() {
		return lastFetch;
	}

	/**
	 *
	 * @return query returning object that this config was loaded from
	 */
	public String loadedFrom() {
		return query;
	}

	/**
	 * Reload configuration using the given session.  Original DQL will be used.
	 *
	 * @param session
	 * @throws DfException
	 */
	public void refresh(IDfSession session) throws DfException {
		if (!configMap.isEmpty() && DfLogger.isDebugEnabled(this))
			DfLogger.debug(this, "Reloading config via: " + query, null, null);
		Map<String, String> tMap = new HashMap<String, String>();
		String objectID = null;
		DfLogger.debug(this, "Config Object Query [" + query + "]", null, null);

		IDfQuery idfQry = new DfQuery();
		idfQry.setDQL(query);
		IDfCollection coll = idfQry.execute(session, DfQuery.DF_READ_QUERY);
		if (coll.next()) {
			objectID = coll.getString("r_object_id");
			DfLogger.debug(this, "Config Object's Object ID [" + objectID + "]", null, null);
		}
		coll.close();

		if (objectID != null) {
			IDfSysObject configObj = (IDfSysObject) session.getObject(new DfId(objectID));
			int keywordsCount = configObj.getKeywordsCount();
			DfLogger.debug(this, "Total keywords in Config Object [" + keywordsCount + "]", null, null);
			for (int count = 0; count < keywordsCount; count++) {
				String keyValuePair = configObj.getKeywords(count);
				DfLogger.debug(this, "keyValuePair at[" + count + "] index is [" + keyValuePair + "]", null, null);
				if (keyValuePair.contains(CONFIG_KEYVALUE_SEPERATOR)) {
					StringTokenizer strTok = new StringTokenizer(keyValuePair, CONFIG_KEYVALUE_SEPERATOR);
					if (strTok.hasMoreTokens()) {
						String key = strTok.nextToken();
						String value = strTok.nextToken();
						DfLogger.debug(this, "Storing key [" + key + "] value [" + value + "] in the hashmap", null, null);
						tMap.put(key, value);
					}
				}
			}
		} else {
			DfLogger.warn(this, "No configuration found with dql", new String[] { query }, null);
		}
		// synchronized to avoid thread issues.
		synchronized (this.configMap) {
			this.configMap.clear();
			for (String key : tMap.keySet())
				this.configMap.put(key, tMap.get(key));
		}
		DfLogger.debug(this, configMap.toString(), null, null);
		lastFetch=System.currentTimeMillis();
		configByDQL.put(query, this);
	}

	/**
	 * Checks to see if a config of the given name exists
	 *
	 * @param name name of config to check for, resolves to /CDCR-Config/config/[name]
	 * @param session session to use
	 * @return true if there is a config with the given name
	 * @throws DfException if something goes wrong
	 */
	public static boolean exists(String name, IDfSession session) throws DfException {
		Object[] objArgs = { name };
		MessageFormat form = new MessageFormat(GET_CONFIG_QRY);
		String dql = form.format(objArgs);
		IDfQuery idfQry = new DfQuery();
		idfQry.setDQL(dql);
		IDfCollection coll = idfQry.execute(session, DfQuery.DF_READ_QUERY);
		try {
			return coll.next();
		} finally {
			coll.close();
		}
	}

}
