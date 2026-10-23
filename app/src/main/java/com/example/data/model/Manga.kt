package com.example.data.model

data class Manga(
  val id: String,
  val title: String,
  val titleAr: String? = null,
  val altTitle: String? = null,
  val description: String = "",
  val descriptionAr: String? = null,
  val coverUrl: String = "",
  val bannerUrl: String? = null,
  val rating: Float? = null,
  val viewCount: String? = null,
  val status: MangaStatus = MangaStatus.ONGOING,
  val genres: List<String> = emptyList(),
  val author: String = "غير محدد",
  val artist: String = "غير محدد",
  val lastUpdate: String = "",
  val sourceId: String = "mangadex",
  val sourceName: String = "MangaDex",
  val totalChapters: Int = 0,
  val readChaptersCount: Int = 0,
  val lastReadChapterNumber: Float? = null,
  val lastReadChapterTitle: String? = null,
  val lastReadProgressPercent: Int = 0,
  val inLibrary: Boolean = false,
  val isFavorite: Boolean = false,
  val categoryId: String = "all",
  val isNewRelease: Boolean = false
)

enum class MangaStatus(val labelAr: String, val labelEn: String) {
  ONGOING("مستمر", "Ongoing"),
  COMPLETED("مكتمل", "Completed"),
  HIATUS("متوقف مؤقتاً", "Hiatus"),
  CANCELLED("ملغي", "Cancelled")
}
