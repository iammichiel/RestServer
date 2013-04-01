package controllers

import play.api._
import play.api.mvc._

import models._

object Application extends Controller with Authorization {

    // Supprime toutes les informations de l'utilisateur. 
    def deleteAll = asUser { apiKey => _ => 
        
        Projet.deleteAll(apiKey.key)
        // Utilisateur.deleteAll(apikey)
        //Taches.deleteAll(apiKey)

        Ok
    }
}