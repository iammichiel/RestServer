package models

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.Json

import org.joda.time.DateTime

case class Tache(
    id: Pk[Long] = NotAssigned,
    nom: String,
    statut: Int,
    utilisateur: Option[Long],
    dateCreation: DateTime, 
    description: Option[String]
) {
    def toJson = {
        Json.toJson(
            Map(
                "id"           -> id.toString,
                "nom"          -> nom, 
                "statut"       -> statut.toString,
                "utilisateur"  -> utilisateur.map { _.toString }.getOrElse { "" },
                "dateCreation" -> dateCreation.getMillis.toString,
                "description"  -> description.map { _.toString }.getOrElse { "" }
            )
        )
    }
}

case class TacheAPI(tache: Tache, key: String)

// Tache
object Tache {

    val simple = {
        get[Pk[Long]]("id_tache") ~
        get[String]("nom") ~
        get[Int]("statut") ~
        get[Option[Long]]("id_utilisateur") ~
        get[Long]("date_creation") ~
        get[Option[String]]("description") map {
            case id~nom~statut~utilisateur~dateCreation~description => 
                Tache(id, nom, statut, utilisateur, new DateTime(dateCreation), description)
        }
    }

    def all(idProjet:String, key:String) = {
        DB.withConnection { implicit connection =>
            SQL(
                "SELECT * FROM taches WHERE id_projet = {idProjet} and key = {key}"
            ).on(
                'idProjet -> idProjet, 
                'key      -> key
            ).as(simple *)
        }
    }

    def add(idProjet:String, tache:Tache, key:String) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    INSERT INTO taches 
                        (nom, statut, utilisateur, date_creation, description, id_projet, key)
                    VALUES 
                        ({nom}, {statut}, {utilisateur}, {dateCreation}, {description}, {idProjet}, {key})
                """
            ).on(
                'nom          -> tache.nom,
                'statut       -> tache.statut, 
                'utilisateur  -> tache.utilisateur, 
                'dateCreation -> tache.dateCreation, 
                'description  -> tache.description, 
                'idProjet     -> idProjet, 
                'key          -> key
            ).executeInsert()
        }
    }
}