package app.gateway.product

import app.product.ProductService
import app.user.User
import cats.Monad
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}
import cats.implicits._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import io.circe.generic.auto._

final case class ProductController[F[_] : Monad](productService: ProductService[F]) extends Http4sDsl[F] {

  private val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root as user =>
      if (user.hasProductPermission)
        productService.getById("1").flatMap(_.fold(NotFound("product not found"))(product => Ok(ProductApiOutput.fromDomain(product))))
      else Forbidden("insufficient permissions")
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    "/products" -> authMiddleware(httpRoutes)
  )
}
