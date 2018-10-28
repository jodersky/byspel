package byspel
import byspel.app.DatabaseApi
import java.io.File

trait Migrations extends app.DatabaseApp { self: DatabaseApi =>

  override def start(): Unit = {
    super.start()
    log("running migrations")
    import sys.process._
    val cmd = Process(
      s"sqitch deploy db:sqlite:${config.database.file}",
      Some(new File(config.database.sqitch_base))
    )
    if (cmd.run.exitValue() != 0) {
      log("fatal: applying database migrations failed")
      sys.exit(1)
    }
  }

}
