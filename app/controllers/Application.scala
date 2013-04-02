package controllers

import org.pegdown.PegDownProcessor

import play.api._
import play.api.mvc._

import models._

object Application extends Controller with Authorization {

    def index = Action {
        val p:PegDownProcessor = new PegDownProcessor();
        Ok(p.markdownToHtml(views.html.index().toString)).as("html")
    }

    def deleteAll = asUser { apiKey => _ => 
        Projet.deleteAll(apiKey.key)
        // Utilisateur.deleteAll(apikey)
        //Taches.deleteAll(apiKey)

        Ok
    }
}