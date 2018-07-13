package org.bjason

import sbt._
import Keys._

object CrudPlugin extends AutoPlugin {

  var configInDirectory = "conf/crud"
  var createHere = "."
  val suffix = ".conf"

  lazy val helloCommand =
    Command.command("hello") { (state: State) =>

      println("Hi!")
      state
    }

  /*
  lazy val crudCommand =
    Command.command("crud") { (state: State) =>

      val all = processAllFiles( ( sourceManaged in Compile).value )
      all.map{ f => println(f) }
      state
    }
    */

  // by defining autoImport, the settings are automatically imported into user's `*.sbt`
  object autoImport {
    // configuration points, like the built-in `version`, `libraryDependencies`, or `compile`

    val swaggerClean = taskKey[Unit]("Clean swagger generated packages")
  }


  import autoImport._

  override def requires = sbt.plugins.JvmPlugin

  // This plugin is automatically enabled for projects which are JvmPlugin.
  override def trigger = allRequirements


  override lazy val projectSettings = {
    Seq(
      commands += helloCommand,
      //commands += crudCommand,
      /*
      sourceGenerators in Compile += Def.task {
        val contains = org.bjason.txt.Hello.render("Detention","org.bjason.hello")
        val file = (sourceManaged in Compile).value / "org/bjason/hello" / "Detention.scala"
        IO.write(file, contains.toString )
        Seq(file)
      }.taskValue)
      */
      sourceGenerators in Compile += Def.task {
        val all = processAllFiles( ( sourceManaged in Compile).value )
        all.map{ f => println(s"Created ${f}")}
        all.toSeq
        //val crud = Crud("Request","org.bjason.demo",List(),"")
        //val createCode = CreateCode( (sourceManaged in Compile).value.toString, crud)
      }.taskValue)
  }



  def processAllFiles(baseDir:File) = {
    val allFiles = for ( conf <- generateSourceFilesFromConfig ) yield {
      val crud = GenerateFromConfig(conf.toString)
      val createCode = CreateCode(baseDir.toString,crud.fromConfig() ) ;
      createCode.createRoutes
      List(createCode.createApi,createCode.createModel,createCode.createTable,createCode.createDataConvert)
    }
    allFiles.flatten
  }
  def generateSourceFilesFromConfig: List[File] = {
    val dir = new File(configInDirectory)
    println("Config directory is "+dir)
    val source = if ( dir.exists() && dir.isDirectory ) {
      val confFiles = dir.listFiles().toList.filter{ f => f.getName.endsWith(suffix)    }
      confFiles
    } else {
      List()
    }
    source
  }

}
