package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.libs.iteratee._

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import reactivemongo.bson._

import models._
import controllers._

object Utilisateurs extends Controller with MongoController with Authorization {

    def list = asUser { apiKey => _ =>
        Async {
            Utilisateur.all(apiKey.key).toList.map { utilisateurs => 
                Ok(Json.toJson(utilisateurs.map { _.toJson })).as("application/javascript")
            }
        }
    }

    def authenticate = TODO
}
