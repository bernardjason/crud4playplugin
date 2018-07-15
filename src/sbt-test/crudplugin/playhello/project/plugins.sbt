// The Play plugin
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.13")
//addSbtPlugin("org.bjason" % "crud4playplugin" % "0.1-SNAPSHOT")

sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("org.bjason" % "crud4playplugin" % x)
  case _ => addSbtPlugin("org.bjason" % "crud4playplugin" % "0.2-SNAPSHOT" )
}

