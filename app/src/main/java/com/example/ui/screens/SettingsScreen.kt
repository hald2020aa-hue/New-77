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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeonCard
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalMangaColors
import com.example.ui.theme.NeonBluePrimary
import com.example.ui.theme.PurplePrimary

@Composable
fun SettingsScreen(
  currentPalette: AppThemePalette,
  onPaletteChange: (AppThemePalette) -> Unit,
  isDarkTheme: Boolean,
  onThemeModeChange: (Boolean) -> Unit,
  isArabic: Boolean,
  onLanguageChange: (Boolean) -> Unit,
  onOpenBackup: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalMangaColors.current

  var isAdBlockEnabled by remember { mutableStateOf(true) }
  var isAutoTranslateEnabled by remember { mutableStateOf(true) }
  var isOcrEnabled by remember { mutableStateOf(true) }
  var showAboutDialog by remember { mutableStateOf(false) }

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
      Text(
        text = "الإعدادات والمظهر",
        color = colors.textPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      )
      Text(
        text = "خصص تجربتك في Manga XW بالشكل الذي يناسبك",
        color = colors.textSecondary,
        fontSize = 11.sp
      )
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Language Setting (اللغة)
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "اللغة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "اختر لغة واجهة التطبيق (العربية / English)",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }

            // Language Switch Pills
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface)
                .padding(2.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isArabic) colors.primary else Color.Transparent)
                  .clickable { onLanguageChange(true) }
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "العربية",
                  color = if (isArabic) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (!isArabic) colors.primary else Color.Transparent)
                  .clickable { onLanguageChange(false) }
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "English",
                  color = if (!isArabic) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // 2. Dark / Light Mode Switcher
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isDarkTheme) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "المظهر",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = if (isDarkTheme) "الوضع الداكن (أزرق نيون)" else "الوضع الفاتح المريح",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }

            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface)
                .padding(2.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isDarkTheme) colors.primary else Color.Transparent)
                  .clickable { onThemeModeChange(true) }
                  .padding(horizontal = 12.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "داكن",
                  color = if (isDarkTheme) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (!isDarkTheme) colors.primary else Color.Transparent)
                  .clickable { onThemeModeChange(false) }
                  .padding(horizontal = 12.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "فاتح",
                  color = if (!isDarkTheme) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // 3. Theme Colors (ألوان الثيم الخمسة)
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ألوان الثيم",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5 Theme Palette Selectors matching user screenshots
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AppThemePalette.entries.forEach { palette ->
                val isSelected = palette == currentPalette
                val paletteAccent = when (palette) {
                  AppThemePalette.NEON_BLUE -> NeonBluePrimary
                  AppThemePalette.CRIMSON_RED -> CrimsonPrimary
                  AppThemePalette.EMERALD_GREEN -> EmeraldPrimary
                  AppThemePalette.IMPERIAL_PURPLE -> PurplePrimary
                  AppThemePalette.DARK_GOLD -> GoldPrimary
                }

                Box(
                  modifier = Modifier
                    .width(95.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) paletteAccent else colors.border,
                      shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onPaletteChange(palette) }
                    .padding(8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                      modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(paletteAccent),
                      contentAlignment = Alignment.Center
                    ) {
                      if (isSelected) {
                        Icon(
                          imageVector = Icons.Default.Check,
                          contentDescription = null,
                          tint = Color.Black,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = palette.labelAr,
                      color = if (isSelected) paletteAccent else colors.textPrimary,
                      fontSize = 10.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 4. Built-in Translation Settings
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "نظام الترجمة الفورية للعربية",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Auto-translate toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ترجمة النصوص تلقائياً",
                  color = colors.textPrimary,
                  fontSize = 13.sp
                )
                Text(
                  text = "ترجمة العناوين والوصف والفصول من اليابانية/الإنجليزية للعربية",
                  color = colors.textSecondary,
                  fontSize = 10.sp
                )
              }
              Switch(
                checked = isAutoTranslateEnabled,
                onCheckedChange = { isAutoTranslateEnabled = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = colors.primary
                )
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // OCR Manga Page Translation toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ترجمة نصوص صفحات المانجا (OCR)",
                  color = colors.textPrimary,
                  fontSize = 13.sp
                )
                Text(
                  text = "التعرف على فقاعات الحوار وعرض الترجمة العربية فوقها في القارئ",
                  color = colors.textSecondary,
                  fontSize = 10.sp
                )
              }
              Switch(
                checked = isOcrEnabled,
                onCheckedChange = { isOcrEnabled = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = colors.primary
                )
              )
            }
          }
        }
      }

      // 5. Ad Blocker Toggle (حظر الإعلانات)
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Block,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "حظر الإعلانات والنوافذ المنبثقة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "تصفية الإعلانات في مواقع ومصادر الويب المدمجة لتجربة نظيفة",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
            Switch(
              checked = isAdBlockEnabled,
              onCheckedChange = { isAdBlockEnabled = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.primary
              )
            )
          }
        }
      }

      // 6. Backup & Restore Shortcut
      item {
        NeonCard(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenBackup),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "النسخ الاحتياطي والاستعادة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "احفظ بياناتك محلياً أو سحابياً واستعدها في أي وقت",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = colors.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // 7. About App (حول التطبيق & ترخيص GPL-3.0)
      item {
        NeonCard(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showAboutDialog = true },
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "حول التطبيق",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Manga XW • الإصدار 2.4.0 (GPL-3.0)",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = colors.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }

  // About Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = {
        Text(
          text = "Manga XW V13",
          fontWeight = FontWeight.Bold,
          color = colors.textPrimary,
          fontSize = 18.sp
        )
      },
      text = {
        Column {
          Text(
            text = "تطبيق قراءة مانجا متطور ومستقل بهوية وتصميم جديدين كلياً.",
            color = colors.textPrimary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "مبني بالاعتماد على المحرك الأساسي لمشروع Kotatsu-Redo مفتوح المصدر بموجب ترخيص GNU General Public License v3.0 (GPL-3.0). يتم الحفاظ على حقوق النشر البرمجية لجميع المساهمين الأصليين، مع تقديم واجهة مستخدم وهوية مستقلة بالكامل.",
            color = colors.textSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      },
      confirmButton = {
        Button(onClick = { showAboutDialog = false }) {
          Text("إغلاق")
        }
      }
    )
  }
}
