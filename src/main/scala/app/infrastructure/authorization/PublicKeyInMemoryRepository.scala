package app.infrastructure.authorization

import app.domain.authorization.PublicKeyRepository
import cats.effect.Async

case class PublicKeyInMemoryRepository[F[_] : Async](publicKey: String) extends PublicKeyRepository[F] {
  override def fetch(): F[String] =
    Async[F].pure(publicKey)
}
