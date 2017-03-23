package com.esave.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class Utils {

	private static final String NOTIFICATION_API_URL = "http://user.diningedge.com/confirm-order?orderId=%s&purveyorId=%s&success=%d";

	private static final String USER_AGENT = "Mozilla/5.0";
	
	private static final Logger logger = Logger.getLogger(Utils.class);

	public void sendNotification(String orderId, String purveyorId, NotificationEvent event) throws IOException {
		String notficationUrl = NOTIFICATION_API_URL;

		if (NotificationEvent.SUCCESS.equals(event)) {
			notficationUrl = String.format(notficationUrl, orderId, purveyorId, 1);
		} else {
			notficationUrl = String.format(notficationUrl, orderId, purveyorId, 0);
		}
		logger.info("Notification URL is :" + notficationUrl);
		URL url = null;
		try {
			url = new URL(notficationUrl);
		} catch (MalformedURLException e) {
			logger.info("Incorrect Format of notification url");
			e.printStackTrace();
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = connection.getResponseCode();
		logger.info("Notification Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			logger.info(response.toString());
		} else {
			logger.info("Notification Response Code is other than 200_OK so failed to send notification");
		}
	}

}
