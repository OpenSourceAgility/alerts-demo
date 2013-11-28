alerts-demo
===========

Demo using Spring-Integration and Spring-Batch to poll an Alert table for new alerts and to process them according to an assigned urgency rating.

Alerts with urgency <=50 are written a CSV file with a filename stamped with the hour corresponding to the Alert's persist_date.

These files are subsequently uploaded to Amazon S3 once it has been determined all Alerts for that hour have been processed.

Alerts with urgency > 50 are sent to an ActiveMQ queue

MySQL Setup
-----

```

CREATE TABLE Alert 
(id MEDIUMINT NOT NULL AUTO_INCREMENT,
message_code VARCHAR(30) NOT NULL, 
urgency INT NOT NULL,
processed BOOL DEFAULT false,
persist_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id)) 
ENGINE=InnoDB;

```

Example data
------------

```
insert into Alert (message_code,urgency) values ('someLowUrgencyMessageCode',40);
insert into Alert (message_code,urgency) values ('someHighUrgencyMessageCode',70);
```

Properties files
----------------

To run the main application,po pulate src/main/resources/envionment.properties file with values for
```
amazon.s3.bucketName=
amazon.aws.accesskey=
amazon.aws.secretkey=
database.url=
```

( These are not required to run the integration test )

Running the application
-----------------------

Run com.opensourceagility.springintegration.alerts.Main as a Java application


Running the integration test
----------------------------

mvn test

The AlertsIntegrationTest creates 50 urgent alerts and 50 non-urgent alerts and checks that they are processed as expected. 
This test is configured to create CSV files timestamped by minute rather than hour to enable the test to run in a timely manner.

The test sleeps for 2 minutes before asserting that the expected CSV files have been created

The AmazonS3FileUploader is mocked out and an H2 Embedded database is used instead of MySQL, and so the environment properties for amazon or the database are not required for this test to run.

Visualising the integration graph
---------------------------------

Import the project into STS, and view the "integration-graph" tab when viewing urgent-alerts.xml or non-urgent-alerts.xml


