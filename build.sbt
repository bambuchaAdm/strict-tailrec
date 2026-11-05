import sbt.internal.inc.Analysis
import xsbti.{CompileFailed, Severity}

ThisBuild / scalaVersion := "3.9.0"
ThisBuild / crossScalaVersions := Seq("2.12.20", "2.13.16", "3.3.6", "3.4.3", "3.5.0", "3.5.1", "3.5.2", "3.7.4", "3.9.0", "3.10.0")

def variant(name: String) = Project(name, file(name)).settings(
  Compile / unmanagedSourceDirectories := Seq(baseDirectory.value, (LocalRootProject / baseDirectory).value / "stubs")
)

lazy val v1 = variant("v1")
lazy val v2 = variant("v2")
lazy val v3 = variant("v3")

lazy val demo = (project in file("demo")).dependsOn(v1).settings(fork := true)

def atLeast(scalaVersion: String, major: Int, minor: Int, patch: Int): Boolean = {
  val Array(maj, min, pat) = scalaVersion.split('.').take(3).map(_.takeWhile(_.isDigit).toInt)
  Ordering[(Int, Int, Int)].gteq((maj, min, pat), (major, minor, patch))
}

def expectedOutcome(scalaVersion: String, variant: String): String = variant match {
  case "v1" => "silent"
  case "v2" => "error"
  case "v3" => if (atLeast(scalaVersion, 3, 5, 1)) "warning E199" else "error"
}

lazy val verifyMatrix = taskKey[Unit]("Compile v1, v2, v3 and check each outcome against the expected one for the current Scala version")

lazy val root = (project in file(".")).settings(
  publish / skip := true,
  verifyMatrix := Def.uncached {
    val sv = scalaVersion.value
    val observed = Seq(
      "v1" -> (v1 / Compile / compile).result.value,
      "v2" -> (v2 / Compile / compile).result.value,
      "v3" -> (v3 / Compile / compile).result.value
    ).map { case (name, result) =>
      val outcome = result.toEither match {
        case Left(incomplete) =>
          val problems = Incomplete.allExceptions(incomplete).collect { case failed: CompileFailed => failed.problems.toSeq }.flatten
          if (problems.exists(p => p.severity == Severity.Error && p.message.contains("tail position"))) "error" else "OTHER ERROR"
        case Right(analysis: Analysis) =>
          val warnings = analysis.infos.allInfos.values.flatMap(_.getReportedProblems).filter(_.severity == Severity.Warn)
          if (warnings.exists(_.message.contains("non-inlined inner def"))) "warning E199"
          else if (warnings.isEmpty) "silent"
          else "OTHER WARNING"
        case Right(_) => "OTHER"
      }
      name -> outcome
    }
    val mismatches = observed.collect { case (name, got) if got != expectedOutcome(sv, name) => s"$name: expected ${expectedOutcome(sv, name)}, got $got" }
    observed.foreach { case (name, got) => streams.value.log.info(s"$sv $name: $got") }
    if (mismatches.nonEmpty) sys.error(s"Scala $sv: " + mismatches.mkString("; "))
  }
)
