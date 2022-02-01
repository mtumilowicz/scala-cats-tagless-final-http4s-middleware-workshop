package app

import app.authorization.AuthorizationService
import app.gateway.HttpApi
import app.product.ProductService
import app.user.UserService
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

  override def run(args: List[String]): IO[ExitCode] = for {
    logger <- Slf4jLogger.create[IO]
    authorizationService = AuthorizationService(UserService.inMemory[IO]())
    productService = ProductService.inMemory[IO]()
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
