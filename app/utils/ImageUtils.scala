package utils

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object ImageUtils {

  def pngToRaw2BitBitmap(imageBytes: Array[Byte], width: Int, height: Int): (Array[Byte], Array[Byte]) = {
    implicit val img: BufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes))
    val black = new Array[Byte](width * height / 8)
    val red = new Array[Byte](width * height / 8)

    for (y <- 0 until height; x <- 0 until width) {
      val (r, g, b) = getRGB(x, y)

      val idx = (y * width + x) / 8
      val shift = 7 - ((y * width + x) % 8)

      val isRed = r > 228 && g < 50 && b < 50
      val isWhite = r >= 180 && g >= 180 && b >= 180
      if (isBlackish(x,y) && !isWhite && !isRed) {
        black(idx) = (black(idx) | (1 << shift)).toByte

      } else if (!isWhite) {
        red(idx) = (red(idx) | (1 << shift)).toByte
      }
      // white → do nothing

    }

    (black, red)
  }

  private def isBlackish(x: Int, y: Int)(implicit image: BufferedImage): Boolean = {
    val height = 480
    val width = 800

    def isInBoundaries(x1: Int, y1: Int): Boolean = x1 >= 0 && x1 < width && y1 >= 0 && y1 < height

    val rgb = getRGB(x, y)
    if (isDefinitelyBlack(rgb)) return true
    else if (isInBoundaries(x - 1, y + 1) && isDefinitelyBlack(getRGB(x - 1, y + 1))) return true
    else if (isInBoundaries(x, y + 1) && isDefinitelyBlack(getRGB(x, y + 1))) return true
    else if (isInBoundaries(x + 1, y + 1) && isDefinitelyBlack(getRGB(x + 1, y + 1))) return true
    else if (isInBoundaries(x - 1, y) && isDefinitelyBlack(getRGB(x - 1, y))) return true
    else if (isInBoundaries(x + 1, y) && isDefinitelyBlack(getRGB(x + 1, y))) return true
    else if (isInBoundaries(x - 1, y - 1) && isDefinitelyBlack(getRGB(x - 1, y - 1))) return true
    else if (isInBoundaries(x, y - 1) && isDefinitelyBlack(getRGB(x, y - 1))) return true
    else if (isInBoundaries(x + 1, y - 1) && isDefinitelyBlack(getRGB(x + 1, y - 1))) return true
    false
  }


  private def isDefinitelyBlack(tuple: (Int, Int, Int))(implicit image: BufferedImage): Boolean = tuple match {
    case (r, g, b) => r < 100 && g < 100 && b < 100
  }


  private def getRGB(x: Int, y: Int)(implicit image: BufferedImage): (Int, Int, Int) = {
    val rgb = image.getRGB(x, y)
    val r = (rgb >> 16) & 0xFF
    val g = (rgb >> 8) & 0xFF
    val b = rgb & 0xFF
    (r, g, b)
  }
}
