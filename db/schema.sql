create database if not exists "songo";
use "songo";

CREATE TABLE `songo`.`songs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR  NOT NULL,
  `track_number` TINYINT UNSIGNED,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
CHARACTER SET utf8 COLLATE utf8_general_ci;
