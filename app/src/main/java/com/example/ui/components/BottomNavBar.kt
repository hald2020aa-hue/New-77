package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalMangaColors

enum class AppNavTab(val titleAr: String, val titleEn: String, val icon: ImageVector) {
  HOME("الرئيسية", "Home", Icons.Default.Home),
  SOURCES("المصادر", "Sources", Icons.Default.Explore),
  LIBRARY("المكتبة", "Library", Icons.Default.CollectionsBookmark),
  READER("القارئ", "Reader", Icons.Default.MenuBook),
  SETTINGS("الإعدادات", "Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
  currentTab: AppNavTab,
  onTabSelected: (AppNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalMangaColors.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(colors.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      AppNavTab.entries.forEach { tab ->
        val isSelected = tab == currentTab

        Column(
          modifier = Modifier
            .testTag("nav_tab_${tab.name.lowercase()}")
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTabSelected(tab) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else Color.Transparent),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = tab.icon,
              contentDescription = tab.titleAr,
              tint = if (isSelected) colors.primary else colors.textSecondary,
              modifier = Modifier.size(22.dp)
            )
          }

          Text(
            text = tab.titleAr,
            color = if (isSelected) colors.primary else colors.textSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )

          if (isSelected) {
            Box(
              modifier = Modifier
                .padding(top = 2.dp)
                .size(4.dp)
                .clip(CircleShape)
                .background(colors.primary)
            )
          }
        }
      }
    }
  }
}
