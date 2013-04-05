package test

import play.api._
import play.api.mvc._

import org.specs2.mutable._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._

class StatutsSpec extends Specification {
    val headers = FakeHeaders(Seq("api-key" -> Seq("michiel")))

    def defaultApplication = FakeApplication(
        additionalConfiguration = (inMemoryDatabase(options = Map("MODE" -> "MySQL")))
    )

    "L'api des status : " should {

        "afficher la liste " in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(GET, "/statuts", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(result) must equalTo(OK)
                contentType(result) must beSome("application/json")
                contentAsString(result) must */("id" -> "En attente")
            }
        }
    }
}