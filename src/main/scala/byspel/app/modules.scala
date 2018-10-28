package byspel
package app

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import spray.json.DefaultJsonProtocol
import scala.concurrent.Await
import scala.concurrent.duration._

trait HttpApi
    extends Directives
    with SprayJsonSupport
    with DefaultJsonProtocol {
  def route: Route
}

trait HttpApp extends App { self: HttpApi =>

  override def start() = {
    super.start()
    log("binding to interface")
    val future =
      Http().bindAndHandle(route, config.http.address, config.http.port)
    Await.result(future, 2.seconds)
  }

}

trait DatabaseApi extends Tables {
  val profile = Tables.profile
  import profile.api._

  def database: Database

}

trait DatabaseApp extends App { self: DatabaseApi =>
  import profile.api.Database

  lazy val database: Database = Database.forURL(
    s"jdbc:sqlite:${config.database.file}",
    driver = "org.sqlite.JDBC"
  )

  override def start() = {
    super.start()
    log("initializing database")
    database
  }
}
