object TabExpander {
  def expandTabs(input: String, tabStop: Int): String = {
    require(tabStop > 0, "Tab stop must be a positive integer")

    input.flatMap {
      case '\t' => " " * tabStop // Replace each tab with the appropriate number of spaces
      case other => other.toString // Keep other characters unchanged
    }
  }

  def main(args: Array[String]): Unit = {
    val textWithTabs = "This\tis\ta\ttest"
    val tabStop = 4

    val expandedText = expandTabs(textWithTabs, tabStop)
    println(s"Original: [$textWithTabs]")
    println(s"Expanded: [$expandedText]")
  }
}

