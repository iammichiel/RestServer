import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "RestServer"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.reactivemongo" %% "play2-reactivemongo" % "0.8", 
        "com.wordnik"       %% "swagger-play2"       % "1.2.1-SNAPSHOT"
    )


    val main = play.Project(appName, appVersion, appDependencies).settings(
        // Add your own project settings here      
    )
}