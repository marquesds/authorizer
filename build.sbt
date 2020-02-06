import sbt._

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "authorizer",
    version := "1.0",
    scalaVersion := "2.12.10",
    Defaults.itSettings,
    libraryDependencies ++= Dependencies.libraries,
    outputStrategy := Some(StdoutOutput),
    scalacOptions += "-language:higherKinds",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

coverageExcludedPackages := SCoverageConfig.excludedPackages
coverageMinimum := SCoverageConfig.minimumCoverage
coverageFailOnMinimum := SCoverageConfig.failOnMinimum
