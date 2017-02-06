package com.cosd.greenbuild.calwin.utils;

import com.cosd.greenbuild.calwin.adobe.AdobeRESTClient;

public interface COSDCalwinConstants {

	/**
	 * Constants related to configuration names and values
	 *
	 * @see CDCRConfig
	 *
	 * @author Andy.Taylor
	 */
	public interface CONFIG {
		/**
		 * File prefix on LC server when using rest services.
		 */
		public static final String ADOBE_REST_PREFIX = "adobe_ws_prefix";
		/**
		 * Default file prefix on LC server when using rest services.
		 */
		public static final String ADOBE_REST_PREFIX_DEFAULT = "/rest/services/";
		/**
		 * adobe_ws_server: Hostname for Adobe LiveCycle server (required)
		 *
		 * @see AdobeRESTClient#AdobeRESTClient(String, String, CDCRConfig)
		 */
		public static final String ADOBE_WS_SERVER = "adobe_ws_server";
		/**
		 * adobe_ws_port: Port for Adobe LiveCycle server
		 *
		 * @see AdobeRESTClient#AdobeRESTClient(String, String, CDCRConfig)
		 */
		public static final String ADOBE_WS_PORT = "adobe_ws_port";

		/**
		 * Authentication type to use when communicating with Adobe
		 */
		public static final String ADOBE_WS_AUTH = "adobe_ws_auth";
		/**
		 * Use no authentication (the default)
		 */
		public static final String ADOBE_WS_AUTH_NONE = null;
		/**
		 * Use basic authentication (over https suggested)
		 */
		public static final String ADOBE_WS_AUTH_BASIC = "basic";
		/**
		 * Username to provide when authentication is used
		 */
		public static final String ADOBE_WS_USER="adobe_ws_username";
		/**
		 * Default username to use
		 */
		//public static final String ADOBE_WS_USER_DEFAULT="erms_dctm_lc_admin"; //TO-DO: Change
		public static final String ADOBE_WS_USER_DEFAULT="dm_user";
		/**
		 * Password for user when authentication is used.
		 */
		public static final String ADOBE_WS_PASS="adobe_ws_password";
		/**
		 * Default password to use, as provided via CryptTool
		 * <p/>
		 * java -cp build/cdcr-tools.jar com.cdcr.soms.utils.CryptTool [pass]
		 *
		 * @see CryptTool
		 */
		public static final String ADOBE_WS_PASS_DEFAULT="ZG1fcGFzc3dvcmQ="; //TO-DO: Change (DONE!!)
		/**
		 * 7003: Default value for ADOBE_WS_PORT
		 */
		//public static final int ADOBE_WS_PORT_DEFAULT = 7003; //TO-DO: Change?
		public static final int ADOBE_WS_PORT_DEFAULT = 8080;
		/**
		 * http_mode: Protocol to use when communicating with LiveCycle server
		 */
		public static final String ADOBE_HTTP_MODE = "http_mode";
		public static final String ADOBE_HTTP_MODE_HTTP = "http";
		public static final String ADOBE_HTTP_MODE_HTTPS = "https";
		/**
		 * http: Default value for ADOBE_HTTP_MODE
		 */
		public static final String ADOBE_HTTP_MODE_DEFAULT = ADOBE_HTTP_MODE_HTTP;
		/**
		 * Realm for basic authentication
		 */
		public static final String ADOBE_WS_AUTH_REALM = "auth_realm";
		/**
		 * Default livecycle basic auth realm (LiveCycle)
		 */
		public static final String ADOBE_WS_AUTH_REALM_DEFAULT = "LiveCycle";
		/**
		 * rmApp: Name of application which contains the CONFIG_SET_RM_POLICY_METHOD
		 */
		public static final String APP_RM = "rmApp";
		/**
		 * SOMS_RIGHTS_MANAGEMENT_POC: Default value for APP_RM
		 */
		public static final String APP_RM_DEFAULT = "SOMS_RIGHTS_MANAGEMENT_POC";
		/**
		 * setRMPolicyMethod: Name of method within CONFIG_RM_APP to set the RM policy.
		 * Defaults to METHOD_SET_RM_POLICY_DEFAULT
		 */
		public static final String METHOD_SET_RM_POLICY = "setRMPolicyMethod";
		/**
		 * Set_RM_Policy: Default value for METHOD_SET_RM_POLICY
		 */
		public static final String METHOD_SET_RM_POLICY_DEFAULT = "Set_RM_Policy";

