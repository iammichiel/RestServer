package controllers

import anorm._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import models._
import controllers._

object Commentaires extends Controller with Authorization {
    
    def listTache(idTache: String) = asUser { apiKey => _ => 
        Ok(
            Json.toJson(
                Commentaire.all(idTache, apiKey.key).map { p => p.toJson }.toList
            )
        ).as("application/json")
    }
}