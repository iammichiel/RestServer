package controllers

import play.api._
import play.api.mvc._

case class ApiKey(
    prenom: String, 
    key: String 
)

// Authorization 
trait Authorization {

     // En cas de non authorization, on redirige par la. 
    private def onUnauthorized(request: RequestHeader) = Results.Unauthorized("Missing API-KEY")

    // La liste des utilisateurs.
    private val users:Seq[ApiKey] = Seq(
        ApiKey("Michiel", "michiel"),
        ApiKey("Nicolas", "lzbwTVP27YzEXquNe5bPIBqblKsvOhh3"), 
        ApiKey("Guillaume", "iUMfqg6pQB9RHoy00RJBLrb52ji1cpv4"), 
        ApiKey("Valentin", "I2cyqundzRWsphJ67XdtYfMnzaecYpb9"), 
        ApiKey("Hugo", "lHDoz0vQsuCFBYIh1AKNBwUNvc5Nmoza")
    )

    // Retourne le nom d'utilisateur.
    private def username(request: RequestHeader):Option[String] = {
        request.headers.get("api-key").flatMap { key => 
            users.find ( _.key == key ).map { _.prenom }
        }
    }

    // Vérifie que l'utilisateur est bien authentifiée. 
    def asUser(f: => String => Request[AnyContent] => Result) ={
        Security.Authenticated(username, onUnauthorized) { email => 
            Action({ request =>
                Logger.info("Acces granted - " + email)
                f(email)(request)
            })
        }
    }
}