		/**
		 * rmTBOApp: Name of application which contains the method as specified in METHOD_SET_RM_POLICY_TBO
		 */
		public static final String APP_RM_TBO = "rmTBOApp";
		/**
		 * SOMS_RM_TBO: Default value for APP_RM
		 */
		public static final String APP_RM_TBO_DEFAULT = "SOMS_TBO_RM";
		/**
		 * setRMPolicyTBOMethod: The config entry name containing the name of the method to call for rm application for new documents
		 * (ie acl_name=='erms_default_create_acl') as called from TBO and RMService. Defaults
		 * to METHOD_SET_RM_POLICY_TBO_DEFAULT
		 */
		public static final String METHOD_SET_RM_POLICY_TBO = "setRMPolicyTBOMethod";
		/**
		 * WriteFile: Default value for METHOD_SET_RM_POLICY_TBO
		 */
		public static final String METHOD_SET_RM_POLICY_TBO_DEFAULT = "WriteFile";

		/**
		 * wholeCFilesApp: Application containing the {@link CONFIG#METHOD_MASHUP}
		 */
		//public static final String APP_WHOLE_CFILES = "wholeCFilesApp"; //TO-DO: Change
		public static final String APP_WHOLE_CFILES = "GB_MashUp";
		//public static final String APP_WHOLE_CFILES_EXPORT = "GB_ExportPDF";
		public static final String APP_WHOLE_CFILES_EXPORT = "GB_MashUp";
		/**
		 * SOMS_Mashup_Whole_C_Files: Default value for APP_WHOLE_CFILES
		 */
		//public static final String APP_WHOLE_CFILES_DEFAULT = "SOMS_Mashup_Whole_C_Files"; //TO-DO: Change
		public static final String APP_WHOLE_CFILES_DEFAULT = "GB_MashUp";
/*		public static final String APP_WHOLE_CFILES_EXPORT_DEFAULT = "GB_ExportPDF"; */
		public static final String APP_WHOLE_CFILES_EXPORT_DEFAULT = "GB_MashUp";
		/**
		 * wholeCFilesMethod: Method within {@link APP_WHOLE_CFILES} which performs a mashup
		 */
		//public static final String METHOD_MASHUP = "wholeCFilesMethod"; //TO-DO: Change
		public static final String METHOD_MASHUP = "MashUp";
		/**
		 * CombinePDFDocs4: Default value for METHOD_MASHUP (CombinePDFDocs4)
		 */
		//public static final String METHOD_MASHUP_DEFAULT = "CombinePDFDocs4";
		public static final String METHOD_MASHUP_DEFAULT = "MashUp";
		/**
		 * saveMashupMethod: Method within {@link APP_WHOLE_CFILES} which saves a mashup for
		 * export
		 */
		//public static final String METHOD_SAVE_MASHUP = "saveMashupMethod";
		//public static final String METHOD_SAVE_MASHUP = "ExportNoPolicy";
		public static final String METHOD_SAVE_MASHUP = "MashUpWithReturnDoc";
		/**
		 * SaveMashUpDocument: Default value for METHOD_SAVE_MASHUP
		 */
		//public static final String METHOD_SAVE_MASHUP_DEFAULT = "SaveMashUpDocument";
		//public static final String METHOD_SAVE_MASHUP_DEFAULT = "ExportNoPolicy";
		public static final String METHOD_SAVE_MASHUP_DEFAULT = "MashUpWithReturnDoc";
		/**
		 * mashup_mode: configuration key to specify mode
		 */
		public static final String MASHUP_MODE = "mashup_mode";
		/**
		 * adts: configuration value to specify that ADTS (CTS) should be used
		 * for mashups (not fully validated)
		 */
		public static final String MASHUP_MODE_ADTS = "adts";
		/**
		 * adobe: configuration value to specify that Adobe LiveCycle is to be
		 * used for mashups (preferred)
		 */
		public static final String MASHUP_MODE_ADOBE = "adobe";
		/**
		 * mashup_fg_max_count: configuration key to specify maximum number of
		 * documents in FG mashups.  If zero, no maximum will be enforced
		 */
		public static final String MASHUP_FG_MAX_COUNT = "mashup_fg_max_count";
		/**
		 * 0: Default value for MASHUP_FG_MAX_COUNT
		 */
		public static final int MASHUP_FG_MAX_COUNT_DEFAULT = 0;
		/**
		 * mashup_fg_max_length: maximum length of combined documents in fg
		 * mashups, if zero, not enforced
		 */
		public static final String MASHUP_FG_MAX_LENGTH = "mashup_fg_max_length";
		/**
		 * 500MB: Default value in bytes for MASHUP_FG_MAX_LENGTH
		 */
		public static final long MASHUP_FG_MAX_LENGTH_DEFAULT = 500*1024*1024;
		/**
		 * mashup_max_length: maximum length of combined documents for any type
		 * of mashup (fg or bg), if zero, not enforced.
		 */
		public static final String MASHUP_MAX_LENGTH = "mashup_max_length";
		/**
		 * 1GB: Default value in bytes for MASHUP_MAX_LENGTH
		 */
		public static final long MASHUP_MAX_LENGTH_DEFAULT = 1024*1024*1024;
		/** max_retries: maximum number of times to retry a document */
		public static final String MASHUP_MAX_RETRIES = "max_retries";
		/**
		 * 3: Default value for MASHUP_MAX_RETRIES
		 */
		public static final int MASHUP_MAX_RETRIES_DEFAULT = 3;
		/**
		 * retry_interval: time in ms between retries for failed mashups.
		 * mashups will not be attepted for this many ms after failure.
		 */
		public static final String MASHUP_RETRY_INTERVAL = "retry_interval";
		/**
		 * 10mins: Default value in ms for MASHUP_RETRY_INTERVAL
		 */
		public static final long MASHUP_RETRY_INTERVAL_DEFAULT = 10 * 60 * 1000;
	}

