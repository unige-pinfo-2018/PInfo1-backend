CREATE DATABASE UNIHUB_DB;
USE UNIHUB_DB;

DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS HASHES;

CREATE TABLE USERS (
  ID INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USERNAME varchar(35),
  EMAIL varchar(255) NOT NULL,
  ROLE varchar(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE HASHES (
  ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USER_ID INT(11),
  HASH varchar(1000) NOT NULL,
  SALT varchar(100) NOT NULL,
  FOREIGN KEY (USER_ID) REFERENCES USERS(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;