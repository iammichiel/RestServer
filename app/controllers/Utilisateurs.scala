package controllers

import anorm._

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.libs.iteratee._

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import models._
import controllers._

object Utilisateurs extends Controller with Authorization {

    val form = Form(
        mapping(
            "prenom"     -> nonEmptyText, 
            "nom"        -> nonEmptyText, 
            "email"      -> email, 
            "motdepasse" -> nonEmptyText
        )(
            (prenom, nom, email, motdepasse) => Utilisateur(NotAssigned, prenom, nom, email, motdepasse)
        )(
            (u:Utilisateur) => Some(u.prenom, u.nom, u.email, u.motdepasse)
        )
    )

    val loginForm = Form(
        tuple(
            "login"      -> nonEmptyText, 
            "motdepasse" -> nonEmptyText
        )
    )

    def list = asUser { apiKey => _ =>
        Ok(
            Json.toJson(
                Utilisateur.all(apiKey.key).map { p => p.toJson }.toList
            )
        ).as("application/javascript")
    }

    def add = asUser { apiKey => implicit request =>
        form.bindFromRequest.fold(
            errors => BadRequest, 
            utilisateur => {
                Utilisateur.insert(utilisateur, apiKey.key)
                Created
            }
        )
    }

    def authenticate = asUser { apiKey => implicit request => 
        loginForm.bindFromRequest.fold(
            errors => BadRequest("Username and password have to be defined"),
            loginTuple => {
                Utilisateur.authenticate(loginTuple._1, loginTuple._2).map { optionUtilisateur => Ok }.getOrElse { NotFound}
            }
        )
    }

    def delete(id: String) = asUser { apiKey => _ => 
        Utilisateur.delete(id, apiKey.key)
        Ok
    }

    def edit(id: String) = asUser { apiKey => implicit request => 
        form.bindFromRequest.fold(
            errors => BadRequest, 
            utilisateur => {
                Utilisateur.update(id, utilisateur, apiKey.key)
                Ok
            }
        )
    }
}
