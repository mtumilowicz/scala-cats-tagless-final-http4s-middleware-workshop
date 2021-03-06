package app

import app.domain.product.Product
import app.domain.user.{Permission, Permissions, User}
import app.gateway.HttpApi
import app.infrastructure.product.ProductModule
import cats.data.Kleisli
import cats.effect._
import com.comcast.ip4s.Port
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.defaults.Banner
import org.http4s.server.{AuthMiddleware, Server}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object ApplicationMockedAuthorization extends IOApp {

  private def showEmberBanner[F[_] : Logger](s: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")

  private val products = List(
    Product("1", "product 1")
  )

  private def mockedAuthMiddleware(): AuthMiddleware[IO, User] =
    AuthMiddleware[IO, User](Kleisli.pure(User("user1", Permissions(Set(Permission.Product)))))

  override def run(args: List[String]): IO[ExitCode] = for {
    logger <- Slf4jLogger.create[IO]
    productService = ProductModule.inMemoryService[IO](products)
    api = HttpApi[IO](mockedAuthMiddleware(), productService).httpApp
    server <- EmberServerBuilder.default[IO]
      .withPort(Port.fromInt(9090).get)
      .withHttpApp(api)
      .build
      .evalTap(showEmberBanner(_)(logger))
      .useForever
      .as(ExitCode.Success)
  } yield server
}
