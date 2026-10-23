package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Manga
import com.example.data.repository.MangaRepository
import com.example.ui.components.AppHeader
import com.example.ui.components.MangaCoverCard
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors

@Composable
fun HomeScreen(
  repository: MangaRepository,
  onMangaClick: (Manga) -> Unit,
  onOpenReader: (Manga) -> Unit,
  onOpenSources: () -> Unit,
  onOpenLibrary: () -> Unit,
  onOpenDownloads: () -> Unit,
  onOpenBackup: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalMangaColors.current
  val trendingManga by repository.trendingManga.collectAsState()
  val continueReading by repository.continueReading.collectAsState()
  val library by repository.library.collectAsState()
  val sources by repository.sources.collectAsState()
  val searchResults by repository.searchResults.collectAsState()
  val isLoading by repository.isLoading.collectAsState()
  val errorMessage by repository.errorMessage.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedSourceId by remember { mutableStateOf("mangadex") }
  var isSearching by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background)
  ) {
    // Header
    AppHeader(
      searchQuery = searchQuery,
      onSearchQueryChange = {
        searchQuery = it
        if (it.isBlank()) {
          isSearching = false
        }
      },
      onSearchSubmit = {
        if (searchQuery.isNotBlank()) {
          isSearching = true
          coroutineScope.launch {
            repository.searchManga(searchQuery)
          }
        }
      },
      sources = sources,
      selectedSourceId = selectedSourceId,
      onSourceSelect = { selectedSourceId = it },
      onAddSourceClick = onOpenSources,
      onNotificationsClick = {},
      onBackupClick = onOpenBackup,
      onMoreMenuClick = onOpenSources
    )

    if (isSearching && searchQuery.isNotBlank()) {
      // Search Results View
      Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = colors.primary
          )
        } else if (searchResults.isEmpty()) {
          Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Explore,
              contentDescription = null,
              tint = colors.textSecondary,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "لم يتم العثور على نتائج لـ \"$searchQuery\"",
              color = colors.textSecondary,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "جرب البحث باللغة الإنجليزية أو اسم مختلف",
              color = colors.textTertiary,
              fontSize = 12.sp
            )
          }
        } else {
          LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(searchResults) { manga ->
              MangaCoverCard(
                manga = manga,
                onClick = { onMangaClick(manga) },
                onFavoriteClick = { repository.toggleFavorite(manga) }
              )
            }
          }
        }
      }
    } else {
      // Main Content Feed
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        // 1. Quick Navigation Shortcuts Row (الأكثر رواجاً / جديد المانجا / تصفح حسب النوع / المصادر)
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // المصادر
            ShortcutCard(
              title = "المصادر",
              subtitle = "مواقع مدمجة",
              icon = Icons.Default.Explore,
              modifier = Modifier.weight(1f),
              onClick = onOpenSources
            )
            // تصفح حسب النوع
            ShortcutCard(
              title = "التصنيفات",
              subtitle = "أكشن، مغامرة",
              icon = Icons.Default.MenuBook,
              modifier = Modifier.weight(1f),
              onClick = onOpenLibrary
            )
            // جديد المانجا
            ShortcutCard(
              title = "الجديد",
              subtitle = "أحدث الفصول",
              icon = Icons.Default.AutoAwesome,
              modifier = Modifier.weight(1f),
              onClick = { repository.loadTrendingManga() }
            )
            // الأكثر رواجاً
            ShortcutCard(
              title = "الأكثر رواجاً",
              subtitle = "المانجا الأشهر",
              icon = Icons.Default.LocalFireDepartment,
              modifier = Modifier.weight(1f),
              onClick = { repository.loadTrendingManga() }
            )
          }
        }

        // 2. Continue Reading (متابعة القراءة) Section
        item {
          Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "متابعة القراءة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Spacer(modifier = Modifier.weight(1f))
              Text(
                text = "عرض الكل",
                color = colors.primary,
                fontSize = 12.sp,
                modifier = Modifier.clickable(onClick = onOpenLibrary)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (continueReading.isEmpty()) {
              // Empty State matching Design
              NeonCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = colors.card
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(44.dp)
                      .clip(CircleShape)
                      .background(colors.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.MenuBook,
                      contentDescription = null,
                      tint = colors.primary,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "لا توجد قراءات حديثة",
                      color = colors.textPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "اختر مانجا من القائمة أدناه للبدء في القراءة وحفظ تقدمك",
                      color = colors.textSecondary,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            } else {
              LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                items(continueReading) { manga ->
                  Box(modifier = Modifier.width(130.dp)) {
                    MangaCoverCard(
                      manga = manga,
                      onClick = { onOpenReader(manga) },
                      onFavoriteClick = { repository.toggleFavorite(manga) },
                      showProgress = true
                    )
                  }
                }
              }
            }
          }
        }

        // 3. Trending / Popular Manga Section (Real Data from Source)
        item {
          Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFFF5252),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "المانجا الأكثر شعبية وتحديثاً",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Spacer(modifier = Modifier.weight(1f))
              if (isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = colors.primary,
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "تحديث",
                  tint = colors.textSecondary,
                  modifier = Modifier
                    .size(18.dp)
                    .clickable { repository.loadTrendingManga() }
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (errorMessage != null && trendingManga.isEmpty()) {
              NeonCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = colors.card
              ) {
                Column(
                  modifier = Modifier.padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = errorMessage ?: "حدث خطأ أثناء الاتصال بالمصدر",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "إعادة المحاولة",
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { repository.loadTrendingManga() }
                  )
                }
              }
            } else {
              LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                items(trendingManga) { manga ->
                  Box(modifier = Modifier.width(130.dp)) {
                    MangaCoverCard(
                      manga = manga,
                      onClick = { onMangaClick(manga) },
                      onFavoriteClick = { repository.toggleFavorite(manga) }
                    )
                  }
                }
              }
            }
          }
        }

        // 4. Promo Banner Card (تجربة قراءة متحركة وحديثة)
        item {
          Spacer(modifier = Modifier.height(16.dp))
          NeonCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            hasGlow = true,
            backgroundColor = colors.surface
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(
                      colors.primary.copy(alpha = 0.15f),
                      colors.card
                    )
                  )
                )
                .padding(16.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                    modifier = Modifier.size(28.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "تجربة قراءة متحركة وحديثة",
                      color = colors.textPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                      Text(
                        text = "محسنة V13",
                        color = colors.primary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "انتقالات سلسة، ترجمة تلقائية فورية للعربية، وقارئ مانجا احترافي",
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

        // 5. Recent Integrated Sources (المواقع الأخيرة)
        item {
          Spacer(modifier = Modifier.height(18.dp))
          Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "المواقع الأخيرة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Spacer(modifier = Modifier.weight(1f))
              Text(
                text = "عرض الكل",
                color = colors.primary,
                fontSize = 12.sp,
                modifier = Modifier.clickable(onClick = onOpenSources)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              sources.take(5).forEach { src ->
                NeonCard(
                  modifier = Modifier
                    .width(115.dp)
                    .height(95.dp),
                  shape = RoundedCornerShape(14.dp),
                  backgroundColor = colors.card,
                  onClick = onOpenSources
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = src.iconLetter,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = src.name,
                      color = colors.textPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ShortcutCard(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val colors = LocalMangaColors.current

  NeonCard(
    modifier = modifier.height(76.dp),
    shape = RoundedCornerShape(14.dp),
    backgroundColor = colors.card,
    onClick = onClick
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = colors.primary,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        color = colors.textPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
      Text(
        text = subtitle,
        color = colors.textSecondary,
        fontSize = 8.sp,
        maxLines = 1
      )
    }
  }
}
