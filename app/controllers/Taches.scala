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

object Taches extends Controller with MongoController {

    def list = TODO
}
