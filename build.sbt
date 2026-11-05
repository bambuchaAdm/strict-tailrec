ThisBuild / scalaVersion := "3.9.0"
ThisBuild / crossScalaVersions := Seq("2.12.20", "2.13.16", "3.3.6", "3.4.3", "3.5.0", "3.5.1", "3.5.2", "3.7.4", "3.9.0", "3.10.0")

def variant(name: String) = Project(name, file(name)).settings(
  Compile / unmanagedSourceDirectories := Seq(baseDirectory.value, (LocalRootProject / baseDirectory).value / "stubs")
)

lazy val v1 = variant("v1")
lazy val v2 = variant("v2")
lazy val v3 = variant("v3")

lazy val demo = (project in file("demo")).dependsOn(v1).settings(fork := true)

lazy val root = (project in file(".")).settings(publish / skip := true)
