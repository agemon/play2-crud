import sbt._
import Keys._
import play.Project._
import com.typesafe.sbtidea.SbtIdeaPlugin

object ApplicationBuild extends Build {

  override def settings = super.settings ++ SbtIdeaPlugin.ideaSettings

  val PlayCrudName = "play-crud"
  val PlayCrudVersion = "1.0.0-SNAPSHOT"
  val PlayCrudDependencies = Seq(
    javaCore,
    "commons-beanutils" % "commons-beanutils" % "1.8.3"
  )
  val PlayCrudEbeanDependencies = Seq(
    javaCore,javaJdbc,javaEbean
  )

  val appName         = PlayCrudName+"-ebean-test"
  val appVersion      = PlayCrudVersion
  val appDependencies = PlayCrudEbeanDependencies

  val PlayCrudCore = Project(PlayCrudName+"-core", file("modules/core")).settings(
    crossPaths := false,
    version := PlayCrudVersion,
    scalaVersion := "2.10.0"
  )

  val PlayCrud = play.Project(PlayCrudName, PlayCrudVersion, PlayCrudDependencies, file("modules/crud")).settings(
    crossPaths := false
  ).dependsOn(PlayCrudCore)

  val PlayCrudEbean = play.Project(PlayCrudName+"-ebean", PlayCrudVersion, PlayCrudEbeanDependencies, file("modules/ebean")).settings(
    crossPaths := false
  ).dependsOn(PlayCrudCore)


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  ).dependsOn(PlayCrud, PlayCrudEbean).aggregate(PlayCrud, PlayCrudEbean)

}
