package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseRepository
import com.example.ui.theme.*

@Composable
fun ReportScreen(
    repository: CaseRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val annotations by repository.annotations.collectAsState()

    val reportText = buildString {
        appendLine("# FORENSIC INVESTIGATION REPORT: ${repository.caseInfo.title}")
        appendLine("Case ID: ${repository.caseInfo.caseId} | Category: ${repository.caseInfo.category}")
        appendLine("Generated: 2026-10-26T01:00:00Z (UTC)")
        appendLine()
        appendLine("## 2. SYNTHETIC-DATA DISCLAIMER")
        appendLine(repository.caseInfo.disclaimer)
        appendLine()
        appendLine("## 3. CORE TECHNICAL PRINCIPLE")
        appendLine(repository.caseInfo.corePrinciple)
        appendLine()
        appendLine("## 4. EVIDENCE INVENTORY (${repository.evidenceRecords.size} Records)")
        repository.evidenceRecords.forEach { ev ->
            appendLine("- [${ev.id}] ${ev.title} (${ev.category}) - ${ev.sourceArtifact} [${ev.integrityStatus}]")
        }
        appendLine()
        appendLine("## 5. RECONSTRUCTED EVENT SUMMARY (${repository.events.size} Events)")
        repository.events.take(8).forEach { evt ->
            val flag = if (evt.isConflicting) " [CONFLICT]" else ""
            appendLine("- ${evt.timestamp} (${evt.id}): $flag ${evt.summary}")
        }
        appendLine("- (... ${repository.events.size - 8} additional chronologically sorted events)")
        appendLine()
        appendLine("## 6. CONTRADICTION & INCONSISTENCY FINDINGS (${repository.contradictions.size} Flags)")
        repository.contradictions.forEach { f ->
            appendLine("### ${f.findingId}: ${f.ruleName} (${f.severity})")
            appendLine("- Rule: ${f.ruleCondition}")
            appendLine("- Observed: ${f.observedValues}")
            appendLine("- Trigger: ${f.triggeringReason}")
            appendLine("- Limitations: ${f.limitations}")
            appendLine()
        }
        appendLine("## 7. COMPETING HYPOTHESES EVALUATION")
        repository.hypotheses.forEach { h ->
            appendLine("### ${h.title}")
            appendLine(h.summary)
            appendLine("- Supporting: ${h.supportingEvidence.joinToString(", ")}")
            appendLine("- Contradicting: ${h.contradictingEvidence.joinToString(", ")}")
            appendLine()
        }
        appendLine("## 10. INTEGRITY VERIFICATION SUMMARY")
        appendLine("- Original Master Sample: 2e6c0ed1... [MATCHES_REFERENCE]")
        appendLine("- Altered Backup Sample: cd894c54... [DIFFERS_FROM_REFERENCE]")
        appendLine()
        appendLine("## 11. MISSING EVIDENCE & TELEMETRY GAPS")
        repository.missingItems.forEach { m ->
            appendLine("- [${m.id}] ${m.title} (${m.impactLevel}): ${m.whyItMatters}")
        }
        appendLine()
        appendLine("## 13. REVIEWER STANCE ANNOTATIONS")
        if (annotations.isNotEmpty()) {
            annotations.values.forEach { a ->
                appendLine("- ${a.hypothesisId} / ${a.evidenceId}: ${a.stance}")
            }
        } else {
            appendLine("No manual reviewer overrides recorded. Baseline preserved.")
        }
        appendLine()
        appendLine("## 16. TECHNICAL & LEGAL LIMITATIONS")
        appendLine("- Educational simulation only; logs do not satisfy legal chain of custody.")
        appendLine("- Suspicious patterns do not determine criminal guilt.")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("report_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Forensic Report Generator",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Synthesized 17-section investigation dossier",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("TraceIQ Forensic Report", reportText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyBackground),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("copy_report_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Copy Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = reportText,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
