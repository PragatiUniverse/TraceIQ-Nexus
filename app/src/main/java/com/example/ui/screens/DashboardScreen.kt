package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseRepository
import com.example.data.Entity
import com.example.data.NavDestination
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    repository: CaseRepository,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val caseInfo = repository.caseInfo
    val entities = repository.entities

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .testTag("dashboard_hero_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = CyanAccentDim,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = caseInfo.caseId,
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            color = VioletAccentDim,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = caseInfo.status,
                                color = VioletAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = caseInfo.title,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Connect the Clues. Challenge the Story. Verify the Evidence.",
                        color = CyanAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = NavySurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Core Principle",
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = caseInfo.corePrinciple,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "EVIDENCE",
                    value = "${repository.evidenceRecords.size}",
                    color = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "EVENTS",
                    value = "${repository.events.size}",
                    color = VioletAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "CONTRADICTIONS",
                    value = "${repository.contradictions.size}",
                    color = RoseConflict,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "HYPOTHESES",
                    value = "${repository.hypotheses.size}",
                    color = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions Grid
        item {
            Text(
                text = "INVESTIGATION WORKSPACES",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(
                        title = "AI Copilot",
                        subtitle = "Multi-turn Gemini with role instructions",
                        icon = Icons.Default.SmartToy,
                        tint = CyanAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.GEMINI_CHAT) }
                    )
                    ActionTile(
                        title = "Live Voice Lab",
                        subtitle = "Real-time gemini-3.8-live audio analysis",
                        icon = Icons.Default.Mic,
                        tint = EmeraldVerify,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.VOICE_LIVE) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(
                        title = "Time Machine",
                        subtitle = "Replay chronological event stream",
                        icon = Icons.Default.Schedule,
                        tint = CyanAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.TIMELINE) }
                    )
                    ActionTile(
                        title = "Hypothesis Lab",
                        subtitle = "ACH competing scenario testing",
                        icon = Icons.Default.Psychology,
                        tint = VioletAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.HYPOTHESES) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(
                        title = "Contradictions",
                        subtitle = "7 rule-based inconsistency flags",
                        icon = Icons.Default.Warning,
                        tint = RoseConflict,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.CONTRADICTIONS) }
                    )
                    ActionTile(
                        title = "SHA-256 Hashes",
                        subtitle = "FIPS 180-4 cryptographic check",
                        icon = Icons.Default.Lock,
                        tint = EmeraldVerify,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.INTEGRITY) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(
                        title = "Challenge Lab",
                        subtitle = "Interactive forensic CUJ challenge tasks",
                        icon = Icons.Default.EmojiEvents,
                        tint = AmberWarning,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.CHALLENGE) }
                    )
                    ActionTile(
                        title = "Evidence Graph",
                        subtitle = "Relational chain-of-custody nodes",
                        icon = Icons.Default.AccountTree,
                        tint = VioletAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(NavDestination.PROVENANCE) }
                    )
                }
            }
        }

        // Entities List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DOCUMENTED ENTITIES & HOSTS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${entities.size} Identifiers",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(entities) { entity ->
            EntityCard(entity)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .testTag("action_tile_${title.lowercase().replace(' ', '_')}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
fun EntityCard(entity: Entity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (entity.type == "PERSONNEL") VioletAccentDim else CyanAccentDim,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (entity.type == "PERSONNEL") Icons.Default.Person else Icons.Default.Computer,
                        contentDescription = entity.type,
                        tint = if (entity.type == "PERSONNEL") VioletAccent else CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entity.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = entity.role, color = TextSecondary, fontSize = 11.sp)
                Text(
                    text = "${entity.workstation} • IP: ${entity.ipAddress}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
