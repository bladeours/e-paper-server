package service

import com.google.inject.ImplementedBy
import com.microsoft.playwright.{Browser, BrowserType, Page, Playwright}
import play.api.{Configuration, Logging}
import utils.ImageUtils.pngToRaw2BitBitmap

import java.nio.file.{Files, Paths}
import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CalendarServiceImpl])
trait CalendarService {
  def getCalendar(htmlFile: String)(implicit ec: ExecutionContext): Future[(Array[Byte], Array[Byte])]
}

@Singleton
class CalendarServiceImpl @Inject()(val config: Configuration, val iCalService: ICalService) extends CalendarService with Logging {
  override def getCalendar(htmlFile: String)(implicit ec: ExecutionContext): Future[(Array[Byte], Array[Byte])] = Future {
    val customPlaywright = CustomPlaywrightPage.preparePage()
    try {
      val page = customPlaywright.page
      page.navigate("http://localhost:9000/calendar/html")

      page.waitForFunction(
        """
      () => {
        const images = document.querySelectorAll('#calendar img');
        return Array.from(images).every(img => img.complete);
      }
      """
      )

      logger.debug("Saving screenshot to debug.png")
      val calendarDiv = page.querySelector("#calendar")
      val screenshotBytes: Array[Byte] = calendarDiv.screenshot()
      Files.write(Paths.get("debug.png"), screenshotBytes)

      logger.debug("Converting screenshot to 2-bit bitmap")
      pngToRaw2BitBitmap(screenshotBytes, 800, 480)

    } finally {
      customPlaywright.close()
    }
  }


  case class CustomPlaywrightPage(page: Page, playwright: Playwright, browser: Browser) {
    def close(): Unit = {
      page.close()
      browser.close()
      playwright.close()
    }
  }

  private object CustomPlaywrightPage {
    def preparePage(): CustomPlaywrightPage = {
      val playwright = Playwright.create()
      val browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions()
          .setHeadless(true)
          .setSlowMo(50)
      )
      val context = browser.newContext(new Browser.NewContextOptions().setLocale("en-US"))
      val page = context.newPage()
      CustomPlaywrightPage(page, playwright, browser)
    }
  }
}
