DROP TABLE IF EXISTS `rim_kamaloka`;
CREATE TABLE IF NOT EXISTS `rim_kamaloka` (
  `playerName` VARCHAR(35) NOT NULL,
  `score` SMALLINT(5) UNSIGNED NOT NULL,
  PRIMARY KEY (`playerName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;