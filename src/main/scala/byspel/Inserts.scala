package byspel

import app.{DatabaseApi, DatabaseApp}
import java.security.SecureRandom
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._

trait Inserts extends DatabaseApp { self: DatabaseApi =>
  import profile.api._

  private val root =
    UsersRow(new UUID(0l, 0l).toString,
             "root@crashbox.io",
             Some("Root User"),
             "no avatar",
             Some(Timestamp.from(Instant.now())))

  private val password = {
    val prng = new SecureRandom()
    val bytes = new Array[Byte](8)
    prng.nextBytes(bytes)
    bytes.map(b => f"$b%02x").mkString("")
  }

  private def inserts = Seq(
    Users insertOrUpdate root,
    Shadow insertOrUpdate ShadowRow(
      root.id,
      PasswordHash.protect(password)
    )
  )

  override def start(): Unit = {
    super.start()
    log("checking for root user")

    val f = Users.filter(_.id === root.id).exists.result.flatMap {
      case false =>
        log("creating root user")
        log(s"root password is: $password")
        (Users insertOrUpdate root).andThen(
          Shadow insertOrUpdate ShadowRow(
            root.id,
            PasswordHash.protect(password)
          )
        )
      case true =>
        log("root user exists")
        DBIO.successful(())
    }
    Await.result(database.run(f), 2.seconds)
  }

}
