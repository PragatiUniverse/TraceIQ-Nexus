package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import com.example.service.ChatMessage
import com.example.service.GeminiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(
    geminiService: GeminiService,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var selectedModel by remember { mutableStateOf("gemini-3.8-flash") }
    var selectedRole by remember { mutableStateOf("Lead Forensic Investigator") }

    val roleInstructions = mapOf(
        "Lead Forensic Investigator" to "You are the TraceIQ Nexus Senior Forensic AI Investigator. You provide objective, rigorous, evidence-linked analysis of Case TQN-2026-001 (The Missing Research Files). You never assume guilt without verifiable telemetry.",
        "Hostile Defense Counsel" to "You are a defense attorney rigorously challenging every piece of digital evidence. You highlight alternative non-malicious explanations, clock skew, and missing logs to defend against premature accusations.",
        "Incident Response Commander" to "You are an executive Incident Response Commander focused on containment, business continuity, forensic write-blocking, and regulatory disclosure requirements."
    )

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    role = "model",
                    content = "Hello Investigator. I am your TraceIQ Nexus Forensic AI Assistant. You can ask me to cross-examine evidence records, explain flagged contradictions, compare Hypothesis A vs B, or formulate next verification steps for Case TQN-2026-001.",
                    modelUsed = "gemini-3.8-flash"
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isKeyReady = geminiService.isApiKeyConfigured()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .testTag("gemini_chat_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Gemini Forensic Assistant",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Multi-Turn Investigation Copilot • Case TQN-2026-001",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Surface(
                color = if (isKeyReady) EmeraldVerify.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (isKeyReady) "API Key Configured" else "Demo Sandbox Mode",
                    color = if (isKeyReady) EmeraldVerify else AmberWarning,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Model & Role Selectors
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Model Selector
            val models = listOf(
                "gemini-3.8-flash" to "General (3.8-flash)",
                "gemini-4-argon" to "Complex (4-argon)",
                "gemini-3.1-flash-lite" to "Fast (3.1-lite)"
            )
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(models) { (modelKey, label) ->
                    val isSelected = selectedModel == modelKey
                    OutlinedButton(
                        onClick = { selectedModel = modelKey },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) CyanAccent.copy(alpha = 0.2f) else Color.Transparent,
                            contentColor = if (isSelected) CyanAccent else TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanAccent else NavyBorder),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pre-canned Prompts
        val suggestedPrompts = listOf(
            "Analyze the 7 contradictions",
            "Evaluate Hypothesis A vs B",
            "Explain Chrony NTP skew on SVR-01",
            "Why does EVD-099 matter?"
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(suggestedPrompts) { prompt ->
                Surface(
                    color = NavySurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier
                ) {
                    TextButton(
                        onClick = { inputText = prompt },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = prompt, color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) NavySurfaceElevated else NavySurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .border(
                                1.dp,
                                if (isUser) CyanAccent.copy(alpha = 0.5f) else VioletAccent.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isUser) "Investigator" else "Gemini AI",
                                    color = if (isUser) CyanAccent else VioletAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                msg.modelUsed?.let {
                                    Text(
                                        text = it,
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.content,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = CyanAccent,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini is analyzing forensic telemetry...",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        errorMessage?.let { err ->
            Surface(
                color = RoseConflict.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = err,
                    color = RoseConflict,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field and Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Gemini about Case TQN-2026-001...", color = TextMuted, fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("gemini_chat_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val prompt = inputText.trim()
                    if (prompt.isNotBlank() && !isLoading) {
                        val userMsg = ChatMessage(role = "user", content = prompt)
                        val updatedList = messages + userMsg
                        messages = updatedList
                        inputText = ""
                        isLoading = true
                        errorMessage = null

                        coroutineScope.launch {
                            listState.animateScrollToItem(updatedList.size - 1)

                            if (isKeyReady) {
                                val sysInstruction = roleInstructions[selectedRole] ?: ""
                                val result = geminiService.sendMultiTurnChat(
                                    messages = updatedList,
                                    model = selectedModel,
                                    systemInstruction = sysInstruction
                                )
                                isLoading = false
                                result.fold(
                                    onSuccess = { reply ->
                                        messages = messages + ChatMessage(
                                            role = "model",
                                            content = reply,
                                            modelUsed = selectedModel
                                        )
                                        listState.animateScrollToItem(messages.size - 1)
                                    },
                                    onFailure = { ex ->
                                        errorMessage = ex.message
                                    }
                                )
                            } else {
                                // Deterministic offline fallback for sandbox demonstration
                                kotlinx.coroutines.delay(800)
                                isLoading = false
                                val fallbackReply = when {
                                    prompt.contains("contradiction", ignoreCase = true) ->
                                        "TraceIQ Nexus has detected 7 deterministic contradiction rules. Crucially, RULE-CONTR-001 flags that Dr. Elena Rostova's physical badge exit occurred at 01:45 UTC, while console commands were recorded at 01:47 UTC. However, Chrony NTP log EVD-023 reveals a +3200ms clock skew on SVR-VAULT-01, explaining the apparent sequence inversion as a benign synchronization issue rather than malicious credential theft."
                                    prompt.contains("hypothesis", ignoreCase = true) ->
                                        "Hypothesis A attributes the missing file to an authorized maintenance rsync job by Marcus Vance compounded by disk sector failures (/dev/sdb). Hypothesis B proposes an unrecognized access sequence via external IP 198.51.100.42. Because the secondary switch PCAP (EVD-099) is missing, neither hypothesis can be definitively established without further network tap acquisition."
                                    prompt.contains("hash", ignoreCase = true) || prompt.contains("sha", ignoreCase = true) ->
                                        "The original master document quantum_protocol_v3.pdf matches reference digest 2e6c0ed1..., while the recovered backup replica quantum_protocol_v3_tampered.pdf calculates to cd894c54... A cryptographic hash mismatch proves byte-level differences, but does not distinguish between malicious tampering and disk sector corruption."
                                    else ->
                                        "In Case TQN-2026-001, every finding links to verifiable evidence records. To enable live API responses with model $selectedModel, configure your GEMINI_API_KEY in the Secrets panel in AI Studio."
                                }
                                messages = messages + ChatMessage(
                                    role = "model",
                                    content = fallbackReply,
                                    modelUsed = "$selectedModel (Sandbox)"
                                )
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = CyanAccent,
                    contentColor = NavyBackground
                ),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("send_chat_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
            }
        }
    }
}
