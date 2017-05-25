-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: Ago 05, 2013 alle 11:19
-- Versione del server: 5.0.84
-- Versione PHP: 5.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `panciera2`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `epigr`
--

CREATE TABLE IF NOT EXISTS `epigr` (
  `id_nr` varchar(9) NOT NULL default '',
  `provinz` varchar(10) NOT NULL,
  `land` varchar(6) NOT NULL,
  `fo_antik` varchar(30) NOT NULL,
  `fo_modern` varchar(60) NOT NULL,
  `fundstelle` varchar(250) NOT NULL,
  `aufbewahrung` varchar(250) NOT NULL,
  `denkmaltyp` varchar(30) NOT NULL,
  `material` varchar(30) NOT NULL,
  `scriptura` varchar(30) NOT NULL,
  `lingua` varchar(15) NOT NULL,
  `i_gattung` varchar(50) NOT NULL,
  `livellosocialeTot` varchar(100) NOT NULL,
  `dat_tag` int(11) default NULL,
  `dat_monat` int(11) default NULL,
  `dat_jahr_a` int(11) default NULL,
  `dat_jahr_e` int(11) default NULL,
  `mis_alto` float(5,2) default NULL,
  `mis_largo` float(5,2) default NULL,
  `mis_spesso` float(5,2) default NULL,
  `mis_lettere` varchar(20) NOT NULL,
  `status_tituli` varchar(80) NOT NULL,
  `rif_gis_x` varchar(25) default NULL,
  `rif_gis_y` varchar(25) default NULL,
  `datazione` varchar(80) NOT NULL,
  PRIMARY KEY  (`id_nr`),
  KEY `provinz` (`provinz`),
  KEY `land` (`land`),
  KEY `fo_antik` (`fo_antik`),
  KEY `fo_modern` (`fo_modern`),
  KEY `fundstelle` (`fundstelle`),
  KEY `aufbewahrung` (`aufbewahrung`),
  KEY `denkmaltyp` (`denkmaltyp`),
  KEY `material` (`material`),
  KEY `status_tituli` (`status_tituli`),
  KEY `scriptura` (`scriptura`),
  KEY `lingua` (`lingua`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `epigr`
--

INSERT INTO `epigr` (`id_nr`, `provinz`, `land`, `fo_antik`, `fo_modern`, `fundstelle`, `aufbewahrung`, `denkmaltyp`, `material`, `scriptura`, `lingua`, `i_gattung`, `livellosocialeTot`, `dat_tag`, `dat_monat`, `dat_jahr_a`, `dat_jahr_e`, `mis_alto`, `mis_largo`, `mis_spesso`, `mis_lettere`, `status_tituli`, `rif_gis_x`, `rif_gis_y`, `datazione`) VALUES
('EDR124968', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 3.50, 9.00, 0.00, '1,3-3,5', 'tit. integer', '', '', 'archaeologia'),
('EDR124971', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 5.00, 49.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR124993', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', '', '', '', 'archaeologia'),
('EDR124994', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 1.50, 7.00, 0.00, '0,9-1,5', '', '', '', 'archaeologia'),
('EDR124995', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 1.00, 13.00, 0.00, '1', 'tit. integer', '', '', 'archaeologia'),
('EDR124996', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 1.00, 10.00, 0.00, '1', 'tit. mutilus', '', '', 'archaeologia'),
('EDR124998', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 0.00, 11.50, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR124999', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 0.00, 15.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR125000', 'LaC', 'I', 'Pompeii', 'Pompei', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 0.00, 26.00, 0.00, '', '', '', '', 'archaeologia'),
('EDR124967', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR124969', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', '', 0, 0, 1, 79, 38.00, 55.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR124970', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 12.00, 10.50, 0.00, '3,5-4', 'tit. integer', '', '', 'archaeologia'),
('EDR124972', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'alia', 'cetera', '', 0, 0, 1, 79, 7.50, 7.50, 0.00, '', '', '', '', 'archaeologia'),
('EDR124973', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'alia', 'cetera', '', 0, 0, 0, 0, 10.50, 10.50, 0.00, '', '', '', '', ''),
('EDR124974', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', '', 'cetera', '', 0, 0, 1, 79, 10.00, 10.00, 0.00, '', '', '', '', 'archaeologia'),
('EDR124975', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 32.50, 0.00, '', 'tit. integer?', '', '', 'archaeologia'),
('EDR124976', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', '', 'cetera', '', 0, 0, 1, 79, 6.50, 9.00, 0.00, '', '', '', '', 'archaeologia'),
('EDR124977', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', '', 'cetera', '', 0, 0, 1, 79, 6.50, 10.50, 0.00, '', '', '', '', 'archaeologia'),
('EDR124978', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', '', 'cetera', '', 0, 0, 1, 79, 7.00, 11.00, 0.00, '', '', '', '', 'archaeologia'),
('EDR124981', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 2.50, 7.50, 0.00, '2-2,5', 'tit. integer', '', '', 'archaeologia'),
('EDR124982', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 2.00, 8.00, 0.00, '2', 'tit. integer', '', '', 'archaeologia'),
('EDR124983', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.60, 7.50, 0.00, '0,6', 'tit. integer', '', '', 'archaeologia'),
('EDR124984', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 1.00, 15.00, 0.00, '1', 'tit. integer', '', '', 'archaeologia'),
('EDR124987', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 3.50, 5.00, 0.00, '1,5-3,5', 'tit. integer', '', '', 'archaeologia'),
('EDR124989', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. mutilus', '', '', 'archaeologia'),
('EDR124991', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR124992', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 4.00, 10.70, 0.00, '0,8-4', 'tit. integer', '', '', 'archaeologia'),
('EDR125002', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa dei Quattro Stili (I.8.17)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', '', 'cetera', '', 0, 0, 1, 79, 12.00, 0.00, 0.00, '', '', '', '', 'archaeologia'),
('EDR128545', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa della Statuetta Indiana (I.8.5)', 'Pompei (Napoli), Casa della Statuetta Indiana (I.8.5)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128558', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Domus M. Epidii Primi (I.8.14)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 36.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128546', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit?', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128547', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128548', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128551', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128553', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Shop (I.8.3)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 7.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128557', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Taberna (I.8.1)', 'Periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 8.10, 28.00, 0.00, '1,5-3,0', 'tit. mutilus', '', '', 'archaeologia'),
('EDR128562', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di Stephanus (I.8.2)', 'periit?', 'aedificium', 'tectorium', 'cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128564', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Taberna (I.8.1)', 'Periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 8.50, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128556', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Officina N. Fufidii Successi (I.8.16)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 6.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128542', 'LaC', 'I', 'Pompeii', 'Pompeii (Napoli)', 'Pompei (Napoli), Officina N. Fufidii Successi (I.8.15)', 'Periit?', 'aedificium', 'tectorium', 'cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128552', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '3,0-6,0', 'tit. integer', '', '', 'archaeologia'),
('EDR128555', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128561', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'Periit? ', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128565', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Domus A. Grassi Romani (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128566', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Domus A. Grassi Romani (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128616', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Domus M. Epidii Primi (I.8.14)', 'Periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128615', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di M. Epidio Primo (I.8.14)', 'Periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 18.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128617', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Officina N. Fufidii Successi (I.8.15)', 'Pompei (Napoli), in repositis, inv. n. 41660', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 50.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128567', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera, carbone ', 'latina-graeca', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128543', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di Stephanus (I.8.2)', 'periit?', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 8.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128544', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera, carbone', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128550', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Domus di A. Grassus Romanus (I.8.13)', 'Pompei (Napoli), Domus di A. Grassus Romanus (I.8.13)', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '3,0-6,0', 'tit. integer', '', '', 'archaeologia'),
('EDR128554', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128559', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13)', 'periit ', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 27.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128560', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli),  Casa della Statuetta Indiana (I.8.5)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 0.00, 0.00, '1,0', 'tit. integer', '', '', 'archaeologia'),
('EDR128581', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di Stephanus (I.8.2)', 'Periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'ignoratur', 0, 0, 1, 79, 0.00, 0.00, 0.00, '4-6', 'tit. integer', '', '', 'archaeologia'),
('EDR128646', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera,carbone ', 'latina-graeca', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128647', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera,carbone ', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128648', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera,carbone ', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128649', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli) ', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera,carbone ', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128650', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', ' cetera,carbone ', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128749', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di L. Betitius Placidus (I.8.9)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '7,0', 'tit. integer', '', '', 'archaeologia'),
('EDR128673', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa della Statuetta Indiana (I.8.5)', 'periit', 'aedificium', 'tectorium', 'litt. scariph.', 'alia', 'cetera', 'cet.', 0, 0, 1, 79, 4.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128651', 'LaC', 'I', 'Pompeii ', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di A. Grassus Romanus (I.8.13) ', 'Periit? ', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 16.00, 21.00, 0.00, '1,8-3,6 ', 'tit. mutilus', '', '', 'archaeologia'),
('EDR128739', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di L. Betitius Placidus (I.8.9)', 'periit?', 'columna', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 7.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128740', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa di L. Betitius Placidus (I.8.9)', 'periit?', 'columna', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 30.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128752', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Hospitium dei Pulcinella (I.8.10)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128753', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Hospitium dei Pulcinella (I.8.5)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 11.00, 0.00, '', 'tit. integer', '', '', 'archaeologia'),
('EDR128755', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Hospitium dei Pulcinella (I.8.5)', 'periit?', 'aedificium', 'tectorium', 'litt. scariph.', 'latina', 'cetera', 'cet.', 0, 0, 1, 79, 0.00, 20.00, 0.00, '', 'tit. integer?', '', '', 'archaeologia'),
('EDR128899', 'LaC', 'I', 'Pompeii', 'Pompei (Napoli)', 'Pompei (Napoli), Casa della Statuetta Indiana (I.8.5)', 'Periit', 'aedificium', 'tectorium', 'litt. scariph.', 'alia', 'cetera', 'cet.', 0, 0, 1, 79, 8.00, 0.00, 0.00, '', 'tit. integer', '', '', 'archaeologia');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
