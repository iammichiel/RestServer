package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.libs.iteratee._

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

import play.api.data._
import play.api.data.Forms._

object Personnes extends Controller with MongoController with Authorization {

    def authenticate = TODO

    def list = asUser { apiKey => implicit request => 
        Ok
    }
}