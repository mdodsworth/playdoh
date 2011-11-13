package controllers

import play._
import play.mvc._

object Application extends Controller {
    
    import views.Application._
    
    def index = html.index()
    def sayHello = {
        params.get("myName") match {
            case name:String if name.trim().length() > 0 => html.sayHello(name)
            case other => {
                flash += ("error" -> "Oops, please enter your name!")
                Action(index)
            }
        }
    }
}
