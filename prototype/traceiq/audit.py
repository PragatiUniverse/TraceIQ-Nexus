"""Audit trail recording for TraceIQ Nexus."""
from typing import List, Dict, Any
from traceiq.models import get_db_connection, log_audit

def record_action(action: str, target: str, details: str = "") -> None:
    log_audit(action, target, details)

def get_recent_audit_events(limit: int = 50) -> List[Dict[str, Any]]:
    conn = get_db_connection()
    cur = conn.cursor()
    cur.execute("SELECT event_id, action_type, target_entity, details, timestamp FROM audit_events ORDER BY event_id DESC LIMIT ?", (limit,))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows
