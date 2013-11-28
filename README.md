alerts-demo
===========

Demo using Spring-Integration and Spring-Batch to poll an Alert table for new alerts and to process them according to an assigned urgency rating.

Alerts with urgency <=50 are written a CSV file whos name is stamped with the hour corresponding to the persist_date.
These files are subsequently uploaded to Amazon S3 once it has been determined all Alerts for that hour have been processed.

Alerts with urgency > 50 are sent to an ActiveMQ queue

Setup
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

insert into Alert (message_code,urgency) values ('someLowUrgencyMessageCode',40);
insert into Alert (message_code,urgency) values ('someHighUrgencyMessageCode',70);

