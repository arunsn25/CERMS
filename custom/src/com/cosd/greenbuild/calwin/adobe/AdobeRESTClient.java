package com.cosd.greenbuild.calwin.adobe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import HTTPClient.AuthorizationInfo;
import HTTPClient.CookieModule;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;

import com.cosd.greenbuild.calwin.mashup.adobe.XML;
import com.cosd.greenbuild.calwin.mashup.adobe.XMLResult;
import com.cosd.greenbuild.calwin.utils.Base64;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConfig;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * A ReST (Representational State Transfer) client which facilitates
 * communication with adobe LiveCycle ReST services.
 * this is changed
 * @author Andy.Taylor
 *
 */
public class AdobeRESTClient implements COSDCalwinConstants {

	private static final Logger log = Logger.getLogger(AdobeRESTClient.class);

	private final List<NVPair> nvPairs = new ArrayList<NVPair>();
	private HTTPResponse resp;
	private String text;
	private final URL url;
	private HTTPConnection client;
	private final String file;
	private String authRealm = CONFIG.ADOBE_WS_AUTH_REALM_DEFAULT;

	// usage of insecure only logged once
	private static boolean nonsecureLogged = false;

	public AdobeRESTClient(String application, String method, IDfSession sess) throws DfException {
		this(application, method, COSDCalwinConfig.getConfig(COSDCalwinConstants.CONFIG_NAME_ADOBE_ASSEMBLER, sess));
	}

