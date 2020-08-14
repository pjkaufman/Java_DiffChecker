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
-- Table structure for table `spatialcompositemodify`
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
  `new_table2col` varchar(45) DEFAULT NULL,
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
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `d`
--

DROP TABLE IF EXISTS `d`;
CREATE TABLE IF NOT EXISTS `d` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  FULLTEXT KEY `add_index` (`new_table2col2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `da`
--

DROP TABLE IF EXISTS `da`;
CREATE TABLE IF NOT EXISTS `da` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `das`
--

DROP TABLE IF EXISTS `das`;
CREATE TABLE IF NOT EXISTS `das` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `dfas`
--

DROP TABLE IF EXISTS `dfas`;
CREATE TABLE IF NOT EXISTS `dfas` (
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
-- Table structure for table `dfasdfsa`
--

DROP TABLE IF EXISTS `dfasdfsa`;
CREATE TABLE IF NOT EXISTS `dfasdfsa` (
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
-- Table structure for table `dfasdfsafff`
--

DROP TABLE IF EXISTS `dfasdfsafff`;
CREATE TABLE IF NOT EXISTS `dfasdfsafff` (
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
-- Table structure for table `l`
--

DROP TABLE IF EXISTS `l`;
CREATE TABLE IF NOT EXISTS `l` (
  `id1` int(11) NOT NULL,
  `id2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id1`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `lab`
--

DROP TABLE IF EXISTS `lab`;
CREATE TABLE IF NOT EXISTS `lab` (
  `idnew_table2` int(11) NOT NULL,
  `idnew_table3` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`,`idnew_table3`)
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
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2col1` varchar(45) DEFAULT NULL,
  `new_table2col2` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`,`part2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `r`
--

DROP TABLE IF EXISTS `r`;
CREATE TABLE IF NOT EXISTS `r` (
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
-- Table structure for table `rdsf`
--

DROP TABLE IF EXISTS `rdsf`;
CREATE TABLE IF NOT EXISTS `rdsf` (
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
-- Table structure for table `rdsff`
--

DROP TABLE IF EXISTS `rdsff`;
CREATE TABLE IF NOT EXISTS `rdsff` (
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
-- Table structure for table `rdsffdaf`
--

DROP TABLE IF EXISTS `rdsffdaf`;
CREATE TABLE IF NOT EXISTS `rdsffdaf` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2cffol1ds` varchar(45) DEFAULT NULL,
  `new_table2fcol2afd` varchar(45) DEFAULT NULL,
  `new_tablef2colfaddsa` varchar(45) DEFAULT NULL,
  `new_tablfe2col1afdds` varchar(45) DEFAULT NULL,
  `new_tabfle2col2afdas` varchar(45) DEFAULT NULL,
  `new_tafble2colf` varchar(45) DEFAULT NULL,
  `new_tfable2colad1` varchar(45) DEFAULT NULL,
  `new_ftable2codasl2` varchar(45) DEFAULT NULL,
  `newf_table2coadl` varchar(45) DEFAULT NULL,
  `nefw_table2col1` varchar(45) DEFAULT NULL,
  `nfew_tafble2col2` varchar(45) DEFAULT NULL,
  `new_tabdsafle2col` varchar(45) DEFAULT NULL,
  `new_table2cfdafol1` varchar(45) DEFAULT NULL,
  `new_table2ffdascol2` varchar(45) DEFAULT NULL,
  `new_tabflfe2adfscol` varchar(45) DEFAULT NULL,
  `new_tablffe2coldf1` varchar(45) DEFAULT NULL,
  `new_tabflefff2col2aasfd` varchar(45) DEFAULT NULL,
  `idnew_ftable2` int(11) NOT NULL,
  `new_tfable2col` varchar(45) DEFAULT NULL,
  `new_ftable2cofl1ds` varchar(45) DEFAULT NULL,
  `new_table2colff2afd` varchar(45) DEFAULT NULL,
  `new_table2coflffaddsa` varchar(45) DEFAULT NULL,
  `new_table2cfol1affdds` varchar(45) DEFAULT NULL,
  `new_table2fcol2affdas` varchar(45) DEFAULT NULL,
  `new_tablef2colf` varchar(45) DEFAULT NULL,
  `new_tablfe2colad1` varchar(45) DEFAULT NULL,
  `new_tabfle2codasl2` varchar(45) DEFAULT NULL,
  `new_tafble2coadl` varchar(45) DEFAULT NULL,
  `new_tfable2col1` varchar(45) DEFAULT NULL,
  `new_ftafble2col2` varchar(45) DEFAULT NULL,
  `newf_tabdsafle2col` varchar(45) DEFAULT NULL,
  `nefw_table2cdafol1` varchar(45) DEFAULT NULL,
  `nfew_table2fdascol2` varchar(45) DEFAULT NULL,
  `fnew_tabldfse2adfscol` varchar(45) DEFAULT NULL,
  `new_table2cfoldf1` varchar(45) DEFAULT NULL,
  `new_tafble2col2aasfd` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vldakl`
--

DROP TABLE IF EXISTS `vldakl`;
CREATE TABLE IF NOT EXISTS `vldakl` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2cffol1ds` varchar(45) DEFAULT NULL,
  `new_table2fcol2afd` varchar(45) DEFAULT NULL,
  `new_tablef2colfaddsa` varchar(45) DEFAULT NULL,
  `new_tablfe2col1afdds` varchar(45) DEFAULT NULL,
  `new_tabfle2col2afdas` varchar(45) DEFAULT NULL,
  `new_tafble2colf` varchar(45) DEFAULT NULL,
  `new_tfable2colad1` varchar(45) DEFAULT NULL,
  `new_ftable2codasl2` varchar(45) DEFAULT NULL,
  `newf_table2coadl` varchar(45) DEFAULT NULL,
  `nefw_table2col1` varchar(45) DEFAULT NULL,
  `nfew_tafble2col2` varchar(45) DEFAULT NULL,
  `new_tabdsafle2col` varchar(45) DEFAULT NULL,
  `new_table2cfdafol1` varchar(45) DEFAULT NULL,
  `new_table2ffdascol2` varchar(45) DEFAULT NULL,
  `new_tabflfe2adfscol` varchar(45) DEFAULT NULL,
  `new_tablffe2coldf1` varchar(45) DEFAULT NULL,
  `new_tabflefff2col2aasfd` varchar(45) DEFAULT NULL,
  `idnew_ftable2` int(11) NOT NULL,
  `new_tfable2col` varchar(45) DEFAULT NULL,
  `new_ftable2cofl1ds` varchar(45) DEFAULT NULL,
  `new_table2colff2afd` varchar(45) DEFAULT NULL,
  `new_table2coflffaddsa` varchar(45) DEFAULT NULL,
  `new_table2cfol1affdds` varchar(45) DEFAULT NULL,
  `new_table2fcol2affdas` varchar(45) DEFAULT NULL,
  `new_tablef2colf` varchar(45) DEFAULT NULL,
  `new_tablfe2colad1` varchar(45) DEFAULT NULL,
  `new_tabfle2codasl2` varchar(45) DEFAULT NULL,
  `new_tafble2coadl` varchar(45) DEFAULT NULL,
  `new_tfable2col1` varchar(45) DEFAULT NULL,
  `new_ftafble2col2` varchar(45) DEFAULT NULL,
  `newf_tabdsafle2col` varchar(45) DEFAULT NULL,
  `nefw_table2cdafol1` varchar(45) DEFAULT NULL,
  `nfew_table2fdascol2` varchar(45) DEFAULT NULL,
  `fnew_tabldfse2adfscol` varchar(45) DEFAULT NULL,
  `new_table2cfoldf1` varchar(45) DEFAULT NULL,
  `new_tafble2col2aasfd` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vldakldsa`
--

DROP TABLE IF EXISTS `vldakldsa`;
CREATE TABLE IF NOT EXISTS `vldakldsa` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2cffol1ds` varchar(45) DEFAULT NULL,
  `new_table2fcol2afd` varchar(45) DEFAULT NULL,
  `new_tablef2colfaddsa` varchar(45) DEFAULT NULL,
  `new_tablfe2col1afdds` varchar(45) DEFAULT NULL,
  `new_tabfle2col2afdas` varchar(45) DEFAULT NULL,
  `new_tafble2colf` varchar(45) DEFAULT NULL,
  `new_tfable2colad1` varchar(45) DEFAULT NULL,
  `new_ftable2codasl2` varchar(45) DEFAULT NULL,
  `newf_table2coadl` varchar(45) DEFAULT NULL,
  `nefw_table2col1` varchar(45) DEFAULT NULL,
  `nfew_tafble2col2` varchar(45) DEFAULT NULL,
  `new_tabdsafle2col` varchar(45) DEFAULT NULL,
  `new_table2cfdafol1` varchar(45) DEFAULT NULL,
  `new_table2ffdascol2` varchar(45) DEFAULT NULL,
  `new_tabflfe2adfscol` varchar(45) DEFAULT NULL,
  `new_tablffe2coldf1` varchar(45) DEFAULT NULL,
  `new_tabflefff2col2aasfd` varchar(45) DEFAULT NULL,
  `idnew_ftable2` int(11) NOT NULL,
  `new_tfable2col` varchar(45) DEFAULT NULL,
  `new_ftable2cofl1ds` varchar(45) DEFAULT NULL,
  `new_table2colff2afd` varchar(45) DEFAULT NULL,
  `new_table2coflffaddsa` varchar(45) DEFAULT NULL,
  `new_table2cfol1affdds` varchar(45) DEFAULT NULL,
  `new_table2fcol2affdas` varchar(45) DEFAULT NULL,
  `new_tablef2colf` varchar(45) DEFAULT NULL,
  `new_tablfe2colad1` varchar(45) DEFAULT NULL,
  `new_tabfle2codasl2` varchar(45) DEFAULT NULL,
  `new_tafble2coadl` varchar(45) DEFAULT NULL,
  `new_tfable2col1` varchar(45) DEFAULT NULL,
  `new_ftafble2col2` varchar(45) DEFAULT NULL,
  `newf_tabdsafle2col` varchar(45) DEFAULT NULL,
  `nefw_table2cdafol1` varchar(45) DEFAULT NULL,
  `nfew_table2fdascol2` varchar(45) DEFAULT NULL,
  `fnew_tabldfse2adfscol` varchar(45) DEFAULT NULL,
  `new_table2cfoldf1` varchar(45) DEFAULT NULL,
  `new_tafble2col2aasfd` varchar(45) DEFAULT NULL,
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
-- Table structure for table `vldakldsafds`
--

