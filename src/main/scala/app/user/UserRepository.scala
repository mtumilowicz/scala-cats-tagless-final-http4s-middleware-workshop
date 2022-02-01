package app.user

import cats.Applicative
import cats.effect.{Ref, Sync}
import cats.implicits._

trait UserRepository[F[_]] {
  def getByUserName(userName: String): F[Option[User]]
}

case class UserInMemoryRepository[F[_] : Applicative](ref: Ref[F, Map[String, User]])
  extends UserRepository[F] {
  override def getByUserName(userName: String): F[Option[User]] = ref.get.map(_.get(userName))
}

object UserInMemoryRepository {
  def apply[F[_] : Sync](init: Map[String, User]): UserRepository[F] =
    UserInMemoryRepository[F](Ref.unsafe(init))
}
