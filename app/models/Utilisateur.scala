package models

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.Json

import scala.language.postfixOps

case class Utilisateur(
    id: Pk[Long] = NotAssigned, 
    prenom: String, 
    nom: String, 
    email: String, 
    motdepasse: String
) {
    def toJson = Json.toJson(Map(
        "id"         -> id.toString,
        "prenom"     -> prenom,
        "nom"        -> nom, 
        "email"      -> email, 
        "motdepasse" -> motdepasse
    ))
}

object Utilisateur {

    val simple = {
        get[Pk[Long]]("id_utilisateur") ~
        get[String]("prenom") ~
        get[String]("nom") ~
        get[String]("email") ~
        get[String]("motdepasse") map {
            case id~prenom~nom~email~motdepasse => Utilisateur(id, prenom, nom, email, motdepasse)
        }
    }

    // Retourne la liste de tout les utilisateurs.
    def all(apikey: String) = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    SELECT 
                        id_utilisateur, prenom, nom, email, motdepasse 
                    FROM 
                        utilisateurs 
                    WHERE 
                        apikey = {apikey}
                """
            ).on(
                'apikey -> apikey
            ).as(simple *)
        }
    }

    def insert(u:Utilisateur, apikey:String) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    INSERT INTO utilisateurs 
                        (prenom, nom, email, motdepasse, apikey)
                    VALUES 
                        ({prenom}, {nom}, {email}, {motdepasse}, {apikey})
                """
            ).on(
                'prenom     -> u.prenom, 
                'nom        -> u.nom,
                'email      -> u.email,
                'motdepasse -> u.motdepasse, 
                'apikey     -> apikey
            ).execute()
        }
    }

    def update(id:String, u:Utilisateur, apikey:String) = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    UPDATE utilisateurs SET 
                        prenom = {prenom}, 
                        nom  = {nom}, 
                        email = {email}, 
                        motdepasse = {motdepasse}
                    WHERE 
                        id_utilisateur = {idUtilisateur} AND 
                        apikey = {apikey}
                """
            ).on(
                'prenom        -> u.prenom, 
                'nom           -> u.nom,
                'email         -> u.email,
                'motdepasse    -> u.motdepasse, 
                'apikey        -> apikey, 
                'idUtilisateur -> id
            ).execute()
        }
    }

    def delete(id:String, apikey:String) = {
        DB.withConnection { implicit connection => 
            SQL(
                "DELETE FROM utilisateurs WHERE id_utilisateur = {id} AND apikey = {apikey}"
            ).on(
                'id     -> id, 
                'apikey -> apikey
            ).execute()
        }
    }

    def authenticate(email:String, motdepasse:String, apikey:String) = {
        DB.withConnection { implicit connection => 
            SQL(
                """
                    SELECT 
                        id_utilisateur, prenom, nom, email, motdepasse 
                    FROM 
                        utilisateurs 
                    WHERE 
                        apikey = {apikey} AND
                        email = {email} AND 
                        motdepasse = {motdepasse}
                    LIMIT 1
                """
            ).on(
                'apikey     -> apikey,
                'email      -> email, 
                'motdepasse -> motdepasse
            ).as(simple.singleOpt)
        }
    }
}