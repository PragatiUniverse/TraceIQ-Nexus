# TraceIQ Nexus — Master Build Log
**Project:** TraceIQ Nexus (Explainable Digital Forensics Investigation Lab)  
**Author:** B.Tech AI & Data Science Candidate  
**Date:** October 26, 2026  
**Status:** Complete Competition Submission Package  

---

## Chapter 1 — Problem Discovery and Initial Assumptions

### 1.1 Work Performed
- Investigated the cognitive workflow of modern digital forensics practitioners and incident responders when analyzing multi-source enterprise telemetry (firewalls, Active Directory authentication logs, physical access badges, disk journals, and network captures).
- Identified the prevalence of confirmation bias and premature investigative closure during cyber incident evaluations.

### 1.2 Decisions Made
- Chose an educational simulation focus based on synthetic, strictly fictional case data (`TQN-2026-001: The Missing Research Files`).
- Formulated the core governing maxim: *"A suspicious pattern is not proof of wrongdoing. Every finding must have a traceable basis, limitations, and a way for a human reviewer to challenge it."*

### 1.3 Sources Consulted
- Carrier & Spafford (2004), *An Event-based Digital Investigation Framework*.
- Richards J. Heuer Jr. (1999), *Psychology of Intelligence Analysis*, CIA Center for the Study of Intelligence.

### 1.4 Initial Assumptions
- Investigators need explainable, rule-linked reasoning rather than opaque AI confidence scores.
- Discrepancies between physical telemetry (badge scanners) and virtual telemetry (server console commands) are frequently caused by clock drift rather than malicious credential compromise.

### 1.5 Evidence Collected
- Analyzed enterprise case studies where clock skew (NTP drift > 3000ms) resulted in false-positive accusations during internal audits.

### 1.6 Unverified Items & Next Steps
- Field evaluation with professional incident responders remains pending. Next step: formalize the competitor landscape in Chapter 2.

---

## Chapter 2 — Research and Evidence

### 2.1 Work Performed
- Conducted technical comparative analysis across primary open-source forensics solutions: Autopsy/The Sleuth Kit (Basis Technology), Plaso/Timesketch (Google Open Source), and Velociraptor (Rapid7).
- Authored `docs/COMPETITIVE_ANALYSIS.md` detailing functional capabilities, citations, and specific architectural gaps.

### 2.2 Decisions Made
- Confirmed that existing tools excel at artifact ingestion and timeline filtering, but lack native Multiple-Hypothesis Lab (ACH) support and deterministic cross-telemetry contradiction flagging.
- Decided to implement a dual-sequence timeline replay that allows investigators to compare raw logs against hypothetical time-adjusted models without modifying original records.

### 2.3 Sources Consulted
- Autopsy 4.21 Documentation (2024).
- Timesketch Architecture Guide & Open Source Repository (2024).
- Velociraptor VQL Technical Reference (2024).

### 2.4 Assumptions
- Forensic reviewers benefit from an interactive "Why was this flagged?" explainability card for every anomaly.

### 2.5 Evidence Collected
- Compiled a capability matrix demonstrating that zero current open-source triage tools natively provide built-in Analysis of Competing Hypotheses side-by-side with interactive slider replay.

### 2.6 Unverified Items & Next Steps
- Quantitative cognitive workload reduction requires human subject study. Next step: specify target user personas in Chapter 3.

---

## Chapter 3 — Problem Definition and Target User

### 3.1 Work Performed
- Defined primary target personas:
  1. *Student & Junior Forensic Analysts:* Seeking structured educational sandboxes to practice multi-source correlation without legal liabilities.
  2. *Forensic Reviewers & Quality Officers:* Requiring transparent provenance chains (`Artifact -> Record -> Event -> Finding -> Report`) to cross-examine technical conclusions.

### 3.2 Decisions Made
- Case TQN-2026-001 must contain at least 25 evidence records, 30+ events, 6+ entities, 2 competing hypotheses (Authorized Maintenance vs Unrecognized Remote Exfiltration), 2 seeded timestamp inconsistencies, and an actual tampered file sample.

### 3.3 Sources Consulted
- NIST Special Publication 800-86: *Guide to Integrating Forensic Techniques into Incident Response*.
- ISO/IEC 27037:2012: *Guidelines for identification, collection, acquisition, and preservation of digital evidence*.

### 3.4 Assumptions
- User environments will include Windows 10/11 running Python 3.11 with zero external cloud dependencies.

### 3.5 Evidence Collected
- Synthesized 28 distinct evidence artifacts spanning network flows, physical badges, ext4 inode unlinks, crontab tables, S.M.A.R.T. disk alerts, and NTP logs.

### 3.6 Unverified Items & Next Steps
- Real-time collaborative multi-analyst editing is deferred to future work. Next step: design architecture in Chapter 4.

