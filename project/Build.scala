import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "RestServer"
    val appVersion      = "2.0-SNAPSHOT"

    val appDependencies = Seq(
        jdbc, anorm, 
        "mysql"       % "mysql-connector-java" % "5.1.21", 
        "org.pegdown" % "pegdown"              % "1.2.1"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
        scalacOptions ++= Seq("-deprecation","-unchecked","-feature")
    )
}