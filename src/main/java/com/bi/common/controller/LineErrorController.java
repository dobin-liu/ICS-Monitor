package com.bi.common.controller;

import java.net.HttpURLConnection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bi.line.model.Event;
import com.bi.line.model.Message;
import com.bi.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = {"public line"})
@RequestMapping("public")
@RestController
public class LineErrorController {
	private static final Logger logger = LoggerFactory.getLogger(LineErrorController.class);
	private static final String MODEL_URL = "/common/error";
	private static final String ACCESS_TOKEN = "TimE1YF7Kcx0uZU8+DIG0HDPOIQ1URLj2IdEVy7BiZ282LhdbxDwfgSRzu8y+N7EIHJPqPeidaJZVyeQU3qREtPsYOWCSmYQjCNTa2nHRyU/Q13qsIjQgbT9RzzyXCFdgF/5PjacGDxq3IfZCmlwXgdB04t89/1O/w1cDnyilFU=";
	private static final String LINE_API_URL = "https://api.line.me/v2/bot/message/push";
	private static String groups[] = { "U25c070d19c6c0beb50c5d37d5ef2e260", "Ubfebdb547927f5fd36f8f5a5010c2dcd", "", "",
			"", "" };// piko,eric
	


	@GetMapping(MODEL_URL + "/monitorMessage")
	@ApiOperation(value = "monitorMessage")
	public int monitorMessage(@ApiParam("傳送訊息") @RequestParam(defaultValue = "") String message) {
		for (String userId : groups) {
			if (StrUtil.isNotEmpty(userId)) {
				pushMessage(userId, message);
			}

		}
		return HttpURLConnection.HTTP_OK;
	}

	private void pushMessage(String userId, String message) {
		ObjectMapper mapper = new ObjectMapper();
		Event event = new Event();
		event.setTo(userId);
		Message replayMessage = new Message();
		replayMessage.setText(message);
		replayMessage.setType("text");
		event.getMessages().add(replayMessage);

		// Create HTTP client
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(LINE_API_URL);
			post.setHeader("Authorization", "Bearer " + ACCESS_TOKEN);
			post.setHeader("Content-Type", "application/json");

			String jsonPayload = mapper.writeValueAsString(event);

			// Set JSON payload
			StringEntity entity = new StringEntity(jsonPayload, "UTF-8");
			post.setEntity(entity);

			// Execute the request
			HttpResponse response = httpClient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity);

			System.out.println("Response: " + responseString);
		} catch (Exception e) {
			e.printStackTrace();
			String stacktrace = ExceptionUtils.getStackTrace(e);
			logger.error("發送Line Error:" + stacktrace);
		}
	}
}
