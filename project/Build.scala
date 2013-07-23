import sbt._
import Keys._
import play.Project._

object PlayCrudBuild extends Build {
	
	val PlayCrudName = "play-crud"
	val PlayCrudVersion = "1.0.0-SNAPSHOT"
	val PlayCrudOrganisation = "fr.njin"
	
	val PlayCrudDependencies = Seq(
      javaCore,
      "commons-beanutils" % "commons-beanutils" % "1.8.3"
    )

	val PlayCrudEbeanDependencies = Seq(
		javaCore,javaJdbc,javaEbean
  	)

	lazy val PlayCrudCore = Project(PlayCrudName+"-core", file("src/core")).settings(
		crossPaths := false,
		version := PlayCrudVersion,
		organization := PlayCrudOrganisation,
		scalaVersion := "2.10.0",

		javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
		// See : https://play.lighthouseapp.com/projects/82401/tickets/898-javadoc-error-invalid-flag-g-when-publishing-new-module-local
		publishArtifact in (Compile, packageDoc) := false
	)

    lazy val PlayCrud = play.Project(PlayCrudName, PlayCrudVersion, PlayCrudDependencies, file("src/crud")).settings(
    	crossPaths := false,
      	organization := PlayCrudOrganisation,

		javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
		// See : https://play.lighthouseapp.com/projects/82401/tickets/898-javadoc-error-invalid-flag-g-when-publishing-new-module-local
      	publishArtifact in (Compile, packageDoc) := false

    ).dependsOn(PlayCrudCore).aggregate(PlayCrudCore, PlayCrudEbean)

    lazy val PlayCrudEbean = play.Project(PlayCrudName+"-ebean", PlayCrudVersion, PlayCrudEbeanDependencies, file("src/ebean")).settings(
    	crossPaths := false,
      	organization := PlayCrudOrganisation,

      	javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
      	// See : https://play.lighthouseapp.com/projects/82401/tickets/898-javadoc-error-invalid-flag-g-when-publishing-new-module-local
      	publishArtifact in (Compile, packageDoc) := false

    ).dependsOn(PlayCrudCore)

}