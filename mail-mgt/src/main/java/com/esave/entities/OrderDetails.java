package com.esave.entities;

public class OrderDetails {

	private String purveyorURL;

	private String userName;

	private String password;
	
	private String orderId;
	
	private String purveyorId;
	
	private String deliverydate;

	public OrderDetails( String userName, String password, String orderId, String purveyorId, String deliverydate) {
		super();
//		this.purveyorURL = purveyorURL;
		this.userName = userName;
		this.password = password;
		this.orderId = orderId;
		this.purveyorId = purveyorId;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPurveyorId() {
		return purveyorId;
	}

	
	public String getDeliverydate() {
		return deliverydate;
	}

	public void setDeliverydate(String deliverydate) {
		this.deliverydate = deliverydate;
	}

	@Override
	public String toString() {
		return "PurveyorDetails [purveyorURL=" + purveyorURL + ", userName=" + userName + ", password=" + password
				+ "]";
	}

}
