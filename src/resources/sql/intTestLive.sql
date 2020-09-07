SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `intTestLive`
--
CREATE DATABASE IF NOT EXISTS `intTestLive` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `intTestLive`;

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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `compositemodify`
--

DROP TABLE IF EXISTS `compositemodify`;
CREATE TABLE IF NOT EXISTS `compositemodify` (
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
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `af`
--

DROP TABLE IF EXISTS `af`;
CREATE TABLE IF NOT EXISTS `af` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  `dropme` varchar(45) DEFAULT NULL,
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
  FOREIGN KEY (`id3`) REFERENCES `3`(`idnew_table2`),
  FOREIGN KEY (`id5`,`part3`) REFERENCES `compositeprimarymodification`(`idnew_table2`,`part3`),
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
  `dropme2` varchar(45) DEFAULT NULL,
  UNIQUE KEY `drop_index` (`dropme2`),
  PRIMARY KEY (`idnew_table2`)
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

--
-- Table structure for table `addprimarykey`
--

DROP TABLE IF EXISTS `addprimarykey`;
CREATE TABLE IF NOT EXISTS `addprimarykey` (
  `id` int(11) NOT NULL,
  `column1` varchar(45) DEFAULT NULL,
  `column2` varchar(45) DEFAULT NULL
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
  PRIMARY KEY (`id2`)
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
  PRIMARY KEY (`idnew_table2`,`part3`)
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `droppedgroceries`
--

DROP TABLE IF EXISTS `droppedgroceries`;
CREATE TABLE IF NOT EXISTS `droppedgroceries` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `brand` varchar(45) DEFAULT NULL,
  `disposalArea` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
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
-- Table structure for table `planes`
--

DROP TABLE IF EXISTS `planes`;
CREATE TABLE IF NOT EXISTS `planes` (
  `modelId` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `manufacturer` varchar(45) DEFAULT NULL,
  `plantLocation` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`modelId`)
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
-- Stand-in structure for view `view2`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `view2`;
CREATE TABLE IF NOT EXISTS `view2` (
`idnew_table2` int(11)
,`new_tabdsafle2col` varchar(45)
,`new_table2adfscol` varchar(45)
,`new_table2cdafol1` varchar(45)
,`new_table2coadl` varchar(45)
,`new_table2codasl2` varchar(45)
,`new_table2col` varchar(45)
,`new_table2col1` varchar(45)
,`new_table2col1afdds` varchar(45)
,`new_table2col1ds` varchar(45)
,`new_table2col2aasfd` varchar(45)
,`new_table2col2afd` varchar(45)
,`new_table2col2afdas` varchar(45)
,`new_table2colad1` varchar(45)
,`new_table2coldf1` varchar(45)
,`new_table2colf` varchar(45)
,`new_table2colfaddsa` varchar(45)
,`new_table2fdascol2` varchar(45)
,`new_tafble2col2` varchar(45)
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
-- Structure for view `view2`
--
DROP TABLE IF EXISTS `view2`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view2`  AS  select `rds`.`idnew_table2` AS `idnew_table2`,`rds`.`new_table2col` AS `new_table2col`,`rds`.`new_table2col1ds` AS `new_table2col1ds`,`rds`.`new_table2col2afd` AS `new_table2col2afd`,`rds`.`new_table2colfaddsa` AS `new_table2colfaddsa`,`rds`.`new_table2col1afdds` AS `new_table2col1afdds`,`rds`.`new_table2col2afdas` AS `new_table2col2afdas`,`rds`.`new_table2colf` AS `new_table2colf`,`rds`.`new_table2colad1` AS `new_table2colad1`,`rds`.`new_table2codasl2` AS `new_table2codasl2`,`rds`.`new_table2coadl` AS `new_table2coadl`,`rds`.`new_table2col1` AS `new_table2col1`,`rds`.`new_tafble2col2` AS `new_tafble2col2`,`rds`.`new_tabdsafle2col` AS `new_tabdsafle2col`,`rds`.`new_table2cdafol1` AS `new_table2cdafol1`,`rds`.`new_table2fdascol2` AS `new_table2fdascol2`,`rds`.`new_table2adfscol` AS `new_table2adfscol`,`rds`.`new_table2coldf1` AS `new_table2coldf1`,`rds`.`new_table2col2aasfd` AS `new_table2col2aasfd` from `rds` ;

-- --------------------------------------------------------

--
-- Structure for view `view3`
--
DROP TABLE IF EXISTS `view3`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view3`  AS  select `afa`.`idnew_table2` AS `idnew_table2` from `afa` ;

COMMIT;
