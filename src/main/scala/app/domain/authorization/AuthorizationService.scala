package app.domain.authorization

import app.domain.user.{User, UserService}
import cats.Applicative
import cats.effect._
import io.circe.generic.auto._
import io.circe.parser.decode
import pdi.jwt.JwtClaim

case class AuthorizationService[F[_] : Async](
                                               userJwtAuth: UserJwtAuth,
                                               userService: UserService[F]
                                             ) {
  def authorize(claim: JwtClaim): F[Option[User]] = {

    val claimContent = decode[ClaimContent](claim.content).left.map(_.getMessage)
    val userMaybe = claimContent.map(_.user_name).map(userService.getByUserName)
    userMaybe match {
      case Left(_) => Applicative[F].pure(None)
      case Right(user) => user
    }
  }
}
