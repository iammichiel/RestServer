package models

import play.api._
import play.api.Play.current
import play.modules.reactivemongo._

import play.api.libs.json.Json

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler

import scala.concurrent.ExecutionContext

case class Utilisateur(
    id: Option[BSONObjectID],
    prenom: String, 
    nom: String,
    email: String, 
    motdepasse: String
)

case class UtilisateurAPI(utilisateur: Utilisateur, key: String) {
    def toJson = {
        Json.toJson(
            Map(
                "id"         -> utilisateur.id.get.stringify,
                "prenom"     -> utilisateur.prenom,
                "nom"        -> utilisateur.nom, 
                "email"      -> utilisateur.email, 
                "motdepasse" -> utilisateur.motdepasse
            )
        )
    }
}

object Utilisateur {

    // Execution contexte. 
    implicit def ec: ExecutionContext = ExecutionContext.Implicits.global

    // Acces a la base de données.
    val db = ReactiveMongoPlugin.db
    val collection = db("utilisateurs")

    // Reader.
    implicit object UtilisateurApiBSONReader extends BSONReader[UtilisateurAPI] {
        def fromBSON(document: BSONDocument):UtilisateurAPI = {
            val doc = document.toTraversable
            UtilisateurAPI(
                Utilisateur(
                    doc.getAs[BSONObjectID]("_id"), 
                    doc.getAs[BSONString]("prenom").get.value,
                    doc.getAs[BSONString]("nom").get.value,
                    doc.getAs[BSONString]("email").get.value, 
                    doc.getAs[BSONString]("motdepasse").get.value
                ), 
                doc.getAs[BSONString]("key").get.value
            )
        }
    }

    // Writer.
    implicit object UtilisateurBSONWriter extends BSONWriter[UtilisateurAPI] {
        def toBSON(u: UtilisateurAPI) = {
            BSONDocument(
                "_id"        -> u.utilisateur.id.getOrElse(BSONObjectID.generate),
                "prenom"     -> BSONString(u.utilisateur.prenom),
                "nom"        -> BSONString(u.utilisateur.nom),
                "email"      -> BSONString(u.utilisateur.email),
                "motdepasse" -> BSONString(u.utilisateur.motdepasse),
                "key"        -> BSONString(u.key)
            )
        }
    }

    def all(key: String) = {
        collection.find(BSONDocument("key" -> BSONString(key)))
    }

    def insert(u:Utilisateur, key:String) = {
        collection.insert(UtilisateurAPI(u, key))
    }

    def update(id:String, u:Utilisateur, key:String) = {
        collection.update(
            BSONDocument("_id" -> BSONObjectID(id)),
            UtilisateurAPI(
                Utilisateur(Some(BSONObjectID(id)), u.prenom, u.nom, u.email, u.motdepasse), 
                key
            )
        )
    }

    def delete(id:String, key:String) = {
        collection.remove(BSONDocument(
            "_id" -> BSONObjectID(id),
            "key" -> BSONString(key)
        ))
    }

    def authenticate(email: String, motdepasse: String) = {
        collection.find(
            BSONDocument(
                "email" -> BSONString(email), 
                "motdepasse" -> BSONString(motdepasse)
            )
        ).headOption
    }
}