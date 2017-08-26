import Dependencies._

lazy val commonSettings = Seq(
  organization := "crossroad0201.dddonscala",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.3",
  scalacOptions := Seq(
    "-deprecation",
    "-feature"
  ),
  scalafmtOnCompile in ThisBuild := true,
  scalafmtTestOnCompile in ThisBuild := true,
  libraryDependencies ++= CommonDepends
)

lazy val dddOnScala = (project in file("."))
  .aggregate(domain, application, infrastructure, rdb, sampleController)
  .settings(
    commonSettings,
    name := "dddonscala",
    publishArtifact := false
  )

lazy val domain = (project in file("modules/domain")).settings(
  commonSettings,
  name := "dddonscala-domain"
)

lazy val application = (project in file("modules/application"))
  .dependsOn(domain)
  .settings(
    commonSettings,
    name := "dddonscala-application"
  )

lazy val infrastructure = (project in file("modules/adapter/infrastructure"))
  .dependsOn(application, domain)
  .settings(
    commonSettings,
    name := "dddonscala-infrastructure"
  )

lazy val rdb = (project in file("modules/adapter/infrastructure/rdb"))
  .dependsOn(infrastructure, application, domain)
  .settings(
    commonSettings,
    name := "dddonscala-rdb",
    libraryDependencies ++= InfrastructureDepends,
    // Flyway でデータベースをマイグレーションするための接続情報
    flywayUrl := "jdbc:mariadb://localhost:3306/dddonscala",
    flywayUser := "root",
    flywayPassword := "dddonscala"
  )

lazy val sampleController = (project in file("modules/adapter/controller/sample"))
  .dependsOn(rdb, infrastructure, application, domain)
  .settings(
    commonSettings,
    name := "dddonscala-sampleadapter"
  )
