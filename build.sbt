lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "authorizer",
    version := "1.0",
    scalaVersion := "2.13.1",
    Defaults.itSettings,
    libraryDependencies ++= Dependencies.libraries,
    outputStrategy := Some(StdoutOutput)
  )

scalacOptions += "-language:higherKinds"

coverageExcludedPackages := SCoverageConfig.excludedPackages
coverageMinimum := SCoverageConfig.minimumCoverage
coverageFailOnMinimum := SCoverageConfig.failOnMinimum
