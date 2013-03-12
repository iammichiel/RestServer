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

object Projets extends Controller with MongoController {

    val addForm = Form(
        mapping(
            "nom" -> nonEmptyText
        )(
            (nom) => Projet(Some(BSONObjectID.generate), nom)
        )(
            (p:Projet) => Some(p.nom)
        )
    )

    def list = Action { implicit request =>
        Async {
            Projet.all.toList.map { projets => Ok(Json.toJson(projets.map { _.toJson })) }
        }
    }

    def add = Action { implicit request =>
        addForm.bindFromRequest.fold(
            errors => BadRequest, 
            projet => {
                Projet.add(projet)
                Ok
            }
        )
    }
}