package app.infrastructure.user

import app.domain.user.{User, UserRepository, UserService}
import cats.effect.Ref
import cats.effect.kernel.Sync

object UserModule {

  def inMemoryRepository[F[_] : Sync](init: Map[String, User]): UserRepository[F] =
    UserInMemoryRepository[F](Ref.unsafe(init))

  def inMemoryService[F[_] : Sync](users: List[User]): UserService[F] = {
    val asMap = users.map(user => (user.userName, user)).toMap
    UserService(inMemoryRepository(asMap))
  }

}
