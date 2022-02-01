import sbt.Keys.{scalaVersion, testFrameworks}

val http4s = "0.23.1"
val http4sJwtAuth = "1.0.0"
val circe = "0.14.1"

def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % http4s
def circe(artifact: String): ModuleID = "io.circe" %% s"circe-$artifact" % circe

lazy val root = (project in file("."))
  .settings(
    name := "scala-http4s-middleware-workshop",
    version := "0.1",
    scalaVersion := "2.13.8",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= Dependencies.App,
    scalacOptions in ThisBuild := Options.scalacOptions
  )
