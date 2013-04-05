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
                    AnyContentAsFormUrlEncoded(Map.empty)
                ))

                status(result) must equalTo(OK)
                contentType(result) must beSome("application/json")
                contentAsString(result) must contain("[]")
            }
        }

        "ajouter un utilisateur valide" in {
            running(defaultApplication) {
                val Some(addUtilisateur) = route(FakeRequest(POST, "/utilisateurs/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq("missotten"), 
                        "prenom"     -> Seq("michiel"), 
                        "email"      -> Seq("michiel@lil-web.fr"), 
                        "motdepasse" -> Seq("test")
                    ))
                ))

                status(addUtilisateur) must equalTo(CREATED)

                // Listing des utilisateurs.
                val Some(listeUtilisateurs) = route(FakeRequest(GET, "/utilisateurs", headers, 
                    AnyContentAsFormUrlEncoded(Map.empty)
                ))

                status(listeUtilisateurs) must equalTo(OK)
                contentType(listeUtilisateurs) must beSome("application/json")
                contentAsString(listeUtilisateurs) must */("prenom" -> "michiel")
                contentAsString(listeUtilisateurs) must */("nom" -> "missotten")
                contentAsString(listeUtilisateurs) must */("email" -> "michiel@lil-web.fr")
                contentAsString(listeUtilisateurs) must */("motdepasse" -> "test")
            }
        }

        "ajouter un utilisateur invalide" in {
            running(defaultApplication) {
                val Some(addUtilisateur) = route(FakeRequest(POST, "/utilisateurs/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq(""), 
                        "prenom"     -> Seq(""), 
                        "email"      -> Seq(""), 
                        "motdepasse" -> Seq("")
                    ))
                ))            

                status(addUtilisateur) must equalTo(BAD_REQUEST)
                contentAsString(addUtilisateur) must contain("prenom")
                contentAsString(addUtilisateur) must contain("nom")
                contentAsString(addUtilisateur) must contain("email")
                contentAsString(addUtilisateur) must contain("motdepasse")
            }
        }

        "modifier avec utilisateur valide" in {
            running(defaultApplication) {
                val Some(addUtilisateur) = route(FakeRequest(POST, "/utilisateurs/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq("missotten"), 
                        "prenom"     -> Seq("michiel"), 
                        "email"      -> Seq("michiel@lil-web.fr"), 
                        "motdepasse" -> Seq("hello")
                    ))
                ))            

                status(addUtilisateur) must equalTo(CREATED)

                val Some(editUtilisateur) = route(FakeRequest(POST, "/utilisateurs/edit/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq("nom-test"), 
                        "prenom"     -> Seq("prenom-test"), 
                        "email"      -> Seq("email-test@lil-web.fr"), 
                        "motdepasse" -> Seq("motdepasse-test")
                    ))
                ))                            
                status(editUtilisateur) must equalTo(OK)

                val Some(listeUtilisateurs) = route(FakeRequest(GET, "/utilisateurs", headers, 
                    AnyContentAsFormUrlEncoded(Map.empty)
                ))

                status(listeUtilisateurs) must equalTo(OK)
                contentType(listeUtilisateurs) must beSome("application/json")
                contentAsString(listeUtilisateurs) must */("prenom" -> "prenom-test")
                contentAsString(listeUtilisateurs) must */("nom" -> "nom-test")
                contentAsString(listeUtilisateurs) must */("email" -> "email-test@lil-web.fr")
                contentAsString(listeUtilisateurs) must */("motdepasse" -> "motdepasse-test")
            }   
        }
    }
}