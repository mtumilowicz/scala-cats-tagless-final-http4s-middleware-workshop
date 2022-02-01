package gateway

import app.domain.product.Product
import app.domain.user._
import app.gateway.product.{ProductApiOutput, ProductController}
import app.infrastructure.product.ProductModule
import cats.data.Kleisli
import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.server.AuthMiddleware

object ProductControllerTest extends HttpSuite {

  test("GET items fails 2") {
    val authMiddleware = AuthMiddleware[IO, User](Kleisli.pure(User("user1", Permissions(Set(Permission.Product)))))
    val productService = ProductModule.inMemoryService[IO](List(Product("1", "product 1")))

    val req = GET(uri"/products")
    val routes = ProductController[IO](productService, authMiddleware).routes

    expectHttpBodyAndStatus(routes, req)(ProductApiOutput("product 1"), Status.Ok)
  }

  test("GET items fails 3") {
    val authMiddleware = AuthMiddleware[IO, User](Kleisli.pure(User("user1", Permissions(Set(Permission.None)))))
    val productService = ProductModule.inMemoryService[IO](List(Product("1", "product 1")))

    val req = GET(uri"/products")
    val routes = ProductController[IO](productService, authMiddleware).routes

    expectHttpStatus(routes, req)(Status.Forbidden)
  }

}
