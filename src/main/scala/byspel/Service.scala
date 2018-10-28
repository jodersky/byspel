package byspel

import app.DatabaseApi
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait Service { self: DatabaseApi =>
  import profile.api._

  implicit def executionContext: ExecutionContext

  def login(id: String,
            password: String): Future[Option[(UsersRow, SessionsRow)]] = {
    val query = for {
      user <- Users
      if user.primaryEmail === id || user.id === id
      shadow <- Shadow
      if shadow.userId === user.id
    } yield {
      user -> shadow.hash
    }
    val userResult = database.run(query.result.headOption).map {
      case Some((user, hash)) if PasswordHash.verify(password, hash) =>
        Some(user)
      case _ =>
        // dummy password hash to avoid timing attacks
        PasswordHash.verify(
          password,
          "$argon2i$v=19$m=65536,t=10,p=1$gFZ4l8R2rpuhfqXDFuugNg$fOvTwLSaOMahD/5AfWlbRsSMj4E6k34VpGyl5xe24yA")
        None
    }

    userResult.flatMap {
      case Some(u) =>
        val newSession = SessionsRow(
          UUID.randomUUID().toString,
          u.id,
          Timestamp.from(Instant.now.plusSeconds(60 * 60 * 24))
        )
        database
          .run(
            Sessions += newSession
          )
          .map(_ => Some(u -> newSession))
      case None => Future.successful(None)
    }
  }

  def checkSession(sessionId: String): Future[Option[UsersRow]] = database.run {
    val query = for {
      session <- Sessions
      if session.sessionId === sessionId
      if session.expires > Timestamp.from(Instant.now())
      user <- Users
      if user.id === session.userId
    } yield {
      user
    }
    query.result.headOption
  }

  def endSession(sessionId: String) = database.run {
    Sessions.filter(_.sessionId === sessionId).delete
  }

}
