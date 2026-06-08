-- MySQL dump 10.13  Distrib 8.1.0, for macos13.3 (arm64)
--
-- Host: localhost    Database: medical
-- ------------------------------------------------------
-- Server version	8.1.0

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
-- Table structure for table `t_parchase_batch`
--

DROP TABLE IF EXISTS `t_parchase_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_parchase_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user` bigint DEFAULT NULL COMMENT '进货人 id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0: 正常，1：异常',
  PRIMARY KEY (`id`),
  KEY `t_parchase_batch_t_users_id_fk` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='进货批次表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_parchase_batch`
--

LOCK TABLES `t_parchase_batch` WRITE;
/*!40000 ALTER TABLE `t_parchase_batch` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_parchase_batch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_parchase_detail`
--

DROP TABLE IF EXISTS `t_parchase_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_parchase_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 id',
  `batch` bigint DEFAULT NULL COMMENT '批次 id',
  `physic` bigint DEFAULT NULL COMMENT '药品 id',
  `price` float NOT NULL DEFAULT '0' COMMENT '进货价格',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人',
  `status` tinyint DEFAULT '0' COMMENT '状态 0 正常 1 异常',
  `account` float NOT NULL DEFAULT '0' COMMENT '数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='进货详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_parchase_detail`
--

LOCK TABLES `t_parchase_detail` WRITE;
/*!40000 ALTER TABLE `t_parchase_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_parchase_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_patient`
--

DROP TABLE IF EXISTS `t_patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_patient` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '患者 id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '患者姓名',
  `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '患者电话',
  `birthday` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '患者出生日期',
  `addr` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '患者地址',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '患者状态， 0:正常  1:异常',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint NOT NULL COMMENT '修改人 id',
  `create_user` int DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='患者信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_patient`
--

LOCK TABLES `t_patient` WRITE;
/*!40000 ALTER TABLE `t_patient` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_perscription`
--

DROP TABLE IF EXISTS `t_perscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_perscription` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键 id',
  `patient` bigint NOT NULL COMMENT '患者',
  `user` bigint NOT NULL COMMENT '开处方人',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0:正常  1:异常',
  `comments` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '医嘱信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='处方信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_perscription`
--

LOCK TABLES `t_perscription` WRITE;
/*!40000 ALTER TABLE `t_perscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_perscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_physic`
--

DROP TABLE IF EXISTS `t_physic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_physic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '药品 id',
  `name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `type` tinyint NOT NULL DEFAULT '10' COMMENT '类型  10:药品   20：耗材',
  `unit` tinyint NOT NULL DEFAULT '1' COMMENT '单位  1:个  2:瓶 3：盒 4: 袋  11: 毫克 12：克 13：千克  21：毫升 22：升 ',
  `alias` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '别名',
  `status` tinyint DEFAULT '0' COMMENT '状态 0:正常  1：异常',
  `manufacturer` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '厂商',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人 id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='药品耗材信息元数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_physic`
--

LOCK TABLES `t_physic` WRITE;
/*!40000 ALTER TABLE `t_physic` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_physic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_prescription_physic`
--

DROP TABLE IF EXISTS `t_prescription_physic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_prescription_physic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `physic` bigint DEFAULT NULL COMMENT '药品 id',
  `amount` float NOT NULL DEFAULT '0' COMMENT '数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人',
  `prescription` bigint DEFAULT NULL COMMENT '处方',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0 正常 1 异常',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='处方药品（耗材）表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_prescription_physic`
--

LOCK TABLES `t_prescription_physic` WRITE;
/*!40000 ALTER TABLE `t_prescription_physic` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_prescription_physic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_selling_price`
--

DROP TABLE IF EXISTS `t_selling_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_selling_price` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键 id',
  `physic` bigint DEFAULT NULL COMMENT '药品 id',
  `price` float NOT NULL DEFAULT '0' COMMENT '卖出单价',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0:正常  1:异常',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='卖出价格';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_selling_price`
--

LOCK TABLES `t_selling_price` WRITE;
/*!40000 ALTER TABLE `t_selling_price` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_selling_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_tenants`
--

DROP TABLE IF EXISTS `t_tenants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_tenants` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '租户 id',
  `name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '租户名字',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0：正常，1：异常',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_tenants`
--

LOCK TABLES `t_tenants` WRITE;
/*!40000 ALTER TABLE `t_tenants` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_tenants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名字',
  `type` tinyint NOT NULL DEFAULT '10' COMMENT '类型  ：10、医生  20：护士   30：麻醉师  40：其他',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint DEFAULT NULL COMMENT '修改用户 ID',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0：正常  1：异常',
  `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户dian',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='使用用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (1,'clwu',1,'2023-09-12 09:00:44','2023-09-12 09:00:44',NULL,0,'18611964654'),(4,'clwu',0,'2023-09-12 11:12:22','2023-09-12 11:12:22',NULL,0,'186119646541'),(5,'clwu',0,'2023-09-13 10:28:25','2023-09-13 10:28:25',NULL,0,'1861196465413'),(6,'clwu',3,'2023-09-13 10:29:40','2023-09-13 10:29:40',NULL,0,'1861196465413'),(7,'clwu',3,'2023-09-15 08:23:01','2023-09-15 08:23:01',NULL,0,'18611964654134'),(8,'clwu',0,'2023-09-15 13:29:24','2023-09-15 13:29:24',NULL,0,'18611964654134'),(9,'clwu',0,'2023-09-15 13:34:11','2023-09-15 13:34:11',NULL,0,'18611964654134'),(10,'clwu',0,'2023-09-15 13:42:25','2023-09-15 13:42:25',NULL,0,'18611964654134'),(11,'clwu',-1,'2023-09-15 13:46:36','2023-09-15 13:46:36',NULL,0,'18611964654134');
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'medical'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-22 17:06:03
