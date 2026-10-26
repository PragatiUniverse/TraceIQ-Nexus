# TraceIQ Nexus — Data Dictionary
**Case Scope:** TQN-2026-001 (The Missing Research Files)

---

## 1. Case Entities (`entities`)
| Field Name | Type | Description | Example |
|---|---|---|---|
| `id` | String | Unique entity identifier | `ENT-001` |
| `name` | String | Fictional personnel or system name | `Dr. Elena Rostova` |
| `role` | String | Organizational responsibility | `Lead Quantum Cryptographer` |
| `type` | String | Classification: `PERSONNEL`, `AUTOMATION`, `NETWORK_HOST`, `SERVER`, `BACKUP_TARGET` | `PERSONNEL` |
| `badge_id` | String | Physical access token or rack ID | `BDG-8812` |
| `workstation` | String | Primary workstation or server hostname | `WS-LAB-04` |
| `ip_address` | String | RFC 5737 documentation IP address | `192.0.2.85` |

---

## 2. Evidence Records (`evidence_records`)
| Field Name | Type | Description | Example |
|---|---|---|---|
| `id` | String | Unique evidence identifier | `EVD-001` |
| `source_artifact` | String | Raw file, log, or telemetry source name | `fw-core-20261026.log` |
| `title` | String | Descriptive artifact title | `Core Firewall Inbound SSH Log` |
| `category` | String | Forensic classification (e.g., `NETWORK_TELEMETRY`, `AUTHENTICATION`, `FILE_INTEGRITY`) | `NETWORK_TELEMETRY` |
| `collection_timestamp` | ISO-8601 String | Timestamp when artifact was harvested | `2026-10-26T01:15:22Z` |
| `import_timestamp` | ISO-8601 String | Timestamp when record was ingested into TraceIQ | `2026-10-26T03:00:00Z` |
| `timezone_status` | String | Explicit status: `UTC_EXPLICIT`, `LOCAL_SYSTEM_TIME_UNCERTAIN`, `UTC_OFFSET_AFFECTED` | `UTC_EXPLICIT` |
| `reference_hash` | 64-Hex String | Baseline SHA-256 digest recorded in evidence manifest | `2e6c0ed1...` |
| `calculated_hash` | 64-Hex String | Cryptographically verified digest computed by `hashlib` | `2e6c0ed1...` |
| `integrity_status` | String | Status: `MATCHES_REFERENCE`, `DIFFERS_FROM_REFERENCE`, `REFERENCE_UNAVAILABLE` | `MATCHES_REFERENCE` |

---

## 3. Events (`events`)
| Field Name | Type | Description | Example |
|---|---|---|---|
| `id` | String | Chronological event identifier | `EVT-001` |
| `timestamp` | ISO-8601 String | Recorded event occurrence timestamp | `2026-10-25T16:00:00Z` |
| `event_type` | String | Classification (e.g., `AUTH`, `FILE_SYSTEM`, `PHYSICAL`, `HARDWARE`, `SYSTEM_LOG`) | `PHYSICAL` |
| `entity_id` | String | Foreign key to `entities.id` | `ENT-001` |
| `evidence_id` | String | Foreign key to `evidence_records.id` | `EVD-003` |
| `is_conflicting` | Boolean | True if temporal or contextual contradiction is flagged | `true` |
| `hypothetical_timestamp` | ISO-8601 / Null | Adjusted timestamp under explicit documented assumption | `2026-10-26T01:44:02Z` |

---

## 4. Contradiction Rules (`contradiction_rules`)
| Field Name | Type | Description | Example |
|---|---|---|---|
| `rule_id` | String | Rule identifier (`RULE-CONTR-001` to `007`) | `RULE-CONTR-001` |
| `severity` | String | Severity label: `CRITICAL`, `HIGH`, `MEDIUM`, `LOW` | `HIGH` |
| `rule_condition` | String | Boolean logic evaluated against telemetry | `timestamp(Console) > timestamp(Badge_Exit)` |
| `observed_values` | String | Actual observed field values | `Exit at 01:45 vs Console at 01:47` |
| `triggering_reason` | String | Plain-language trigger explanation | `Attributed console activity after physical exit` |
| `limitations` | String | Technical and epistemic boundaries of the rule | `Assumes both clock sources were synchronized` |
