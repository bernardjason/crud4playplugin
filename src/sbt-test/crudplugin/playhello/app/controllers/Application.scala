package controllers

import java.sql.Timestamp

import javax.inject._
import org.bjason.request.tables.{RequestModel, RequestTableOperation}
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.format.Formats._
import play.api.data.validation._
import play.api.libs.json.Json
import play.api.Logger

import scala.concurrent.ExecutionContext

class Application @Inject()( repo: RequestTableOperation,
                             cc: MessagesControllerComponents
                           )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val timestamp: Mapping[Timestamp] = of[Timestamp]
  def nonEmptyTimeStamp: Constraint[Timestamp] = nonEmpty()
  def nonEmpty(errorMessage: String = "error.required"): Constraint[Timestamp] = Constraint[Timestamp]("constraint.required") { o =>
    if (o == null) Invalid(ValidationError(errorMessage)) else Valid
  }

  val requestForm: Form[RequestModel] = Form {
    mapping(
    "request_id" -> default(longNumber, -1L ) ,
    "request_name" -> text.verifying(Constraints.nonEmpty),
    "request_desc" -> nonEmptyText,
    "request_start_date" -> optional(timestamp),
    "request_required_for_date" -> optional(timestamp)
    )(RequestModel.apply)(RequestModel.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.index(requestForm))
  }
}

