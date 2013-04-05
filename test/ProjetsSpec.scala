package test

import play.api._
import play.api.mvc._

import org.specs2.mutable._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._

class ProjetsSpec extends Specification {
  
    val headers = FakeHeaders(Seq("api-key" -> Seq("michiel")))

    def defaultApplication = FakeApplication(
        additionalConfiguration = (
            inMemoryDatabase(options = Map("MODE" -> "MySQL"))
        )
    )

    "L'API Projets : " should {
    
        "lister les projets à vide" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(GET, "/projets", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(result) must equalTo(OK)
                contentType(result) must beSome("application/json")
                contentAsString(result) must contain("[]")
            }
        }

        "ajouter un projet valide" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("projet-nom-test")
                    ))
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
                val Some(addProjet) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("projet-nom-test")
                    ))
                ))
                status(addProjet) must equalTo(CREATED)

                val Some(lister) = route(FakeRequest(GET, "/projets", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(lister) must equalTo(OK)
                contentType(lister) must beSome("application/json")
                contentAsString(lister) must contain("\"id\":")
                contentAsString(lister) must contain("\"nom\":\"projet-nom-test\"")

            }
        }

        "editer un projet valide" in {
            running(defaultApplication) {

                // Création du projet
                val Some(ajoutProjet) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("projet-nom-test")
                    ))
                ))
                status(ajoutProjet) must equalTo(CREATED)

                // Edition du projet
                val Some(editProjet) = route(FakeRequest(POST, "/projets/edit/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("Nouveau nom")
                    ))
                ))
                status(editProjet) must equalTo(OK)

                // Vérification de la présence. 
                val Some(listeProjets) = route(FakeRequest(GET, "/projets", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))
                status(listeProjets) must equalTo(OK)
                contentType(listeProjets) must beSome("application/json")
                contentAsString(listeProjets) must contain("Nouveau nom")
            }
        }

        "editer un projet invalide" in {
            running(defaultApplication) {

                // Création du projet
                val Some(ajoutProjet) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("projet-nom-test")
                    ))
                ))
                status(ajoutProjet) must equalTo(CREATED)

                // Edition du projet
                val Some(editProjet) = route(FakeRequest(POST, "/projets/edit/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("")
                    ))
                ))
                status(editProjet) must equalTo(BAD_REQUEST)

                // Vérification de la présence. 
                val Some(listeProjets) = route(FakeRequest(GET, "/projets", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))
                status(listeProjets) must equalTo(OK)
                contentType(listeProjets) must beSome("application/json")
                contentAsString(listeProjets) must not contain("Nouveau nom")
            }
        }

        "supprimer un projet" in {
            running(defaultApplication) {

                val Some(ajoutProjet) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom" -> Seq("projet-nom-test")
                    ))
                ))
                status(ajoutProjet) must equalTo(CREATED)

                val Some(editProjet) = route(FakeRequest(GET, "/projets/delete/1", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))
                status(editProjet) must equalTo(OK)

                val Some(listeProjets) = route(FakeRequest(GET, "/projets", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))
                status(listeProjets) must equalTo(OK)
                contentType(listeProjets) must beSome("application/json")
                contentAsString(listeProjets) must contain("[]")
            }
        }
    }
}