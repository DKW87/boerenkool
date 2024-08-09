-- MySQL Workbench Forward Engineering

-- SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
-- SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
-- SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema zkwantd1
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema zkwantd1
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `zkwantd1` ;
USE `zkwantd1` ;

-- -----------------------------------------------------
-- Table `zkwantd1`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`User` (
                                                 `userId` INT NOT NULL AUTO_INCREMENT,
                                                 `typeOfUser` VARCHAR(10) NOT NULL DEFAULT 'huurder',
                                                 `username` VARCHAR(25) NOT NULL,
                                                 `hashedPassword` VARCHAR(255) NOT NULL,
                                                 `firstName` VARCHAR(45) NOT NULL,
                                                 `infix` VARCHAR(15) NULL DEFAULT NULL,
                                                 `lastName` VARCHAR(150) NOT NULL,
                                                 `coinBalance` BIGINT NOT NULL DEFAULT '0',
                                                 `phoneNumber` VARCHAR(13) NOT NULL,
                                                 `emailaddress` VARCHAR(150) NOT NULL,
                                                 PRIMARY KEY (`userId`),
                                                 UNIQUE INDEX `username_UNIQUE` (`username` ASC),
                                                 UNIQUE INDEX `emailaddress_UNIQUE` (`emailaddress` ASC))
     ENGINE = InnoDB
     AUTO_INCREMENT = 11
     DEFAULT CHARACTER SET = utf8mb4
     COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`BlockedList`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`BlockedList` (
                                                        `userId` INT NOT NULL,
                                                        `blockedUser` INT NOT NULL,
                                                        PRIMARY KEY (`userId`, `blockedUser`),
                                                        INDEX `!VERZINZELF!1_idx` (`blockedUser` ASC),
                                                        INDEX `!VERZINZELF!_idx` (`userId` ASC),
                                                        CONSTRAINT `fk_blockedlist_blockedUser`
                                                            FOREIGN KEY (`blockedUser`)
                                                                REFERENCES `zkwantd1`.`User` (`userId`),
                                                        CONSTRAINT `fk_blockedlist_user`
                                                            FOREIGN KEY (`userId`)
                                                                REFERENCES `zkwantd1`.`User` (`userId`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`ExtraFeature`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`ExtraFeature` (
                                                         `extraFeatureId` INT NOT NULL AUTO_INCREMENT,
                                                         `extraFeatureName` VARCHAR(45) NOT NULL,
                                                         PRIMARY KEY (`extraFeatureId`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 11
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`HouseType`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`HouseType` (
                                                      `houseTypeId` INT NOT NULL AUTO_INCREMENT,
                                                      `houseTypeName` VARCHAR(50) NOT NULL,
                                                      PRIMARY KEY (`houseTypeId`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 5
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`House`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`House` (
                                                  `houseId` INT NOT NULL AUTO_INCREMENT,
                                                  `houseName` VARCHAR(150) NOT NULL,
                                                  `houseTypeId` INT NOT NULL,
                                                  `houseOwnerId` INT NOT NULL,
                                                  `province` VARCHAR(150) NOT NULL,
                                                  `city` VARCHAR(150) NOT NULL,
                                                  `streetAndNumber` VARCHAR(250) NOT NULL,
                                                  `zipcode` VARCHAR(6) NOT NULL,
                                                  `maxGuest` INT NOT NULL,
                                                  `roomCount` INT NOT NULL,
                                                  `pricePPPD` INT NOT NULL,
                                                  `description` VARCHAR(2550) NOT NULL,
                                                  `isNotAvailable` TINYINT NOT NULL DEFAULT '0',
                                                  PRIMARY KEY (`houseId`),
                                                  INDEX `!VERZINZELF!9_idx` (`houseTypeId` ASC),
                                                  INDEX `!VERZINZELF!10_idx` (`houseOwnerId` ASC),
                                                  CONSTRAINT `fk_house_housetype`
                                                      FOREIGN KEY (`houseTypeId`)
                                                          REFERENCES `zkwantd1`.`HouseType` (`houseTypeId`)
                                                          ON DELETE RESTRICT
                                                          ON UPDATE CASCADE,
                                                  CONSTRAINT `fk_house_user`
                                                      FOREIGN KEY (`houseOwnerId`)
                                                          REFERENCES `zkwantd1`.`User` (`userId`)
                                                          ON DELETE CASCADE
                                                          ON UPDATE CASCADE)
    ENGINE = InnoDB
    AUTO_INCREMENT = 6
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`HouseExtraFeature`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`HouseExtraFeature` (
                                                              `houseId` INT NOT NULL,
                                                              `featureId` INT NOT NULL,
                                                              PRIMARY KEY (`houseId`, `featureId`),
                                                              INDEX `!VERZINZELF!8_idx` (`featureId` ASC),
                                                              INDEX `!VERZINZELF!7_idx` (`houseId` ASC),
                                                              CONSTRAINT `fk_houseextrafeature_extrafeature`
                                                                  FOREIGN KEY (`featureId`)
                                                                      REFERENCES `zkwantd1`.`ExtraFeature` (`extraFeatureId`),
                                                              CONSTRAINT `fk_houseextrafeature_house`
                                                                  FOREIGN KEY (`houseId`)
                                                                      REFERENCES `zkwantd1`.`House` (`houseId`)
                                                                      ON DELETE CASCADE
                                                                      ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`Message`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`Message` (
                                                    `messageId` INT NOT NULL AUTO_INCREMENT,
                                                    `senderId` INT NULL DEFAULT NULL,
                                                    `receiverId` INT NULL DEFAULT NULL,
                                                    `dateTimeSent` DATETIME NOT NULL,
                                                    `subject` VARCHAR(150) NOT NULL,
                                                    `body` VARCHAR(2550) NOT NULL,
                                                    `archivedBySender` TINYINT NOT NULL DEFAULT '0',
                                                    `archivedByReceiver` TINYINT NOT NULL DEFAULT '0',
                                                    `readByReceiver` TINYINT NOT NULL DEFAULT '0',
                                                    PRIMARY KEY (`messageId`),
                                                    INDEX `!VERZINZELF!4_idx` (`senderId` ASC),
                                                    INDEX `!VERZINZELF!5_idx` (`receiverId` ASC),
                                                    CONSTRAINT `fk_message_Receiver`
                                                        FOREIGN KEY (`senderId`)
                                                            REFERENCES zkwantd1.`User` (`userId`),
                                                    CONSTRAINT `fk_message_Sender`
                                                        FOREIGN KEY (`receiverId`)
                                                            REFERENCES `zkwantd1`.`User` (`userId`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 12
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`Picture`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`Picture` (
                                                    `pictureId` INT NOT NULL AUTO_INCREMENT,
                                                    `houseId` INT NOT NULL,
                                                    `picture` MEDIUMBLOB NOT NULL,
                                                    `pictureDescription` VARCHAR(255) NULL DEFAULT NULL,
                                                    PRIMARY KEY (`pictureId`),
                                                    INDEX `!VERZINZELF!6_idx` (`houseId` ASC),
                                                    CONSTRAINT `fk_picture_house`
                                                        FOREIGN KEY (`houseId`)
                                                            REFERENCES `zkwantd1`.`House` (`houseId`)
                                                            ON DELETE CASCADE
                                                            ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `zkwantd1`.`Reservation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zkwantd1`.`Reservation` (
                                                        `reservationId` INT NOT NULL AUTO_INCREMENT,
                                                        `reservedByUserId` INT NOT NULL,
                                                        `houseId` INT NOT NULL,
                                                        `startDate` DATE NOT NULL,
                                                        `endDate` DATE NOT NULL,
                                                        `guestCount` INT NOT NULL,
                                                        PRIMARY KEY (`reservationId`),
                                                        INDEX `!VERZINZELF!3_idx` (`houseId` ASC),
                                                        INDEX `!VERZINZELF!2_idx` (`reservedByUserId` ASC),
                                                        CONSTRAINT `fk_reservation_house`
                                                            FOREIGN KEY (`houseId`)
                                                                REFERENCES `zkwantd1`.`House` (`houseId`),
                                                        CONSTRAINT `fk_reservation_user`
                                                            FOREIGN KEY (`reservedByUserId`)
                                                                REFERENCES `zkwantd1`.`User` (`userId`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 6
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- SET SQL_MODE=@OLD_SQL_MODE;
-- SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
-- SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;