---

## Chapter 4 — Solution Design and Reusable Specification

### 4.1 Work Performed
- Designed the eight core modules:
  1. Evidence Time Machine (`timeline_engine.py`)
  2. Multiple-Hypothesis Lab (`hypothesis_engine.py`)
  3. Contradiction Hunter (`contradiction_engine.py`)
  4. Evidence Provenance & Graph (`provenance.py` + `graph_builder.py`)
  5. SHA-256 Integrity Engine (`integrity.py`)
  6. Missing-Evidence Analysis (`missing_evidence.py`)
  7. Live Investigation Challenge Mode (`challenge_engine.py`)
  8. Evidence-Linked Report Generator (`report_generator.py`)
- Designed responsive dark UI utilizing deep navy background (`#0B1021`), cyan (`#00E5FF`), and violet (`#8B5CF6`) accents.

### 4.2 Decisions Made
- Stored raw case data in deterministic `sample_case.json` and user mutations (annotations, challenge progress, audit logs) in SQLite `traceiq_nexus.db`.
- Standardized file integrity calculations on FIPS 180-4 SHA-256 using Python standard library `hashlib`.

### 4.3 Sources Consulted
- NIST FIPS PUB 180-4: *Secure Hash Standard (SHS)*.
- Cytoscape.js Graph Theory Library Documentation (v3.28).

### 4.4 Assumptions
- Keeping support counts descriptive rather than converting them into pseudo-probabilistic "threat scores" reinforces critical human judgment.

### 4.5 Evidence Collected
- Validated JSON schemas and SQLite relational schemas ensuring foreign key consistency across evidence IDs, events, and findings.

### 4.6 Unverified Items & Next Steps
- High-volume streaming ingest (>100k events/sec) was not tested. Next step: implement and verify tests in Chapter 5.

---

## Chapter 5 — Implementation and Testing Workflow

### 5.1 Work Performed
- Generated complete Python backend architecture in `/prototype/traceiq/` and root/prototype launcher entrypoints.
- Built interactive HTML5/Bootstrap 5/Cytoscape.js templates.
- Created comprehensive test suite in `/prototype/tests/` covering routes, integrity hashing, timeline replay, contradiction detection, hypotheses, provenance, missing data, challenge progress, and report formatting.
- Executed automated tests using `unittest` verifying 100% pass status.

### 5.2 Decisions Made
- Hardcoded reference digest for `quantum_protocol_v3.pdf` (`2e6c0ed1...`) and confirmed that `quantum_protocol_v3_tampered.pdf` computes to `cd894c54...`, generating an authentic cryptographic mismatch.
- Provided a single-click "Reset Demo" endpoint to restore pristine baseline data on demand.

### 5.3 Sources Consulted
- Python 3.11 Standard Library Documentation (`unittest`, `sqlite3`, `hashlib`).

### 5.4 Assumptions
- Safe temporary in-memory buffers prevent disk pollution during text and digest comparisons.

### 5.5 Evidence Collected
- 100% of test suites executed with zero failures.

### 5.6 Unverified Items & Next Steps
- Cross-browser rendering on Safari mobile requires manual tester verification. Next step: synthesize evaluation in Chapter 6.

---

## Chapter 6 — Evaluation, Limitations, and Next Steps

### 6.1 Work Performed
- Evaluated prototype against the 24 acceptance criteria defined in the competition brief.
- Checked packaging size and confirmed the final archive remains comfortably under the 25,000,000-byte submission limit.
- Documented all ethical, technical, and methodological constraints in `docs/LIMITATIONS.md`.

### 6.2 Decisions Made
- Maintained strict technical honesty: no fabricated user interviews, no claims of legal admissibility, no fake AI accuracy percentages.
- Packaged clean, runnable source code without `.venv`, `__pycache__`, or model weights.

### 6.3 Sources Consulted
- Competition Rules & Master Specification Prompt.

### 6.4 Key Verified Results
- **Evidence Time Machine:** Replays 28 events, scrubs smoothly, highlights physical vs console time conflict.
- **Multiple-Hypothesis Lab:** Compares Hypothesis A vs B, persists reviewer annotations to SQLite.
- **Contradiction Hunter:** Detects all 7 seeded rules with "Why was this flagged?" explainability.
- **Integrity Verification:** Computes genuine SHA-256 digests and confirms tamper detection.
- **Challenge Mode:** Tracks 6 investigation quests with scoring up to 100 points.
- **Report Generator:** Generates full 17-section forensic dossier with Markdown export.

### 6.5 Remaining Limitations & Future Work
- Integration with live Elasticsearch/Timesketch clusters for enterprise-scale ingestion.
- Future addition of optional local explainable ML classifiers for anomaly clustering.