	public static final String CDCR_CFILE_DOC = "cdcr_cfile_doc"; // TO-DO: Change

	//public static final String CDCR_CFILE_MASHUP = "cdcr_cfile_mashup"; // TO-DO: Change
	public static final String CDCR_CFILE_MASHUP = "calwin_case_mashup"; 
	
	//public static final String CDCR_CABINET = "/CDCR"; // TO-DO: Change
	public static final String CDCR_CABINET = "/CalWIN";
	
	//public static final String LOOKUP_CABINET = "/CDCR-Lookup"; // TO-DO: Change
	public static final String LOOKUP_CABINET = "/CDCR Admin Cases";
	
	//public static final String MASHUPUP_CABINET = "/CDCR-Mashup"; // TO-DO: Change
	public static final String MASHUPUP_CABINET = "/CalWIN-Mashup";
	
	public static final String MASHUP_CTS_PROFILE_NAME = "condensePDF_adts";

	public static final String EFORM_RELATION_TYPE = "peer";

	public static final String EFORM_ID = "EformId123";

	public static final String MASHUP_ALL_REQUESTED = "ALL";

	public static final String MASHUP_SELECTED_REQUESTED = "SELECTED";

	/* All audit related string starts here */
	public static final String AUDIT_EVENT_MASHUP = "erms_mashup";

	/** Mashup of all documents in section performed */
	public static final String AUDIT_EVENT_MASHUP_ALL = "erms_mashupall";
	/** Mashup of selected documents performed (View Documents) */
	public static final String AUDIT_EVENT_MASHUP_SELECTED = "erms_mashupselected";
	/** Advanced mashup performed */
	public static final String AUDIT_EVENT_MASHUP_ADVANCED = "erms_mashupadvanced";
	/** RM applied to non-RM document (by LiveCycle) */
	public static final String AUDIT_EVENT_RM_APPLIED = "erms_appliedrm";
	/** on create of a mashup (normal, fg, bg)... create of a cdcr_cfile_mashup */
	public static final String AUDIT_EVENT_MASHUP_CREATE = "erms_mashup_create";
	/** on save (export) of a mashup (on LiveCycle side) */
	public static final String AUDIT_EVENT_MASHUP_SAVE = "erms_mashupsave";
	/** on delete of a mashup */
	public static final String AUDIT_EVENT_MASHUP_DELETE = "erms_mashup_delete";
	public static final String MASHUP_PRINT_AUDIT_EVENT = "erms_mashupprint";

	public static final String PREVIEW_AUDIT_EVENT = "erms_preview";

	public static final String SAVE_AUDIT_EVENT = "erms_save";

