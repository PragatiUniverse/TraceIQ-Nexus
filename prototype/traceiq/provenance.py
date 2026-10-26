"""Evidence Provenance and Graph Builder for Feature 4."""
from typing import List, Dict, Any, Optional
from traceiq.data_loader import load_case_data, get_evidence_by_id
from traceiq.contradiction_engine import ContradictionEngine

class ProvenanceEngine:
    def __init__(self):
        self.contradiction_engine = ContradictionEngine()

    def get_provenance_chains(self) -> List[Dict[str, Any]]:
        case = load_case_data()
        evidence_list = case.get("evidence_records", [])
        events = case.get("events", [])
        findings = self.contradiction_engine.evaluate_all_rules()
        
        chains = []
        for ev in evidence_list:
            ev_id = ev["id"]
            derived_events = [e for e in events if e.get("evidence_id") == ev_id]
            derived_findings = [f for f in findings if ev_id in f.get("source_evidence_ids", [])]
            
            chain = {
                "evidence_id": ev_id,
                "title": ev.get("title"),
                "source_artifact": ev.get("source_artifact"),
                "category": ev.get("category"),
                "collection_timestamp": ev.get("collection_timestamp"),
                "timezone_status": ev.get("timezone_status"),
                "import_timestamp": ev.get("import_timestamp"),
                "reference_hash": ev.get("reference_hash"),
                "calculated_hash": ev.get("calculated_hash"),
                "integrity_status": ev.get("integrity_status"),
                "provenance_pipeline": [
                    {"stage": "1. Source Artifact", "label": ev.get("source_artifact"), "type": "RAW_SOURCE"},
                    {"stage": "2. Parsed Record", "label": ev_id, "type": "NORMALIZED"},
                    {"stage": "3. Reconstructed Events", "count": len(derived_events), "event_ids": [e["id"] for e in derived_events], "type": "EVENT"},
                    {"stage": "4. Analysis Findings", "count": len(derived_findings), "finding_ids": [f["finding_id"] for f in derived_findings], "type": "RULE_OUTPUT"},
                    {"stage": "5. Report Synthesis", "label": f"Included in Evidence Inventory ({ev_id})", "type": "REPORT"}
                ],
                "derived_events": derived_events,
                "derived_findings": derived_findings
            }
            chains.append(chain)
        return chains

    def get_provenance_for_evidence(self, evidence_id: str) -> Optional[Dict[str, Any]]:
        for c in self.get_provenance_chains():
            if c["evidence_id"] == evidence_id:
                return c
        return None

class GraphBuilder:
    def __init__(self):
        self.provenance = ProvenanceEngine()

    def build_cytoscape_elements(self) -> Dict[str, List[Dict[str, Any]]]:
        """Generates nodes and edges formatted for Cytoscape.js interactive visualization."""
        case = load_case_data()
        nodes = []
        edges = []
        
        # 1. Entity Nodes
        for ent in case.get("entities", []):
            nodes.append({
                "data": {
                    "id": ent["id"],
                    "label": ent["name"],
                    "sublabel": ent["role"],
                    "node_type": "ENTITY",
                    "color": "#8B5CF6"
                }
            })
            
        # 2. Evidence Nodes
        for ev in case.get("evidence_records", []):
            nodes.append({
                "data": {
                    "id": ev["id"],
                    "label": ev["id"],
                    "sublabel": ev["title"][:22] + "...",
                    "node_type": "EVIDENCE",
                    "color": "#00E5FF"
                }
            })
            
        # 3. Hypothesis Nodes
        for h in case.get("hypotheses", []):
            nodes.append({
                "data": {
                    "id": h["id"],
                    "label": h["id"],
                    "sublabel": h["title"][:24] + "...",
                    "node_type": "HYPOTHESIS",
                    "color": "#EC4899"
                }
            })
            
            # Edges: Hypothesis -> Evidence
            for eid in h.get("supporting_evidence", []):
                edges.append({
                    "data": {
                        "id": f"edge-sup-{h['id']}-{eid}",
                        "source": h["id"],
                        "target": eid,
                        "label": "SUPPORTS",
                        "line_color": "#10B981"
                    }
                })
            for eid in h.get("contradicting_evidence", []):
                edges.append({
                    "data": {
                        "id": f"edge-con-{h['id']}-{eid}",
                        "source": h["id"],
                        "target": eid,
                        "label": "CONTRADICTS",
                        "line_color": "#EF4444"
                    }
                })

        # 4. Event to Entity & Evidence edges
        for evt in case.get("events", []):
            ent_id = evt.get("entity_id")
            ev_id = evt.get("evidence_id")
            if ent_id and ev_id:
                edges.append({
                    "data": {
                        "id": f"edge-evt-{evt['id']}",
                        "source": ent_id,
                        "target": ev_id,
                        "label": evt.get("event_type"),
                        "line_color": "#64748B"
                    }
                })

        return {"nodes": nodes, "edges": edges}
