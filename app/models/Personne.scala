package models

import play.api._
import play.api.Play.current
import play.modules.reactivemongo._

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler

import scala.concurrent.ExecutionContext

case class Personne(
    id: Option[BSONObjectID], 
    prenom: String, 
    nom: String
)

object Personne {

    // Execution contexte. 
    implicit def ec: ExecutionContext = ExecutionContext.Implicits.global

    // Acces a la base de données
    val db = ReactiveMongoPlugin.db
    val collection = db("personnes")

    // Medecin reader./
    implicit object PersonneBSONReader extends BSONReader[Personne] {
        def fromBSON(document: BSONDocument):Personne = {
            val doc = document.toTraversable
            Personne(
                doc.getAs[BSONObjectID]("_id"),
                doc.getAs[BSONString]("nom").get.value, 
                doc.getAs[BSONString]("prenom").get.value
            )
        }
    }

    // Writer du medecin.
    implicit object PersonneBSONWriter extends BSONWriter[Personne] {
        def toBSON(personne: Personne) = {
            BSONDocument(
                "_id"    -> personne.id.getOrElse(BSONObjectID.generate),
                "nom"    -> BSONString(personne.nom), 
                "prenom" -> BSONString(personne.prenom)
            )
        }
    }

    // 
    def findByUsername() = {

    }

}