	public static final String PRINT_AUDIT_EVENT = "erms_print";

	public static final String ADD_EFORM_AUDIT_EVENT = "erms_addeform";

	public static final String UPDATE_EFORM_AUDIT_EVENT = "erms_updateeform";

	public static final String REPORT_EXCEPTION_AUDIT_EVENT = "erms_reportexception";

	public static final String AUDIT_EVENT_REMOVE_DOCUMENT = "erms_removedocument";

	public static final String METADATA_CHANGE_AUDIT_EVENT = "erms_metadatachange";

	public static final String SEND_EMAIL_AUDIT_EVENT = "erms_sendemail";

	public static final String CHECKIN_AUDIT_EVENT = "erms_checkin";

	public static final String SEARCH_AUDIT_EVENT = "erms_search_offender";

	/* All config object related strings starts here */

	public static final String MASHUP_PARENT_FOLDER = "mashup_data";

	public static final String UNIX_FILE_SEPERATOR = "/";

	/**
	 * Name of object within config directory containing Adobe LiveCycle config
	 */
	//public static final String CONFIG_NAME_ADOBE_ASSEMBLER = "erms_adobe_asembler_ws"; // TO-DO: Change
	public static final String CONFIG_NAME_ADOBE_ASSEMBLER = "cerms_adobe_asembler_ws"; // TO-DO: Change
	
	/**
	 * Name of object within config directory containing Adobe LiveCycle config for eForm processor.
	 * Used by AddEformContainer.createEform (and perhaps others) and will default
	 * to CONFIG_NAME_ADOBE_ASSEMBLER if does not exist.
	 */
	public static final String CONFIG_NAME_ADOBE_EFORM = "erms_adobe_eform_ws";

	/**
	 * Name of object within config directory containing CTS config
	 */
	public static final String CONFIG_NAME_CTS = "erms_adts_ws";

	/**
	 * cdcr number stored in session as entered by the user
	 *
	 * see SearchComponent#onSearchTrigger(com.documentum.web.form.Control,
	 *      com.documentum.web.common.ArgumentList)
	 */
	public static final String SESSION_CDCR_NUMBER = "CDCR_NUMBER";
	/**
	 * number of cdcr_cfile_doc for the selected cdcr_number
	 *
	 * see SearchComponent#onSearchTrigger(com.documentum.web.form.Control,
	 *      com.documentum.web.common.ArgumentList)
	 */
	public static final String SESSION_CDCR_NUMBER_COUNT = "CDCR_NUMBER_COUNT";
	/**
	 * the selected section (ie tab) selected by the user
	 */
	public static final String SESSION_SELECTED_SECTION = "SelectedSection";

	/* All query related strings starts here */
	public static final String QUERY_COLUMN_MARKER = "'";

	public static final String GET_CTS_PROFILES_QRY = "SELECT r_object_id FROM dm_media_profile WHERE "
			+ " ANY (filter_names='Visibility' AND filter_values='Public')";

	public static final String GET_ADOBE_WS_CONFIG_QRY = "select r_object_id from dm_document where "
			+ "folder('/CDCR-Config/Config') and object_name='erms_adobe_lc_ws'";

	public static final String GET_CTS_CONFIG_QRY = "select r_object_id from dm_document where "
			+ "folder('/CDCR-Config/Config') and object_name=''";

	public static final String GET_MASHUP_EXTERNAL_PATH_QRY = "select file_system_path from dm_location where object_name=";

	public static final String FIND_CDCR_FOLDER_QRY = "select r_object_id from cdcr_fld where folder('/CDCR') and "
			+ " object_name=";

	public static final String SECTION_LOOKUP_OBJECT_QRY = "select title from cdcr_lookup_value where value_id=";

	public static final String LOOKUP_FOLDER_LOCATION_QRY = " and folder('/CDCR-Lookup/SectionName') ";

	public static final String GET_EFORM_PARENT_QRY = "select parent_id from dm_relation where child_id=";

	public static final String GET_EFORM_CHILD_QRY = "select child_id from dm_relation where parent_id=";

	//public static final String GET_EFORM_ENABLED_DOCTYPES_FOR_BPH = "Select title, value_id from cdcr_lookup_value where "
		//	+ "eform_enabled=1 and parent_id=";

