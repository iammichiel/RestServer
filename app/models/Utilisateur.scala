package models

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
    prenom: String
)