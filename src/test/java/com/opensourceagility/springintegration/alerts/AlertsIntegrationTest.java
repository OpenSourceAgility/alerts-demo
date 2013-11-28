/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensourceagility.springintegration.alerts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;

import com.opensourceagility.springintegration.alerts.nonurgent.MockAmazonS3FileUploader;


public class AlertsIntegrationTest {

	private static final Logger LOGGER = Logger.getLogger(AlertsIntegrationTest.class);

	
	
	private Map<Integer,String> expectedXmlTimeStringsByAlertId = new HashMap<Integer,String>();

	private Map<Integer,String> expectedCsvTimeStringsByAlertId = new HashMap<Integer,String>();

	private SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

	private SimpleDateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private SimpleDateFormat csvDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	private String thisMinuteString;
	private Date thisMinuteDate;
	private String nextMinuteString;
	
	@Before
	public void setup() throws ParseException
	{
		SetupUtils.prepare();
		
		Date testRunDate = new Date();
		this.thisMinuteString = fileNameDateFormat.format(testRunDate);
		thisMinuteDate = fileNameDateFormat.parse(thisMinuteString);
		Calendar nextMinuteCalendar = Calendar.getInstance();
		nextMinuteCalendar.setTime(thisMinuteDate);
		nextMinuteCalendar.add(Calendar.MINUTE, 1);
		this.nextMinuteString = fileNameDateFormat.format(nextMinuteCalendar.getTime());
	}
	
	private final static String[] configFilesChannelAdapterDemo = {
		
		"/META-INF/spring/integration/common.xml",
		"/META-INF/spring/integration/urgent-alerts.xml",
		"/META-INF/spring/integration/non-urgent-alerts.xml",
		"/test.xml"
	};

	@SuppressWarnings("unchecked")
	@Test
	public void testChannelAdapterDemo() throws InterruptedException, ParseException {

		System.setProperty("spring.profiles.active", "testCase");
		

		final GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext(configFilesChannelAdapterDemo);

		final MessageChannel createAlertRequestChannel = applicationContext.getBean("createAlertRequestChannel", MessageChannel.class);

		createAlertTestData(createAlertRequestChannel,thisMinuteDate);
		
		final QueueChannel queueChannel = applicationContext.getBean("queueChannel", QueueChannel.class);


		for (int counter = 1; counter <= 50; counter++)
		{
			int id = counter * 2;
			Message<String> reply = (Message<String>) queueChannel.receive(10000);
			Assert.assertNotNull(reply);
			String out = reply.getPayload();
			Assert.assertEquals(getExpectedXml(id), out);
			LOGGER.debug("Received expected Alert XML from queue:" + out);
		}
		LOGGER.debug("Asserted that urgent alerts are those 50 alerts with even ids, ranging from 2 up to 100... and that these are received from the queue");

		Message<String> reply = (Message<String>) queueChannel.receive(10000);
		Assert.assertNull(reply);
		
		LOGGER.debug("Sleeping for 2 minutes until files are expected to have been (mock) uploaded...");
		Thread.sleep(120 * 1000);
		
		final MockAmazonS3FileUploader mockFileProcessor = applicationContext.getBean("amazonS3FileUploader", MockAmazonS3FileUploader.class);
		Assert.assertEquals(2, mockFileProcessor.getLinesByFileName().size());
		LOGGER.debug("Asserted successfully that 2 csv files were expected");

		List<String> minute1Lines = mockFileProcessor.getLinesByFileName().get(thisMinuteString);
		List<String> minute2Lines = mockFileProcessor.getLinesByFileName().get(nextMinuteString);
		
		// Assert we have the correct 30 entries in the csv file for the 1st minute
		Assert.assertEquals(30, minute1Lines.size());
		LOGGER.debug("Asserted successfully that 30 alerts found in file:" + thisMinuteString);

		
		// Assert we have 20 correct entries in the csv file for the 2nd minute
		Assert.assertEquals(20, minute2Lines.size());
		LOGGER.debug("Asserted successfully that 20 alerts found in file:" + nextMinuteString);

		
		// Check that the 1st minute's csv lines are as expected
		for (int counter = 1; counter <= 30; counter++)
		{
			int id = counter * 2 - 1;
			String line = minute1Lines.get(counter - 1);
			Assert.assertEquals(getExpectedCsvLine(id), line);
			LOGGER.debug("Found expected csv line in file:" + thisMinuteString + ":" + line);

			
		}
		
		// Check that the 2nd minute's csv lines are as expected
		for (int counter = 31; counter <= 50; counter++)
		{
			int id = counter * 2 - 1;
			String line = minute2Lines.get(counter - 31);
			Assert.assertEquals(getExpectedCsvLine(id), line);
			LOGGER.debug("Found expected csv line in file:" + nextMinuteString + " : " + line);

		}
		
		LOGGER.debug("Asserted that non-urgent alerts are those 50 alerts with odd ids, ranging from 1 up to 99... and that these are written to csv files and uploaded");

	
		LOGGER.debug("Completed test successfully, closing application context");

		applicationContext.close();
	}
	
	
	
