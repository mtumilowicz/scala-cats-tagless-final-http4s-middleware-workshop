import sbt._

object Dependencies {

  object Versions {
    val http4s = "0.23.1"
    val http4sJwtAuth = "1.0.0"
    val circe = "0.14.1"
  }
  import Versions._

  def http4sLib(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % http4s
  def circeLib(artifact: String): ModuleID = "io.circe" %% s"circe-$artifact" % circe

  val App =
    List(
      "org.typelevel" %% "cats-core" % "2.6.1",
      "org.typelevel" %% "cats-effect" % "3.1.1",
      "org.typelevel" %% "log4cats-slf4j" % "2.2.0",
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "dev.profunktor" %% "http4s-jwt-auth" % "1.0.0",
      http4sLib("dsl"),
      http4sLib("ember-server"),
      http4sLib("ember-client"),
      http4sLib("circe"),
      circeLib("core"),
      circeLib("generic"),
      circeLib("parser"),
      circeLib("refined"),
      "com.disneystreaming" %% "weaver-cats" % "0.7.6" % Test,
      "com.disneystreaming" %% "weaver-scalacheck" % "0.7.6" % Test
    )

}
