package com.bi.job;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import com.bi.util.PropertiesLoader;
import com.bi.util.SslUtils;


public class MonitorJob implements Job {
	private static final int HTTP_REQUEST_TIMEOUT = 30000;
	private HttpURLConnection conn;
		
	@Value("${lineBotUrl}")
	private static String lineBotUrl;

	@Value("${webUrl}")
	private static String webUrl;

    static {
        try {
        	loadProperties();
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		checkIcsV2Web();
	}

	private static void loadProperties() {
		try {
			Properties systemProp = PropertiesLoader.loadWithProfile("/system.properties");
			webUrl = systemProp.getProperty("webUrl");
			lineBotUrl = systemProp.getProperty("lineBotUrl");
			System.out.println("=======>webUrl:"+webUrl);
			System.out.println("=======>lineBotUrl:"+lineBotUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkIcsV2Web() {
		try {
			URL url;
			url = new URL(webUrl);
			if ("https".equalsIgnoreCase(url.getProtocol())) {
				SslUtils.ignoreSsl();
			}
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.setReadTimeout(HTTP_REQUEST_TIMEOUT);
			conn.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
			int statusCode = conn.getResponseCode();
			switch (statusCode) {
			case java.net.HttpURLConnection.HTTP_BAD_REQUEST:// 400
	                sendLineBotMsg("ICS_V2網站異常:拒絕連線!");
	                break;
			case java.net.HttpURLConnection.HTTP_GATEWAY_TIMEOUT:// 504
				sendLineBotMsg("ICS_V2網站異常:連線網址逾時!");
				break;
			case java.net.HttpURLConnection.HTTP_FORBIDDEN:// 403
				sendLineBotMsg("ICS_V2網站異常:連線網址禁止!");
				break;
			case java.net.HttpURLConnection.HTTP_INTERNAL_ERROR:// 500
				sendLineBotMsg("ICS_V2網站異常:連線網址錯誤或不存在!");
				break;
			case java.net.HttpURLConnection.HTTP_NOT_FOUND:// 404
				sendLineBotMsg("ICS_V2網站異常:連線網址不存在!");
				break;
			case java.net.HttpURLConnection.HTTP_OK:
				System.out.println("IcsV2 OK:" + Calendar.getInstance().getTime());
				break;
			default:
				sendLineBotMsg("ICS_V2網站異常:HTTP狀態碼:" + statusCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
			sendLineBotMsg("ICS_V2網站異常");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	public void sendLineBotMsg(String message) {
		try {
			String baseUrl=lineBotUrl;
		    String encodedQuery = URLEncoder.encode(message, "UTF-8");
		    String urlString = baseUrl + "?message=" + encodedQuery;
			URL obj = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("GET Response Code : " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// print result
				System.out.println(response.toString());
			} else {
				System.out.println("GET request did not work.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
