package com.bi.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bi.common.util.MecUtil;
import com.bi.spring.rest.exception.BIRestException;

import lombok.extern.log4j.Log4j2;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Log4j2
public class HttpConnectUtil {

	private OkHttpClient client;

	public HttpConnectUtil() {
		client = getOkHttpClient(false, null);
	}

	public HttpConnectUtil(boolean keepCookie) {
		client = getOkHttpClient(keepCookie, null);
	}

	public HttpConnectUtil(boolean keepCookie, KeyManager[] kms) {
		client = getOkHttpClient(keepCookie, kms);
	}

	/**
	 * 送出自定義的GET要求，使用okHttp
	 * 
	 * @param urlStr
	 *            網址
	 * 
	 * @return text http回應內容
	 * @throws IOException
	 */
	public String sendGETokHttp(String urlStr) throws IOException {
	    //// final String toStr = MecUtil.genString(urlStr);
	    
		// log.info("sendGETokHttp: " + urlStr);
	    
	    //// final long st = System.currentTimeMillis();
        //// log.info("call server start, =>" + toStr);

		String text = null;

		Request request = new Request.Builder().url(urlStr).build();

		Response response = client.newCall(request).execute();

		//// log.info("call server end, =>" + toStr +  ", cost ms : " + (System.currentTimeMillis() - st));   
		
		if (response.isSuccessful()) {
			text = response.body().string();
			log.info(text);
		} else {
			log.error("GET request not worked, code: " + response.code());
			//// log.error("call server error, =>" + toStr + ", responseCode: " + response.code());
		}

		return text;
	}

	/**
	 * 送出自定義的POST要求，使用okHttp
	 * 
	 * @param urlStr
	 *            網址
	 * @param params
	 *            參數
	 * @return text http回應內容
	 * @throws IOException
	 */
	public String sendPOSTokHttp(String urlStr, String params) throws IOException {
		// log.info("sendPOSTokHttp: " + urlStr);
		// log.info("sendPOSTokHttp params: " + params);
	    //// final String toStr = MecUtil.genString(urlStr);

	    //// final long st = System.currentTimeMillis();
	    
	    //// log.info("call server start, =>" + toStr);
	    
		String text = null;

		// OkHttpClient client = getOkHttpClient(kms);

		MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/json; charset=UTF-8");
		RequestBody body = RequestBody.create(params, MEDIA_TYPE_TEXT);

		Request request = new Request.Builder().url(urlStr).post(body).build();

		Response response = client.newCall(request).execute();

		//// log.info("call server end, =>" + toStr +  ", cost ms : " + (System.currentTimeMillis() - st));      
		
		if (response.isSuccessful()) {
			text = response.body().string();
			// logger.debug(text);
		} else {
			log.error("POST request not worked, code: " + response.code());
			//// log.error("call server error, =>" + toStr + ", responseCode: " + response.code());
		}

		return text;
	}

	public static OkHttpClient getOkHttpClient(boolean keepCookie, KeyManager[] kms) {
		int connectTimeout = 30;
		int writeTimeout = 30;
		int readTimeout = 150;
		return getOkHttpClient(connectTimeout, writeTimeout, readTimeout, keepCookie, kms);
	}

    public static OkHttpClient getOkHttpClient(int connectTimeout, int writeTimeout, int readTimeout,
            boolean keepCookie,
            KeyManager[] kms) {
		try {
			// System.setProperty("jsse.enableSNIExtension", "false");

			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[] {};
				}
			} };

			// Install the all-trusting trust manager
			// final SSLContext sslContext = SSLContext.getInstance("TLS");
			final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(kms, trustAllCerts, new java.security.SecureRandom());

			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

			builder.hostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return !"NOTOCRSERVER".equals(hostname);
				}
			});

            return builder.connectTimeout(connectTimeout, TimeUnit.SECONDS).writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
						private final HttpUrl keepUrl = HttpUrl.parse("http://KEEP");

						@Override
						public List<Cookie> loadForRequest(HttpUrl url) {
							if (keepCookie) {
								final HttpUrl origurl = HttpUrl.parse(url.scheme() + "://" + url.host());
								List<Cookie> cookies = cookieStore.get(origurl);
								return cookies != null ? cookies : new ArrayList<Cookie>();
							}
							return Collections.emptyList();
						}

						@Override
						public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
							if (keepCookie) {
								final HttpUrl origurl = HttpUrl.parse(url.scheme() + "://" + url.host());
								cookieStore.put(origurl, cookies);
							}
						}
                    })
                    .build();

		} catch (BIRestException | KeyManagementException | NoSuchAlgorithmException e) {
			// log.error("err: ", e);
		    log.error(MecUtil.SafeErrorLog(e));
		}
		return new OkHttpClient();
	}

}
