package models

import play.db.anorm._
import play.db.anorm.defaults._
import play.data.validation.Annotations._

case class User(
    id: Pk[Long],
    @Required name: String
)

object User extends Magic[User]