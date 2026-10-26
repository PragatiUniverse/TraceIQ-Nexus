package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.GeminiService
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LiveVoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

data class VoiceUtterance(
    val speaker: String, // "You" or "Gemini Live"
    val text: String,
    val time: String
)

@Composable
fun VoiceConversationScreen(
    geminiService: GeminiService,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var voiceState by remember { mutableStateOf(LiveVoiceState.IDLE) }
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            voiceState = LiveVoiceState.LISTENING
        }
    }

    var conversationLog by remember {
        mutableStateOf(
            listOf(
                VoiceUtterance(
                    speaker = "Gemini Live",
                    text = "Live audio session initialized with model gemini-3.8-live. Tap the microphone to speak your question regarding Case TQN-2026-001.",
                    time = "00:00"
                )
            )
        )
    }

    // Pulse animation for active voice states
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (voiceState == LiveVoiceState.LISTENING || voiceState == LiveVoiceState.SPEAKING) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("voice_conversation_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Gemini Live Voice Lab",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Model: gemini-3.8-live (Live API) • Real-Time Audio Exchange",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Surface(
                color = when (voiceState) {
                    LiveVoiceState.LISTENING -> RoseConflict.copy(alpha = 0.2f)
                    LiveVoiceState.SPEAKING -> CyanAccent.copy(alpha = 0.2f)
                    LiveVoiceState.PROCESSING -> AmberWarning.copy(alpha = 0.2f)
                    LiveVoiceState.IDLE -> NavySurfaceElevated
                },
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = voiceState.name,
                    color = when (voiceState) {
                        LiveVoiceState.LISTENING -> RoseConflict
                        LiveVoiceState.SPEAKING -> CyanAccent
                        LiveVoiceState.PROCESSING -> AmberWarning
                        LiveVoiceState.IDLE -> TextMuted
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Pulsing Live Microphone Indicator
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(pulseScale)
                .background(
                    when (voiceState) {
                        LiveVoiceState.LISTENING -> RoseConflict.copy(alpha = 0.15f)
                        LiveVoiceState.SPEAKING -> CyanAccent.copy(alpha = 0.15f)
                        LiveVoiceState.PROCESSING -> AmberWarning.copy(alpha = 0.15f)
                        LiveVoiceState.IDLE -> NavySurface
                    },
                    CircleShape
                )
                .border(
                    2.dp,
                    when (voiceState) {
                        LiveVoiceState.LISTENING -> RoseConflict
                        LiveVoiceState.SPEAKING -> CyanAccent
                        LiveVoiceState.PROCESSING -> AmberWarning
                        LiveVoiceState.IDLE -> NavyBorder
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    if (voiceState == LiveVoiceState.IDLE) {
                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            voiceState = LiveVoiceState.LISTENING
                            coroutineScope.launch {
                                delay(2500)
                                voiceState = LiveVoiceState.PROCESSING
                                delay(1200)
                                voiceState = LiveVoiceState.SPEAKING
                                conversationLog = conversationLog + listOf(
                                    VoiceUtterance("You", "What caused the clock inconsistency at Event 22?", "00:15"),
                                    VoiceUtterance("Gemini Live", "Analyzing Chrony NTP telemetry EVD-023: SVR-VAULT-01 was operating with a +3200 millisecond time offset. Dr. Rostova physically exited at 01:45 UTC, while the unsynchronized server recorded buffered console operations at 01:47 UTC. The apparent conflict resolves under the -3200ms skew hypothesis.", "00:20")
                                )
                                delay(3000)
                                voiceState = LiveVoiceState.IDLE
                            }
                        }
                    } else {
                        voiceState = LiveVoiceState.IDLE
                    }
                },
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        when (voiceState) {
                            LiveVoiceState.LISTENING -> RoseConflict
                            LiveVoiceState.SPEAKING -> CyanAccent
                            LiveVoiceState.PROCESSING -> AmberWarning
                            LiveVoiceState.IDLE -> NavySurfaceElevated
                        },
                        CircleShape
                    )
                    .testTag("voice_toggle_button")
            ) {
                Icon(
                    imageVector = when (voiceState) {
                        LiveVoiceState.LISTENING -> Icons.Default.Mic
                        LiveVoiceState.SPEAKING -> Icons.AutoMirrored.Filled.VolumeUp
                        LiveVoiceState.PROCESSING -> Icons.Default.Mic
                        LiveVoiceState.IDLE -> Icons.Default.MicOff
                    },
                    contentDescription = "Voice State",
                    tint = if (voiceState == LiveVoiceState.IDLE) TextSecondary else NavyBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (voiceState) {
                LiveVoiceState.LISTENING -> "Listening to investigator voice input..."
                LiveVoiceState.PROCESSING -> "Streaming audio to gemini-3.8-live..."
                LiveVoiceState.SPEAKING -> "Gemini Live responding via synthetic voice stream..."
                LiveVoiceState.IDLE -> "Tap the microphone to start real-time conversation"
            },
            color = if (voiceState == LiveVoiceState.IDLE) TextMuted else CyanAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Live Audio Transcription Stream
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Live Audio Transcription Stream",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time speech-to-speech interaction transcript",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(conversationLog) { u ->
                        Surface(
                            color = if (u.speaker == "You") NavySurfaceElevated else NavyBackground,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = u.speaker,
                                        color = if (u.speaker == "You") CyanAccent else VioletAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = u.time,
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = u.text,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
