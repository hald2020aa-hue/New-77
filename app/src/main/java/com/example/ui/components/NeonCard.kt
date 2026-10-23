package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalMangaColors

@Composable
fun NeonCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(16.dp),
  hasGlow: Boolean = true,
  glowColor: Color? = null,
  backgroundColor: Color? = null,
  borderWidth: Dp = 1.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val colors = LocalMangaColors.current
  val finalGlow = glowColor ?: colors.primary.copy(alpha = if (colors.isDark) 0.35f else 0.2f)
  val finalBg = backgroundColor ?: colors.card

  val borderModifier = if (hasGlow) {
    Modifier.border(
      border = BorderStroke(
        width = borderWidth,
        brush = Brush.verticalGradient(
          colors = listOf(
            finalGlow,
            colors.border.copy(alpha = 0.5f)
          )
        )
      ),
      shape = shape
    )
  } else {
    Modifier.border(
      border = BorderStroke(borderWidth, colors.border),
      shape = shape
    )
  }

  val clickModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .then(clickModifier)
      .clip(shape)
      .background(finalBg)
      .then(borderModifier)
  ) {
    content()
  }
}
