# Comment

# --- !Ups

CREATE TABLE `projets` (
  `id_projet` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nom` varchar(80) NOT NULL DEFAULT '',
  `description` text,
  `apikey` char(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id_projet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# --- !Downs


DROP TABLE `projets`;