	public static final String GET_EFORM_ENABLED_DOCTYPES_FOR_BPH = "Select distinct lvl1.title as title, lvl1.value_id as value_id from cdcr_lookup_value lvl1 where "
		+ "lvl1.value_id in (select lvl2.parent_id from cdcr_lookup_value lvl2 where lvl2.eform_enabled=1) and lvl1.parent_id=";

	public static final String GET_BPH_DOCTYPE_EFORMS = "SELECT title,r_object_id,document_type from cdcr_cfile_doc where "
			+ "folder('/CDCR-Lookup/SectionName',DESCEND) and is_eform=1 and "
			+ "section=$section_id and document_type=$doctype_id";

	public static final String GET_EFORM_ENABLED_SECTIONS_QRY = "Select title, value_id from cdcr_lookup_value where "
			+ "folder ('/CDCR-Lookup/SectionName') and eform_enabled=1 order by sort_order";

	public static final String GET_EFORM_TEMPLATES_QRY = "SELECT title,r_object_id,document_type from cdcr_cfile_doc where "
			+ "folder('/CDCR-Lookup/SectionName',DESCEND) and is_eform=1 and section=";

	public static final String ORDER_BY_DOCUMENT_TYPE = " order by document_type";

	public static final String ORDER_BY_VALUE_ID = " order by value_id";

	public static final String GET_SECTION_FOR_ADD_EFORM_QRY = "cdcr_lookup_value where folder ('/CDCR-Lookup/SectionName') and value_id=";

	public static final String GET_ALL_SECTION_DOCUMENTS_QRY = "select c.r_object_id,d.sort_order,c.a_content_type,datediff(day,date(TODAY),c.document_date) as DiffDate from cdcr_cfile_doc c, "
			+ "cdcr_lookup_value d where c.cdcr_number=''{0}'' and c.section={1} and c.is_eform=0 and c.remove_document=false "
			+ "and c.document_type=d.value_id and c.document_type<>{2} UNION select c.r_object_id,d.sort_order,c.a_content_type,"
			+ "datediff(day,c.scan_date,date(TODAY)) as DiffDate from cdcr_cfile_doc c, cdcr_lookup_value d where c.cdcr_number=''{3}'' and c.section={4} "
			+ "and c.is_eform=0 and c.remove_document=false and c.document_type=d.value_id and c.document_type={5} order by 2,3,4 DESC";

	public static final String GET_BPH_DOCUMENTS_QRY = "select c.r_object_id,d.sort_order,s.sort_order,c.a_content_type,datediff(day,date(TODAY),c.document_date) as DiffDate from cdcr_cfile_doc c, "
			+ "cdcr_lookup_value d,cdcr_lookup_value s where c.cdcr_number=''{0}'' and c.section={1} and c.is_eform=0 and c.remove_document=false "
			+ "and c.document_type=d.value_id and c.document_subtype=s.value_id and s.parent_id=d.value_id and c.document_type<>{2} UNION select c.r_object_id,d.sort_order,s.sort_order,c.a_content_type,"
			+ "datediff(day,c.scan_date,date(TODAY)) as DiffDate from cdcr_cfile_doc c, cdcr_lookup_value d,cdcr_lookup_value s where c.cdcr_number=''{3}'' and c.section={4} "
			+ "and c.is_eform=0 and c.remove_document=false and c.document_type=d.value_id and c.document_subtype=s.value_id and s.parent_id=d.value_id and c.document_type={5} order by 2,3,4,5 DESC";

	public static final String DELETE_PREVIOUS_REQUESTS_QRY = "select r_object_id from cdcr_cfile_mashup where language_code=''DONE'' and cdcr_number=''{0}'' and mashup_owner=''{1}''";

	public static final String GET_LOOKUP_FROM_PARENTID_QRY = "select value_id,title from cdcr_lookup_value where parent_id=";

	public static final String GET_SECTIONS_QRY = "select value_id,title from cdcr_lookup_value where "
			+ "folder('/CDCR-Lookup/SectionName')";

	public static final String GET_ALL_EXCEPTIONS_QRY = "select title,value_id from cdcr_lookup_value where "
			+ "folder('/CDCR-Lookup/ExceptionTypes') order by 2";

	public static final String PARENT_ID_CLAUSE = " and parent_id=";

	public static final String GET_EXCEPTION_QRY = "select title from cdcr_lookup_value where "
			+ "folder('/CDCR-Lookup/ExceptionTypes') and value_id=";

