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

case class Tache(
    id: Option[BSONObjectID],
    projet: BSONObjectID, 
    nom: String,
    utilisateur: Option[BSONObjectID],
    dateCreation: DateTime, 
    commentaire: String
)
case class TacheAPI(tache: Tache, key: String) {
    def toJson = {
        Json.toJson(
            Map(
                "id"           -> tache.id.get.stringify,
                "projet"       -> tache.projet.stringify, 
                "nom"          -> tache.nom, 
                "utilisateur"  -> tache.utilisateur.map { _.stringify }.getOrElse { "" },
                "dateCreation" -> tache.dateCreation.getMillis.toString,
                "commentaire"  -> tache.commentaire
            )
        )
    }
}

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
                Tache(
                    doc.getAs[BSONObjectID]("_id"),
                    doc.getAs[BSONObjectID]("projet").getOrElse(BSONObjectID.generate),
                    doc.getAs[BSONString]("nom").get.value,
                    doc.getAs[BSONObjectID]("utilisateur").map { t => t },
                    new DateTime(doc.getAs[BSONDateTime]("dateCreation")),
                    doc.getAs[BSONString]("commentaire").map { _.value }.getOrElse("")
                ),
                doc.getAs[BSONString]("key").get.value
            )
        }
    }

    // Writer!
    implicit object TacheBSONWriter extends BSONWriter[TacheAPI] {
        def toBSON(t:TacheAPI) = {
            BSONDocument(
                "_id"          -> t.tache.id.getOrElse(BSONObjectID.generate),
                "projet"       -> t.tache.projet,
                "nom"          -> BSONString(t.tache.nom),
                "utilisateur"  -> t.tache.utilisateur.getOrElse(BSONNull),
                "dateCreation" -> BSONDateTime(t.tache.dateCreation.getMillis),
                "commentaire"  -> BSONString(t.tache.commentaire),
                "key"          -> BSONString(t.key)
            )
        }
    }

    def all(idProjet: String, api:String) = {
        collection.find(
            BSONDocument(
                "api"    -> BSONString(api), 
                "projet" -> BSONObjectID(idProjet)
            )
        )
    }
}