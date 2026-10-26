package com.example.data

enum class NavDestination(val label: String, val icon: String) {
    DASHBOARD("Overview", "dashboard"),
    TIMELINE("Time Machine", "timeline"),
    HYPOTHESES("Hypotheses", "psychology"),
    CONTRADICTIONS("Contradictions", "warning"),
    INTEGRITY("SHA-256", "lock"),
    PROVENANCE("Provenance", "account_tree"),
    MISSING("Missing Data", "help_outline"),
    CHALLENGE("Challenge", "emoji_events"),
    REPORT("Report", "description"),
    METHODOLOGY("Methodology", "menu_book"),
    GEMINI_CHAT("AI Copilot", "smart_toy"),
    VOICE_LIVE("Live Voice", "mic")
}

data class CaseInfo(
    val caseId: String,
    val title: String,
    val category: String,
    val status: String,
    val createdAt: String,
    val disclaimer: String,
    val corePrinciple: String
)

data class Entity(
    val id: String,
    val name: String,
    val role: String,
    val type: String,
    val badgeId: String,
    val workstation: String,
    val ipAddress: String,
    val notes: String
)

data class EvidenceRecord(
    val id: String,
    val sourceArtifact: String,
    val title: String,
    val category: String,
    val collectionTimestamp: String,
    val importTimestamp: String,
    val timezoneStatus: String,
    val referenceHash: String,
    val calculatedHash: String,
    val integrityStatus: String,
    val description: String
)

data class TimelineEvent(
    val id: String,
    val timestamp: String,
    val timezoneStatus: String,
    val eventType: String,
    val entityId: String,
    val evidenceId: String,
    val summary: String,
    val details: String,
    val isConflicting: Boolean,
    val hypotheticalTimestamp: String? = null
)

data class Hypothesis(
    val id: String,
    val title: String,
    val summary: String,
    val supportingEvidence: List<String>,
    val contradictingEvidence: List<String>,
    val neutralEvidence: List<String>,
    val missingInformation: List<String>,
    val assumptions: List<String>,
    val unansweredQuestions: List<String>,
    val suggestedNextSteps: List<String>
)

data class ContradictionFinding(
    val findingId: String,
    val ruleId: String,
    val ruleName: String,
    val severity: String,
    val sourceEvidenceIds: List<String>,
    val supportingEventIds: List<String>,
    val ruleCondition: String,
    val observedValues: String,
    val triggeringReason: String,
    val alternativeExplanations: List<String>,
    val missingInformation: String,
    val suggestedAction: String,
    val limitations: String
)

data class MissingEvidenceItem(
    val id: String,
    val title: String,
    val impactLevel: String,
    val affectedHypotheses: List<String>,
    val affectedFindings: List<String>,
    val whyItMatters: String,
    val resolutionSteps: String
)

data class ChallengeTask(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val verificationKey: String,
    val hint: String,
    val isCompleted: Boolean = false,
    val userNotes: String = ""
)

data class ReviewerAnnotation(
    val hypothesisId: String,
    val evidenceId: String,
    val stance: String, // "SUPPORTS", "CONTRADICTS", "NEUTRAL", "REQUIRES_REVIEW"
    val notes: String = ""
)

data class IntegrityVerificationResult(
    val actionId: String,
    val filename: String,
    val calculatedSha256: String,
    val referenceSha256: String?,
    val matchStatus: String,
    val statusText: String,
    val isMatch: Boolean?,
    val verifiedAt: String,
    val forensicGuidance: String
)
