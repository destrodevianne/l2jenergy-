CREATE TABLE IF NOT EXISTS `global_tasks` (
  `id` int(11) NOT NULL auto_increment,
  `task` varchar(50) NOT NULL DEFAULT '',
  `type` varchar(50) NOT NULL DEFAULT '',
  `last_activation` bigint(13) unsigned NOT NULL DEFAULT '0',
  `param1` varchar(100) NOT NULL DEFAULT '',
  `param2` varchar(100) NOT NULL DEFAULT '',
  `param3` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `global_tasks` VALUES ('201', 'script', 'TYPE_GLOBAL_TASK', '0', '1', '04:00:00', 'ScheduledAutoRestart.java');