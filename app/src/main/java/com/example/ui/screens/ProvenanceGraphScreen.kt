package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseRepository
import com.example.ui.theme.*

@Composable
fun ProvenanceGraphScreen(
    repository: CaseRepository,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("provenance_graph_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Evidence Provenance & Graph",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Step-by-step transformation tracing: Raw Source -> Parsed Record -> Event -> Finding",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Provenance Transformation Pipeline Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Sample Provenance Chain: quantum_protocol_v3.pdf (EVD-011)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val stages = listOf(
                        Pair("1. Raw Artifact", "quantum_protocol_v3.pdf deposited in vault filesystem"),
                        Pair("2. Parsed Record", "Ingested as EVD-011 with verified SHA-256 (2e6c0ed1...)"),
                        Pair("3. Derived Event", "EVT-002: Dr. Elena Rostova deposited protocol draft"),
                        Pair("4. Rule Evaluation", "Compared with EVT-028 backup recovery in RULE-CONTR-002"),
                        Pair("5. Contradiction Flag", "FND-002: Cryptographic Hash Digest Mismatch"),
                        Pair("6. Investigation Report", "Synthesized into Section 4, 6, and 10 of Dossier")
                    )

                    stages.forEachIndexed { idx, stage ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Surface(
                                color = CyanAccentDim,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "${idx + 1}", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stage.first, color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = stage.second, color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        if (idx < stages.size - 1) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 11.dp)
                                    .width(2.dp)
                                    .height(12.dp)
                                    .background(NavyBorder)
                            )
                        }
                    }
                }
            }
        }

        // Network Topology Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Entity-Relationship Node Mapping",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mapped relationships across active actors and targets",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val nodeRelations = listOf(
                        Triple("Dr. Elena Rostova", "AUTHENTICATED_ON", "WS-LAB-04 (192.0.2.85)"),
                        Triple("Marcus Vance", "MANAGED_SERVICE", "SVR-VAULT-01 (192.0.2.1)"),
                        Triple("DevSecOps Daemon", "TRIGGERED_SYNC", "NAS-STORAGE-09 (203.0.113.15)"),
                        Triple("External IP (198.51.100.42)", "INITIATED_TUNNEL", "VPN Gateway (RADIUS 99201)"),
                        Triple("Nightly Sync Job", "UNLINKED_INODE", "quantum_protocol_v3.pdf (#482910)")
                    )

                    nodeRelations.forEach { rel ->
                        Surface(
                            color = NavySurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = rel.first, color = VioletAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = rel.second, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = rel.third, color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
