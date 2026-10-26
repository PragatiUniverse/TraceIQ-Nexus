# TraceIQ Nexus — Executive Build Summary

**Project Name:** TraceIQ Nexus  
**Tagline:** Connect the Clues. Challenge the Story. Verify the Evidence.  
**Category:** AI-Assisted Digital Forensics & Explainable Evidence Intelligence  
**Target User:** Digital forensics students, incident response trainees, quality reviewers, and forensic educators.  

---

### The Problem
Digital forensics investigations suffer from severe cognitive overload, fragmented telemetry across physical and digital logs, and confirmation bias. Existing tools (Autopsy, Timesketch) excel at ingestion and keyword indexing, but lack native frameworks to evaluate competing hypotheses, detect cross-source timeline contradictions, model clock skew assumptions, and trace findings directly to raw source evidence.

### The Proposed Solution
TraceIQ Nexus is an educational investigation lab built on a foundational principle: **a suspicious pattern is not proof of wrongdoing.** It integrates timeline replay, competing-hypothesis comparison, deterministic contradiction detection, missing-evidence taxonomies, SHA-256 integrity verification, and an interactive challenge lab using a fully self-contained synthetic case (`TQN-2026-001: The Missing Research Files`).

### Core Features & Modules
1. **Evidence Time Machine:** Interactive timeline replay with scrubber control, play/pause, step controls, and hypothetical clock-skew toggle (-3200ms) without altering raw data.
2. **Multiple-Hypothesis Lab (ACH):** Structured comparison of Hypothesis A (Authorized Scheduled Maintenance) vs Hypothesis B (Unrecognized Remote Exfiltration) with interactive reviewer annotation persistence.
3. **Contradiction Hunter:** Deterministic evaluation of 7 inconsistency rules with expandable "Why was this flagged?" explainability blocks, alternative explanations, and limitations.
4. **Evidence Provenance & Graph:** Full lifecycle pipeline (`Source Artifact -> Parsed Record -> Event -> Rule -> Finding -> Report`) with interactive Cytoscape.js network visualization.
5. **SHA-256 File Integrity:** Genuine hashing engine using Python `hashlib`, verifying reference matches and detecting deliberate byte-level modifications.
6. **Missing-Evidence Analysis:** Explicit taxonomy of missing records and their impact on competing hypotheses.
7. **Live Investigation Challenge:** Scored 6-task educational lab tracking investigator progress up to 100 points with single-click reset.
8. **Evidence-Linked Report Generator:** Compiles an evidence-linked 17-section forensic dossier in Markdown and plain text.

### Technology Stack
- **Backend:** Python 3.11, Flask, SQLite3, NetworkX, `hashlib`.
- **Frontend:** HTML5, CSS3, Vanilla JavaScript, Cytoscape.js, Bootstrap 5.
- **Mobile/Android:** Kotlin, Jetpack Compose, Material Design 3, Dark Forensic Theme.
- **Testing:** Python `unittest` test suite + Android Robolectric JVM tests.

### Validation Status & Limitations
- **100% Automated Test Passing:** All unit tests for routes, integrity, timeline, hypotheses, contradictions, and reports pass.
- **Zero Cloud / API Dependencies:** Runs completely offline without API keys, paid credits, or large model weights.
- **Strict Size Compliance:** Deliverable archive is strictly below the 25,000,000-byte submission limit (target < 10 MB).
- **Limitations:** Educational simulation only; local SQLite storage is not tamper-proof; does not replace certified legal chain of custody.
