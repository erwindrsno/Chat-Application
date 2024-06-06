-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jun 05, 2024 at 03:19 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chat_application`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  `username` varchar(128) NOT NULL,
  `password` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `username`, `password`) VALUES
(1, 'Erwin Darsono', 'erwindarsono', 'password12345'),
(2, 'Fabrianus Pujanugraha Mone', 'fabrianus@12345', 'braiganteng'),
(3, 'Erwin Darsono', 'erwindarsono08', '5d44546d7cc2768a988d98f97fb54a54b5fd1488faa8423b8325aca01a484c6a'),
(4, 'Dummy Data', 'dummy@1234', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
(5, 'babi', 'kobo', 'b1912e0efc209a3d9c87dc6ad45159376e52d06c7b20fdb4923efc5a1c8796e8'),
(6, 'Erwin Darsono', 'erwin@unpar.ac.id', '5d44546d7cc2768a988d98f97fb54a54b5fd1488faa8423b8325aca01a484c6a'),
(7, 'admin', 'admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918'),
(8, 'admin2', 'admin2', '1c142b2d01aa34e9a36bde480645a57fd69e14155dacfab5a3f9257b77fdc8d8'),
(9, 'ajazz', 'ajazz', 'fb186987c3843e029526948d59e5c56a40e19290e6fc7ef197c9000945e2f329'),
(10, 'dell', 'dell', 'd0e2ece166bdef23adedfaa3f1c47c9f969270f0fc519185514d58b4c7f6d199'),
(11, 'tom rhodes', 'tom rhodes', 'e3b318ee29f21c8924f9e92adde8f91c646fbdfa75463193a811ff0f20253aaa'),
(12, 'iu', 'iu', 'fe9e123b30d7ded2b7426f11d57bf1272d4a4a15ee8534b01bb8e6a8d5465581'),
(13, 'cosmos', 'cosmos', '4cbe19716b1aa73a67dc4b28c34391879b503259fc76852082b4dafcf0de85b2'),
(14, 'leminerale', 'leminerale', '8fde7d1a4be75d0772779044a222b3c1c7a3caaf63955a8b2c3e7741f3b35b50'),
(15, 'aqua', 'aqua', '143ea17f6a47a30b5a974a6b4f9649a5937a25fda9c4fa7e839a435c4ad38714'),
(16, 'laptop', 'laptop', '5eec0dc419aa8337bf725f026fda9c78c1cb1c642eeaff9d6e1112f37783e942'),
(17, 'helm', 'helm', 'ab14d3faa25e917efe6e7135d4ecca197866738885a88b9b95d1a16d2bb5b323'),
(18, 'mcd', 'mcd', 'c1f5def06c9c8066f2388eb67559d9ccb16892f0e243d69120802df77e455772'),
(19, 'rafif pratama', 'rafif', '3b93d6ff697f9de43f93d3601955eca714863a2f5bcacab831c9fdbfedffdbb6'),
(20, 'Rafif', 'rafif', '3b93d6ff697f9de43f93d3601955eca714863a2f5bcacab831c9fdbfedffdbb6'),
(21, 'user bebas', 'bebas', '003ead49de0211052aa43f7cc2ef0035346283d4d9ab8a3cd3cc7e00e22316d7'),
(22, 'name', 'username', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'),
(23, 'Vincentius Daryl Kurniawan', 'daryl', '3b43e48861a62d3bbd6434b81ac6d8d2d89973554ce1374b7a7455b97b99d466');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(64) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
