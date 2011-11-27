package controllers

import play.mvc._
import views.Application._
import play.libs.OAuth2
import play.libs.WS
import play.Logger
import models.User
import com.google.gson.{JsonObject, Gson}
import java.lang.String
import play.db.anorm.NotAssigned
import util.Random

object Application extends Controller {

    val FACEBOOK: OAuth2 = new OAuth2(
        "https://graph.facebook.com/oauth/authorize",
        "https://graph.facebook.com/oauth/access_token",
        "205397189534884",
        "74d3a14cea14ee09b497f335e9720405")

    def authURL = play.mvc.Router.getFullUrl("Application.auth")

    def index = {
        val user = renderArgs.get("user", classOf[User])
        var friends: Option[JsonObject] = None

        user.accessToken.map(accessToken => {
            val gson = new Gson
            val me = WS.url("https://graph.facebook.com/me?access_token=%s", WS.encode(accessToken)).get.getJson.getAsJsonObject
            val id = gson.fromJson(me.get("id"), classOf[String]);
            friends = Some(WS.url("https://graph.facebook.com/%s/friends?access_token=%s", id, WS.encode(accessToken)).get.getJson.getAsJsonObject)
        })

        html.index(friends)
    }

    def auth = {
        // if we get an authenticated response, update the user access token and redirect to Action(index)
        if (OAuth2.isCodeResponse) {
            val user = renderArgs.get("user", classOf[User])
            val response = FACEBOOK.retrieveAccessToken(authURL)
            user.accessToken = Some(response.accessToken)
            User.update(user)
            Action(index)
        }
        else {
            FACEBOOK.retrieveVerificationCode(authURL);
        }
    }

    def getUser(uid: Long): Option[User] = User.findById(uid)

    @Before private[controllers] def setUser() {

        def getUserFromSession: Option[User] = {
            if (session contains "uid") {
                Logger.info("existing user: " + session.get("uid"))
                getUser(session.get("uid").toLong)
            }
            else None
        }

        val user = getUserFromSession getOrElse {
            User.create(User(NotAssigned, Random.alphanumeric take 10 mkString)).fold(
                ex => throw new RuntimeException(ex.toString),
                (newUser: User) => {
                    session.put("uid", newUser.id)
                    newUser
                }
            )
        }

        renderArgs.put("user", user)
    }
}
