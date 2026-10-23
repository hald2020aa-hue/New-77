package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.repository.MangaRepository
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors

@Composable
fun DownloadsScreen(
  repository: MangaRepository,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)
  val colors = LocalMangaColors.current
  val usedStorageMb by repository.usedStorageMb.collectAsState()
  val cacheSizeMb by repository.cacheSizeMb.collectAsState()

  var isCacheCleared by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(colors.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp),
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
          text = "التنزيلات",
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Text(
          text = "اقرأ المانجا في أي وقت بدون إنترنت",
          color = colors.textSecondary,
          fontSize = 11.sp
        )
      }
      Icon(
        imageVector = Icons.Default.CloudDownload,
        contentDescription = null,
        tint = colors.primary,
        modifier = Modifier.size(24.dp)
      )
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Storage Usage Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          backgroundColor = colors.card
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(colors.surface),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  tint = colors.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "مساحة التخزين",
                  color = colors.textPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = "${String.format("%.1f", usedStorageMb / 1024f)} جيجابايت مستخدمة من 64 جيجابايت",
                  color = colors.textSecondary,
                  fontSize = 11.sp
                )
              }
              Text(
                text = "${((usedStorageMb / (64 * 1024)) * 100).toInt()}%",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
              progress = { (usedStorageMb / (64 * 1024)).coerceIn(0.05f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = colors.primary,
              trackColor = colors.surface
            )
          }
        }
      }

      // 2. Cache Cleaner Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
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
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CleaningServices,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "تنظيف ذاكرة التخزين المؤقت",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = if (isCacheCleared) "تم تنظيف الكاش بنجاح!" else "يمكنك تحرير ${String.format("%.0f", cacheSizeMb)} ميجابايت من الملفات المؤقتة",
                color = if (isCacheCleared) colors.primary else colors.textSecondary,
                fontSize = 11.sp
              )
            }
            NeonButton(
              text = if (isCacheCleared) "تم" else "تنظيف الآن",
              isPrimary = !isCacheCleared,
              onClick = {
                repository.clearCache()
                isCacheCleared = true
              }
            )
          }
        }
      }

      // 3. Completed Downloads Section
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "الملفات المحملة",
              color = colors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
              text = "حذف الكل",
              color = Color(0xFFFF5252),
              fontSize = 12.sp,
              modifier = Modifier.clickable { /* delete all */ }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          NeonCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            backgroundColor = colors.card
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "لا توجد فصول محملة حالياً",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "اضغط على أيقونة التنزيل بجانب أي فصل لحفظه وقراءته بدون إنترنت",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }
  }
}
