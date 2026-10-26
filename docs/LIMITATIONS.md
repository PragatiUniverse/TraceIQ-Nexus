# TraceIQ Nexus — Technical, Methodological & Ethical Limitations

**System Classification:** Educational Forensic Simulation & Workflow Prototype  
**Scope Boundary:** Case TQN-2026-001 (Fictional Synthetic Dataset Only)  

---

## 1. Technical Limitations
1. **Local SQLite Persistence:**  
   TraceIQ Nexus stores reviewer annotations, challenge task progress, and audit logs in a local SQLite database (`traceiq_nexus.db`). The database is designed for rapid local demonstration and evaluation. It is **not** cryptographically sealed or tamper-proof; any user with operating system write access to the database file can alter records.
2. **Deterministic Rule Engine (No Black-Box ML):**  
   The current prototype operates strictly on deterministic rule conditions (`RULE-CONTR-001` through `007`). It does not run neural network inference, deep learning models, or external LLMs. This guarantees total offline privacy, complete explainability, and zero API dependencies, but limits pattern recognition to predefined rule sets.
3. **Absence of Enterprise Multi-Tenant Authentication:**  
   The local prototype is single-user and does not enforce role-based access control (RBAC), multi-factor authentication (MFA), or enterprise identity federation.
4. **File Ingestion & Path Security:**  
   File verification accepts byte streams and local samples within the project directory. It enforces a strict 10 MB upload ceiling and validates hex digests. It does not accept arbitrary operating system root paths from user input.

---

## 2. Methodological & Epistemic Limitations
1. **Correlation Does Not Establish Causation or Guilt:**  
   Anomalies such as temporal sequence inversions, uncataloged certificate identities, or permission modifications can easily occur from benign operational factors (e.g., NTP clock slew, hypervisor migration latency, test scripts). Flagged findings represent review recommendations, **not legal conclusions of wrongdoing**.
2. **Analysis of Competing Hypotheses (ACH) Limitations:**  
   Heuer's ACH framework evaluates consistency with alternative scenarios. TraceIQ Nexus deliberately refuses to convert evidence support counts into Bayesian probabilities of truth, as evidence quantity does not equate to diagnostic weight.
3. **Cryptographic Integrity vs Legal Chain of Custody:**  
   A matching SHA-256 digest proves byte-level mathematical identity with a reference string. It does **not** establish the authenticity of the reference itself, the identity of the author, the physical state of the storage media, or compliance with court evidentiary standards (e.g., Federal Rules of Evidence 901/902).

---

## 3. Ethical Principles & Data Privacy
1. **100% Synthetic Fictional Telemetry:**  
   All personnel names (Dr. Elena Rostova, Marcus Vance, Sarah Chen), IP addresses (RFC 5737 documentation blocks `192.0.2.0/24`, `198.51.100.0/24`, `203.0.113.0/24`), workstation hostnames, and badge numbers are entirely fictional. No real personal identifiable information (PII) or real network logs are used.
2. **No Offensive Cyber Capabilities:**  
   TraceIQ Nexus contains zero credential harvesting, malware detonation, remote code execution, or unauthorized collection capabilities. It is purely an analytical defense and educational forensics platform.
3. **Human-in-the-Loop Requirement:**  
   TraceIQ Nexus is engineered as an investigator cognitive aid. It is not designed to replace certified digital forensics examiners, law enforcement investigators, or formal court proceedings.
