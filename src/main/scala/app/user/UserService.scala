package app.user

case class UserService[F[_]](repository: UserRepository[F]) {
  def getByUserName(userName: String): F[Option[User]] =
    repository.getByUserName(userName)
}
