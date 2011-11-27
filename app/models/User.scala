package models

import play.db.anorm._
import play.db.anorm.defaults._
import play.data.validation.Annotations._

case class User(
    id: Pk[Long],
    @Required name: String,
    var accessToken: Option[String] = None
)

object User extends Magic[User] {
    def findById(userId: Long): Option[User] = find("id = {user_id}").on("user_id" -> userId).first()
}