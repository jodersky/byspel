lazy val byspel = project
  .in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.6.7",
      "com.typesafe.akka" %% "akka-http" % "10.1.5",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",
      "com.typesafe.akka" %% "akka-stream" % "2.5.17",
      "de.mkammerer" % "argon2-jvm" % "2.5",
      "tech.sparse" %% "toml-scala" % "0.1.1"
    )
  )

lazy val dist = taskKey[File](
  "Generate single, distributable package under a well-known directory."
)
dist := {
  val out = target.value / "dist.jar"
  IO.copyFile(assembly.value, out)
  out
}
