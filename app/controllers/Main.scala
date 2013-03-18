package controllers 

import play.api.mvc._

// 
object Main extends Controller {
    
    // Affiche la doc
    def index = Action { implicit request =>
        Ok(views.html.index())
    }

    // Affiche le JSON de base. 
    def docs = Action { implicit request =>
        Ok(views.html.swagger.docs()).as("application/javascript").withHeaders(
            ("Access-Control-Allow-Origin", "*"),
            ("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT"),
            ("Access-Control-Allow-Headers", "Content-Type, api_key, Authorization")
        )
    }

    // Affiche le JSON sur les projets
    def projets = Action { implicit request =>
        Ok(views.html.swagger.projets()).as("application/javascript").withHeaders(
            ("Access-Control-Allow-Origin", "*"),
            ("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT"),
            ("Access-Control-Allow-Headers", "Content-Type, api_key, Authorization")
        )
    }
}