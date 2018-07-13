name := """playhello"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  cacheApi,
  ehcache,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "joda-time" % "joda-time" % "2.3",
  "org.xerial" % "sqlite-jdbc" % "3.8.6"
  ,"com.typesafe.play" %% "play-slick" % "3.0.3"
  ,"com.typesafe.play" %% "play-slick-evolutions" % "3.0.3"
  ,"org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
)

//libraryDependencies += evolutions

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

//routesGenerator := InjectedRoutesGenerator

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

scalaSource in Test := baseDirectory.value / "src/test/scala"