	public static final String GET_PROCESS_EXCEPTION_WFLW_QRY = "dm_process where object_name='ProcessExceptionWorkflow'  and r_definition_state=2";

	public static final String GET_WKFLW_COMPONENT_QRY = "select distinct r_component_id from dmi_package where "
			+ "r_workflow_id in " + "(select router_id from dmi_queue_item where r_object_id=''{0}'')";

	public static final String GET_AUDIT_OBJ_QRY = "select user_name,event_name,time_stamp,string_1,string_2,"
			+ "string_3,string_4,string_5 from dm_audittrail where " + "audited_obj_id=''{0}'' and event_name in "
			+ "(''erms_metadatachange'',''erms_removedocument''," + "''erms_reportexception'') order by time_stamp desc";

	public static final String GET_USER_EMAIL_QRY = "select user_address from dm_user where user_name='";
	public static final String GET_TO_USER_EMAIL_QRY = "select u.user_address from dm_workflow w,dmi_queue_item q,dm_user u where q.router_id=w.r_object_id and w.supervisor_name=u.user_name and q.r_object_id='";

	public static final String GET_EFORM_DQL_SECTION1 = "Select title, value_id from cdcr_lookup_value where folder ('/CDCR-Lookup/SectionName',DESCEND) and eform_enabled=1 and parent_id=";
	public static final String GET_EFORM_DQL_SECTION2 = " and value_id=";

	public static final String GET_EFORM_DQL_DOCTYPE = "Select title, value_id from cdcr_lookup_value where folder ('/CDCR-Lookup/SectionName',DESCEND) and eform_enabled=1 and parent_id=";

	public static final String EXPORT_QRY = "select c.r_object_id,d.sort_order,c.a_content_type,datediff(day,date(TODAY),c.document_date) as DiffDate,"
			+ "datediff(day,date(TODAY),c.scan_date) as DiffScanDate,c.document_date,c.scan_date,c.section,d.title,c.document_subtype,c.r_content_size,d.value_id "
			+ "from cdcr_cfile_doc c, cdcr_lookup_value d "
			+ "where c.cdcr_number=''{0}'' and c.section={1} and c.is_eform=0 and c.remove_document=false and c.document_type=d.value_id and c.document_type<>{2}{3} "
			+ "UNION select c.r_object_id,d.sort_order,c.a_content_type,datediff(day,c.scan_date,date(TODAY)) as DiffDate,"
			+ "datediff(day,c.scan_date,date(TODAY)) as DiffScanDate,c.document_date,c.scan_date,c.section,d.title,c.document_subtype,c.r_content_size,d.value_id "
			+ "from cdcr_cfile_doc c, cdcr_lookup_value d "
			+ "where c.cdcr_number=''{4}'' and c.section={5} and c.is_eform=0 and c.remove_document=false and c.document_type=d.value_id and c.document_type={6}{7} "
			+ "order by 2,3,4 DESC,5 DESC";

/*	Changed as part of UAT 2.0 defect..
	public static final String EXPORT_BPH_QRY = "select c.r_object_id,d.sort_order,s.sort_order,c.a_content_type,datediff(day,date(TODAY),c.document_date) as DiffDate,"
			+ "datediff(day,date(TODAY),c.scan_date) as DiffScanDate,c.document_date,c.scan_date,c.section,d.title,c.document_subtype,c.r_content_size,d.value_id "
			+ "from cdcr_cfile_doc c, cdcr_lookup_value d,cdcr_lookup_value s "
			+ "where c.cdcr_number=''{0}'' and c.section={1} and c.document_subtype=s.value_id and s.parent_id=d.value_id and c.is_eform=0 and c.remove_document=false "
			+ "and c.document_type=d.value_id and c.document_type<>{2}{3} "
			+ "UNION select c.r_object_id,d.sort_order,s.sort_order,c.a_content_type,datediff(day,c.scan_date,date(TODAY)) as DiffDate,"
			+ "datediff(day,c.scan_date,date(TODAY)) as DiffScanDate,c.document_date,c.scan_date,c.section,d.title,c.document_subtype,c.r_content_size,d.value_id "
			+ "from cdcr_cfile_doc c, cdcr_lookup_value d,cdcr_lookup_value s "
			+ "where c.cdcr_number=''{4}'' and c.section={5} and c.document_subtype=s.value_id and s.parent_id=d.value_id and c.is_eform=0 and "
			+ "c.remove_document=false and c.document_type=d.value_id and c.document_type={6}{7} " + "order by 2,3,4,5 DESC";
*/
	public static final String EXPORT_BPH_QRY = "select c.r_object_id,d.sort_order,s.sort_order,c.a_content_type,datediff(day,date(TODAY),c.document_date) as DiffDate,"
		+ "datediff(day,date(TODAY),c.scan_date) as DiffScanDate,c.document_date,c.scan_date,c.section,d.title,c.document_subtype,c.r_content_size,d.value_id, s.title as subtypetitle "
		+ "from cdcr_cfile_doc c, cdcr_lookup_value d,cdcr_lookup_value s "
		+ "where c.cdcr_number=''{0}'' and c.section={1} and c.document_subtype=s.value_id and s.parent_id=d.value_id and c.is_eform=0 and c.remove_document=false "
		+ "and c.document_type=d.value_id and c.document_type<>{2}{3} "
		+ "UNION select c.r_object_id,d.sort_order,s.sort_order,c.a_content_type,datediff(day,c.scan_date,date(TODAY)) as DiffDate,"
		+ "datediff(day,c.scan_date,date(TODAY)) as DiffScanDate,c.document_date,c.scan_date,c.section,d.title,c.document_subtype,c.r_content_size,d.value_id, s.title as subtypetitle "
		+ "from cdcr_cfile_doc c, cdcr_lookup_value d,cdcr_lookup_value s "
		+ "where c.cdcr_number=''{4}'' and c.section={5} and c.document_subtype=s.value_id and s.parent_id=d.value_id and c.is_eform=0 and "
		+ "c.remove_document=false and c.document_type=d.value_id and c.document_type={6}{7} " + "order by 2,3,4,5 DESC";
	
