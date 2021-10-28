package chamette.tools;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.*;

public class RestTool {

	public int returnCode;
	public JsonObject result;
	public org.w3c.dom.Document resultXml;
	public String content;
	public String urlStart;
	public org.apache.http.HttpResponse httpResponse;
	public org.apache.http.HttpEntity entity;
	
	private Logger logger;
	private int connectTimeout = 0;
	private int socketTimeout = 0;
	private HttpHost httpProxyHost = null;
	private Executor executor;
	private String basicAuthentUser = null;
	private String basicAuthentPassword = null;
	private HashMap<String, String> headers = new HashMap<String, String>();
	private Charset entityCharset = null;
	
	public RestTool(String urlStart) {
		logger = Logger.getLogger(this.getClass());
		executor = Executor.newInstance();
		this.urlStart = urlStart;
	}

	public RestTool post(String url, JsonObject json) throws Exception {
		post(url, JsonHelper.getStringFromJsonObject(json));
		return this;
	}
	
	public RestTool post(String url, String requestString) throws Exception {
		simpleCall("POST", url, requestString);
		return this;
	}
	
	/**
	 * Simple HTTP Get
	 * @param url
	 * @return This RestHelper object to be fluent
	 * @throws Exception
	 */
	public RestTool get(String url) throws Exception {
		simpleCall("GET", url, null);
		return this;
	}
	
	/**
	 * Simple HTTP Get but the full url must be specified in the constructor
	 * @param url
	 * @return This RestHelper object to be fluent
	 * @throws Exception
	 */
	public RestTool get() throws Exception {
		get("");
		return this;
	}
	
	public RestTool delete(String url) throws Exception {
		simpleCall("DELETE", url, null);
		return this;
	}
	
	public RestTool simpleCall(String method, String url, String input) throws Exception {
		simpleCall(method, url, "json", input);
		return this;
	}
	
	public RestTool simpleCall(String method, String url, String inputType, String input) throws Exception {
		//logger.setLevel(Level.DEBUG);
		String realUrl = urlStart+url; 
		
		logger.debug(method+" "+realUrl);
		logger.debug("input: "+input);
 
		Request request = null;
		if (method.toLowerCase().equals("put"))     request = Request.Put(realUrl);
		if (method.toLowerCase().equals("get"))     request = Request.Get(realUrl);
		if (method.toLowerCase().equals("delete"))  request = Request.Delete(realUrl);
		if (method.toLowerCase().equals("post"))    request = Request.Post(realUrl);
		if (method.toLowerCase().equals("head"))    request = Request.Head(realUrl);
		if (method.toLowerCase().equals("options")) request = Request.Options(realUrl);
		if (method.toLowerCase().equals("patch"))   request = Request.Patch(realUrl);
		if (method.toLowerCase().equals("trace"))   request = Request.Trace(realUrl);
		
		if (httpProxyHost != null) request.viaProxy(httpProxyHost);
		if (connectTimeout > 0) request.connectTimeout(connectTimeout);
		if (socketTimeout > 0)  request.socketTimeout(socketTimeout);
		
		logger.debug("basicAuthentUser="+basicAuthentUser+"; basicAuthentPassword="+basicAuthentPassword);
		if (basicAuthentUser != null) {
			String auth = basicAuthentUser + ":" + basicAuthentPassword;
			String authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
			logger.debug("request.setHeader("+HttpHeaders.AUTHORIZATION+", "+authHeader+")");
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		}
		
		for (String key : headers.keySet()) {
			request.setHeader(key, headers.get(key));
		}
		
		if (input != null) {
			if (inputType == "json") request.bodyString(input, org.apache.http.entity.ContentType.APPLICATION_JSON);
			else if (inputType == "xml") request.bodyString(input, org.apache.http.entity.ContentType.APPLICATION_XML);
			else request.bodyString(input, org.apache.http.entity.ContentType.create(inputType));
		}

		org.apache.http.client.fluent.Response response = executor.execute(request);
		httpResponse = response.returnResponse();
		returnCode = httpResponse.getStatusLine().getStatusCode();
		logger.debug("\nhttpResponse.getStatusLine()="+httpResponse.getStatusLine());
				
		entity = httpResponse.getEntity();
		if (entity != null && entityCharset == null) {
			content = org.apache.http.util.EntityUtils.toString(entity).trim();
			logger.debug("defaut charset : "+entity.getContentEncoding());
		} else if (entity != null && entityCharset != null) {
			content = org.apache.http.util.EntityUtils.toString(entity, entityCharset).trim();
			logger.debug("forced charset : "+entityCharset);
		}
		
		logger.debug("\n"+content+"\n");
		result = null;
		if (content.trim().startsWith("{")) {
			try {
				JsonReader reader = Json.createReader(new StringReader(content));
				result = (JsonObject) reader.read();
			} catch (Exception e) {
				String exceptionText = "Invalid json response: "+content.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
				if (exceptionText.length() > 100) exceptionText = exceptionText.substring(0, 100);
				logger.error(exceptionText);
				logger.error("\n"+content+"\n");
				throw new Exception(exceptionText);
			}
		}
		
		resultXml = null;
		if (content.trim().startsWith("<")) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				resultXml = dBuilder.parse(content);
 			} catch (Exception e) {
				String exceptionText = "Invalid xml response: "+content.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
				if (exceptionText.length() > 100) exceptionText = exceptionText.substring(0, 100);
				logger.error(exceptionText);
				logger.error("\n"+content+"\n");
				throw new Exception(exceptionText);
			}
		}
		return this;
	}
		
	/**
	 * Set the response timeout (in milliseconds).
	 */
	public RestTool setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	/**
	 * Set the socket connection timeout (in milliseconds).
	 */
	public RestTool setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}
	
	/**
	 * Set the proxy host and an optionnal authentification.<br>
	 * Attention : this is incompatible with ignoreSSLCertificatVerification().<br>
	 * Sample creds = new org.apache.http.auth.NTCredentials("S566605", "my password", "proxy hostname", "SIEGE");
	 */
	public RestTool setProxy(String hostname, int port, org.apache.http.auth.Credentials creds) {
		httpProxyHost = new HttpHost(hostname, port);
		executor.authPreemptive(httpProxyHost);
		if (creds != null) {
			executor.auth(httpProxyHost, creds);
		}
		return this;
	}

	/**
	 * Set a basic authentication for the request.
	 */
	public RestTool setBasicAuthentication(String basicAuthentUser, String basicAuthentPassword) {
		this.basicAuthentUser = basicAuthentUser;
		this.basicAuthentPassword = basicAuthentPassword;
		return this;
	}
	
	/**
	 * Add a header in the request.
	 */
	public RestTool addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	/**
	 * Force an entity charset.
	 */
	public RestTool setEntityCharset(Charset entityCharset) {
		this.entityCharset = entityCharset;
		return this;
	}

	/**
	 * Force an entity charset.
	 */
	public RestTool setEntityCharset(String entityCharsetName) {
		this.entityCharset = Charset.forName(entityCharsetName);
		return this;
	}

	/**
	 * Ignore SSL certificat verification.<br>Attention : this is incompatible with setProxy().
	 * Do not call if you use a proxy.
	 */
	public RestTool ignoreSSLCertificatVerification() throws Exception {
        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
                (s, sslSession) -> true);

        org.apache.http.client.HttpClient client = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
		
		executor = Executor.newInstance(client);
		
		return this;
	}
}
