-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: PV_TugasBesar
-- ------------------------------------------------------
-- Server version	8.4.9

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
-- Table structure for table `attendance_form_levels`
--

DROP TABLE IF EXISTS `attendance_form_levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_form_levels` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `attendance_form_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_attendance_form_levels` (`attendance_form_uuid`,`level_uuid`),
  KEY `fk_attendance_form_levels_level_uuid` (`level_uuid`),
  CONSTRAINT `fk_attendance_form_levels_form_uuid` FOREIGN KEY (`attendance_form_uuid`) REFERENCES `attendance_forms` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_attendance_form_levels_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_form_levels`
--

LOCK TABLES `attendance_form_levels` WRITE;
/*!40000 ALTER TABLE `attendance_form_levels` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_form_levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_forms`
--

DROP TABLE IF EXISTS `attendance_forms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_forms` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `coach_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `attendance_date` date DEFAULT NULL,
  `pertemuan_ke` tinyint DEFAULT NULL,
  `period_year` int NOT NULL,
  `period_month` tinyint NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `notes` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `idx_attendance_forms_slot` (`coach_uuid`,`level_uuid`,`attendance_date`,`pertemuan_ke`),
  KEY `idx_attendance_forms_period` (`period_year`,`period_month`,`coach_uuid`),
  KEY `idx_attendance_forms_level_date` (`level_uuid`,`attendance_date`,`pertemuan_ke`),
  CONSTRAINT `fk_attendance_forms_coach_uuid` FOREIGN KEY (`coach_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_attendance_forms_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_forms`
--

LOCK TABLES `attendance_forms` WRITE;
/*!40000 ALTER TABLE `attendance_forms` DISABLE KEYS */;
INSERT INTO `attendance_forms` VALUES ('1e4b0111-1273-4db8-ba87-b275dc493fee','acc751af-eb0e-4f22-a7ea-4a26b84df271','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-15',3,2026,5,1,'P3 Gamma Class coach jokowi','2026-05-15 15:57:24','2026-05-15 15:57:24'),('2409e91e-3907-4f59-89c9-cc0f4f6a4a86','c20235d4-c99a-42e8-8a18-57170e19dd4c','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-05',1,2026,5,1,'','2026-05-05 03:30:15','2026-05-05 03:30:15'),('2a8f1587-b2e1-423a-9050-619c5156965e','6764381e-7db8-45fe-ac26-ebbce8b52259','690b9b97-648a-4f59-b1ef-7c1a392dc9ea','2026-05-10',1,2026,5,1,'','2026-05-10 13:46:37','2026-05-10 13:46:37'),('3c2ae72a-6cff-4ac9-af36-3dadd09305ae','c20235d4-c99a-42e8-8a18-57170e19dd4c','690b9b97-648a-4f59-b1ef-7c1a392dc9ea','2026-05-05',1,2026,5,1,'','2026-05-05 03:30:23','2026-05-05 03:30:23'),('4c9c43b2-ea2d-45ff-8bf6-0cbfb5cd034c','5a0008c3-987f-48f9-b738-00af8bd79f94','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-05',1,2026,5,1,'','2026-05-05 02:42:27','2026-05-05 02:42:27'),('5b146d9b-7253-4c35-9433-19b94244bcae','c20235d4-c99a-42e8-8a18-57170e19dd4c','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-10',1,2026,5,1,'','2026-05-10 13:55:45','2026-05-10 13:55:45'),('5bc8ca67-40c8-4157-8acd-39489b2f2dee','6764381e-7db8-45fe-ac26-ebbce8b52259','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',1,2026,5,1,'P1 Beta','2026-05-05 02:41:11','2026-05-05 02:41:11'),('61c4503f-db20-49f3-860c-164e5b3038aa','6764381e-7db8-45fe-ac26-ebbce8b52259','f36165d3-8838-414c-bcb8-8def8c1a21a7','2026-05-10',1,2026,5,1,'','2026-05-10 13:48:31','2026-05-10 13:48:31'),('7fbe365b-e3c1-493c-a08a-5a8ba21aa2a6','6764381e-7db8-45fe-ac26-ebbce8b52259','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-10',1,2026,5,1,'','2026-05-10 13:20:44','2026-05-10 13:20:44'),('9fec369d-e343-4aac-a409-e51467537766','5a0008c3-987f-48f9-b738-00af8bd79f94','f36165d3-8838-414c-bcb8-8def8c1a21a7','2026-05-10',1,2026,5,1,'','2026-05-10 13:56:25','2026-05-10 13:56:25'),('be86b505-09f0-498a-bc02-886560adefa3','c20235d4-c99a-42e8-8a18-57170e19dd4c','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-05',1,2026,5,1,'','2026-05-05 02:41:51','2026-05-05 02:41:51'),('e4553e74-fd00-40c6-95cd-d418e3d398d2','6764381e-7db8-45fe-ac26-ebbce8b52259','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-10',1,2026,5,1,'','2026-05-10 13:20:32','2026-05-10 13:20:32'),('ea5c4e60-ffc3-4019-b1d2-b089643c28a4','acc751af-eb0e-4f22-a7ea-4a26b84df271','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-15',1,2026,5,1,'P1','2026-05-15 14:53:53','2026-05-15 14:53:53'),('ed586b42-bc22-4d22-a4d1-2dd6194e85c4','94b44189-f22b-424e-a8a5-3401f45b5177','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-15',3,2026,5,1,'Ini Form Absensi Sgma Class P3','2026-05-15 15:55:13','2026-05-15 15:55:13'),('ee9655c4-9f6d-4a52-b1d8-ab07ccd9708a','6764381e-7db8-45fe-ac26-ebbce8b52259','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-10',1,2026,5,1,'','2026-05-10 13:46:49','2026-05-10 13:46:49'),('ef6a0dd7-3b3b-4348-b5a8-6c12b683cf52','6764381e-7db8-45fe-ac26-ebbce8b52259','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',2,2026,5,1,'','2026-05-05 02:42:07','2026-05-05 02:42:07');
/*!40000 ALTER TABLE `attendance_forms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_records`
--

DROP TABLE IF EXISTS `attendance_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_records` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `attendance_form_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `coach_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `murid_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tanggal_absensi` date NOT NULL,
  `pertemuan_ke` tinyint NOT NULL,
  `status_absensi` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Hadir',
  `catatan` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_attendance_unique_entry` (`coach_uuid`,`murid_uuid`,`tanggal_absensi`,`pertemuan_ke`),
  KEY `idx_attendance_month` (`tanggal_absensi`),
  KEY `idx_attendance_coach` (`coach_uuid`),
  KEY `idx_attendance_murid` (`murid_uuid`),
  KEY `idx_attendance_records_form_uuid` (`attendance_form_uuid`),
  KEY `idx_attendance_records_level_uuid` (`level_uuid`),
  CONSTRAINT `fk_attendance_coach_uuid` FOREIGN KEY (`coach_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_attendance_murid_uuid` FOREIGN KEY (`murid_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_attendance_records_form_uuid` FOREIGN KEY (`attendance_form_uuid`) REFERENCES `attendance_forms` (`uuid`) ON DELETE SET NULL,
  CONSTRAINT `fk_attendance_records_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_records`
--

LOCK TABLES `attendance_records` WRITE;
/*!40000 ALTER TABLE `attendance_records` DISABLE KEYS */;
INSERT INTO `attendance_records` VALUES ('02cfa755-ffcc-4251-94c9-4cdaf15a7108','ea5c4e60-ffc3-4019-b1d2-b089643c28a4','acc751af-eb0e-4f22-a7ea-4a26b84df271','be4a9fea-8c22-4de1-9d18-37c56bbcc07b','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-15',1,'Hadir','','2026-05-15 15:00:12','2026-05-15 15:00:12'),('052e7943-58ca-4da1-9c2d-440334a3903a','2409e91e-3907-4f59-89c9-cc0f4f6a4a86','c20235d4-c99a-42e8-8a18-57170e19dd4c','13a3c7d4-dce8-4500-91ab-35a79c601f45','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-05',1,'Hadir','','2026-05-05 03:52:28','2026-05-05 03:52:28'),('267bb751-1be7-49af-abd2-1e150ba11aa6','be86b505-09f0-498a-bc02-886560adefa3','c20235d4-c99a-42e8-8a18-57170e19dd4c','e427197b-e050-4ebf-b403-0d1e8ba525fa','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:52:22','2026-05-05 03:52:22'),('2dc04fbf-7438-42db-81e0-2880d2ae1398','4c9c43b2-ea2d-45ff-8bf6-0cbfb5cd034c','5a0008c3-987f-48f9-b738-00af8bd79f94','b449567e-9d98-42bc-8bdc-19ca9f394f1e','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:34:05','2026-05-05 03:34:05'),('2de1469e-68e2-4dfc-9f21-f97aa88ac058','3c2ae72a-6cff-4ac9-af36-3dadd09305ae','c20235d4-c99a-42e8-8a18-57170e19dd4c','d8980455-34bf-49aa-84ef-8ba0c3b2576c','690b9b97-648a-4f59-b1ef-7c1a392dc9ea','2026-05-05',1,'Hadir','','2026-05-05 03:52:26','2026-05-05 03:52:26'),('33a7685d-7bd9-4246-ae1a-178d9f1f94ce','ea5c4e60-ffc3-4019-b1d2-b089643c28a4','acc751af-eb0e-4f22-a7ea-4a26b84df271','90fc286a-784c-4e91-972b-08168aa008fc','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-15',1,'Alpha','','2026-05-15 15:00:13','2026-05-15 15:00:13'),('3cc918d1-b504-40d1-a93c-bb63d9a15fad','ea5c4e60-ffc3-4019-b1d2-b089643c28a4','acc751af-eb0e-4f22-a7ea-4a26b84df271','8af9d17b-40be-44b4-a325-d910ba071c24','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-15',1,'Hadir','','2026-05-15 15:00:12','2026-05-15 15:00:12'),('4d0a07e2-371c-4de5-a663-f5ebcd5b604a','3c2ae72a-6cff-4ac9-af36-3dadd09305ae','c20235d4-c99a-42e8-8a18-57170e19dd4c','d9dfda19-4ce0-4309-87ba-a397e5a3acf4','690b9b97-648a-4f59-b1ef-7c1a392dc9ea','2026-05-05',1,'Hadir','','2026-05-05 03:52:26','2026-05-05 03:52:26'),('644c7f97-1e72-4b8d-b87e-43bc15ad6731','4c9c43b2-ea2d-45ff-8bf6-0cbfb5cd034c','5a0008c3-987f-48f9-b738-00af8bd79f94','8af9d17b-40be-44b4-a325-d910ba071c24','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:34:05','2026-05-05 03:34:05'),('69e96409-5efb-4731-a88c-621df4378435','be86b505-09f0-498a-bc02-886560adefa3','c20235d4-c99a-42e8-8a18-57170e19dd4c','81af22aa-fcf1-484d-a130-cfa236e221a9','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:52:22','2026-05-05 03:52:22'),('836e5b4f-7e34-4331-a708-71ecbec011cd','ef6a0dd7-3b3b-4348-b5a8-6c12b683cf52','6764381e-7db8-45fe-ac26-ebbce8b52259','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',2,'Hadir','','2026-05-05 03:52:10','2026-05-05 03:52:10'),('865eb432-4045-416a-8fbe-40be072fac89','ea5c4e60-ffc3-4019-b1d2-b089643c28a4','acc751af-eb0e-4f22-a7ea-4a26b84df271','b449567e-9d98-42bc-8bdc-19ca9f394f1e','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-15',1,'Hadir','','2026-05-15 15:00:13','2026-05-15 15:00:13'),('8ae5a9f9-553c-4f35-b3ee-22712aa30992','ed586b42-bc22-4d22-a4d1-2dd6194e85c4','94b44189-f22b-424e-a8a5-3401f45b5177','ac3ef9e4-c903-4c43-b60e-e8446d0820f7','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-15',3,'Hadir','','2026-05-15 16:03:35','2026-05-15 16:03:35'),('9a14d547-5a90-47f8-add0-dd0492bb4f78','2409e91e-3907-4f59-89c9-cc0f4f6a4a86','c20235d4-c99a-42e8-8a18-57170e19dd4c','1a6a95a2-fd89-46cc-9de8-4c7b8c097271','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-05',1,'Alpha','','2026-05-05 03:52:28','2026-05-05 03:52:28'),('9ae2a78d-a809-4f37-82bb-92505511880c','be86b505-09f0-498a-bc02-886560adefa3','c20235d4-c99a-42e8-8a18-57170e19dd4c','33f63f0d-7bc0-4d2a-acf3-360d67a11049','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:52:22','2026-05-05 03:52:22'),('b75a5190-7b85-4142-8222-3ff190fbf020','2409e91e-3907-4f59-89c9-cc0f4f6a4a86','c20235d4-c99a-42e8-8a18-57170e19dd4c','ac3ef9e4-c903-4c43-b60e-e8446d0820f7','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-05',1,'Alpha','','2026-05-05 03:52:28','2026-05-05 03:52:28'),('bc99956a-9df2-4d20-8775-4448ba29989a','be86b505-09f0-498a-bc02-886560adefa3','c20235d4-c99a-42e8-8a18-57170e19dd4c','62ed7565-e339-489a-8811-4f16109a6a8e','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:52:22','2026-05-05 03:52:22'),('c1ba2483-da15-4283-b548-c3178651ae76','ed586b42-bc22-4d22-a4d1-2dd6194e85c4','94b44189-f22b-424e-a8a5-3401f45b5177','1a6a95a2-fd89-46cc-9de8-4c7b8c097271','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-15',3,'Alpha','','2026-05-15 16:03:35','2026-05-15 16:03:35'),('c43f6f80-1a39-4db9-8ae3-7bb48e71df94','5bc8ca67-40c8-4157-8acd-39489b2f2dee','6764381e-7db8-45fe-ac26-ebbce8b52259','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:52:04','2026-05-05 03:52:04'),('c7e07dca-1a94-4ff8-a13e-0bfd58efbb59','5bc8ca67-40c8-4157-8acd-39489b2f2dee','6764381e-7db8-45fe-ac26-ebbce8b52259','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Alpha','','2026-05-05 03:52:04','2026-05-05 03:52:04'),('d9a2ffaa-3c1b-4c8c-b5df-cbde260d0a46','4c9c43b2-ea2d-45ff-8bf6-0cbfb5cd034c','5a0008c3-987f-48f9-b738-00af8bd79f94','90fc286a-784c-4e91-972b-08168aa008fc','b19e7e38-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Alpha','','2026-05-05 03:34:05','2026-05-05 03:34:05'),('d9dbbb88-aee7-4a86-96d3-914ad9177b5f','3c2ae72a-6cff-4ac9-af36-3dadd09305ae','c20235d4-c99a-42e8-8a18-57170e19dd4c','b5bc308b-cffc-46d8-8a53-0a515a97e6de','690b9b97-648a-4f59-b1ef-7c1a392dc9ea','2026-05-05',1,'Alpha','','2026-05-05 03:52:26','2026-05-05 03:52:26'),('e1e143fc-a705-44a3-8853-7b86e5529c08','5bc8ca67-40c8-4157-8acd-39489b2f2dee','6764381e-7db8-45fe-ac26-ebbce8b52259','be4a9fea-8c22-4de1-9d18-37c56bbcc07b','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Alpha','','2026-05-05 03:52:04','2026-05-05 03:52:04'),('ea271e1a-36e2-4677-ab8b-d4ac1a32b199','ef6a0dd7-3b3b-4348-b5a8-6c12b683cf52','6764381e-7db8-45fe-ac26-ebbce8b52259','be4a9fea-8c22-4de1-9d18-37c56bbcc07b','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',2,'Hadir','','2026-05-05 03:52:10','2026-05-05 03:52:10'),('ecc34c3c-6679-4795-b5d1-93113cc0b4fa','be86b505-09f0-498a-bc02-886560adefa3','c20235d4-c99a-42e8-8a18-57170e19dd4c','8684594e-c0d3-4efd-9b09-accb95a5858f','b19d5454-35b9-11f1-a56d-002b671d8831','2026-05-05',1,'Hadir','','2026-05-05 03:52:22','2026-05-05 03:52:22'),('ed08e9c0-a0f0-4fe3-aa1e-b2140e89097b','ed586b42-bc22-4d22-a4d1-2dd6194e85c4','94b44189-f22b-424e-a8a5-3401f45b5177','13a3c7d4-dce8-4500-91ab-35a79c601f45','a539ae38-7527-45aa-84f0-19d9c9ae91a8','2026-05-15',3,'Hadir','Kiboy Telat 30 menit','2026-05-15 16:03:35','2026-05-15 16:04:28'),('fdb30c87-9bfd-4282-9171-8bd9cfdf024c','ef6a0dd7-3b3b-4348-b5a8-6c12b683cf52','6764381e-7db8-45fe-ac26-ebbce8b52259','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','b19d7d32-35b9-11f1-a56d-002b671d8831','2026-05-05',2,'Alpha','','2026-05-05 03:52:10','2026-05-05 03:52:10');
/*!40000 ALTER TABLE `attendance_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coach_salary_payments`
--

DROP TABLE IF EXISTS `coach_salary_payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coach_salary_payments` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `coach_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `grade_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `payment_year` int NOT NULL,
  `payment_month` tinyint NOT NULL,
  `salary_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `is_paid` tinyint(1) NOT NULL DEFAULT '0',
  `paid_at` timestamp NULL DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_coach_salary_payments_period` (`coach_uuid`,`payment_year`,`payment_month`),
  KEY `fk_coach_salary_payments_grade_uuid` (`grade_uuid`),
  CONSTRAINT `fk_coach_salary_payments_coach_uuid` FOREIGN KEY (`coach_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_coach_salary_payments_grade_uuid` FOREIGN KEY (`grade_uuid`) REFERENCES `grades` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coach_salary_payments`
--

LOCK TABLES `coach_salary_payments` WRITE;
/*!40000 ALTER TABLE `coach_salary_payments` DISABLE KEYS */;
INSERT INTO `coach_salary_payments` VALUES ('1f61eca7-4e67-11f1-b077-bab4cd8b914d','5a0008c3-987f-48f9-b738-00af8bd79f94','6e4dfa2a-4066-11f1-8f98-002b671d8831',2026,8,20000.00,0,NULL,'','2026-05-13 01:00:24','2026-05-13 01:00:24'),('1f61f034-4e67-11f1-b077-bab4cd8b914d','6764381e-7db8-45fe-ac26-ebbce8b52259','6e4d58df-4066-11f1-8f98-002b671d8831',2026,8,50000.00,0,NULL,'','2026-05-13 01:00:24','2026-05-13 01:00:24'),('1f61f224-4e67-11f1-b077-bab4cd8b914d','acc751af-eb0e-4f22-a7ea-4a26b84df271','6e4dfa2a-4066-11f1-8f98-002b671d8831',2026,8,20000.00,0,NULL,'','2026-05-13 01:00:24','2026-05-13 01:00:24'),('1f61f2c6-4e67-11f1-b077-bab4cd8b914d','c20235d4-c99a-42e8-8a18-57170e19dd4c','6e4d27f9-4066-11f1-8f98-002b671d8831',2026,8,30000.00,0,NULL,'','2026-05-13 01:00:24','2026-05-13 01:00:24'),('3e76952c-5077-11f1-bb85-0e17646df36c','94b44189-f22b-424e-a8a5-3401f45b5177','6e4d58df-4066-11f1-8f98-002b671d8831',2026,5,10000000.00,1,'2026-05-15 23:14:22','','2026-05-15 16:00:50','2026-05-15 16:14:21'),('69357b24-4f60-11f1-b168-521f29b6c773','d2f54458-126b-4913-97ab-78a7fadc752b','6e4d27f9-4066-11f1-8f98-002b671d8831',2026,5,7000000.00,0,NULL,'Rekening keblokir','2026-05-14 06:44:52','2026-05-15 16:14:47'),('dd5179c0-4c76-11f1-af32-166b1b2e3a2d','acc751af-eb0e-4f22-a7ea-4a26b84df271','6e4d27f9-4066-11f1-8f98-002b671d8831',2026,5,7000000.00,1,'2026-05-10 21:25:10','','2026-05-10 13:48:02','2026-05-15 16:10:35'),('e69d09a5-4c6e-11f1-af32-166b1b2e3a2d','5a0008c3-987f-48f9-b738-00af8bd79f94','6e4d27f9-4066-11f1-8f98-002b671d8831',2026,5,7000000.00,1,'2026-05-10 21:25:10','','2026-05-10 12:51:02','2026-05-15 16:10:35'),('e69d0f7f-4c6e-11f1-af32-166b1b2e3a2d','6764381e-7db8-45fe-ac26-ebbce8b52259','6e4d58df-4066-11f1-8f98-002b671d8831',2026,5,10000000.00,1,'2026-05-15 23:14:31','','2026-05-10 12:51:02','2026-05-15 16:14:30'),('e69d1184-4c6e-11f1-af32-166b1b2e3a2d','c20235d4-c99a-42e8-8a18-57170e19dd4c','6e4dfa2a-4066-11f1-8f98-002b671d8831',2026,5,5000000.00,0,NULL,'Belum bikin rekening','2026-05-10 12:51:02','2026-05-15 16:14:39');
/*!40000 ALTER TABLE `coach_salary_payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade_coach_payment_rates`
--

DROP TABLE IF EXISTS `grade_coach_payment_rates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_coach_payment_rates` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `grade_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `monthly_rate` decimal(12,2) NOT NULL DEFAULT '0.00',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_grade_coach_payment_rates_grade_uuid` (`grade_uuid`),
  CONSTRAINT `fk_grade_coach_payment_rates_grade_uuid` FOREIGN KEY (`grade_uuid`) REFERENCES `grades` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade_coach_payment_rates`
--

LOCK TABLES `grade_coach_payment_rates` WRITE;
/*!40000 ALTER TABLE `grade_coach_payment_rates` DISABLE KEYS */;
INSERT INTO `grade_coach_payment_rates` VALUES ('a16559f8-4866-11f1-b8d2-f28d797ea5d6','6e4dfa2a-4066-11f1-8f98-002b671d8831',5000000.00,'2026-05-05 09:41:45','2026-05-15 16:10:24'),('a1655ac2-4866-11f1-b8d2-f28d797ea5d6','6e4d27f9-4066-11f1-8f98-002b671d8831',7000000.00,'2026-05-05 09:41:45','2026-05-15 16:10:35'),('a1655ae3-4866-11f1-b8d2-f28d797ea5d6','6e4d58df-4066-11f1-8f98-002b671d8831',10000000.00,'2026-05-05 09:41:45','2026-05-15 16:10:46');
/*!40000 ALTER TABLE `grade_coach_payment_rates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grades` (
  `uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci NOT NULL,
  `grade_value` int NOT NULL,
  `sort_order` int NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `grade_value` (`grade_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grades`
--

LOCK TABLES `grades` WRITE;
/*!40000 ALTER TABLE `grades` DISABLE KEYS */;
INSERT INTO `grades` VALUES ('6e4d27f9-4066-11f1-8f98-002b671d8831','Grade 1','Grade 1 = beginner.',1,1,'2026-04-25 05:20:10','2026-04-25 05:28:19'),('6e4d58df-4066-11f1-8f98-002b671d8831','Grade 2','Grade 2 = Classic',2,2,'2026-04-25 05:20:10','2026-04-25 05:28:35'),('6e4dfa2a-4066-11f1-8f98-002b671d8831','Grade 0','Grade 0 = basic',0,3,'2026-04-25 05:20:10','2026-04-25 05:27:52');
/*!40000 ALTER TABLE `grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `level_payment_configs`
--

DROP TABLE IF EXISTS `level_payment_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `level_payment_configs` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `monthly_spp` decimal(12,2) NOT NULL DEFAULT '0.00',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_level_payment_configs_level_uuid` (`level_uuid`),
  CONSTRAINT `fk_level_payment_configs_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `level_payment_configs`
--

LOCK TABLES `level_payment_configs` WRITE;
/*!40000 ALTER TABLE `level_payment_configs` DISABLE KEYS */;
INSERT INTO `level_payment_configs` VALUES ('a163292e-4866-11f1-b8d2-f28d797ea5d6','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',100000.00,'2026-05-05 09:41:45','2026-05-15 16:09:50'),('a1632b00-4866-11f1-b8d2-f28d797ea5d6','a539ae38-7527-45aa-84f0-19d9c9ae91a8',90000.00,'2026-05-05 09:41:45','2026-05-15 16:09:28'),('a1632b15-4866-11f1-b8d2-f28d797ea5d6','b19d5454-35b9-11f1-a56d-002b671d8831',50000.00,'2026-05-05 09:41:45','2026-05-09 01:48:15'),('a1632b22-4866-11f1-b8d2-f28d797ea5d6','f36165d3-8838-414c-bcb8-8def8c1a21a7',0.00,'2026-05-05 09:41:45','2026-05-05 09:41:45'),('a1632b2e-4866-11f1-b8d2-f28d797ea5d6','b19d7d32-35b9-11f1-a56d-002b671d8831',70000.00,'2026-05-05 09:41:45','2026-05-15 16:09:09'),('a1632b3b-4866-11f1-b8d2-f28d797ea5d6','3c9ae974-2fe3-47e5-81ac-8707e67978a5',0.00,'2026-05-05 09:41:45','2026-05-05 09:41:45'),('a1632b44-4866-11f1-b8d2-f28d797ea5d6','b19e7e38-35b9-11f1-a56d-002b671d8831',80000.00,'2026-05-05 09:41:45','2026-05-15 16:09:17');
/*!40000 ALTER TABLE `level_payment_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `levels`
--

DROP TABLE IF EXISTS `levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `levels` (
  `uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `grade_uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_levels_grade_uuid` (`grade_uuid`),
  CONSTRAINT `fk_levels_grade_uuid` FOREIGN KEY (`grade_uuid`) REFERENCES `grades` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `levels`
--

LOCK TABLES `levels` WRITE;
/*!40000 ALTER TABLE `levels` DISABLE KEYS */;
INSERT INTO `levels` VALUES ('3c9ae974-2fe3-47e5-81ac-8707e67978a5','No Level','Role ini tidak butuh level','6e4dfa2a-4066-11f1-8f98-002b671d8831',4,'2026-04-11 16:50:28','2026-04-25 05:28:53'),('690b9b97-648a-4f59-b1ef-7c1a392dc9ea','Omega Class','Grade 2nd:Classic','6e4d58df-4066-11f1-8f98-002b671d8831',6,'2026-04-25 04:58:50','2026-05-14 13:11:49'),('a539ae38-7527-45aa-84f0-19d9c9ae91a8','Sigma Class','Grade 2nd: Classic','6e4d58df-4066-11f1-8f98-002b671d8831',5,'2026-04-25 04:58:33','2026-05-14 13:12:06'),('b19d5454-35b9-11f1-a56d-002b671d8831','Alpha Class','Level Grade 0 Basic','6e4dfa2a-4066-11f1-8f98-002b671d8831',1,'2026-04-11 15:18:29','2026-05-14 13:11:28'),('b19d7d32-35b9-11f1-a56d-002b671d8831','Beta Class','Level Grade  1st : Beginner','6e4d27f9-4066-11f1-8f98-002b671d8831',2,'2026-04-11 15:18:29','2026-05-14 13:11:42'),('b19e7e38-35b9-11f1-a56d-002b671d8831','Gamma Class','Grade 1st: Beginner','6e4d27f9-4066-11f1-8f98-002b671d8831',3,'2026-04-11 15:18:29','2026-05-14 13:11:35'),('f36165d3-8838-414c-bcb8-8def8c1a21a7','Basic','Pelatih level basic','6e4d27f9-4066-11f1-8f98-002b671d8831',7,'2026-04-25 05:07:59','2026-04-25 05:20:10');
/*!40000 ALTER TABLE `levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `master_peralatan`
--

DROP TABLE IF EXISTS `master_peralatan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `master_peralatan` (
  `uuid` char(36) NOT NULL,
  `nama_peralatan` varchar(120) NOT NULL,
  `jenis` varchar(80) NOT NULL,
  `ukuran` varchar(40) DEFAULT NULL,
  `jumlah` int NOT NULL DEFAULT '0',
  `kondisi` varchar(40) NOT NULL DEFAULT 'Baik',
  `status` varchar(40) NOT NULL DEFAULT 'Tersedia',
  `keterangan` varchar(255) NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `nama_peralatan` (`nama_peralatan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `master_peralatan`
--

LOCK TABLES `master_peralatan` WRITE;
/*!40000 ALTER TABLE `master_peralatan` DISABLE KEYS */;
INSERT INTO `master_peralatan` VALUES ('735e93b0-8a79-4ae9-81e7-3c06f9cee445','Helm','barang penting','40',10,'Baik','Dipinjam','Helm Ukuran 40','2026-05-15 15:51:46','2026-05-15 15:51:46'),('7403b19d-91e2-4eef-a60e-7839bd9d21f8','Sepatu Roda','barang penting','30',5,'Cukup','Tersedia','Sepatu Roda ukuran 30','2026-05-15 15:51:17','2026-05-15 15:51:17'),('e1fb5c05-9fbe-4717-b9f2-b8e23d9e39c1','test','test','test',10,'Baik','Tersedia','','2026-05-10 13:48:21','2026-05-10 13:48:21');
/*!40000 ALTER TABLE `master_peralatan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modules`
--

DROP TABLE IF EXISTS `modules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modules` (
  `uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `code` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modules`
--

LOCK TABLES `modules` WRITE;
/*!40000 ALTER TABLE `modules` DISABLE KEYS */;
INSERT INTO `modules` VALUES ('0a5941fe-476d-11f1-844f-3e98f7c39a3e','0a594240-476d-11f1-844f-3e98f7c39a3e','Absensi','Kelola absensi murid per pertemuan oleh coach.',8,'2026-05-04 03:55:07','2026-05-04 03:55:07'),('6e62f4df-4066-11f1-8f98-002b671d8831','6e62f4ef-4066-11f1-8f98-002b671d8831','Grade','Kelola grade untuk klasifikasi level user.',7,'2026-04-25 05:20:10','2026-04-25 05:20:10'),('93579e9b-4f4f-11f1-b168-521f29b6c773','93579eb0-4f4f-11f1-b168-521f29b6c773','Master Progress Murid','Kelola template progress murid per level dan checklist kelulusan oleh coach.',16,'2026-05-14 04:44:21','2026-05-14 04:44:21'),('946a26a8-4f5d-11f1-b168-521f29b6c773','946a26b3-4f5d-11f1-b168-521f29b6c773','Peralatan Coach','Lihat data peralatan untuk kebutuhan coach tanpa akses master.',17,'2026-05-14 06:24:36','2026-05-14 06:24:36'),('96ee31de-35c5-11f1-a56d-002b671d8831','96ee31fb-35c5-11f1-a56d-002b671d8831','Level','Kelola level user dan klasifikasi pembinaan.',6,'2026-04-11 16:43:38','2026-04-11 16:43:38'),('a1669671-4866-11f1-b8d2-f28d797ea5d6','a1669688-4866-11f1-b8d2-f28d797ea5d6','Master Pembayaran','Kelola SPP per level, rate coach per grade, dan checklist pembayaran murid.',15,'2026-05-05 09:41:45','2026-05-05 09:41:45'),('d31030b2-35a1-11f1-a56d-002b671d8831','d31030b2-35a1-11f1-a56d-002b671d8831','Dashboard','Ringkasan utama aplikasi dan informasi umum.',1,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d31041b2-35a1-11f1-a56d-002b671d8831','d31041b2-35a1-11f1-a56d-002b671d8831','User','Manajemen data pengguna, role, status akun, dan penanda super admin.',2,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d3105333-35a1-11f1-a56d-002b671d8831','d3105333-35a1-11f1-a56d-002b671d8831','Role','Mengatur hak akses module yang tampil di navbar berdasarkan role.',3,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d310697c-35a1-11f1-a56d-002b671d8831','d310697c-35a1-11f1-a56d-002b671d8831','Laporan','Akses laporan dan rekapitulasi data.',4,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d31078f1-35a1-11f1-a56d-002b671d8831','d31078f1-35a1-11f1-a56d-002b671d8831','Pengaturan','Konfigurasi aplikasi dan preferensi sistem.',5,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d5b21e5a-4f5a-11f1-b168-521f29b6c773','d5b21e6a-4f5a-11f1-b168-521f29b6c773','Checklist Progress Murid','Halaman coach untuk mengisi checklist ujian progress murid.',17,'2026-05-14 06:04:57','2026-05-14 06:04:57'),('d5b2e661-4f5a-11f1-b168-521f29b6c773','d5b2e66d-4f5a-11f1-b168-521f29b6c773','Progress Saya','Halaman murid untuk melihat progress checklist miliknya.',18,'2026-05-14 06:04:57','2026-05-14 06:04:57'),('eb498562-476d-11f1-844f-3e98f7c39a3e','eb49878f-476d-11f1-844f-3e98f7c39a3e','Master Murid','Master data murid.',11,'2026-05-04 04:01:24','2026-05-04 04:01:24'),('eb4a1fcd-476d-11f1-844f-3e98f7c39a3e','eb4a1fe0-476d-11f1-844f-3e98f7c39a3e','Master Coach','Master data coach.',12,'2026-05-04 04:01:24','2026-05-04 04:01:24'),('eb4a8785-476d-11f1-844f-3e98f7c39a3e','eb4a8795-476d-11f1-844f-3e98f7c39a3e','Master Peralatan','Master data peralatan.',13,'2026-05-04 04:01:24','2026-05-04 04:01:24'),('eb4ae10d-476d-11f1-844f-3e98f7c39a3e','eb4ae118-476d-11f1-844f-3e98f7c39a3e','Master Absensi','Master form absensi coach.',14,'2026-05-04 04:01:24','2026-05-04 04:01:24');
/*!40000 ALTER TABLE `modules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progress_template_items`
--

DROP TABLE IF EXISTS `progress_template_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `progress_template_items` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `template_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kode_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kompetensi` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'FISIK',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_progress_template_items_code` (`template_uuid`,`kode_unit`),
  CONSTRAINT `fk_progress_template_items_template_uuid` FOREIGN KEY (`template_uuid`) REFERENCES `progress_templates` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progress_template_items`
--

LOCK TABLES `progress_template_items` WRITE;
/*!40000 ALTER TABLE `progress_template_items` DISABLE KEYS */;
INSERT INTO `progress_template_items` VALUES ('2b717d1e-448f-43ab-9d39-fd5afff1f2e7','32965d2c-d643-4175-9dae-6f692784fb3f','BB3','Lemon Push','FAIRPLAY',1,3,'2026-05-15 16:21:00','2026-05-15 16:21:00'),('3e312b6e-7bf8-4f51-893e-f478a8b2f3cf','d9bfe1d7-e501-4381-9ae6-46ee0263090b','AA2','Zigzag','TEKNIK',1,2,'2026-05-14 07:01:24','2026-05-14 07:01:24'),('4d856ee6-b0cf-4d90-b766-093d6becea80','32965d2c-d643-4175-9dae-6f692784fb3f','BB2','Lari Sprint','FISIK',1,2,'2026-05-15 16:20:46','2026-05-15 16:20:46'),('5904f551-60a5-49de-99da-1ac659099384','965a5edb-ae4b-47b3-971c-dd54ec1350c3','AA1','ADUHAI','TEKNIK',1,1,'2026-05-14 13:47:42','2026-05-14 13:47:42'),('810907ed-2c5b-4f52-99ba-6568b27ef820','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','AA7','Zigzag','FAIRPLAY',1,3,'2026-05-14 06:27:07','2026-05-14 06:27:07'),('9befc9c5-2e03-4c93-b767-e1e8b1f6c082','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','AA2','Lemon Stop','TEKNIK',1,2,'2026-05-14 06:26:49','2026-05-14 06:26:49'),('a0426a02-37ac-4aef-b3c3-4ad74ef487d3','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','AA1','Lemon Push','FISIK',1,1,'2026-05-14 04:59:47','2026-05-14 04:59:47'),('bee4cec0-fe26-4ba5-8e09-923d6c868c09','32965d2c-d643-4175-9dae-6f692784fb3f','BB1','Zigzag','TEKNIK',1,1,'2026-05-15 16:20:29','2026-05-15 16:20:29'),('dad21853-3bf0-4911-b138-9bcf097197be','d9bfe1d7-e501-4381-9ae6-46ee0263090b','AA1','Stepping Stabil','FISIK',1,1,'2026-05-14 07:01:11','2026-05-14 07:01:11');
/*!40000 ALTER TABLE `progress_template_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progress_templates`
--

DROP TABLE IF EXISTS `progress_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `progress_templates` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_progress_templates_level_name` (`level_uuid`,`name`),
  CONSTRAINT `fk_progress_templates_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progress_templates`
--

LOCK TABLES `progress_templates` WRITE;
/*!40000 ALTER TABLE `progress_templates` DISABLE KEYS */;
INSERT INTO `progress_templates` VALUES ('32965d2c-d643-4175-9dae-6f692784fb3f','b19d7d32-35b9-11f1-a56d-002b671d8831','Beta - Naik Level','Ini Form ujian Naik Level Beta - Gamma',1,'2026-05-15 16:19:56','2026-05-15 16:19:56'),('4bbd10a2-a77b-4dec-8ca5-fce5012064fc','b19d5454-35b9-11f1-a56d-002b671d8831','Ujian Kenaikan','',1,'2026-05-14 04:59:30','2026-05-14 04:59:30'),('965a5edb-ae4b-47b3-971c-dd54ec1350c3','a539ae38-7527-45aa-84f0-19d9c9ae91a8','Sigma Test','bagus',1,'2026-05-14 13:43:42','2026-05-14 13:43:42'),('d9bfe1d7-e501-4381-9ae6-46ee0263090b','b19e7e38-35b9-11f1-a56d-002b671d8831','TEST','',1,'2026-05-14 07:00:55','2026-05-14 07:00:55');
/*!40000 ALTER TABLE `progress_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_modules`
--

DROP TABLE IF EXISTS `role_modules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_modules` (
  `uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `role_uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `module_uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `can_view` tinyint(1) NOT NULL DEFAULT '1',
  `can_create` tinyint(1) NOT NULL DEFAULT '0',
  `can_update` tinyint(1) NOT NULL DEFAULT '0',
  `can_delete` tinyint(1) NOT NULL DEFAULT '0',
  `can_export` tinyint(1) NOT NULL DEFAULT '0',
  `can_import` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_role_modules_role_module` (`role_uuid`,`module_uuid`),
  KEY `fk_role_modules_module_uuid` (`module_uuid`),
  CONSTRAINT `fk_role_modules_module_uuid` FOREIGN KEY (`module_uuid`) REFERENCES `modules` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_modules_role_uuid` FOREIGN KEY (`role_uuid`) REFERENCES `roles` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_modules`
--

LOCK TABLES `role_modules` WRITE;
/*!40000 ALTER TABLE `role_modules` DISABLE KEYS */;
INSERT INTO `role_modules` VALUES ('63df2623-35aa-11f1-a56d-002b671d8831','d30fc8bd-35a1-11f1-a56d-002b671d8831','d31030b2-35a1-11f1-a56d-002b671d8831',1,0,0,0,0,0,'2026-04-11 13:28:56','2026-04-11 13:28:56'),('69e1aba2-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d31030b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e24cee-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d31041b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e2994b-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d3105333-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e2d6b8-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d310697c-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e307af-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d31078f1-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e33119-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','96ee31de-35c5-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e361be-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','6e62f4df-4066-11f1-8f98-002b671d8831',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e3aee8-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','0a5941fe-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e3f9a6-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','eb498562-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e43a70-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','eb4a1fcd-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e47ff8-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','eb4a8785-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e4bca1-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','eb4ae10d-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e507aa-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','a1669671-4866-11f1-b8d2-f28d797ea5d6',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e561ef-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','93579e9b-4f4f-11f1-b168-521f29b6c773',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e5bd90-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','946a26a8-4f5d-11f1-b168-521f29b6c773',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e5fdfc-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d5b21e5a-4f5a-11f1-b168-521f29b6c773',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('69e64ba0-5072-11f1-bb85-0e17646df36c','c97e238b-9c13-43fc-9c6e-0b845c290c93','d5b2e661-4f5a-11f1-b168-521f29b6c773',1,1,1,1,1,1,'2026-05-15 15:26:15','2026-05-15 15:26:15'),('946ab9af-4f5d-11f1-b168-521f29b6c773','f16118fa-9902-4d7c-b2c2-34faaae93870','946a26a8-4f5d-11f1-b168-521f29b6c773',1,0,0,0,0,0,'2026-05-14 06:24:36','2026-05-14 06:24:36'),('d5b47808-4f5a-11f1-b168-521f29b6c773','f16118fa-9902-4d7c-b2c2-34faaae93870','d5b21e5a-4f5a-11f1-b168-521f29b6c773',1,0,1,0,0,0,'2026-05-14 06:04:57','2026-05-14 06:04:57'),('d5b4cd1e-4f5a-11f1-b168-521f29b6c773','d30fc8bd-35a1-11f1-a56d-002b671d8831','d5b2e661-4f5a-11f1-b168-521f29b6c773',1,0,0,0,0,0,'2026-05-14 06:04:57','2026-05-14 06:04:57'),('e91a249c-4f53-11f1-b168-521f29b6c773','f16118fa-9902-4d7c-b2c2-34faaae93870','d31030b2-35a1-11f1-a56d-002b671d8831',1,0,0,0,0,0,'2026-05-14 05:15:23','2026-05-14 05:15:23'),('e91b2895-4f53-11f1-b168-521f29b6c773','f16118fa-9902-4d7c-b2c2-34faaae93870','0a5941fe-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-14 05:15:23','2026-05-14 05:15:23'),('f8613895-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','d31030b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f862afee-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','d31041b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f862e810-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','96ee31de-35c5-11f1-a56d-002b671d8831',1,0,0,0,0,0,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f8632b30-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','6e62f4df-4066-11f1-8f98-002b671d8831',1,0,0,0,0,0,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f8636dd1-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','eb498562-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f863b222-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','eb4a1fcd-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f864285a-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','eb4a8785-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f8647357-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','eb4ae10d-476d-11f1-844f-3e98f7c39a3e',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f864a315-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','a1669671-4866-11f1-b8d2-f28d797ea5d6',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f864cc65-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','93579e9b-4f4f-11f1-b168-521f29b6c773',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30'),('f864f445-507a-11f1-bb85-0e17646df36c','97b8173f-c9a4-44d7-9078-e8196a896cb0','d5b21e5a-4f5a-11f1-b168-521f29b6c773',1,1,1,1,1,1,'2026-05-15 16:27:30','2026-05-15 16:27:30');
/*!40000 ALTER TABLE `role_modules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `code` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('97b8173f-c9a4-44d7-9078-e8196a896cb0','97b8173f-c9a4-44d7-9078-e8196a896cb0','Admin','Staff untuk Administrasi',6,'2026-04-11 13:01:32','2026-04-11 13:01:32'),('c97e238b-9c13-43fc-9c6e-0b845c290c93','c97e238b-9c13-43fc-9c6e-0b845c290c93','Owner','Owner Perusahaan',4,'2026-04-11 12:39:10','2026-04-11 12:39:10'),('d30fc8bd-35a1-11f1-a56d-002b671d8831','d30fc8bd-35a1-11f1-a56d-002b671d8831','Murid','Akun murid yang dibuat melalui halaman register.',3,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('f16118fa-9902-4d7c-b2c2-34faaae93870','f16118fa-9902-4d7c-b2c2-34faaae93870','Pelatih','Pelatih Sepatu roda',5,'2026-04-11 12:41:14','2026-04-11 12:41:14');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_migrations`
--

DROP TABLE IF EXISTS `schema_migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schema_migrations` (
  `version` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_migrations`
--

LOCK TABLES `schema_migrations` WRITE;
/*!40000 ALTER TABLE `schema_migrations` DISABLE KEYS */;
INSERT INTO `schema_migrations` VALUES ('V1','initial_schema','2026-03-28 06:25:09'),('V10','role_module_permissions','2026-04-11 13:27:41'),('V11','user_levels_uuid_relation','2026-04-11 15:18:29'),('V12','add_level_module_to_nav_access','2026-04-11 16:43:38'),('V13','add_grade_module_and_level_grade_relation','2026-04-25 05:20:10'),('V14','add_master_peralatan_table','2026-05-04 10:29:22'),('V15','add_attendance_module_and_table','2026-05-04 11:01:23'),('V16','add_attendance_forms_for_coach_checklist_flow','2026-05-04 11:01:25'),('V17','refine_attendance_form_flow_and_add_master_modules','2026-05-04 11:01:25'),('V18','add_grade_relation_to_users','2026-05-05 09:19:00'),('V19','add_master_pembayaran_module_and_tables','2026-05-05 16:41:46'),('V2','roles_modules','2026-03-28 06:25:09'),('V20','add_coach_salary_payments','2026-05-10 19:46:44'),('V21','add_master_progress_murid_module','2026-05-14 11:44:22'),('V22','add_category_to_progress_template_items','2026-05-14 12:13:22'),('V23','split_progress_access_for_admin_coach_student','2026-05-14 13:04:57'),('V24','add_progress_assessment_history_and_coach_equipment','2026-05-14 13:24:36'),('V25','restore_role_module_and_coach_grade_progress_scope','2026-05-14 13:58:41'),('V26','sync_user_grade_from_level','2026-05-14 20:30:51'),('V3','uuid_foundation','2026-03-28 06:32:19'),('V4','super_admin_and_management_pages','2026-03-28 11:01:20'),('V5','user_role_relation_cleanup','2026-03-28 11:29:31'),('V6','rename_role_module_to_role','2026-03-28 11:44:53'),('V7','uuid_relations_and_murid_role','2026-04-11 12:12:28'),('V8','rebuild_uuid_primary_schema','2026-04-11 12:27:37'),('V9','restore_superadmin_role_access','2026-04-11 12:37:02');
/*!40000 ALTER TABLE `schema_migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_payments`
--

DROP TABLE IF EXISTS `student_payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_payments` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `murid_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `grade_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `payment_year` int NOT NULL,
  `payment_month` tinyint NOT NULL,
  `spp_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `is_paid` tinyint(1) NOT NULL DEFAULT '0',
  `paid_at` timestamp NULL DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_student_payments_period` (`murid_uuid`,`payment_year`,`payment_month`),
  KEY `fk_student_payments_grade_uuid` (`grade_uuid`),
  KEY `fk_student_payments_level_uuid` (`level_uuid`),
  CONSTRAINT `fk_student_payments_grade_uuid` FOREIGN KEY (`grade_uuid`) REFERENCES `grades` (`uuid`),
  CONSTRAINT `fk_student_payments_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`),
  CONSTRAINT `fk_student_payments_murid_uuid` FOREIGN KEY (`murid_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_payments`
--

LOCK TABLES `student_payments` WRITE;
/*!40000 ALTER TABLE `student_payments` DISABLE KEYS */;
INSERT INTO `student_payments` VALUES ('17dac70f-4e67-11f1-b077-bab4cd8b914d','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','6e4d58df-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dacbab-4e67-11f1-b077-bab4cd8b914d','13a3c7d4-dce8-4500-91ab-35a79c601f45','6e4d27f9-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dacc5b-4e67-11f1-b077-bab4cd8b914d','1a6a95a2-fd89-46cc-9de8-4c7b8c097271','6e4d27f9-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daccdd-4e67-11f1-b077-bab4cd8b914d','33f63f0d-7bc0-4d2a-acf3-360d67a11049','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,11,50000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dace83-4e67-11f1-b077-bab4cd8b914d','62ed7565-e339-489a-8811-4f16109a6a8e','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,11,50000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dacf1a-4e67-11f1-b077-bab4cd8b914d','81af22aa-fcf1-484d-a130-cfa236e221a9','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,11,50000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dacf89-4e67-11f1-b077-bab4cd8b914d','8684594e-c0d3-4efd-9b09-accb95a5858f','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,11,50000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dacffe-4e67-11f1-b077-bab4cd8b914d','8af9d17b-40be-44b4-a325-d910ba071c24','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,11,10000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dad149-4e67-11f1-b077-bab4cd8b914d','90fc286a-784c-4e91-972b-08168aa008fc','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,11,10000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17dae92d-4e67-11f1-b077-bab4cd8b914d','ac3ef9e4-c903-4c43-b60e-e8446d0820f7','6e4d27f9-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daea0e-4e67-11f1-b077-bab4cd8b914d','b449567e-9d98-42bc-8bdc-19ca9f394f1e','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,11,10000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daea8a-4e67-11f1-b077-bab4cd8b914d','b5bc308b-cffc-46d8-8a53-0a515a97e6de','6e4d27f9-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daebfa-4e67-11f1-b077-bab4cd8b914d','be4a9fea-8c22-4de1-9d18-37c56bbcc07b','6e4d58df-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daecb4-4e67-11f1-b077-bab4cd8b914d','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','6e4d58df-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daed40-4e67-11f1-b077-bab4cd8b914d','d8980455-34bf-49aa-84ef-8ba0c3b2576c','6e4d27f9-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daedb6-4e67-11f1-b077-bab4cd8b914d','d9dfda19-4ce0-4309-87ba-a397e5a3acf4','6e4d27f9-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,11,0.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('17daef00-4e67-11f1-b077-bab4cd8b914d','e427197b-e050-4ebf-b403-0d1e8ba525fa','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,11,50000.00,0,NULL,'','2026-05-13 01:00:11','2026-05-13 01:00:11'),('1bd39e00-4e67-11f1-b077-bab4cd8b914d','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','6e4d58df-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3a765-4e67-11f1-b077-bab4cd8b914d','13a3c7d4-dce8-4500-91ab-35a79c601f45','6e4d27f9-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3a95b-4e67-11f1-b077-bab4cd8b914d','1a6a95a2-fd89-46cc-9de8-4c7b8c097271','6e4d27f9-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3aa6d-4e67-11f1-b077-bab4cd8b914d','33f63f0d-7bc0-4d2a-acf3-360d67a11049','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,12,50000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3abde-4e67-11f1-b077-bab4cd8b914d','62ed7565-e339-489a-8811-4f16109a6a8e','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,12,50000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3acda-4e67-11f1-b077-bab4cd8b914d','81af22aa-fcf1-484d-a130-cfa236e221a9','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,12,50000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3ad9b-4e67-11f1-b077-bab4cd8b914d','8684594e-c0d3-4efd-9b09-accb95a5858f','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,12,50000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3af61-4e67-11f1-b077-bab4cd8b914d','8af9d17b-40be-44b4-a325-d910ba071c24','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,12,10000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b039-4e67-11f1-b077-bab4cd8b914d','90fc286a-784c-4e91-972b-08168aa008fc','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,12,10000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b10f-4e67-11f1-b077-bab4cd8b914d','ac3ef9e4-c903-4c43-b60e-e8446d0820f7','6e4d27f9-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b230-4e67-11f1-b077-bab4cd8b914d','b449567e-9d98-42bc-8bdc-19ca9f394f1e','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,12,10000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b309-4e67-11f1-b077-bab4cd8b914d','b5bc308b-cffc-46d8-8a53-0a515a97e6de','6e4d27f9-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b3e7-4e67-11f1-b077-bab4cd8b914d','be4a9fea-8c22-4de1-9d18-37c56bbcc07b','6e4d58df-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b4d0-4e67-11f1-b077-bab4cd8b914d','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','6e4d58df-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b5ac-4e67-11f1-b077-bab4cd8b914d','d8980455-34bf-49aa-84ef-8ba0c3b2576c','6e4d27f9-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b66a-4e67-11f1-b077-bab4cd8b914d','d9dfda19-4ce0-4309-87ba-a397e5a3acf4','6e4d27f9-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,12,0.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('1bd3b73d-4e67-11f1-b077-bab4cd8b914d','e427197b-e050-4ebf-b403-0d1e8ba525fa','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,12,50000.00,0,NULL,'','2026-05-13 01:00:18','2026-05-13 01:00:18'),('3e735bce-5077-11f1-bb85-0e17646df36c','75f61c4b-56ef-49d0-8ce4-a650395c3ee7','6e4dfa2a-4066-11f1-8f98-002b671d8831','3c9ae974-2fe3-47e5-81ac-8707e67978a5',2026,5,0.00,0,NULL,'','2026-05-15 16:00:50','2026-05-15 16:00:50'),('f2b8c053-489c-11f1-aa1c-ae84ceacffdd','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,5,70000.00,1,'2026-05-15 23:12:41','','2026-05-05 16:10:34','2026-05-15 16:12:41'),('f2b8d0d5-489c-11f1-aa1c-ae84ceacffdd','13a3c7d4-dce8-4500-91ab-35a79c601f45','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,5,90000.00,1,'2026-05-10 20:56:34','','2026-05-05 16:10:34','2026-05-15 16:09:28'),('f2b8d278-489c-11f1-aa1c-ae84ceacffdd','1a6a95a2-fd89-46cc-9de8-4c7b8c097271','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,5,90000.00,1,'2026-05-10 20:56:35','','2026-05-05 16:10:34','2026-05-15 16:09:28'),('f2b8d3ab-489c-11f1-aa1c-ae84ceacffdd','33f63f0d-7bc0-4d2a-acf3-360d67a11049','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,5,50000.00,0,NULL,'','2026-05-05 16:10:34','2026-05-14 13:31:03'),('f2b8d62a-489c-11f1-aa1c-ae84ceacffdd','62ed7565-e339-489a-8811-4f16109a6a8e','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,5,70000.00,1,'2026-05-15 23:12:40','','2026-05-05 16:10:34','2026-05-15 16:12:39'),('f2b8d6db-489c-11f1-aa1c-ae84ceacffdd','81af22aa-fcf1-484d-a130-cfa236e221a9','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,5,50000.00,1,'2026-05-10 20:56:33','','2026-05-05 16:10:34','2026-05-14 13:31:03'),('f2b8d74d-489c-11f1-aa1c-ae84ceacffdd','8684594e-c0d3-4efd-9b09-accb95a5858f','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,5,50000.00,1,'2026-05-15 23:12:14','','2026-05-05 16:10:34','2026-05-15 16:12:13'),('f2b8d7cd-489c-11f1-aa1c-ae84ceacffdd','8af9d17b-40be-44b4-a325-d910ba071c24','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,5,80000.00,1,'2026-05-15 23:12:49','','2026-05-05 16:10:34','2026-05-15 16:12:49'),('f2b8d9ea-489c-11f1-aa1c-ae84ceacffdd','90fc286a-784c-4e91-972b-08168aa008fc','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,5,80000.00,0,NULL,'','2026-05-05 16:10:34','2026-05-15 16:09:18'),('f2b8da7f-489c-11f1-aa1c-ae84ceacffdd','ac3ef9e4-c903-4c43-b60e-e8446d0820f7','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',2026,5,90000.00,1,'2026-05-10 20:56:34','','2026-05-05 16:10:34','2026-05-15 16:09:28'),('f2b8dc9b-489c-11f1-aa1c-ae84ceacffdd','b449567e-9d98-42bc-8bdc-19ca9f394f1e','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,5,80000.00,0,NULL,'Nunggak 10 bulan','2026-05-05 16:10:34','2026-05-15 16:13:02'),('f2b8ddc4-489c-11f1-aa1c-ae84ceacffdd','b5bc308b-cffc-46d8-8a53-0a515a97e6de','6e4d58df-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,5,100000.00,1,'2026-05-10 20:56:34','','2026-05-05 16:10:34','2026-05-15 16:09:50'),('f2b8de67-489c-11f1-aa1c-ae84ceacffdd','be4a9fea-8c22-4de1-9d18-37c56bbcc07b','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',2026,5,80000.00,1,'2026-05-15 23:12:50','','2026-05-05 16:10:34','2026-05-15 16:12:49'),('f2b8dfdf-489c-11f1-aa1c-ae84ceacffdd','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',2026,5,70000.00,1,'2026-05-15 23:12:49','','2026-05-05 16:10:34','2026-05-15 16:12:48'),('f2b8e09d-489c-11f1-aa1c-ae84ceacffdd','d8980455-34bf-49aa-84ef-8ba0c3b2576c','6e4d58df-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,5,100000.00,1,'2026-05-10 20:56:33','','2026-05-05 16:10:34','2026-05-15 16:09:50'),('f2b8e10d-489c-11f1-aa1c-ae84ceacffdd','d9dfda19-4ce0-4309-87ba-a397e5a3acf4','6e4d58df-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',2026,5,100000.00,1,'2026-05-10 20:56:32','','2026-05-05 16:10:34','2026-05-15 16:09:50'),('f2b8e18d-489c-11f1-aa1c-ae84ceacffdd','e427197b-e050-4ebf-b403-0d1e8ba525fa','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',2026,5,50000.00,0,NULL,'','2026-05-05 16:10:34','2026-05-14 13:31:03');
/*!40000 ALTER TABLE `student_payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_progress_assessments`
--

DROP TABLE IF EXISTS `student_progress_assessments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_progress_assessments` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `murid_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `coach_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `template_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `assessment_date` date NOT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  KEY `fk_student_progress_assessments_murid_uuid` (`murid_uuid`),
  KEY `fk_student_progress_assessments_coach_uuid` (`coach_uuid`),
  KEY `fk_student_progress_assessments_template_uuid` (`template_uuid`),
  CONSTRAINT `fk_student_progress_assessments_coach_uuid` FOREIGN KEY (`coach_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_student_progress_assessments_murid_uuid` FOREIGN KEY (`murid_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_student_progress_assessments_template_uuid` FOREIGN KEY (`template_uuid`) REFERENCES `progress_templates` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_progress_assessments`
--

LOCK TABLES `student_progress_assessments` WRITE;
/*!40000 ALTER TABLE `student_progress_assessments` DISABLE KEYS */;
INSERT INTO `student_progress_assessments` VALUES ('0279a71d-e923-4773-85a4-b00d95939a1f','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','Beta - Naik Level - Beta Class','2026-05-15','','2026-05-15 16:23:48','2026-05-15 16:23:48'),('059e8745-969a-4b6d-bde4-7a67ecf5953e','81af22aa-fcf1-484d-a130-cfa236e221a9','d311cef3-35a1-11f1-a56d-002b671d8831','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','Ujian Kenaikan - Alpha Class','2026-05-14','','2026-05-14 06:39:49','2026-05-14 06:39:49'),('1f83e8c7-1270-4ec6-8299-464dbc30a8d4','62ed7565-e339-489a-8811-4f16109a6a8e','d311cef3-35a1-11f1-a56d-002b671d8831','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','Ujian Kenaikan - Alpha Class','2026-05-14','','2026-05-14 06:39:23','2026-05-14 06:39:23'),('44ef37da-33a6-4b0b-ac74-0787b0bc2bf2','62ed7565-e339-489a-8811-4f16109a6a8e','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','Beta - Naik Level - Beta Class','2026-05-15','','2026-05-15 16:22:18','2026-05-15 16:22:18'),('601f8eda-9841-4dc0-90cf-a7b32a486b89','1a6a95a2-fd89-46cc-9de8-4c7b8c097271','392938c3-efff-4f65-b31f-20360c4d63d5','965a5edb-ae4b-47b3-971c-dd54ec1350c3','Sigma Test - Sigma Class','2026-05-15','','2026-05-15 16:28:29','2026-05-15 16:28:29'),('8d33c444-5506-4f56-bb3b-03381764d09c','e427197b-e050-4ebf-b403-0d1e8ba525fa','d2f54458-126b-4913-97ab-78a7fadc752b','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','Ujian Kenaikan - Alpha Class','2026-05-14','','2026-05-14 13:08:22','2026-05-14 13:08:22'),('a7c7c71e-af8d-43ba-8c75-ebfae22630ae','8684594e-c0d3-4efd-9b09-accb95a5858f','d2f54458-126b-4913-97ab-78a7fadc752b','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','Ujian Kenaikan - Alpha Class','2026-05-14','','2026-05-14 13:08:24','2026-05-14 13:08:24'),('aedab6c4-820b-4aaf-846e-fdf4a636e4b1','ac3ef9e4-c903-4c43-b60e-e8446d0820f7','6764381e-7db8-45fe-ac26-ebbce8b52259','965a5edb-ae4b-47b3-971c-dd54ec1350c3','Sigma Test - Sigma Class','2026-05-14','','2026-05-14 13:47:15','2026-05-14 13:47:15'),('b2c4415a-e7fb-4554-b57f-9fe21af57e14','33f63f0d-7bc0-4d2a-acf3-360d67a11049','d311cef3-35a1-11f1-a56d-002b671d8831','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','Ujian Kenaikan - Alpha Class','2026-05-14','','2026-05-14 06:39:21','2026-05-14 06:39:21'),('e07591ad-08d4-4581-a29f-069daa5ce755','8af9d17b-40be-44b4-a325-d910ba071c24','5a0008c3-987f-48f9-b738-00af8bd79f94','d9bfe1d7-e501-4381-9ae6-46ee0263090b','TEST - Gamma Class','2026-05-14','','2026-05-14 13:49:17','2026-05-14 13:49:17'),('f45ff492-e70c-49e9-9ade-bb4b6e9a1c4d','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','Beta - Naik Level - Beta Class','2026-05-15','','2026-05-15 16:23:42','2026-05-15 16:23:42');
/*!40000 ALTER TABLE `student_progress_assessments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_progress_records`
--

DROP TABLE IF EXISTS `student_progress_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_progress_records` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `assessment_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `murid_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `coach_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `template_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `item_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_passed` tinyint(1) NOT NULL DEFAULT '0',
  `checked_at` timestamp NULL DEFAULT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uq_student_progress_assessment_item` (`assessment_uuid`,`item_uuid`),
  KEY `fk_student_progress_coach_uuid` (`coach_uuid`),
  KEY `fk_student_progress_template_uuid` (`template_uuid`),
  KEY `fk_student_progress_item_uuid` (`item_uuid`),
  KEY `idx_student_progress_murid_uuid` (`murid_uuid`),
  CONSTRAINT `fk_student_progress_assessment_uuid` FOREIGN KEY (`assessment_uuid`) REFERENCES `student_progress_assessments` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_student_progress_coach_uuid` FOREIGN KEY (`coach_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_student_progress_item_uuid` FOREIGN KEY (`item_uuid`) REFERENCES `progress_template_items` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_student_progress_murid_uuid` FOREIGN KEY (`murid_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE,
  CONSTRAINT `fk_student_progress_template_uuid` FOREIGN KEY (`template_uuid`) REFERENCES `progress_templates` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_progress_records`
--

LOCK TABLES `student_progress_records` WRITE;
/*!40000 ALTER TABLE `student_progress_records` DISABLE KEYS */;
INSERT INTO `student_progress_records` VALUES ('02e942e2-e433-4bb7-a2f9-438f87ff2757','0279a71d-e923-4773-85a4-b00d95939a1f','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','4d856ee6-b0cf-4d90-b766-093d6becea80',0,NULL,'-','2026-05-15 16:23:50','2026-05-15 16:23:50'),('383fcdac-7ad3-454e-8108-7cfa60df6830','44ef37da-33a6-4b0b-ac74-0787b0bc2bf2','62ed7565-e339-489a-8811-4f16109a6a8e','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','4d856ee6-b0cf-4d90-b766-093d6becea80',1,'2026-05-15 16:23:29','-','2026-05-15 16:23:29','2026-05-15 16:23:29'),('3d4d114a-97c7-45da-a142-77706d442c54','b2c4415a-e7fb-4554-b57f-9fe21af57e14','33f63f0d-7bc0-4d2a-acf3-360d67a11049','c20235d4-c99a-42e8-8a18-57170e19dd4c','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','9befc9c5-2e03-4c93-b767-e1e8b1f6c082',1,'2026-05-14 14:04:50','-','2026-05-14 14:04:20','2026-05-14 14:04:50'),('43ec4970-5237-4528-be12-e32537a11f92','b2c4415a-e7fb-4554-b57f-9fe21af57e14','33f63f0d-7bc0-4d2a-acf3-360d67a11049','c20235d4-c99a-42e8-8a18-57170e19dd4c','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','810907ed-2c5b-4f52-99ba-6568b27ef820',1,'2026-05-14 14:04:50','-','2026-05-14 14:04:20','2026-05-14 14:04:50'),('57f4f613-aa17-4bb8-a1c2-cfd035b5e8c1','44ef37da-33a6-4b0b-ac74-0787b0bc2bf2','62ed7565-e339-489a-8811-4f16109a6a8e','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','bee4cec0-fe26-4ba5-8e09-923d6c868c09',1,'2026-05-15 16:23:29','-','2026-05-15 16:23:29','2026-05-15 16:23:29'),('5c8f769f-3b48-409d-a348-790844cb26e1','0279a71d-e923-4773-85a4-b00d95939a1f','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','2b717d1e-448f-43ab-9d39-fd5afff1f2e7',0,NULL,'-','2026-05-15 16:23:50','2026-05-15 16:23:50'),('6d715892-5994-4249-9f67-ae4a31e0457d','44ef37da-33a6-4b0b-ac74-0787b0bc2bf2','62ed7565-e339-489a-8811-4f16109a6a8e','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','2b717d1e-448f-43ab-9d39-fd5afff1f2e7',0,NULL,'-','2026-05-15 16:23:29','2026-05-15 16:23:29'),('6db7adbe-e185-42bf-90e9-1302127b5706','1f83e8c7-1270-4ec6-8299-464dbc30a8d4','62ed7565-e339-489a-8811-4f16109a6a8e','c20235d4-c99a-42e8-8a18-57170e19dd4c','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','a0426a02-37ac-4aef-b3c3-4ad74ef487d3',1,'2026-05-14 14:05:36','-','2026-05-14 14:05:34','2026-05-14 14:05:36'),('6e926f72-8308-403f-8187-378dfd5bd1e0','f45ff492-e70c-49e9-9ade-bb4b6e9a1c4d','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','bee4cec0-fe26-4ba5-8e09-923d6c868c09',1,'2026-05-15 16:23:45','-','2026-05-15 16:23:45','2026-05-15 16:23:45'),('a6fb385c-f8b0-4d72-806d-7f7fd1bb7798','f45ff492-e70c-49e9-9ade-bb4b6e9a1c4d','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','4d856ee6-b0cf-4d90-b766-093d6becea80',1,'2026-05-15 16:23:45','-','2026-05-15 16:23:45','2026-05-15 16:23:45'),('b92b967c-35df-4623-a379-8ae7bbe9f4ff','e07591ad-08d4-4581-a29f-069daa5ce755','8af9d17b-40be-44b4-a325-d910ba071c24','5a0008c3-987f-48f9-b738-00af8bd79f94','d9bfe1d7-e501-4381-9ae6-46ee0263090b','dad21853-3bf0-4911-b138-9bcf097197be',1,'2026-05-14 13:49:41','-','2026-05-14 13:49:41','2026-05-14 13:49:41'),('be7b9ca4-b987-4e8e-a480-08ede18e8425','1f83e8c7-1270-4ec6-8299-464dbc30a8d4','62ed7565-e339-489a-8811-4f16109a6a8e','c20235d4-c99a-42e8-8a18-57170e19dd4c','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','810907ed-2c5b-4f52-99ba-6568b27ef820',1,'2026-05-14 14:05:36','-','2026-05-14 14:05:34','2026-05-14 14:05:36'),('cf522b2d-c3ca-4f94-833d-0f16859ceabb','1f83e8c7-1270-4ec6-8299-464dbc30a8d4','62ed7565-e339-489a-8811-4f16109a6a8e','c20235d4-c99a-42e8-8a18-57170e19dd4c','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','9befc9c5-2e03-4c93-b767-e1e8b1f6c082',1,'2026-05-14 14:05:36','-','2026-05-14 14:05:34','2026-05-14 14:05:36'),('dadbd9d6-6d40-45ff-ab52-43de5e4345d0','f45ff492-e70c-49e9-9ade-bb4b6e9a1c4d','08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','2b717d1e-448f-43ab-9d39-fd5afff1f2e7',1,'2026-05-15 16:23:45','-','2026-05-15 16:23:45','2026-05-15 16:23:45'),('dcc5e4f8-eda4-4b97-a1c8-bd47b68aefe1','b2c4415a-e7fb-4554-b57f-9fe21af57e14','33f63f0d-7bc0-4d2a-acf3-360d67a11049','c20235d4-c99a-42e8-8a18-57170e19dd4c','4bbd10a2-a77b-4dec-8ca5-fce5012064fc','a0426a02-37ac-4aef-b3c3-4ad74ef487d3',1,'2026-05-14 14:04:50','-','2026-05-14 14:04:20','2026-05-14 14:04:50'),('eb1c55e2-9084-483c-bf7d-0ba6e88ca6d3','e07591ad-08d4-4581-a29f-069daa5ce755','8af9d17b-40be-44b4-a325-d910ba071c24','5a0008c3-987f-48f9-b738-00af8bd79f94','d9bfe1d7-e501-4381-9ae6-46ee0263090b','3e312b6e-7bf8-4f51-893e-f478a8b2f3cf',1,'2026-05-14 13:49:41','-','2026-05-14 13:49:41','2026-05-14 13:49:41'),('ff000f66-f881-46f2-a19c-8ac418a69b6b','0279a71d-e923-4773-85a4-b00d95939a1f','c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','acc751af-eb0e-4f22-a7ea-4a26b84df271','32965d2c-d643-4175-9dae-6f692784fb3f','bee4cec0-fe26-4ba5-8e09-923d6c868c09',1,'2026-05-15 16:23:50','-','2026-05-15 16:23:50','2026-05-15 16:23:50');
/*!40000 ALTER TABLE `student_progress_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `role_uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `grade_uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level_uuid` char(36) COLLATE utf8mb4_general_ci NOT NULL,
  `is_super_admin` tinyint(1) NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `last_login_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_users_role_uuid` (`role_uuid`),
  KEY `idx_users_level_uuid` (`level_uuid`),
  KEY `idx_users_grade_uuid` (`grade_uuid`),
  CONSTRAINT `fk_users_grade_uuid` FOREIGN KEY (`grade_uuid`) REFERENCES `grades` (`uuid`),
  CONSTRAINT `fk_users_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`),
  CONSTRAINT `fk_users_role_uuid` FOREIGN KEY (`role_uuid`) REFERENCES `roles` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('08b618f5-67c7-41bc-a2ef-7ad1807bfa1b','baloy','baloy','baloy@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-14 20:49:47','2026-05-05 01:43:25','2026-05-14 13:49:47'),('13a3c7d4-dce8-4500-91ab-35a79c601f45','kiboy','kiboy','kiboy@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',0,1,NULL,'2026-05-05 01:41:40','2026-05-14 13:30:50'),('1a6a95a2-fd89-46cc-9de8-4c7b8c097271','sanz','sanz','sanz@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',0,1,'2026-05-15 23:17:37','2026-05-05 01:41:13','2026-05-15 16:17:36'),('33f63f0d-7bc0-4d2a-acf3-360d67a11049','alex','alex','alex@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-15 23:17:19','2026-05-05 01:40:58','2026-05-15 16:17:18'),('392938c3-efff-4f65-b31f-20360c4d63d5','Aghata','Aghata 123','aghata@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','97b8173f-c9a4-44d7-9078-e8196a896cb0','6e4dfa2a-4066-11f1-8f98-002b671d8831','3c9ae974-2fe3-47e5-81ac-8707e67978a5',0,1,'2026-05-15 23:27:35','2026-04-11 13:03:05','2026-05-15 16:27:35'),('5a0008c3-987f-48f9-b738-00af8bd79f94','nandang','nandang','nandang@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-14 21:11:41','2026-05-05 02:21:29','2026-05-14 14:11:41'),('5ed74503-df91-4953-9dd8-140a230372dd','Safrudin','Pak Boss','bos@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','c97e238b-9c13-43fc-9c6e-0b845c290c93','6e4dfa2a-4066-11f1-8f98-002b671d8831','3c9ae974-2fe3-47e5-81ac-8707e67978a5',0,1,'2026-05-15 23:29:17','2026-04-11 12:43:35','2026-05-15 16:29:17'),('62ed7565-e339-489a-8811-4f16109a6a8e','arfy','arfy','arfy@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-15 23:30:24','2026-05-05 01:40:25','2026-05-15 16:30:24'),('6764381e-7db8-45fe-ac26-ebbce8b52259','Ari','Ari','ari@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',0,1,'2026-05-15 23:29:25','2026-05-05 02:20:35','2026-05-15 16:29:24'),('75f61c4b-56ef-49d0-8ce4-a650395c3ee7','Prabowo Sbs','prabowo update','prabowo@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4dfa2a-4066-11f1-8f98-002b671d8831','3c9ae974-2fe3-47e5-81ac-8707e67978a5',0,1,NULL,'2026-05-15 15:35:30','2026-05-15 15:36:25'),('81af22aa-fcf1-484d-a130-cfa236e221a9','Nino','nino','nino@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',0,1,NULL,'2026-05-05 01:39:24','2026-05-14 13:30:50'),('8684594e-c0d3-4efd-9b09-accb95a5858f','M Rofi','Rofi Udin Update di master','rofi@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',0,1,NULL,'2026-04-11 12:52:23','2026-05-14 13:30:50'),('8af9d17b-40be-44b4-a325-d910ba071c24','reza','reza','reza@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,NULL,'2026-05-05 01:38:13','2026-05-14 13:30:50'),('90fc286a-784c-4e91-972b-08168aa008fc','Zae','Zae','zae@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,NULL,'2026-05-05 01:37:52','2026-05-14 13:30:50'),('94b44189-f22b-424e-a8a5-3401f45b5177','Coach Yeb','Yeb','yeb@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',0,1,'2026-05-15 23:29:30','2026-05-15 15:48:06','2026-05-15 16:29:30'),('ac3ef9e4-c903-4c43-b60e-e8446d0820f7','kairi','kairi','kairi@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d58df-4066-11f1-8f98-002b671d8831','a539ae38-7527-45aa-84f0-19d9c9ae91a8',0,1,'2026-05-15 23:06:46','2026-05-05 01:41:27','2026-05-15 16:06:45'),('acc751af-eb0e-4f22-a7ea-4a26b84df271','Jokowi','Jokowi','jokowi@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-15 23:23:40','2026-05-10 13:20:22','2026-05-15 16:23:40'),('b449567e-9d98-42bc-8bdc-19ca9f394f1e','Udin','udin','udin@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-15 22:00:21','2026-05-05 02:20:49','2026-05-15 15:00:20'),('b5bc308b-cffc-46d8-8a53-0a515a97e6de','Skylar','Skylar','skylar@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d58df-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',0,1,NULL,'2026-05-05 01:43:01','2026-05-14 13:30:50'),('be4a9fea-8c22-4de1-9d18-37c56bbcc07b','Rezaldy Abidin','rezaldy abidin','rezaldy266@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-14 13:40:01','2026-04-11 12:43:09','2026-05-14 13:30:50'),('c20235d4-c99a-42e8-8a18-57170e19dd4c','Rezaldy','rezaldy','rezaldy@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-15 23:16:09','2026-05-05 02:21:08','2026-05-15 16:16:09'),('c92f7cfa-b4b0-478f-a4e4-a1b3399d2fc4','maykinds','maykinds','maykinds@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d27f9-4066-11f1-8f98-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-15 23:25:38','2026-05-05 01:43:45','2026-05-15 16:25:38'),('d2f54458-126b-4913-97ab-78a7fadc752b','GammaCoach','GammaCoach','gamma@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','6e4d27f9-4066-11f1-8f98-002b671d8831','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-14 20:31:29','2026-05-14 06:44:20','2026-05-14 13:31:28'),('d311cef3-35a1-11f1-a56d-002b671d8831','Super Admin','superadmin','test1@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','c97e238b-9c13-43fc-9c6e-0b845c290c93','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',1,1,'2026-05-15 23:44:32','2026-04-11 12:27:37','2026-05-15 16:44:31'),('d8980455-34bf-49aa-84ef-8ba0c3b2576c','R7','R7','r7@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d58df-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',0,1,NULL,'2026-05-05 01:42:12','2026-05-14 13:30:50'),('d9dfda19-4ce0-4309-87ba-a397e5a3acf4','lemon','lemon','lemon@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4d58df-4066-11f1-8f98-002b671d8831','690b9b97-648a-4f59-b1ef-7c1a392dc9ea',0,1,NULL,'2026-05-05 01:42:34','2026-05-14 13:30:50'),('e427197b-e050-4ebf-b403-0d1e8ba525fa','Hijume','Hijume','hijume@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','6e4dfa2a-4066-11f1-8f98-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',0,1,'2026-05-14 13:29:04','2026-05-05 01:39:46','2026-05-14 13:30:50');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'PV_TugasBesar'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-16  9:19:32
