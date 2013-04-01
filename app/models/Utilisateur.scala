package models

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.Json

case class Utilisateur(
    id: Pk[Long] = NotAssigned,
    prenom: String, 
    nom: String,
    email: String, 
    motdepasse: String
) {
    def toJson = {
        Json.toJson(
            Map(
                "id"         -> id.toString,
                "prenom"     -> prenom,
                "nom"        -> nom, 
                "email"      -> email, 
                "motdepasse" -> motdepasse
            )
        )
    }
}

case class UtilisateurAPI(utilisateur: Utilisateur, key: String)

object Utilisateur {

    val simple = {
        get[Pk[Long]]("id_utilisateur") ~
        get[String]("prenom") ~
        get[String]("nom") ~
        get[String]("email") ~
        get[String]("motdepasse") map {
            case id~prenom~nom~email~motdepasse => 
                Utilisateur(id, prenom, nom, email, motdepasse)
        }
    }

    // Retourne la liste de tout les utilisateurs.
    def all(key: String) = {
        DB.withConnection { implicit connection => 
            SQL(
                "SELECT * FROM utilisateurs WHERE key = {key}"
            ).on(
                'key -> key
            ).as(simple *)
        }
    }

    def insert(u:Utilisateur, key:String) = {
        //collection.insert(UtilisateurAPI(u, key))
    }

    def update(id:String, u:Utilisateur, key:String) = {
        //collection.update(
        //     BSONDocument("_id" -> BSONObjectID(id)),
        //     UtilisateurAPI(
        //         Utilisateur(Some(BSONObjectID(id)), u.prenom, u.nom, u.email, u.motdepasse), 
        //         key
        //     )
        // )
    }

    def delete(id:String, key:String) = {
        // collection.remove(BSONDocument(
        //     "_id" -> BSONObjectID(id),
        //     "key" -> BSONString(key)
        // ))
    }

    def authenticate(email: String, motdepasse: String) = {
        // collection.find(
        //     BSONDocument(
        //         "email" -> BSONString(email), 
        //         "motdepasse" -> BSONString(motdepasse)
        //     )
        // ).headOption
        None
    }
}