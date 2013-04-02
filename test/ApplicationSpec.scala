package test

import org.specs2.mutable._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._

class ProjetsSpec extends Specification {
  
    val headers = FakeHeaders(Seq("api-key" -> Seq("michiel")))

    def defaultApplication = FakeApplication(
        additionalConfiguration = (inMemoryDatabase(options = Map("MODE" -> "MySQL")))
    )

    "Projets" should {
    
        "lister les projets à vide" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(GET, "/projets", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(result) must equalTo(OK)
                contentType(result) must beSome("application/javascript")
                contentAsString(result) must contain("[]")
            }
        }

        "ajouter un projet valide" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(
                        Map("nom" -> Seq("michiel"))
                    )
                ))

                status(result) must equalTo(CREATED)
            }
        }

        "ajouter un projet invalide" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(
                        Map()
                    )
                ))

                status(result) must equalTo(BAD_REQUEST)
            }
        }

        "ajouter un projet valide et le voir dans la liste" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(
                        Map("nom" -> Seq("michiel"))
                    )
                ))

                status(result) must equalTo(CREATED)
            }
        }

        // "ajouter un projet invalide" in {
        //     running(FakeApplication()) {
        //         route(FakeRequest(POST, "/"))
        //     }
        // } 
    }
}