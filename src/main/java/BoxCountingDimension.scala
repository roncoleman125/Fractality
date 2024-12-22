import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object BoxCountingDimension {
  def main(args: Array[String]): Unit = {
    // Path to the input PNG file
    val inputPngPath = args(0)

    // Load the image
    val image = ImageIO.read(new File(inputPngPath))

    // Ensure the image is in grayscale
    val grayscaleImage = convertToGrayscale(image)

    // Calculate the box-counting dimension
    val dimension = calculateBoxCountingDimension(grayscaleImage)

    println(f"$inputPngPath estimated Box-Counting Dimension: $dimension%.4f")
  }

  // Function to convert an image to grayscale
  def convertToGrayscale(image: BufferedImage): BufferedImage = {
    val grayscaleImage = new BufferedImage(image.getWidth, image.getHeight, BufferedImage.TYPE_BYTE_GRAY)
    val g = grayscaleImage.createGraphics()
    g.drawImage(image, 0, 0, null)
    g.dispose()
    grayscaleImage
  }

  // Function to calculate the box-counting dimension
  def calculateBoxCountingDimension(image: BufferedImage): Double = {
    val width = image.getWidth
    val height = image.getHeight

    // Function to count non-empty boxes for a given box size
    def countNonEmptyBoxes(boxSize: Int): Int = {
      var count = 0
      for (x <- 0 until width by boxSize; y <- 0 until height by boxSize) {
        if (isBoxNonEmpty(image, x, y, boxSize)) {
          count += 1
        }
      }
      count
    }

    // Function to check if a box contains any non-white pixel
    def isBoxNonEmpty(image: BufferedImage, startX: Int, startY: Int, boxSize: Int): Boolean = {
      val endX = math.min(startX + boxSize, width)
      val endY = math.min(startY + boxSize, height)
      for (x <- startX until endX; y <- startY until endY) {
        val pixel = image.getRGB(x, y) & 0xFF // Extract the grayscale value
        if (pixel < 255) return true // Non-white pixel found
      }
      false
    }

    // Calculate the box counts for multiple scales
//    val scales = (1 to math.min(width, height)).filter(s => (width % s == 0) && (height % s == 0))
//    val scales = Array(2,4,8,16,32,64,128,256,512)
    val scales = Array(2, 3, 4, 6, 8, 12, 16, 32, 64, 128)
    val counts = scales.map(s => (s, countNonEmptyBoxes(s)))

    // Log-log regression to estimate the fractal dimension
    val logScales = counts.map { case (scale, _) => -math.log(1.0 / scale) }
    val logCounts = counts.map { case (_, count) => math.log(count) }
    val meanLogScale = logScales.sum / logScales.size
    val meanLogCount = logCounts.sum / logCounts.size

    val numerator = logScales.zip(logCounts).map { case (x, y) => (x - meanLogScale) * (y - meanLogCount) }.sum
    val denominator = logScales.map(x => math.pow(x - meanLogScale, 2)).sum

    -numerator / denominator // The negative slope is the box-counting dimension
  }
}

