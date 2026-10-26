# TraceIQ Nexus — Digital Forensics Investigation Lab
> **Connect the Clues. Challenge the Story. Verify the Evidence.**

TraceIQ Nexus is a competition-ready, explainable digital forensics prototype designed for educational investigations, hypothesis falsification, timeline reconstruction, and evidence intelligence.

---

## 🚀 Quick Start Guide (Windows 10 / 11 & VS Code)

### 1. Prerequisites
- Python 3.10 or 3.11 installed from [python.org](https://www.python.org) (ensure **"Add Python to PATH"** was checked during installation).
- Modern web browser (Chrome, Edge, or Firefox).

### 2. Open Project in VS Code
Open the root `TraceIQ-Nexus` directory in Visual Studio Code. Open a PowerShell terminal in VS Code (`Ctrl + ~`).

### 3. Create & Activate Virtual Environment
```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
```
*(If PowerShell restricts script execution, run: `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass` then re-run the activate command).*

### 4. Install Dependencies
```powershell
python -m pip install --upgrade pip
python -m pip install -r prototype\requirements.txt
```

### 5. Launch Application
You can run the application directly from the root launcher:
```powershell
python app.py
```
Or directly from the prototype folder:
```powershell
python prototype\app.py
```

### 6. Open in Browser
Open your browser and navigate to:
**`http://127.0.0.1:5000`**

---

## 🧪 Running Automated Tests
Run the complete test suite locally:
```powershell
python -m unittest discover -s prototype\tests -p "test_*.py"
```

---

## 📁 Project Directory Structure
```
TraceIQ-Nexus/
├── app.py                      # Root launcher delegating to prototype/app.py
├── requirements.txt            # Dependency manifest
├── README.md                   # Windows setup & overview
├── Build-Log.md                # Complete 6-chapter engineering log
├── Build-Summary.md            # Concise 1-page executive summary
├── docs/                       # Comprehensive documentation suite
│   ├── ARCHITECTURE.md         # Component and data layer design
│   ├── COMPETITIVE_ANALYSIS.md # Detailed literature review & comparison matrix
│   ├── DATA_DICTIONARY.md      # Field-level telemetry schemas
│   ├── TEST_PLAN.md            # Test matrix and verification records
│   ├── LIMITATIONS.md          # Technical, ethical & legal boundaries
│   └── DEMO_SCRIPT.md          # 5-minute timed demonstration script
└── prototype/                  # Runnable prototype application
    ├── app.py                  # Prototype entrypoint
    ├── requirements.txt
    ├── traceiq/                # Core Python analytical engines
    │   ├── models.py           # SQLite database schema
    │   ├── data_loader.py      # Case data ingestion & caching
    │   ├── timeline_engine.py  # Feature 1: Evidence Time Machine
    │   ├── hypothesis_engine.py# Feature 2: Multiple-Hypothesis Lab
    │   ├── contradiction_engine.py # Feature 3: Contradiction Hunter
    │   ├── provenance.py       # Feature 4: Provenance pipeline
    │   ├── integrity.py        # Feature 5: SHA-256 integrity hashing
    │   ├── missing_evidence.py # Feature 6: Missing-evidence analysis
    │   ├── challenge_engine.py # Feature 7: Live investigation challenge
    │   ├── report_generator.py # Feature 8: Evidence-linked report generator
    │   ├── graph_builder.py    # Cytoscape graph generator
    │   ├── audit.py            # SQLite audit trail recorder
    │   └── routes.py           # Flask endpoints & API
    ├── data/                   # Fictional synthetic investigation case
    │   ├── sample_case.json    # Case TQN-2026-001 (28 records, 32 events)
    │   ├── evidence_manifest.json
    │   └── sample_evidence/    # Original & tampered sample test files
    ├── templates/              # Responsive dark UI templates
    ├── static/                 # CSS & JavaScript interactions
    └── tests/                  # Automated verification test suite
```

---

## 🔄 Resetting the Demo Data
To restore the database and challenge progress back to baseline:
1. Click the **"Reset Demo"** button in the navigation bar, OR
2. In PowerShell, delete `traceiq_nexus.db` (it will be regenerated automatically).

---

## 📜 Ethical & Technical Disclaimer
Case `TQN-2026-001` is entirely fictional. TraceIQ Nexus is an educational prototype and cognitive aid. It does not replace certified digital forensics examiners, nor does it provide court-certified chain of custody.
