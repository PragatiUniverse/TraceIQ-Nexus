package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseRepository
import com.example.data.TimelineEvent
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TimeMachineScreen(
    repository: CaseRepository,
    modifier: Modifier = Modifier
) {
    val events = repository.events
    val useHypothetical by repository.useHypothetical.collectAsState()
    val scrubIndex by repository.scrubIndex.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying, scrubIndex) {
        if (isPlaying) {
            delay(1200)
            if (scrubIndex < events.size - 1) {
                repository.setScrubIndex(scrubIndex + 1)
            } else {
                isPlaying = false
            }
        }
    }

    val activeEvent = events.getOrNull(scrubIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("time_machine_screen")
    ) {
        // Top Title & Hypothetical Model Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Evidence Time Machine",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Causal sequence replay & NTP offset modeling",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hypothetical Toggle Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (useHypothetical) NavySurfaceElevated else NavySurface
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (useHypothetical) AmberWarning else NavyBorder,
                    RoundedCornerShape(10.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hypothetical NTP Skew Model (-3200ms)",
                        color = if (useHypothetical) AmberWarning else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Realigns SVR-VAULT-01 events with stratum 1 clock without altering raw logs",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Switch(
                    checked = useHypothetical,
                    onCheckedChange = { repository.toggleHypothetical(it) },
                    modifier = Modifier.testTag("hypothetical_model_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Replay Controls Card
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                isPlaying = false
                                repository.setScrubIndex(scrubIndex - 1)
                            },
                            enabled = scrubIndex > 0,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.SkipPrevious, "Previous", tint = CyanAccent)
                        }
                        Button(
                            onClick = { isPlaying = !isPlaying },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) AmberWarning else CyanAccent,
                                contentColor = NavyBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp).testTag("play_pause_button")
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPlaying) "Pause" else "Replay", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        IconButton(
                            onClick = {
                                isPlaying = false
                                repository.setScrubIndex(scrubIndex + 1)
                            },
                            enabled = scrubIndex < events.size - 1,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.SkipNext, "Next", tint = CyanAccent)
                        }
                    }

                    Text(
                        text = "Event ${scrubIndex + 1} of ${events.size}",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = scrubIndex.toFloat(),
                    onValueChange = {
                        isPlaying = false
                        repository.setScrubIndex(it.toInt())
                    },
                    valueRange = 0f..(events.size - 1).toFloat(),
                    steps = events.size - 2,
                    modifier = Modifier.testTag("timeline_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Event Inspector
        activeEvent?.let { evt ->
            val effectiveTime = if (useHypothetical && evt.hypotheticalTimestamp != null) {
                evt.hypotheticalTimestamp
            } else {
                evt.timestamp
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (evt.isConflicting) RoseConflict else CyanAccent,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (evt.isConflicting) RoseConflict else CyanAccent,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = evt.id,
                                    color = NavyBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = VioletAccentDim,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = evt.eventType,
                                    color = VioletAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (evt.isConflicting) {
                            Surface(
                                color = RoseConflict.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "⚠️ CONFLICT FLAGGED",
                                    color = RoseConflict,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = evt.summary,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = evt.details,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Timestamp: $effectiveTime",
                            color = if (useHypothetical && evt.hypotheticalTimestamp != null) AmberWarning else CyanAccent,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Ref: ${evt.evidenceId}",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Feed of Replayed Events
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val visibleEvents = events.take(scrubIndex + 1)
            itemsIndexed(visibleEvents) { index, evt ->
                val isCurrent = index == scrubIndex
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) NavySurfaceElevated else NavySurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isCurrent) CyanAccent else NavyBorder,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (evt.isConflicting) RoseConflict else CyanAccent,
                                    RoundedCornerShape(50)
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = evt.summary,
                                color = if (isCurrent) CyanAccent else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = "${evt.timestamp} • ${evt.evidenceId}",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
