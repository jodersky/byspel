package byspel
package app

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext
import toml.Toml

trait App {

  implicit lazy val system: ActorSystem = ActorSystem()

  implicit lazy val materializer: Materializer = ActorMaterializer()

  implicit lazy val executionContext: ExecutionContext = system.dispatcher

  def start(): Unit = {}
  def stop(): Unit = {}

  def log(msg: String) = System.err.println(msg)

  private var _args: List[String] = Nil
  def args = _args

  lazy val config = args match {
    case Nil =>
      log("fatal: no config file given as first argument")
      sys.exit(1)
    case head :: _ if Files.isReadable(Paths.get(head)) =>
      log(s"loading config from '${args(0)}'")
      import toml.Codecs._
      Toml.parseAs[Config](Files.readString(Paths.get(head))) match {
        case Left(err) =>
          log(s"fatal: syntax error in config file: $err")
          sys.exit(1)
        case Right(value) => value
      }
    case head :: _ =>
      log(s"fatal: config file '$head' is not readable or does not exist")
      sys.exit(1)
  }

  def main(args: Array[String]): Unit = {
    log("starting application")
    _args = args.toList
    config
    sys.addShutdownHook {
      log("stopping application")
      stop()
      log("bye")
    }
    start()
    log("ready")
  }

}
