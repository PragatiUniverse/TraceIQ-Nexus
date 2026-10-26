"""Hypothesis Engine for Feature 2: Multiple-Hypothesis Lab."""
import datetime
from typing import List, Dict, Any, Optional
from traceiq.data_loader import load_case_data, get_evidence_by_id
from traceiq.models import get_db_connection

class HypothesisEngine:
    def __init__(self):
        pass

    def get_hypotheses(self) -> List[Dict[str, Any]]:
        case = load_case_data()
        raw_hypotheses = case.get("hypotheses", [])
        
        # Load reviewer annotations from SQLite
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT hypothesis_id, evidence_id, stance, notes, updated_at FROM reviewer_annotations")
        rows = cur.fetchall()
        conn.close()
        
        annotations_by_hyp = {}
        for r in rows:
            h_id = r["hypothesis_id"]
            if h_id not in annotations_by_hyp:
                annotations_by_hyp[h_id] = []
            annotations_by_hyp[h_id].append({
                "evidence_id": r["evidence_id"],
                "stance": r["stance"],
                "notes": r["notes"],
                "updated_at": r["updated_at"]
            })

        results = []
        for h in raw_hypotheses:
            item = dict(h)
            h_id = item["id"]
            item["reviewer_annotations"] = annotations_by_hyp.get(h_id, [])
            
            # Enrich supporting/contradicting evidence with full records
            item["supporting_records"] = [get_evidence_by_id(eid) for eid in item.get("supporting_evidence", []) if get_evidence_by_id(eid)]
            item["contradicting_records"] = [get_evidence_by_id(eid) for eid in item.get("contradicting_evidence", []) if get_evidence_by_id(eid)]
            item["neutral_records"] = [get_evidence_by_id(eid) for eid in item.get("neutral_evidence", []) if get_evidence_by_id(eid)]
            
            # Metrics (Deterministic counts, strictly documented that counts != probabilities)
            item["metric_support_count"] = len(item.get("supporting_evidence", []))
            item["metric_contradict_count"] = len(item.get("contradicting_evidence", []))
            item["metric_missing_count"] = len(item.get("missing_information", []))
            item["evaluation_note"] = "Evidence support counts reflect documented consistency with the scenario, NOT mathematical probability of truth."
            
            results.append(item)
            
        return results

    def get_hypothesis_by_id(self, hypothesis_id: str) -> Optional[Dict[str, Any]]:
        for h in self.get_hypotheses():
            if h["id"] == hypothesis_id:
                return h
        return None

    def save_reviewer_annotation(self, case_id: str, hypothesis_id: str, evidence_id: str, stance: str, notes: str = "") -> Dict[str, Any]:
        valid_stances = {"SUPPORTS", "CONTRADICTS", "NEUTRAL", "REQUIRES_REVIEW"}
        if stance not in valid_stances:
            raise ValueError(f"Invalid stance '{stance}'. Must be one of {valid_stances}")
        
        conn = get_db_connection()
        cur = conn.cursor()
        now = datetime.datetime.now(datetime.timezone.utc).isoformat()
        cur.execute("""
        INSERT INTO reviewer_annotations (case_id, hypothesis_id, evidence_id, stance, notes, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(case_id, hypothesis_id, evidence_id) DO UPDATE SET
            stance=excluded.stance,
            notes=excluded.notes,
            updated_at=excluded.updated_at
        """, (case_id, hypothesis_id, evidence_id, stance, notes, now, now))
        conn.commit()
        conn.close()
        
        return {
            "status": "success",
            "case_id": case_id,
            "hypothesis_id": hypothesis_id,
            "evidence_id": evidence_id,
            "stance": stance,
            "updated_at": now
        }
