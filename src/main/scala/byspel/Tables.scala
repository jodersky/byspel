package byspel
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.SQLiteProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema
    : profile.SchemaDescription = Sessions.schema ++ Shadow.schema ++ Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Sessions
    *  @param sessionId Database column session_id SqlType(UUID), PrimaryKey
    *  @param userId Database column user_id SqlType(UUID)
    *  @param expires Database column expires SqlType(TIMESTAMP) */
  case class SessionsRow(sessionId: String,
                         userId: String,
                         expires: java.sql.Timestamp)

  /** GetResult implicit for fetching SessionsRow objects using plain SQL queries */
  implicit def GetResultSessionsRow(
      implicit e0: GR[String],
      e1: GR[java.sql.Timestamp]): GR[SessionsRow] = GR { prs =>
    import prs._
    SessionsRow.tupled((<<[String], <<[String], <<[java.sql.Timestamp]))
  }

  /** Table description of table sessions. Objects of this class serve as prototypes for rows in queries. */
  class Sessions(_tableTag: Tag)
      extends profile.api.Table[SessionsRow](_tableTag, "sessions") {
    def * =
      (sessionId, userId, expires) <> (SessionsRow.tupled, SessionsRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (Rep.Some(sessionId), Rep.Some(userId), Rep.Some(expires)).shaped.<>(
        { r =>
          import r._; _1.map(_ => SessionsRow.tupled((_1.get, _2.get, _3.get)))
        },
        (_: Any) =>
          throw new Exception("Inserting into ? projection not supported."))

    /** Database column session_id SqlType(UUID), PrimaryKey */
    val sessionId: Rep[String] = column[String]("session_id", O.PrimaryKey)

    /** Database column user_id SqlType(UUID) */
    val userId: Rep[String] = column[String]("user_id")

    /** Database column expires SqlType(TIMESTAMP) */
    val expires: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("expires")

    /** Foreign key referencing Users (database name users_FK_1) */
    lazy val usersFk = foreignKey("users_FK_1", userId, Users)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade)
  }

  /** Collection-like TableQuery object for table Sessions */
  lazy val Sessions = new TableQuery(tag => new Sessions(tag))

  /** Entity class storing rows of table Shadow
    *  @param userId Database column user_id SqlType(UUID), PrimaryKey
    *  @param hash Database column hash SqlType(STRING) */
  case class ShadowRow(userId: String, hash: String)

  /** GetResult implicit for fetching ShadowRow objects using plain SQL queries */
  implicit def GetResultShadowRow(implicit e0: GR[String]): GR[ShadowRow] = GR {
    prs =>
      import prs._
      ShadowRow.tupled((<<[String], <<[String]))
  }

  /** Table description of table shadow. Objects of this class serve as prototypes for rows in queries. */
  class Shadow(_tableTag: Tag)
      extends profile.api.Table[ShadowRow](_tableTag, "shadow") {
    def * = (userId, hash) <> (ShadowRow.tupled, ShadowRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (Rep.Some(userId), Rep.Some(hash)).shaped.<>(
        { r =>
          import r._; _1.map(_ => ShadowRow.tupled((_1.get, _2.get)))
        },
        (_: Any) =>
          throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(UUID), PrimaryKey */
    val userId: Rep[String] = column[String]("user_id", O.PrimaryKey)

    /** Database column hash SqlType(STRING) */
    val hash: Rep[String] = column[String]("hash")

    /** Foreign key referencing Users (database name users_FK_1) */
    lazy val usersFk = foreignKey("users_FK_1", userId, Users)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction)
  }

  /** Collection-like TableQuery object for table Shadow */
  lazy val Shadow = new TableQuery(tag => new Shadow(tag))

  /** Entity class storing rows of table Users
    *  @param id Database column id SqlType(UUID), PrimaryKey
    *  @param primaryEmail Database column primary_email SqlType(STRING)
    *  @param fullName Database column full_name SqlType(STRING)
    *  @param avatar Database column avatar SqlType(STRING)
    *  @param lastLogin Database column last_login SqlType(TIMESTAMP) */
  case class UsersRow(id: String,
                      primaryEmail: String,
                      fullName: Option[String],
                      avatar: String,
                      lastLogin: Option[java.sql.Timestamp])

  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(
      implicit e0: GR[String],
      e1: GR[Option[String]],
      e2: GR[Option[java.sql.Timestamp]]): GR[UsersRow] = GR { prs =>
    import prs._
    UsersRow.tupled(
      (<<[String],
       <<[String],
       <<?[String],
       <<[String],
       <<?[java.sql.Timestamp]))
  }

  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag)
      extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * =
      (id, primaryEmail, fullName, avatar, lastLogin) <> (UsersRow.tupled, UsersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (Rep.Some(id),
       Rep.Some(primaryEmail),
       fullName,
       Rep.Some(avatar),
       lastLogin).shaped.<>(
        { r =>
          import r._;
          _1.map(_ => UsersRow.tupled((_1.get, _2.get, _3, _4.get, _5)))
        },
        (_: Any) =>
          throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(UUID), PrimaryKey */
    val id: Rep[String] = column[String]("id", O.PrimaryKey)

    /** Database column primary_email SqlType(STRING) */
    val primaryEmail: Rep[String] = column[String]("primary_email")

    /** Database column full_name SqlType(STRING) */
    val fullName: Rep[Option[String]] = column[Option[String]]("full_name")

    /** Database column avatar SqlType(STRING) */
    val avatar: Rep[String] = column[String]("avatar")

    /** Database column last_login SqlType(TIMESTAMP) */
    val lastLogin: Rep[Option[java.sql.Timestamp]] =
      column[Option[java.sql.Timestamp]]("last_login")
  }

  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
