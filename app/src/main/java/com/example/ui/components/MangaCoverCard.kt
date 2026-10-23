package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Manga
import com.example.data.model.MangaStatus
import com.example.ui.theme.LocalMangaColors

@Composable
fun MangaCoverCard(
  manga: Manga,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onFavoriteClick: (() -> Unit)? = null,
  showProgress: Boolean = false
) {
  val colors = LocalMangaColors.current
  val shape = RoundedCornerShape(14.dp)

  NeonCard(
    modifier = modifier
      .testTag("manga_card_${manga.id}")
      .fillMaxWidth(),
    shape = shape,
    hasGlow = true,
    onClick = onClick
  ) {
    Column {
      // Cover Image Box with Aspect Ratio 3:4
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(0.72f)
          .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
          .background(colors.surface)
      ) {
        if (manga.coverUrl.isNotBlank()) {
          AsyncImage(
            model = manga.coverUrl,
            contentDescription = manga.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          // Placeholder gradient if no image
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.linearGradient(
                  colors = listOf(colors.card, colors.surface)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = manga.title.take(2).uppercase(),
              color = colors.primary,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Top Badges Overlay (Status & Favorite)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Status Chip (جديد / مستمر / مكتمل)
          val badgeColor = when (manga.status) {
            MangaStatus.COMPLETED -> colors.secondary
            MangaStatus.ONGOING -> colors.primary
            else -> colors.accent
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(badgeColor.copy(alpha = 0.9f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (manga.isNewRelease) "جديد" else manga.status.labelAr,
              color = if (colors.isDark) Color.Black else Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.weight(1f))

          // Favorite Heart Button
          if (onFavoriteClick != null) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onFavoriteClick),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (manga.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "المفضلة",
                tint = if (manga.isFavorite) Color(0xFFFF1744) else Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        // Bottom Gradient on Cover for Text Contrast
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(40.dp)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
              )
            )
        )
      }

      // Reading Progress Bar if in history
      if (showProgress && manga.lastReadProgressPercent > 0) {
        LinearProgressIndicator(
          progress = { manga.lastReadProgressPercent / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp),
          color = colors.primary,
          trackColor = colors.border
        )
      }

      // Metadata Info Below Cover
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        Text(
          text = manga.title,
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          val subtitle = if (manga.lastReadChapterTitle != null) {
            manga.lastReadChapterTitle
          } else if (manga.totalChapters > 0) {
            "فصل ${manga.totalChapters}"
          } else {
            manga.genres.firstOrNull() ?: "مانجا"
          }

          Text(
            text = subtitle,
            color = colors.textSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (manga.rating != null) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "تقييم",
              tint = Color(0xFFFFD600),
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = String.format("%.1f", manga.rating),
              color = colors.textPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
