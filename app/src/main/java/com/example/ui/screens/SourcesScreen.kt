package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MangaSource
import com.example.data.model.SourceStatus
import com.example.data.repository.MangaRepository
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.LocalMangaColors
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusSlow
import com.example.ui.theme.StatusWorking

@Composable
fun SourcesScreen(
  repository: MangaRepository,
  modifier: Modifier = Modifier
) {
  val colors = LocalMangaColors.current
  val sources by repository.sources.collectAsState()

  var selectedFilter by remember { mutableStateOf("all") } // all, built_in, custom
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

  var newSourceName by remember { mutableStateOf("") }
  var newSourceUrl by remember { mutableStateOf("") }

  val filteredSources = remember(sources, selectedFilter, searchQuery) {
    sources.filter { src ->
      val matchesTab = when (selectedFilter) {
        "built_in" -> !src.isCustom
        "custom" -> src.isCustom
        else -> true
      }
      val matchesQuery = searchQuery.isBlank() || src.name.contains(searchQuery, ignoreCase = true) || src.url.contains(searchQuery, ignoreCase = true)
      matchesTab && matchesQuery
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
      Text(
        text = "المصادر",
        color = colors.textPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      )
      Text(
        text = "إدارة مواقع مانجا المدمجة والمخصصة داخل التطبيق",
        color = colors.textSecondary,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Filter Tabs (الكل | مدمجة | مخصصة)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val totalCount = sources.size
        val builtInCount = sources.count { !it.isCustom }
        val customCount = sources.count { it.isCustom }

        SourceFilterTab(
          label = "الكل ($totalCount)",
          isSelected = selectedFilter == "all",
          modifier = Modifier.weight(1f),
          onClick = { selectedFilter = "all" }
        )
        SourceFilterTab(
          label = "مدمجة ($builtInCount)",
          isSelected = selectedFilter == "built_in",
          modifier = Modifier.weight(1f),
          onClick = { selectedFilter = "built_in" }
        )
        SourceFilterTab(
          label = "مخصصة ($customCount)",
          isSelected = selectedFilter == "custom",
          modifier = Modifier.weight(1f),
          onClick = { selectedFilter = "custom" }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons (+ إضافة موقع | استيراد | تصدير)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        NeonButton(
          text = "إضافة موقع",
          isPrimary = true,
          icon = {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = if (colors.isDark) Color.Black else Color.White,
              modifier = Modifier.size(16.dp)
            )
          },
          modifier = Modifier.weight(1.2f),
          onClick = { showAddDialog = true }
        )

        NeonButton(
          text = "استيراد",
          isPrimary = false,
          icon = {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              tint = colors.textPrimary,
              modifier = Modifier.size(16.dp)
            )
          },
          modifier = Modifier.weight(1f),
          onClick = {}
        )

        NeonButton(
          text = "تصدير",
          isPrimary = false,
          icon = {
            Icon(
              imageVector = Icons.Default.Upload,
              contentDescription = null,
              tint = colors.textPrimary,
              modifier = Modifier.size(16.dp)
            )
          },
          modifier = Modifier.weight(1f),
          onClick = {}
        )
      }
    }

    // Sources List
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredSources) { src ->
        SourceItemCard(
          source = src,
          onToggle = { repository.toggleSource(src.id) },
          onDelete = { repository.removeCustomSource(src.id) }
        )
      }
    }
  }

  // Add Custom Source Dialog
  if (showAddDialog) {
    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = {
        Text(
          text = "إضافة مصدر مانجا مخصص",
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      },
      text = {
        Column {
          OutlinedTextField(
            value = newSourceName,
            onValueChange = { newSourceName = it },
            label = { Text("اسم الموقع") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = newSourceUrl,
            onValueChange = { newSourceUrl = it },
            label = { Text("رابط الموقع (URL)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newSourceName.isNotBlank() && newSourceUrl.isNotBlank()) {
              repository.addCustomSource(newSourceName, newSourceUrl)
              newSourceName = ""
              newSourceUrl = ""
              showAddDialog = false
            }
          }
        ) {
          Text("إضافة")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }
}

@Composable
private fun SourceFilterTab(
  label: String,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val colors = LocalMangaColors.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) colors.primary else colors.card)
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = if (isSelected) (if (colors.isDark) Color.Black else Color.White) else colors.textSecondary,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}

@Composable
private fun SourceItemCard(
  source: MangaSource,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val colors = LocalMangaColors.current

  NeonCard(
    modifier = Modifier
      .testTag("source_card_${source.id}")
      .fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    backgroundColor = colors.card
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Source letter / logo avatar
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(colors.surface),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = source.iconLetter,
          color = colors.primary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = source.name,
          color = colors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Text(
          text = source.url,
          color = colors.textTertiary,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Status badge
        Row(verticalAlignment = Alignment.CenterVertically) {
          val (dotColor, statusText) = when (source.status) {
            SourceStatus.WORKING -> StatusWorking to source.status.labelAr
            SourceStatus.SLOW -> StatusSlow to source.status.labelAr
            SourceStatus.OFFLINE -> StatusOffline to source.status.labelAr
          }

          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(dotColor)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = statusText,
            color = dotColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(colors.surface)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = if (source.isCustom) "مخصصة" else "مدمجة",
              color = colors.textSecondary,
              fontSize = 9.sp
            )
          }
        }
      }

      // Delete custom source
      if (source.isCustom) {
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "حذف",
            tint = Color(0xFFFF5252),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Toggle switch
      Switch(
        checked = source.isEnabled,
        onCheckedChange = { onToggle() },
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = colors.primary,
          uncheckedTrackColor = colors.border
        )
      )
    }
  }
}