	public static final String GET_SUBTYPES_QRY = "select value_id,title from cdcr_lookup_value where parent_id in "
			+ "(select value_id from cdcr_lookup_value where parent_id=21000) and value_id > 22000";

	public static final String GET_MASHUP_REQUEST_DQL = "select r_object_id from cdcr_cfile_mashup where language_code='' '' {0} order by r_creation_date enable(return_top 1)";

	public static final String GET_MASHUP_OBJECT_DQL = "select r_object_id from cdcr_cfile_doc where folder('/CDCR-Mashup') and a_retention_date < date(Today) and not a_retention_date is NULLDATE UNION select r_object_id from cdcr_cfile_doc where folder('/CDCR-Mashup') and dateadd(day,1,r_modify_date) < date(Today) "
			+ "and r_content_size < 10 UNION select r_object_id from cdcr_cfile_doc where folder('/CDCR-Mashup') and dateadd(day,1,r_modify_date) < date(Today) and object_name='TEMP' enable(return_top 1)";
	/**
	 * query to get object containing config
	 *
	 * @see CDCRConfig
	 */
	//public static final String GET_CONFIG_QRY = "select r_object_id from dm_document where folder(''/CDCR-Config/Config'') and object_name=''{0}''";
	public static final String GET_CONFIG_QRY = "select r_object_id from dm_document where folder(''/CalWIN-Config/Config'') and object_name=''{0}''";
	
	public static final String GET_NUM_DOCUMENTS_IN_CDCR_NUMBER_QRY = "select count(r_object_id) as numdocs from cdcr_cfile_doc where cdcr_number=''{0}''";

	public static final String GET_CODB_INFO_QRY = "select lastname from CODB_ADMIN.dm_codb_offender where cdcnumber=''{0}''";

	// All Control IDs within the "Find_C_File_VerticalBox" control - as part of
	// Defect ID #62
	public static final String TXT_AGENCY_DATA = "txt_agency_data";
	public static final String TXT_WARRANT = "txt_warrant";
	public static final String TXT_CASE_NUMBER = "txt_case_number";
	public static final String DATE_RANGE_2 = "date_range_2";
	public static final String DATE_RANGE_1 = "date_range_1";
	public static final String DATE_SINGLE_DATE = "date_single_date";
	public static final String LST_DOCUMENT_TYPE = "lst_documentType";
	
	// COSD: CalWIN
	
	// Data Month query
	public static final String GET_DATA_MONTH_QUERY = "select distinct data_month from calwin_case_doc order by data_month";
	
