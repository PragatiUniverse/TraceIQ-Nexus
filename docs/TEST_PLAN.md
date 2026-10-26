# TraceIQ Nexus — Test Plan & Execution Matrix

## 1. Test Suite Overview
The testing framework for TraceIQ Nexus validates:
1. **Cryptographic Integrity:** Verifying known bytes, digest matching, tamper detection, empty input, and oversize boundaries.
2. **Temporal Correlation:** Chronological sorting, timezone ambiguity preservation, and hypothetical sequence calculation.
3. **Contradiction Detection:** Evaluating all 7 seeded forensic contradiction rules.
4. **Multiple Hypotheses:** Ensuring support/contradicting evidence linkage and separate storage of reviewer annotations.
5. **Missing Evidence:** Identifying absent records and assessing impact taxonomies.
6. **Challenge Progress:** Task completion scoring, state persistence, and reset capabilities.
7. **Report Synthesis:** Verifying evidence ID references across all 17 required report sections.

---

## 2. Test Execution Matrix

| Test ID | Subsystem | Purpose & Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| `TEST-INT-001` | `integrity.py` | SHA-256 of known string (`b"TraceIQ"`) | Matches pre-computed hex digest | Matched | **PASS** |
| `TEST-INT-002` | `integrity.py` | Original sample `quantum_protocol_v3.pdf` against reference | Returns `MATCHES_REFERENCE` | `MATCHES_REFERENCE` | **PASS** |
| `TEST-INT-003` | `integrity.py` | Modified sample `quantum_protocol_v3_tampered.pdf` against reference | Returns `DIFFERS_FROM_REFERENCE` | `DIFFERS_FROM_REFERENCE` | **PASS** |
| `TEST-INT-004` | `integrity.py` | Malformed reference digest (`"XYZ123"`) | Raises `ValueError` for invalid hex | Raised `ValueError` | **PASS** |
| `TEST-INT-005` | `integrity.py` | Payload exceeding 10MB limit | Raises `ValueError` for oversize payload | Raised `ValueError` | **PASS** |
| `TEST-TIM-001` | `timeline_engine.py` | Chronological ordering of 28+ events | Effective timestamps strictly non-decreasing | Sorted correctly | **PASS** |
| `TEST-TIM-002` | `timeline_engine.py` | Hypothetical sequence toggle with NTP offset | EVT-022 adjusts from 01:47:15 to 01:44:02 | Adjusted correctly | **PASS** |
| `TEST-CONTR-001` | `contradiction_engine.py` | Evaluate all rules against case TQN-2026-001 | Detects exactly 7 seeded contradiction findings | 7 findings detected | **PASS** |
| `TEST-HYP-001` | `hypothesis_engine.py` | Competing hypothesis retrieval | Returns HYP-A and HYP-B with supporting/contradicting lists | Returned both | **PASS** |
| `TEST-HYP-002` | `hypothesis_engine.py` | Reviewer annotation persistence | Stance saved to SQLite and retrieved | Persisted correctly | **PASS** |
| `TEST-MIS-001` | `missing_evidence.py` | Missing evidence enumeration | Identifies MIS-001 (EVD-099 packet capture) | Identified correctly | **PASS** |
| `TEST-CHAL-001` | `challenge_engine.py` | Challenge task completion & scoring | Completing task awards points up to 100 | Scored accurately | **PASS** |
| `TEST-CHAL-002` | `challenge_engine.py` | Challenge reset | Score and task completions reset to 0 | Reset to 0 | **PASS** |
| `TEST-REP-001` | `report_generator.py` | Evidence-linked report generation | Contains all 17 required sections and references EVD-001..028 | All 17 sections present | **PASS** |
| `TEST-ROUT-001` | `routes.py` | Flask health endpoint `/` & `/dashboard` | Status 200 and loads metrics | Status 200 | **PASS** |
| `TEST-DB-001` | `models.py` | Database initialization & reset | Tables created and cleared on reset | Verified | **PASS** |
