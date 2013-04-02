package models

import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._
import play.api.Play.current

import play.api.libs.json.Json

case class Projet(id: Pk[Long] = NotAssigned, nom:String, description: Option[String]) {
    def toJson = Json.toJson(Map(
        "id"          -> id.toString, 
        "nom"         -> nom, 
        "description" -> description.getOrElse("")
    ))
}

object Projet {

    val simple = {
        get[Pk[Long]]("id_projet") ~
        get[String]("nom") ~
        get[Option[String]]("description") map {
            case id~nom~description => Projet(id, nom, description)   
        }
    }

    def all(apikey: String) = {
        DB.withConnection { implicit connection => 
            SQL(
                "SELECT id_projet, nom, description FROM projets WHERE apikey = {apikey}"
            ).on(
                'apikey -> apikey
            ).as(simple *)
        }
    }

    def insert(p:Projet, apikey:String):Boolean = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    INSERT INTO projets (nom, description, apikey) VALUES 
                        ({nom}, {description}, {apikey})
                """
            ).on(
                'nom         -> p.nom, 
                'description -> p.description,
                'apikey      -> apikey
            ).execute()
        }
    }

    def update(idProjet:String, p:Projet, apikey:String):Boolean = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    UPDATE projets SET 
                        nom         = {nom}, 
                        description = {description}
                    WHERE 
                        id_projet = {idProjet} and apikey = {apikey}
                """
            ).on(
                'nom         -> p.nom, 
                'description -> p.description,
                'idProjet    -> idProjet,
                'apikey      -> apikey
            ).execute()
        }
    }

    def delete(idProjet:String, apikey:String):Boolean = {
        DB.withConnection { implicit connection => 
            SQL(
                "DELETE FROM projets WHERE id_projet = {idProjet} AND apikey = {apikey}"
            ).on(
                'idProjet -> idProjet, 
                'apikey   -> apikey 
            ).execute()
        }
    }

    def deleteAll(apikey: String) = {
        DB.withConnection { implicit connection => 
            SQL("DELETE FROM projets WHERE apikey = {apikey}").on('apikey -> apikey).execute()
        }
    }
}