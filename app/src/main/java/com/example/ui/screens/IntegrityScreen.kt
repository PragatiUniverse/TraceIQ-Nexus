package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.IntegrityVerificationResult
import com.example.ui.theme.*

@Composable
fun IntegrityScreen(
    repository: CaseRepository,
    modifier: Modifier = Modifier
) {
    var activeResult by remember { mutableStateOf<IntegrityVerificationResult?>(null) }
    var customText by remember { mutableStateOf("") }
    var customRefHash by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("integrity_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "SHA-256 Integrity Verification",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "FIPS 180-4 Cryptographic Hash Engine • Real MessageDigest Calculation",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Active Verification Result Card
        activeResult?.let { res ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (res.isMatch == true) EmeraldVerify else RoseConflict,
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (res.isMatch == true) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = "Status",
                                    tint = if (res.isMatch == true) EmeraldVerify else RoseConflict,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = res.filename,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                color = if (res.isMatch == true) EmeraldVerify.copy(alpha = 0.2f) else RoseConflict.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = res.statusText,
                                    color = if (res.isMatch == true) EmeraldVerify else RoseConflict,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "CALCULATED SHA-256:",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = res.calculatedSha256,
                            color = CyanAccent,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "REFERENCE MANIFEST SHA-256:",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = res.referenceSha256 ?: "None provided",
                            color = if (res.isMatch == true) EmeraldVerify else RoseConflict,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = res.forensicGuidance,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Built-in Synthetic Samples
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
                        text = "Built-in Sample Evidence Checks",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Test the authentic master specification against the recovered backup replica",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavySurfaceElevated, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "quantum_protocol_v3.pdf",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Original reference sample deposited in vault",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Button(
                            onClick = {
                                activeResult = repository.verifySampleOriginal()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyBackground),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("verify_original_button")
                        ) {
                            Text(text = "Verify Authentic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavySurfaceElevated, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "quantum_protocol_v3_tampered.pdf",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Altered recovered backup with modified header",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Button(
                            onClick = {
                                activeResult = repository.verifySampleTampered()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoseConflict, contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("verify_tampered_button")
                        ) {
                            Text(text = "Verify Tampered", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Custom Hash Calculator
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
                        text = "Live String Digest Calculator",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Compute real SHA-256 for arbitrary text or verify against expected digest",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { customText = it },
                        placeholder = { Text("Enter string or payload...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("custom_hash_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customRefHash,
                        onValueChange = { customRefHash = it },
                        placeholder = { Text("Expected 64-char reference hash (optional)...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VioletAccent,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val calculated = repository.calculateSha256(customText)
                            val ref = customRefHash.trim().takeIf { it.isNotEmpty() }
                            val isMatch = ref?.equals(calculated, ignoreCase = true)
                            activeResult = IntegrityVerificationResult(
                                actionId = "CUSTOM-${System.currentTimeMillis() % 10000}",
                                filename = "User_Text_Input",
                                calculatedSha256 = calculated,
                                referenceSha256 = ref,
                                matchStatus = if (isMatch == true) "MATCHES_REFERENCE" else if (isMatch == false) "DIFFERS_FROM_REFERENCE" else "REFERENCE_UNAVAILABLE",
                                statusText = if (isMatch == true) "Matches reference exactly" else if (isMatch == false) "Differs from reference" else "Computed SHA-256",
                                isMatch = isMatch,
                                verifiedAt = "Live Calculation",
                                forensicGuidance = "Calculated via JVM java.security.MessageDigest SHA-256."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VioletAccent, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("calculate_sha256_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Calculate & Verify SHA-256", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
