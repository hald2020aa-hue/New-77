package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MangaRepository
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors

@Composable
fun BackupStorageScreen(
  repository: MangaRepository,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)
  val colors = LocalMangaColors.current
  val context = LocalContext.current
  val cacheSizeMb by repository.cacheSizeMb.collectAsState()

  var isCloudSelected by remember { mutableStateOf(true) }
  var showImportDialog by remember { mutableStateOf(false) }
  var importText by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf<String?>(null) }

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
          text = "النسخ الاحتياطي والاستعادة",
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Text(
          text = "إدارة بياناتك وحفظها بأمان",
          color = colors.textSecondary,
          fontSize = 11.sp
        )
      }
      Icon(
        imageVector = Icons.Default.CloudDone,
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
      // 1. Backup Status Hero Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          hasGlow = true,
          backgroundColor = colors.card
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.linearGradient(
                  colors = listOf(
                    colors.primary.copy(alpha = 0.12f),
                    colors.surface
                  )
                )
              )
              .padding(16.dp)
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "آخر نسخ احتياطي: 2025/04/28 - 14:30",
                  color = colors.textPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "تم حفظ بياناتك بنجاح • 1,248 عنصراً",
                color = colors.textSecondary,
                fontSize = 11.sp
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Cloud vs Local Device Radio Switch
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(colors.card)
                  .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // السحابة
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCloudSelected) colors.primary else Color.Transparent)
                    .clickable { isCloudSelected = true }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Cloud,
                      contentDescription = null,
                      tint = if (isCloudSelected) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "السحابة",
                      color = if (isCloudSelected) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                // الجهاز المحلي
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isCloudSelected) colors.primary else Color.Transparent)
                    .clickable { isCloudSelected = false }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.PhoneAndroid,
                      contentDescription = null,
                      tint = if (!isCloudSelected) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "الجهاز المحلي",
                      color = if (!isCloudSelected) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 2. Export / Import JSON Action Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // تصدير JSON
          NeonCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = colors.card,
            onClick = {
              val json = repository.exportBackupJson()
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Manga XW Backup", json))
              Toast.makeText(context, "تم تصدير ونسخ بيانات JSON بنجاح إلى الحافظة!", Toast.LENGTH_LONG).show()
              statusMessage = "تم نسخ ملف النسخ الاحتياطي JSON إلى الحافظة"
            }
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(colors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Download,
                  contentDescription = null,
                  tint = colors.primary,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "تصدير JSON",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "حفظ مكتبتك وإعداداتك",
                color = colors.textSecondary,
                fontSize = 10.sp
              )
            }
          }

          // استيراد JSON
          NeonCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = colors.card,
            onClick = { showImportDialog = true }
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(colors.secondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Upload,
                  contentDescription = null,
                  tint = colors.secondary,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "استيراد JSON",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "استعادة من ملف سابق",
                color = colors.textSecondary,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // 3. Reading History Management
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
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "سجل القراءة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "إدارة وحفظ سجل قراءاتك أو مسحه",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
            NeonButton(
              text = "مسح السجل",
              isPrimary = false,
              onClick = {
                repository.clearReadingHistory()
                Toast.makeText(context, "تم مسح سجل القراءة بنجاح", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
      }

      // 4. Cache Wipe
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
                text = "مسح ذاكرة التخزين المؤقتة",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "تحرير ${String.format("%.0f", cacheSizeMb)} MB من الملفات المؤقتة والترجمات",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
            NeonButton(
              text = "مسح الكاش",
              isPrimary = false,
              onClick = {
                repository.clearCache()
                Toast.makeText(context, "تم مسح الكاش بنجاح", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
      }
    }
  }

  // Import JSON Dialog
  if (showImportDialog) {
    AlertDialog(
      onDismissRequest = { showImportDialog = false },
      title = {
        Text(
          text = "استيراد نسخة احتياطية (JSON)",
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      },
      text = {
        Column {
          Text(
            text = "الصق نص JSON المصدّر لاستعادة أعمالك في المكتبة:",
            color = colors.textSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = importText,
            onValueChange = { importText = it },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            placeholder = { Text("الصق بيانات JSON هنا...") }
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val success = repository.importBackupJson(importText)
            if (success) {
              Toast.makeText(context, "تمت استعادة المكتبة بنجاح!", Toast.LENGTH_LONG).show()
              showImportDialog = false
            } else {
              Toast.makeText(context, "تنسيق JSON غير صالح", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("استيراد")
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }
}
