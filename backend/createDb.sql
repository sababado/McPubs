CREATE SCHEMA `MCPUBS` ;

  CREATE TABLE `MCPUBS`.`Pub` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `fullCode` varchar(70) NOT NULL COMMENT '4200.43 or 3-12',
    `rootCode` varchar(10) NOT NULL COMMENT '4200 or 3-12',
    `code` int(11) DEFAULT NULL COMMENT '43 or null',
    `version` varchar(5) DEFAULT NULL COMMENT 'B or null',
    `isActive` tinyint(1) DEFAULT '1' COMMENT 'true or false',
    `title` text COMMENT 'MCWP 3-12',
    `readableTitle` text COMMENT 'Marine Corps Tank Employment',
    `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last time this pub was updated.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `fullCode_UNIQUE` (`fullCode` ASC)
  );