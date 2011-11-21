package controllers

import play.mvc._
import views.Application._
import play.libs.OAuth2
import play.Logger
import models.User
import play.db.anorm.NotAssigned
import util.Random
import com.google.gson.JsonObject

object Application extends Controller {

    val FACEBOOK: OAuth2 = new OAuth2(
        "https://graph.facebook.com/oauth/authorize",
        "https://graph.facebook.com/oauth/access_token",
        "205397189534884",
        "74d3a14cea14ee09b497f335e9720405")

    def authURL = play.mvc.Router.getFullUrl("Application.auth")

    def index = {
        val user = renderArgs("user").get.asInstanceOf[Option[User]]
        val friends =
        html.index(user)
    }

    @Before private[controllers] def setUser() {
        var user: Option[User] = None

        if (session contains "uid") {
            Logger.info("existing user: " + session.get("uid"))
            user = User.find("id = {user_id}").on("user_id" -> session.get("uid").toLong).first()
        }
        user match {
            case Some(existingUser) => Logger.info("welcome back: " + existingUser.name)
            case None => {
                user = Some(User.create(User(NotAssigned, Random.alphanumeric take 10 mkString)).get)
                session.put("uid", user.get.id)
            }
        }

        renderArgs.put("user", user)
    }
}
