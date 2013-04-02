package controllers

import anorm._

import play.api._
import play.api.Play.current
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import org.joda.time.DateTime

import models._

object Taches extends Controller with Authorization {

    val form = Form(
        mapping(
            "nom"         -> text,
            "description" -> optional(text),
            "statut"      -> number,
            "utilisateur" -> optional(longNumber)
        )(
            (nom, description, statut, utilisateur) => 
                Tache(
                    NotAssigned, 
                    nom, 
                    description,
                    statut,
                    new DateTime(), 
                    utilisateur
                )

        )(
            (t:Tache) => Some((t.nom, t.description, t.statut, t.utilisateur))
        )
    )

    def listProjet(idProjet: String) = asUser { apiKey => _ => 
        Ok(
            Json.toJson(
                Tache.all(idProjet, apiKey.key).map { p => p.toJson }.toList
            )
        ).as("application/javascript")
    }

    def add(idProjet: String) = asUser { apiKey => implicit request => 
        form.bindFromRequest.fold(
            errors => BadRequest,  
            tache => {
                Tache.add(idProjet, tache, apiKey.key)
                Created
            }
        )
    }
}
