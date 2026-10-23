package com.example.data.model

data class Chapter(
  val id: String,
  val mangaId: String,
  val number: Float,
  val title: String = "",
  val titleAr: String? = null,
  val releaseDate: String = "",
  val scanlationGroup: String = "",
  val pageCount: Int = 0,
  val isRead: Boolean = false,
  val isReading: Boolean = false,
  val readProgressPercent: Int = 0,
  val lastReadPageIndex: Int = 0,
  val isDownloaded: Boolean = false,
  val downloadSizeMb: Float = 0f,
  val pageUrls: List<String> = emptyList()
)
