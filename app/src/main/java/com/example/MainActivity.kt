package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseRepository
import com.example.data.NavDestination
import com.example.service.GeminiService
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val repository by lazy { CaseRepository.createWithDatabase(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TraceIQNexusTheme {
                TraceIQApp(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceIQApp(repository: CaseRepository) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    val geminiService = remember { GeminiService() }

    // Android back button handling
    BackHandler(enabled = currentDestination != NavDestination.DASHBOARD) {
        currentDestination = NavDestination.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NavyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TRACEIQ NEXUS",
                            color = CyanAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = CyanAccentDim,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "TQN-2026-001",
                                color = CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentDestination = NavDestination.GEMINI_CHAT },
                        modifier = Modifier.testTag("open_gemini_chat_action")
                    ) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = "AI Copilot",
                            tint = if (currentDestination == NavDestination.GEMINI_CHAT) CyanAccent else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = NavDestination.VOICE_LIVE },
                        modifier = Modifier.testTag("open_voice_live_action")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Live Voice Lab",
                            tint = if (currentDestination == NavDestination.VOICE_LIVE) EmeraldVerify else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { repository.resetCase() },
                        modifier = Modifier.testTag("reset_demo_action")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Demo", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBackground,
                    titleContentColor = CyanAccent
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavySurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                val primaryNavItems = listOf(
                    NavDestination.DASHBOARD,
                    NavDestination.TIMELINE,
                    NavDestination.HYPOTHESES,
                    NavDestination.CONTRADICTIONS,
                    NavDestination.GEMINI_CHAT,
                    NavDestination.VOICE_LIVE,
                    NavDestination.REPORT
                )

                primaryNavItems.forEach { dest ->
                    val isSelected = currentDestination == dest
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = dest },
                        icon = {
                            val iconVector = when (dest) {
                                NavDestination.DASHBOARD -> Icons.Default.Dashboard
                                NavDestination.TIMELINE -> Icons.Default.Schedule
                                NavDestination.HYPOTHESES -> Icons.Default.Psychology
                                NavDestination.CONTRADICTIONS -> Icons.Default.Warning
                                NavDestination.INTEGRITY -> Icons.Default.Lock
                                NavDestination.CHALLENGE -> Icons.Default.EmojiEvents
                                NavDestination.REPORT -> Icons.Default.Description
                                NavDestination.GEMINI_CHAT -> Icons.Default.SmartToy
                                NavDestination.VOICE_LIVE -> Icons.Default.Mic
                                else -> Icons.Default.Info
                            }
                            Icon(iconVector, contentDescription = dest.label)
                        },
                        label = {
                            Text(
                                text = dest.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyBackground,
                            selectedTextColor = CyanAccent,
                            indicatorColor = CyanAccent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${dest.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavDestination.DASHBOARD -> DashboardScreen(
                    repository = repository,
                    onNavigate = { currentDestination = it }
                )
                NavDestination.TIMELINE -> TimeMachineScreen(
                    repository = repository
                )
                NavDestination.HYPOTHESES -> HypothesisLabScreen(
                    repository = repository
                )
                NavDestination.CONTRADICTIONS -> ContradictionHunterScreen(
                    repository = repository
                )
                NavDestination.INTEGRITY -> IntegrityScreen(
                    repository = repository
                )
                NavDestination.PROVENANCE -> ProvenanceGraphScreen(
                    repository = repository
                )
                NavDestination.MISSING -> MissingEvidenceScreen(
                    repository = repository
                )
                NavDestination.CHALLENGE -> ChallengeScreen(
                    repository = repository
                )
                NavDestination.REPORT -> ReportScreen(
                    repository = repository
                )
                NavDestination.METHODOLOGY -> MethodologyScreen()
                NavDestination.GEMINI_CHAT -> GeminiChatScreen(
                    geminiService = geminiService
                )
                NavDestination.VOICE_LIVE -> VoiceConversationScreen(
                    geminiService = geminiService
                )
            }
        }
    }
}
