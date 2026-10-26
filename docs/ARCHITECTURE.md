# TraceIQ Nexus — Software Architecture Specification

## 1. System Overview
TraceIQ Nexus is organized around a clean modular architecture separating data representation, analytical rule evaluation, stateful reviewer annotations, and presentation layers.

```
[ Synthetic Forensic Artifacts ]
              │
              ▼
    [ traceiq/data_loader.py ] ──► [ SQLite traceiq_nexus.db ]
              │                              ▲
              ▼                              │
┌────────────────────────────────────────┐   │
│         ANALYTICAL ENGINE LAYER        │   │ (Reviewer Annotations,
│  - timeline_engine.py                  │   │  Challenge Progress,
│  - hypothesis_engine.py                │   │  Audit Events)
│  - contradiction_engine.py             │   │
│  - provenance.py & graph_builder.py    │   │
│  - integrity.py (hashlib SHA-256)      │   │
│  - missing_evidence.py                 │───┘
│  - report_generator.py                 │
└────────────────────────────────────────┘
              │
              ▼
┌────────────────────────────────────────┐
│           PRESENTATION LAYER           │
│  - Flask Routes (traceiq/routes.py)    │
│  - Responsive Dark UI (Templates)      │
│  - Cytoscape.js Evidence Graph         │
│  - Native Jetpack Compose Android App  │
└────────────────────────────────────────┘
```

## 2. Key Modules & Responsibilities
- `traceiq/models.py`: SQLite connection handling, schema definitions for `reviewer_annotations`, `challenge_progress`, `verification_actions`, and `audit_events`.
- `traceiq/timeline_engine.py`: Event sorting, timezone normalization, scrubber position frames, and recorded vs. hypothetical timeline comparison.
- `traceiq/hypothesis_engine.py`: Structured Analysis of Competing Hypotheses (ACH) models, supporting/contradicting record cross-referencing, reviewer stance updates.
- `traceiq/contradiction_engine.py`: Deterministic evaluation of 7 forensic contradiction rules with "Why was this flagged?" explainability blocks.
- `traceiq/integrity.py`: SHA-256 hashing via standard library `hashlib`, upload safety boundaries, sample file comparison.
- `traceiq/provenance.py`: Multi-stage pipeline reconstruction (`Source Artifact -> Parsed Record -> Event -> Rule -> Finding -> Report`).
- `traceiq/challenge_engine.py`: 6-task interactive scoring system with reset functionality.
- `traceiq/report_generator.py`: Generates complete 17-section forensic dossier in Markdown and plain text.

## 3. Data Integrity & Safety Boundaries
- **Max Upload Limit:** 10 MB strict threshold for integrity verification.
- **Path Sanitization:** File operations are restricted to known synthetic directory boundaries; user-supplied paths are rejected.
- **SQL Injection Prevention:** 100% of SQLite database interactions utilize parameterized query placeholders (`?`).
