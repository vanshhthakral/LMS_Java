-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: smart_library_db
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `backup_logs`
--

DROP TABLE IF EXISTS `backup_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `operation` enum('Backup','Restore') DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `operation_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `performed_by` int DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `performed_by` (`performed_by`),
  CONSTRAINT `backup_logs_ibfk_1` FOREIGN KEY (`performed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_logs`
--

LOCK TABLES `backup_logs` WRITE;
/*!40000 ALTER TABLE `backup_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `backup_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_requests`
--

DROP TABLE IF EXISTS `book_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_requests` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `request_type` enum('New','Hold') NOT NULL,
  `status` enum('Pending','Approved','Rejected') DEFAULT 'Pending',
  `request_date` date DEFAULT NULL,
  `hold_expiry_date` date DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `student_id` (`student_id`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `book_requests_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_requests`
--

LOCK TABLES `book_requests` WRITE;
/*!40000 ALTER TABLE `book_requests` DISABLE KEYS */;
INSERT INTO `book_requests` VALUES (3,103,NULL,'Java','New','Approved','2025-05-03',NULL),(4,103,105,NULL,'Hold','Approved','2025-05-03','2025-05-10'),(5,1002,NULL,'Advanced Mathematics','New','Approved','2025-05-03',NULL),(6,1002,105,NULL,'Hold','Rejected','2025-05-03','2025-05-10'),(7,1002,NULL,'Java','New','Rejected','2025-05-03',NULL),(8,1002,101,NULL,'Hold','Rejected','2025-05-03','2025-05-10'),(9,1002,NULL,'Java','New','Rejected','2025-05-03',NULL),(10,1002,NULL,'Programming in C','New','Rejected','2025-05-03',NULL),(11,1002,105,NULL,'Hold','Rejected','2025-05-03','2025-05-10'),(12,1002,NULL,'Java','New','Approved','2025-05-03',NULL),(13,1002,105,NULL,'Hold','Approved','2025-05-03','2025-05-10'),(14,1002,NULL,'Java','New','Pending','2025-05-04',NULL),(15,1002,105,NULL,'Hold','Pending','2025-05-04','2025-05-11'),(16,1003,NULL,'Java','New','Pending','2025-05-04',NULL),(17,1003,105,NULL,'Hold','Pending','2025-05-04','2025-05-11'),(18,1003,NULL,'Advanced Mathematics','New','Pending','2025-05-04',NULL);
/*!40000 ALTER TABLE `book_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_reviews`
--

DROP TABLE IF EXISTS `book_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `review` text,
  `review_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`review_id`),
  KEY `student_id` (`student_id`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `book_reviews_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `book_reviews_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  CONSTRAINT `book_reviews_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_reviews`
--

LOCK TABLES `book_reviews` WRITE;
/*!40000 ALTER TABLE `book_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `book_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_transactions`
--

DROP TABLE IF EXISTS `book_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `book_id` int NOT NULL,
  `issue_date` date NOT NULL,
  `due_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Issued',
  `fine_amount` decimal(10,2) DEFAULT '0.00',
  `availability_status` varchar(20) DEFAULT 'Issued',
  `returned_status` enum('Returned','Not Returned') DEFAULT 'Not Returned',
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_transactions`
--

LOCK TABLES `book_transactions` WRITE;
/*!40000 ALTER TABLE `book_transactions` DISABLE KEYS */;
INSERT INTO `book_transactions` VALUES (1,1001,101,'2025-05-03','2025-05-17',NULL,'Issued',0.00,'Issued','Not Returned'),(2,1002,102,'2025-05-03','2025-05-10',NULL,'Issued',0.00,'Issued','Not Returned'),(3,1003,103,'2025-05-03','2025-05-13','2025-05-03','Returned',0.00,'Issued','Not Returned'),(4,103,101,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(5,103,101,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(6,103,105,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(7,103,101,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(8,103,101,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(9,103,105,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(10,103,101,'2025-05-03','2025-05-10',NULL,'Reissued',0.00,'Issued','Not Returned'),(11,103,105,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(12,103,105,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(13,103,105,'2025-05-03','2025-05-10',NULL,'Reissued',0.00,'Issued','Not Returned'),(14,103,101,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(15,103,100,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(16,103,105,'2025-05-03','2025-05-10',NULL,'Reissued',0.00,'Issued','Not Returned'),(17,103,105,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(18,103,105,'2025-05-03','2025-05-10','2025-05-03','Returned',0.00,'Issued','Not Returned'),(19,103,105,'2025-05-03','2025-05-10','2025-05-04','Returned',0.00,'Issued','Not Returned'),(20,103,105,'2025-05-03','2025-05-10',NULL,'Issued',0.00,'Issued','Not Returned'),(21,103,101,'2025-05-03','2025-05-10',NULL,'Issued',0.00,'Issued','Not Returned'),(22,103,1,'2025-05-03','2025-05-10',NULL,'Issued',0.00,'Issued','Not Returned'),(23,103,105,'2025-05-03','2025-05-10',NULL,'Issued',0.00,'Issued','Not Returned'),(24,103,101,'2025-05-03','2025-05-10',NULL,'Issued',0.00,'Issued','Not Returned'),(25,103,105,'2025-05-04','2025-05-11',NULL,'Issued',0.00,'Issued','Not Returned'),(26,103,105,'2025-05-04','2025-05-11',NULL,'Issued',0.00,'Issued','Not Returned'),(27,103,101,'2025-05-04','2025-05-11',NULL,'Issued',0.00,'Issued','Not Returned'),(28,103,105,'2025-05-04','2025-05-11',NULL,'Issued',0.00,'Issued','Not Returned'),(29,103,101,'2025-05-04','2025-05-11',NULL,'Issued',0.00,'Issued','Not Returned');
/*!40000 ALTER TABLE `book_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `book_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `total_copies` int NOT NULL,
  `available_copies` int NOT NULL,
  `qr_code_path` varchar(255) DEFAULT NULL,
  `availability_status` varchar(20) DEFAULT 'Available',
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=334 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'Programming in C','Yashvat Kanethkar',NULL,70,65,NULL,'Available'),(100,'Physics','HC Verma',NULL,200,198,NULL,'Available'),(101,'Advanced Mathematics','RD Sharma',NULL,500,482,NULL,'Available'),(105,'Java','Anamika',NULL,100,87,NULL,'Available'),(106,'DCN','Behrouz A. Forouzan',NULL,70,70,NULL,'Available'),(107,'Beehive','Ruskin Bond',NULL,800,797,NULL,'Available');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrowed_books`
--

DROP TABLE IF EXISTS `borrowed_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrowed_books` (
  `borrow_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `borrow_date` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`borrow_id`),
  KEY `fk_borrowedbooks_student` (`student_id`),
  KEY `fk_borrowedbooks_book` (`book_id`),
  CONSTRAINT `fk_borrowed_book_id` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_borrowedbooks_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_borrowedbooks_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrowed_books`
--

LOCK TABLES `borrowed_books` WRITE;
/*!40000 ALTER TABLE `borrowed_books` DISABLE KEYS */;
INSERT INTO `borrowed_books` VALUES (12,1002,101,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(13,1003,101,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(14,1003,105,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(15,1003,101,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(16,1003,101,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(17,1003,107,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(18,1003,101,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(19,1003,107,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(20,1003,105,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(21,1003,107,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04'),(22,1003,101,'2025-05-04','2025-05-11',NULL,'Issued','2025-05-04');
/*!40000 ALTER TABLE `borrowed_books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fines`
--

DROP TABLE IF EXISTS `fines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fines` (
  `fine_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  `borrow_id` int DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `paid` tinyint(1) DEFAULT '0',
  `payment_date` date DEFAULT NULL,
  `fine_amount` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`fine_id`),
  KEY `borrow_id` (`borrow_id`),
  KEY `fines_ibfk_1` (`student_id`),
  CONSTRAINT `fines_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fines`
--

LOCK TABLES `fines` WRITE;
/*!40000 ALTER TABLE `fines` DISABLE KEYS */;
/*!40000 ALTER TABLE `fines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `issued_books`
--

DROP TABLE IF EXISTS `issued_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issued_books` (
  `issued_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `fine_paid` decimal(10,2) DEFAULT NULL,
  `fine` double DEFAULT '0',
  PRIMARY KEY (`issued_id`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `issued_books_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issued_books`
--

LOCK TABLES `issued_books` WRITE;
/*!40000 ALTER TABLE `issued_books` DISABLE KEYS */;
INSERT INTO `issued_books` VALUES (19,100,1001,'2025-05-01','2025-05-15','2025-05-02',NULL,0),(20,1,1001,'2025-05-01','2025-05-02','2025-05-02',NULL,0),(21,1,1001,'2025-05-01','2025-05-01','2025-05-02',NULL,2),(22,100,1001,'2025-05-01','2025-05-15','2025-05-01',NULL,0),(23,101,1001,'2025-05-02','2025-05-04','2025-05-02',NULL,0),(24,101,1001,'2025-05-02','2025-05-16','2025-05-02',NULL,0),(25,105,1002,'2025-05-03','2025-05-17','2025-05-03',NULL,0),(26,105,1003,'2025-05-03','2025-05-17','2025-05-03',NULL,0),(27,101,1003,'2025-05-03','2025-05-17','2025-05-04',NULL,0),(28,105,1003,'2025-05-04','2025-05-18','2025-05-04',NULL,0),(29,106,1002,'2025-05-04','2025-05-18',NULL,NULL,0);
/*!40000 ALTER TABLE `issued_books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `librarians`
--

DROP TABLE IF EXISTS `librarians`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `librarians` (
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `librarian_id` int NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`librarian_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `librarian_id` (`librarian_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `librarians`
--

LOCK TABLES `librarians` WRITE;
/*!40000 ALTER TABLE `librarians` DISABLE KEYS */;
/*!40000 ALTER TABLE `librarians` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `message` text,
  `status` varchar(20) NOT NULL DEFAULT 'Sent',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` varchar(50) NOT NULL DEFAULT 'Due Date',
  PRIMARY KEY (`notification_id`),
  KEY `notifications_ibfk_1` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (26,3,NULL,'New book request for \'Java\' submitted.','Read','2025-05-03 18:30:00','Approval'),(27,3,NULL,'Hold request for book ID 105 submitted.','Read','2025-05-03 18:30:00','Approval'),(28,3,NULL,'Book ID 105 reissued. New due date: 2025-05-11','Read','2025-05-03 18:30:00','Due Date'),(29,3,NULL,'New book request for \'Advanced Mathematics\' submitted.','Read','2025-05-03 18:30:00','Approval'),(30,3,NULL,'Book ID 105 reissued. New due date: 2025-05-11','Read','2025-05-03 18:30:00','Due Date');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` int NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `year` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  KEY `fk_book_id` (`book_id`),
  KEY `fk_students_user` (`user_id`),
  CONSTRAINT `fk_book_id` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  CONSTRAINT `fk_students_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1001,'john_doe','CSE',3,501,NULL),(1002,'Mohit','CS',4,103,101),(1003,'student','CSE',3,3,NULL);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('Admin','Librarian','Student') NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `phone` varchar(15) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin123','Admin','admin123@gmail.com',1,'2345678987','active',1),(2,'librarian','libpass','Librarian','libpass123@gmail.com',1,'9876587987','active',1),(3,'student','stupass','Student','student@gmail.com',1,'9873524827','active',1),(103,'Mohit','stu2pass','Student',NULL,1,NULL,'active',1),(501,'john_doe','password123','Student',NULL,1,NULL,'active',1),(1003,'ABCD','xyz','Student',NULL,1,NULL,'active',1),(1005,'Shaswat','password','Student',NULL,1,NULL,'active',1),(1006,'xyz','xyzlib','Librarian','xyz@gmail.com',1,'9876543210','Active',1),(1007,'Shweta','shweta','Librarian','shw@gmail.com',1,'9837892088','Active',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-04 20:10:09
