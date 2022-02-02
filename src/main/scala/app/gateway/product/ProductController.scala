package app.gateway.product

import app.domain.product._
import app.domain.user.User
import cats.Monad
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

final case class ProductController[F[_] : Monad](
                                                  productService: ProductService[F],
                                                  authMiddleware: AuthMiddleware[F, User]
                                                ) extends Http4sDsl[F] {

  private val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root / id as user =>
      if (user.hasProductPermission)
        productService.getById(id).flatMap(_.fold(NotFound("product not found"))(product => Ok(ProductApiOutput.fromDomain(product))))
      else Forbidden(ErrorApiOutput("insufficient permissions"))
  }

  def routes: HttpRoutes[F] = Router(
    "/products" -> authMiddleware(httpRoutes)
  )
}