	/**
	 * Creates 100 alerts with ids 1..100 in order...incrementing the persist time by 1 second for each
	 * Each Alert with an odd id is given an urgency of (id + 1)/2... so urgency of 1 through 50
	 * Each Alert with an even id is given an urgency of (50 + id/2)..so urgency of 51 through 100
	 * 
	 * @param createAlertChannel The alert channel responsible for persisting alerts
	 * @param thisMinute Date representing the start of the current minute
	 * @throws ParseException
	 */
	private void createAlertTestData(MessageChannel createAlertChannel,Date thisMinute) throws ParseException
	{
		LOGGER.debug("Creating 100 Alerts in database for this test");

		Calendar insertTime = Calendar.getInstance();
		insertTime.setTime(thisMinute);

		for (int id = 1; id <= 100; id++)
		{
			int urgency = id % 2 == 1 ? (id + 1)/2 : (50 + id/2);
		
			Alert alert = createTestAlert(id,urgency,insertTime.getTime());	
			createAlertChannel.send(MessageBuilder.withPayload(alert).build());
			String csvDate = csvDateFormat.format(alert.getPersistDate());
			expectedXmlTimeStringsByAlertId.put(id, xmlDateFormat.format(alert.getPersistDate()));
			expectedCsvTimeStringsByAlertId.put(id,csvDate );

			insertTime.add(Calendar.SECOND, 1);
		}
	}
	

	private Alert createTestAlert(int id,int urgency,Date insertTime)
	{
		Alert alert = new Alert();
		alert.setId(id);
		alert.setMessageCode("MSG-" + id);
		alert.setPersistDate(insertTime);
		alert.setUrgency(urgency);
		return alert;
	}

	/**
	 * Non-urgent alerts are those with odd ids (1,3,5..99).  The urgency of these 50 alerts are 1 through 50 respectively. Csv lines are generated for these non-urgent alerts
	 * 
	 * @param alertId
	 * @return
	 */
	private String getExpectedCsvLine(int alertId)
	{
		int urgency = (alertId + 1)/2;
		String expectedTimeStamp = expectedCsvTimeStringsByAlertId.get(alertId);
		return alertId + "," + expectedTimeStamp + "," + urgency + "," + "MSG-" + alertId;
		
	}
	
	/**
	 * Urgent alerts are those with even ids (2,4,6..100).  The urgency of these 50 alerts are 51 through 100 respectively
	 * 
	 * @param alertId
	 * @return
	 */
	private String getExpectedXml(int alertId)
	{
		int urgency = 50 + alertId/2;
		String expectedTimeStamp = expectedXmlTimeStringsByAlertId.get(alertId);
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Alert><id>" + alertId + "</id><messageCode>MSG-" +  alertId + "</messageCode><persistDate>" + expectedTimeStamp + "</persistDate><urgency>" + urgency + "</urgency></Alert>";

	}
	

}
