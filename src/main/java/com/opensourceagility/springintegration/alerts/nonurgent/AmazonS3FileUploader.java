package com.opensourceagility.springintegration.alerts.nonurgent;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Uploads a File to AmazonS3 bucket
 * 
 * @author Michael Lavelle
 */
public class AmazonS3FileUploader {

	private static final Logger LOGGER = Logger.getLogger(AmazonS3FileUploader.class);
	
	@Autowired
	private AmazonS3 s3Client;
	
	private String bucketName;
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	@ServiceActivator
	public void processFile(File file)
	{
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(), file);
		s3Client.putObject(putObjectRequest);
		LOGGER.info("Uploaded file:" + file.getPath() + " to Amazon S3 Bucket:" + bucketName); 
		file.delete();
	}
	
}
