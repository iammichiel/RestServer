package models

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._
import play.api.Play.current

import play.api.libs.json.Json

case class Projet(id: Pk[Long] = NotAssigned, nom:String)
case class ProjetAPI(id: Pk[Long] = NotAssigned, nom: String, key: String) {
    def toJson = {
        Json.toJson(
            Map(
                "id"  -> id.toString,
                "nom" -> nom
            )
        )
    }
}

object Projet {

    val simple = {
        get[Pk[Long]]("id_projet") ~
        get[String]("nom") ~
        get[String]("id_utilisateur") map {
            case id~nom~utilisateur => ProjetAPI(id, nom, utilisateur)
        }
    }

    def all(key: String) = {
        DB.withConnection { implicit connection => 
            SQL("SELECT * FROM projets").as(simple * )
        }
    }

    def insert(p:Projet, key:String) = {
        DB.withConnection { implicit connection => 
            SQL(
                "INSERT INTO projets (nom, id_utilisateur) VALUES ({nom}n {utilisateur})"
            ).on(
                'nom         -> p.nom, 
                'utilisateur -> key
            ).executeInsert()
        }
    }

    def update(idProjet:String, p:Projet, key:String) = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    UPDATE projets SET 
                        nom = {nom} 
                    WHERE id_projet = {idProjet} and key = {key}
                """
            ).on(
                'nom      -> p.nom, 
                'idProjet -> idProjet,
                'key      -> key
            ).executeUpdate()
        }
    }

    def delete(idProjet:String, key:String) = {
        DB.withConnection { implicit connection => 
            SQL(
                "DELETE FROM projets WHERE id_projet = {idProjet} AND key = {key}"
            ).on(
                'idProjet -> idProjet, 
                'key      -> key 
            ).executeUpdate()
        }
    }
}