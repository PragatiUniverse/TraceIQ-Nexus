package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CaseRepository
import com.example.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: TraceIQDatabase
  private lateinit var dao: ForensicDao
  private lateinit var repository: CaseRepository

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, TraceIQDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    dao = db.forensicDao()
    repository = CaseRepository(forensicDao = dao)
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TraceIQ Nexus", appName)
  }

  @Test
  fun `verify SHA-256 calculation matches expected`() {
    val hash = repository.calculateSha256("TraceIQ")
    assertEquals(64, hash.length)
  }

  @Test
  fun `verify authentic original sample matches reference manifest`() {
    val res = repository.verifySampleOriginal()
    assertEquals("MATCHES_REFERENCE", res.matchStatus)
    assertTrue(res.isMatch == true)
  }

  @Test
  fun `verify tampered sample detects cryptographic mismatch`() {
    val res = repository.verifySampleTampered()
    assertEquals("DIFFERS_FROM_REFERENCE", res.matchStatus)
    assertFalse(res.isMatch == true)
  }

  @Test
  fun `verify Room database seeds and queries evidence records`() = runBlocking {
    val evidenceList = repository.seedEvidenceRecords.map { it.toEntity() }
    dao.insertEvidence(evidenceList)

    val stored = dao.getAllEvidence().first()
    assertEquals(evidenceList.size, stored.size)
    assertEquals("EVD-001", stored.first().id)
  }

  @Test
  fun `verify Room persistence for reviewer annotations`() = runBlocking {
    val annotation = ReviewerAnnotationEntity(
        hypothesisId = "HYP-A",
        evidenceId = "EVD-011",
        stance = "SUPPORTS",
        notes = "Room verified local persistence"
    )
    dao.insertAnnotation(annotation)

    val stored = dao.getAllAnnotations().first()
    assertEquals(1, stored.size)
    assertEquals("SUPPORTS", stored.first().stance)
    assertEquals("HYP-A", stored.first().hypothesisId)
  }

  @Test
  fun `verify Room challenge task completion and reset`() = runBlocking {
    val tasks = repository.seedTasks.map { it.toEntity() }
    dao.insertChallengeTasks(tasks)

    dao.updateTaskCompletion("TASK-001", true, "Verified in Room test")
    var stored = dao.getAllChallengeTasks().first()
    assertTrue(stored.first { it.id == "TASK-001" }.isCompleted)

    dao.resetChallengeTasks()
    stored = dao.getAllChallengeTasks().first()
    assertFalse(stored.first { it.id == "TASK-001" }.isCompleted)
  }

  @Test
  fun `verify contradiction rules completeness`() {
    assertEquals(7, repository.seedContradictions.size)
  }
}
