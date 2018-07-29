package controllers

import java.sql.Timestamp

import javax.inject._
import org.bjason.blog.blog.tables.{BlogModel, BlogTableOperation}
import org.bjason.blog.user.tables.{UserModel, UserTableOperation}
import play.api.Logger
import play.api.cache.SyncCacheApi
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation._
import play.api.data.{Form, Mapping}
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.ExecutionContext

class Application @Inject()(blog: BlogTableOperation, user: UserTableOperation, cc: MessagesControllerComponents
                           )(implicit ec: ExecutionContext, cache: SyncCacheApi)
  extends MessagesAbstractController(cc) {

  val timestamp: Mapping[Timestamp] = of[Timestamp]

  def nonEmptyTimeStamp: Constraint[Timestamp] = nonEmpty()

  def nonEmpty(errorMessage: String = "error.required"): Constraint[Timestamp] = Constraint[Timestamp]("constraint.required") { o =>
    if (o == null) Invalid(ValidationError(errorMessage)) else Valid
  }

  val userForm: Form[UserModel] = Form {
    mapping(
      "user_id" -> default(longNumber, -1L),
      "user_name" -> nonEmptyText,
      "user_password" -> nonEmptyText,
      "user_handle" -> nonEmptyText
    )(UserModel.apply)(UserModel.unapply)
  }
  val blogForm: Form[BlogModel] = Form {
    mapping(
      "blog_id" -> default(longNumber, -1L),
      "blog_user_id" -> default(longNumber, -1L),
      "blog_user_handle" -> nonEmptyText,
      "blog_created" -> timestamp,
      "blog_text" -> nonEmptyText
    )(BlogModel.apply)(BlogModel.unapply)
  }

  def index = Action { implicit request =>
    getAuth.map { userModel =>
      Ok(views.html.index(userForm, blogForm,Some(userModel)))
    }.getOrElse {
      Ok(views.html.index(userForm, blogForm,None))
    }
  }

  def logout = Action { implicit request =>
    request.session.get("user").map { u =>
      Logger.info("Removed session "+u)
      cache.remove(u);
    }
    Redirect(routes.Application.index()).withNewSession
  }

  def getAuth(implicit request: play.api.mvc.Request[play.api.mvc.AnyContent]): Option[UserModel] = {
    request.session.get("user").map { u =>
      Logger.info(s"session user is ${u}")
      return cache.get[UserModel](u)
    }
    None
  }


  case class UserPassword(user_name: String, user_password: String)

  implicit val userPasswordReads = Json.reads[UserPassword]

  def login = Action.async(parse.json) { implicit request =>
    val userPassword = request.body.as[UserPassword]
    var id: Option[String] = None
    user.list(None,None).map { rows =>
      rows.filter { r => r.user_name == userPassword.user_name && r.user_password == userPassword.user_password }.map { r =>
        id = Some(java.util.UUID.randomUUID().toString)
        cache.set(id.get, r)

        Logger.info(s"login is [${id}] and db is ${r.user_name} ${r.user_handle}")
      }
      if (id.isEmpty) {
        BadRequest.withNewSession
      } else {
        Redirect(routes.Application.index()).withSession("user" -> id.get)
      }
    }
  }
}

