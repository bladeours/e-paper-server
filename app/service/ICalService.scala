package service

import biweekly.Biweekly
import com.google.inject.ImplementedBy
import org.apache.pekko.util.ccompat.JavaConverters.ListHasAsScala
import play.api.libs.json.{Json, Writes}
import play.api.{Configuration, Logging}

import java.net.URI
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.{Date, TimeZone}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[ICalServiceImpl])
trait ICalService {
  def fetchEvents()(implicit ec: ExecutionContext): String
}

@Singleton
class ICalServiceImpl @Inject()(val config: Configuration) extends ICalService with Logging {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
  private val zoneId = config.getOptional[String]("zoneId").map(z => ZoneId.of(z)).getOrElse(ZoneId.systemDefault())

  case class Event(title: String, start: String, end: String, allDay: Boolean)

  implicit val eventWrites: Writes[Event] = Json.writes[Event]
  private val now = LocalDateTime.now()
  private val startFilter: LocalDateTime = now.minusMonths(1).withDayOfMonth(1)
  private val endFilter: LocalDateTime = now.plusMonths(1).withDayOfMonth(now.plusMonths(1).getDayOfMonth)

  override def fetchEvents()(implicit ec: ExecutionContext): String = {
    val servers = config.get[Seq[Configuration]]("calendars")

    val allEvents: Seq[Event] = servers.flatMap { config =>

      val icsUrl = config.get[String]("url")
      val tag = config.get[String]("tag")
      logger.debug(s"fetching events from $icsUrl")
      val stream = new URI(icsUrl).toURL.openStream()
      val ical = Biweekly.parse(stream).first

      ical.getEvents.asScala
        .flatMap { event =>

          val iterator = event.getDateIterator(TimeZone.getTimeZone(zoneId))
          iterator.advanceTo(Date.from(Instant.now().minus(7, ChronoUnit.DAYS)))

          val startDateTime = (if iterator.hasNext then iterator.next() else event.getDateStart.getValue).toInstant.atZone(zoneId).toLocalDateTime
          val endDateTime = event.getDateEnd.getValue.toInstant.atZone(zoneId).toLocalDateTime
          val title = tag + event.getSummary.getValue
          val start = startDateTime.format(formatter)
          val end = endDateTime.format(formatter)
          val allDay = (startDateTime.getHour == 0 && startDateTime.getMinute == 0)

          Some(Event(title, start, end, allDay))
        }
    }
    logger.debug(s"found ${allEvents.size} events")
    Json.stringify(Json.toJson(allEvents))
  }

  private def dateFilter(date: Instant): Boolean = date.atZone(zoneId).toLocalDateTime.isAfter(startFilter) &&
    date.atZone(zoneId).toLocalDateTime.isBefore(endFilter)
}
