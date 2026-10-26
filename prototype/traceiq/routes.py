"""Flask routes and API endpoints for TraceIQ Nexus."""
import io
from flask import Blueprint, render_template, request, jsonify, send_file, Response
from traceiq.data_loader import load_case_data, get_evidence_by_id, get_entity_by_id, reset_case_state
from traceiq.timeline_engine import TimelineEngine
from traceiq.hypothesis_engine import HypothesisEngine
from traceiq.contradiction_engine import ContradictionEngine
from traceiq.provenance import ProvenanceEngine, GraphBuilder
from traceiq.integrity import IntegrityEngine
from traceiq.missing_evidence import MissingEvidenceEngine
from traceiq.challenge_engine import ChallengeEngine
from traceiq.report_generator import ReportGenerator
from traceiq.audit import record_action, get_recent_audit_events

bp = Blueprint("traceiq", __name__)

timeline_engine = TimelineEngine()
hypothesis_engine = HypothesisEngine()
contradiction_engine = ContradictionEngine()
provenance_engine = ProvenanceEngine()
graph_builder = GraphBuilder()
integrity_engine = IntegrityEngine()
missing_evidence_engine = MissingEvidenceEngine()
challenge_engine = ChallengeEngine()
report_generator = ReportGenerator()

@bp.route("/")
@bp.route("/dashboard")
def dashboard():
    case = load_case_data()
    evidence = case.get("evidence_records", [])
    events = case.get("events", [])
    contradictions = contradiction_engine.evaluate_all_rules()
    hypotheses = hypothesis_engine.get_hypotheses()
    missing_items = missing_evidence_engine.get_missing_items()
    challenge_status = challenge_engine.get_challenge_status()
    recent_audits = get_recent_audit_events(limit=8)
    
    # Calculate live metrics strictly from loaded data
    metrics = {
        "evidence_count": len(evidence),
        "event_count": len(events),
        "contradiction_count": len(contradictions),
        "hypotheses_count": len(hypotheses),
        "missing_items_count": len(missing_items),
        "challenge_score": challenge_status["current_score"],
        "challenge_percent": challenge_status["score_percentage"]
    }
    
    return render_template(
        "dashboard.html",
        case=case,
        metrics=metrics,
        entities=case.get("entities", []),
        recent_audits=recent_audits,
        challenge=challenge_status
    )

@bp.route("/evidence")
def evidence_list():
    case = load_case_data()
    search = request.args.get("q", "").strip().lower()
    cat_filter = request.args.get("category", "")
    
    records = case.get("evidence_records", [])
    if cat_filter:
        records = [r for r in records if r.get("category") == cat_filter]
    if search:
        records = [
            r for r in records
            if search in r.get("id", "").lower() or search in r.get("title", "").lower() or search in r.get("description", "").lower()
        ]
        
    categories = sorted(list({r.get("category") for r in case.get("evidence_records", [])}))
    return render_template("evidence.html", records=records, categories=categories, search=search, selected_cat=cat_filter)

@bp.route("/evidence/<evidence_id>")
def evidence_detail(evidence_id):
    rec = get_evidence_by_id(evidence_id)
    if not rec:
        return "Evidence record not found", 404
    prov = provenance_engine.get_provenance_for_evidence(evidence_id)
    return render_template("evidence_detail.html", record=rec, provenance=prov)

@bp.route("/timeline")
def timeline():
    use_hyp = request.args.get("hypothetical", "0") == "1"
    search = request.args.get("q", "")
    entity_f = request.args.get("entity", "")
    events = timeline_engine.get_timeline_events(use_hypothetical=use_hyp, search_query=search, entity_filter=entity_f)
    case = load_case_data()
    return render_template("timeline.html", events=events, use_hypothetical=use_hyp, search=search, entities=case.get("entities", []))

@bp.route("/api/timeline/replay")
def api_timeline_replay():
    index = int(request.args.get("index", 0))
    use_hyp = request.args.get("hypothetical", "0") == "1"
    frame = timeline_engine.get_replay_frame(index, use_hypothetical=use_hyp)
    return jsonify(frame)

@bp.route("/hypotheses")
def hypotheses():
    data = hypothesis_engine.get_hypotheses()
    return render_template("hypothesis_lab.html", hypotheses=data)

