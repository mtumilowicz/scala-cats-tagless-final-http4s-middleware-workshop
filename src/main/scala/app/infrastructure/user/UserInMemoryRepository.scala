package app.infrastructure.user

import app.domain.user.{User, UserRepository}
import cats.Applicative
import cats.effect.Ref
import cats.implicits._


case class UserInMemoryRepository[F[_] : Applicative](ref: Ref[F, Map[String, User]])
  extends UserRepository[F] {
  override def getByUserName(userName: String): F[Option[User]] = ref.get.map(_.get(userName))
}