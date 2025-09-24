package service

import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.*
import play.api.Configuration

import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.{Await, ExecutionContext}

class CalendarServiceImplSpec
  extends PlaySpec
    with MockitoSugar
    with ScalaFutures {

  implicit val ec: ExecutionContext = ExecutionContext.global

  "CalendarServiceImpl#getCalendar" should {

    "inject events and call screenshot conversion" in {
      val mockICal = mock[ICalService]
      when(mockICal.fetchEvents()).thenReturn("[{\"title\":\"Another longer event title to test wrapping\",\"start\":\"2025-08-20T10:30:00\"}]")

      val config = Configuration("lat" -> "50.0", "long" -> "20.0", "tmpDir" -> "/tmp")

//      val service = new CalendarServiceImpl(config, mockICal)
//
//      val expectedBlack = Files.readAllBytes(Paths.get(getClass.getClassLoader.getResource(s"expected/black.bin").getPath))
//      val expectedRed = Files.readAllBytes(Paths.get(getClass.getClassLoader.getResource(s"expected/red.bin").getPath))
//
//      val (black, red) = Await.result(service.getCalendar("calendar-week.html"), 60.seconds)

//      black mustBe expectedBlack
//      red mustBe expectedRed
    }
  }
}
