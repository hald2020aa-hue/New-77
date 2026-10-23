package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Manga
import com.example.data.repository.MangaRepository
import com.example.ui.components.MangaCoverCard
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors

@Composable
fun LibraryScreen(
  repository: MangaRepository,
  onMangaClick: (Manga) -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalMangaColors.current
  val library by repository.library.collectAsState()
  val categories by repository.categories.collectAsState()

  var selectedCategory by remember { mutableStateOf("all") }
  var isGridView by remember { mutableStateOf(true) }

  val filteredManga = remember(library, selectedCategory) {
    when (selectedCategory) {
      "favorites" -> library.filter { it.isFavorite }
      "reading" -> library.filter { it.lastReadProgressPercent in 1..99 }
      "completed" -> library.filter { it.lastReadProgressPercent >= 100 }
      else -> library
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background)
  ) {
    // Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(colors.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "المكتبة",
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          )
          Text(
            text = "مجموعتك من المانجا • ${library.size} عمل",
            color = colors.textSecondary,
            fontSize = 11.sp
          )
        }

        // View Mode Switcher (Grid / List)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.card)
            .padding(2.dp)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isGridView) colors.primary else Color.Transparent)
              .clickable { isGridView = true },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.GridView,
              contentDescription = "عرض شبكي",
              tint = if (isGridView) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }

          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(if (!isGridView) colors.primary else Color.Transparent)
              .clickable { isGridView = false },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ViewList,
              contentDescription = "عرض قائمة",
              tint = if (!isGridView) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Filter Chips (الكل | المفضلة | قيد القراءة | أريد قراءتها | مكتمل)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { cat ->
          val isSelected = cat.id == selectedCategory
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) colors.primary else colors.card)
              .clickable { selectedCategory = cat.id }
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = cat.name,
              color = if (isSelected) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Custom Folders Carousel ("المجلدات الخاصة بي")
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // + مجلد جديد
        NeonCard(
          modifier = Modifier.width(110.dp).height(65.dp),
          shape = RoundedCornerShape(12.dp),
          backgroundColor = colors.card,
          onClick = {}
        ) {
          Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = colors.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "مجلد جديد", color = colors.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Default Folders
        listOf("مانجا أكشن", "المفضلة", "أريد قراءتها", "مكتمل").forEach { folderName ->
          NeonCard(
            modifier = Modifier.width(115.dp).height(65.dp),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = colors.card,
            onClick = {}
          ) {
            Column(
              modifier = Modifier.fillMaxSize().padding(8.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = folderName,
                color = colors.textPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // Manga Collection View
    if (filteredManga.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(colors.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CollectionsBookmark,
              contentDescription = null,
              tint = colors.primary,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "المكتبة فارغة حالياً",
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "استكشف أفضل المانجا المتاحة من المصادر وأضفها إلى مفضلتك لتتابع فصولها بسهولة.",
            color = colors.textSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          NeonButton(
            text = "استكشف المانجا الآن",
            isPrimary = true,
            onClick = onExploreClick
          )
        }
      }
    } else {
      if (isGridView) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredManga) { manga ->
            MangaCoverCard(
              manga = manga,
              onClick = { onMangaClick(manga) },
              onFavoriteClick = { repository.toggleFavorite(manga) },
              showProgress = true
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredManga) { manga ->
            MangaCoverCard(
              manga = manga,
              onClick = { onMangaClick(manga) },
              onFavoriteClick = { repository.toggleFavorite(manga) },
              showProgress = true
            )
          }
        }
      }
    }
  }
}
