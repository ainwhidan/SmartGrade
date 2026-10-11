-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jan 22, 2026 at 03:40 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `student_tracker`
--

-- --------------------------------------------------------

--
-- Table structure for table `announcements`
--

CREATE TABLE `announcements` (
  `announcement_id` int(11) NOT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `message` text NOT NULL,
  `level` varchar(10) DEFAULT NULL,
  `created_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `announcements`
--

INSERT INTO `announcements` (`announcement_id`, `teacher_id`, `title`, `message`, `level`, `created_date`) VALUES
(1, 5, 'Test 1', 'Please complete your test before our class next week 10/1/2026. Write your answer in your exercise book. We will discuss in class.', '6', '2026-01-06 23:49:41'),
(2, 7, 'Mid Term Test', 'Please do your midterm test, and write your answer on your exercise book also.', '5', '2026-01-09 21:38:31'),
(3, 8, 'Test 1', 'Sila jawab test ini sebelum kelas minggu hadapan', 'All', '2026-01-14 22:31:50');

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `question_id` int(11) NOT NULL,
  `quiz_id` int(11) DEFAULT NULL,
  `question_text` text NOT NULL,
  `option_a` varchar(200) DEFAULT NULL,
  `option_b` varchar(200) DEFAULT NULL,
  `option_c` varchar(200) DEFAULT NULL,
  `option_d` varchar(200) DEFAULT NULL,
  `correct_answer` char(1) DEFAULT NULL,
  `marks` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`question_id`, `quiz_id`, `question_text`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_answer`, `marks`) VALUES
(1, 1, 'What is basic unit of life?', 'Atom', 'Molecule', 'Cell', 'Tissue', 'C', 1),
(2, 1, 'Who discovered the cell?', 'Isaac Newton', 'Robert Hooke', 'Albert Einstein', 'Galileo', 'B', 1),
(3, 2, 'Azmi akan menyewakan rumah kampungnya itu______ sesiapa yang berminat.', 'daripada', 'dengan', 'kepada', 'pada', 'C', 1),
(4, 3, 'Siapakah Perdana Menteri pertama?', 'Tun Abdul Rahman', 'Tun Abdul Razak Hussein', 'Tun Hussein Onn', 'Datuk Seri Najib', 'A', 1),
(5, 4, 'I want to ____ ?', 'run', 'running', 'ran', 'runed', 'A', 1),
(6, 4, 'She___ a chocolate cake.', 'wanted', 'want', 'wanting', 'wants', 'D', 1),
(7, 5, 'Siapakah Perdana Menteri pertama?', 'Dato Seri Najib', 'Tunku Abdul Rahman', 'Anwar Ibrahim', 'Tun Mahathir', 'B', 1),
(8, 5, 'iudcdkb', 'jshdhj', 'jsndj', 'jdj', 'sjndj', 'D', 1);

-- --------------------------------------------------------

--
-- Table structure for table `quiz`
--

