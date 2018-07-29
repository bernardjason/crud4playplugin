# crud4playplugin

this plugin can be used to generate the boiler plate code to access a
database and provide a rest api for it that offers create read update delete.
I use Slick for the database access.

from the demo blog project for example

GET     /tables/blog ( with optional query params sortBy and direction)

GET     /tables/blog/<primary key>

POST    /tables/blog

PUT     /tables/blog

DELETE  /tables/blog/:blog_id

slick,controller and routes generated from single configuration file.

You have to create the database as well as configure it in application.conf

There are sample test projects under directory

a simple project

[src/sbt-test/crudplugin/playhello](src/sbt-test/crudplugin/playhello)

that shows how the configuration file

[src/sbt-test/crudplugin/playhello/conf/crud/Request.conf](src/sbt-test/crudplugin/playhello/conf/crud/Request.conf)

generates some simple code with an event simple form to create entries. There are
also from curl commands as well.

A more complicated 2 table example.

[src/sbt-test/crudplugin/blog](src/sbt-test/crudplugin/blog)

This one is a simple blog that shows a user table and a blog table. The user
table holds 1 entry for demo purposes, see
[src/sbt-test/crudplugin/blog/conf/evolutions/1.sql](src/sbt-test/crudplugin/blog/conf/eveolutions/1.sql). Without logging on the blog api is unavilable.

The example project shows uses a Module to override the generated Action implementation
and the project provides its own authentication classes. The project also includes
a better example of mapping the date from json to a timestamp in the conf file
```
object_mapper = "extends DateConvert(inDateFormat=\"YYYY-MM-dd HH:mm:ss\" , outDateFormat=\"YYYY-MM-dd HH:mm:ss\")"
```

## details
The configuration file is used to generate some slick db code to perform usual sql operations.
The generated scala code is directory target/scala-2.12/src_managed/main

The plugin will scan for all files under conf/crud and generate a set of
files
* Model.scala holds the case class that represents the db table
* TableOperation.scala holds the slick code for db operations
* Api.scala is the scala code for play to expose api
* DataConvert is a class for handling mapping db timestamp to json
* CrudAction is a trait and class for the api that can be overriden to implement
authentication. The blog project shows some simple authentication on the api's.

the plugin also generates a routes file in conf using the package as
defined in the equivalent conf/crud file

so in the example project the package is org.bjason and the config file is Request so
we end up with this file

org.bjason.request.routes

that needs to be included in routes file with this

-> /tables/request org.bjason.request.Routes

if we want to be able to call the crud api with a uri like

/table/request

## conf file format
```
model = {
    name = "<same as filename>"
        packageName = "<start of package name>"
        fields = [
                // a list of the fields now
                {
                        name = "<scala field name>"
                        type = "<scala type, can be an Option too>"
                        column = "<database column name>"
                        column_definition = "<optional entry see http://slick.lightbend.com/doc/3.0.0/schemas.html>"
                }, ....

        ]
        object_mapper = "<optional if the table has a timestamp to convert>"
}
```


the plugin can be built and tested using
```
sbt:crud4playplugin> scripted
```



