package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Chapter
import com.example.data.model.Manga
import com.example.data.repository.MangaRepository
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors

@Composable
fun DetailsScreen(
  manga: Manga,
  repository: MangaRepository,
  onBack: () -> Unit,
  onReadChapter: (Chapter) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)
  val colors = LocalMangaColors.current
  val context = LocalContext.current

  var chapters by remember { mutableStateOf<List<Chapter>>(emptyList()) }
  var isLoadingChapters by remember { mutableStateOf(true) }
  var isDescriptionExpanded by remember { mutableStateOf(false) }
  var isSortDescending by remember { mutableStateOf(true) }
  var selectedTab by remember { mutableStateOf(0) } // 0: الفصول, 1: التفاصيل, 2: التعليقات

  LaunchedEffect(manga.id) {
    isLoadingChapters = true
    chapters = repository.getChapters(manga.id)
    isLoadingChapters = false
  }

  val sortedChapters = remember(chapters, isSortDescending) {
    if (isSortDescending) chapters else chapters.reversed()
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // Top Bar with Back, Title, Share, Bookmark
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "رجوع",
            tint = colors.textPrimary
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = manga.title,
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "اقرأ مانجا ${manga.title} على Manga XW!")
            type = "text/plain"
          }
          context.startActivity(Intent.createChooser(sendIntent, "مشاركة"))
        }) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "مشاركة",
            tint = colors.textPrimary
          )
        }
        IconButton(onClick = { repository.toggleFavorite(manga) }) {
          Icon(
            imageVector = if (manga.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "حفظ",
            tint = if (manga.isFavorite) colors.primary else colors.textPrimary
          )
        }
      }
    }

    // Hero Section: Cover Image + Title + Rating + Badges
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Glowing Manga Cover Art
        NeonCard(
          modifier = Modifier
            .width(135.dp)
            .aspectRatio(0.72f),
          shape = RoundedCornerShape(16.dp),
          hasGlow = true
        ) {
          if (manga.coverUrl.isNotBlank()) {
            AsyncImage(
              model = manga.coverUrl,
              contentDescription = manga.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(colors.card),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = manga.title.take(2).uppercase(),
                color = colors.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Metadata on right side
        Column(modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(colors.primary.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "سلسلة • ${manga.status.labelAr}",
              color = colors.primary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = manga.title,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = Color(0xFFFFD600),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = String.format("%.1f", manga.rating ?: 4.8f),
              color = colors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "• ${manga.sourceName}",
              color = colors.textSecondary,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Genre tags chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            manga.genres.forEach { genre ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(colors.card)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = genre,
                  color = colors.textSecondary,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }
    }

    // Synopsis / Description with Expand button
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = manga.description.ifBlank { "لا يتوفر وصف لهذا العمل حالياً." },
          color = colors.textSecondary,
          fontSize = 13.sp,
          lineHeight = 20.sp,
          maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          modifier = Modifier
            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isDescriptionExpanded) "عرض أقل" else "قراءة المزيد",
            color = colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Icon(
            imageVector = if (isDescriptionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // Big Glowing Action CTA: "متابعة القراءة / ابدأ القراءة"
    item {
      val firstOrLatestChapter = sortedChapters.firstOrNull()
      val ctaText = if (manga.lastReadChapterTitle != null) {
        "متابعة القراءة (${manga.lastReadChapterTitle})"
      } else {
        "ابدأ القراءة الآن"
      }

      NeonCard(
        modifier = Modifier
          .testTag("start_reading_cta")
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        hasGlow = true,
        backgroundColor = colors.surface,
        onClick = {
          firstOrLatestChapter?.let { onReadChapter(it) }
        }
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  colors.primary.copy(alpha = 0.2f),
                  colors.card
                )
              )
            )
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(colors.primary, colors.secondary)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (colors.isDark) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = ctaText,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "الفصل ${firstOrLatestChapter?.number ?: 1} • جودة عالية",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }

            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = colors.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // Metadata Details Card (المؤلف / الرسام / الحالة / آخر تحديث)
    item {
      NeonCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        backgroundColor = colors.card
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "المؤلف", color = colors.textTertiary, fontSize = 11.sp)
            Text(text = manga.author, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "الرسام", color = colors.textTertiary, fontSize = 11.sp)
            Text(text = manga.artist, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "الحالة", color = colors.textTertiary, fontSize = 11.sp)
            Text(text = manga.status.labelAr, color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "آخر تحديث", color = colors.textTertiary, fontSize = 11.sp)
            Text(text = manga.lastUpdate.ifBlank { "مؤخراً" }, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Tabs Header (الفصول | التفاصيل | التعليقات)
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        listOf("الفصول", "التفاصيل", "التعليقات").forEachIndexed { idx, tabTitle ->
          val isSelected = selectedTab == idx
          Column(
            modifier = Modifier
              .clickable { selectedTab = idx }
              .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = tabTitle,
              color = if (isSelected) colors.primary else colors.textSecondary,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 14.sp
            )
            if (isSelected) {
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .width(28.dp)
                  .height(3.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(colors.primary)
              )
            }
          }
        }
      }
      HorizontalDivider(color = colors.border, thickness = 1.dp)
    }

    // Chapters Header with Sort & Count
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${sortedChapters.size} فصل",
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.card)
            .clickable { isSortDescending = !isSortDescending }
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Sort,
            contentDescription = "ترتيب",
            tint = colors.primary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isSortDescending) "الأحدث أولاً" else "الأقدم أولاً",
            color = colors.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Chapters List items
    if (isLoadingChapters) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = colors.primary)
        }
      }
    } else {
      items(sortedChapters) { chapter ->
        ChapterItemRow(
          chapter = chapter,
          onClick = { onReadChapter(chapter) },
          onDownloadClick = { /* download chapter */ }
        )
      }
    }
  }
}

@Composable
private fun ChapterItemRow(
  chapter: Chapter,
  onClick: () -> Unit,
  onDownloadClick: () -> Unit
) {
  val colors = LocalMangaColors.current

  Box(
    modifier = Modifier
      .testTag("chapter_item_${chapter.id}")
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Read status indicator dot/icon
      if (chapter.isReading) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(colors.primary)
        )
      } else if (chapter.isRead) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "تمت القراءة",
          tint = colors.primary,
          modifier = Modifier.size(16.dp)
        )
      } else {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(colors.textTertiary)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = chapter.title,
          color = if (chapter.isRead) colors.textSecondary else colors.textPrimary,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${chapter.releaseDate.ifBlank { "مؤخراً" }} • ${chapter.pageCount} صفحة",
          color = colors.textTertiary,
          fontSize = 10.sp
        )
      }

      // Download Button
      IconButton(onClick = onDownloadClick, modifier = Modifier.size(36.dp)) {
        Icon(
          imageVector = Icons.Default.Download,
          contentDescription = "تنزيل",
          tint = colors.textSecondary,
          modifier = Modifier.size(18.dp)
        )
      }

      // More
      IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "خيارات",
          tint = colors.textTertiary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
