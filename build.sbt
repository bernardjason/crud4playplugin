name := "crud4playplugin"

organization := "org.bjason"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.6"

sbtPlugin := true

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"

