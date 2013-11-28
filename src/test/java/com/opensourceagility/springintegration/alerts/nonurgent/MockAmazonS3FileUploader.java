package com.opensourceagility.springintegration.alerts.nonurgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Mock AmazonS3FileUploader
 * 
 * @author Michael Lavelle
 */
public class MockAmazonS3FileUploader {

	private MultiValueMap<String,String> linesByFileName = new LinkedMultiValueMap<String,String>();
	
	private static final Logger LOGGER = Logger.getLogger(MockAmazonS3FileUploader.class);

	
	public MultiValueMap<String, String> getLinesByFileName() {
		return linesByFileName;
	}


	@ServiceActivator
	public void processFile(File file) throws IOException
	{
		LOGGER.debug("Mocking upload of file to Amazon S3 for file:" + file.getPath());
		FileReader fileReader = new FileReader(file);
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(fileReader);
			String line = br.readLine();
			while (line != null)
			{
				linesByFileName.add(file.getName(), line);
				line = br.readLine();
			}
			LOGGER.debug("Deleting file: "  + file.getPath());
			file.delete();
		}
		finally
		{
			try
			{
				br.close();
			}
			finally
			{
				fileReader.close();
			}
		}
		
	}
	
}