@bp.route("/api/hypotheses/annotate", methods=["POST"])
def api_annotate_hypothesis():
    req = request.get_json() or {}
    case_id = req.get("case_id", "TQN-2026-001")
    hyp_id = req.get("hypothesis_id")
    ev_id = req.get("evidence_id")
    stance = req.get("stance")
    notes = req.get("notes", "")
    
    if not (hyp_id and ev_id and stance):
        return jsonify({"error": "Missing required fields"}), 400
        
    res = hypothesis_engine.save_reviewer_annotation(case_id, hyp_id, ev_id, stance, notes)
    record_action("REVIEWER_ANNOTATION", f"{hyp_id}:{ev_id}", f"Stance: {stance}")
    return jsonify(res)

@bp.route("/contradictions")
def contradictions():
    findings = contradiction_engine.evaluate_all_rules()
    return render_template("contradiction_lab.html", findings=findings)

@bp.route("/graph")
def graph():
    return render_template("graph.html")

@bp.route("/api/graph/elements")
def api_graph_elements():
    elements = graph_builder.build_cytoscape_elements()
    return jsonify(elements)

@bp.route("/integrity", methods=["GET", "POST"])
def integrity():
    verification_result = None
    if request.method == "POST":
        action = request.form.get("action")
        if action == "verify_original":
            verification_result = integrity_engine.verify_sample_original()
            record_action("INTEGRITY_CHECK", "quantum_protocol_v3.pdf", "Original sample verified")
        elif action == "verify_tampered":
            verification_result = integrity_engine.verify_sample_tampered()
            record_action("INTEGRITY_CHECK", "quantum_protocol_v3_tampered.pdf", "Tampered sample detected")
        elif action == "verify_custom_text":
            text_content = request.form.get("text_content", "").encode("utf-8")
            ref_hash = request.form.get("reference_hash", "").strip() or None
            verification_result = integrity_engine.verify_file(
                filename="custom_text_input.txt",
                data_bytes=text_content,
                reference_sha256=ref_hash
            )
            record_action("INTEGRITY_CHECK", "custom_text_input.txt", f"Status: {verification_result['match_status']}")
            
    return render_template("integrity.html", result=verification_result)

@bp.route("/missing")
def missing_evidence():
    items = missing_evidence_engine.get_missing_items()
    return render_template("missing_evidence.html", items=items)

@bp.route("/challenge")
def challenge():
    status = challenge_engine.get_challenge_status()
    return render_template("challenge.html", challenge=status)

@bp.route("/api/challenge/complete", methods=["POST"])
def api_complete_challenge_task():
    req = request.get_json() or {}
    task_id = req.get("task_id")
    user_notes = req.get("notes", "")
    if not task_id:
        return jsonify({"error": "task_id required"}), 400
    res = challenge_engine.complete_task(task_id, user_notes=user_notes)
    record_action("CHALLENGE_TASK_COMPLETE", task_id, f"Completed with notes: {user_notes}")
    return jsonify(res)

@bp.route("/api/challenge/reset", methods=["POST"])
def api_reset_challenge():
    res = challenge_engine.reset_challenge()
    record_action("CHALLENGE_RESET", "ALL_TASKS", "Challenge progress cleared")
    return jsonify(res)

@bp.route("/report")
def report():
    md = report_generator.generate_markdown_report()
    return render_template("report.html", report_markdown=md)

@bp.route("/api/report/download")
def download_report():
    md = report_generator.generate_markdown_report()
    record_action("REPORT_EXPORT", "TQN-2026-001", "Exported forensic report in Markdown")
    return Response(
        md,
        mimetype="text/markdown",
        headers={"Content-Disposition": "attachment;filename=TraceIQ_Investigation_Report_TQN-2026-001.md"}
    )

@bp.route("/methodology")
def methodology():
    return render_template("methodology.html")

@bp.route("/api/case/reset", methods=["POST"])
def api_reset_case():
    reset_case_state()
    record_action("CASE_RESET", "TQN-2026-001", "Restored demo dataset to pristine initial baseline")
    return jsonify({"status": "success", "message": "Demo data reset successfully"})
