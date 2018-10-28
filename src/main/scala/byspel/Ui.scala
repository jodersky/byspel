package byspel

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.model.{MediaTypes, StatusCodes, Uri}
import app.HttpApi
import scalatags.Text.all._

trait Ui extends HttpApi { self: Service with Tables =>

  // allows using scalatags templates as HTTP responses
  implicit val tagMarshaller: ToEntityMarshaller[Tag] = {
    Marshaller.stringMarshaller(MediaTypes.`text/html`).compose { (tag: Tag) =>
      tag.render
    }
  }

  def page(content: Tag*) = html(
    scalatags.Text.all.head(
      link(
        rel := "stylesheet",
        `type` := "text/css",
        href := "/assets/normalize.css"
      ),
      link(
        rel := "stylesheet",
        `type` := "text/css",
        href := "/assets/main.css"
      )
    ),
    body(
      content
    )
  )

  def loginForm(alert: Option[String]) = page(
    img(src := "/assets/logo.svg"),
    h3("Sign in to crashbox"),
    alert match {
      case Some(message) => div(`class` := "alert")(message)
      case None          => span()
    },
    form(action := "/login", attr("method") := "post")(
      label(`for` := "username")("Username or email address"),
      input(`type` := "text", placeholder := "", name := "username", required),
      label(`for` := "password")("Password"),
      input(`type` := "password",
            placeholder := "",
            name := "password",
            required),
      button(`type` := "submit")("Sign in")
    )
  )

  def mainPage(user: UsersRow) = page(
    h1(s"Welcome ${user.fullName.getOrElse("")}!"),
    form(action := "/logout", attr("method") := "post")(
      button(`type` := "submit")("Sign out")
    )
  )

  def authenticated(inner: UsersRow => Route): Route =
    optionalCookie("session") {
      case Some(sessionCookie) =>
        onSuccess(self.checkSession(sessionCookie.value)) {
          case Some(user) =>
            inner(user)
          case None => complete(StatusCodes.NotFound)
        }
      case None => complete(StatusCodes.NotFound)
    }

  def route =
    pathPrefix("assets") {
      getFromResourceDirectory("assets")
    } ~ path("login") {
      get {
        complete(loginForm(None))
      } ~
        post {
          formFields("username", "password") {
            case (u, p) =>
              onSuccess(self.login(u, p)) {
                case None =>
                  complete(StatusCodes.NotFound -> loginForm(
                    Some("Incorrect username or password.")))
                case Some((user, session)) =>
                  setCookie(HttpCookie("session", session.sessionId)) {
                    redirect(Uri(s"/${user.primaryEmail}"), StatusCodes.Found)
                  }
              }
          }
        }
    } ~ path("logout") {
      post {
        cookie("session") { cookiePair =>
          onSuccess(endSession(cookiePair.value)) { _ =>
            deleteCookie(cookiePair.name) {
              redirect(Uri("/"), StatusCodes.Found)
            }
          }
        }
      }
    } ~ path(Segment) { userEmail =>
      authenticated { user =>
        if (user.primaryEmail == userEmail) {
          get {
            complete(mainPage(user))
          }
        } else {
          complete(StatusCodes.NotFound)
        }
      }
    } ~ get {
      redirect(Uri("/login"), StatusCodes.Found)
    }

}
