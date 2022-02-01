package app

import app.domain.authorization.{AuthorizationService, JwtPublicKeyService}
import app.domain.product.Product
import app.domain.user.{Permission, Permissions, User}
import app.gateway.HttpApi
import app.infrastructure.authorization.PublicKeyInMemoryRepository
import app.infrastructure.product.ProductModule
import app.infrastructure.user.UserModule
import cats.effect._
import com.comcast.ip4s.Port
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Application extends IOApp {

  private def showEmberBanner[F[_] : Logger](s: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")

  // for the sake of simplicity - probably will be fetched from the authorization server
  private val jwtPublicKey =
    """-----BEGIN PUBLIC KEY-----
    |MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCjk/HJbdaoZqIq8ZIien3wxqP
    |4jwJRXTMu6s95FYZm2ADr6ROE5gPIxX0RmTFN1lyvAEtZbQgKG63TJiPHgYQu8RD
    |31ERe4X0pXpDoTEiinyVy7j2aL8s+2aFe0c/X3Ny4Hnk+y1S5qlKPgrLV5bbylLZ
    |/Ml3+ofZ+HgIntpKcQIDAQAB
    |-----END PUBLIC KEY-----""".stripMargin

  private val users = List(
    User("user1", Permissions(Set(Permission.Product))),
    User("user2", Permissions(Set(Permission.None)))
  )

  private val products = List(
    Product("1", "product 1")
  )

  private def prepareAuthorizationService(): IO[AuthorizationService[IO]] = for {
    jwtUserAuth <- JwtPublicKeyService(PublicKeyInMemoryRepository[IO](jwtPublicKey)).fetchUserAuth()
    userService = UserModule.inMemoryService[IO](users)
  } yield AuthorizationService(jwtUserAuth, userService)

  override def run(args: List[String]): IO[ExitCode] = for {
    logger <- Slf4jLogger.create[IO]
    authorizationService <- prepareAuthorizationService()
    productService = ProductModule.inMemoryService[IO](products)
    api = HttpApi[IO](authorizationService, productService).httpApp
    server <- EmberServerBuilder.default[IO]
      .withPort(Port.fromInt(9090).get)
      .withHttpApp(api)
      .build
      .evalTap(showEmberBanner(_)(logger))
      .useForever
      .as(ExitCode.Success)
  } yield server
}
