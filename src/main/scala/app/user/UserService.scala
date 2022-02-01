package app.user

import cats.effect.kernel.Sync

case class UserService[F[_]](repository: UserRepository[F]) {
  def getByUserName(userName: String): F[Option[User]] =
    repository.getByUserName(userName)
}

object UserService {
  def inMemory[F[_] : Sync](): UserService[F] = {
    val init = Map(
      "user1" -> User("user1", Permissions(Set(Permission.Product))),
      "user2" -> User("user2", Permissions(Set(Permission.None)))
    )
    UserService(UserInMemoryRepository[F](init))
  }
}