	/**
	 * Creates a new client for the given application and method, using values
	 * from the given config.
	 * <p/>
	 * The application<sup>1</sup> and method<sup>2</sup> are as defined in
	 * adobe and found in the rest url, ie:
	 * <p/>
	 * <i>http://somehost:7003/rest/services/SOMS_Mashup_Whole_C_Files<sup>1</
	 * sup>/wholeCFilesMethod<sup>2</sup></i><br/>
	 *
	 * <p/>
	 * The configuration object is a {@link CDCRConfig} object (usually fetched
	 * with {@link CDCRConfig#getConfig(String, IDfSession)}). The following
	 * values are used from the config:
	 * <ul>
	 * <li><i>CONFIG.ADOBE_WS_SERVER (adobe_ws_server)</i>: The host on which
	 * LiveCycle rest services are running (<span
	 * stype="color: red">REQUIRED</span>)</li>
	 * <li><i>CONFIG.ADOBE_HTTP_MODE (http_mode)</i>: The protocol to be used
	 * for connecting. One of http or https (default: http)</li>
	 * <li><i>CONFIG.ADOBE_WS_PORT (adobe_ws_port)</i>: The port on which
	 * LiveCycle is listening (default: 7003)</li>
	 * <li><i>CONFIG.ADOBE_WS_USER (adobe_ws_username)</i>: The username used to
	 * connect to adobe when adobe_ws_auth is specified (default:
	 * erms_dctm_lc_admin)</li>
	 * <li><i>CONFIG.ADOBE_WS_PASS (adobe_ws_password)</i>: The password for the
	 * user used to connect. This password should be crypted using CryptTool,
	 * which creates cryped strings. CryptTool can be run from the directory
	 * containing cdcr-tools.jar via:<br/>
	 * java -cp cdcr-tools.jar com.cdcr.soms.utils.CryptTool [pass]<br/>
	 * (default: the crypted default erms password for erms_dctm_lc_admin)</li>
	 * <li><i>CONFIG.ADOBE_WS_AUTH_REALM (auth_realm)</i>: The authentication
	 * realm for Adobe LiveCycle basic auth (default: LiveCycle)</li>
	 * <li><i>CONFIG.ADOBE_WS_AUTH (adobe_ws_auth)</i>: The type of
	 * authentication to used when talking to LiveCycle. If not specified, no
	 * authentication will be used. In this case, authentication needs to be
	 * disabled on LiveCycle. If specified, this value should be 'basic', which
	 * indicates basic authentication should be used. When basic auth is used,
	 * the values for <i>adobe_ws_username</i>, <i>adobe_ws_password</i> and
	 * <i>auth_realm</i> are used when establishing the connection.</li>
	 * </ul>
	 * <p/>
	 * ReST service reachability can be validated from a browser. It should come
	 * back with a response, or perhaps a 500 error if parameters are missing. A
	 * 404 indicates that the method or application is incorrect, a 401
	 * indicates that security is required and not given.
	 *
	 * @param application
	 *            Application as specified in Adobe
	 * @param method
	 *            The method as declared in Adobe
	 * @param cosdCalwinConfig
	 *            The configuration object as fetched with
	 *            {@link CDCRConfig#getConfig(String, IDfSession)}
	 */
	public AdobeRESTClient(String application, String method, COSDCalwinConfig cosdCalwinConfig) {
		try {
			String protocol = cosdCalwinConfig.get(CONFIG.ADOBE_HTTP_MODE, CONFIG.ADOBE_HTTP_MODE_DEFAULT);
			String host = cosdCalwinConfig.get(CONFIG.ADOBE_WS_SERVER);
			int port = cosdCalwinConfig.getInteger(CONFIG.ADOBE_WS_PORT, CONFIG.ADOBE_WS_PORT_DEFAULT);
			String authMode = cosdCalwinConfig.get(CONFIG.ADOBE_WS_AUTH, CONFIG.ADOBE_WS_AUTH_NONE);
			String user = null;
			String pass = null;
			if (authMode != CONFIG.ADOBE_WS_AUTH_NONE) {
				user = cosdCalwinConfig.get(CONFIG.ADOBE_WS_USER, CONFIG.ADOBE_WS_USER_DEFAULT);
				pass = cosdCalwinConfig.get(CONFIG.ADOBE_WS_PASS, CONFIG.ADOBE_WS_PASS_DEFAULT);
				this.authRealm = cosdCalwinConfig.get(CONFIG.ADOBE_WS_AUTH_REALM, CONFIG.ADOBE_WS_AUTH_REALM_DEFAULT);
				if (pass != null&&pass.trim().length()>0) {
					byte[] decode = Base64.decodeFast(pass);
					if (decode!=null)
						pass = new String(decode);
				}
				if (CONFIG.ADOBE_WS_AUTH_BASIC.equals(authMode)) {
					if (protocol.equals(CONFIG.ADOBE_HTTP_MODE_HTTP) && !nonsecureLogged) {
						log.warn("Authentication is being used over a non-secure connection", new RuntimeException("here"));
						nonsecureLogged = true;
					}
				} else
					throw new IllegalArgumentException(AdobeRESTClient.class.getName()
							+ " does not support authentication method: " + authMode + ".  Set " + CONFIG.ADOBE_WS_AUTH + " to "
							+ CONFIG.ADOBE_WS_AUTH_BASIC + " in " + cosdCalwinConfig.loadedFrom());
			}
			String urlStr = protocol + "://";
			if (user != null)
				urlStr += user + ":" + (pass != null ? pass : "") + "@";
			urlStr += host;
			if (protocol.equals(CONFIG.ADOBE_HTTP_MODE_HTTP) && port != 80)
				urlStr += ":" + port;
			else if (protocol.equals(CONFIG.ADOBE_HTTP_MODE_HTTPS) && port != 443)
				urlStr += ":" + port;
			this.file = cosdCalwinConfig.get(CONFIG.ADOBE_REST_PREFIX, CONFIG.ADOBE_REST_PREFIX_DEFAULT) + application + "/" + method;
			urlStr += file;

			try {
				//System.out.println("REST URL: " + urlStr);
				this.url = new URL(urlStr);
			} catch (MalformedURLException e) {
				log.warn("Error creating url from: "+urlStr);
				throw e;
			}
			this.client = new HTTPConnection(url);
		} catch (Throwable e) {
			throw new RuntimeException("Unexpected error", e);
		}
	}

	public AdobeRESTClient(URL url) throws DfException {
		try {
			this.url = url;
			this.file = url.getFile();
			this.client = new HTTPConnection(url);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error", e);
		}
	}

	public AdobeRESTClient(String protocol, String host, int port, String application, String method) {
		this.file = CONFIG.ADOBE_REST_PREFIX + application + "/" + method;
		try {
			this.url = new URL(protocol, host, port, file);
			this.client = new HTTPConnection(url);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error", e);
		}
	}

	public void addParameter(String name, String val) {
		nvPairs.add(new NVPair(name, val));
	}

	public String invoke() throws IOException {
		// log.info("Connect to: "+this);
		NVPair[] params = new NVPair[nvPairs.size()];
		for (int i = 0; i < nvPairs.size(); i++) {
			NVPair curr = nvPairs.get(i);
			params[i] = curr;
		}

		try {
			// blindly accept cookies.. mmm, fresh cookies.
			CookieModule.setCookiePolicyHandler(null);
			// never prompt
			client.setAllowUserInteraction(false);

			if (url.getUserInfo() != null) {
				String[] uInfo = url.getUserInfo().split(":");
				String user = uInfo[0];
				String pass = uInfo.length > 1 ? uInfo[1] : null;
				client.addBasicAuthorization(this.authRealm, user, pass);
			} else
				// don't do authentication
				AuthorizationInfo.setAuthHandler(null);

			this.resp = client.Post(file, params);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(resp.getInputStream()));
				String msg = in.readLine();
				while (msg != null) {
					if (text == null)
						text = msg;
					else
						text += "\n" + msg;
					msg = in.readLine();
				}
			} catch (IOException e) {
				System.err.println("No response text.");
				text = null;
			}
			if (resp.getStatusCode() >= 400) {
				log.warn("Error indicated response to request:" + this);
				throw new IOException(resp.getStatusCode() + " " + resp.getReasonLine() + "\nError during request to: " + getURL()
						+ (text != null ? "\nresponse ---\n" + text + "\n----" : "(no response)"));
			}

