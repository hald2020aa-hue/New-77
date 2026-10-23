package com.example.data.api

import com.example.data.model.Chapter
import com.example.data.model.Manga
import com.example.data.model.MangaStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MangaDexService {
  private val client = OkHttpClient.Builder()
    .connectTimeout(12, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

  private val baseUrl = "https://api.mangadex.org"
  private val coverBaseUrl = "https://uploads.mangadex.org/covers"

  suspend fun getPopularManga(limit: Int = 20): Result<List<Manga>> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/manga?limit=$limit&includes[]=cover_art&order[followedCount]=desc&contentRating[]=safe&contentRating[]=suggestive"
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP error ${response.code}"))
      }
      val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
      val list = parseMangaList(body)
      Result.success(list)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun searchManga(query: String, limit: Int = 20): Result<List<Manga>> = withContext(Dispatchers.IO) {
    try {
      val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
      val url = "$baseUrl/manga?title=$encodedQuery&limit=$limit&includes[]=cover_art&contentRating[]=safe&contentRating[]=suggestive"
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP error ${response.code}"))
      }
      val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
      val list = parseMangaList(body)
      Result.success(list)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun getChapters(mangaId: String): Result<List<Chapter>> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/manga/$mangaId/feed?limit=100&translatedLanguage[]=en&translatedLanguage[]=ar&order[chapter]=desc"
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP error ${response.code}"))
      }
      val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
      val json = JSONObject(body)
      val dataArr = json.optJSONArray("data") ?: JSONArray()
      val chapters = mutableListOf<Chapter>()

      for (i in 0 until dataArr.length()) {
        val chObj = dataArr.getJSONObject(i)
        val id = chObj.optString("id")
        val attrs = chObj.optJSONObject("attributes") ?: JSONObject()
        val chapterNumStr = attrs.optString("chapter", "0")
        val chapterNum = chapterNumStr.toFloatOrNull() ?: (i + 1).toFloat()
        val title = attrs.optString("title", "").ifBlank { "الفصل $chapterNumStr" }
        val publishAt = attrs.optString("publishAt", "").take(10)
        val pagesCount = attrs.optInt("pages", 15)

        chapters.add(
          Chapter(
            id = id,
            mangaId = mangaId,
            number = chapterNum,
            title = title,
            releaseDate = publishAt,
            pageCount = pagesCount
          )
        )
      }
      Result.success(chapters)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun getChapterPages(chapterId: String): Result<List<String>> = withContext(Dispatchers.IO) {
    try {
      val url = "$baseUrl/at-home/server/$chapterId"
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        return@withContext Result.failure(Exception("HTTP error ${response.code}"))
      }
      val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
      val json = JSONObject(body)
      val hostBase = json.optString("baseUrl")
      val chapterData = json.optJSONObject("chapter") ?: JSONObject()
      val hash = chapterData.optString("hash")
      val dataArr = chapterData.optJSONArray("data") ?: JSONArray()

      val pageUrls = mutableListOf<String>()
      for (i in 0 until dataArr.length()) {
        val fileName = dataArr.getString(i)
        pageUrls.add("$hostBase/data/$hash/$fileName")
      }
      Result.success(pageUrls)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun parseMangaList(jsonStr: String): List<Manga> {
    val json = JSONObject(jsonStr)
    val dataArr = json.optJSONArray("data") ?: return emptyList()
    val list = mutableListOf<Manga>()

    for (i in 0 until dataArr.length()) {
      val item = dataArr.getJSONObject(i)
      val id = item.optString("id")
      val attrs = item.optJSONObject("attributes") ?: JSONObject()
      val titleObj = attrs.optJSONObject("title") ?: JSONObject()
      val title = titleObj.optString("en", "")
        .ifBlank { titleObj.optString("ja-ro", "") }
        .ifBlank { titleObj.optString("ja", "عنوان المانجا") }

      val descObj = attrs.optJSONObject("description") ?: JSONObject()
      val desc = descObj.optString("en", "").ifBlank { descObj.optString("ar", "لا يتوفر وصف لهذا العمل.") }

      val statusStr = attrs.optString("status", "ongoing")
      val status = when (statusStr.lowercase()) {
        "completed" -> MangaStatus.COMPLETED
        "hiatus" -> MangaStatus.HIATUS
        "cancelled" -> MangaStatus.CANCELLED
        else -> MangaStatus.ONGOING
      }

      val tagsArr = attrs.optJSONArray("tags") ?: JSONArray()
      val genres = mutableListOf<String>()
      for (t in 0 until tagsArr.length()) {
        val tagObj = tagsArr.getJSONObject(t).optJSONObject("attributes") ?: JSONObject()
        val nameObj = tagObj.optJSONObject("name") ?: JSONObject()
        val nameEn = nameObj.optString("en", "")
        if (nameEn.isNotBlank() && genres.size < 5) {
          genres.add(translateTagToAr(nameEn))
        }
      }

      // Cover Art filename from relationships
      var coverFileName = ""
      val relArr = item.optJSONArray("relationships") ?: JSONArray()
      for (r in 0 until relArr.length()) {
        val rel = relArr.getJSONObject(r)
        if (rel.optString("type") == "cover_art") {
          val relAttrs = rel.optJSONObject("attributes")
          if (relAttrs != null) {
            coverFileName = relAttrs.optString("fileName", "")
          }
        }
      }

      val coverUrl = if (coverFileName.isNotBlank()) {
        "$coverBaseUrl/$id/$coverFileName.256.jpg"
      } else {
        ""
      }

      val lastUpdate = attrs.optString("updatedAt", "").take(10)

      list.add(
        Manga(
          id = id,
          title = title,
          description = desc,
          coverUrl = coverUrl,
          status = status,
          genres = genres.ifEmpty { listOf("أكشن", "فانتازيا") },
          lastUpdate = lastUpdate,
          rating = 4.8f,
          sourceId = "mangadex",
          sourceName = "MangaDex"
        )
      )
    }
    return list
  }

  private fun translateTagToAr(tagEn: String): String {
    return when (tagEn.lowercase()) {
      "action" -> "أكشن"
      "adventure" -> "مغامرة"
      "fantasy" -> "فانتازيا"
      "supernatural" -> "خارق للطبيعة"
      "mystery" -> "غموض"
      "horror" -> "رعب"
      "comedy" -> "كوميديا"
      "drama" -> "دراما"
      "romance" -> "رومانسي"
      "sci-fi" -> "خيال علمي"
      "isekai" -> "إيسيكاي"
      "shounen" -> "شونين"
      "seinen" -> "سينين"
      "magic" -> "سحر"
      "martial arts" -> "فنون قتالية"
      "reincarnation" -> "إعادة تجسيد"
      else -> tagEn
    }
  }
}
