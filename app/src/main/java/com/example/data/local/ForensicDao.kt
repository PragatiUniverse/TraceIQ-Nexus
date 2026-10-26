package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ForensicDao {

    // --- Evidence Records ---
    @Query("SELECT * FROM evidence_records ORDER BY id ASC")
    fun getAllEvidence(): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidence_records WHERE id = :id")
    fun getEvidenceById(id: String): Flow<EvidenceEntity?>

    @Query("SELECT COUNT(*) FROM evidence_records")
    suspend fun getEvidenceCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(items: List<EvidenceEntity>)

    // --- Timeline Events ---
    @Query("SELECT * FROM timeline_events ORDER BY timestamp ASC")
    fun getAllEvents(): Flow<List<TimelineEventEntity>>

    @Query("SELECT COUNT(*) FROM timeline_events")
    suspend fun getEventsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(items: List<TimelineEventEntity>)

    // --- Entities ---
    @Query("SELECT * FROM entities ORDER BY id ASC")
    fun getAllEntities(): Flow<List<CaseEntityRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntities(items: List<CaseEntityRecord>)

    // --- Hypotheses ---
    @Query("SELECT * FROM hypotheses ORDER BY id ASC")
    fun getAllHypotheses(): Flow<List<HypothesisEntity>>

    @Query("SELECT * FROM hypotheses WHERE id = :id")
    fun getHypothesisById(id: String): Flow<HypothesisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHypotheses(items: List<HypothesisEntity>)

    // --- Contradiction Findings ---
    @Query("SELECT * FROM contradiction_findings ORDER BY findingId ASC")
    fun getAllContradictions(): Flow<List<ContradictionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContradictions(items: List<ContradictionEntity>)

    // --- Missing Evidence ---
    @Query("SELECT * FROM missing_evidence ORDER BY id ASC")
    fun getAllMissingEvidence(): Flow<List<MissingEvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissingEvidence(items: List<MissingEvidenceEntity>)

    // --- Challenge Tasks ---
    @Query("SELECT * FROM challenge_tasks ORDER BY id ASC")
    fun getAllChallengeTasks(): Flow<List<ChallengeTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallengeTasks(items: List<ChallengeTaskEntity>)

    @Query("UPDATE challenge_tasks SET isCompleted = :isCompleted, userNotes = :notes WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean, notes: String)

    @Query("UPDATE challenge_tasks SET isCompleted = 0, userNotes = ''")
    suspend fun resetChallengeTasks()

    // --- Reviewer Annotations ---
    @Query("SELECT * FROM reviewer_annotations")
    fun getAllAnnotations(): Flow<List<ReviewerAnnotationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: ReviewerAnnotationEntity)

    @Query("DELETE FROM reviewer_annotations")
    suspend fun clearAnnotations()

    // --- Verification Actions ---
    @Query("SELECT * FROM verification_actions ORDER BY verifiedAt DESC")
    fun getAllVerificationActions(): Flow<List<VerificationActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationAction(action: VerificationActionEntity)

    // --- Global Wipe / Reset ---
    @Query("DELETE FROM reviewer_annotations")
    suspend fun clearAllAnnotations()

    @Query("DELETE FROM verification_actions")
    suspend fun clearAllVerifications()
}
