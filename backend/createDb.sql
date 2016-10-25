CREATE SCHEMA `MCPUBS` ;

  CREATE TABLE `Pub` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `fullCode` varchar(70) NOT NULL COMMENT '4200.43 or 3-12',
    `rootCode` varchar(10) NOT NULL COMMENT '4200 or 3-12',
    `code` int(11) DEFAULT NULL COMMENT '43 or null',
    `version` varchar(5) DEFAULT NULL COMMENT 'B or null',
    `isActive` tinyint(1) DEFAULT '1' COMMENT 'true or false',
    `pubType` INT(4) UNSIGNED NOT NULL COMMENT 'Int type of the pub, ex MCO is 2005',
    `title` text COMMENT 'MCWP 3-12',
    `readableTitle` text COMMENT 'Marine Corps Tank Employment',
    `lastUpdated` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last time this pub was updated.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `fullCode_UNIQUE` (`fullCode` ASC)
  );

  CREATE TABLE `Device` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `deviceToken` varchar(255) NOT NULL COMMENT 'Token used to identify the device for notifications.',
    `lastNotificationFail` timestamp NOT NULL DEFAULT 0 COMMENT 'Timestamp of the last time this token attempted to be used but it failed. This is indicative that the device may have uninstalled the app.',
    `keepAlive` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'This is the last time the device used this app.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `deviceToken_UNIQUE` (`deviceToken`)
  );


  CREATE TABLE `PubDevices` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `deviceId` int(11) NOT NULL COMMENT 'ID of the device.',
    `pubId` int(11) NOT NULL COMMENT 'ID of the pub',
    PRIMARY KEY (`id`,`deviceId`),
    UNIQUE KEY `deviceId_pubId_UNIQUE` (`deviceId`,`pubId`),
    KEY `device_idx` (`deviceId`),
    KEY `pub_idx` (`pubId`),
    CONSTRAINT `fk_device` FOREIGN KEY (`deviceId`) REFERENCES `Device` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_pub` FOREIGN KEY (`pubId`) REFERENCES `Pub` (`id`) ON UPDATE NO ACTION
  );