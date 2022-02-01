package app.domain.user

case class UserService[F[_]](repository: UserRepository[F]) {
  def getByUserName(userName: String): F[Option[User]] =
    repository.getByUserName(userName)
}