	// Default Category,Subcategory,Doctype Queries
	public static final String GET_DEFAULT_CATEGORY_QUERY = "select title from calwin_admin_case where is_category=1 and title!='null' order by sort_order";
	public static final String GET_DEFAULT_SUBCATEGORY_QUERY = "select title from calwin_admin_case where is_subcategory=1 and title!='null' order by sort_order";
	public static final String GET_DEFAULT_DOCTYPE_QUERY = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and title!='null' order by title";
	
	// onChangeCategoryDDDList queries
	public static final String GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE = "select title from calwin_admin_case where is_subcategory=1 and title!='null' and parent_id in (select value_id from calwin_admin_case where is_category=1 and title='";
	public static final String GET_SUBCATEGORY_QUERY_ON_CATEGORY_CHANGE_ORDER_BY = " order by sort_order";
	public static final String GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_1 = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and title!='null' and root_id in (select value_id from calwin_admin_case where is_category=1 and title='";
	public static final String GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_2 = " and parent_id in (select value_id from calwin_admin_case where is_subcategory=1 and title='";
	public static final String GET_DOCTYPE_QUERY_ON_CATEGORY_CHANGE_ORDER_BY = " order by title";
	
	// onChangeSubCategoryDDDList queries
	public static final String GET_CATEGORY_QUERY_ON_SUBCATEGORY_CHANGE = "select title from calwin_admin_case where is_category=1 and title!='null' and value_id in (select parent_id from calwin_admin_case where is_subcategory=1 and title='";
	public static final String GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_1 = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and title!='null' and parent_id in (select value_id from calwin_admin_case where is_subcategory=1 and title='";
	public static final String GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_2 = " and root_id in (select value_id from calwin_admin_case where is_category=1 and title='";
	public static final String GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_ORDER_BY = " order by title";
	public static final String GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and title!='null' and root_id in (select value_id from calwin_admin_case where is_category=1 and title='";
	public static final String GET_DOCTYPE_QUERY_ON_SUBCATEGORY_CHANGE_TO_NULL_WITH_CATEGORY_NOT_NULL_ORDER_BY = " order by title";
	
	// onChangeDoctypeDDDList queries
	public static final String GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE = "select title from calwin_admin_case where is_category=1 and title!='null' and value_id in (select root_id from calwin_admin_case where is_category=0 and is_subcategory=0 and title='";
	public static final String GET_CATEGORY_QUERY_ON_DOCTYPE_CHANGE_ORDER_BY = " order by title";
	public static final String GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE = "select title from calwin_admin_case where is_subcategory=1 and title!='null' and value_id in (select parent_id from calwin_admin_case where is_category=0 and is_subcategory=0 and title='";
	public static final String GET_SUBCATEGORY_QUERY_ON_DOCTYPE_CHANGE_ORDER_BY = " order by title";
	
	// CalwinSearchFrame Configuration Elements
	public static final String CALWIN_SEARCH_QUERY = "calwinsearchquery";
	public static final String CALWIN_SEARCH_ATTRS = "calwinsearchattr";
	public static final String CALWIN_SEARCH_TYPE = "calwinsearchtype";
	
	// Category Value ID query
	public static final String CATEGORY_VALUE_ID_QUERY = "select value_id from calwin_admin_case where is_category=1 and title='";
	
	// Sub-Category Value ID query
	public static final String SUBCATEGORY_VALUE_ID_QUERY = "select value_id from calwin_admin_case where is_subcategory=1 and title='";
	
	// Document Type Value ID query
	public static final String DOCTYPE_VALUE_ID_QUERY = "select value_id from calwin_admin_case where is_category=0 and is_subcategory=0 and title='";
	
	// Category title-from-valueID query
	public static final String CATEGORY_TITLE_FROM_VALUE_ID_QUERY = "select title from calwin_admin_case where is_category=1 and value_id='";
	
	// Sub-Category title-from-valueID query
	public static final String SUBCATEGORY_TITLE_FROM_VALUE_ID_QUERY = "select title from calwin_admin_case where is_subcategory=1 and value_id='";
	
	// Document Type title-from-valueID query
	public static final String DOCTYPE_TITLE_FROM_VALUE_ID_QUERY = "select title from calwin_admin_case where is_category=0 and is_subcategory=0 and value_id='";
}
