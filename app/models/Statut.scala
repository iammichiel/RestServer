package models

import play.api.libs.json.Json

case class Statut(id: Long, nom: String) {
    def toJson = Json.toJson(
        Map(
            "id"  -> id.toString,
            "nom" -> nom
        )
    )
}

object Statut {

    val statuts = Seq(
        Statut(1, "En attente"), 
        Statut(2, "En cours"), 
        Statut(3, "En attente de validation"), 
        Statut(4, "Terminé"), 
        Statut(5, "Fermé")
    )

    def all = statuts

    def id(id:String) = {
        statuts.find( id == _.id.toString ).getOrElse( throw new Exception("Shit happened"))
    }
}