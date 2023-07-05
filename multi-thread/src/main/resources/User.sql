-- auto Generated on 2023-07-05
-- DROP TABLE IF EXISTS thread_user;
CREATE TABLE thread_user(
	id BIGINT (15) NOT NULL AUTO_INCREMENT COMMENT 'id',
	username VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'username',
	`password` VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'password',
	nickname VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'nickname',
	birthday DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT 'birthday',
	phone VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'phone',
	height DOUBLE (8,2) NOT NULL DEFAULT -1.0 COMMENT 'height',
	gender INT (11) NOT NULL DEFAULT -1 COMMENT 'gender',
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'thread_user';
