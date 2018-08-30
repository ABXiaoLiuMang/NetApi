package com.cn.div.net;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public abstract class WebUtils {
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";


	private WebUtils() {

	}

	/**
	 * 执行HTTP POST请求
	 * 
	 *            TODO
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 *            TODO
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 * @param responseError
	 *            TODO
	 * @return 响应字符
	 * @throws IOException
	 */
	private static String doPost(String url,
			Map<String, String> params, String charset, boolean responseError) throws IOException {
//		String ctype = "application/json;charset=" + charset;
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset);

		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		NetLog.d("Net","me post content="+content.length);
		return doPost(url, ctype, content,responseError);
	}

	/**
	 * 执行HTTP POST请求
	 * 
	 *            TODO
	 * @param url
	 *            请求地址
	 * @param ctype
	 *            请求类型
	 * @param content
	 *            请求字节数组
	 *            请求header
	 * @param responseError
	 *            TODO
	 * @return 响应字符
	 * @throws IOException
	 */
	private static String doPost(String url, String ctype,
			byte[] content, boolean responseError) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				conn = getConnection(new URL(url), METHOD_POST, ctype);
			} catch (IOException e) {
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn, responseError);
			} catch (IOException e) {
				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}
	
	


	/**
	 * 执行带文件上传的HTTP POST请求
	 * 
	 *            TODO
	 * @param url
	 *            请求地址
	 * @param fileParams
	 *            文件请求参数
	 *            文本请求参数
	 * 
	 * @return 响应字符
	 * @throws IOException
	 */
	public static String doPost(String url,
			Map<String, String> params, Map<String, FileItem> fileParams) throws IOException {
		if (fileParams == null || fileParams.isEmpty()) {
			return doPost(url, params, DEFAULT_CHARSET, false);
		} else {
			return doPost(url, params, fileParams, DEFAULT_CHARSET);
		}
	}

	/**
	 * 执行带文件上传的HTTP POST请求
	 * 
	 *            TODO
	 * @param url
	 *            请求地址
	 * @param fileParams
	 *            文件请求参数
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 *            文本请求参数
	 * 
	 * @return 响应字符
	 * @throws IOException
	 */
	private static String doPost(String url,
			Map<String, String> params, Map<String, FileItem> fileParams,
			String charset)
			throws IOException {
		String boundary = System.currentTimeMillis() + ""; // 随机分隔�?
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				String ctype = "multipart/form-data;charset=" + charset
						+ ";boundary=" + boundary;
				conn = getConnection(new URL(url), METHOD_POST, ctype);

			} catch (IOException e) {

				throw e;
			}

			try {
				out = conn.getOutputStream();

				byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n")
						.getBytes(charset);

				// 组装文本请求参数
				Set<Entry<String, String>> textEntrySet = params.entrySet();
				for (Entry<String, String> textEntry : textEntrySet) {
					byte[] textBytes = getTextEntry(textEntry.getKey(),
							textEntry.getValue(), charset);
					out.write(entryBoundaryBytes);
					out.write(textBytes);
				}
				// 组装文件请求参数
				Set<Entry<String, FileItem>> fileEntrySet = fileParams
						.entrySet();
				for (Entry<String, FileItem> fileEntry : fileEntrySet) {
					FileItem fileItem = fileEntry.getValue();
					byte[] fileBytes = getFileEntry(fileEntry.getKey(),
							fileItem.getFileName(), fileItem.getMimeType(),
							charset);
					out.write(entryBoundaryBytes);
					out.write(fileBytes);
					out.write(fileItem.getContent());
				}
				// 添加请求结束标志
				byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n")
						.getBytes(charset);
				out.write(endBoundaryBytes);
				rsp = getResponseAsString(conn, false);
			} catch (IOException e) {

				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static byte[] getTextEntry(String fieldName, String fieldValue,
			String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
		entry.append(fieldValue);
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName,
			String mimeType, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\";filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type:");
		entry.append(mimeType);
		entry.append("\r\n\r\n");
		return entry.toString().getBytes(charset);
	}

	/**
	 * 执行HTTP GET请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * 
	 * @return 响应字符
	 * @throws IOException
	 */
	public static String doGet(String url,
			Map<String, String> params) throws IOException {
		return doGet(url, params, DEFAULT_CHARSET);
	}

	/**
	 * 执行HTTP GET请求
	 * 
	 *            TODO
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            字符集，如UTF-8, GBK, GB2312
	 * 
	 * @return 响应字符
	 * @throws IOException
	 */
	private static String doGet(String url,
			Map<String, String> params, String charset) throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;

		try {
			String ctype = "application/x-www-form-urlencoded;charset="
					+ charset;
			String query = buildQuery(params, charset);
			try {
				conn = getConnection(buildGetUrl(url, query),
						METHOD_GET, ctype); 
 
			} catch (IOException e) {

				throw e;
			}

			try {
				rsp = getResponseAsString(conn, false);
			} catch (IOException e) {
//				ExceptionUtil.handle(e);
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpURLConnection getConnection(URL url,
			String method, String ctype) throws IOException {

		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0],
						new TrustManager[] { new DefaultTrustManager() },
						new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;// 默认都认证�?�?
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setRequestMethod(method);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(30000);
		conn.setRequestProperty("Content-Type",ctype);
		return conn;
	}
	
	private static URL buildGetUrl(String strUrl, String query)
			throws IOException {
		URL url = new URL(strUrl);
		if (TextUtils.isEmpty(query)) {
			return url;
		}

		if (TextUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}
//		HBLog.d("请求地址：" + strUrl);
		return new URL(strUrl);
	}

	public static String buildQuery(Map<String, String> params, String charset)
			throws UnsupportedEncodingException {
		if (params == null || params.isEmpty()) {
			return null;
		}
		if (TextUtils.isEmpty(charset)) {
			charset = DEFAULT_CHARSET;
		}

		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			// 忽略参数名或参数值为空的参数
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

//				query.append(name).append("=")
//						.append(value);
//				
				query.append(name).append("=")
				.append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

	/**
	 * 获取http响应内容<br>
	 * 当响应状态码不等�?00时，如果要换取响应的内容responseError参数请输入true。否则会抛出IOException
	 * 
	 * @param conn
	 * @param responseError
	 * @return
	 * @throws IOException
	 */
	protected static String getResponseAsString(HttpURLConnection conn,
			boolean responseError) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		String header = conn.getHeaderField("Content-Encoding");
		boolean isGzip = false;
		if (header != null && header.toLowerCase().contains("gzip")) {
			isGzip = true;
		}
		InputStream es = conn.getErrorStream();
		if (es == null) {
			InputStream input = conn.getInputStream();
			if (isGzip) {
				input = new GZIPInputStream(input);
			}
			return getStreamAsString(input, charset);
		} else {
			if (isGzip) {
				es = new GZIPInputStream(es);
			}
			String msg = getStreamAsString(es, charset);
			if (TextUtils.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":"
						+ conn.getResponseMessage());
			} else if (responseError) {
				return msg;
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset)
			throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!TextUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!TextUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

//	/**
//	 * 使用指定的字符集反编码请求参数
//	 * 
//	 * @param value
//	 *            参数
//	 * @param charset
//	 *            字符
//	 * @return 反编码后的参数
//	 */
//	private static String decode(String value, String charset) {
//		String result = null;
//		if (!TextUtils.isEmpty(value)) {
//			try {
//				result = URLDecoder.decode(value, charset);
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		return result;
//	}

	/**
	 * 使用指定的字符集编码请求参数值
	 * 
	 *            参数
	 *            字符
	 * @return 编码后的参
	 */
//	private static String encode(String value, String charset) {
//		String result = null;
//		if (!TextUtils.isEmpty(value)) {
//			try {
//				result = URLEncoder.encode(value, charset);
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		return result;
//	}

	private static class DefaultTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	}

}
