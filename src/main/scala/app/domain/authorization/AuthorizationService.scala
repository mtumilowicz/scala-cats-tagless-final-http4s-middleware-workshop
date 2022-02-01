package app.domain.authorization

import app.domain.user.{User, UserService}
import cats.effect._
import dev.profunktor.auth.JwtPublicKey
import dev.profunktor.auth.jwt.JwtAsymmetricAuth
import io.circe.generic.auto._
import io.circe.parser.decode
import pdi.jwt.{JwtAlgorithm, JwtClaim}

import scala.util.Try

case class AuthorizationService[F[_] : Async](userService: UserService[F]) {

  // for the sake of simplicity - probably will be fetched from the authorization server
  private val jwtPublicKey =
    """-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCjk/HJbdaoZqIq8ZIien3wxqP
4jwJRXTMu6s95FYZm2ADr6ROE5gPIxX0RmTFN1lyvAEtZbQgKG63TJiPHgYQu8RD
31ERe4X0pXpDoTEiinyVy7j2aL8s+2aFe0c/X3Ny4Hnk+y1S5qlKPgrLV5bbylLZ
/Ml3+ofZ+HgIntpKcQIDAQAB
-----END PUBLIC KEY-----"""

  private val rsa = JwtPublicKey.rsa[Try](jwtPublicKey, JwtAlgorithm.allRSA())

  val userJwtAuth: UserJwtAuth =
    UserJwtAuth(
      JwtAsymmetricAuth(rsa.get)
    )

  def authorize(claim: JwtClaim): F[Option[User]] = {
    val claimContent = decode[ClaimContent](claim.content).left.map(_.getMessage)
    val userMaybe = claimContent.map(_.user_name).map(userService.getByUserName)
    userMaybe match {
      case Left(_) => Async[F].pure(None)
      case Right(user) => user
    }
  }
}
