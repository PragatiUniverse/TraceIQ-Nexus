"""Missing-Evidence and Uncertainty Engine for Feature 6."""
from typing import List, Dict, Any, Optional
from traceiq.data_loader import load_case_data

class MissingEvidenceEngine:
    def __init__(self):
        pass

    def get_missing_items(self) -> List[Dict[str, Any]]:
        case = load_case_data()
        items = case.get("missing_evidence_items", [])
        
        results = []
        for it in items:
            item = dict(it)
            # Add taxonomy category
            if "BLOCKS" in item.get("impact_level", ""):
                item["taxonomy"] = "MISSING_CRITICAL_RECORD"
                item["severity_badge"] = "CRITICAL_GAP"
            else:
                item["taxonomy"] = "CONTEXT_ATTENUATION"
                item["severity_badge"] = "MODERATE_GAP"
                
            results.append(item)
        return results

    def get_missing_item_by_id(self, item_id: str) -> Optional[Dict[str, Any]]:
        for it in self.get_missing_items():
            if it["id"] == item_id:
                return it
        return None
