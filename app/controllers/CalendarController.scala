package controllers

import play.api.*
import play.api.libs.json.Json
import play.api.mvc.*
import service.{CalendarService, ICalService}

import javax.inject.*
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class CalendarController @Inject()(val controllerComponents: ControllerComponents,
                                   val calendarService: CalendarService,
                                   val iCalService: ICalService,
                                   val config: Configuration) extends BaseController with Logging {

  def getCalendarWeek: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    logger.debug("Got request...")
    calendarService.getCalendar("calendar-week.html").map { case (blackBytes, redBytes) =>

      val combinedBytes = blackBytes ++ redBytes
      logger.debug("sending calendar in bytes")
      Ok(combinedBytes)
        .as("application/octet-stream")
    }.recover {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error(s"error while creating calendar: ${ex.getMessage}")
        InternalServerError(Json.obj(
          "status" -> "error",
          "message" -> s"Failed to fetch bitmap: ${ex.getMessage}"
        ))
    }
  }

  def calendarPage: Action[AnyContent] = Action { implicit request =>
    val long = config.get[String]("long")
    val lat = config.get[String]("lat")
    val counterStart = config.getOptional[String]("counterStart").getOrElse("1990-01-01")
    val counterEnd = config.getOptional[String]("counterEnd").getOrElse("1990-01-01")
    Ok(views.html.calendarWeek(lat, long, iCalService.fetchEvents(), counterStart, counterEnd))
  }
}
