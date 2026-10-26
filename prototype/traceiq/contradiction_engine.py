"""Contradiction Hunter Engine for Feature 3."""
from typing import List, Dict, Any, Optional
from traceiq.data_loader import load_case_data, get_evidence_by_id, get_event_by_id

class ContradictionEngine:
    def __init__(self):
        pass

    def evaluate_all_rules(self) -> List[Dict[str, Any]]:
        case = load_case_data()
        rules = case.get("contradiction_rules", [])
        
        findings = []
        for r in rules:
            finding = dict(r)
            finding["finding_id"] = f"FND-{finding['rule_id'].replace('RULE-CONTR-', '')}"
            
            # Enrich source evidence records
            source_records = []
            for eid in finding.get("source_evidence_ids", []):
                rec = get_evidence_by_id(eid)
                if rec:
                    source_records.append(rec)
                else:
                    source_records.append({
                        "id": eid,
                        "title": f"Unarchived / Missing Reference ({eid})",
                        "category": "MISSING_ARTIFACT",
                        "status": "NOT_IN_MANIFEST"
                    })
            finding["source_records"] = source_records
            
            # Enrich supporting events
            finding["supporting_events"] = [
                get_event_by_id(evtid) for evtid in finding.get("supporting_event_ids", [])
                if get_event_by_id(evtid)
            ]
            
            # Why was this flagged explainability block
            finding["explainability"] = {
                "rule_condition": finding.get("rule_condition"),
                "observed_values": finding.get("observed_values"),
                "triggering_reason": finding.get("triggering_reason"),
                "alternative_explanations": finding.get("alternative_explanations", []),
                "limitations": finding.get("limitations")
            }
            
            findings.append(finding)
            
        return findings

    def get_finding_by_id(self, finding_id: str) -> Optional[Dict[str, Any]]:
        for f in self.evaluate_all_rules():
            if f["finding_id"] == finding_id or f["rule_id"] == finding_id:
                return f
        return None
