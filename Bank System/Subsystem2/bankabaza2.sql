-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: bankabaza2
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `komitent`
--

DROP TABLE IF EXISTS `komitent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `komitent` (
  `idkomitent` int NOT NULL,
  `naziv` varchar(45) NOT NULL,
  `adresa` varchar(45) NOT NULL,
  `mesto` int NOT NULL,
  PRIMARY KEY (`idkomitent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `komitent`
--

LOCK TABLES `komitent` WRITE;
/*!40000 ALTER TABLE `komitent` DISABLE KEYS */;
INSERT INTO `komitent` VALUES (11,'foirma1','Adresica',21000),(13,'firmica1','Adresica',21000),(14,'Adobe','Adr',11000),(15,'kom123456','adr',11000),(16,'komomomo','adr',11000);
/*!40000 ALTER TABLE `komitent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racun`
--

DROP TABLE IF EXISTS `racun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `racun` (
  `brojRacuna` varchar(45) NOT NULL,
  `mesto` int NOT NULL,
  `stanje` float NOT NULL,
  `minus` float NOT NULL,
  `datumVreme` datetime NOT NULL,
  `brojTransakcija` int NOT NULL,
  `komitent` int NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`brojRacuna`),
  UNIQUE KEY `brojRacuna_UNIQUE` (`brojRacuna`),
  KEY `komitent1_idx` (`komitent`),
  CONSTRAINT `komitent1` FOREIGN KEY (`komitent`) REFERENCES `komitent` (`idkomitent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racun`
--

LOCK TABLES `racun` WRITE;
/*!40000 ALTER TABLE `racun` DISABLE KEYS */;
INSERT INTO `racun` VALUES ('123',11000,4639,0,'2022-07-04 15:00:22',28,13,'Neaktivan'),('321',21000,1000,0,'2022-07-05 22:50:32',3,11,'aktivan'),('4132',11000,-2900,0,'2022-07-08 17:11:11',2,14,'blokiran');
/*!40000 ALTER TABLE `racun` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transakcija`
--

DROP TABLE IF EXISTS `transakcija`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transakcija` (
  `idtransakcija` int NOT NULL AUTO_INCREMENT,
  `racun1` varchar(45) NOT NULL,
  `racun2` varchar(45) DEFAULT NULL,
  `kolicina` float NOT NULL,
  `datumVreme` datetime NOT NULL,
  `redniBroj1` int NOT NULL,
  `redniBroj2` int NOT NULL,
  `svrha` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idtransakcija`),
  KEY `racun1_idx` (`racun1`),
  KEY `racun2_idx` (`racun2`),
  CONSTRAINT `racun1` FOREIGN KEY (`racun1`) REFERENCES `racun` (`brojRacuna`),
  CONSTRAINT `racun2` FOREIGN KEY (`racun2`) REFERENCES `racun` (`brojRacuna`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transakcija`
--

LOCK TABLES `transakcija` WRITE;
/*!40000 ALTER TABLE `transakcija` DISABLE KEYS */;
INSERT INTO `transakcija` VALUES (1,'123','321',1000,'2022-07-05 23:01:22',1,0,'Test'),(2,'123',NULL,500,'2022-07-06 16:47:02',2,0,'uplata'),(3,'123',NULL,500,'2022-07-06 16:47:29',3,0,'uplata'),(4,'123',NULL,500,'2022-07-06 16:47:36',4,0,'uplata'),(10,'123',NULL,1000,'2022-07-06 22:42:57',5,0,'tst'),(11,'123',NULL,1000,'2022-07-06 22:42:58',6,0,'tst'),(12,'123',NULL,1000,'2022-07-06 22:42:59',7,0,'tst'),(13,'123',NULL,1000,'2022-07-06 22:42:59',8,0,'tst'),(14,'123',NULL,1000,'2022-07-06 22:43:00',9,0,'tst'),(15,'123',NULL,50,'2022-07-06 22:58:21',10,0,'uplatica'),(16,'123',NULL,50,'2022-07-06 22:58:22',11,0,'uplatica'),(17,'123',NULL,50,'2022-07-06 22:58:23',12,0,'uplatica'),(18,'123',NULL,50,'2022-07-06 22:58:23',13,0,'uplatica'),(19,'123',NULL,50,'2022-07-06 22:58:24',14,0,'uplatica'),(20,'123',NULL,50,'2022-07-06 22:58:24',15,0,'uplatica'),(21,'123',NULL,50,'2022-07-06 22:58:25',16,0,'uplatica'),(22,'123',NULL,50,'2022-07-06 22:58:26',17,0,'uplatica'),(23,'123',NULL,50,'2022-07-06 22:58:26',18,0,'uplatica'),(24,'123',NULL,50,'2022-07-06 22:58:27',19,0,'uplatica'),(25,'123',NULL,50,'2022-07-06 22:58:27',20,0,'uplatica'),(26,'123',NULL,50,'2022-07-06 22:58:28',21,0,'uplatica'),(27,'123',NULL,50,'2022-07-06 22:58:29',22,0,'uplatica'),(28,'321',NULL,100,'2022-07-07 21:50:58',1,0,'tst'),(29,'123',NULL,-11,'2022-07-07 22:58:47',23,0,'upd'),(30,'4132',NULL,-3000,'2022-07-08 17:13:39',1,0,'isplata'),(31,'321','4132',100,'2022-07-08 19:52:15',2,0,'tst');
/*!40000 ALTER TABLE `transakcija` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-08 17:54:52
