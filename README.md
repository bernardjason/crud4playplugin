# crud4playplugin

this plugin can be used to generate the boiler plate code to access a
database and provide a rest api for it that offers create read update delete.

There is a sample test project under directory

[src/sbt-test/crudplugin/playhello](src/sbt-test/crudplugin/playhello)

that shows how the configuration file
[conf/crud/Request.conf](conf/crud/Request.conf)

is used to generate some slick db code to perform usual sql operations.
The generated scala code is directory target/scala-2.12/src_managed/main

The plugin will scan for all files under conf/crud and generate a set of
files
* Model.scala holds the case class that represents the db table
* TableOperation.scala holds the slick code for db operations
* Api.scala is the scala code for play to expose api
* DataConvert is a class for handling mapping db timestamp to json

the plugin also generates a routes file in conf using the package as
defined in the equivalent conf/crud file

so in example project the package is org.bjason and the config file is Request so
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
