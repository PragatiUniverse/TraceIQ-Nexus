"""Timeline Engine for Feature 1: Evidence Time Machine."""
from typing import List, Dict, Any, Optional
from traceiq.data_loader import load_case_data, get_evidence_by_id

class TimelineEngine:
    def __init__(self):
        pass

    def get_timeline_events(
        self,
        use_hypothetical: bool = False,
        search_query: Optional[str] = None,
        entity_filter: Optional[str] = None,
        event_type_filter: Optional[str] = None
    ) -> List[Dict[str, Any]]:
        case = load_case_data()
        events = list(case.get("events", []))
        
        # Enrich events with evidence collection/import time and entity metadata
        enriched = []
        for evt in events:
            item = dict(evt)
            ev_id = item.get("evidence_id")
            evidence = get_evidence_by_id(ev_id) if ev_id else None
            
            item["evidence_details"] = evidence
            item["effective_timestamp"] = (
                item.get("hypothetical_timestamp") if (use_hypothetical and item.get("hypothetical_timestamp"))
                else item["timestamp"]
            )
            item["is_hypothetical_applied"] = bool(use_hypothetical and item.get("hypothetical_timestamp"))
            
            # Apply filters
            if entity_filter and item.get("entity_id") != entity_filter:
                continue
            if event_type_filter and item.get("event_type") != event_type_filter:
                continue
            if search_query:
                q = search_query.lower()
                summary = item.get("summary", "").lower()
                details = item.get("details", "").lower()
                evt_id = item.get("id", "").lower()
                if q not in summary and q not in details and q not in evt_id:
                    continue
            
            enriched.append(item)
            
        # Chronological sort on effective timestamp
        enriched.sort(key=lambda x: x["effective_timestamp"])
        return enriched

    def get_replay_frame(self, index: int, use_hypothetical: bool = False) -> Dict[str, Any]:
        all_events = self.get_timeline_events(use_hypothetical=use_hypothetical)
        total = len(all_events)
        if total == 0:
            return {"events": [], "current_event": None, "current_index": 0, "total": 0, "progress_percent": 0}
        
        idx = max(0, min(index, total - 1))
        visible_events = all_events[: idx + 1]
        current_event = all_events[idx]
        
        # Entity relationships active in visible frame
        active_entities = {e.get("entity_id") for e in visible_events if e.get("entity_id")}
        
        return {
            "events": visible_events,
            "current_event": current_event,
            "current_index": idx,
            "total": total,
            "progress_percent": round(((idx + 1) / total) * 100, 1),
            "active_entities": list(active_entities),
            "has_conflict": any(e.get("is_conflicting") for e in visible_events)
        }
