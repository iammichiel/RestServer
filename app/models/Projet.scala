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

import controllers._

case class Projet(id: Option[BSONObjectID], nom:String)
case class ProjetAPI(id: Option[BSONObjectID], nom: String, api: String) {
    def toJson = {
        Json.toJson(
            Map(
                "id"  -> id.get.stringify,
                "nom" -> nom
            )
        )
    }
}

object Projet {

    // Execution contexte. 
    implicit def ec: ExecutionContext = ExecutionContext.Implicits.global

    // Acces a la base de données.
    val db = ReactiveMongoPlugin.db
    val collection = db("projets")

    // Reader.
    implicit object ProjetApiBSONReader extends BSONReader[ProjetAPI] {
        def fromBSON(document: BSONDocument):ProjetAPI = {
            val doc = document.toTraversable
            ProjetAPI(
                doc.getAs[BSONObjectID]("_id"), 
                doc.getAs[BSONString]("nom").get.value,
                doc.getAs[BSONString]("api").get.value
            )
        }
    }

    // Writer.
    implicit object ProjetApiBSONWriter extends BSONWriter[ProjetAPI] {
        def toBSON(projet: ProjetAPI) = {
            BSONDocument(
                "_id" -> projet.id.getOrElse(BSONObjectID.generate),
                "nom" -> BSONString(projet.nom),
                "api" -> BSONString(projet.api)
            )
        }
    }

    // Ajoute
    def add(p:Projet, key:String) = {
        collection.insert(ProjetAPI(Some(p.id.get), p.nom, key))
    }

    // Mise à jour. 
    def update(p:Projet, api:String) = {
        
    }

    def delete(id:String, api:String) = {

    }

    def all(key: String) = {
        collection.find(BSONDocument("api" -> BSONString(key)))
    }
}