package test

import play.api._
import play.api.mvc._

import org.specs2.mutable._

import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test._
import play.api.test.Helpers._

class ProjetsSpec extends Specification {
  
    val headers = FakeHeaders(Seq("api-key" -> Seq("michiel")))

    // Ajout d'un utilisateur
    def ajoutUtilisateur = route(FakeRequest(POST, "/utilisateurs/add", headers, 
        AnyContentAsFormUrlEncoded(Map(
            "nom"        -> Seq("missotten"), 
            "prenom"     -> Seq("michiel"), 
            "email"      -> Seq("michiel@lil-web.fr"), 
            "motdepasse" -> Seq("hello")
        ))
    ))

    // Ajout d'un projet
    def ajoutProjet = route(FakeRequest(POST, "/projets/add", headers, 
        AnyContentAsFormUrlEncoded(Map(
            "nom" -> Seq("projet-nom-test")
        ))
    ))

    // Initialisation d'une application. 
    def defaultApplication = FakeApplication(
        additionalConfiguration = (inMemoryDatabase(options = Map("MODE" -> "MySQL")))
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
                val Some(result) = ajoutProjet
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
                val Some(addProjet) = ajoutProjet
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


    "L'API Taches : " should {

        "etre vide si aucun projet existe" in {
            running(defaultApplication) {
                val Some(result) = route(FakeRequest(GET, "/taches/1", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(result) must equalTo(OK)
                contentType(result) must beSome("application/json")
                contentAsString(result) must contain("[]")
            }
        }

        "etre vide si un projet existe" in {
            running(defaultApplication) {

                // Ajout d'un projet valide
                val Some(addProjet) = route(FakeRequest(POST, "/projets/add", headers, 
                    AnyContentAsFormUrlEncoded(Map("nom" -> Seq("michiel")))
                ))
                status(addProjet) must equalTo(CREATED)

                // Récuperation de la liste des projets
                val Some(listingTache) = route(FakeRequest(GET, "/taches/1", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))

                status(listingTache) must equalTo(OK)
                contentType(listingTache) must beSome("application/json")
                contentAsString(listingTache) must contain("[]")
            }
        }

        "ajouter une tache avec projet et sans utilisateur" in {
            running(defaultApplication) {

                // Ajout d'un projet valide
                val Some(addProjet) = ajoutProjet
                status(addProjet) must equalTo(CREATED)

                // Ajout d'une tache
                val Some(addTache) = route(FakeRequest(POST, "/taches/add/1", headers, 
                    AnyContentAsFormUrlEncoded(
                        Map(
                            "titre"       -> Seq("michiel"), 
                            "description" -> Seq(), 
                            "statut"      -> Seq("0"), 
                            "utilisateur" -> Seq()
                        )
                    )
                ))

                status(addTache) must equalTo(CREATED)

                // Ajout d'une tache
                val Some(listeTache) = route(FakeRequest(GET, "/taches/1", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))
                status(listeTache) must equalTo(OK)
                contentType(listeTache) must beSome("application/json")
                contentAsString(listeTache) must contain("\"titre\":\"michiel\"")
                contentAsString(listeTache) must contain("\"statut\":\"0\"")
            }
        }

        "ajouter une tache avec projet et utilisateur" in {
            running(defaultApplication) {

                // Ajout d'un projet valide
                val Some(addProjet) = ajoutProjet
                status(addProjet) must equalTo(CREATED)

                // Ajoute un utilisateur.
                val Some(addUtilisateur) = ajoutUtilisateur
                status(addUtilisateur) must equalTo(CREATED)

                // Ajout d'une tache
                val Some(addTache) = route(FakeRequest(POST, "/taches/add/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "titre"       -> Seq("titre-test"), 
                        "description" -> Seq("tache-description-test"), 
                        "statut"      -> Seq("0"), 
                        "utilisateur" -> Seq("1")
                    ))
                ))
                status(addTache) must equalTo(CREATED)
            }
        }

        "editer une tache" in {
            running(defaultApplication) {
                // Ajout d'un projet valide
                val Some(addProjet) = ajoutProjet
                status(addProjet) must equalTo(CREATED)

                // Ajoute un utilisateur.
                val Some(addUtilisateur1) = route(FakeRequest(POST, "/utilisateurs/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq("nom-1"), 
                        "prenom"     -> Seq("prenom-1"), 
                        "email"      -> Seq("user1@test.fr"), 
                        "motdepasse" -> Seq("motdepasse-1")
                    ))
                ))
                status(addUtilisateur1) must equalTo(CREATED)

                // Ajoute un utilisateur.
                val Some(addUtilisateur2) = route(FakeRequest(POST, "/utilisateurs/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq("nom-2"), 
                        "prenom"     -> Seq("prenom-2"), 
                        "email"      -> Seq("user2@test.fr"), 
                        "motdepasse" -> Seq("motdepasse-2")
                    ))
                ))
                status(addUtilisateur2) must equalTo(CREATED)

                // Ajout d'une tache
                val Some(addTache) = route(FakeRequest(POST, "/taches/add/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "titre"       -> Seq("titre-test"), 
                        "description" -> Seq("tache-description-test"), 
                        "statut"      -> Seq("0"), 
                        "utilisateur" -> Seq()
                    ))
                ))
                status(addTache) must equalTo(CREATED)

                // Edition d'une tache
                val Some(editTache) = route(FakeRequest(POST, "/taches/edit/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "titre"       -> Seq("titre-test-edit"), 
                        "description" -> Seq("tache-description-test-edit"), 
                        "statut"      -> Seq("1"), 
                        "utilisateur" -> Seq("2")
                    ))
                ))
                status(editTache) must equalTo(OK)

                // Affichage de la liste des taches
                val Some(liste) = route(FakeRequest(GET, "/taches/1", headers, 
                    AnyContentAsFormUrlEncoded(Map())
                ))
                status(liste) must equalTo(OK)
                contentType(liste) must beSome("application/json")
                contentAsString(liste) must contain("titre-test-edit")
            }
        }

        "editer une tache invalide" in {
            running(defaultApplication) {

                // Ajout d'un projet valide
                val Some(addProjet) = ajoutProjet
                status(addProjet) must equalTo(CREATED)

                // Ajoute un utilisateur.
                val Some(addUtilisateur1) = route(FakeRequest(POST, "/utilisateurs/add", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "nom"        -> Seq("nom-1"), 
                        "prenom"     -> Seq("prenom-1"), 
                        "email"      -> Seq("user1@test.fr"), 
                        "motdepasse" -> Seq("motdepasse-1")
                    ))
                ))
                status(addUtilisateur1) must equalTo(CREATED)

                // Ajout d'une tache
                val Some(addTache) = route(FakeRequest(POST, "/taches/add/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "titre"       -> Seq("titre-test"), 
                        "description" -> Seq("tache-description-test"), 
                        "statut"      -> Seq("0"), 
                        "utilisateur" -> Seq()
                    ))
                ))
                status(addTache) must equalTo(CREATED)

                // Edition d'une tache
                val Some(editTache) = route(FakeRequest(POST, "/taches/edit/1", headers, 
                    AnyContentAsFormUrlEncoded(Map(
                        "titre"       -> Seq(""), 
                        "description" -> Seq(""), 
                        "statut"      -> Seq(""), 
                        "utilisateur" -> Seq("test")
                    ))
                ))
                status(editTache) must equalTo(BAD_REQUEST)
                contentAsString(editTache) must contain("titre")
                contentAsString(editTache) must contain("statut")
                contentAsString(editTache) must contain("utilisateur")
            }
        }

        "supprimer une tache" in {
            todo
        }
    }

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