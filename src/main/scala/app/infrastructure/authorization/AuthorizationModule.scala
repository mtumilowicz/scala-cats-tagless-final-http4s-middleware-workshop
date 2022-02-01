package app.infrastructure.authorization

import app.domain.authorization.AuthorizationService
import app.domain.user.{User, UserService}
import cats.MonadThrow
import cats.effect.Async
import dev.profunktor.auth.JwtAuthMiddleware
import org.http4s.server.AuthMiddleware

object AuthorizationModule {

  def service[F[_] : Async](userService: UserService[F]): AuthorizationService[F] =
    AuthorizationService(userService)

    def middleware[F[_] : MonadThrow](authorizationService: AuthorizationService[F]): AuthMiddleware[F, User] =
      JwtAuthMiddleware[F, User](
        authorizationService.userJwtAuth.value,
        _ => claim => authorizationService.authorize(claim)
      )

}
