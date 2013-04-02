package models

import scala.language.postfixOps

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
    description: Option[String],
    statut: Int,
    dateCreation: DateTime, 
    utilisateur: Option[Long]
) {
    def toJson = {
        Json.toJson(
            Map(
                "id"           -> id.toString,
                "nom"          -> nom, 
                "description"  -> description.map { _.toString }.getOrElse { "" },
                "statut"       -> statut.toString,
                "dateCreation" -> dateCreation.getMillis.toString,
                "utilisateur"  -> utilisateur.map { _.toString }.getOrElse { "" }
            )
        )
    }
}

// Tache
object Tache {

    val simple = {
        get[Pk[Long]]("id_tache") ~
        get[String]("titre") ~
        get[Option[String]]("description") ~
        get[Int]("statut") ~
        get[Long]("date_creation") ~
        get[Option[Long]]("id_utilisateur") map {
            case id~nom~description~statut~dateCreation~idUtilisateur => 
                Tache(id, nom, description, statut, new DateTime(dateCreation), idUtilisateur)
        }
    }

    def all(idProjet:String, key:String) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    SELECT 
                        id_tache, titre, description, statut, date_creation, id_utilisateur  
                    FROM 
                        taches 
                    WHERE 
                        id_projet = {idProjet} and key = {key}
                """
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