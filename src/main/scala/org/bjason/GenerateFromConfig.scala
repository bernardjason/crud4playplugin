package org.bjason

import java.io.File

import com.typesafe.config.{ConfigFactory, ConfigObject, ConfigValue}
import scala.collection.JavaConverters._

case class Crud(name:String,packageName:String,fields:List[Field],objectMapper:String,fromFile:File) {

  fields(0).fieldName
  def createModel() = {
    val result = new StringBuilder
    var comma=false
    var defaultField=true
    for (f <- fields) {
      if (comma) result.append(",") else comma = true
      result.append(s"${f.fieldName}:${f.fieldType}")
      if (defaultField) {
        result.append(s"= -1")
        defaultField = false
      }
    }
    result.toString
  }
  def getPackageName:String = {

    if ( packageName != null && packageName.length > 0 ) {
      return s"${packageName}."
    }
    return ""
  }

  def createTable() = {
    val result = new StringBuilder
    for (f <- fields) {
      result.append(s"""      def ${f.fieldName} = column[${f.fieldType}]( "${f.fieldColumn}" """)
      if ( f.fieldColumnDefinition.isDefined ) {
        result.append(s""" , ${f.fieldColumnDefinition.get} """)
      }
      result.append(s""" )\n""")
    }

    result.append(s"      def * = (")


    result.append( getFieldNameList() )
    result.append(s""") <> (( ${name}Model.apply _).tupled,${name}Model.unapply)""")

    result.toString()
  }

  def getFieldNameList(objPrefix:String="",skipPrimaryKey:Boolean=false) = {
    val result = new StringBuilder
    var comma=false
    var skipNow=true;
    for (f <- fields) {
      if (  skipPrimaryKey && skipNow ) {
        skipNow=false
      } else {
        if (comma) result.append(",") else comma = true
        result.append(s"""${objPrefix}${f.fieldName}""")
      }
    }
    result.toString()
  }

  def createCreate() = {
    val result = new StringBuilder
    result.append(s"""  def create(""")
    var comma=false
    for (i <- 1 until fields.length) {
      val f= fields(i)
      if ( comma ) result.append(",") else comma = true
      result.append(s"${f.fieldName}:${f.fieldType}")
    }
    result.append(s""") = db.run {\n""")
    result.append(s"""    ( request.map(p=>(\n""")
    result.append(s"""      """)
    comma=false
    for (i <- 1 until fields.length) {
      val f= fields(i)
      if ( comma ) result.append(",") else comma = true
      result.append(s"p.${f.fieldName}")
    }
    result.append(s"))\n")
    result.append(s"      returning request.map(_.${fields(0).fieldName})\n")
    result.append(s"      into ((info, ${fields(0).fieldName}) => ${name}Model(${fields(0).fieldName}, ")
    comma=false
    for(i <- 1 until fields.length ) {
      if ( comma ) result.append(",") else comma = true
      result.append(s"info._${i}")

    }
    result.append(s"))\n")
    result.append(s"    ) +=(")
    comma=false
    for (i <- 1 until fields.length) {
      val f= fields(i)
      if ( comma ) result.append(",") else comma = true
      result.append(s"${f.fieldName}")
    }
    result.append(s")\n")
    result.append(s"  }\n")
    result.toString()
  }

  def createUpdate() = {
    val result = new StringBuilder
    result.append(s"""  def update(""")
    var comma=false
    for (f <- fields) {
      if ( comma ) result.append(",") else comma = true
      result.append(s"${f.fieldName}:${f.fieldType}")
    }
    result.append(s"""): Future[Long]  = db.run {\n""")
    result.append(s"""    val newRow = ${name}Model(""")
    comma=false
    for (f <- fields) {
      if ( comma ) result.append(",") else comma = true
      result.append(s"${f.fieldName}")
    }
    result.append(s""")\n""")
    result.append(s"""    val row = for { row <- request.filter { r => r.${fields(0).fieldName} === ${fields(0).fieldName } } } yield row\n""")
    result.append(s"""    row.update (newRow ).map(returnStatus(_))\n""")
    result.append(s"""  }\n""")

    result.toString()
  }
}
case class Field(fieldName:String,fieldType:String,fieldColumn:String,fieldColumnDefinition:Option[String])

case class GenerateFromConfig(fileName:String) {


  def toString(implicit x: ConfigValue,path:String):scala.Predef.String = {
    val v = x.asInstanceOf[ConfigObject].toConfig.root().get(path)
    if ( v != null ) return v.render().dropRight(1).drop(1)
    return null;
  }

  //def fromConfig(filename:String="src/test/resources/Request.conf") {
  def fromConfig()  = {
    val confFile = new File(fileName);
    val conf = ConfigFactory.parseFile(confFile)
    val model = conf.getConfig("model")
    val name = model.getString("name")
    val packageName = model.getString("packageName")

    val fields = for (f <- model.getList("fields").asScala) yield {
      val optional = f.atPath("column_definition")
      val columnDefinition = if (optional == null) None else Some(toString(f, "column_definition"))
      Field(
        toString(f, "name"),
        toString(f, "type"),
        toString(f, "column"),
        columnDefinition
      )
    }
    val objectMapper = if ( model.hasPath("object_mapper") )
      model.getString("object_mapper")
    else ""

    Crud(name,packageName,fields.toList,objectMapper,confFile)
  }
}
