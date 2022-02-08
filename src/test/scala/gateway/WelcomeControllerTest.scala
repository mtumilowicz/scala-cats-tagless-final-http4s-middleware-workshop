package gateway

import app.gateway.welcome.{HelloApiOutput, WelcomeController}
import cats.effect._
import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax

object WelcomeControllerTest extends HttpSuite {

  test("GET on open route always succeeds") {
    val req = GET(uri"/")
    val routes = WelcomeController[IO]().routes
    expectHttpBodyAndStatus(routes, req)(HelloApiOutput("hi"), Status.Ok)
  }

}
