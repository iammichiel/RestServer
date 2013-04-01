package controllers

import anorm._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import models._
import controllers._

object Projets extends Controller with Authorization {

    val projetForm = Form(
        mapping(
            "nom"         -> nonEmptyText(maxLength = 80),
            "description" -> optional(text)
        )(
            (nom, description) => Projet(NotAssigned, nom, description)
        )(
            (p:Projet) => Some(p.nom, p.description)
        )
    )

    // Retourne la liste des utilisateurs. 
    def list = asUser { apiKey => implicit request =>
        Ok(
            Json.toJson(
                Projet.all(apiKey.key).map { p => p.toJson }.toList
            )
        ).as("application/javascript")
    }

    // Ajoute un projet
    def add = asUser { apiKey => implicit request =>
        projetForm.bindFromRequest.fold(
            errors => BadRequest(errors.errorsAsJson), 
            projet => {
                Projet.insert(projet, apiKey.key) match {
                    case true  => Created
                    case false => InternalServerError("Mysql exception")
                }
            }
        )
    }

    // Edition d'un projet
    def edit(id:String) = asUser { apiKey => implicit request =>
        projetForm.bindFromRequest.fold(
            errors => BadRequest(errors.errorsAsJson), 
            projet => {
                Projet.update(id, projet, apiKey.key) match {
                    case true  => Ok
                    case false => InternalServerError("Mysql exception")
                }
            }
        )
    }

    // Supprime le projet
    def delete(id:String) = asUser { apiKey => implicit request =>
        Projet.delete(id, apiKey.key) match {
            case true  => Ok
            case false => InternalServerError("Mysql exception")
        }
    }
}