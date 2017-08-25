import sbt._

object Dependencies {
  lazy val CommonDepends = Seq(
    "ch.qos.logback"  %  "logback-classic"   % "1.2.3",
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test
  )

  lazy val InfrastructureDepends = Seq(
    "org.mariadb.jdbc" % "mariadb-java-client" % "2.1.0",
    "org.scalikejdbc" %% "scalikejdbc" % "3.0.2",
    "org.scalikejdbc" %% "scalikejdbc-config"  % "3.0.2",
    "org.scalikejdbc" %% "scalikejdbc-test" % "3.0.2" % Test
  )
}
