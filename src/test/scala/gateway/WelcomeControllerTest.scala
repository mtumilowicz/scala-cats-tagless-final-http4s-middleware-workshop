package gateway

import app.gateway.welcome.{Hello, WelcomeController}
import cats.effect._
import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.syntax.literals._

object WelcomeControllerTest extends HttpSuite {

  test("GET items fails") {
    val req = GET(uri"/")
    val routes = WelcomeController[IO]().routes
    expectHttpBodyAndStatus(routes, req)(Hello("hi"), Status.Ok)
  }

}
