package com.opensourceagility.springintegration.alerts;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Alert")
public class Alert {
	
	private long id;
	private int urgency;
	private Date persistDate;
	private String messageCode;
	
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public Date getPersistDate() {
		return persistDate;
	}
	public void setPersistDate(Date persistDate) {
		this.persistDate = persistDate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getUrgency() {
		return urgency;
	}
	public void setUrgency(int urgency) {
		this.urgency = urgency;
	}
	
		

}
