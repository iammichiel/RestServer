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

    val form = Form(
        mapping(
            "prenom"     -> nonEmptyText, 
            "nom"        -> nonEmptyText, 
            "email"      -> email, 
            "motdepasse" -> nonEmptyText
        )(
            (prenom, nom, email, motdepasse) => Utilisateur(Some(BSONObjectID.generate), prenom, nom, email, motdepasse)
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
        Async {
            Utilisateur.all(apiKey.key).toList.map { utilisateurs => 
                Ok(Json.toJson(utilisateurs.map { _.toJson })).as("application/javascript")
            }
        }
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
                Async {
                    Utilisateur.authenticate(loginTuple._1, loginTuple._2).map { optionUtilisateur =>
                        optionUtilisateur match {
                            case Some(u) => Ok
                            case _ => NotFound
                        }
                    }
                }
            }
        )
    }
}
