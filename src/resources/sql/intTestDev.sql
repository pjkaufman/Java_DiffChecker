SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `intTestDev`
--
CREATE DATABASE IF NOT EXISTS `intTestDev` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `intTestDev`;

-- --------------------------------------------------------

--
-- Table structure for table `3`
--

DROP TABLE IF EXISTS `3`;
CREATE TABLE IF NOT EXISTS `3` (
  `idnew_table2` int(11) NOT NULL AUTO_INCREMENT,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT 'b',
  `3col` geometry NOT NULL,
  `3col1` geometry NOT NULL,
  PRIMARY KEY (`idnew_table2`),
  UNIQUE KEY `index3` (`new_table2col2`,`new_table2col1`),
  KEY `dfjsalkldskj` (`new_table2col`,`new_table2col1`),
  SPATIAL KEY `index4` (`3col`),
  FULLTEXT KEY `index5` (`new_table2col2`)
) ENGINE=MyISAM AUTO_INCREMENT=1000 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `compositemodify`
--

DROP TABLE IF EXISTS `compositemodify`;
CREATE TABLE IF NOT EXISTS `compositemodify` (
  `idnew_table2` int(11) NOT NULL AUTO_INCREMENT,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col10` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT 'b',
  `3col` geometry NOT NULL,
  `3col1` geometry NOT NULL,
  PRIMARY KEY (`idnew_table2`),
  UNIQUE KEY `index3` (`new_table2col2`,`new_table2col10`),
  KEY `dfjsalkldskj` (`new_table2col`,`new_table2col10`),
  SPATIAL KEY `index4` (`3col`),
  FULLTEXT KEY `index5` (`new_table2col`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `af`
--

DROP TABLE IF EXISTS `af`;
CREATE TABLE IF NOT EXISTS `af` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT '',
  `addme` int(24) NOT NULL,
  UNIQUE KEY `common_index` (`new_table2col2`,`new_table2col1`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `afa`
--

DROP TABLE IF EXISTS `afa`;
CREATE TABLE IF NOT EXISTS `afa` (
  `idnew_table2` int(11) NOT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `fkchanges`
--

DROP TABLE IF EXISTS `fkchanges`;
CREATE TABLE IF NOT EXISTS `fkchanges` (
  `id` int(11) NOT NULL,
  `id2` int(11) NOT NULL,
  `id3` int(11) NOT NULL,
  `id4` int(11) NOT NULL,
  `id5` int(11) NOT NULL,
  `part2` int(8) NOT NULL,
  `part3` int(5) NOT NULL,
  FOREIGN KEY (`id2`) REFERENCES `afa`(`idnew_table2`),
  FOREIGN KEY (`id4`) REFERENCES `nochanges`(`id`),
  FOREIGN KEY (`id5`,`part2`) REFERENCES `compositeprimarymodification`(`idnew_table2`,`part2`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `fkignore`
--

DROP TABLE IF EXISTS `fkignore`;
CREATE TABLE IF NOT EXISTS `fkignore` (
  `id` int(11) NOT NULL,
  `idnew_table2` int(11) NOT NULL,
  FOREIGN KEY (`idnew_table2`) REFERENCES `afa`(`idnew_table2`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `d`
--

DROP TABLE IF EXISTS `d`;
CREATE TABLE IF NOT EXISTS `d` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  FULLTEXT KEY `add_index` (`new_table2col`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `nochanges`
--

DROP TABLE IF EXISTS `nochanges`;
CREATE TABLE IF NOT EXISTS `nochanges` (
  `id` int(11) NOT NULL,
  `column1` varchar(45) DEFAULT NULL,
  `column2` varchar(45) DEFAULT NULL,
  `column3` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

-- --------------------------------------------------------

--
-- Table structure for table `addprimarykey`
--

DROP TABLE IF EXISTS `addprimarykey`;
CREATE TABLE IF NOT EXISTS `addprimarykey` (
  `id` int(11) NOT NULL,
  `column1` varchar(45) DEFAULT NULL,
  `column2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `modifyprimarykey`
--

DROP TABLE IF EXISTS `modifyprimarykey`;
CREATE TABLE IF NOT EXISTS `modifyprimarykey` (
  `id1` int(11) NOT NULL,
  `id2` int(11) NOT NULL,
  `column1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id1`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `new_table`
--

DROP TABLE IF EXISTS `new_table`;
CREATE TABLE IF NOT EXISTS `new_table` (
  `idnew_table` int(11) NOT NULL,
  `new_tablecol` varchar(45) DEFAULT NULL,
  `new_tablecol1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `new_table2`
--

DROP TABLE IF EXISTS `new_table2`;
CREATE TABLE IF NOT EXISTS `new_table2` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `compositeprimarymodification`
--

DROP TABLE IF EXISTS `compositeprimarymodification`;
CREATE TABLE IF NOT EXISTS `compositeprimarymodification` (
  `idnew_table2` int(11) NOT NULL,
  `part2` int(8) NOT NULL,
  `part3` int(5) NOT NULL,
  `column1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`,`part2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rds`
--

DROP TABLE IF EXISTS `rds`;
CREATE TABLE IF NOT EXISTS `rds` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1ds` varchar(45) DEFAULT NULL,
  `new_table2col2afd` varchar(45) DEFAULT NULL,
  `new_table2colfaddsa` varchar(45) DEFAULT NULL,
  `new_table2col1afdds` varchar(45) DEFAULT NULL,
  `new_table2col2afdas` varchar(45) DEFAULT NULL,
  `new_table2colf` varchar(45) DEFAULT NULL,
  `new_table2colad1` varchar(45) DEFAULT NULL,
  `new_table2codasl2` varchar(45) DEFAULT NULL,
  `new_table2coadl` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_tafble2col2` varchar(45) DEFAULT NULL,
  `new_tabdsafle2col` varchar(45) DEFAULT NULL,
  `new_table2cdafol1` varchar(45) DEFAULT NULL,
  `new_table2fdascol2` varchar(45) DEFAULT NULL,
  `new_table2adfscol` varchar(45) DEFAULT NULL,
  `new_table2coldf1` varchar(45) DEFAULT NULL,
  `new_table2col2aasfd` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `charsetcheck`
--

DROP TABLE IF EXISTS `charsetcheck`;
CREATE TABLE IF NOT EXISTS `charsetcheck` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `default` varchar(45) DEFAULT NULL,
  `expectedresults` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Stand-in structure for view `view1`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `view1`;
CREATE TABLE IF NOT EXISTS `view1` (
`3col` geometry
,`3col1` geometry
,`idnew_table2` int(11)
,`new_table2col` varchar(45)
,`new_table2col1` varchar(45)
,`new_table2col2` varchar(45)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `view3`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `view3`;
CREATE TABLE IF NOT EXISTS `view3` (
`idnew_table2` int(11)
);

-- --------------------------------------------------------

--
-- Structure for view `view1`
--
DROP TABLE IF EXISTS `view1`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view1`  AS  select `3`.`idnew_table2` AS `idnew_table2`,`3`.`new_table2col` AS `new_table2col`,`3`.`new_table2col1` AS `new_table2col1`,`3`.`new_table2col2` AS `new_table2col2`,`3`.`3col` AS `3col`,`3`.`3col1` AS `3col1` from `3` ;

-- --------------------------------------------------------

--
-- Structure for view `view3`
--
DROP TABLE IF EXISTS `view3`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view3`  AS  select `afa`.`idnew_table2` AS `idnew_table2` from `afa` ;

COMMIT;
