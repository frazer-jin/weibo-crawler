DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `user_detail`;
CREATE TABLE `user` (
	id INT auto_increment PRIMARY KEY
	, uid BIGINT NOT NULL
	, nick_name VARCHAR(32)
	, sex CHAR(1)
	, flag BOOLEAN DEFAULT false
	, UNIQUE KEY (`uid`)
	);

CREATE TABLE user_detail (
	id INT auto_increment PRIMARY KEY
	, uid BIGINT NOT NULL
	, retried INT NOT NULL DEFAULT 0
	, nick_name VARCHAR(32)
	, sex CHAR(1)
	, birth DATE
	, create_date DATE
	, address VARCHAR(128)
	, college VARCHAR(128)
	, memo VARCHAR(128)
	, flag BOOLEAN DEFAULT false
	, UNIQUE KEY (`uid`)
	);
	
INSERT INTO `user` (uid) VALUES ('1005056032746934');
INSERT INTO `user_detail` (uid) VALUES ('1005056032746934');