CREATE TABLE `quiz` (
  `quiz_id` int(11) NOT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `subject` varchar(50) DEFAULT NULL,
  `level` varchar(10) DEFAULT NULL,
  `total_marks` int(11) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiz`
--

INSERT INTO `quiz` (`quiz_id`, `teacher_id`, `title`, `subject`, `level`, `total_marks`, `duration`, `created_date`) VALUES
(1, 5, 'Test 1', 'Science', '6', 2, 5, '2026-01-06 23:24:17'),
(2, 5, 'Test 1', 'BM', '6', 1, 5, '2026-01-07 00:02:24'),
(3, 5, 'TEST', 'BM', '6', 1, 1, '2026-01-07 21:45:34'),
(4, 7, 'Mid Term Test', 'BI', '5', 2, 30, '2026-01-09 21:34:18'),
(5, 8, 'Test 1', 'BM', '5', 2, 30, '2026-01-14 22:28:17');

-- --------------------------------------------------------

--
-- Table structure for table `quiz_results`
--

CREATE TABLE `quiz_results` (
  `result_id` int(11) NOT NULL,
  `quiz_id` int(11) DEFAULT NULL,
  `student_ic` varchar(20) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `total_marks` int(11) DEFAULT NULL,
  `percentage` decimal(5,2) DEFAULT NULL,
  `completed_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiz_results`
--

INSERT INTO `quiz_results` (`result_id`, `quiz_id`, `student_ic`, `score`, `total_marks`, `percentage`, `completed_date`) VALUES
(1, 1, '020304010304', 0, 2, 0.00, '2026-01-06 23:50:46'),
(2, 2, '020304010304', 1, 1, 100.00, '2026-01-07 00:03:14'),
(3, 3, '020304010304', 1, 1, 100.00, '2026-01-07 21:46:24'),
(4, 4, '020608110098', 1, 2, 50.00, '2026-01-09 21:39:59'),
(5, 5, '030925010348', 1, 2, 50.00, '2026-01-14 22:30:05');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `ic` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `teacherid` int(11) DEFAULT NULL,
  `level` varchar(10) DEFAULT NULL,
  `subject1` int(11) DEFAULT NULL,
  `grade1` varchar(2) DEFAULT NULL,
  `subject2` int(11) DEFAULT NULL,
  `grade2` varchar(2) DEFAULT NULL,
  `subject3` int(11) DEFAULT NULL,
  `grade3` varchar(2) DEFAULT NULL,
  `subject4` int(11) DEFAULT NULL,
  `grade4` varchar(2) DEFAULT NULL,
  `subject5` int(11) DEFAULT NULL,
  `grade5` varchar(2) DEFAULT NULL,
  `subject6` int(11) DEFAULT NULL,
  `grade6` varchar(2) DEFAULT NULL,
  `total` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`id`, `ic`, `name`, `teacherid`, `level`, `subject1`, `grade1`, `subject2`, `grade2`, `subject3`, `grade3`, `subject4`, `grade4`, `subject5`, `grade5`, `subject6`, `grade6`, `total`, `rank`) VALUES
(1, '020425081001', 'Muhammad Faris ', 4, '5', 80, 'A', 85, 'A', 90, 'A', 70, 'B', 75, 'B', 90, 'A', 490, 1),
(2, '020304010304', 'Ali bin Abu', 5, '6', 90, 'A', 98, 'A', 70, 'B', 80, 'A', 80, 'A', 87, 'A', 505, 1),
(3, '020608110098', 'Nurul Ashikin binti Rusli', 7, '5', 90, 'A', 99, 'A', 95, 'A', 80, 'B', 75, 'A', 70, 'C', 509, 1),
(4, '030909010333', 'Harith Syafiq', 5, '5', 80, 'A', 85, 'A', 90, 'A', 95, 'A', 70, 'B', 76, 'B', 496, 1),
(5, '030925010348', 'Nurul ain binti wahidan', 8, '5', 91, 'A', 95, 'A', 98, 'A', 100, 'A', 96, 'A', 80, 'A', 560, 1);

-- --------------------------------------------------------

--
-- Table structure for table `student_answers`
--

CREATE TABLE `student_answers` (
  `answer_id` int(11) NOT NULL,
  `result_id` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `student_answer` char(1) DEFAULT NULL,
  `is_correct` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_answers`
--

INSERT INTO `student_answers` (`answer_id`, `result_id`, `question_id`, `student_answer`, `is_correct`) VALUES
(1, 3, 4, 'A', 1),
(2, 4, 5, 'B', 0),
(3, 4, 6, 'D', 1),
(4, 5, 7, 'C', 0),
(5, 5, 8, 'D', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `teacherid` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `security_question` varchar(200) DEFAULT NULL,
  `security_answer` varchar(200) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  `profile_picture` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`teacherid`, `username`, `password`, `security_question`, `security_answer`, `full_name`, `phone`, `email`, `address`, `role`, `profile_picture`) VALUES
(4, 'ainwhidan', '111111', 'What is your mother\'s maiden name?', 'admin', 'Nurul Ain ', NULL, NULL, NULL, 'teacher', NULL),
(5, 'farysha01', '55555', 'What is your mother\'s maiden name?', 'admin', 'Farysha Adella', '0123456789', 'faryshaadella@gmail.com', '', 'teacher', '/Users/nurulain/Downloads/PHOTO-2026-01-14-16-00-06.jpg'),
(6, 'aisyah76', 'aisyah', 'What is your mother\'s maiden name?', 'admin', 'Siti Aisyah', NULL, NULL, NULL, 'teacher', NULL),
(7, 'angel02', '000000', 'What is your favorite food?', 'Maggie', 'Angel Ency', NULL, NULL, NULL, 'teacher', NULL),
(8, 'aisyahrusli', '123456', 'What is your favorite food?', 'Ayam Gepuk', 'Siti Aisyah', '0123456789', '', '', 'teacher', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `announcements`
--
ALTER TABLE `announcements`
  ADD PRIMARY KEY (`announcement_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `quiz_id` (`quiz_id`);

--
-- Indexes for table `quiz`
--
ALTER TABLE `quiz`
  ADD PRIMARY KEY (`quiz_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Indexes for table `quiz_results`
--
ALTER TABLE `quiz_results`
  ADD PRIMARY KEY (`result_id`),
  ADD KEY `quiz_id` (`quiz_id`),
  ADD KEY `student_ic` (`student_ic`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_ic` (`ic`),
  ADD KEY `teacherid` (`teacherid`);

--
-- Indexes for table `student_answers`
--
ALTER TABLE `student_answers`
  ADD PRIMARY KEY (`answer_id`),
  ADD KEY `result_id` (`result_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`teacherid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `announcements`
--
ALTER TABLE `announcements`
  MODIFY `announcement_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `question_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `quiz`
--
ALTER TABLE `quiz`
  MODIFY `quiz_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `quiz_results`
--
ALTER TABLE `quiz_results`
  MODIFY `result_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `student_answers`
--
ALTER TABLE `student_answers`
  MODIFY `answer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `teacherid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcements`
--
ALTER TABLE `announcements`
  ADD CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`teacherid`) ON DELETE CASCADE;

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
  ADD CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE;

--
-- Constraints for table `quiz`
--
ALTER TABLE `quiz`
  ADD CONSTRAINT `quiz_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`teacherid`) ON DELETE CASCADE;

--
-- Constraints for table `quiz_results`
--
ALTER TABLE `quiz_results`
  ADD CONSTRAINT `quiz_results_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `quiz_results_ibfk_2` FOREIGN KEY (`student_ic`) REFERENCES `student` (`ic`) ON DELETE CASCADE;

--
-- Constraints for table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`teacherid`) REFERENCES `users` (`teacherid`);

--
-- Constraints for table `student_answers`
--
ALTER TABLE `student_answers`
  ADD CONSTRAINT `student_answers_ibfk_1` FOREIGN KEY (`result_id`) REFERENCES `quiz_results` (`result_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `student_answers_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
