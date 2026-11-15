package com.sms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwilioVoiceService {
	private static final Pattern CALL_SID = Pattern.compile("\"sid\"\\s*:\\s*\"(CA[0-9a-fA-F]+)\"");

	private final String accountSid;
	private final String authToken;
	private final String fromNumber;
	private final String voiceUrl;

	public TwilioVoiceService() {
		accountSid = System.getenv("TWILIO_ACCOUNT_SID");
		authToken = System.getenv("TWILIO_AUTH_TOKEN");
		fromNumber = System.getenv("TWILIO_FROM_NUMBER");
		voiceUrl = System.getenv("TWILIO_VOICE_URL");
	}

	public boolean isConfigured() {
		return notBlank(accountSid) && notBlank(authToken) && notBlank(fromNumber) && notBlank(voiceUrl);
	}

	public String placeCall(String toNumber) throws IOException {
		if (!isConfigured()) {
			throw new IllegalStateException("Twilio voice is not configured.");
		}
		String endpoint = "https://api.twilio.com/2010-04-01/Accounts/"
				+ URLEncoder.encode(accountSid, "UTF-8") + "/Calls.json";
		String body = "To=" + formValue(toNumber) + "&From=" + formValue(fromNumber)
				+ "&Url=" + formValue(voiceUrl);
		HttpURLConnection connection = (HttpURLConnection) URI.create(endpoint).toURL().openConnection();
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(15000);
		connection.setDoOutput(true);
		String authorization = Base64.getEncoder().encodeToString(
				(accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
		connection.setRequestProperty("Authorization", "Basic " + authorization);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		try (OutputStream output = connection.getOutputStream()) {
			output.write(body.getBytes(StandardCharsets.UTF_8));
		}

		int status = connection.getResponseCode();
		InputStream stream = status >= 200 && status < 300
				? connection.getInputStream() : connection.getErrorStream();
		String response;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			response = content.toString();
		} finally {
			connection.disconnect();
		}
		if (status < 200 || status >= 300) {
			throw new TwilioRejectedException("Twilio rejected the voice request with HTTP " + status + ".");
		}
		Matcher matcher = CALL_SID.matcher(response);
		if (!matcher.find()) {
			throw new IOException("Twilio accepted the request but did not return a call identifier.");
		}
		return matcher.group(1);
	}

	private static String formValue(String value) throws IOException {
		return URLEncoder.encode(value, "UTF-8");
	}

	private static boolean notBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}

	public static class TwilioRejectedException extends IOException {
		private static final long serialVersionUID = 1L;

		public TwilioRejectedException(String message) {
			super(message);
		}
	}
}
