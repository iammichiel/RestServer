package controllers

import org.pegdown.PegDownProcessor

import play.api._
import play.api.mvc._

import models._

object Application extends Controller with Authorization {

    def index = Action {
        val p:PegDownProcessor = new PegDownProcessor();
        val t:String = p.markdownToHtml(views.html.index().toString)
        
        Ok(views.html.main(t))
    }
}