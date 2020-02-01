import sbt._

object Dependencies {

  lazy val catsVersion = "2.0.0"
  lazy val circeVersion = "0.12.3"
  lazy val scalaTestVersion = "3.1.0"

  val ItUnitTest = "it,test"

  lazy val libraries = Seq(
    "org.typelevel" %% "cats-core" % catsVersion,

    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,

    "org.scalatest" %% "scalatest" % scalaTestVersion % ItUnitTest
  )
}
