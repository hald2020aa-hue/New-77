package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Chapter
import com.example.data.model.Manga
import com.example.data.model.ReadingMode
import com.example.data.model.TextBubble
import com.example.data.repository.MangaRepository
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors

@Composable
fun ReaderScreen(
  manga: Manga,
  chapter: Chapter,
  repository: MangaRepository,
  onBack: () -> Unit,
  onNextChapter: (() -> Unit)? = null,
  onPreviousChapter: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)
  val colors = LocalMangaColors.current

  var pages by remember { mutableStateOf<List<String>>(emptyList()) }
  var isLoadingPages by remember { mutableStateOf(true) }
  var isUiVisible by remember { mutableStateOf(true) }
  var readingMode by remember { mutableStateOf(ReadingMode.VERTICAL) }
  var showTranslationOverlay by remember { mutableStateOf(true) }
  var showDualText by remember { mutableStateOf(false) }
  var bubbleOpacity by remember { mutableFloatStateOf(0.85f) }
  var brightness by remember { mutableFloatStateOf(1.0f) }
  var showBrightnessSlider by remember { mutableStateOf(false) }
  var isBookmarked by remember { mutableStateOf(false) }

  val totalPagesCount = remember(pages) { if (pages.isNotEmpty()) pages.size else chapter.pageCount.coerceAtLeast(24) }
  var currentPageIndex by remember { mutableIntStateOf(1) }

  LaunchedEffect(chapter.id) {
    isLoadingPages = true
    val fetchedPages = repository.getChapterPages(chapter.id)
    pages = fetchedPages
    isLoadingPages = false
  }

  // Update repository reading progress
  LaunchedEffect(currentPageIndex, totalPagesCount) {
    val progressPercent = ((currentPageIndex.toFloat() / totalPagesCount.toFloat()) * 100).toInt().coerceIn(1, 100)
    repository.updateReadingProgress(
      mangaId = manga.id,
      chapterNum = chapter.number,
      chapterTitle = chapter.title,
      progressPercent = progressPercent
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = { isUiVisible = !isUiVisible }
        )
      }
  ) {
    // 1. Manga Pages Display (Vertical scroll or Paged)
    if (isLoadingPages) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = colors.primary)
          Spacer(modifier = Modifier.height(12.dp))
          Text(text = "جارِ تحميل صفحات الفصل...", color = colors.primary, fontSize = 13.sp)
        }
      }
    } else {
      if (readingMode == ReadingMode.VERTICAL) {
        val listState = rememberLazyListState()
        val firstVisible by remember { derivedStateOf { listState.firstVisibleItemIndex } }
        LaunchedEffect(firstVisible) {
          currentPageIndex = (firstVisible + 1).coerceIn(1, totalPagesCount)
        }

        LazyColumn(
          state = listState,
          modifier = Modifier.fillMaxSize()
        ) {
          items(totalPagesCount) { idx ->
            val pageUrl = pages.getOrNull(idx) ?: ""
            MangaPageView(
              pageIndex = idx,
              pageUrl = pageUrl,
              showTranslation = showTranslationOverlay,
              showDual = showDualText,
              bubbleOpacity = bubbleOpacity,
              repository = repository
            )
          }
        }
      } else {
        val pagerState = rememberPagerState(pageCount = { totalPagesCount })
        LaunchedEffect(pagerState.currentPage) {
          currentPageIndex = pagerState.currentPage + 1
        }

        HorizontalPager(
          state = pagerState,
          modifier = Modifier.fillMaxSize()
        ) { idx ->
          val pageUrl = pages.getOrNull(idx) ?: ""
          MangaPageView(
            pageIndex = idx,
            pageUrl = pageUrl,
            showTranslation = showTranslationOverlay,
            showDual = showDualText,
            bubbleOpacity = bubbleOpacity,
            repository = repository
          )
        }
      }
    }

    // 2. Dim overlay when brightness slider adjusted
    if (brightness < 1.0f) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 1.0f - brightness))
      )
    }

    // 3. Top Controls Bar
    if (isUiVisible) {
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(colors.surface.copy(alpha = 0.95f), Color.Transparent)
            )
          )
          .padding(top = 16.dp, bottom = 24.dp, start = 12.dp, end = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "رجوع",
              tint = colors.textPrimary
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = manga.title,
              color = colors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "${chapter.title} • صفحة $currentPageIndex من $totalPagesCount",
              color = colors.primary,
              fontSize = 11.sp
            )
          }

          // Bookmark
          IconButton(onClick = { isBookmarked = !isBookmarked }) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "علامة مرجعية",
              tint = if (isBookmarked) colors.primary else colors.textPrimary
            )
          }

          // Brightness toggle
          IconButton(onClick = { showBrightnessSlider = !showBrightnessSlider }) {
            Icon(
              imageVector = Icons.Default.BrightnessMedium,
              contentDescription = "السطوع",
              tint = if (showBrightnessSlider) colors.primary else colors.textPrimary
            )
          }

          // Settings
          IconButton(onClick = { showTranslationOverlay = !showTranslationOverlay }) {
            Icon(
              imageVector = Icons.Default.Translate,
              contentDescription = "ترجمة",
              tint = if (showTranslationOverlay) colors.primary else colors.textPrimary
            )
          }
        }
      }
    }

    // 4. Floating Brightness Slider Panel
    if (showBrightnessSlider && isUiVisible) {
      NeonCard(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 80.dp)
          .width(280.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = colors.surface.copy(alpha = 0.95f)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "سطوع شاشة القارئ: ${(brightness * 100).toInt()}%",
            color = colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Slider(
            value = brightness,
            onValueChange = { brightness = it },
            valueRange = 0.2f..1.0f,
            colors = SliderDefaults.colors(
              thumbColor = colors.primary,
              activeTrackColor = colors.primary,
              inactiveTrackColor = colors.border
            )
          )
        }
      }
    }

    // 5. Bottom Navigation & Tools Bar
    if (isUiVisible) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, colors.surface.copy(alpha = 0.96f))
            )
          )
          .padding(bottom = 16.dp, top = 20.dp, start = 12.dp, end = 12.dp)
      ) {
        Column {
          // Page Progress Slider Row
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "$currentPageIndex / $totalPagesCount",
              color = colors.primary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
              value = currentPageIndex.toFloat(),
              onValueChange = { currentPageIndex = it.toInt() },
              valueRange = 1f..totalPagesCount.toFloat(),
              modifier = Modifier.weight(1f),
              colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.border
              )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Prev Chapter
            IconButton(
              onClick = { onPreviousChapter?.invoke() },
              enabled = onPreviousChapter != null,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "الفصل السابق",
                tint = if (onPreviousChapter != null) colors.primary else colors.textTertiary,
                modifier = Modifier.size(18.dp)
              )
            }

            // Next Chapter
            IconButton(
              onClick = { onNextChapter?.invoke() },
              enabled = onNextChapter != null,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "الفصل التالي",
                tint = if (onNextChapter != null) colors.primary else colors.textTertiary,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Action Tools Buttons Row (عمودي | صفحة | ترجمة | السطوع | إخفاء)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // نمط عمودي
            ReaderToolItem(
              label = "عمودي",
              icon = Icons.Default.ViewAgenda,
              isActive = readingMode == ReadingMode.VERTICAL,
              onClick = { readingMode = ReadingMode.VERTICAL }
            )

            // نمط صفحة
            ReaderToolItem(
              label = "صفحة",
              icon = Icons.Default.MenuBook,
              isActive = readingMode == ReadingMode.PAGED,
              onClick = { readingMode = ReadingMode.PAGED }
            )

            // الترجمة الفورية للعربية
            ReaderToolItem(
              label = "ترجمة",
              icon = Icons.Default.Translate,
              isActive = showTranslationOverlay,
              onClick = {
                showTranslationOverlay = !showTranslationOverlay
                if (showTranslationOverlay) {
                  showDualText = !showDualText
                }
              }
            )

            // السطوع
            ReaderToolItem(
              label = "السطوع",
              icon = Icons.Default.BrightnessMedium,
              isActive = showBrightnessSlider,
              onClick = { showBrightnessSlider = !showBrightnessSlider }
            )

            // إخفاء الواجهة
            ReaderToolItem(
              label = "إخفاء",
              icon = Icons.Default.VisibilityOff,
              isActive = false,
              onClick = { isUiVisible = false }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ReaderToolItem(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isActive: Boolean,
  onClick: () -> Unit
) {
  val colors = LocalMangaColors.current

  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isActive) colors.primary else colors.textSecondary,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      color = if (isActive) colors.primary else colors.textSecondary,
      fontSize = 10.sp,
      fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
    )
  }
}

