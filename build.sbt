import Dependencies._

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

lazy val commonSettings = Seq(
  organization := "crossroad0201.dddonscala",
  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.12.3",
  scalacOptions := Seq(
    "-deprecation",
    "-feature"
  ),
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignArguments, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(NewlineAtEndOfFile, true),

  libraryDependencies += scalaTest % Test
)

lazy val dddOnScala = (project in file("."))
  .aggregate(domain, infrastructure, application)
  .settings(
    commonSettings,
    name := "dddonscala",
    publishArtifact := false
  )

lazy val domain = (project in file("modules/domain"))
  .settings(
    commonSettings,
    name := "dddonscala-domain"
  )

lazy val infrastructure = (project in file("modules/infrastructure"))
  .dependsOn(domain)
  .settings(
    commonSettings,
    name := "dddonscala-infrastructure"
  )

lazy val application = (project in file("modules/application"))
  .dependsOn(domain, infrastructure)
  .settings(
    commonSettings,
    name := "dddonscala-application"
  )
