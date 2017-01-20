package com.esave.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import com.esave.common.PurveyorRegister;
import com.esave.entities.PurveyorDetails;

/**
 * Start up class
 * 
 */
public class StartUp {

	/**
	 * Reading properties from config file in resources
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static HashMap<String, PurveyorDetails> readProperties(String purvayorFilePath, String credentialsFilePath)
			throws IOException {
		try {
			InputStream inputStream = StartUp.class.getClassLoader().getResourceAsStream(purvayorFilePath);
			Properties purveyorProperties = new Properties();
			purveyorProperties.load(inputStream);

			inputStream = StartUp.class.getClassLoader().getResourceAsStream(credentialsFilePath);
			Properties credentialProperties = new Properties();
			credentialProperties.load(inputStream);

			for (String purveyorId : purveyorProperties.stringPropertyNames()) {
				String purveyorURL = purveyorProperties.getProperty(purveyorId);
				String[] crendentials = credentialProperties.getProperty(purveyorURL).split("/");
				PurveyorDetails purveyorDetails = new PurveyorDetails(purveyorURL, crendentials[0], crendentials[1]);
				PurveyorRegister.getInstance().put(purveyorId, purveyorDetails);
			}
		} catch (Exception e) {
		}
		return PurveyorRegister.getInstance();

	}

}
