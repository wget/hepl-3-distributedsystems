-- MySQL Script generated by MySQL Workbench
-- Tue 24 Jan 2017 06:49:58 PM CET
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema hepl_distributed_systems
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema hepl_distributed_systems
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `hepl_distributed_systems` DEFAULT CHARACTER SET utf8 ;
USE `hepl_distributed_systems` ;

-- -----------------------------------------------------
-- Table `hepl_distributed_systems`.`seller`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hepl_distributed_systems`.`seller` ;

CREATE TABLE IF NOT EXISTS `hepl_distributed_systems`.`seller` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `lastName` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `firstName` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `login` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `hepl_distributed_systems`.`batch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hepl_distributed_systems`.`batch` ;

CREATE TABLE IF NOT EXISTS `hepl_distributed_systems`.`batch` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `purchaseDate` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `sellerId` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `seller_id_fk_idx` (`sellerId` ASC),
  CONSTRAINT `seller_id_fk`
    FOREIGN KEY (`sellerId`)
    REFERENCES `hepl_distributed_systems`.`seller` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `hepl_distributed_systems`.`buyer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hepl_distributed_systems`.`buyer` ;

CREATE TABLE IF NOT EXISTS `hepl_distributed_systems`.`buyer` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `lastName` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `firstName` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `login` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `hepl_distributed_systems`.`item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hepl_distributed_systems`.`item` ;

CREATE TABLE IF NOT EXISTS `hepl_distributed_systems`.`item` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `announcedPrice` FLOAT NULL DEFAULT NULL,
  `sellPrice` FLOAT NULL DEFAULT NULL,
  `sellDate` DATE NULL DEFAULT NULL,
  `buyerId` INT(11) NULL DEFAULT NULL,
  `batchId` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `buyer_id_fk_idx` (`buyerId` ASC),
  INDEX `batch_id_fk_idx` (`batchId` ASC),
  CONSTRAINT `batch_id_fk`
    FOREIGN KEY (`batchId`)
    REFERENCES `hepl_distributed_systems`.`batch` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `buyer_id_fk`
    FOREIGN KEY (`buyerId`)
    REFERENCES `hepl_distributed_systems`.`buyer` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `hepl_distributed_systems`.`log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hepl_distributed_systems`.`log` ;

CREATE TABLE IF NOT EXISTS `hepl_distributed_systems`.`log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `info` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
