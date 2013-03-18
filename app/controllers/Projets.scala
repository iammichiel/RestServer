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

object Projets extends Controller with MongoController with Authorization {

    val projetForm = Form(
        mapping(
            "nom" -> nonEmptyText
        )(
            (nom) => Projet(Some(BSONObjectID.generate), nom)
        )(
            (p:Projet) => Some(p.nom)
        )
    )

    // Retourne la liste des utilisateurs. 
    def list = asUser { apiKey => implicit request =>
        Async {
            Projet.all(apiKey.key).toList.map { projets => 
                Ok(Json.toJson(projets.map { _.toJson })).as("application/javascript") 
            }
        }
    }

    // Ajoute un projet
    def add = asUser { apiKey => implicit request =>
        projetForm.bindFromRequest.fold(
            errors => BadRequest, 
            projet => {
                Projet.add(projet, apiKey.key)

                // Retournez le projet qu'on vient de créer.
                Ok
            }
        )
    }

    // Supprime le projet
    def delete(id:String) = asUser { apiKey => implicit request =>
        Projet.delete(id, apiKey.key)
        Ok
    }
}