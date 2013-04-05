# Comment

# --- !Ups

CREATE TABLE `projets` (
    `id_projet` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `nom` varchar(80) NOT NULL DEFAULT '',
    `description` text,
    `apikey` char(32) NOT NULL DEFAULT '',
    PRIMARY KEY (`id_projet`)
);

CREATE TABLE `utilisateurs` (
    `id_utilisateur` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `prenom` varchar(80) NOT NULL DEFAULT '',
    `nom` varchar(80) NOT NULL DEFAULT '',
    `email` varchar(255) NOT NULL DEFAULT '',
    `motdepasse` varchar(80) NOT NULL DEFAULT '',
    `apikey` char(32) NOT NULL DEFAULT '',
    PRIMARY KEY (`id_utilisateur`)
);

CREATE TABLE `taches` (
    `id_tache` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `titre` varchar(80) NOT NULL DEFAULT '',
    `description` text,
    `date_creation` bigint(20) NOT NULL,
    `statut` int(1) NOT NULL DEFAULT '0',
    `id_projet` int(10) unsigned DEFAULT NULL,
    `id_utilisateur` int(11) unsigned DEFAULT NULL,
    `apikey` char(32) NOT NULL DEFAULT '',
    PRIMARY KEY (`id_tache`),
    KEY `fk_taches_utilisateurs` (`id_utilisateur`),
    KEY `fk_taches_projets` (`id_projet`),
    CONSTRAINT `fk_taches_projets` FOREIGN KEY (`id_projet`) 
        REFERENCES `projets` (`id_projet`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_taches_utilisateurs` FOREIGN KEY (`id_utilisateur`) 
        REFERENCES `utilisateurs` (`id_utilisateur`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `commentaires` (
    `id_commentaire` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `id_tache` int(11) unsigned NOT NULL,
    `id_utilisateur` int(11) unsigned NOT NULL,
    `date_creation` bigint(20) NOT NULL,
    `contenu` text NOT NULL,
    `apikey` char(32) NOT NULL DEFAULT '',
    PRIMARY KEY (`id_commentaire`),
    KEY `fk_commentaires_taches` (`id_tache`),
    KEY `fk_commentaires_utilisateurs` (`id_utilisateur`),
    CONSTRAINT `fk_commentaires_utilisateurs` FOREIGN KEY (`id_utilisateur`) 
        REFERENCES `utilisateurs` (`id_utilisateur`),
    CONSTRAINT `fk_commentaires_taches` FOREIGN KEY (`id_tache`) 
        REFERENCES `taches` (`id_tache`)
);

# --- !Downs

DROP TABLE `projets`;
DROP TABLE `utilisateurs`;
DROP TABLE `taches`;
DROP TABLE `commentaires`;