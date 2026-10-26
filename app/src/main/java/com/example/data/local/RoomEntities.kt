package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.*

@Entity(tableName = "evidence_records")
data class EvidenceEntity(
    @PrimaryKey val id: String,
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
) {
    fun toDomain() = EvidenceRecord(
        id = id,
        sourceArtifact = sourceArtifact,
        title = title,
        category = category,
        collectionTimestamp = collectionTimestamp,
        importTimestamp = importTimestamp,
        timezoneStatus = timezoneStatus,
        referenceHash = referenceHash,
        calculatedHash = calculatedHash,
        integrityStatus = integrityStatus,
        description = description
    )
}

fun EvidenceRecord.toEntity() = EvidenceEntity(
    id = id,
    sourceArtifact = sourceArtifact,
    title = title,
    category = category,
    collectionTimestamp = collectionTimestamp,
    importTimestamp = importTimestamp,
    timezoneStatus = timezoneStatus,
    referenceHash = referenceHash,
    calculatedHash = calculatedHash,
    integrityStatus = integrityStatus,
    description = description
)

@Entity(tableName = "timeline_events")
data class TimelineEventEntity(
    @PrimaryKey val id: String,
    val timestamp: String,
    val timezoneStatus: String,
    val eventType: String,
    val entityId: String,
    val evidenceId: String,
    val summary: String,
    val details: String,
    val isConflicting: Boolean,
    val hypotheticalTimestamp: String?
) {
    fun toDomain() = TimelineEvent(
        id = id,
        timestamp = timestamp,
        timezoneStatus = timezoneStatus,
        eventType = eventType,
        entityId = entityId,
        evidenceId = evidenceId,
        summary = summary,
        details = details,
        isConflicting = isConflicting,
        hypotheticalTimestamp = hypotheticalTimestamp
    )
}

fun TimelineEvent.toEntity() = TimelineEventEntity(
    id = id,
    timestamp = timestamp,
    timezoneStatus = timezoneStatus,
    eventType = eventType,
    entityId = entityId,
    evidenceId = evidenceId,
    summary = summary,
    details = details,
    isConflicting = isConflicting,
    hypotheticalTimestamp = hypotheticalTimestamp
)

@Entity(tableName = "entities")
data class CaseEntityRecord(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val type: String,
    val badgeId: String,
    val workstation: String,
    val ipAddress: String,
    val notes: String
) {
    fun toDomain() = com.example.data.Entity(
        id = id,
        name = name,
        role = role,
        type = type,
        badgeId = badgeId,
        workstation = workstation,
        ipAddress = ipAddress,
        notes = notes
    )
}

fun com.example.data.Entity.toEntity() = CaseEntityRecord(
    id = id,
    name = name,
    role = role,
    type = type,
    badgeId = badgeId,
    workstation = workstation,
    ipAddress = ipAddress,
    notes = notes
)

@Entity(tableName = "hypotheses")
data class HypothesisEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val supportingEvidence: List<String>,
    val contradictingEvidence: List<String>,
    val neutralEvidence: List<String>,
    val missingInformation: List<String>,
    val assumptions: List<String>,
    val unansweredQuestions: List<String>,
    val suggestedNextSteps: List<String>
) {
    fun toDomain() = Hypothesis(
        id = id,
        title = title,
        summary = summary,
        supportingEvidence = supportingEvidence,
        contradictingEvidence = contradictingEvidence,
        neutralEvidence = neutralEvidence,
        missingInformation = missingInformation,
        assumptions = assumptions,
        unansweredQuestions = unansweredQuestions,
        suggestedNextSteps = suggestedNextSteps
    )
}

fun Hypothesis.toEntity() = HypothesisEntity(
    id = id,
    title = title,
    summary = summary,
    supportingEvidence = supportingEvidence,
    contradictingEvidence = contradictingEvidence,
    neutralEvidence = neutralEvidence,
    missingInformation = missingInformation,
    assumptions = assumptions,
    unansweredQuestions = unansweredQuestions,
    suggestedNextSteps = suggestedNextSteps
)

