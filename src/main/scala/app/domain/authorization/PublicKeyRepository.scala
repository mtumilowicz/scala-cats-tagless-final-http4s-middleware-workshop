package app.domain.authorization

trait PublicKeyRepository[F[_]] {
  def fetch(): F[String]
}
