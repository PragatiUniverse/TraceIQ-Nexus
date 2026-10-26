package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        EvidenceEntity::class,
        TimelineEventEntity::class,
        CaseEntityRecord::class,
        HypothesisEntity::class,
        ContradictionEntity::class,
        MissingEvidenceEntity::class,
        ChallengeTaskEntity::class,
        ReviewerAnnotationEntity::class,
        VerificationActionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TraceIQDatabase : RoomDatabase() {

    abstract fun forensicDao(): ForensicDao

    companion object {
        @Volatile
        private var INSTANCE: TraceIQDatabase? = null

        fun getInstance(context: Context): TraceIQDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TraceIQDatabase::class.java,
                    "traceiq_forensics.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
