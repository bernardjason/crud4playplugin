lazy val commonSettings = Seq(
  organization in ThisBuild := "org.bjason",
  git.useGitDescribe := true
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    sbtPlugin := true,
    name := "crud4playplugin",
    description := "plugin for play framework to generate crud interface",
    // This is an example.  sbt-bintray requires licenses to be specified 
    // (using a canonical name).
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := false,
    bintrayRepository := "org.bjason",
    bintrayOrganization in bintray := None
  ).enablePlugins(SbtTwirl,GitVersioning)


libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"

scalaVersion := "2.12.6"

sbtPlugin := true

releaseVersionFile := file("version.sbt")

import sbtrelease.ReleaseStateTransformations._

releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
