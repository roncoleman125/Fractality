import java.awt.image.BufferedImage
import java.awt.{Font, Graphics2D, Color}
import java.io.File
import javax.imageio.ImageIO
import scala.io.Source

object TextToPng {
  val INSET = 10
  def main(args: Array[String]): Unit = {
    // Path to the source text file
    val sourceFilePath = args(0)

    // Path to the output PNG file
    val outputPngPath = "c:/tmp/" + sourceFilePath.map { c => if(c == '.') '_' else c} + ".png"

    // Read text from the source file
    val lines = Source.fromFile(sourceFilePath).getLines().toSeq.map { line => TabExpander.expandTabs(line,4)}

    // Define image dimensions and font settings
    val font = new Font("Courier", Font.PLAIN, 11)
    val metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics().getFontMetrics(font)
    //val metrics = g.getFontMetrics(font)

    val height = metrics.getHeight() + 3

    // Get the graphics context for the BufferedImage

    val imageWidth = lines.foldLeft(0) { (biggest, line) => Math.max(biggest, metrics.stringWidth(line)) } + 2 * INSET
    val imageHeight = height * (lines.length + 1) + 2 * INSET // +1 because anchor is UL corner

    // Create a BufferedImage
    val bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)

    val g: Graphics2D = bufferedImage.createGraphics()

    // Set background color
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, imageWidth, imageHeight)

    // Set text color and font
    g.setColor(Color.BLACK)
    g.setFont(font)

    // Draw the text
    var y = height+INSET

    for (line <- lines) {
      g.drawString(line, INSET, y)
      y += height
      if (y > imageHeight) {
        println("Warning: Text exceeds image height. Some lines may be truncated.")
        y = imageHeight // Prevent overflow
      }
    }

    // Dispose of the graphics context
    g.dispose()

    // Save the BufferedImage as a PNG file
    val outputFile = new File(outputPngPath)
    ImageIO.write(bufferedImage, "png", outputFile)

    println(s"$sourceFilePath saved as $outputPngPath")
  }
}

