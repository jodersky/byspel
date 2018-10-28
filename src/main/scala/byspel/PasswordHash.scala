package byspel

import de.mkammerer.argon2.Argon2Factory
import java.nio.charset.StandardCharsets

object PasswordHash {

  private val argon2 = Argon2Factory.create()

  /** Salt and hash a password. */
  def protect(plain: String): String =
    argon2.hash(
      10, // iterations
      65536, // memory
      1, // parallelism
      plain, // password
      StandardCharsets.UTF_8
    )

  def verify(plain: String, hashed: String): Boolean =
    argon2.verify(hashed, plain)

}
