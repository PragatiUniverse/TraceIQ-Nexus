package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.ChallengeTask
import com.example.ui.theme.*

@Composable
fun ChallengeScreen(
    repository: CaseRepository,
    modifier: Modifier = Modifier
) {
    val tasks by repository.tasks.collectAsState()
    val totalScore = tasks.sumOf { it.points }
    val currentScore = tasks.filter { it.isCompleted }.sumOf { it.points }
    val completedCount = tasks.count { it.isCompleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("challenge_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Score Progress Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = AmberWarning, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Investigation Challenge",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "The Missing Research Files (TQN-2026-001)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { repository.resetChallenge() },
                            modifier = Modifier.testTag("reset_challenge_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Challenge", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Score: $currentScore / $totalScore pts",
                            color = CyanAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "$completedCount of ${tasks.size} Tasks Done",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (totalScore > 0) currentScore.toFloat() / totalScore.toFloat() else 0f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = CyanAccent,
                        trackColor = NavySurfaceElevated,
                    )
                }
            }
        }

        // Disclaimer Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Educational Notice: Challenge score measures task completion in the demonstration workflow, not individual forensic investigator competence or real-world legal guilt.",
                    color = TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        items(tasks) { task ->
            ChallengeTaskCard(
                task = task,
                onComplete = { repository.completeTask(task.id, "Verified in live Android lab session") }
            )
        }
    }
}

@Composable
fun ChallengeTaskCard(
    task: ChallengeTask,
    onComplete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (task.isCompleted) EmeraldVerify else NavyBorder,
                RoundedCornerShape(12.dp)
            )
            .testTag("challenge_task_${task.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (task.isCompleted) EmeraldVerify.copy(alpha = 0.2f) else CyanAccentDim,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = task.id,
                        color = if (task.isCompleted) EmeraldVerify else CyanAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = if (task.isCompleted) EmeraldVerify else VioletAccent,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (task.isCompleted) "✓ ${task.points} PTS" else "+${task.points} PTS",
                        color = if (task.isCompleted) NavyBackground else androidx.compose.ui.graphics.Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = task.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = task.description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = NavySurfaceElevated,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hint: ${task.hint}",
                    color = AmberWarning,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Key: ${task.verificationKey}",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (!task.isCompleted) {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyBackground),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("complete_button_${task.id.lowercase()}")
                    ) {
                        Text(text = "Verify Task", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = EmeraldVerify, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Completed", color = EmeraldVerify, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