DROP TABLE IF EXISTS `vldakldsafds`;
CREATE TABLE IF NOT EXISTS `vldakldsafds` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2cffol1ds` varchar(45) DEFAULT NULL,
  `new_table2fcol2afd` varchar(45) DEFAULT NULL,
  `new_tablef2colfaddsa` varchar(45) DEFAULT NULL,
  `new_tablfe2col1afdds` varchar(45) DEFAULT NULL,
  `new_tabfle2col2afdas` varchar(45) DEFAULT NULL,
  `new_tafble2colf` varchar(45) DEFAULT NULL,
  `new_tfable2colad1` varchar(45) DEFAULT NULL,
  `new_ftable2codasl2` varchar(45) DEFAULT NULL,
  `newf_table2coadl` varchar(45) DEFAULT NULL,
  `nefw_table2col1` varchar(45) DEFAULT NULL,
  `nfew_tafble2col2` varchar(45) DEFAULT NULL,
  `new_tabdsafle2col` varchar(45) DEFAULT NULL,
  `new_table2cfdafol1` varchar(45) DEFAULT NULL,
  `new_table2ffdascol2` varchar(45) DEFAULT NULL,
  `new_tabflfe2adfscol` varchar(45) DEFAULT NULL,
  `new_tablffe2coldf1` varchar(45) DEFAULT NULL,
  `new_tabflefff2col2aasfd` varchar(45) DEFAULT NULL,
  `idnew_ftable2` int(11) NOT NULL,
  `new_tfable2col` varchar(45) DEFAULT NULL,
  `new_ftable2cofl1ds` varchar(45) DEFAULT NULL,
  `new_table2colff2afd` varchar(45) DEFAULT NULL,
  `new_table2coflffaddsa` varchar(45) DEFAULT NULL,
  `new_table2cfol1affdds` varchar(45) DEFAULT NULL,
  `new_table2fcol2affdas` varchar(45) DEFAULT NULL,
  `new_tablef2colf` varchar(45) DEFAULT NULL,
  `new_tablfe2colad1` varchar(45) DEFAULT NULL,
  `new_tabfle2codasl2` varchar(45) DEFAULT NULL,
  `new_tafble2coadl` varchar(45) DEFAULT NULL,
  `new_tfable2col1` varchar(45) DEFAULT NULL,
  `new_ftafble2col2` varchar(45) DEFAULT NULL,
  `newf_tabdsafle2col` varchar(45) DEFAULT NULL,
  `nefw_table2cdafol1` varchar(45) DEFAULT NULL,
  `nfew_table2fdascol2` varchar(45) DEFAULT NULL,
  `fnew_tabldfse2adfscol` varchar(45) DEFAULT NULL,
  `new_table2cfoldf1` varchar(45) DEFAULT NULL,
  `new_tafble2col2aasfd` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vldakldsafdsfdsasfd`
