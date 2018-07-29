package org.bjason

import java.io.File

import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.Matchers._
import scala.io.Source
import scala.reflect.io.Directory


trait CustomMatchers {

  class FileExistsMatcher extends Matcher[java.io.File] {

    def apply(left: java.io.File) = {

      val fileOrDir = if (left.isFile) "file" else "directory"

      val failureMessageSuffix =
        fileOrDir + " named " + left.getName + " did not exist"

      val negatedFailureMessageSuffix =
        fileOrDir + " named " + left.getName + " existed"

      MatchResult(
        left.exists,
        "The " + failureMessageSuffix,
        "The " + negatedFailureMessageSuffix,
        "the " + failureMessageSuffix,
        "the " + negatedFailureMessageSuffix
      )
    }
  }

  val exist = new FileExistsMatcher
}

// Make them easy to import with:
// import CustomMatchers._
object CustomMatchers extends CustomMatchers

class BasicTests extends FlatSpec with MustMatchers {

  "A string" should "say bye" in {
    assert("bye" === "bye")
  }

  def withGeneratedCode( test:(CreateCode,Crud) => Unit): Unit = {
    val output = new File("target/tmpgen")
    val directory = new Directory(output)
    directory.deleteRecursively()

    output.mkdirs()
    CrudPlugin.createHere = output.toString
    //val crud = Crud("Hello","hello.world",List(),"")
    val crud = GenerateFromConfig("src/test/resources/Request.conf")
    val c = CreateCode(output.toString,crud.fromConfig() ) ;
    test(c,crud.fromConfig);
  }

  "A simple test" should "create 3 scala files" in withGeneratedCode { (c,crud) =>

    val table = c.createTable
      table must exist
      table should be a 'file
      table getName() should be ("RequestTableOperation.scala")

    c.createModel must not be null


    c.createDataConvert must not be null

  }

  "an api test" should "create 1 file" in withGeneratedCode( { (c,crud)  =>
    val api = c.createApi
      api must not be null
      api must exist

  })

  "expand model" should "create case values" in withGeneratedCode { (c, crud) =>

    crud.createModel() must equal ("request_id:Long= -1,request_name:String,request_desc:String,request_start_date:Option[java.sql.Timestamp],request_required_for_date:Option[java.sql.Timestamp]")
    println(crud.createModel())
  }
  "expand table" should "create defs" in withGeneratedCode { (c, crud) =>

    println(s"[${crud.createTable()}]")
    crud.createTable() must equal (
"""      def request_id = column[Long]( "REQUEST_ID"  , O.PrimaryKey, O.AutoInc  )
      def request_name = column[String]( "REQUEST_NAME"  , null  )
      def request_desc = column[String]( "REQUEST_DESC"  , null  )
      def request_start_date = column[Option[java.sql.Timestamp]]( "REQUEST_START_DATE"  , null  )
      def request_required_for_date = column[Option[java.sql.Timestamp]]( "REQUEST_REQUIRED_FOR_DATE"  , null  )
      def * = (request_id,request_name,request_desc,request_start_date,request_required_for_date) <> (( RequestModel.apply _).tupled,RequestModel.unapply)"""
    )
  }
  "expand create insert" should "create defs" in withGeneratedCode { (c, crud) =>

    println(s"${crud.createCreate()}")
    crud.createCreate() must equal (
"""  def create(request_name:String,request_desc:String,request_start_date:Option[java.sql.Timestamp],request_required_for_date:Option[java.sql.Timestamp]) = db.run {
    ( request.map(p=>(
      p.request_name,p.request_desc,p.request_start_date,p.request_required_for_date))
      returning request.map(_.request_id)
      into ((info, request_id) => RequestModel(request_id, info._1,info._2,info._3,info._4))
    ) +=(request_name,request_desc,request_start_date,request_required_for_date)
  }
"""
    )
  }
  "expand update" should "create defs" in withGeneratedCode { (c, crud) =>

    println(s"${crud.createUpdate()}")
    crud.createUpdate() must equal (
"""  def update(request_id:Long,request_name:String,request_desc:String,request_start_date:Option[java.sql.Timestamp],request_required_for_date:Option[java.sql.Timestamp]): Future[Long]  = db.run {
    val newRow = RequestModel(request_id,request_name,request_desc,request_start_date,request_required_for_date)
    val row = for { row <- request.filter { r => r.request_id === request_id } } yield row
    row.update (newRow ).map(returnStatus(_))
  }
"""
    )
  }

  "plugin" should "be able to load conf files" in {
    CrudPlugin.configInDirectory = "src/test/resources"
    val packageName = "org/bjason/request"
    val output = new File("target/tmp/target/scala-2.12/src_managed/main/")
    val directory = new Directory(output)
    directory.deleteRecursively()
    output.mkdirs()
    val files = CrudPlugin.processAllFiles(output)
    files.length should equal (5)
    files(0).toString should equal (s"${output}/${packageName}/controllers/RequestApi.scala")
    files(1).toString should equal (s"${output}/${packageName}/tables/RequestModel.scala")
    files(2).toString should equal (s"${output}/${packageName}/tables/RequestTableOperation.scala")
    files(3).toString should equal (s"${output}/${packageName}/tables/DateConvert.scala")
    for(f <- files ) {
      println(f)
    }
  }

  "plugin" should "create routes file" in {
    CrudPlugin.configInDirectory = "src/test/resources"
    val packageName = "org/bjason/request"
    val output = new File("target/tmp/target/scala-2.12/src_managed/main/")
    val directory = new Directory(output)
    directory.deleteRecursively()
    output.mkdirs()
    val files = CrudPlugin.processAllFiles(output)
    files.length should equal (5)
    val route = new File("target/tmp/conf/org.bjason.request.routes")
    route should exist
    Source.fromFile(route).toList.mkString should equal(
"""GET     /               org.bjason.request.controllers.RequestApi.getAll
GET     /:request_id          org.bjason.request.controllers.RequestApi.get(request_id:Long)
POST    /               org.bjason.request.controllers.RequestApi.insertupdate
PUT     /               org.bjason.request.controllers.RequestApi.insertupdate
DELETE  /:request_id          org.bjason.request.controllers.RequestApi.delete(request_id:Long)
"""
    )
  }
}
