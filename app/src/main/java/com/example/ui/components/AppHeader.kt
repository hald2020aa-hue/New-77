package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MangaSource
import com.example.ui.theme.LocalMangaColors

@Composable
fun AppHeader(
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onSearchSubmit: () -> Unit,
  sources: List<MangaSource>,
  selectedSourceId: String,
  onSourceSelect: (String) -> Unit,
  onAddSourceClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  onBackupClick: () -> Unit,
  onMoreMenuClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalMangaColors.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(colors.surface)
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    // 1. Top Brand Bar: Logo + Manga XW V13 + Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Glowing App Logo Icon
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(colors.primary, colors.secondary)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.MenuBook,
          contentDescription = "Manga XW",
          tint = if (colors.isDark) Color.Black else Color.White,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Manga XW",
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(colors.primary.copy(alpha = 0.2f))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = "V13",
              color = colors.primary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Text(
          text = "قارئ مانجا احترافي • NATIVE WEBSPACE",
          color = colors.textSecondary,
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.weight(1f))

      // Notification Bell
      IconButton(onClick = onNotificationsClick, modifier = Modifier.size(36.dp)) {
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = "الإشعارات",
          tint = colors.textPrimary,
          modifier = Modifier.size(20.dp)
        )
      }

      // Cloud / Backup icon
      IconButton(onClick = onBackupClick, modifier = Modifier.size(36.dp)) {
        Icon(
          imageVector = Icons.Default.CloudDownload,
          contentDescription = "النسخ الاحتياطي",
          tint = colors.textPrimary,
          modifier = Modifier.size(20.dp)
        )
      }

      // More menu
      IconButton(onClick = onMoreMenuClick, modifier = Modifier.size(36.dp)) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "المزيد",
          tint = colors.textPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 2. Search Field with glowing border
    NeonCard(
      modifier = Modifier
        .testTag("app_search_bar")
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(24.dp),
      hasGlow = true,
      backgroundColor = colors.card
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "بحث",
          tint = colors.primary,
          modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          modifier = Modifier
            .weight(1f)
            .testTag("search_text_input"),
          textStyle = TextStyle(
            color = colors.textPrimary,
            fontSize = 13.sp
          ),
          cursorBrush = SolidColor(colors.primary),
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
          decorationBox = { innerTextField ->
            if (searchQuery.isEmpty()) {
              Text(
                text = "ابحث عن مانجا أو مؤلف أو أدخل رابط...",
                color = colors.textTertiary,
                fontSize = 12.sp
              )
            }
            innerTextField()
          }
        )

        if (searchQuery.isNotEmpty()) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "مسح",
            tint = colors.textSecondary,
            modifier = Modifier
              .size(18.dp)
              .clickable { onSearchQueryChange("") }
          )
          Spacer(modifier = Modifier.width(8.dp))
        }

        // Submit arrow button
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
              Brush.horizontalGradient(
                colors = listOf(colors.primary, colors.secondary)
              )
            )
            .clickable(onClick = onSearchSubmit),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "تنفيذ البحث",
            tint = if (colors.isDark) Color.Black else Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3. Quick Sources Pills Carousel
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      verticalAlignment = Alignment.CenterVertically
    ) {
      sources.filter { it.isEnabled }.forEach { src ->
        val isSelected = src.id == selectedSourceId
        Box(
          modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.25f) else colors.card)
            .clickable { onSourceSelect(src.id) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(colors.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = src.iconLetter,
                color = if (colors.isDark) Color.Black else Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = src.name,
              color = if (isSelected) colors.primary else colors.textPrimary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      // Add Custom Source Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(colors.card)
          .clickable(onClick = onAddSourceClick)
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "إضافة مصدر",
            tint = colors.primary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "مصدر مخصص",
            color = colors.textSecondary,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
