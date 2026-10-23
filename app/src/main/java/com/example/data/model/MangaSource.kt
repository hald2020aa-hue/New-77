package com.example.data.model

data class MangaSource(
  val id: String,
  val name: String,
  val url: String,
  val status: SourceStatus = SourceStatus.WORKING,
  val isCustom: Boolean = false,
  val isEnabled: Boolean = true,
  val language: String = "Multi",
  val iconLetter: String = name.take(1).uppercase()
)

enum class SourceStatus(val labelAr: String, val labelEn: String) {
  WORKING("يعمل بشكل جيد", "Working"),
  SLOW("بطيء أحياناً", "Slow"),
  OFFLINE("غير متاح حالياً", "Offline")
}

data class Category(
  val id: String,
  val name: String,
  val count: Int = 0,
  val isCustom: Boolean = false
)