--

DROP TABLE IF EXISTS `vldakldsafdsfdsasfd`;
CREATE TABLE IF NOT EXISTS `vldakldsafdsfdsasfd` (
  `idnew_table2` int(11) NOT NULL,
  `new_table2col` varchar(45) DEFAULT NULL,
  `new_table2cffol1ds` varchar(45) DEFAULT NULL,
  `new_table2fcol2afd` varchar(45) DEFAULT NULL,
  `new_tablef2colfaddsa` varchar(45) DEFAULT NULL,
  `new_tablfe2col1afdds` varchar(45) DEFAULT NULL,
  `new_tabfle2col2afdas` varchar(45) DEFAULT NULL,
  `new_tafble2colf` varchar(45) DEFAULT NULL,
  `new_tfable2colad1` varchar(45) DEFAULT NULL,
  `new_ftable2codasl2` varchar(45) DEFAULT NULL,
  `newf_table2coadl` varchar(45) DEFAULT NULL,
  `nefw_table2col1` varchar(45) DEFAULT NULL,
  `nfew_tafble2col2` varchar(45) DEFAULT NULL,
  `new_tabdsafle2col` varchar(45) DEFAULT NULL,
  `new_table2cfdafol1` varchar(45) DEFAULT NULL,
  `new_table2ffdascol2` varchar(45) DEFAULT NULL,
  `new_tabflfe2adfscol` varchar(45) DEFAULT NULL,
  `new_tablffe2coldf1` varchar(45) DEFAULT NULL,
  `new_tabflefff2col2aasfd` varchar(45) DEFAULT NULL,
  `idnew_ftable2` int(11) NOT NULL,
  `new_tfable2col` varchar(45) DEFAULT NULL,
  `new_ftable2cofl1ds` varchar(45) DEFAULT NULL,
  `new_table2colff2afd` varchar(45) DEFAULT NULL,
  `new_table2coflffaddsa` varchar(45) DEFAULT NULL,
  `new_table2cfol1affdds` varchar(45) DEFAULT NULL,
  `new_table2fcol2affdas` varchar(45) DEFAULT NULL,
  `new_tablef2colf` varchar(45) DEFAULT NULL,
  `new_tablfe2colad1` varchar(45) DEFAULT NULL,
  `new_tabfle2codasl2` varchar(45) DEFAULT NULL,
  `new_tafble2coadl` varchar(45) DEFAULT NULL,
  `new_tfable2col1` varchar(45) DEFAULT NULL,
  `new_ftafble2col2` varchar(45) DEFAULT NULL,
  `newf_tabdsafle2col` varchar(45) DEFAULT NULL,
  `nefw_table2cdafol1` varchar(45) DEFAULT NULL,
  `nfew_table2fdascol2` varchar(45) DEFAULT NULL,
  `fnew_tabldfse2adfscol` varchar(45) DEFAULT NULL,
  `new_table2cfoldf1` varchar(45) DEFAULT NULL,
  `new_tafble2col2aasfd` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnew_table2`)
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
,`new_table2col` varchar(45)
,`new_table2col1` varchar(45)
,`new_table2col2` varchar(45)
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

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view3`  AS  select `afa`.`idnew_table2` AS `idnew_table2`,`afa`.`new_table2col` AS `new_table2col`,`afa`.`new_table2col1` AS `new_table2col1`,`afa`.`new_table2col2` AS `new_table2col2` from `afa` ;

COMMIT;
