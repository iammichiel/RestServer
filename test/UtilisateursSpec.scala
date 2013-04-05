package test

import play.api._
import play.api.mvc._

import org.specs2.mutable._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._

class UtilisateursSpec extends Specification {
  
    val headers = FakeHeaders(Seq("api-key" -> Seq("michiel")))

    def defaultApplication = FakeApplication(
        additionalConfiguration = (inMemoryDatabase(options = Map("MODE" -> "MySQL")))
    )

    "L'API Utilisateurs : " should {

        "lister les utilisateurs à vide" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(GET, "/utilisateurs", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(result) must equalTo(OK)
                contentType(result) must beSome("application/json")
                contentAsString(result) must contain("[]")
            }
        }

        "lister un utilisateur apres ajout" in {
            todo
        }

        "ajouter un utilisateur valide" in {
            todo
        }

        "modifier avec utilisateur valide" in {
            todo            
        }
    }
}