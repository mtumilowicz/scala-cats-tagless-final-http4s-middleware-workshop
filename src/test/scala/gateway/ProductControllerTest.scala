package gateway

import app.domain.authorization.UserJwtAuth
import app.domain.product.Product
import app.domain.user._
import app.gateway.product.ProductController
import app.infrastructure.authorization.AuthorizationModule
import app.infrastructure.product.ProductModule
import app.infrastructure.user.UserModule
import cats.data.Kleisli
import cats.effect.IO
import dev.profunktor.auth.JwtPublicKey
import dev.profunktor.auth.jwt.JwtAsymmetricAuth
import org.http4s.Method.GET
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.server.AuthMiddleware
import pdi.jwt.JwtAlgorithm

object ProductControllerTest extends HttpSuite {

  private val jwtPublicKey =
"""-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCjk/HJbdaoZqIq8ZIien3wxqP
4jwJRXTMu6s95FYZm2ADr6ROE5gPIxX0RmTFN1lyvAEtZbQgKG63TJiPHgYQu8RD
31ERe4X0pXpDoTEiinyVy7j2aL8s+2aFe0c/X3Ny4Hnk+y1S5qlKPgrLV5bbylLZ
/Ml3+ofZ+HgIntpKcQIDAQAB
-----END PUBLIC KEY-----"""

  test("GET items fails") {
    for {
      dummy <- JwtPublicKey.rsa[IO](jwtPublicKey, JwtAlgorithm.allRSA())
      userJwtAuth = UserJwtAuth(JwtAsymmetricAuth(dummy))
      userService = UserModule.inMemoryService[IO](List(User("user1", Permissions(Set(Permission.None)))))
      authorizationService = AuthorizationModule.service(userService, userJwtAuth)
      authMiddleware = AuthorizationModule.middleware[IO](authorizationService)
      productService = ProductModule.inMemoryService[IO](List(Product("1", "product 1")))
      req = GET(uri"/products")
      routes = ProductController[IO](productService, authMiddleware).routes

      result <- expectHttpStatus(routes, req)(Status.Forbidden)
    } yield result
  }

  test("GET items fails 2") {
    val authMiddleware = AuthMiddleware[IO, User](Kleisli.pure(User("user1", Permissions(Set(Permission.Product)))))
    val productService = ProductModule.inMemoryService[IO](List(Product("1", "product 1")))

    val req = GET(uri"/products")
    val routes = ProductController[IO](productService, authMiddleware).routes

    expectHttpStatus(routes, req)(Status.Ok)
  }

}
