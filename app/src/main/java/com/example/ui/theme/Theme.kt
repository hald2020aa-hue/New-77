package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemePalette(val labelAr: String, val labelEn: String) {
  NEON_BLUE("الأزرق الافتراضي", "Neon Blue"),
  CRIMSON_RED("الأحمر القرمزي", "Crimson Samurai"),
  EMERALD_GREEN("الأخضر النيوني", "Emerald Green"),
  IMPERIAL_PURPLE("البنفسجي الإمبراطوري", "Imperial Purple"),
  DARK_GOLD("ذهبي داكن", "Dark Gold")
}

@Immutable
data class MangaThemeColors(
  val primary: Color,
  val secondary: Color,
  val accent: Color,
  val glow: Color,
  val background: Color,
  val surface: Color,
  val card: Color,
  val border: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textTertiary: Color,
  val isDark: Boolean
)

val LocalMangaColors = staticCompositionLocalOf {
  getThemeColors(AppThemePalette.NEON_BLUE, isDark = true)
}

fun getThemeColors(palette: AppThemePalette, isDark: Boolean): MangaThemeColors {
  return if (isDark) {
    when (palette) {
      AppThemePalette.NEON_BLUE -> MangaThemeColors(
        primary = NeonBluePrimary,
        secondary = NeonBlueSecondary,
        accent = NeonBlueAccent,
        glow = NeonBlueGlow,
        background = NeonBlueBgDark,
        surface = NeonBlueSurfaceDark,
        card = NeonBlueCardDark,
        border = NeonBlueBorderDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        isDark = true
      )
      AppThemePalette.CRIMSON_RED -> MangaThemeColors(
        primary = CrimsonPrimary,
        secondary = CrimsonSecondary,
        accent = CrimsonAccent,
        glow = CrimsonGlow,
        background = CrimsonBgDark,
        surface = CrimsonSurfaceDark,
        card = CrimsonCardDark,
        border = CrimsonBorderDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        isDark = true
      )
      AppThemePalette.EMERALD_GREEN -> MangaThemeColors(
        primary = EmeraldPrimary,
        secondary = EmeraldSecondary,
        accent = EmeraldAccent,
        glow = EmeraldGlow,
        background = EmeraldBgDark,
        surface = EmeraldSurfaceDark,
        card = EmeraldCardDark,
        border = EmeraldBorderDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        isDark = true
      )
      AppThemePalette.IMPERIAL_PURPLE -> MangaThemeColors(
        primary = PurplePrimary,
        secondary = PurpleSecondary,
        accent = PurpleAccent,
        glow = PurpleGlow,
        background = PurpleBgDark,
        surface = PurpleSurfaceDark,
        card = PurpleCardDark,
        border = PurpleBorderDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        isDark = true
      )
      AppThemePalette.DARK_GOLD -> MangaThemeColors(
        primary = GoldPrimary,
        secondary = GoldSecondary,
        accent = GoldAccent,
        glow = GoldGlow,
        background = GoldBgDark,
        surface = GoldSurfaceDark,
        card = GoldCardDark,
        border = GoldBorderDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        isDark = true
      )
    }
  } else {
    // Light Mode equivalents
    val primaryColor = when (palette) {
      AppThemePalette.NEON_BLUE -> Color(0xFF0284C7)
      AppThemePalette.CRIMSON_RED -> Color(0xFFDC2626)
      AppThemePalette.EMERALD_GREEN -> Color(0xFF16A34A)
      AppThemePalette.IMPERIAL_PURPLE -> Color(0xFF9333EA)
      AppThemePalette.DARK_GOLD -> Color(0xFFD97706)
    }
    MangaThemeColors(
      primary = primaryColor,
      secondary = primaryColor.copy(alpha = 0.8f),
      accent = primaryColor.copy(alpha = 0.9f),
      glow = primaryColor.copy(alpha = 0.3f),
      background = BgLight,
      surface = SurfaceLight,
      card = CardLight,
      border = BorderLight,
      textPrimary = TextPrimaryLight,
      textSecondary = TextSecondaryLight,
      textTertiary = Color(0xFF94A3B8),
      isDark = false
    )
  }
}

@Composable
fun MangaXWTheme(
  palette: AppThemePalette = AppThemePalette.NEON_BLUE,
  darkTheme: Boolean = true,
  content: @Composable () -> Unit
) {
  val mangaColors = getThemeColors(palette, darkTheme)

  val materialColorScheme = if (darkTheme) {
    darkColorScheme(
      primary = mangaColors.primary,
      secondary = mangaColors.secondary,
      background = mangaColors.background,
      surface = mangaColors.surface,
      onPrimary = Color.Black,
      onSecondary = Color.White,
      onBackground = mangaColors.textPrimary,
      onSurface = mangaColors.textPrimary
    )
  } else {
    lightColorScheme(
      primary = mangaColors.primary,
      secondary = mangaColors.secondary,
      background = mangaColors.background,
      surface = mangaColors.surface,
      onPrimary = Color.White,
      onSecondary = Color.White,
      onBackground = mangaColors.textPrimary,
      onSurface = mangaColors.textPrimary
    )
  }

  CompositionLocalProvider(LocalMangaColors provides mangaColors) {
    MaterialTheme(
      colorScheme = materialColorScheme,
      typography = Typography,
      content = content
    )
  }
}
