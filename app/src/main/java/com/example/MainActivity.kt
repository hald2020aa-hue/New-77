package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.model.Chapter
import com.example.data.model.Manga
import com.example.data.repository.MangaRepository
import com.example.ui.components.AppNavTab
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.BackupStorageScreen
import com.example.ui.screens.DetailsScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SourcesScreen
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.LocalMangaColors
import com.example.ui.theme.MangaXWTheme

class MainActivity : ComponentActivity() {
  private val repository = MangaRepository()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MangaApp(repository = repository)
    }
  }
}

enum class SubScreen {
  DETAILS,
  READER,
  DOWNLOADS,
  BACKUP
}

@Composable
fun MangaApp(repository: MangaRepository) {
  var currentTab by remember { mutableStateOf(AppNavTab.HOME) }
  var subScreen by remember { mutableStateOf<SubScreen?>(null) }
  var activeManga by remember { mutableStateOf<Manga?>(null) }
  var activeChapter by remember { mutableStateOf<Chapter?>(null) }

  // Theme settings
  var currentPalette by remember { mutableStateOf(AppThemePalette.NEON_BLUE) }
  var isDarkTheme by remember { mutableStateOf(true) }
  var isArabic by remember { mutableStateOf(true) }

  val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    MangaXWTheme(
      palette = currentPalette,
      darkTheme = isDarkTheme
    ) {
      val colors = LocalMangaColors.current
      val isReaderActive = subScreen == SubScreen.READER

      Scaffold(
        modifier = Modifier
          .fillMaxSize()
          .background(colors.background),
        bottomBar = {
          if (!isReaderActive) {
            BottomNavBar(
              currentTab = currentTab,
              onTabSelected = { tab ->
                currentTab = tab
                subScreen = null
              }
            )
          }
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(if (isReaderActive) androidx.compose.foundation.layout.PaddingValues() else innerPadding)
            .background(colors.background)
        ) {
          Crossfade(targetState = subScreen) { screen ->
            when (screen) {
              SubScreen.DETAILS -> {
                activeManga?.let { manga ->
                  DetailsScreen(
                    manga = manga,
                    repository = repository,
                    onBack = { subScreen = null },
                    onReadChapter = { chapter ->
                      activeChapter = chapter
                      subScreen = SubScreen.READER
                    }
                  )
                }
              }
              SubScreen.READER -> {
                val manga = activeManga ?: Manga(id = "current", title = "مانجا")
                val chapter = activeChapter ?: Chapter(
                  id = "ch_1",
                  mangaId = manga.id,
                  number = 1f,
                  title = "الفصل 1"
                )
                ReaderScreen(
                  manga = manga,
                  chapter = chapter,
                  repository = repository,
                  onBack = {
                    subScreen = if (activeManga != null) SubScreen.DETAILS else null
                  }
                )
              }
              SubScreen.DOWNLOADS -> {
                DownloadsScreen(
                  repository = repository,
                  onBack = { subScreen = null }
                )
              }
              SubScreen.BACKUP -> {
                BackupStorageScreen(
                  repository = repository,
                  onBack = { subScreen = null }
                )
              }
              null -> {
                when (currentTab) {
                  AppNavTab.HOME -> {
                    HomeScreen(
                      repository = repository,
                      onMangaClick = { manga ->
                        activeManga = manga
                        subScreen = SubScreen.DETAILS
                      },
                      onOpenReader = { manga ->
                        activeManga = manga
                        activeChapter = Chapter(
                          id = "ch_${manga.id}_${manga.lastReadChapterNumber ?: 1}",
                          mangaId = manga.id,
                          number = manga.lastReadChapterNumber ?: 1f,
                          title = manga.lastReadChapterTitle ?: "الفصل 1"
                        )
                        subScreen = SubScreen.READER
                      },
                      onOpenSources = { currentTab = AppNavTab.SOURCES },
                      onOpenLibrary = { currentTab = AppNavTab.LIBRARY },
                      onOpenDownloads = { subScreen = SubScreen.DOWNLOADS },
                      onOpenBackup = { subScreen = SubScreen.BACKUP }
                    )
                  }
                  AppNavTab.SOURCES -> {
                    SourcesScreen(repository = repository)
                  }
                  AppNavTab.LIBRARY -> {
                    LibraryScreen(
                      repository = repository,
                      onMangaClick = { manga ->
                        activeManga = manga
                        subScreen = SubScreen.DETAILS
                      },
                      onExploreClick = { currentTab = AppNavTab.HOME }
                    )
                  }
                  AppNavTab.READER -> {
                    // Open last read or first available manga in reader
                    val lastManga = repository.continueReading.value.firstOrNull()
                      ?: repository.trendingManga.value.firstOrNull()
                      ?: Manga(id = "default", title = "Manga XW")
                    activeManga = lastManga
                    activeChapter = Chapter(
                      id = "ch_last",
                      mangaId = lastManga.id,
                      number = lastManga.lastReadChapterNumber ?: 1f,
                      title = lastManga.lastReadChapterTitle ?: "الفصل 1"
                    )
                    subScreen = SubScreen.READER
                  }
                  AppNavTab.SETTINGS -> {
                    SettingsScreen(
                      currentPalette = currentPalette,
                      onPaletteChange = { currentPalette = it },
                      isDarkTheme = isDarkTheme,
                      onThemeModeChange = { isDarkTheme = it },
                      isArabic = isArabic,
                      onLanguageChange = { isArabic = it },
                      onOpenBackup = { subScreen = SubScreen.BACKUP }
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
