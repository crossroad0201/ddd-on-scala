import sbt._

object Dependencies {
  lazy val TestingDepends = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )

  lazy val InfrastructureDepends = Seq(
    "org.scalikejdbc" %% "scalikejdbc" % "3.0.2"
  )
}
