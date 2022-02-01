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
    libraryDependencies ++= Seq(
      compilerPlugin(
        "org.typelevel" %% "kind-projector" % "0.13.2"
          cross CrossVersion.full
      ),
      "org.typelevel" %% "cats-core" % "2.6.1",
      "org.typelevel" %% "cats-effect" % "3.1.1",
      "org.typelevel" %% "log4cats-slf4j" % "2.2.0",
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "dev.profunktor" %% "http4s-jwt-auth" % "1.0.0",
      http4s("dsl"),
      http4s("ember-server"),
      http4s("ember-client"),
      http4s("circe"),
      circe("core"),
      circe("generic"),
      circe("parser"),
      circe("refined"),
      "com.disneystreaming" %% "weaver-cats" % "0.7.6" % Test,
      "com.disneystreaming" %% "weaver-scalacheck" % "0.7.6" % Test
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations", "-Wconf:cat=unused:info"
    )
  )
