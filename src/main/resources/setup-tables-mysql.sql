CREATE TABLE Alert (id MEDIUMINT NOT NULL AUTO_INCREMENT,message_code VARCHAR(30) NOT NULL, urgency int not null,processed bool default false, persist_date timestamp DEFAULT CURRENT_TIMESTAMP,PRIMARY KEY (id)) ENGINE=InnoDB;

