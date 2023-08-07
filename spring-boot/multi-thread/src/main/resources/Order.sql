-- auto Generated on 2023-07-05
-- DROP TABLE IF EXISTS thread_order;
CREATE TABLE thread_order(
	id VARCHAR (50) NOT NULL COMMENT 'id',
	order_id VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'orderId',
	address VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'address',
	create_time DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT 'createTime',
	product_id VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'productId',
	`name` VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'name',
	subtitle VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'subtitle',
	brand_name VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'brandName',
	price DECIMAL (13,4) NOT NULL DEFAULT -1 COMMENT 'price',
	`count` INT (11) NOT NULL DEFAULT -1 COMMENT 'count',
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'thread_order';
