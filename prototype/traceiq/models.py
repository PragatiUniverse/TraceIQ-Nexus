"""Database models and SQLite connection layer for TraceIQ Nexus."""
import sqlite3
import os
import datetime
from typing import Optional, List, Dict, Any

DATABASE_NAME = "traceiq_nexus.db"

def get_db_path() -> str:
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    return os.path.join(base_dir, DATABASE_NAME)

def get_db_connection() -> sqlite3.Connection:
    conn = sqlite3.connect(get_db_path())
    conn.row_factory = sqlite3.Row
    return conn

def init_db(db_path: Optional[str] = None) -> None:
    path = db_path or get_db_path()
    conn = sqlite3.connect(path)
    cur = conn.cursor()
    
    # Reviewer Annotations table
    cur.execute("""
    CREATE TABLE IF NOT EXISTS reviewer_annotations (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        case_id TEXT NOT NULL,
        hypothesis_id TEXT NOT NULL,
        evidence_id TEXT NOT NULL,
        stance TEXT NOT NULL CHECK(stance IN ('SUPPORTS', 'CONTRADICTS', 'NEUTRAL', 'REQUIRES_REVIEW')),
        notes TEXT,
        created_at TEXT NOT NULL,
        updated_at TEXT NOT NULL,
        UNIQUE(case_id, hypothesis_id, evidence_id)
    )
    """)
    
    # Challenge Progress table
    cur.execute("""
    CREATE TABLE IF NOT EXISTS challenge_progress (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        task_id TEXT UNIQUE NOT NULL,
        completed INTEGER NOT NULL DEFAULT 0,
        completed_at TEXT,
        score INTEGER NOT NULL DEFAULT 0,
        user_notes TEXT
    )
    """)
    
    # File Verification Actions table
    cur.execute("""
    CREATE TABLE IF NOT EXISTS verification_actions (
        action_id TEXT PRIMARY KEY,
        filename TEXT NOT NULL,
        file_size_bytes INTEGER NOT NULL,
        calculated_sha256 TEXT NOT NULL,
        reference_sha256 TEXT,
        match_status TEXT NOT NULL,
        verified_at TEXT NOT NULL
    )
    """)
    
    # Application Audit Log table
    cur.execute("""
    CREATE TABLE IF NOT EXISTS audit_events (
        event_id INTEGER PRIMARY KEY AUTOINCREMENT,
        action_type TEXT NOT NULL,
        target_entity TEXT NOT NULL,
        details TEXT,
        timestamp TEXT NOT NULL
    )
    """)

    conn.commit()
    conn.close()

def log_audit(action_type: str, target: str, details: str, db_path: Optional[str] = None) -> None:
    path = db_path or get_db_path()
    try:
        conn = sqlite3.connect(path)
        cur = conn.cursor()
        now = datetime.datetime.now(datetime.timezone.utc).isoformat()
        cur.execute(
            "INSERT INTO audit_events (action_type, target_entity, details, timestamp) VALUES (?, ?, ?, ?)",
            (action_type, target, details, now)
        )
        conn.commit()
        conn.close()
    except Exception as e:
        # Audit logging failure should not crash analytical operations
        pass
