package com.opensourceagility.springintegration.alerts.nonurgent;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Provides methods to obtain file timestamp headers for a File, namely 'fromTimestamp' and 'toTimestamp'
 * 
 * @author Michael Lavelle
 */
public class FileTimeRangeHeaderEnricher {
	
	public void setFilenameTimeStampFormat(String filenameTimeStampFormat) {
		this.filenameTimeStampFormat = filenameTimeStampFormat;
	}

	public void setFileTimeRangeDurationSeconds(int fileTimeRangeDurationSeconds) {
		this.fileTimeRangeDurationSeconds = fileTimeRangeDurationSeconds;
	}


	private int fileTimeRangeDurationSeconds;
	
	private String filenameTimeStampFormat;
	
	public Date getFromTimestamp(File file) throws ParseException
	{
		DateFormat hourFormat = new SimpleDateFormat(filenameTimeStampFormat);
		return hourFormat.parse(file.getName());
	}
	
	public Date getToTimestamp(File file) throws ParseException
	{
		DateFormat hourFormat = new SimpleDateFormat(filenameTimeStampFormat);

		Date date = hourFormat.parse(file.getName());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, fileTimeRangeDurationSeconds);
		return cal.getTime();
	}
	
}
