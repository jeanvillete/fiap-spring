CREATE TABLE STUDENT (
  id integer NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  subscription varchar(10) NOT NULL,
  code varchar(10) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE CARD_LIMIT (
  id integer NOT NULL AUTO_INCREMENT,
  datetime_limit timestamp NOT NULL,
  value decimal(19,2) DEFAULT NULL,
  student_id integer NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES STUDENT(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE CARD_TRANSACTION (
  id integer NOT NULL AUTO_INCREMENT,
  datetime_transaction timestamp NOT NULL,
  value decimal(19,2) DEFAULT NULL,
  description varchar(150),
  card_limit_id integer NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (card_limit_id) REFERENCES CARD_LIMIT(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;