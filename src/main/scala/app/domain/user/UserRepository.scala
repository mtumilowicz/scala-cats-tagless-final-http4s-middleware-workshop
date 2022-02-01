package app.domain.user

trait UserRepository[F[_]] {
  def getByUserName(userName: String): F[Option[User]]
}