@Entity(tableName = "contradiction_findings")
data class ContradictionEntity(
    @PrimaryKey val findingId: String,
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
) {
    fun toDomain() = ContradictionFinding(
        findingId = findingId,
        ruleId = ruleId,
        ruleName = ruleName,
        severity = severity,
        sourceEvidenceIds = sourceEvidenceIds,
        supportingEventIds = supportingEventIds,
        ruleCondition = ruleCondition,
        observedValues = observedValues,
        triggeringReason = triggeringReason,
        alternativeExplanations = alternativeExplanations,
        missingInformation = missingInformation,
        suggestedAction = suggestedAction,
        limitations = limitations
    )
}

fun ContradictionFinding.toEntity() = ContradictionEntity(
    findingId = findingId,
    ruleId = ruleId,
    ruleName = ruleName,
    severity = severity,
    sourceEvidenceIds = sourceEvidenceIds,
    supportingEventIds = supportingEventIds,
    ruleCondition = ruleCondition,
    observedValues = observedValues,
    triggeringReason = triggeringReason,
    alternativeExplanations = alternativeExplanations,
    missingInformation = missingInformation,
    suggestedAction = suggestedAction,
    limitations = limitations
)

@Entity(tableName = "missing_evidence")
data class MissingEvidenceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val impactLevel: String,
    val affectedHypotheses: List<String>,
    val affectedFindings: List<String>,
    val whyItMatters: String,
    val resolutionSteps: String
) {
    fun toDomain() = MissingEvidenceItem(
        id = id,
        title = title,
        impactLevel = impactLevel,
        affectedHypotheses = affectedHypotheses,
        affectedFindings = affectedFindings,
        whyItMatters = whyItMatters,
        resolutionSteps = resolutionSteps
    )
}

fun MissingEvidenceItem.toEntity() = MissingEvidenceEntity(
    id = id,
    title = title,
    impactLevel = impactLevel,
    affectedHypotheses = affectedHypotheses,
    affectedFindings = affectedFindings,
    whyItMatters = whyItMatters,
    resolutionSteps = resolutionSteps
)

@Entity(tableName = "challenge_tasks")
data class ChallengeTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val verificationKey: String,
    val hint: String,
    val isCompleted: Boolean,
    val userNotes: String
) {
    fun toDomain() = ChallengeTask(
        id = id,
        title = title,
        description = description,
        points = points,
        verificationKey = verificationKey,
        hint = hint,
        isCompleted = isCompleted,
        userNotes = userNotes
    )
}

fun ChallengeTask.toEntity() = ChallengeTaskEntity(
    id = id,
    title = title,
    description = description,
    points = points,
    verificationKey = verificationKey,
    hint = hint,
    isCompleted = isCompleted,
    userNotes = userNotes
)

@Entity(
    tableName = "reviewer_annotations",
    primaryKeys = ["hypothesisId", "evidenceId"]
)
data class ReviewerAnnotationEntity(
    val hypothesisId: String,
    val evidenceId: String,
    val stance: String,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = ReviewerAnnotation(
        hypothesisId = hypothesisId,
        evidenceId = evidenceId,
        stance = stance,
        notes = notes
    )
}

fun ReviewerAnnotation.toEntity() = ReviewerAnnotationEntity(
    hypothesisId = hypothesisId,
    evidenceId = evidenceId,
    stance = stance,
    notes = notes
)

@Entity(tableName = "verification_actions")
data class VerificationActionEntity(
    @PrimaryKey val actionId: String,
    val filename: String,
    val calculatedSha256: String,
    val referenceSha256: String?,
    val matchStatus: String,
    val statusText: String,
    val isMatch: Boolean?,
    val verifiedAt: String,
    val forensicGuidance: String
) {
    fun toDomain() = IntegrityVerificationResult(
        actionId = actionId,
        filename = filename,
        calculatedSha256 = calculatedSha256,
        referenceSha256 = referenceSha256,
        matchStatus = matchStatus,
        statusText = statusText,
        isMatch = isMatch,
        verifiedAt = verifiedAt,
        forensicGuidance = forensicGuidance
    )
}

fun IntegrityVerificationResult.toEntity() = VerificationActionEntity(
    actionId = actionId,
    filename = filename,
    calculatedSha256 = calculatedSha256,
    referenceSha256 = referenceSha256,
    matchStatus = matchStatus,
    statusText = statusText,
    isMatch = isMatch,
    verifiedAt = verifiedAt,
    forensicGuidance = forensicGuidance
)
