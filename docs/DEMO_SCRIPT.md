# TraceIQ Nexus — 5-Minute Live Demonstration Script
**Target Duration:** 5 Minutes (Strict Timing)  
**Case:** TQN-2026-001 (The Missing Research Files)  
**Presenter:** Student Lead / Project Architect  

---

### Minute 1: Introduction & Case Brief (0:00 – 1:00)
- **UI Location:** Dashboard (`/dashboard`)
- **Action:**
  1. Open browser to `http://127.0.0.1:5000/dashboard`.
  2. Point to the live case banner: *Case TQN-2026-001: The Missing Research Files*.
  3. Highlight the live metrics row (28 Evidence Records, 32 Reconstructed Events, 7 Flagged Contradictions).
- **Spoken Talking Point:**
  > "Welcome to TraceIQ Nexus. Digital forensics investigations face a critical bottleneck: cognitive overload and confirmation bias. Our core principle is: *A suspicious pattern is not proof of wrongdoing.* TraceIQ Nexus is an educational investigation lab designed to connect clues, challenge assumptions, and trace findings directly back to source evidence without black-box opacity."

---

### Minute 2: Evidence Time Machine & Timestamp Contradiction (1:00 – 2:00)
- **UI Location:** Evidence Time Machine (`/timeline`)
- **Action:**
  1. Navigate to `/timeline`.
  2. Click **Play (▶️)** to demonstrate the automated causal event stream replay.
  3. Drag the time scrubber slider to **Event 22 (`EVT-022`)**.
  4. Point out the flagged conflict badge: Console activity attributed to Dr. Rostova at 01:47 UTC, occurring *two minutes after* her physical exit badge scan at 01:45 UTC.
  5. Toggle the **Hypothetical Sequence (NTP Skew -3200ms)** switch.
  6. Show how the hypothetical model realigns Event 22 to 01:44:02 UTC based on Chrony NTP telemetry (`EVD-023`) without overwriting raw logs.
- **Spoken Talking Point:**
  > "Here in the Evidence Time Machine, we replay events chronologically. At Event 22, the system flags a severe anomaly: console activity was recorded after Dr. Rostova badge-exited the building. Rather than immediately concluding credential theft, TraceIQ allows reviewers to test a documented hypothetical: server clock skew. Toggling the NTP model realigns the events cleanly without altering raw evidence."

---

### Minute 3: Multiple-Hypothesis Lab (ACH) (2:00 – 3:00)
- **UI Location:** Hypothesis Lab (`/hypotheses`)
- **Action:**
  1. Navigate to `/hypotheses`.
  2. Show the side-by-side view of **Hypothesis A** (Authorized Scheduled Maintenance) vs **Hypothesis B** (Unrecognized Remote Access).
  3. Click the **Supports** button on Hypothesis A for `EVD-011` to record an interactive reviewer annotation.
  4. Highlight the epistemic guardrail: Support counts reflect consistency, never probability of guilt.
- **Spoken Talking Point:**
  > "In the Multiple-Hypothesis Lab, we implement Richards Heuer's Analysis of Competing Hypotheses. Notice Hypothesis A: Systems Administrator Marcus Vance scheduled a nightly backup during failing disk sector alerts. Hypothesis B suggests external exploitation via VPN split tunnel. Reviewers annotate their stance interactively, while the system explicitly warns that evidence tallies do not equal mathematical proof."

---

### Minute 4: Provenance, Missing Telemetry & SHA-256 Verification (3:00 – 4:00)
- **UI Location:** Contradictions (`/contradictions`), Integrity (`/integrity`), Missing Evidence (`/missing`)
- **Action:**
  1. Open `/contradictions` and click **"Why was this flagged?"** on `FND-002: Cryptographic Hash Digest Mismatch`.
  2. Navigate to `/integrity`.
  3. Click **"Verify (Original)"** on `quantum_protocol_v3.pdf` &rarr; show `MATCHES_REFERENCE` (Green).
  4. Click **"Verify (Tampered)"** on `quantum_protocol_v3_tampered.pdf` &rarr; show `DIFFERS_FROM_REFERENCE` (Red).
  5. Navigate to `/missing` to display `MIS-001` (Missing secondary switch packet capture).
- **Spoken Talking Point:**
  > "Every contradiction provides an explainability breakdown. In our Integrity Verification module, real Python hashlib verifies SHA-256 digests. The authentic reference matches our manifest, while the recovered backup sample detects a byte mismatch. Meanwhile, the Missing Evidence Explorer highlights that without secondary switch PCAP, neither hypothesis can be definitively proven."

---

### Minute 5: Challenge Mode & Forensic Report Generation (4:00 – 5:00)
- **UI Location:** Challenge Mode (`/challenge`) & Report Generator (`/report`)
- **Action:**
  1. Open `/challenge` and show completed tasks and score tracking.
  2. Navigate to `/report`.
  3. Scroll through the generated 17-section forensic report (Evidence inventory, contradiction findings, hypotheses, limitations).
  4. Click **"📋 Copy Markdown"** or **"⬇️ Download Report (.md)"**.
- **Spoken Talking Point:**
  > "To evaluate investigator competence, our Challenge Mode provides interactive scored quests. Finally, TraceIQ Nexus compiles all telemetry, rule findings, and reviewer annotations into an evidence-linked 17-section report ready for review. Thank you!"
