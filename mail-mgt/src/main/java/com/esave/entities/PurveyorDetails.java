package com.esave.entities;

public class PurveyorDetails {

	private String purveyorURL;

	private String userName;

	private String password;

	public PurveyorDetails(String purveyorURL, String userName, String password) {
		super();
		this.purveyorURL = purveyorURL;
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPurveyorURL() {
		return purveyorURL;
	}

	public void setPurveyorURL(String purveyorURL) {
		this.purveyorURL = purveyorURL;
	}

	@Override
	public String toString() {
		return "PurveyorDetails [purveyorURL=" + purveyorURL + ", userName=" + userName + ", password=" + password
				+ "]";
	}

}
