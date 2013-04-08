package models

import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.Json

import org.joda.time.DateTime

case class Commentaire(
    id: Pk[Long] = NotAssigned, 
    utilisateur: Long, 
    dateCreation: DateTime, 
    contenu: String 
) {
    def toJson = Json.toJson(Map(
        "id"           -> id.toString, 
        "utilisateur"  -> utilisateur.toString, 
        "dateCreation" -> dateCreation.getMillis.toString,
        "contenu"      -> contenu
    ))
}

object Commentaire {
    
   val simple = {
        get[Pk[Long]]("id_commentaire") ~
        get[Long]("id_utilisateur") ~
        get[Long]("date_creation") ~
        get[String]("contenu") map {
            case id~utilisateur~dateCreation~contenu => 
                Commentaire(id, utilisateur, new DateTime(dateCreation), contenu)   
        }
    }

    def all(idTache: String, apikey: String) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    SELECT 
                        id_commentaire, id_utilisateur, date_creation, contenu
                    FROM 
                        taches 
                    WHERE 
                        id_tache = {idTache} AND apikey = {apikey}
                """
            ).on(
                'idTache -> idTache,
                'apikey  -> apikey
            ).as(simple *)
        }
    }

    def add(idTache: String, commentaire:Commentaire, apikey: String) = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    INSERT INTO commentaires 
                        (id_utilisateur, date_creation, contenu, apikey) 
                    VALUES 
                        ({idUtilisateur}, {dateCreation}, {contenu}, {apikey})
                """
            ).on(
                'idUtilisateur -> commentaire.utilisateur,
                'dateCreation  -> commentaire.dateCreation,
                'contenu       -> commentaire.contenu,
                'apikey        -> apikey
            ).executeInsert()
        }
    }
}