			return text;
		} catch (ConnectException e) {
			throw new ConnectException("Error connecting to: " + url);
		} catch (ModuleException e) {
			e.printStackTrace();
			throw new IOException("Error reading response from: " + url + ": " + e.getMessage());
		}
	}

	public HTTPResponse getResponse() {
		return resp;
	}

	public String getText() {
		return text;
	}

	public HTTPConnection getClient() {
		return client;
	}

	/**
	 * Set the authentication realm, if user info is passed in the connection
	 * URL. By default, basic authentication is used if a user and password is
	 * given. To do alternate authentication, call @link
	 * {@link AuthorizationInfo#setAuthHandler(HTTPClient.AuthorizationHandler)}
	 *
	 * @param authRealm
	 */
	public void setAuthRealm(String authRealm) {
		this.authRealm = authRealm;
	}

	@Override
	public String toString() {

		String msg = "AdobeRESTClient call to: " + getURL() + "\n";
		msg += "Request parameters: \n";
		for (NVPair curr : nvPairs)
			msg += "\t" + curr.getName() + ":\t" + curr.getValue() + "\n";

		try {
			if (resp != null) {
				msg += "Response: " + resp.getStatusCode() + " " + resp.getReasonLine() + "\n";
				Enumeration<?> hdrE = resp.listHeaders();
				while (hdrE.hasMoreElements()) {
					String key = (String) hdrE.nextElement();
					String val = resp.getHeader(key);
					msg += "\t" + key + ": " + val + "\n";
				}
				if (text != null)
					msg += text;
				else
					msg += "(no response text)";
			} else
				msg += "Response: Not yet submitted.";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * Gets the URL with basic auth password obfuscated, if any. Use this method
	 * when printing, logging, etc
	 *
	 * @return the obfuscated url
	 */
	public URL getURL() {
		if (url.getUserInfo() == null || url.getUserInfo().trim().length() == 0)
			return url;
		String urlStr = url.toString();
		int first = urlStr.indexOf(":");
		if (first > 0) {
			int start = urlStr.indexOf(":", first + 1);
			if (start > 0) {
				int end = urlStr.indexOf("@");
				if (end > 0) {
					String pad = "";
					for (int i = start + 1; i < end; i++)
						pad += "*";
					urlStr = urlStr.substring(0, start + 1) + pad + urlStr.substring(end);
				}
			}
		}
		try {
			return new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return url;
		}
	}

	/*
	 * public static void main(String[] args) { try { URL url=new
	 * URL("http://user:pass@host.com/rest/services/APP/METHOD");
	 * AdobeRESTClient test = new AdobeRESTClient(url);
	 * System.out.println(test.getURL()); url=new
	 * URL("http://host.com/rest/services/APP/METHOD"); test = new
	 * AdobeRESTClient(url); System.out.println(test.getURL()); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 *
	 * public URL getFullURL() { String pStr=""; for (NVPair curr : nvPairs) {
	 * if (pStr.length()>0) pStr+="&"; String val=curr.getValue(); try {
	 * val=URLEncoder.encode(val, "UTF-8"); } catch
	 * (UnsupportedEncodingException e) { // should never happen } pStr +=
	 * curr.getName() + "=" + val; }
	 *
	 * String fullURL=url.toString(); if (pStr.length()>0) fullURL+="?"+pStr;
	 * try { return new URL(fullURL); } catch (MalformedURLException e) { //
	 * shouldn't happen e.printStackTrace(); return url; } }
	 */

	/**
	 * Invoke and get the result as an XMLResult, which contains a document and
	 * methods for easy data access.
	 *
	 * @return XMLResult of invoke
	 * @throws IOException
	 *             if something goes wrong
	 */
	public XMLResult invokeGettingXML() throws IOException {
		String val = invoke();
		Document doc = XML.loadDocument(val);
		return new XMLResult(doc.getDocumentElement());
	}

}
