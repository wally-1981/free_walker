CREATE DATABASE IF NOT EXISTS `itinerary` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE `itinerary`;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `itinerary`.`tag` (
  `name` VARCHAR(32) NOT NULL,
  `frequency` INT(8) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  INDEX `frequency_INDEX` (`frequency` ASC));

--
-- Dumping data for table `country`
--

LOCK TABLES `tag` WRITE;

INSERT INTO itinerary.tag (name, frequency) VALUES ('自然', 10);
INSERT INTO itinerary.tag (name, frequency) VALUES ('户外', 102);
INSERT INTO itinerary.tag (name, frequency) VALUES ('蜜月', 322);
INSERT INTO itinerary.tag (name, frequency) VALUES ('踏青', 252);
INSERT INTO itinerary.tag (name, frequency) VALUES ('摄影', 162);

UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
CREATE TABLE `country` (
  `id` tinyint(8) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` binary(16) NOT NULL,
  `name` varchar(48) NOT NULL,
  `chinese_name` varchar(48) NOT NULL,
  `pinyin_name` varchar(48) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;

INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('af70a55ceb4c415c837588081716f8b8'),'China','中国','zhongguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('cc0968e70fe34cc99f5b3a6898a04506'),'America','美国','meiguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('ef72692dbd2b49a9bc73b74b5ce0b0cb'),'RepublicOfKorea','韩国','hanguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('7737001550974bceb0dc62b3c3977db7'),'KingdomOfThailand','泰国','taiguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('2e072b57c16c46de9dd385e0a67e94da'),'DemocraticPeoplesRepublicOfKorea','朝鲜','chaoxian');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('9ce560518c82415ea6bc6b9fdfc3f92e'),'RepublicOfThePhilippines','菲律宾','feilvbin');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('c042f1964dcb4677a0490d2970553c87'),'KingdomOfCambodia','柬埔寨','jianpuza');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('bf4364fa452946ed916bfb25c5d0e9b9'),'TheRepublicOfMaldives','马尔代夫','maerdaifu');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('ac992fff27704cf9a95a208e183d508c'),'Malaysia','马来西亚','malaixiya');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('e5e94197202147e0badcd17bfbda72f7'),'Mongolia','蒙古','menggu');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('3ae5a40430a046328e7596671e7e1622'),'Japan','日本','riben');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('92aec775bd7c440abcd977a2330ee6a6'),'RepublicOfIndia','印度','yindu');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('e10f231aa4094659b0984b5c21bce82a'),'TheRepublicOfIndonesia','印度尼西亚','yinni');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('59cbf43757414500a71b86c8a0da80d4'),'RepublicOfSingapore','新加坡','xinjiapo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('6eff92de4d11425b893dd6c75efbeb10'),'UnionOfMyanmar','缅甸','miandian');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('34b33dc24b2849d88de116275f7bb779'),'SocialistRepublicOfVietnam','越南','yuenan');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('fe80acb2816e45f0a98819caa587c6fc'),'Spain','西班牙','xibanya');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('ce5fc39199eb49c3a41d7011ca079f67'),'Switzerland','瑞士','ruishi');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('72bfb2b60ba94f249c670c54c1f4a2c5'),'Greece','希腊','xila');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('b7216151483048ca9d9c8cc4719c006f'),'FrenchPolynesia','法属波利尼西亚','fashubolixiya');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('11f2ba9a13534b6a9b1255eebe6691f5'),'TheUnitedArabEmirates','阿拉伯联合酋长国','alabolianheqiuzhangguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('41ef66db8a404c35901a7d616cdad9bb'),'NewZealand','新西兰','xinxilan');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('b164126cb75e4d17a9f63e3f05172e73'),'Britain','英国','yingguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('60128b3dbf8b400a9005791e11ad58b3'),'FiJi','斐济','feiji');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('c3299821a1224a8c890ab921d1edb154'),'Australia','澳大利亚','aodaliya');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('ece04b43cbd840c895932b50041bdc18'),'Brazil','巴西','baxi');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('e85a3d8e1f70458a95a89028ada47b42'),'Argentina','阿根廷','agenting');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('7d8fb4b00c37446b8a28fbc7d998a617'),'Netherlands','荷兰','helan');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('8741e296bc86440e9a570bd09ea12f8a'),'SouthAfrica','南非','nanfei');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('26118eabd3c040c0adea84c8370da3ef'),'France','法国','faguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('e2d3f3d669104487908a1b3f8dda89ff'),'Germany','德国','deguo');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('b763086ef4114975b28da5672e4a15c6'),'Canada','加拿大','jianada');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('aafac90dfe26494b834f769eeedec6f0'),'Kenya','肯尼亚','kenniya');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('227451454f784474a8b453c92e14d141'),'TheUnitedRepublicofTanzania','坦桑尼亚','tansangniya');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('b2f93dcef0394598bda64cc1d49b501b'),'Nepal','尼泊尔','niboer');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('7241d6f1b2464a59ba3e98d0706581b9'),'Mexico','墨西哥','moxige');
INSERT INTO itinerary.country (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('f4f5954849114c7ca79577fa5227e6db'),'Egypt','埃及','aiji');

UNLOCK TABLES;

--
-- Table structure for table `province`
--

DROP TABLE IF EXISTS `province`;
CREATE TABLE `province` (
  `id` smallint(16) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` binary(16) NOT NULL,
  `name` varchar(48) NOT NULL,
  `chinese_name` varchar(48) NOT NULL,
  `pinyin_name` varchar(48) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `province`
--

LOCK TABLES `province` WRITE;

INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX(''),'','','');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('03161e050c2448378eb863bfcbe744f3'),'Hubei','湖北','hubei');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),'Guangdong','广东','guangdong');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),'Sichuan','四川','sichuan');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('0351f620296844a896ab59ffa8c4aea5'),'Zhejiang','浙江','zhejiang');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),'Jiangsu','江苏','jiangsu');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('237db8e728c941469c6009d004057caf'),'Yunnan','云南','yunnan');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('958e992e142e46e595f13752c0721ed0'),'Guangxi','广西','guangxi');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),'Shanxi','山西','shanxi');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('3ba28518d135497aa202005fc95fe148'),'Hebei','河北','hebei');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('d93196c059f64f55b54df0789e370c4e'),'Hainan','海南','hainan');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('8f281df5c57142b289b932ef76b9dc5f'),'Xizang','西藏','xizang');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('7522201519804d038be7fe4540bb90bb'),'Fujian','福建','fujian');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('6824b03ab3f1448881b5df4e44b4e686'),'Xinjiang','新疆','xinjiang');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),'Anhui','安徽','anhui');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('c38dbee96c7e4d4c962222dd90cfea39'),'Chongqing','重庆','chongqing');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('79ff0a4ba8a64653b02436878fff00c9'),'Shandong','山东','shandong');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('92d55e093025479db3f64b6aa38de051'),'Hunan','湖南','hunan');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),'Neimenggu','内蒙古','neimenggu');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),'Taiwan','台湾','taiwan');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('f58ef2c6aa164c99a0b03d4967e2c665'),'Ningxia','宁夏','ningxia');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),'Heilongjiang','黑龙江','heilongjiang');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('e50cef252b64401dae8a9785c5729b6b'),'Henan','河南','henan');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),'Liaoning','辽宁','liaoning');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('fb77a12e99ad45c89f88e50aeb028814'),'Qinghai','青海','qinghai');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('d2ee0db3b064495a85a4de256327fc82'),'Jiangxi','江西','jiangxi');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('81a4199786124152b099e5a84607d508'),'Shanxi','陕西','shanxi1');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),'Jilin','吉林','jilin');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),'Guizhou','贵州','guizhou');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),'Gansu','甘肃','gansu');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('472073413a3d46919342158febdd9a78'),'Commonwealthofthenorthernmarianaislands','北马里亚纳群岛自由联邦','beimaliyanaqundaoziyoulianbang');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('8f18986005ee469b9f1dcdf5eb897804'),'Hawaii','夏威夷','xiaweiyi');
INSERT INTO itinerary.province (uuid, name, chinese_name, pinyin_name) VALUES (UNHEX('fbb3821586c04c1abca1edc25ddde4fc'),'Arizona','亚利桑那','yalisangna');

UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` binary(16) NOT NULL,
  `name` varchar(48) NOT NULL,
  `chinese_name` varchar(48) NOT NULL,
  `pinyin_name` varchar(48) NOT NULL,
  `province_uuid` binary(16) NOT NULL,
  `country_uuid` binary(16) NOT NULL,
  `continent_id` tinyint(8) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;

INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('79fd8642a11d4811887dec4268097a82'),'Wuhan','武汉','wuhan',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cda48bcd9ab64669994013897321a3fb'),'Yichang','宜昌','yichang',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7a087f047b83448aa487920a41f3e201'),'Shiyan','十堰','shiyan',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e98c3585f7474eca9b66b58f57362fb7'),'Enshi','恩施','enshi',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('306f9a4de80f49e6914c8278e89a4802'),'Huanggang','黄冈','huanggang',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6e3982a9543f4b65aa38d433007b915a'),'Xiangyang','襄阳','xiangyang',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('29ac81493101448bb346d03a1b43395d'),'Shennongjia','神农架','shennongjia',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('63eff1c00a6e40eeba0dc897ea9c0369'),'Xiaogan','孝感','xiaogan',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('af2a6fbd2a454abf8a17afc99cc61542'),'Suizhou','随州','suizhou',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('97bdfdb0f96e43f6b1f480e5979eb73c'),'Jingzhou','荆州','jingzhou',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b4cef4731ad746cd8ea5d50bfa3ca033'),'Guangzhou','广州','guangzhou',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5d348f1f2bd1470a80f4ec220042ad2e'),'Chengdu','成都','chengdu',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('13cdf0c45ce84957a51730814d139d40'),'Hangzhou','杭州','hangzhou',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6559338141964d1588ceda253a58927d'),'Suzhou','苏州','suzhou',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('64f62d38b90a4fef9380d93914e370ab'),'Guilin','桂林','guilin',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b2c568e0b1214d0183c83b2517a2632e'),'Taiyuan','太原','taiyuan',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('043bd4587ff5475992a23e7f582d7d39'),'Jinzhong','晋中','jinzhong',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('67564df172dc4a819466faa33ed7a6e7'),'Xuzhou','徐州','xuzhou',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('aa1b54a9d8f74c359fa8ce3fa57fe332'),'Nanjing','南京','nanjing',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4cb224035b1d41ea81605ed82a1befa2'),'Lijiang','丽江','lijiang',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('242ec85db42e4986a1e54cb14baea272'),'Datong','大同','datong',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2fd764d6cc2a44bca6adb021974da493'),'Yuncheng','运城','yuncheng',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('fb173342ccfd45f49bda3b92cdc884ee'),'Xinzhou','忻州','xinzhou',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9fe09980dca346c6a6eae6538273da13'),'Linfen','临汾','linfen',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bcd855ea64764924844d0238201986a1'),'Lvliang','吕梁','lvliang',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6cb276170bd846f8baf3da9c70005d9e'),'Jincheng','晋城','jincheng',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('57bddb588cfc4b43a8ee3cb729ff8290'),'Changzhi','长治','changzhi',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c94829d41f414f418c236c701ab1b41b'),'Yangquan','阳泉','yangquan',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('34057abf82764f12808a5e62ced36233'),'Dali','大理','dali',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3e0c9c2c2bde4930a8ffe6b613e7cb7f'),'Kunming','昆明','kunming',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2ed030431302401488bc305de1398691'),'Qinhuangdao','秦皇岛','qinhuangdao',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cbebd841e3ff4c0a8248d8954389b01e'),'Sanya','三亚','sanya',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2a8de6af69fd4ce3b57924ac801fe3fb'),'Shenzhen','深圳','shenzhen',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d48512f6564043508135415d6620d29b'),'Zhuhai','珠海','zhuhai',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3a2dd13674fc484a8935bb5d8857369d'),'Xianning','咸宁','xianning',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7b76fb690fc041c184c5d32586724119'),'Changzhou','常州','changzhou',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('da35d2581d22417889babc1eccea7392'),'Wuxi','无锡','wuxi',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d8efd14e2b634e38871669b06bf17db6'),'Lasa','拉萨','lasa',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('083490a2e9044c02b22f145a1859c802'),'Hiakou','海口','haikou',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a87a271bd8554bd3858ba974d43c3a8e'),'Xiamen','厦门','xiamen',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('815221d2daf64b41aaa814be17de1c0f'),'Linzhi','林芝','linzhi',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('83dbaa4aff3e47d9a74938836738745b'),'Diqing','迪庆','diqing',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4252fd0ae7474f89b981feb62cd02b9e'),'Xishuangbanna','西双版纳','xishuangbanna',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('90b89ea9796b495aa864d9dd7eaf84d5'),'Aletai','阿勒泰','aletai',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bb6190f349bb4cc0bb65e13ffe26e740'),'Wuhu','芜湖','wuhu',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3181e3719ce84badbbb7af4e4e24de0b'),'Chizhou','池州','chizhou',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f8c2a4fa29f448549a3c285978899572'),'Huangshan','黄山','huangshan',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('dc13e8ff295d49db993163078dafcf69'),'Sansha','三沙','sansha',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5c9f4cf3a9f64eb9b0bae1b9b9e9bdd1'),'Yantai','烟台','yantai',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bbf096770ded40c1b055348bd17ed498'),'Weihai','威海','weihai',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('810b9ced531f494c9455088c80572381'),'Xuancheng','宣城','xuancheng',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d0c2cc02156e46569437814bb0ad0161'),'Luan','六安','luan',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2dca647deb88438eb871154a88470e5d'),'Qingdao','青岛','qingdao',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c1cefafc833f404eb630d2a07be8cbcd'),'Xiangxitujiamiaozuzizhizhou','湘西土家族苗族自治州','xiangxitujiamiaozuzizhizhou',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('163223c96f5b4302b43878d2a1dab58b'),'Beihai','北海','beihai',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ede7487afde9452aa0689a82437cb19b'),'Nanning','南宁','nanning',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1690a9cc42884bc7b30128baa2dc52a3'),'Chongzuo','崇左','chongzuo',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8fe079b6f23247cfa539c6164ce99bae'),'Dongxing','东兴','dongxing',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('272c6a6766654a20b009c5abc71d1183'),'Bama','巴马','bama',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e5dc73cd96a843f68243d927f013f080'),'Baise','百色','baise',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8d1c3c6d7ae2467f88718b273e7f3e3c'),'Abazangzuqiangzuzizhizhou','阿坝藏族羌族自治州','abazangzuqiangzuzizhizhou',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ecc7bdf514714ef09ff4c857f7ab2a7b'),'Leshan','乐山','leshan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ba34f45f6efc492c92256456fd3b1006'),'Emeishan','峨眉山','emeishan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('032e5d7bbe5a497ebd65ce0bbf70ca9a'),'Baotou','包头','baotou',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('252aaef8ad404393bbe18c238e289a1b'),'Liangshanyizuzizhizhou','凉山彝族自治州','liangshanyizuzizhizhou',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('52692442823341a296e1d963527010cf'),'Anshu','安顺','anshun',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a7e87c87dbad439eb738f81c7b63c647'),'Xingyi','兴义','xingyi',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ba16b3070cff4fd79f9ca78ae0aa1faa'),'Guiyang','贵阳','guiyang',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2cf5cf309eb04a7aa06921178b2ecc88'),'Tongren','铜仁','tongren',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('09c946bc2e16426da37283a1e97b7fa1'),'Fenyang','汾阳','fenyang',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9ce60f07fba54cee96d031ed679e679e'),'Rikaze','日喀则','rikaze',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4cd1a65364cc4d13824d13fe0e7ed4ca'),'Kashi','喀什','kashi',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bf9c0b8c1eff4220b1eac20716777f6b'),'Kaili','凯里','kaili',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('437ec18093244a6eab0ba9f75e734398'),'Dujun','都匀','dujun',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('88aaa92e1ed64d9aa0014761f821f50c'),'Taian','泰安','taian',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f6ec93c38c304f57a0cfdbc3c2504294'),'Shaoguan','韶关','shaoguan',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ab3f9d5b1e384030a3bc8de1d71121d0'),'Huizhou','惠州','huizhou',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2bb4084785a945a4a1f2988e7b579a56'),'Qufu','曲阜','qufu',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('918df48cb65c424a84cb6406b045b718'),'Jinan','济南','jinan',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a460c96a38de4eac9e001c68c6dc8889'),'Guanghan','广汉','guanghan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f764ee724e5542c6b0a48b704bf774a2'),'Jiujiang','九江','jiujiang',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('917cde6a9af447a19e36c7f8bfebcf4b'),'Xinyang','信阳','xinyang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('739b2b2946d94cf4806bf70a8ac29abb'),'Zhujiang','珠江','zhujiang',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('40c4e32c08394201bde908a9251cc305'),'Ali','阿里','ali',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5b149f60e8314bffb2a4216e2c6d3ae7'),'Geermubu','格尔木布','geermubu',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9bb41f2aaceb459196feb3d2bac59fdb'),'Changdou','昌都','changdou',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('475e55eec69e401aaa5ff0270ac51190'),'Naqu','那曲','naqu',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('55d61f5561b344288bc383a8959067fd'),'Shannan','山南','shannan',UNHEX('8f281df5c57142b289b932ef76b9dc5f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('dd8954064a5c4e579fda3263be8465ad'),'Bali','巴厘岛','balidao',UNHEX(''),UNHEX('e10f231aa4094659b0984b5c21bce82a'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ba76019ef93d4559a360bcc5324ca72f'),'Saipanlsland','塞班岛','bansaidao',UNHEX('472073413a3d46919342158febdd9a78'),UNHEX('cc0968e70fe34cc99f5b3a6898a04506'),'3');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b42d019a6da84dd2ae5f83d2851b5ed6'),'Dubai','迪拜','dibai',UNHEX(''),UNHEX('11f2ba9a13534b6a9b1255eebe6691f5'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2de8eb0983e8417ca9f34f798c1385dd'),'Tashikeergantaji','塔什库尔干塔吉','tashikuergantaji',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9cbdd4c3120544e8be3fd96f8d14d7d5'),'Dalian','大连','dalian',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('adfd5c5f4ad44c5096f4f837ac71d74d'),'Shijiazhuang','石家庄','shijiazhuang',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4213d4a3b47149b889918fafe87f80fd'),'Tangshan','唐山','tangshan',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('72a8ac18b4a94f6f9fa938f66247ef71'),'Handan','邯郸','handan',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f38f75bc98eb4378a78a6ba7408f2ccb'),'Xingtai','邢台','xingtai',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('addb628881a84f92abf50cca38905c32'),'Baoding','保定','baoding',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('921d9b7825ab48cea4c4b2faf537095c'),'Zhangjiakou','张家口','zhangjiakou',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('16473ee35fe34f258d22426112dc2949'),'Chengde','承德','chengde',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0ee59b54540f4e0c8905dc60a0eef337'),'Cangzhou','沧州','cangzhou',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ca826d55098749de8ff2dd7b0b19c532'),'Langfang','廊坊','langfang',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c418c34878bd4259a61f70989a98830c'),'Hengshui','衡水','hengshui',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('146301ab378940cf87156a846a85792d'),'Zhengzhou','郑州','zhengzhou',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7a46e31f07d34d4fa71f433aa9af0d77'),'Anyang','安阳','anyang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3017435ebd4c4f46aa186658797bb35e'),'Xinxiang','新乡','xinxiang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a3d2d482fba34413ad8c242f55ceba57'),'Xuchang','许昌','xuchang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d98c8473614d4807a3faaae560cb99bb'),'Pingdingshan','平顶山','pingdingshan',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b69b683d2c924d56bd182618bb976187'),'Jiyuan','济源','jiyuan',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e797e3cdabf84e768ecc9189d79320ae'),'Nanyang','南阳','nanyang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('156a42602a394c47a03b2e6cd72e6242'),'Kaifeng','开封','kaifeng',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('910f915202ca4a7089cce7befeaaf885'),'Luoyang','洛阳','luoyang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8a951018fca243dfbc035849bc8d4d15'),'Shangqiu','商丘','shangqiu',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('44e499d9f9034736afa1fe66e5bb94ee'),'Jiaozuo','焦作','jiaozuo',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ca4f7e39a54f48d18c682969cd71c5d2'),'Hebi','鹤壁','hebi',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9f07d05fb7504a808db6df0d2016d985'),'Puyang','濮阳','puyang',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('203e9c0b00f34737a6329b407ed6151e'),'Zhoukou','周口','zhoukou',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7f7b990476fe4fbbaf77a9ddee9eb527'),'Luohe','漯河','luohe',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b66feb4f4690451286927b76f037fdde'),'Zhumadian','驻马店','zhumadian',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('432c6fac3a15414296ad2b34705540c7'),'Sanmenxia','三门峡','sanmenxia',UNHEX('e50cef252b64401dae8a9785c5729b6b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d00d90ca724641659238527c49691158'),'Honghe','红河','honghe',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('42913015ebd34c85a95e001d8865fcbd'),'Qujing','曲靖','qujing',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f1548e97e36e43e287e7356abd2646ce'),'Baoshan','保山','baoshan',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3a3dd6e7ac7948abb0aedc88b64c5164'),'Wenshan','文山','wenshan',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('adab7fecdaaa4de1a18207001ee2b5ee'),'Yuxi','玉溪','yuxi',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('91660cf90d094a40a607d99be0e43a74'),'Chuxiong','楚雄','chuxiong',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e7113468d72d459c9ab2e153e43c8c53'),'Puer','普洱','puer',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('48a5170777e540249a963ea2d7d4f333'),'Zhaotong','昭通','zhaotong',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('068625796692483aa155211e0526a88b'),'Lincang','临沧','lincang',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('21621bb9812748d4aae1b0f84f1e3f7f'),'Nujiang','怒江','nujiang',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1377729c2001403baa3ad7312267871c'),'Dehong','德宏','dehong',UNHEX('237db8e728c941469c6009d004057caf'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('fd9849b5ced843ae8c9552f6683ed458'),'Shenyang','沈阳','shenyang',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9b2c470f8af64200a851512bd690cc21'),'Anshan','鞍山','anshan',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2d89f59e7a144d15833498ef0087e0b9'),'Fushun','抚顺','fushun',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b1f3221a3b054f0fbf65808f38cfe6ca'),'Benxi','本溪','benxi',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('674aa7546258443dac4660e4237f9df7'),'Dandong','丹东','dandong',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3ee88947efa84e8f8cc84e118bfa9c35'),'Jinzhou','锦州','jinzhou',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0a86a6c2102145dd8ea0bd3c6bbac552'),'Yingkou','营口','yingkou',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9364033b8d5e4523b785d52df7b450b7'),'Fuxin','阜新','fuxin',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('dd016404b2714c7ca1deeb9844b2ebf9'),'Liaoyang','辽阳','liaoyang',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e076b04966ff4bb09d82defaf35cba26'),'Tieling','铁岭','tieling',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d62544d4bae24864a843d27b1529b6d8'),'Chaoyang','朝阳','chaoyang',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('44d84b426655482382bd6e637ad17851'),'Panjin','盘锦','panjin',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1d8f8bf2fa0b4d96ae87acf1e4323b8e'),'Huludao','葫芦岛','huludao',UNHEX('4d1a492e0dda4431b8e19ca1b2028069'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7cd99257678245a8a4fd49f7e0026ba9'),'Haerbin','哈尔滨','haerbin',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('409f08ab751a43b9b644a9ab28602ca6'),'Qiqihaer','齐齐哈尔','qiqihaer',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cb53527a6bfd468ab7d16a0e5ebd7dad'),'Mudanjiang','牡丹江','mudanjiang',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a4310ce72c0e45aa95e6b8f0dbb9f0ca'),'Jiamusi','佳木斯','jiamusi',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('450875cfda834479804b1821fc0e57c8'),'Suihua','绥化','suihua',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ea87895426cd4bb79239c455257f2ecf'),'Heihe','黑河','heihe',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7fa108f9ac2f4a438fe50d80455c8f09'),'Daxinganling','大兴安岭','daxinganling',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2e1c4212b64145a79058735262d231ca'),'Yichun','伊春','yichun',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f5badff01a904305b969d20f5b7572f8'),'Daqing','大庆','daqing',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7aa353bc9b314c588b415049cd8a5a4c'),'Qitaihe','七台河','qitaihe',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('23dc24475a0041dea397e32041505ce6'),'Jixi','鸡西','jixi',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('70119492def04d83bc800e9598bf8966'),'Hegang','鹤岗','hegang',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a57f33d79e3744b599b27ed250d2dd02'),'Shuangyashan','双鸭山','shuangyashan',UNHEX('b2e4bc62e7844cc0b499f74bd6b90df0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('675b8393ac04418786ccb1d1618f33f1'),'Changsha','长沙','changsha',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e112197cf11447f1abd26e8014fe3bcf'),'Xiangtan','湘潭','xiangtan',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d5be28628ece419c9bc9e23a697ab8e1'),'Zhuzhou','株洲','zhuzhou',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e581d01f7da647cda21d731ba7090483'),'Hengyang','衡阳','hengyang',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('790644e75ca74b88a9852f11d633fae7'),'Chenzhou','郴州','chenzhou',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('496a7e0b7e0f4d63acfdeb6548238a89'),'Changde','常德','changde',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cc3c370d4ebe431cae9b1a176c644405'),'Yiyang','益阳','yiyang',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('be5972fe3e8b4f8d9ce69463dc8e8d16'),'Loudi','娄底','loudi',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('49d838c5ad53495986b561de0bc2f05c'),'Shaoyang','邵阳','shaoyang',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f75a9cca331b4effbfd9f8ba5c81d92e'),'Yueyang','岳阳','yueyang',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9874bde3cd1e4af8bc9bfb8651557b23'),'Zhangjiajie','张家界','zhangjiajie',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5a2f32b8b44f4ecc951e2abf1c46718e'),'Huaihua','怀化','huaihua',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('43fad94ba94e4c858badb6cc11a428db'),'Yongzhou','永州','yongzhou',UNHEX('92d55e093025479db3f64b6aa38de051'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c9c119facdb040fe886b673ed5dc6812'),'Hefei','合肥','hefei',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8832b94eb9104014b2f80e81eb8a352c'),'Bengbu','蚌埠','bengbu',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0acff68462b64393aa6dea42e22a8127'),'Huainan','淮南','huainan',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3a31aaaf71bf4d5ea803916d39acf472'),'Maanshan','马鞍山','maanshan',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c185d1ca9b4b4eae8eadf0581723662d'),'Anqing','安庆','anqing',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5f1577dff9f5483092b553b56cb155b1'),'Suzhou','宿州','anhuisuzhou',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('33c84df07f5f4e2d852a4588082fa8b1'),'Fuyang','阜阳','fuyang',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7ad5536c5eb64b0590cdaad6a72c8280'),'Bozhou','亳州','bozhou',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6a06bfda7f974f2082305b5aa5bc1196'),'Chuzhou','滁州','chuzhou',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('666a3b84ee3f4c9ea6cbab15fa8d0a7e'),'Huaibei','淮北','huaibei',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ffe841868e204927b08b8c3cef2c1a63'),'Tongling','铜陵','tongling',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e44214607f72498d891a88a5ad574fc7'),'Chaohu','巢湖','chaohu',UNHEX('eb58e961e7fe4748b78d7de940e63d6c'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cbd2673106704f8693b3450bdd85a860'),'Zibo','淄博','zibo',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('67e329425df54dc6a8adbe2ec53584cf'),'Dezhou','德州','dezhou',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1511f0bdba09455c832aa0be1344a544'),'Weifang','潍坊','weifang',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('779cfd0939284bec9b8472dc0065e423'),'Jining','济宁','jining',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('52bb817327404d60be40e600a190adf1'),'Linyi','临沂','linyi',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b0e57111fd9f4c319e44a97d9b5e80cc'),'Heze','菏泽','heze',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('936d660130dc4e0ba214cec9289a7f96'),'Binzhou','滨州','binzhou',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9897e49cb2c44c28bec8bf67779af3aa'),'Dongying','东营','dongying',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e0a50fdf06164b6bac01ceda4b6edebe'),'Zaozhuang','枣庄','zaozhuang',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bcb87f65f8124f20a09ca93c702e0ebf'),'Rizhao','日照','rizhao',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('86cb0f7fb60648b7bb15f8ec40357f4c'),'Laiwu','莱芜','laiwu',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1fd11d770f204817975b810bbeeee24c'),'Liaocheng','聊城','liaocheng',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0493faa139e743b7988b657320fa08d8'),'Wulumuqi','乌鲁木齐','wulumuqi',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('544395436d484e5395100b586703427b'),'Kelamayi','克拉玛依','kelamayi',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0f96be35817349a58d32584234c48735'),'Shihezi','石河子','shihezi',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d190799e05654b87bae5aff848eebb13'),'Changji','昌吉','changji',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7fef9538563d45079821397630b44124'),'Tulufan','吐鲁番','tulufan',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f43850c59da6442cb4ee423f26177776'),'Bayinguoleng','巴音郭楞','bayinguoleng',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f55373e9fa064432b2ec698c51ece30e'),'Akesu','阿克苏','akesu',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4ab0d664185c41bab4774853824ce828'),'Yili','伊犁','yili',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e431d0408efc435d9a8529468b8bc2a6'),'Tacheng','塔城','tacheng',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ce0a0a3b5b67444a85e6030e4a4d1f21'),'Hami','哈密','hami',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9403515d602749c3b531a946d42b54f4'),'Hetian','和田','hetian',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('70faf89ea4bf4d999e190e1de17a5201'),'Kezile','克孜勒','kezile',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3375de126abd4a688e97bd4f4fd8298d'),'Boertala','博尔塔拉','boertala',UNHEX('6824b03ab3f1448881b5df4e44b4e686'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3edb356d7e41436abeb6e25573977c5a'),'Zhenjiang','镇江','zhenjiang',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('94370dd0ad3246e6a7596409fd0015e4'),'Nantong','南通','nantong',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('12a32773554047f08048e0125854b991'),'Yangzhou','扬州','yangzhou',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('29b1ca41f14348d6b5a5bcb9c6a73a2a'),'Yancheng','盐城','jiangsuyancheng',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1b54069f42294060a1bfed0afb3939cb'),'Huaian','淮安','huaian',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c7827baba6544d43a0133da6f944caec'),'Lianyungang','连云港','lianyungang',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c260ba0cd3cc4fcb85db8188240a1761'),'Taizhou','泰州','taizhou',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('17466ecd00e44830bf611c76c7d2e268'),'Suqian','宿迁','suqian',UNHEX('28ce7fecb23941fc8e808bd84e48fb7b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bc99d713c8c348179bb5d01ede734cc5'),'Huzhou','湖州','huzhou',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('57a9db1f428148dd973cbf897290d43e'),'Jiaxing','嘉兴','jiaxing',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c6114f0ccc4f4bc5bf07e8d09ed31bbe'),'Ningbo','宁波','ningbo',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8f730bc4ac294ad5a03c2c85d08a5c74'),'Shaoxing','绍兴','shaoxing',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bc7f0c5275da488f9da2fd8b2b9ecc63'),'Taizhou','台州','zhejiangtaizhou',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('efe9e0e27daa4732b7eaebbec4712d97'),'Wenzhou','温州','wenzhou',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8e393cb0b58e404cbf12ea2b4c5ae01d'),'Lishui','丽水','lishui',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b8cbe609b3f240c99e18482abd7af289'),'Jinhua','金华','jinhua',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('43419baf48c14d6a8592fc5f27867532'),'Quzhou','衢州','quzhou',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('31fb2777c39c410f9bef31e2cb4b347a'),'Zhoushan','舟山','zhoushan',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d03dbb8acd9d4eebb7c982d084bb5221'),'Nanchang','南昌','nanchang',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2399294f0a104d9e9336a2fac9875e4f'),'Shangrao','上饶','shangrao',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('85a8e5f1794047a39bfcff48a38a77d9'),'Fuzhou','抚州','jiangxifuzhou',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2a8195c516a74064ab7908e403eceb97'),'Yichun','宜春','jiangxiyichun',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e278026ba6da47e9afd6fd9c08c48f3a'),'Jian','吉安','jian',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c0f207a6e40748ea904b55fe23a6ba29'),'Ganzhou','赣州','ganzhou',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('52fa4bb1f45649ffbf6f4adbf9c67d0b'),'Jingdezhen','景德镇','jingdezhen',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('25432e7a5608491898a74588253342ee'),'Pingxiang','萍乡','pingxiang',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e8a4a5d12204444690befc391b3d0a58'),'Xinyu','新余','xinyu',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f1a0048777ad4ecf8c02504dcefecb64'),'Yingtan','鹰潭','yingtan',UNHEX('d2ee0db3b064495a85a4de256327fc82'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3427380e3e814433abe79c02f0c1a13d'),'Ezhou','鄂州','ezhou',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5ff2f686874844ce88f611764140dbb4'),'Huangshi','黄石','huangshi',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c548337e0bc842fd8617f7f3bcc521e0'),'Jingmen','荆门','jingmen',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6925521cabb74b83aa7aa8a9ce4d20c4'),'Tianmen','天门','tianmen',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b1adc7e625ac4dccbd1d978d7265e581'),'Xiantao','仙桃','xiantao',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8ac102502873417b9321a04888fea43f'),'Qianjiang','潜江','qianjiang',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('00457e35c98f42d4b5bc02ea73a6ea49'),'Liuzhou','柳州','liuzhou',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('54431983c9064d78ba45b5a8b1403b82'),'Laibin','来宾','laibin',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c55d33f3f0b84c53a80814a77b26b24f'),'Wuzhou','梧州','wuzhou',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4b6e98cd149441929c725da559f9dd7a'),'Hezhou','贺州','hezhou',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('060627c3c180466b8f3e81ce386cc0ac'),'Guigang','贵港','guigang',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('acb0ac69030144419d7ae23ebf80106e'),'Yulin','玉林','yulin',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('845e82d1d10d43939f125759b97696c0'),'Qinzhou','钦州','qinzhou',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e6b98c155ccd40e797babfbdb7eeca64'),'Hechi','河池','hechi',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5b526142064e45f0be3744dce0da2b8f'),'Fangchenggang','防城港','fangchenggang',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1b2dfbf94fbf415bbb8561bc3ded2a58'),'Lanzhou','兰州','lanzhou',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f2a7c82aff4040119d5056a0aad545aa'),'Dingxi','定西','dingxi',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('91c41006a7a74817a4a6945268566a2b'),'Pingliang','平凉','pingliang',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('11eeb8d7a1a24b559ca1b6c2ef5a9d87'),'Qingyang','庆阳','qingyang',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('85dd62d8ea6e425e9bddf16e874a242e'),'Wuwei','武威','wuwei',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9ec3e718121848eb9ce3a6eaacad85dd'),'Jinchang','金昌','jinchang',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2010060d6f794b2aad4a267d3040794e'),'Zhangye','张掖','zhangye',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d91a050b28bb440b8dc4e5761222db5e'),'Jiuquan','酒泉','jiuquan',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d61b2cb4944a44e1a311177e573b5c0c'),'Tianshui','天水','tianshui',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cdf3ccb705034f989ee51483cf353b83'),'Longnan','陇南','longnan',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8af5d693e9bd49e8b7e1792529279a1a'),'Linxia','临夏','linxia',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b5a1e5dd27994cca8192a32662713969'),'Gannan','甘南','gannan',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('85327c27717448328454293f33df21a5'),'Baiyin','白银','baiyin',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0c3d0860e3ef4f65912014b60eab07a8'),'Jiayuguan','嘉峪关','jiayuguan',UNHEX('c4bc835c80464b83826ed5bf535a1d2a'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cc989782f2864ee98f092b09146b427c'),'Shuozhou','朔州','shuozhou',UNHEX('0dabafcd8bdb4746bb5609f8fbcac7b5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b3381fd955554af298148c0675a15551'),'Huhehaote','呼和浩特','huhehaote',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1ca7e26a9e104de98289f02d352fa39b'),'Wuhai','乌海','wuhai',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ffe36e838b1f40b2914ed61cf8d216a7'),'Wulanchabu','乌兰察布','wulanchabu',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d296a425c0da4eee9e5bf1bdafe2d178'),'Tongliao','通辽','tongliao',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1f61377966cf422aa6f50b45355f7eb2'),'Chifeng','赤峰','chifeng',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c77f2fb344d8428eb978693486adede0'),'Eerduosi','鄂尔多斯','eerduosi',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('933b87cda8a5492d89d0ab4db26a9182'),'Bayannaoer','巴彦淖尔','bayannaoer',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1a6df93e62d64458922b0f103fc764cb'),'Xilinguole','锡林郭勒','xilinguole',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('98cfa55cfcde423b87d070789b172256'),'Hulunbeier','呼伦贝尔','hulunbeier',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('018d5a261ef544478a84fe8bf87868f3'),'Xingan','兴安','xingan',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('99ff609685de42dc8c00f2956953522d'),'Alashan','阿拉善','alashan',UNHEX('a1672e3a08614b6896ffbfb3f89c9c0b'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('154b2357da8d44cf976f6a93e631443b'),'Xian','西安','xian',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('326ba547db9240d28f5eaa459d0fd18f'),'Xianyang','咸阳','xianyang',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d17047eed08e4402907f00a46b90a327'),'Yanan','延安','yanan',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bc16b60dd678414f85a12ede2d24edca'),'Yulin','榆林','shanxiyulin',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7898509fb4b54c69aefb014475ca3665'),'Weinan','渭南','weinan',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('09f6c84f0be8430b8b447c4bc5e65125'),'Shangluo','商洛','shangluo',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ad7c7621da094f83b28667fae1d4b644'),'Ankang','安康','ankang',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('57d83765d96b4c5a8a9977119fd74111'),'Hanzhong','汉中','hanzhong',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e799b19c036c4b5ba8f04b8263b5e554'),'Baoji','宝鸡','baoji',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9e7a34faf4974bca900bd8b862b021e9'),'Tongchuan','铜川','tongchuan',UNHEX('81a4199786124152b099e5a84607d508'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('01815a6e067f4cd98d19bf328d4cefe4'),'Changchun','长春','changchun',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('420536b24dbf464f90a0a732e9c69a2b'),'Jilin','吉林','jilinjilin',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bb2ae9ceb7304413a0c4bc1e72d93dd5'),'Yanbian','延边','yanbian',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6ac9fd82c41c4d3ba724ff1143294469'),'Siping','四平','siping',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('79a4b517bc6d40e6a3f3a26ae8e97fac'),'Tonghua','通化','tonghua',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('37e26d5d97124966aea755ff9a01845e'),'Baicheng','白城','baicheng',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('57a5a2ffac6a405f8525e62c0cea2a32'),'Liaoyuan','辽源','liaoyuan',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('05c57e0a960a449196a3d1a7334c4954'),'Songyuan','松原','songyuan',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('54b3b6246c2a4e3587321dc780f225a3'),'Baishan','白山','baishan',UNHEX('4cd4424b0f1e426b99319c56b73ff86e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5ca10067a6984c96a1445122fdad2a5b'),'Fuzhou','福州','fuzhou',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e18c580da8b6454984814d6886ed3a2f'),'Ningde','宁德','ningde',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('dc8d5317fa924a8f81a21ce8b35cd0e0'),'Putian','莆田','putian',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d5e04c89e6a643c2bf6010d5f76d4046'),'Quanzhou','泉州','quanzhou',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a8f0dd1793fb412399eb4b78f4812396'),'Zhangzhou','漳州','zhangzhou',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0471bfe58e6f4f9fb3019e6f64613e5d'),'Longyan','龙岩','longyan',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d8aa6f1ad1b5495f8329eeeaf683be92'),'Sanming','三明','sanming',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b0fe3d25a6404682bc86ab19dd06ef12'),'Nanping','南平','nanping',UNHEX('7522201519804d038be7fe4540bb90bb'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2dd642e7f1cb458bab03dcb6d33fa7e0'),'Zunyi','遵义','zunyi',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f8963b21b1e948c6a5de31f7dd4b6427'),'Bijie','毕节','bijie',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0c8f4c9c58134b3a8d037282316c2ec4'),'Liupanshui','六盘水','liupanshui',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d138f99d675447a29f0c24664301dfb3'),'Meizhou','梅州','meizhou',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a85661685e7847178b52ce93906d90a4'),'Shantou','汕头','shantou',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b7aac11c4c0540faa3a64f6dc7b4b5f2'),'Foshan','佛山','foshan',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('406584fef10549b6b3ba5a121e580f63'),'Zhaoqing','肇庆','zhaoqing',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5a59cdd2adca4fc5b003f9ce546deb80'),'Zhanjiang','湛江','zhanjiang',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('08653d6c6da8461aaa78b0e7775fc2b9'),'Jiangmen','江门','jiangmen',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('c64b698cd4da4ca1b0af5246defba050'),'Heyuan','河源','heyuan',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('32352ab46a81409ab460a0dea62c1e87'),'Qingyuan','清远','qingyuan',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2b9a3134ce754ed4a78ea7038f788720'),'Yunfu','云浮','yunfu',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3136d7be2068476ebc705364e303b7c5'),'Chaozhou','潮州','chaozhou',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('94e9704941ed4d04a63ac62fc890080d'),'Dongguan','东莞','dongguan',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f1fd073d677b44c8b0327551942cba5b'),'Zhongshan','中山','zhongshan',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('02df509e11f54b5da97148e970f25568'),'Yangjiang','阳江','yangjiang',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0a097e88ee84420687e35735cb288a93'),'Jieyang','揭阳','jieyang',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bffd3ddba78b4957afd40194ba779453'),'Maoming','茂名','maoming',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a391bd26033a45e8bc3b42c961ec7e95'),'Shanwei','汕尾','shanwei',UNHEX('5a174ac6fb7e455cab48ea6fa7482b9e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7b24d05570514c78870cc7b4adac21b3'),'Xining','西宁','xining',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6a19ca8f665f43c29f5675fb4510ff40'),'Haidong','海东','haidong',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0371cc69767f45689b2d0890da534d5f'),'Huangnan','黄南','huangnan',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5b353119790f4e249ab7cef4ad8f36b4'),'Hainan','海南','qinghaihainan',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2890740b85d94a2d82306d35fa1e2fc9'),'Guoluo','果洛','guoluo',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3e24c3cb93dc4079894f57c7b71cdd49'),'Yushu','玉树','yushu',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a1fa2ff0bdd54f289618a190e44064c0'),'Haixi','海西','haixi',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('bed80ccb681b43d381f3fb77caafc6e1'),'Haibei','海北','haibei',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f1e0ce78bb954bafbf13a1da9544e3d1'),'Geermu','格尔木','geermu',UNHEX('fb77a12e99ad45c89f88e50aeb028814'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('97b72aa1968d46f6b450825f8eb67c1e'),'Panzhihua','攀枝花','panzhihua',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4aec8d116e0b456daf094f0ff7fde54d'),'Zigong','自贡','zigong',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ed79f782929a41979e85f11a9af1889a'),'Mianyang','绵阳','mianyang',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5b4568c2368c4ac49a89b13508039824'),'Nanchong','南充','nanchong',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2aa73aee10c44d0bbb50aef12b3d2227'),'Dazhou','达州','dazhou',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0bb9f590e11441e98e53f45317096878'),'Suining','遂宁','suining',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('04972e711f3a42bf82535bc1301cc14d'),'Guangan','广安','guangan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9bc2ffd7cd024b628d5004d6a8dcc1c8'),'Bazhong','巴中','bazhong',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6894eafba21d4acba85d298d211a89da'),'Luzhou','泸州','luzhou',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('64a84fb9ee9649df885e47fdce69c474'),'Yibin','宜宾','yibin',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5629613e606344ba8b4fefb4df396f02'),'Neijiang','内江','neijiang',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('4cf96eaab0ba481ab365985cd77a595d'),'Ziyang','资阳','ziyang',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('3006cf02174847d4b478f6742f3b30ba'),'Meishan','眉山','meishan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1aefc55703444a6d909c488a9a314df9'),'Yaan','雅安','yaan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('fc313b12f7b8418aa31134d74eb61dbf'),'Ganzi','甘孜','ganzi',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d09351d2347a4374b24bbba77613b7fe'),'Deyang','德阳','deyang',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('82aaaac8bbba49f688e4d082378a92e6'),'Guangyuan','广元','guangyuan',UNHEX('9ccfbe90bfe24f38ad7cfb429a95354f'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7f0246f0224c45308e56b9cca6ea65eb'),'Yinchuan','银川','yinchuan',UNHEX('f58ef2c6aa164c99a0b03d4967e2c665'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('18fa5cd9e220429bbfb33401f6a838c5'),'Shizuishan','石嘴山','shizuishan',UNHEX('f58ef2c6aa164c99a0b03d4967e2c665'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9e45535523f14cde82a443a4f26afc7c'),'Wuzhong','吴忠','wuzhong',UNHEX('f58ef2c6aa164c99a0b03d4967e2c665'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('395b6b64698d4d708aa6e18183fd422d'),'Guyuan','固原','guyuan',UNHEX('f58ef2c6aa164c99a0b03d4967e2c665'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('612162c5be9c423fab963428fe46b63c'),'Zhongwei','中卫','zhongwei',UNHEX('f58ef2c6aa164c99a0b03d4967e2c665'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('013641f7e4ad4be681dd63d7437de075'),'Wenchang','文昌','wenchang',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('30f5b438295345f18c76d6848e86ca83'),'Lingao','临高','lingao',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7b4bc673028949319f57c7b3fe8cbfef'),'Chengmai','澄迈','chengmai',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('35f24762de924a2ab2174d92342407f8'),'Dingan','定安','dingan',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f796f27517154a04850f88095c63822f'),'Qionghai','琼海','qionghai',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('7c306b60599f4850b306158e704248e6'),'Danzhou','儋州','danzhou',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6f28583f8c8345d89454e1a649412e36'),'Baisha','白沙','hainanbaisha',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('eb103d9dd9a041f690e5898835bb22da'),'Qiongzhong','琼中','qiongzhong',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('85a5fb0996b843f08e84228ea16e0fdc'),'Wanning','万宁','wanning',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('6af45aec5c52424b99aad19445560024'),'Changjiang','昌江','changjiang',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('e05f1125cb0044b191550d922741e0c2'),'Wuzhishan','五指山','wuzhishan',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('a2c4ccbe32604f2ead98c6963686a36c'),'Tunchang','屯昌','tunchang',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('165ac5f676d2443ebe6af9205db0dc4e'),'Lingshui','陵水','lingshui',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1e2fbdc4a2364558a2098a4cc4ec3e16'),'Baoting','保亭','baoting',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('121579ebaff04468bf4f034738b18e46'),'Dongfang','东方','dongfang',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f27d1ccfb769448aaddd60a322321e8e'),'Ledong','乐东','ledong',UNHEX('d93196c059f64f55b54df0789e370c4e'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('02515d41f14141759a119e68b9cfe687'),'Taibei','台北','taibei',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d2990ea002c84561a44022ddb6f98ef8'),'Jiayi','嘉义','jiayi',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b32c3370a6154ee58c8d51b239f4a223'),'Nantou','南投','nantou',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f63db7ef630e4dde9ea1873b5ec56f2f'),'Pingdong','屏东','pingdong',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('2a99146a690d43b7bcb50c682cab995b'),'Xinbei','新北','xinbei',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('45a0b7e936c74969a41154edf26e4141'),'Gaoxiong','高雄','gaoxiong',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('87dd1a8f30be41a19a46dd34ade4c8c3'),'Hualian','花莲','hualian',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0aa40d323e5b41faa6afc958a5051501'),'Taoyuan','桃园','taoyuan',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f9ffbb3b345643359b20e57123559653'),'Xinzhu','新竹','xinzhu',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ecd44b33f54c4615ace332da72a708e6'),'Yilan','宜兰','yilan',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('cf9c435b63e9428c935df42fa5809002'),'Miaoli','苗栗','miaoli',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('336a5e5618e040c189f60ecce0f6b195'),'Taizhong','台中','taizhong',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('0ab88ce6491f4632a9dc8cf7bbfc6fd9'),'Zhanghua','彰化','zhanghua',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('84e3861e9cf748108daafca4b52429f6'),'Yunlin','云林','yunlin',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8094b47b96f644119d150dce1ec3ae41'),'Tainan','台南','tainan',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d60c71fdd58c4c90b2236e99495d1d37'),'Taidong','台东','taidong',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('d6c4ed72ef1a4488bfa09f78d7273c69'),'Jilong','基隆','jilong',UNHEX('6e0bcba8b19f4ce9896018e3e2f607f6'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b48e1dbf570046368ab18e9c51737e0e'),'Tongxiang','桐乡','tongxiang',UNHEX('0351f620296844a896ab59ffa8c4aea5'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('84844276303647dd90e0f095cfa98da5'),'Barcelona','巴塞罗纳','basailuona',UNHEX(''),UNHEX('fe80acb2816e45f0a98819caa587c6fc'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9fb780ecd4b64cfbadc5f5c72d9326e4'),'Basaier','巴塞尔','basaier',UNHEX(''),UNHEX('ce5fc39199eb49c3a41d7011ca079f67'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5baee5c1ced04d3f89b79cc2579180c9'),'Lausanne','洛桑','luosang',UNHEX(''),UNHEX('ce5fc39199eb49c3a41d7011ca079f67'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('46e46912f85649ceb9f8cac99fe9211e'),'Geneva','日内瓦','rineiwa',UNHEX(''),UNHEX('ce5fc39199eb49c3a41d7011ca079f67'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('50eeaf0ea5b84f35bb4f9e70767e53fa'),'Zurich','苏黎世','sulishi',UNHEX(''),UNHEX('ce5fc39199eb49c3a41d7011ca079f67'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('990d650cfdc84ad8842f59d0f27c1f65'),'Madrid','马德里','madeli',UNHEX(''),UNHEX('fe80acb2816e45f0a98819caa587c6fc'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('68cfaa7394344bdc8d3bd94bbfe7e8a7'),'CanaryIslands','加纳利群岛','jianaliqundao',UNHEX(''),UNHEX('fe80acb2816e45f0a98819caa587c6fc'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('539c34b252db467eb5f23a650db4355a'),'Longkou','龙口','longkou',UNHEX('79ff0a4ba8a64653b02436878fff00c9'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('32b16ed6825041a586add3f102790293'),'Koror','科罗尔','keluoer',UNHEX(''),UNHEX('9ce560518c82415ea6bc6b9fdfc3f92e'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('f1db0673904f49b7ae0e33c79a291e63'),'Bazhou','霸州','bazhou',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ea7a39a911204e588134fc9dbe2cd879'),'Dachangxian','大厂县','dachangxian',UNHEX('3ba28518d135497aa202005fc95fe148'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('5e35868a963b478ba8e44dc8bd3e129c'),'Yingshan','英山','yingshan',UNHEX('03161e050c2448378eb863bfcbe744f3'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('73a25c8016504a77b450f99941e71813'),'Qiandongnanmiaozudongzuzizhizhou','黔东南苗族侗族自治州','qiandongnanmiaozudongzuzizhizhou',UNHEX('b6cf83ab7b69490e9b3526c98ea34db0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('ff50623e5a614a6fae8bc961b18b2ba3'),'Xingan','兴安','xingan',UNHEX('958e992e142e46e595f13752c0721ed0'),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('1af9a3af2ad3434b8357547d366bac76'),'Tahiti','大溪地','daxidi',UNHEX(''),UNHEX('26118eabd3c040c0adea84c8370da3ef'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('54053f1a057a4ded88c854c5e56c63f4'),'London','伦敦','lundun',UNHEX(''),UNHEX('b164126cb75e4d17a9f63e3f05172e73'),'2');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('689ddfcdeffd4937b56707c4c8907378'),'Beijing','北京','beijing',UNHEX(''),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('301cdd76923047d28eb28e85932d9f53'),'Shanghai','上海','shanghai',UNHEX(''),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8a1dc8fe34c04380b241c19a61ad07be'),'Hongkong','香港','xianggang',UNHEX(''),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('b6890ab6c23d405fa1c74b9dd5dd2e0c'),'Newyork','纽约','niuyue',UNHEX(''),UNHEX('cc0968e70fe34cc99f5b3a6898a04506'),'3');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('9473223591264341b4e8fa8eee3a0c35'),'Aomen','澳门','aomen',UNHEX(''),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');
INSERT INTO itinerary.city (uuid, name, chinese_name, pinyin_name, province_uuid, country_uuid, continent_id) VALUES (UNHEX('8678048dd15841ecbfc632e54cde3074'),'Tianjin','天津','tianjin',UNHEX(''),UNHEX('af70a55ceb4c415c837588081716f8b8'),'1');

UNLOCK TABLES;

--
-- Table structure for table `location_association`
--

DROP TABLE IF EXISTS `location_association`;
CREATE TABLE `location_association` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT,
  `location_uuid` binary(16) NOT NULL,
  `associated_location_uuid` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `location_association`
--

LOCK TABLES `location_association` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `location_association`
--

DROP TABLE IF EXISTS `port_association`;
CREATE TABLE `port_association` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT,
  `location_uuid` binary(16) NOT NULL,
  `port_location_uuid` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `port_association`
--

LOCK TABLES `port_association` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `location_association`
--

DROP TABLE IF EXISTS `agency`;
CREATE TABLE `agency` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` binary(16) NOT NULL,
  `name` VARCHAR(32) NOT NULL,
  `send_location_uuid` binary(16),
  `recv_location_uuid` binary(16),
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `port_association`
--

LOCK TABLES `agency` WRITE;
UNLOCK TABLES;