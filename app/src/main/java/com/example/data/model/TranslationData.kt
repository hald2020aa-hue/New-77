package com.example.data.model

data class TextBubble(
  val id: String,
  val pageIndex: Int,
  val originalText: String,
  val translatedAr: String,
  val xPercent: Float, // 0.0 to 1.0 on page
  val yPercent: Float,
  val widthPercent: Float,
  val heightPercent: Float,
  val detectedLang: String = "ja" // ja, ko, zh, en
)

enum class ReadingMode(val labelAr: String, val labelEn: String) {
  VERTICAL("عمودي", "Vertical Webtoon"),
  PAGED("صفحة", "Paged")
}

data class ReaderSettings(
  val readingMode: ReadingMode = ReadingMode.VERTICAL,
  val brightness: Float = 1.0f,
  val autoBrightness: Boolean = false,
  val fontSizeMultiplier: Float = 1.0f,
  val showTranslationOverlay: Boolean = false,
  val showDualText: Boolean = false, // Arabic + Original
  val translationBubbleOpacity: Float = 0.85f,
  val autoTranslateToAr: Boolean = true,
  val immersiveMode: Boolean = false
)
