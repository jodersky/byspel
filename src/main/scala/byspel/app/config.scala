package byspel
package app

case class Config(
    http: HttpConfig,
    database: DatabaseConfig
)

case class HttpConfig(
    address: String,
    port: Int
)
case class DatabaseConfig(
    file: String,
    sqitch_base: String
)
