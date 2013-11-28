CREATE TABLE Alert (
     id MEDIUMINT NOT NULL AUTO_INCREMENT,
     message_code VARCHAR(30) NOT NULL,	
    urgency int not null,
    processed bool,
    persist_date timestamp,
     PRIMARY KEY (id)
);





