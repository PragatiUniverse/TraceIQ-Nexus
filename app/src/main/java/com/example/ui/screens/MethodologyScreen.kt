package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MethodologyScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("methodology_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Methodology & Epistemology",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Scientific rigor, falsifiability, and ethical guardrails in digital forensics",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        item {
            MethodologyCard(
                title = "1. Non-Accusatory Correlation",
                body = "In complex systems, anomalous patterns routinely arise from benign operational factors: Network Time Protocol (NTP) clock skew, delayed syslog buffer flushes, automated backup cron jobs, and failing storage sectors. TraceIQ Nexus enforces a cognitive barrier: a suspicious pattern is not proof of wrongdoing."
            )
        }

        item {
            MethodologyCard(
                title = "2. Analysis of Competing Hypotheses (ACH)",
                body = "Adapted from Richards Heuer's seminal intelligence methodology, TraceIQ Nexus treats alternative scenarios as equally subject to falsification. The platform tracks supporting and contradicting evidence independently, explicitly refusing to convert tally counts into probabilistic truth values."
            )
        }

        item {
            MethodologyCard(
                title = "3. Cryptographic Integrity vs Legal Chain of Custody",
                body = "A matching SHA-256 digest proves byte-level mathematical identity with a reference string. It does not establish the authenticity of the reference itself, the identity of the author, or compliance with court evidentiary standards (e.g., Federal Rules of Evidence 901/902)."
            )
        }

        item {
            MethodologyCard(
                title = "4. Ethical Simulation Scope",
                body = "Case TQN-2026-001 is 100% fictional. All IP addresses are from RFC 5737 documentation blocks (192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24). The system contains zero offensive malware or credential theft capabilities."
            )
        }
    }
}

@Composable
fun MethodologyCard(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = body, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}
