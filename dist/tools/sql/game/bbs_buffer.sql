CREATE TABLE `bbs_buffer` (
  `name` varchar(45) NOT NULL,
  `charId` int(11) NOT NULL DEFAULT '0',
  `buffs` varchar(355) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`,`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;