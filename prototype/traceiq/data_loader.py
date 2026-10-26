"""Data loading and caching utility for TraceIQ Nexus."""
import json
import os
from typing import Dict, Any, List, Optional
from traceiq.models import get_db_connection, init_db

_CASE_DATA_CACHE: Optional[Dict[str, Any]] = None

def get_data_dir() -> str:
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    return os.path.join(base_dir, "data")

def load_case_data(force_reload: bool = False) -> Dict[str, Any]:
    global _CASE_DATA_CACHE
    if _CASE_DATA_CACHE is not None and not force_reload:
        return _CASE_DATA_CACHE
    
    json_path = os.path.join(get_data_dir(), "sample_case.json")
    with open(json_path, "r", encoding="utf-8") as f:
        _CASE_DATA_CACHE = json.load(f)
    return _CASE_DATA_CACHE

def get_evidence_by_id(evidence_id: str) -> Optional[Dict[str, Any]]:
    case = load_case_data()
    for ev in case.get("evidence_records", []):
        if ev["id"] == evidence_id:
            return ev
    return None

def get_event_by_id(event_id: str) -> Optional[Dict[str, Any]]:
    case = load_case_data()
    for evt in case.get("events", []):
        if evt["id"] == event_id:
            return evt
    return None

def get_entity_by_id(entity_id: str) -> Optional[Dict[str, Any]]:
    case = load_case_data()
    for ent in case.get("entities", []):
        if ent["id"] == entity_id:
            return ent
    return None

def reset_case_state() -> None:
    """Resets database annotations and challenge progress back to fresh state."""
    global _CASE_DATA_CACHE
    _CASE_DATA_CACHE = None
    init_db()
    conn = get_db_connection()
    cur = conn.cursor()
    cur.execute("DELETE FROM reviewer_annotations")
    cur.execute("DELETE FROM challenge_progress")
    cur.execute("DELETE FROM verification_actions")
    cur.execute("DELETE FROM audit_events")
    conn.commit()
    conn.close()
    load_case_data(force_reload=True)
