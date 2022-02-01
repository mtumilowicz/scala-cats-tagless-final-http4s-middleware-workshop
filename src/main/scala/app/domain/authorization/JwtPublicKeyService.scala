package app.domain.authorization

import cats.effect.kernel.Async
import dev.profunktor.auth.JwtPublicKey
import dev.profunktor.auth.jwt.JwtAsymmetricAuth
import cats.implicits._
import pdi.jwt.JwtAlgorithm

case class JwtPublicKeyService[F[_] : Async](publicKeyRepository: PublicKeyRepository[F]) {
  def fetchUserAuth(): F[UserJwtAuth] =
    for {
      publicKey <- publicKeyRepository.fetch()
      rsa <- JwtPublicKey.rsa[F](publicKey, JwtAlgorithm.allRSA())
    } yield
      UserJwtAuth(
        JwtAsymmetricAuth(rsa)
      )
}
