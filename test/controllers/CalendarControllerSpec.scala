package controllers

import org.apache.pekko.util.ByteString
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.*
import play.api.mvc.*
import play.api.test.*
import play.api.test.Helpers.*
import service.{CalendarService, ICalService}

import scala.concurrent.{ExecutionContextExecutor, Future}

class CalendarControllerSpec
  extends PlaySpec
    with MockitoSugar
    with ScalaFutures
    with Results {

  val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  "CalendarController#getCalendarWeek" should {

    "return Ok with combined bytes when service succeeds" in {
      val mockService = mock[CalendarService]
      val mockIcalService = mock[ICalService]

      when(mockService.getCalendar(any[String])(using ArgumentMatchers.eq(ec)))
        .thenReturn(Future.successful((Array[Byte](1, 2), Array[Byte](3, 4))))

      val controller = new CalendarController(Helpers.stubControllerComponents(), mockService, mockIcalService, null)

      val result = controller.getCalendarWeek.apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/octet-stream")
      contentAsBytes(result) mustBe ByteString(1, 2, 3, 4)
    }

    "return InternalServerError with JSON when service fails" in {
      val mockService = mock[CalendarService]
      val mockIcalService = mock[ICalService]
      when(mockService.getCalendar(any[String])(using ArgumentMatchers.eq(ec)))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val controller = new CalendarController(Helpers.stubControllerComponents(), mockService, mockIcalService, null)

      val result = controller.getCalendarWeek.apply(FakeRequest())

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      (contentAsJson(result) \ "status").as[String] mustBe "error"
      (contentAsJson(result) \ "message").as[String] must include("boom")
    }
  }
}
