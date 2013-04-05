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
    titre: String,
    description: Option[String],
    statut: Int,
    dateCreation: DateTime, 
    utilisateur: Option[Long]
) {
    def toJson = {
        Json.toJson(
            Map(
                "id"           -> id.toString,
                "titre"        -> titre, 
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
            case id~titre~description~statut~dateCreation~idUtilisateur => 
                Tache(id, titre, description, statut, new DateTime(dateCreation), idUtilisateur)
        }
    }

    def all(idProjet:String, apikey:String) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    SELECT 
                        id_tache, titre, description, statut, date_creation, id_utilisateur  
                    FROM 
                        taches 
                    WHERE 
                        id_projet = {idProjet} and apikey = {apikey}
                """
            ).on(
                'idProjet -> idProjet, 
                'apikey   -> apikey
            ).as(simple *)
        }
    }

    def add(idProjet:String, tache:Tache, apikey:String) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    INSERT INTO taches 
                        (titre, statut, id_utilisateur, date_creation, description, id_projet, apikey)
                    VALUES 
                        ({titre}, {statut}, {idUtilisateur}, {dateCreation}, {description}, {idProjet}, {apikey})
                """
            ).on(
                'titre          -> tache.titre,
                'statut         -> tache.statut, 
                'idUtilisateur  -> tache.utilisateur, 
                'dateCreation   -> tache.dateCreation.getMillis, 
                'description    -> tache.description, 
                'idProjet       -> idProjet, 
                'apikey         -> apikey
            ).executeInsert()
        }
    }

    def edit(idTache: String, tache:Tache, apikey: String) = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    UPDATE taches SET 
                        titre = {titre}, 
                        statut = {statut}, 
                        description = {description}, 
                        id_utilisateur = {utilisateur}
                    WHERE 
                        id_tache = {idTache} AND
                        apikey   = {apikey}
                """
            ).on(
                'idTache     -> idTache, 
                'apikey      -> apikey, 
                'titre       -> tache.titre, 
                'description -> tache.description, 
                'statut      -> tache.statut,
                'utilisateur -> tache.utilisateur
            ).executeUpdate()
        }
    }

    def delete(idTache: String, apikey: String) = {
        DB.withConnection { implicit connection =>
            SQL(
                "DELETE FROM taches WHERE id_tache = {idTache} AND apikey = {apikey}"
            ).on(
                'idTache -> idTache, 
                'apikey  -> apikey
            ).executeUpdate()
        }
    }
}