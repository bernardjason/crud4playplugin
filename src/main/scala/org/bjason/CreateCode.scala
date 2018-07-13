package org.bjason

import java.io.{BufferedWriter, File, FileWriter}

//case class CreateCode(fullPath:String,namespace:String,table:String) {
case class CreateCode(fullPath:String,crud:Crud) {

  new File(namespaceAsPath).mkdirs();

  val modelExpanded = org.bjason.txt.Model.render(crud)
  val apiExpanded = org.bjason.txt.Api.render(crud)
  val tableExpanded = org.bjason.txt.TableOperation.render(crud)
  val dataConvert = org.bjason.txt.DateConvert.render(crud)
  val routesExpanded = org.bjason.txt.routes.render(crud)

  val tableName = crud.name.toLowerCase

  def createModel: File = {
    val path = s"${namespaceAsPath}/${tableName}/tables"
    new File(path).mkdirs()
    val file = new File( s"${path}/${crud.name}Model.scala" );
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(modelExpanded.toString)
    bw.close();
    file
  }
  def createApi: File = {
    val path = s"${namespaceAsPath}/${tableName}/controllers"
    new File(path).mkdirs()
    val file = new File( s"${path}/${crud.name}Api.scala" );
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(apiExpanded.toString)
    bw.close();
    file
  }
  def createTable: File = {
    val path = s"${namespaceAsPath}/${tableName}/tables"
    new File(path).mkdirs()
    val file = new File( s"${path}/${crud.name}TableOperation.scala" );
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(tableExpanded.toString)
    bw.close();
    file
  }
  def createDataConvert: File = {
    val path = s"${namespaceAsPath}/${tableName}/tables"
    new File(path).mkdirs()
    val file = new File( s"${path}/DateConvert.scala" );
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(dataConvert.toString)
    bw.close();
    file
  }

  // dont return a file. We dont want sbt trying to compile this one!
  def createRoutes = {
    val path = s"${fullPath}/../../../../conf"
    new File(path).mkdirs()
    val file = new File( s"${path}/${crud.packageName}.${tableName}.routes" );
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(routesExpanded.toString)
    bw.close();
    println("Make sure an entry like this is in main routes file")
    println(s"-> <prefix url> ${crud.packageName}.${tableName}.Routes")
  }

  def namespaceAsPath  = fullPath+"/"+crud.packageName.replaceAll("[.]","/")

}
