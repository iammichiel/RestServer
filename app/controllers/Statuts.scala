package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json.Json

import models.Statut

object Statuts extends Controller with Authorization {
    
    def list = asUser { _ => _ => 
        Ok(Json.toJson(
            Statut.all.map { s => s.toJson }.toList
        )).as("application/json")
    }

    def id(id: String) = asUser { _ => _ => 
        Ok(Json.toJson(Statut.id(id).toJson))
    }
}