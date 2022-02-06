package app.gateway

import app.domain.product.ProductService
import app.domain.user.User
import app.gateway.product.ProductController
import app.gateway.welcome.WelcomeController
import cats.effect.Async
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware._
import org.http4s.{HttpApp, HttpRoutes}

import scala.concurrent.duration.DurationInt

case class HttpApi[F[_] : Async](
                                  authorizationMiddleware: AuthMiddleware[F, User],
                                  productService: ProductService[F]
                                ) {
  private val openRoutes: HttpRoutes[F] = WelcomeController[F]().routes
  private val securedRoutes: HttpRoutes[F] = ProductController[F](productService, authorizationMiddleware).routes

  private val routes: HttpRoutes[F] = openRoutes <+> securedRoutes

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      val allowedOrigins = CORSConfig.default
//        .withAnyMethod(false)
//        .withAnyOrigin(false)
//        .withAllowedMethods(Some(Set(org.http4s.Method.GET)))
//        .withAllowedOrigins(Set("https://yahoo.com", "https://duckduckgo.com"))
      CORS(http, allowedOrigins)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60.seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)
}