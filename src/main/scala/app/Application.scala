package app

import app.domain.authorization.AuthorizationService
import app.domain.product.Product
import app.domain.user.{Permission, Permissions, User}
import app.gateway.HttpApi
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

  private val users = List(
    User("user1", Permissions(Set(Permission.Product))),
    User("user2", Permissions(Set(Permission.None)))
  )

  private val products = List(
    Product("1", "product 1")
  )

  override def run(args: List[String]): IO[ExitCode] = for {
    logger <- Slf4jLogger.create[IO]
    userService = UserModule.inMemoryService[IO](users)
    authorizationService = AuthorizationService(userService)
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