@Composable
private fun MangaPageView(
  pageIndex: Int,
  pageUrl: String,
  showTranslation: Boolean,
  showDual: Boolean,
  bubbleOpacity: Float,
  repository: MangaRepository
) {
  val colors = LocalMangaColors.current
  var bubbles by remember { mutableStateOf<List<TextBubble>>(emptyList()) }

  LaunchedEffect(pageIndex, pageUrl) {
    bubbles = repository.translationEngine.getPageTextBubbles(pageIndex, pageUrl)
  }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.Black)
  ) {
    val boxWidth = maxWidth
    val pageHeight = boxWidth * 1.45f

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(pageHeight)
    ) {
      if (pageUrl.isNotBlank()) {
        AsyncImage(
          model = pageUrl,
          contentDescription = "صفحة ${pageIndex + 1}",
          contentScale = ContentScale.FillWidth,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        // High-contrast clean manga panel frame for demonstration
        Box(
          modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color(0xFF1E293B)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = Color(0xFF334155),
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "لوحة المانجا • صفحة ${pageIndex + 1}",
              color = Color(0xFF64748B),
              fontSize = 13.sp
            )
          }
        }
      }

      // Live Arabic Speech Bubble Overlays (OCR Mode)
      if (showTranslation) {
        bubbles.forEach { bubble ->
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(
                start = boxWidth * bubble.xPercent,
                top = pageHeight * bubble.yPercent
              )
              .width(boxWidth * bubble.widthPercent)
              .clip(RoundedCornerShape(12.dp))
              .background(colors.card.copy(alpha = bubbleOpacity))
              .border(1.dp, colors.primary.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
              .padding(8.dp)
          ) {
            Column {
              Text(
                text = bubble.translatedAr,
                color = colors.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )

              if (showDual) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = bubble.originalText,
                  color = colors.primary,
                  fontSize = 10.sp,
                  lineHeight = 14.sp,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }
        }
      }
    }
  }
}
