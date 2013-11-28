package com.opensourceagility.springintegration.alerts.nonurgent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.opensourceagility.springintegration.alerts.Alert;

/**
 * Provides a method to obtain filename for an Alert
 * 
 * @author Michael Lavelle
 */
public class AlertFilenameHeaderEnricher {

	public void setFilenameTimeStampFormat(String filenameTimeStampFormat) {
		this.filenameTimeStampFormat = filenameTimeStampFormat;
	}

	private String filenameTimeStampFormat;
	
	public String getFilename(Alert alert)
	{
		DateFormat filenameDateFormat = new SimpleDateFormat(filenameTimeStampFormat);
		return filenameDateFormat.format(alert.getPersistDate());
	}
	
}
