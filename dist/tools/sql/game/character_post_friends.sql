CREATE TABLE IF NOT EXISTS `character_post_friends` (
  charId INT UNSIGNED NOT NULL DEFAULT 0,
  post_friend INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`post_friend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;