# Comment

# --- !Ups

CREATE TABLE `projets` (
    `id_projet` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `nom` varchar(80) NOT NULL DEFAULT '',
    `description` text,
    `apikey` char(32) NOT NULL DEFAULT '',
    PRIMARY KEY (`id_projet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `utilisateurs` (
    `id_utilisateur` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `prenom` varchar(80) NOT NULL DEFAULT '',
    `nom` varchar(80) NOT NULL DEFAULT '',
    `email` varchar(255) NOT NULL DEFAULT '',
    `motdepasse` varchar(80) NOT NULL DEFAULT '',
    `apikey` char(32) NOT NULL DEFAULT '',
    PRIMARY KEY (`id_utilisateur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE `projets`;
DROP TABLE `utilisateurs`;