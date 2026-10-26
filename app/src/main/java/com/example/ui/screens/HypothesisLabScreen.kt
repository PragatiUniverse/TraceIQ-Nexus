package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseRepository
import com.example.data.Hypothesis
import com.example.ui.theme.*

@Composable
fun HypothesisLabScreen(
    repository: CaseRepository,
    modifier: Modifier = Modifier
) {
    val hypotheses = repository.hypotheses
    var selectedHypothesisId by remember { mutableStateOf(hypotheses.firstOrNull()?.id ?: "HYP-A") }
    val annotations by repository.annotations.collectAsState()

    val currentHyp = hypotheses.find { it.id == selectedHypothesisId } ?: hypotheses.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("hypothesis_lab_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Multiple-Hypothesis Lab",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Analysis of Competing Hypotheses (ACH) • Falsification Workflow",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Epistemological Warning Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Notice", tint = CyanAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cognitive Guardrail: Support counts reflect documented consistency with a scenario, NOT mathematical probability of truth. Every hypothesis must be challenged equally.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Hypothesis Switcher Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                hypotheses.forEach { hyp ->
                    val isSelected = hyp.id == selectedHypothesisId
                    Button(
                        onClick = { selectedHypothesisId = hyp.id },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) CyanAccent else NavySurface,
                            contentColor = if (isSelected) NavyBackground else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) CyanAccent else NavyBorder, RoundedCornerShape(8.dp))
                            .testTag("tab_${hyp.id.lowercase()}")
                    ) {
                        Text(
                            text = hyp.id,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Current Hypothesis Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentHyp.title,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentHyp.summary,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "SUPPORTING EVIDENCE (${currentHyp.supportingEvidence.size})",
                        color = EmeraldVerify,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(currentHyp.supportingEvidence) { eid ->
                            Surface(
                                color = EmeraldVerify.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldVerify.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = eid,
                                    color = EmeraldVerify,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "CONTRADICTING EVIDENCE (${currentHyp.contradictingEvidence.size})",
                        color = RoseConflict,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(currentHyp.contradictingEvidence) { eid ->
                            Surface(
                                color = RoseConflict.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoseConflict.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = eid,
                                    color = RoseConflict,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "KEY ASSUMPTIONS",
                        color = AmberWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    currentHyp.assumptions.forEach { asm ->
                        Text(
                            text = "• $asm",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "UNANSWERED QUESTIONS",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    currentHyp.unansweredQuestions.forEach { q ->
                        Text(
                            text = "? $q",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Reviewer Interactive Stance Annotation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VioletAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Interactive Reviewer Stance",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Record analytical evaluation for ${currentHyp.id} on critical artifact EVD-011",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    val currentStance = annotations["${currentHyp.id}:EVD-011"]?.stance ?: "UNSET"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StanceButton("SUPPORTS", EmeraldVerify, currentStance == "SUPPORTS", Modifier.weight(1f)) {
                            repository.annotateEvidence(currentHyp.id, "EVD-011", "SUPPORTS")
                        }
                        StanceButton("CONTRADICTS", RoseConflict, currentStance == "CONTRADICTS", Modifier.weight(1f)) {
                            repository.annotateEvidence(currentHyp.id, "EVD-011", "CONTRADICTS")
                        }
                        StanceButton("NEUTRAL", TextSecondary, currentStance == "NEUTRAL", Modifier.weight(1f)) {
                            repository.annotateEvidence(currentHyp.id, "EVD-011", "NEUTRAL")
                        }
                        StanceButton("REVIEW", AmberWarning, currentStance == "REQUIRES_REVIEW", Modifier.weight(1f)) {
                            repository.annotateEvidence(currentHyp.id, "EVD-011", "REQUIRES_REVIEW")
                        }
                    }

                    if (currentStance != "UNSET") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Recorded reviewer stance: $currentStance",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StanceButton(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
            contentColor = if (isSelected) color else TextSecondary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else NavyBorder),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        modifier = modifier.height(34.dp)
    ) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
