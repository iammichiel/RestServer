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

case class Projet(id: Option[BSONObjectID], nom: String) {
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
    implicit object ProjetBSONReader extends BSONReader[Projet] {
        def fromBSON(document: BSONDocument):Projet = {
            val doc = document.toTraversable
            Projet(
                doc.getAs[BSONObjectID]("_id"),
                doc.getAs[BSONString]("nom").get.value
            )
        }
    }

    // Writer.
    implicit object PersonneBSONWriter extends BSONWriter[Projet] {
        def toBSON(projet: Projet) = {
            BSONDocument(
                "_id"    -> projet.id.getOrElse(BSONObjectID.generate),
                "nom"    -> BSONString(projet.nom)
            )
        }
    }

    def add(p:Projet) = {
        collection.insert(p)        
    }

    def update(p:Projet) = {
        
    }

    def delete(id:String) = {

    }

    def all = {
        collection.find(BSONDocument())
    }
}