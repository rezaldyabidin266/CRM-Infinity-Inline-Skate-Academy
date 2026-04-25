-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: pv_tugasbesar
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grades` (
  `uuid` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `grade_value` int(11) NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
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
-- Table structure for table `levels`
--

DROP TABLE IF EXISTS `levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `levels` (
  `uuid` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `grade_uuid` char(36) NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
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
INSERT INTO `levels` VALUES ('3c9ae974-2fe3-47e5-81ac-8707e67978a5','No Level','Role ini tidak butuh level','6e4dfa2a-4066-11f1-8f98-002b671d8831',4,'2026-04-11 16:50:28','2026-04-25 05:28:53'),('690b9b97-648a-4f59-b1ef-7c1a392dc9ea','Omega Class','Grade 2nd:Classic','6e4d27f9-4066-11f1-8f98-002b671d8831',6,'2026-04-25 04:58:50','2026-04-25 05:20:10'),('a539ae38-7527-45aa-84f0-19d9c9ae91a8','Sigma Class','Grade 2nd: Classic','6e4d27f9-4066-11f1-8f98-002b671d8831',5,'2026-04-25 04:58:33','2026-04-25 05:20:10'),('b19d5454-35b9-11f1-a56d-002b671d8831','Alpha Class','Level Grade 0 Basic','6e4d27f9-4066-11f1-8f98-002b671d8831',1,'2026-04-11 15:18:29','2026-04-25 05:20:10'),('b19d7d32-35b9-11f1-a56d-002b671d8831','Beta Class','Level Grade  1st : Beginner','6e4d58df-4066-11f1-8f98-002b671d8831',2,'2026-04-11 15:18:29','2026-04-25 05:20:10'),('b19e7e38-35b9-11f1-a56d-002b671d8831','Gamma Class','Grade 1st: Beginner','6e4dfa2a-4066-11f1-8f98-002b671d8831',3,'2026-04-11 15:18:29','2026-04-25 05:20:10'),('f36165d3-8838-414c-bcb8-8def8c1a21a7','Basic','Pelatih level basic','6e4d27f9-4066-11f1-8f98-002b671d8831',7,'2026-04-25 05:07:59','2026-04-25 05:20:10');
/*!40000 ALTER TABLE `levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modules`
--

DROP TABLE IF EXISTS `modules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modules` (
  `uuid` char(36) NOT NULL,
  `code` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
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
INSERT INTO `modules` VALUES ('6e62f4df-4066-11f1-8f98-002b671d8831','6e62f4ef-4066-11f1-8f98-002b671d8831','Grade','Kelola grade untuk klasifikasi level user.',7,'2026-04-25 05:20:10','2026-04-25 05:20:10'),('96ee31de-35c5-11f1-a56d-002b671d8831','96ee31fb-35c5-11f1-a56d-002b671d8831','Level','Kelola level user dan klasifikasi pembinaan.',6,'2026-04-11 16:43:38','2026-04-11 16:43:38'),('d31030b2-35a1-11f1-a56d-002b671d8831','d31030b2-35a1-11f1-a56d-002b671d8831','Dashboard','Ringkasan utama aplikasi dan informasi umum.',1,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d31041b2-35a1-11f1-a56d-002b671d8831','d31041b2-35a1-11f1-a56d-002b671d8831','User','Manajemen data pengguna, role, status akun, dan penanda super admin.',2,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d3105333-35a1-11f1-a56d-002b671d8831','d3105333-35a1-11f1-a56d-002b671d8831','Role','Mengatur hak akses module yang tampil di navbar berdasarkan role.',3,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d310697c-35a1-11f1-a56d-002b671d8831','d310697c-35a1-11f1-a56d-002b671d8831','Laporan','Akses laporan dan rekapitulasi data.',4,'2026-04-11 12:27:37','2026-04-11 12:27:37'),('d31078f1-35a1-11f1-a56d-002b671d8831','d31078f1-35a1-11f1-a56d-002b671d8831','Pengaturan','Konfigurasi aplikasi dan preferensi sistem.',5,'2026-04-11 12:27:37','2026-04-11 12:27:37');
/*!40000 ALTER TABLE `modules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_modules`
--

DROP TABLE IF EXISTS `role_modules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_modules` (
  `uuid` char(36) NOT NULL,
  `role_uuid` char(36) NOT NULL,
  `module_uuid` char(36) NOT NULL,
  `can_view` tinyint(1) NOT NULL DEFAULT 1,
  `can_create` tinyint(1) NOT NULL DEFAULT 0,
  `can_update` tinyint(1) NOT NULL DEFAULT 0,
  `can_delete` tinyint(1) NOT NULL DEFAULT 0,
  `can_export` tinyint(1) NOT NULL DEFAULT 0,
  `can_import` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
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
INSERT INTO `role_modules` VALUES ('6326e58b-35b1-11f1-a56d-002b671d8831','f16118fa-9902-4d7c-b2c2-34faaae93870','d31030b2-35a1-11f1-a56d-002b671d8831',1,0,0,0,1,1,'2026-04-11 14:19:01','2026-04-11 14:19:01'),('6326fde6-35b1-11f1-a56d-002b671d8831','f16118fa-9902-4d7c-b2c2-34faaae93870','d310697c-35a1-11f1-a56d-002b671d8831',1,0,0,0,1,1,'2026-04-11 14:19:01','2026-04-11 14:19:01'),('63df2623-35aa-11f1-a56d-002b671d8831','d30fc8bd-35a1-11f1-a56d-002b671d8831','d31030b2-35a1-11f1-a56d-002b671d8831',1,0,0,0,0,0,'2026-04-11 13:28:56','2026-04-11 13:28:56'),('72aa0c7e-35b1-11f1-a56d-002b671d8831','97b8173f-c9a4-44d7-9078-e8196a896cb0','d31030b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-11 14:19:27','2026-04-11 14:19:27'),('72aa22a8-35b1-11f1-a56d-002b671d8831','97b8173f-c9a4-44d7-9078-e8196a896cb0','d31041b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-11 14:19:27','2026-04-11 14:19:27'),('72aa3386-35b1-11f1-a56d-002b671d8831','97b8173f-c9a4-44d7-9078-e8196a896cb0','d310697c-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-11 14:19:27','2026-04-11 14:19:27'),('a3f66bfb-404d-11f1-aa30-002b671d8831','c97e238b-9c13-43fc-9c6e-0b845c290c93','d31030b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-25 02:22:43','2026-04-25 02:22:43'),('a4043307-404d-11f1-aa30-002b671d8831','c97e238b-9c13-43fc-9c6e-0b845c290c93','d31041b2-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-25 02:22:43','2026-04-25 02:22:43'),('a40454da-404d-11f1-aa30-002b671d8831','c97e238b-9c13-43fc-9c6e-0b845c290c93','d3105333-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-25 02:22:43','2026-04-25 02:22:43'),('a40471b3-404d-11f1-aa30-002b671d8831','c97e238b-9c13-43fc-9c6e-0b845c290c93','d310697c-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-25 02:22:43','2026-04-25 02:22:43'),('a40493a9-404d-11f1-aa30-002b671d8831','c97e238b-9c13-43fc-9c6e-0b845c290c93','d31078f1-35a1-11f1-a56d-002b671d8831',1,1,1,1,1,1,'2026-04-25 02:22:43','2026-04-25 02:22:43'),('a404b936-404d-11f1-aa30-002b671d8831','c97e238b-9c13-43fc-9c6e-0b845c290c93','96ee31de-35c5-11f1-a56d-002b671d8831',1,0,0,0,0,0,'2026-04-25 02:22:43','2026-04-25 02:22:43');
/*!40000 ALTER TABLE `role_modules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `uuid` char(36) NOT NULL,
  `code` char(36) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
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
  `version` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_migrations`
--

LOCK TABLES `schema_migrations` WRITE;
/*!40000 ALTER TABLE `schema_migrations` DISABLE KEYS */;
INSERT INTO `schema_migrations` VALUES ('V1','initial_schema','2026-03-28 06:25:09'),('V10','role_module_permissions','2026-04-11 13:27:41'),('V11','user_levels_uuid_relation','2026-04-11 15:18:29'),('V12','add_level_module_to_nav_access','2026-04-11 16:43:38'),('V13','add_grade_module_and_level_grade_relation','2026-04-25 05:20:10'),('V2','roles_modules','2026-03-28 06:25:09'),('V3','uuid_foundation','2026-03-28 06:32:19'),('V4','super_admin_and_management_pages','2026-03-28 11:01:20'),('V5','user_role_relation_cleanup','2026-03-28 11:29:31'),('V6','rename_role_module_to_role','2026-03-28 11:44:53'),('V7','uuid_relations_and_murid_role','2026-04-11 12:12:28'),('V8','rebuild_uuid_primary_schema','2026-04-11 12:27:37'),('V9','restore_superadmin_role_access','2026-04-11 12:37:02');
/*!40000 ALTER TABLE `schema_migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `uuid` char(36) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role_uuid` char(36) NOT NULL,
  `level_uuid` char(36) NOT NULL,
  `is_super_admin` tinyint(1) NOT NULL DEFAULT 0,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `last_login_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_users_role_uuid` (`role_uuid`),
  KEY `idx_users_level_uuid` (`level_uuid`),
  CONSTRAINT `fk_users_level_uuid` FOREIGN KEY (`level_uuid`) REFERENCES `levels` (`uuid`),
  CONSTRAINT `fk_users_role_uuid` FOREIGN KEY (`role_uuid`) REFERENCES `roles` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('392938c3-efff-4f65-b31f-20360c4d63d5','Aghata','Aghata 123','aghata@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','97b8173f-c9a4-44d7-9078-e8196a896cb0','3c9ae974-2fe3-47e5-81ac-8707e67978a5',0,1,NULL,'2026-04-11 13:03:05','2026-04-11 16:51:10'),('5ed74503-df91-4953-9dd8-140a230372dd','Safrudin','Pak Boss','bos@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','c97e238b-9c13-43fc-9c6e-0b845c290c93','3c9ae974-2fe3-47e5-81ac-8707e67978a5',0,1,NULL,'2026-04-11 12:43:35','2026-04-11 16:50:40'),('8684594e-c0d3-4efd-9b09-accb95a5858f','M Rofi','Rofi Udin Update di master','rofi@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','b19d5454-35b9-11f1-a56d-002b671d8831',0,1,NULL,'2026-04-11 12:52:23','2026-04-11 18:19:52'),('be4a9fea-8c22-4de1-9d18-37c56bbcc07b','Rezaldy Abidin','rezaldy abidin','rezaldy266@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','d30fc8bd-35a1-11f1-a56d-002b671d8831','b19d7d32-35b9-11f1-a56d-002b671d8831',0,1,'2026-04-25 02:29:01','2026-04-11 12:43:09','2026-04-25 02:29:01'),('ca4dab29-07b3-441a-82e0-f917b4ccc934','Ari Fijaya','Ari Fijaya','ari@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','f16118fa-9902-4d7c-b2c2-34faaae93870','b19e7e38-35b9-11f1-a56d-002b671d8831',0,1,'2026-04-11 13:04:27','2026-04-11 12:42:46','2026-04-11 16:51:05'),('d311cef3-35a1-11f1-a56d-002b671d8831','Super Admin','superadmin','test1@gmail.com','7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2','c97e238b-9c13-43fc-9c6e-0b845c290c93','b19d5454-35b9-11f1-a56d-002b671d8831',1,1,'2026-04-25 05:27:23','2026-04-11 12:27:37','2026-04-25 05:27:23');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'pv_tugasbesar'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-25 13:39:55
