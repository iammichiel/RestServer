package test

import org.specs2.mutable._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._

class TachesSpec extends Specification {

    val headers = FakeHeaders(Seq("api-key" -> Seq("michiel")))
    def defaultApplication = FakeApplication(
        additionalConfiguration = (inMemoryDatabase(options = Map("MODE" -> "MySQL")))
    )

    "L'API Taches : " should {


    }
}