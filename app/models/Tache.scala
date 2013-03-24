package models

import org.joda.time.DateTime

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

case class TacheAPI(
    id: Option[BSONObjectID], 
    projet: BSONObjectID, 
    nom: String, 
    dateCreation: DateTime, 
    key: String
)

case class Tache(
    id: Option[BSONObjectID],
    projet: BSONObjectID, 
    nom: String,
    dateCreation: DateTime
)

// Tache
object Tache {

    // Execution contexte. 
    implicit def ec: ExecutionContext = ExecutionContext.Implicits.global

    // Acces a la base de données.
    val db = ReactiveMongoPlugin.db
    val collection = db("projets")

    // Reader!
    implicit object TacheBSONReader extends BSONReader[TacheAPI] {
        def fromBSON(document: BSONDocument):TacheAPI = {
            val doc = document.toTraversable
            TacheAPI(
                doc.getAs[BSONObjectID]("_id"),
                doc.getAs[BSONObjectID]("projet").getOrElse(BSONObjectID.generate),
                doc.getAs[BSONString]("nom").get.value,
                new DateTime(doc.getAs[BSONDateTime]("dateCreation").get.value), 
                doc.getAs[BSONString]("key").get.value
            )
        }
    }

    // Writer!
    implicit object TacheBSONWriter extends BSONWriter[TacheAPI] {
        def toBSON(tache:TacheAPI) = {
            BSONDocument(
                "_id"          -> tache.id.getOrElse(BSONObjectID.generate),
                "projet"       -> tache.projet,
                "nom"          -> BSONString(tache.nom),
                "dateCreation" -> BSONDateTime(tache.dateCreation.getMillis), 
                "key"          -> BSONString(tache.key)
            )
        }
    }

    def all(api:String) = {
        collection.find(BSONDocument("api" -> BSONString(api)))
    }
}