import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, StandardCopyOption}
import sbt.Keys._
import sbt.{Def, _}
import sbtassembly.AssemblyPlugin

object LocalPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin && AssemblyPlugin
  override def trigger = allRequirements

  object autoImport {
    val dbMigrate = taskKey[File]("Apply sqitch database migrations")
    val dbTables = taskKey[Seq[File]]("Generate database tables")
    val fhsDist = taskKey[File](
      "Copy application  to a directory structure according to the " +
        "Filesystem Hierarchy Standard, simplifying further packaging for " +
        "final platforms such as Debian or Docker.")
  }
  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions ++= Seq("-deprecation", "-feature"),
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.2.3",
      "com.typesafe.slick" %% "slick-codegen" % "3.2.3",
      "org.slf4j" % "slf4j-nop" % "1.7.19",
      "org.xerial" % "sqlite-jdbc" % "3.25.2"
    ),
    dbMigrate := {
      import sys.process._
      val dbFile = target.value / "main.db"
      val cmd = s"sqitch deploy db:sqlite:${dbFile.getPath}"
      val status = cmd.run().exitValue()
      if (status != 0) {
        throw new MessageOnlyException(s"command '$cmd' exited with $status")
      }
      dbFile
    },
    dbTables := {
      val dbUrl = s"jdbc:sqlite:${dbMigrate.value.toPath}"
      val out = (scalaSource in Compile).value
      (runner in Compile).value.run(
        "slick.codegen.SourceCodeGenerator",
        (dependencyClasspath in Compile).value.files,
        Array(
          "slick.jdbc.SQLiteProfile", // slick driver
          "org.sqlite.JDBC", // JDBC driver
          dbUrl, // database connection
          out.toString, // file
          "byspel" // package
        ),
        streams.value.log
      )
      Seq(out / "Tables.scala")
    },
    fhsDist := {
      val root = (target in Compile).value / "dist"
      Files.createDirectories((root / "etc").toPath)
      Files.createDirectories((root / "usr" / "bin").toPath)
      Files.createDirectories((root / "usr" / "share" / "byspel").toPath)
      Files.createDirectories((root / "usr" / "lib" / "byspel").toPath)
      Files.createDirectories((root / "var" / "lib" / "byspel").toPath)
      Files.copy(
        AssemblyPlugin.autoImport.assembly.value.toPath,
        (root / "usr" / "share" / "byspel" / "main.jar").toPath,
        StandardCopyOption.REPLACE_EXISTING
      )
      Files.writeString(
        (root / "etc" / "byspel.toml").toPath,
        """|[http]
           |address = "0.0.0.0"
           |port = 8555
           |
           |[database]
           |file = "/var/lib/byspel/main.db"
           |sqitch_base = "/usr/share/byspel/sqitch"
           |""".stripMargin
      )
      import sys.process._
      require(
        s"sqitch bundle --dest-dir=$root/usr/share/byspel/sqitch"
          .run()
          .exitValue() == 0,
        "error bundling sqitch"
      )
      require(
        Process(
          Seq(
            "cc",
            "-g",
            "-O2",
            "-Wall",
            "-Werror",
            "-fPIC",
            "-shared",
            "-o",
            s"$root/usr/lib/byspel/libprocname.so",
            "launcher/procname.c"
          )
        ).run().exitValue() == 0,
        "error compiling launcher"
      )

      val exec = (root / "usr" / "bin" / "byspel").toPath
      Files.writeString(
        exec,
        s"""|#!/bin/bash
            |export LD_PRELOAD=/usr/lib/byspel/libprocname.so
            |export PROCNAME="byspel"
            |exec java -cp /usr/share/byspel/main.jar byspel.Main "$$@"
            |""".stripMargin
      )
      Files.setPosixFilePermissions(
        exec,
        PosixFilePermissions.fromString("rwxr-xr-x")
      )
      root
    }
  )

  override def buildSettings: Seq[Def.Setting[_]] =
    addCommandAlias("start", "reStart dev.toml") ++
      addCommandAlias("stop", "reStop")

}
