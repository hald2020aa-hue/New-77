package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalMangaColors

@Composable
fun NeonButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPrimary: Boolean = true,
  icon: (@Composable () -> Unit)? = null,
  testTag: String = "neon_button"
) {
  val colors = LocalMangaColors.current
  val shape = RoundedCornerShape(14.dp)

  val backgroundBrush = if (isPrimary) {
    Brush.horizontalGradient(
      colors = listOf(
        colors.primary,
        colors.secondary
      )
    )
  } else {
    Brush.horizontalGradient(
      colors = listOf(
        colors.card,
        colors.surface
      )
    )
  }

  val textColor = if (isPrimary) {
    if (colors.isDark) Color.Black else Color.White
  } else {
    colors.textPrimary
  }

  val borderModifier = if (!isPrimary) {
    Modifier.border(1.dp, colors.border, shape)
  } else {
    Modifier.border(1.dp, colors.glow.copy(alpha = 0.6f), shape)
  }

  Box(
    modifier = modifier
      .testTag(testTag)
      .height(48.dp)
      .clip(shape)
      .background(backgroundBrush)
      .then(borderModifier)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (icon != null) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }
  }
}
