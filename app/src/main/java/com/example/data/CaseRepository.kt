package com.example.data

import android.content.Context
import com.example.data.local.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CaseRepository(
    private val forensicDao: ForensicDao? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    val caseInfo = CaseInfo(
        caseId = "TQN-2026-001",
        title = "The Missing Research Files",
        category = "Educational Digital Forensics Simulation",
        status = "Demonstration Only",
        createdAt = "2026-10-26T01:00:00Z",
        disclaimer = "This scenario and all associated artifacts, logs, timestamps, IP addresses, and personnel names are entirely fictional and synthesized for educational forensics research and demonstration.",
        corePrinciple = "A suspicious pattern is not proof of wrongdoing. Every finding must have a traceable basis, limitations, and a way for a human reviewer to challenge it."
    )

    // Baseline Seed Data
    val seedEntities: List<Entity> = listOf(
        Entity("ENT-001", "Dr. Elena Rostova", "Lead Quantum Cryptographer", "PERSONNEL", "BDG-8812", "WS-LAB-04", "192.0.2.85", "Principal investigator on quantum protocol research."),
        Entity("ENT-002", "Marcus Vance", "Systems Administrator & Infrastructure Lead", "PERSONNEL", "BDG-3304", "WS-ADM-01", "192.0.2.10", "Responsible for scheduled maintenance and automated backups."),
        Entity("ENT-003", "Sarah Chen", "Postgraduate Research Assistant", "PERSONNEL", "BDG-9941", "WS-LAB-02", "192.0.2.44", "Maintained Git repository and local build scripts."),
        Entity("ENT-004", "DevSecOps Maintenance Bot", "Scheduled Sync & Backup Daemon", "AUTOMATION", "N/A", "SVR-VAULT-01", "192.0.2.1", "Runs cron job nightly_sync.sh at 02:00 UTC."),
        Entity("ENT-005", "External Gateway Host", "External SSH / VPN Remote Endpoint", "NETWORK_HOST", "N/A", "REMOTE", "198.51.100.42", "RFC 5737 documentation address representing remote access point."),
        Entity("ENT-006", "SVR-VAULT-01", "Primary Research Storage Server", "SERVER", "RACK-A4", "SVR-VAULT-01", "192.0.2.1", "Houses sensitive quantum protocol dataset. Clock offset +3200ms."),
        Entity("ENT-007", "NAS-STORAGE-09", "Offsite Secondary Storage Appliance", "BACKUP_TARGET", "RACK-B2", "NAS-STORAGE-09", "203.0.113.15", "Destination for nocturnal backup archival rsync.")
    )

    val seedEvidenceRecords: List<EvidenceRecord> = listOf(
        EvidenceRecord("EVD-001", "fw-core-20261026.log", "Core Firewall Inbound SSH Log", "NETWORK_TELEMETRY", "2026-10-26T01:15:22Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "a1b2c3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0", "a1b2c3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0", "MATCHES_REFERENCE", "Inbound TCP SYN packets on port 22 from 198.51.100.42 to 192.0.2.1 accepted per bastion rule 14B."),
        EvidenceRecord("EVD-002", "ad_security_event_4624.evtx", "Active Directory Logon Event (ID 4624)", "AUTHENTICATION", "2026-10-26T01:20:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "b2c3d4e5f6a10718293a4b5c6d7e8f90123456789abcdef0123456789abcdef1", "b2c3d4e5f6a10718293a4b5c6d7e8f90123456789abcdef0123456789abcdef1", "MATCHES_REFERENCE", "Logon type 10 (RemoteInteractive) for user mvance from IP 192.0.2.10."),
        EvidenceRecord("EVD-003", "phys_access_reader_south.csv", "Physical Badge Reader Log — South Lab Portal", "PHYSICAL_SECURITY", "2026-10-26T01:22:15Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "c3d4e5f6a1b20718293a4b5c6d7e8f90123456789abcdef0123456789abcdef2", "c3d4e5f6a1b20718293a4b5c6d7e8f90123456789abcdef0123456789abcdef2", "MATCHES_REFERENCE", "Badge BDG-8812 (Dr. Elena Rostova) verified entry at South Lab Door."),
        EvidenceRecord("EVD-004", "bash_history_root_ws04.txt", "Workstation WS-LAB-04 Root Bash History", "SYSTEM_LOG", "2026-10-26T01:30:10Z", "2026-10-26T03:00:00Z", "LOCAL_SYSTEM_TIME_UNCERTAIN", "d4e5f6a1b2c30718293a4b5c6d7e8f90123456789abcdef0123456789abcdef3", "d4e5f6a1b2c30718293a4b5c6d7e8f90123456789abcdef0123456789abcdef3", "MATCHES_REFERENCE", "Shell commands invoking rsync to local mount point /mnt/backup and export HISTFILESIZE=0."),
        EvidenceRecord("EVD-005", "ext4_journal_dump.raw", "Ext4 Inode Allocation Journal Dump", "FILE_SYSTEM", "2026-10-26T01:38:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "e5f6a1b2c3d40718293a4b5c6d7e8f90123456789abcdef0123456789abcdef4", "e5f6a1b2c3d40718293a4b5c6d7e8f90123456789abcdef0123456789abcdef4", "MATCHES_REFERENCE", "Unlink record for inode #482910 corresponding to quantum_protocol_v3.pdf in /srv/quantum/specs."),
        EvidenceRecord("EVD-006", "crontab_root_vault.cfg", "SVR-VAULT-01 Root Crontab Configuration", "CONFIG", "2026-10-26T01:00:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "f6a1b2c3d4e50718293a4b5c6d7e8f90123456789abcdef0123456789abcdef5", "f6a1b2c3d4e50718293a4b5c6d7e8f90123456789abcdef0123456789abcdef5", "MATCHES_REFERENCE", "Active scheduled job: 0 2 * * * /usr/local/bin/nightly_sync.sh --compress --target=NAS-09."),
        EvidenceRecord("EVD-007", "ipfix_flow_capture.pcapng", "NetFlow Session Telemetry Record", "NETWORK_TELEMETRY", "2026-10-26T02:02:10Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "0718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0a1b2c3d4e5f6", "0718293a4b5c6d7e8f90123456789abcdef0123456789abcdef0a1b2c3d4e5f6", "MATCHES_REFERENCE", "High throughput flow (4.2GB transferred) from 192.0.2.1:49210 to 203.0.113.15:873 (rsync)."),
        EvidenceRecord("EVD-008", "tls_handshake_nginx.log", "TLS Handshake Reverse Proxy Access Log", "NETWORK_TELEMETRY", "2026-10-26T01:40:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "18293a4b5c6d7e8f90123456789abcdef0123456789abcdef01b2c3d4e5f6a107", "18293a4b5c6d7e8f90123456789abcdef0123456789abcdef01b2c3d4e5f6a107", "MATCHES_REFERENCE", "mTLS session established using client cert CN=svc-daemon-guest. Note: CN not found in employee directory."),
        EvidenceRecord("EVD-009", "phys_access_reader_south.csv", "Physical Badge Reader Log — South Lab Portal (Exit)", "PHYSICAL_SECURITY", "2026-10-26T01:45:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "293a4b5c6d7e8f90123456789abcdef0123456789abcdef012c3d4e5f6a1b20718", "293a4b5c6d7e8f90123456789abcdef0123456789abcdef012c3d4e5f6a1b20718", "MATCHES_REFERENCE", "Badge BDG-8812 (Dr. Elena Rostova) scanned at turnstile EXIT at 01:45:00Z."),
        EvidenceRecord("EVD-010", "vpn_gateway_radius.log", "RADIUS VPN Authentication Session Record", "AUTHENTICATION", "2026-10-26T01:12:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "3a4b5c6d7e8f90123456789abcdef0123456789abcdef0123d4e5f6a1b2c307182", "3a4b5c6d7e8f90123456789abcdef0123456789abcdef0123d4e5f6a1b2c307182", "MATCHES_REFERENCE", "Tunnel ID 99201 assigned to 198.51.100.42 using split tunnel routing."),
        EvidenceRecord("EVD-011", "quantum_protocol_v3.pdf", "Research Specification Document (Original Reference)", "FILE_INTEGRITY", "2026-10-25T18:00:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0", "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0", "MATCHES_REFERENCE", "Original master quantum specification document deposited into laboratory repository."),
        EvidenceRecord("EVD-012", "quantum_protocol_v3_tampered.pdf", "Recovered Incomplete Vault Copy (Tampered / Altered)", "FILE_INTEGRITY", "2026-10-26T02:40:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0", "cd894c542077025c9c56004a1b265d64100dc721c1ed65fddf0101fa1612966a", "DIFFERS_FROM_REFERENCE", "Recovered file from backup snapshot with byte differences in header blocks. SHA-256 differs from reference."),
        EvidenceRecord("EVD-013", "auditd_syscall.log", "Linux Auditd Syscall Log — chmod Permissive Change", "SYSTEM_LOG", "2026-10-26T01:32:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "5c6d7e8f90123456789abcdef0123456789abcdef01234567f6a1b2c3d4e5071829", "5c6d7e8f90123456789abcdef0123456789abcdef01234567f6a1b2c3d4e5071829", "MATCHES_REFERENCE", "Command chmod 777 /srv/quantum/specs invoked under root uid=0."),
        EvidenceRecord("EVD-014", "exchange_msg_maint_notice.eml", "Maintenance Notification Email by Marcus Vance", "COMMUNICATION", "2026-10-25T16:00:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "6d7e8f90123456789abcdef0123456789abcdef01234567890718293a4b5c6d7e8f", "6d7e8f90123456789abcdef0123456789abcdef01234567890718293a4b5c6d7e8f", "MATCHES_REFERENCE", "Broadcast email: Scheduled storage migration for SVR-VAULT-01 tonight at 02:00 UTC."),
        EvidenceRecord("EVD-020", "smartd_disk_alert.log", "Disk S.M.A.R.T. Degradation Alert on SVR-VAULT-01", "HARDWARE_STATUS", "2026-10-26T01:10:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "23456789abcdef0123456789abcdef0123456789abcdef0123456c6d7e8f901236", "23456789abcdef0123456789abcdef0123456789abcdef0123456c6d7e8f901236", "MATCHES_REFERENCE", "Drive /dev/sdb reported 14 reallocated sectors. Auto-migration trigger flagged."),
        EvidenceRecord("EVD-023", "chrony_ntp_peer.log", "Chrony NTP Drift & Offset Log on SVR-VAULT-01", "SYSTEM_LOG", "2026-10-26T01:48:00Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "56789abcdef0123456789abcdef0123456789abcdef0123456789e7e8f901239", "56789abcdef0123456789abcdef0123456789abcdef0123456789e7e8f901239", "MATCHES_REFERENCE", "NTP clock skew alert: System time on SVR-VAULT-01 running +3200ms fast."),
        EvidenceRecord("EVD-027", "udev_usb_mount.log", "Kernel UDev USB Block Device Event Log", "SYSTEM_LOG", "2026-10-26T01:28:10Z", "2026-10-26T03:00:00Z", "UTC_EXPLICIT", "9abcdef0123456789abcdef0123456789abcdef0123456789012320e8f90123d", "9abcdef0123456789abcdef0123456789abcdef0123456789012320e8f90123d", "MATCHES_REFERENCE", "USB device insertion: SanDisk Extreme Pro 64GB mounted at /mnt/usb.")
    )

    val seedEvents: List<TimelineEvent> = listOf(
        TimelineEvent("EVT-001", "2026-10-25T16:00:00Z", "UTC_EXPLICIT", "COMMUNICATION", "ENT-002", "EVD-014", "Marcus Vance sent scheduled maintenance notification", "Broadcast email advising research team of tonight's 02:00 UTC storage sync.", false),
        TimelineEvent("EVT-002", "2026-10-25T18:00:00Z", "UTC_EXPLICIT", "FILE_SYSTEM", "ENT-001", "EVD-011", "Dr. Elena Rostova deposited quantum_protocol_v3.pdf reference draft", "Master research protocol PDF saved to vault directory with verified SHA-256.", false),
        TimelineEvent("EVT-005", "2026-10-26T01:00:00Z", "UTC_EXPLICIT", "CONFIG", "ENT-004", "EVD-006", "Cron daemon loaded nightly sync schedule", "Scheduled rsync job armed for execution at 02:00:00 UTC.", false),
        TimelineEvent("EVT-006", "2026-10-26T01:10:00Z", "UTC_EXPLICIT", "HARDWARE", "ENT-006", "EVD-020", "S.M.A.R.T. alert reported reallocated sectors on SVR-VAULT-01", "Storage drive /dev/sdb degradation warning triggered auto-evacuation protocol.", false),
        TimelineEvent("EVT-007", "2026-10-26T01:12:00Z", "UTC_EXPLICIT", "AUTH", "ENT-005", "EVD-010", "VPN split-tunnel session initiated from 198.51.100.42", "Remote connection authenticated through external gateway RADIUS interface.", false),
        TimelineEvent("EVT-008", "2026-10-26T01:15:22Z", "UTC_EXPLICIT", "NETWORK", "ENT-005", "EVD-001", "Inbound SSH connection attempted from 198.51.100.42", "Targeted port 22 on SVR-VAULT-01.", false),
        TimelineEvent("EVT-009", "2026-10-26T01:20:00Z", "UTC_EXPLICIT", "AUTH", "ENT-002", "EVD-002", "Marcus Vance authenticated via Remote Desktop (AD Event 4624)", "Admin session opened to begin pre-maintenance storage diagnostics.", false),
        TimelineEvent("EVT-010", "2026-10-26T01:22:15Z", "UTC_EXPLICIT", "PHYSICAL", "ENT-001", "EVD-003", "Dr. Elena Rostova entered South Lab Door", "Badge BDG-8812 scanned at entrance turnstile.", false),
        TimelineEvent("EVT-012", "2026-10-26T01:28:10Z", "UTC_EXPLICIT", "HARDWARE", "ENT-001", "EVD-027", "SanDisk 64GB USB storage device mounted on WS-LAB-04", "Removable media mounted by logged-in researcher for offline data backup.", false),
        TimelineEvent("EVT-013", "2026-10-26T01:30:10Z", "LOCAL_SYSTEM_TIME_UNCERTAIN", "SYSTEM_LOG", "ENT-001", "EVD-004", "Shell command executed on WS-LAB-04: rsync to local backup mount", "Command history captures manual rsync of working files to /mnt/backup.", false),
        TimelineEvent("EVT-014", "2026-10-26T01:32:00Z", "UTC_EXPLICIT", "CONFIG", "ENT-002", "EVD-013", "Directory permissions changed: chmod 777 /srv/quantum/specs", "Syscall audit captures wide permission modification to allow daemon rsync migration.", false),
        TimelineEvent("EVT-017", "2026-10-26T01:38:00Z", "UTC_EXPLICIT", "FILE_SYSTEM", "ENT-004", "EVD-005", "Ext4 journal recorded inode #482910 unlinked", "The file quantum_protocol_v3.pdf was unlinked during archive rotation.", false),
        TimelineEvent("EVT-018", "2026-10-26T01:40:00Z", "UTC_EXPLICIT", "AUTH", "ENT-005", "EVD-008", "mTLS handshake established with certificate CN=svc-daemon-guest", "Reverse proxy TLS session accepted; user account unknown in LDAP.", false),
        TimelineEvent("EVT-020", "2026-10-26T01:45:00Z", "UTC_EXPLICIT", "PHYSICAL", "ENT-001", "EVD-009", "Dr. Elena Rostova exited South Lab Door (Turnstile scan)", "Physical security access record confirms departure from facility at 01:45:00Z.", true, "2026-10-26T01:45:00Z"),
        TimelineEvent("EVT-022", "2026-10-26T01:47:15Z", "UTC_OFFSET_AFFECTED (+3200ms skew)", "SYSTEM_LOG", "ENT-001", "EVD-004", "[FLAGGED CONFLICT] Console activity attributed to Dr. Rostova on SVR-VAULT-01", "Timestamp 01:47:15 appears 2 min AFTER exit at 01:45. Caused by +3200ms NTP clock drift on SVR-VAULT-01.", true, "2026-10-26T01:44:02Z"),
        TimelineEvent("EVT-023", "2026-10-26T01:48:00Z", "UTC_EXPLICIT", "SYSTEM_LOG", "ENT-006", "EVD-023", "Chrony NTP alert logged clock drift (+3200ms skew on SVR-VAULT-01)", "Provides deterministic explanation for apparent timestamp conflict in EVT-022.", false),
        TimelineEvent("EVT-025", "2026-10-26T02:00:00Z", "UTC_EXPLICIT", "AUTOMATION", "ENT-004", "EVD-006", "Nightly sync cron job triggered automatically on SVR-VAULT-01", "Executed /usr/local/bin/nightly_sync.sh to push files to NAS-STORAGE-09.", false),
        TimelineEvent("EVT-026", "2026-10-26T02:02:10Z", "UTC_EXPLICIT", "NETWORK", "ENT-004", "EVD-007", "NetFlow recorded bulk data transfer (4.2GB) to NAS-STORAGE-09", "Outbound rsync stream transmitted storage contents across isolated backup VLAN.", false),
        TimelineEvent("EVT-028", "2026-10-26T02:40:00Z", "UTC_EXPLICIT", "FILE_INTEGRITY", "ENT-002", "EVD-012", "Recovered backup copy inspected — SHA-256 hash mismatch detected", "The recovered file quantum_protocol_v3_tampered.pdf failed hash comparison against reference digest.", true)
    )

    val seedHypotheses: List<Hypothesis> = listOf(
        Hypothesis(
            id = "HYP-A",
            title = "Hypothesis A: Authorized Maintenance & Storage Migration",
            summary = "The disappearance of quantum_protocol_v3.pdf was the benign byproduct of an authorized, scheduled maintenance operation executed by Systems Administrator Marcus Vance and the DevSecOps daemon, compounded by disk hardware degradation and an unsynchronized server clock.",
            supportingEvidence = listOf("EVD-002", "EVD-006", "EVD-007", "EVD-014", "EVD-020", "EVD-023"),
            contradictingEvidence = listOf("EVD-008", "EVD-012", "EVD-013"),
            neutralEvidence = listOf("EVD-003", "EVD-011"),
            missingInformation = listOf("Complete rsync transfer exit log from nightly_sync.sh on SVR-VAULT-01.", "Full uncompressed sector dump of failing drive /dev/sdb."),
            assumptions = listOf("Cron job deleted source file upon transfer due to --remove-source-files flag.", "Post-exit console activity is explained by NTP clock skew (+3200ms)."),
            unansweredQuestions = listOf("Why did the backup replica have a corrupted SHA-256 digest?", "Why was chmod 777 executed globally instead of service-specific permission?"),
            suggestedNextSteps = listOf("Inspect NAS-STORAGE-09 filesystem journals for block corruption.", "Review Marcus Vance's shell history to confirm backup arguments.")
        ),
        Hypothesis(
            id = "HYP-B",
            title = "Hypothesis B: Unrecognized Access Sequence & Exfiltration",
            summary = "An unauthorized party leveraged the external VPN split tunnel from 198.51.100.42, authenticated via uncataloged mTLS certificate (svc-daemon-guest), took advantage of elevated chmod 777 permissions, altered the quantum protocol, and removed the active file.",
            supportingEvidence = listOf("EVD-001", "EVD-008", "EVD-010", "EVD-012", "EVD-013", "EVD-027"),
            contradictingEvidence = listOf("EVD-014", "EVD-006", "EVD-020", "EVD-023"),
            neutralEvidence = listOf("EVD-002", "EVD-003"),
            missingInformation = listOf("Secondary packet capture log EVD-099 from intermediate switch.", "CA issuance log for client certificate CN=svc-daemon-guest."),
            assumptions = listOf("Connection from 198.51.100.42 was adversary-controlled.", "File mismatch indicates intentional tampering rather than sector corruption."),
            unansweredQuestions = listOf("Did the 4.2GB transfer reach public internet or strictly NAS-STORAGE-09?", "Who provisioned the mTLS certificate CN=svc-daemon-guest?"),
            suggestedNextSteps = listOf("Cryptographically validate client certificate CN=svc-daemon-guest against root CA.", "Examine firewall state tables for public egress.")
        )
    )

    val seedContradictions: List<ContradictionFinding> = listOf(
        ContradictionFinding(
            findingId = "FND-001",
            ruleId = "RULE-CONTR-001",
            ruleName = "Incompatible Physical Egress vs Console Activity",
            severity = "HIGH",
            sourceEvidenceIds = listOf("EVD-009", "EVD-004", "EVD-023"),
            supportingEventIds = listOf("EVT-020", "EVT-022"),
            ruleCondition = "timestamp(Console_Activity) > timestamp(Badge_Exit) FOR SAME ENTITY",
            observedValues = "Exit at 01:45:00 UTC vs Console at 01:47:15 UTC",
            triggeringReason = "Console activity attributed to Dr. Elena Rostova two minutes after physical departure scanned at South Lab turnstile.",
            alternativeExplanations = listOf("SVR-VAULT-01 was suffering from +3200ms NTP clock drift and delayed syslog buffer flushing.", "A background script finished execution after Dr. Rostova physically departed."),
            missingInformation = "Real-time process accounting logs with monotonic kernel timestamps.",
            suggestedAction = "Apply hypothetical timeline adjustment with NTP skew offset (-3200ms) to verify if commands occurred prior to exit.",
            limitations = "Timestamp correlation assumes both clock sources are stable."
        ),
        ContradictionFinding(
            findingId = "FND-002",
            ruleId = "RULE-CONTR-002",
            ruleName = "Cryptographic Hash Digest Mismatch",
            severity = "CRITICAL",
            sourceEvidenceIds = listOf("EVD-011", "EVD-012"),
            supportingEventIds = listOf("EVT-028"),
            ruleCondition = "Calculated_SHA256(Target_File) != Reference_SHA256(Original_Manifest)",
            observedValues = "Reference: 2e6c0ed1... | Calculated: cd894c54... (Files Differ)",
            triggeringReason = "The recovered file quantum_protocol_v3_tampered.pdf from the backup snapshot does not match the official reference digest.",
            alternativeExplanations = listOf("Deliberate modification of the specification file.", "Silent bit-rot or file header truncation during disk sector reallocation."),
            missingInformation = "Intermediate chunk-level block hashes from transfer stream.",
            suggestedAction = "Perform side-by-side hex diff inspection between original and recovered samples to isolate modified bytes.",
            limitations = "A hash mismatch proves the files differ, but cannot determine whether the difference was caused by malicious tampering, corruption, or incomplete transfer."
        ),
        ContradictionFinding(
            findingId = "FND-003",
            ruleId = "RULE-CONTR-003",
            ruleName = "Missing Source Evidence Reference in Event Sequence",
            severity = "MEDIUM",
            sourceEvidenceIds = listOf("EVD-099_MISSING"),
            supportingEventIds = listOf("EVT-008"),
            ruleCondition = "Event refers to Evidence_ID absent from loaded Evidence Manifest",
            observedValues = "Event EVT-008 references secondary switch packet capture 'EVD-099' which is unarchived",
            triggeringReason = "Packet capture evidence from secondary access switch was purged due to rolling buffer retention.",
            alternativeExplanations = listOf("Normal rolling buffer retention overwrote older packet captures.", "Logging level set to error only."),
            missingInformation = "Secondary switch raw PCAP packet stream for port Gi0/14.",
            suggestedAction = "Consult core firewall logs (EVD-001) as secondary corroborating network source.",
            limitations = "Absence of a log does not imply intentional destruction."
        ),
        ContradictionFinding(
            findingId = "FND-004",
            ruleId = "RULE-CONTR-004",
            ruleName = "Unknown Entity Reference in TLS Authentication",
            severity = "MEDIUM",
            sourceEvidenceIds = listOf("EVD-008"),
            supportingEventIds = listOf("EVT-018"),
            ruleCondition = "Client_Cert_CN NOT_IN Known_Personnel_Directory",
            observedValues = "CN=svc-daemon-guest not found in Active Directory or service account catalog",
            triggeringReason = "TLS client certificate was accepted by reverse proxy without LDAP account verification.",
            alternativeExplanations = listOf("Legacy contractor service account left behind during third-party audit.", "Adversary generated self-signed cert accepted by misconfigured trust store."),
            missingInformation = "Reverse proxy trusted CA bundle (/etc/ssl/certs/ca-certificates.crt).",
            suggestedAction = "Inspect Certificate Authority root trust store to locate issuing authority.",
            limitations = "Entity uncataloged status may reflect poor documentation rather than compromise."
        ),
        ContradictionFinding(
            findingId = "FND-005",
            ruleId = "RULE-CONTR-005",
            ruleName = "Incompatible File System Temporal Sequence",
            severity = "LOW",
            sourceEvidenceIds = listOf("EVD-005"),
            supportingEventIds = listOf("EVT-017"),
            ruleCondition = "Inode_Deallocation_Timestamp < Snapshot_Registration_Timestamp WHILE file present in snapshot metadata",
            observedValues = "Inode unlinked at 01:38:00 UTC while snapshot still lists pointer",
            triggeringReason = "Ext4 inode unlinked prior to snapshot, yet snapshot metadata retained entry due to caching.",
            alternativeExplanations = listOf("Snapshot tool read from asynchronous storage cache rather than live disk blocks."),
            missingInformation = "Storage cache flush log.",
            suggestedAction = "Check mount options for async vs sync write mode on SVR-VAULT-01.",
            limitations = "Filesystem caching artifacts frequently create temporal anomalies."
        ),
        ContradictionFinding(
            findingId = "FND-006",
            ruleId = "RULE-CONTR-006",
            ruleName = "Incomplete Evidence Provenance Chain",
            severity = "LOW",
            sourceEvidenceIds = listOf("EVD-012"),
            supportingEventIds = listOf("EVT-028"),
            ruleCondition = "Evidence_Record lacks Cryptographic_Signature OR Collection_Agent_Identity",
            observedValues = "EVD-012 recovered without formal forensic acquisition imaging",
            triggeringReason = "File collected during emergency response without formal forensic disk imaging.",
            alternativeExplanations = listOf("Rapid response protocol prioritized speed over formal disk imaging."),
            missingInformation = "Forensic write-blocker verification log and acquisition hash log.",
            suggestedAction = "Re-acquire disk image using write-blocking hardware.",
            limitations = "Prototype demonstration data. Does not satisfy legal chain of custody."
        ),
        ContradictionFinding(
            findingId = "FND-007",
            ruleId = "RULE-CONTR-007",
            ruleName = "NTP Clock Discrepancy Affecting Sequence Analysis",
            severity = "HIGH",
            sourceEvidenceIds = listOf("EVD-023"),
            supportingEventIds = listOf("EVT-022", "EVT-023"),
            ruleCondition = "NTP_Clock_Offset > 1000ms ON CRITICAL EVIDENCE SOURCE",
            observedValues = "Chrony reported +3200ms offset on SVR-VAULT-01",
            triggeringReason = "Significant clock skew invalidates naive millisecond ordering between server logs and external door badge logs.",
            alternativeExplanations = listOf("Virtual machine time drift following hypervisor live migration.", "NTP daemon network packet drop during power fluctuation (EVD-021)."),
            missingInformation = "Hypervisor host system clock logs.",
            suggestedAction = "Normalize server timestamps by subtracting 3200ms when reconstructing causal order.",
            limitations = "Time correction is an analytical hypothesis and must never overwrite original raw logs."
        )
    )

    val seedMissingItems: List<MissingEvidenceItem> = listOf(
        MissingEvidenceItem("MIS-001", "Secondary Switch Gi0/14 Full Packet Capture (EVD-099)", "BLOCKS_EXFILTRATION_CONCLUSION", listOf("HYP-A", "HYP-B"), listOf("RULE-CONTR-003"), "Without raw payload packets, the investigation cannot prove whether the 4.2GB transfer contained exfiltrated research files or routine compressed OS images.", "Check secondary network tap storage appliances or gateway netflow export archives."),
        MissingEvidenceItem("MIS-002", "Internal CA Certificate Revocation & Issuance Log", "REDUCES_CONTEXT", listOf("HYP-B"), listOf("RULE-CONTR-004"), "Determines whether CN=svc-daemon-guest was officially issued by lab management or forged.", "Query Microsoft CA / Vault PKI audit log for certificate serial 0x7A8812B."),
        MissingEvidenceItem("MIS-003", "Drive /dev/sdb Hardware Bad Block Inode Mapping", "REDUCES_CONTEXT", listOf("HYP-A"), listOf("RULE-CONTR-002", "RULE-CONTR-005"), "Confirms whether the 14 reallocated sectors physically coincided with inode #482910.", "Run badblocks -v /dev/sdb and cross-reference with debugfs inode block table."),
        MissingEvidenceItem("MIS-004", "Nightly Sync Shell Script Exact Execution Arguments", "BLOCKS_MAINTENANCE_CONCLUSION", listOf("HYP-A"), listOf("RULE-CONTR-005"), "Proves whether rsync was instructed to delete source files with --remove-source-files flag.", "Recover deleted /usr/local/bin/nightly_sync.sh from backup or inspect process accounting logs."),
        MissingEvidenceItem("MIS-005", "USB Storage Device SanDisk File Transfer History", "REDUCES_CONTEXT", listOf("HYP-B"), listOf("RULE-CONTR-001"), "Determines whether any files were written to the mounted USB drive at /mnt/usb before sleep.", "Obtain physical acquisition of SanDisk USB drive (Serial AA010928371) and inspect partition table.")
    )

    val seedTasks = listOf(
        ChallengeTask("TASK-001", "Detect Timestamp Inconsistency", "Locate the temporal contradiction between Dr. Rostova's physical badge exit and subsequent console logs.", 15, "FLAG_TIMESTAMP_SKEW_IDENTIFIED", "Examine EVD-009 (exit at 01:45 UTC) vs EVT-022 (console activity at 01:47 UTC) and review NTP log EVD-023."),
        ChallengeTask("TASK-002", "Verify SHA-256 Reference Mismatch", "Run the integrity verification engine against quantum_protocol_v3_tampered.pdf and confirm it differs from reference.", 20, "FLAG_INTEGRITY_MISMATCH_VERIFIED", "In the Integrity Verification view, run calculate on sample file quantum_protocol_v3_tampered.pdf."),
        ChallengeTask("TASK-003", "Trace Evidence Provenance Chain", "Trace the lifecycle path of evidence EVD-011 through analysis rules to Finding RULE-CONTR-002.", 15, "FLAG_PROVENANCE_CHAIN_TRACED", "Open the Provenance & Graph view and inspect the derivation nodes from Source Artifact to Report Statement."),
        ChallengeTask("TASK-004", "Evaluate Competing Hypotheses", "Review Hypothesis A vs Hypothesis B and mark at least two evidence items with reviewer annotations.", 20, "FLAG_HYPOTHESES_ANNOTATED", "In Hypothesis Lab, toggle between Hypothesis A and B, then set an annotation (Supports / Contradicts)."),
        ChallengeTask("TASK-005", "Identify Critical Missing Evidence", "Identify the missing packet capture (EVD-099) that blocks confirming unauthorized exfiltration.", 15, "FLAG_MISSING_EVIDENCE_IDENTIFIED", "Navigate to Missing-Evidence Analysis and inspect MIS-001."),
        ChallengeTask("TASK-006", "Generate Evidence-Linked Investigation Report", "Compile the complete investigation findings into a structured, evidence-linked report.", 15, "FLAG_REPORT_GENERATED", "Navigate to Report Generator, review all 17 forensic sections, and export or copy the report.")
    )

    // In-memory Fallback States (Synced with Room when DAO is provided)
    private val _evidenceState = MutableStateFlow(seedEvidenceRecords)
    val evidenceList: StateFlow<List<EvidenceRecord>> = _evidenceState.asStateFlow()

    private val _eventsState = MutableStateFlow(seedEvents)
    val eventsList: StateFlow<List<TimelineEvent>> = _eventsState.asStateFlow()

    private val _entitiesState = MutableStateFlow(seedEntities)
    val entitiesList: StateFlow<List<Entity>> = _entitiesState.asStateFlow()

    private val _hypothesesState = MutableStateFlow(seedHypotheses)
    val hypothesesList: StateFlow<List<Hypothesis>> = _hypothesesState.asStateFlow()

    private val _contradictionsState = MutableStateFlow(seedContradictions)
    val contradictionsList: StateFlow<List<ContradictionFinding>> = _contradictionsState.asStateFlow()

    private val _missingState = MutableStateFlow(seedMissingItems)
    val missingList: StateFlow<List<MissingEvidenceItem>> = _missingState.asStateFlow()

    private val _tasks = MutableStateFlow(seedTasks)
    val tasks: StateFlow<List<ChallengeTask>> = _tasks.asStateFlow()

    private val _annotations = MutableStateFlow<Map<String, ReviewerAnnotation>>(emptyMap())
    val annotations: StateFlow<Map<String, ReviewerAnnotation>> = _annotations.asStateFlow()

    private val _useHypothetical = MutableStateFlow(false)
    val useHypothetical: StateFlow<Boolean> = _useHypothetical.asStateFlow()

    private val _scrubIndex = MutableStateFlow(seedEvents.size - 1)
    val scrubIndex: StateFlow<Int> = _scrubIndex.asStateFlow()

    // Backwards-compatible public accessors for Composables
    val evidenceRecords: List<EvidenceRecord> get() = _evidenceState.value
    val events: List<TimelineEvent> get() = _eventsState.value
    val entities: List<Entity> get() = _entitiesState.value
    val hypotheses: List<Hypothesis> get() = _hypothesesState.value
    val contradictions: List<ContradictionFinding> get() = _contradictionsState.value
    val missingItems: List<MissingEvidenceItem> get() = _missingState.value

    init {
        forensicDao?.let { dao ->
            scope.launch {
                seedRoomDatabaseIfEmpty(dao)
                observeRoomDatabase(dao)
            }
        }
    }

    private suspend fun seedRoomDatabaseIfEmpty(dao: ForensicDao) {
        if (dao.getEvidenceCount() == 0) {
            dao.insertEvidence(seedEvidenceRecords.map { it.toEntity() })
            dao.insertEvents(seedEvents.map { it.toEntity() })
            dao.insertEntities(seedEntities.map { it.toEntity() })
            dao.insertHypotheses(seedHypotheses.map { it.toEntity() })
            dao.insertContradictions(seedContradictions.map { it.toEntity() })
            dao.insertMissingEvidence(seedMissingItems.map { it.toEntity() })
            dao.insertChallengeTasks(seedTasks.map { it.toEntity() })
        }
    }

    private fun observeRoomDatabase(dao: ForensicDao) {
        scope.launch {
            dao.getAllEvidence().collect { entities ->
                if (entities.isNotEmpty()) {
                    _evidenceState.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllEvents().collect { entities ->
                if (entities.isNotEmpty()) {
                    _eventsState.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllEntities().collect { entities ->
                if (entities.isNotEmpty()) {
                    _entitiesState.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllHypotheses().collect { entities ->
                if (entities.isNotEmpty()) {
                    _hypothesesState.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllContradictions().collect { entities ->
                if (entities.isNotEmpty()) {
                    _contradictionsState.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllMissingEvidence().collect { entities ->
                if (entities.isNotEmpty()) {
                    _missingState.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllChallengeTasks().collect { entities ->
                if (entities.isNotEmpty()) {
                    _tasks.value = entities.map { it.toDomain() }
                }
            }
        }
        scope.launch {
            dao.getAllAnnotations().collect { entities ->
                _annotations.value = entities.associate { 
                    "${it.hypothesisId}:${it.evidenceId}" to it.toDomain() 
                }
            }
        }
    }

    fun toggleHypothetical(enabled: Boolean) {
        _useHypothetical.value = enabled
    }

    fun setScrubIndex(index: Int) {
        _scrubIndex.value = index.coerceIn(0, (events.size - 1).coerceAtLeast(0))
    }

    fun annotateEvidence(hypothesisId: String, evidenceId: String, stance: String, notes: String = "") {
        val key = "$hypothesisId:$evidenceId"
        val updated = _annotations.value.toMutableMap()
        val annotation = ReviewerAnnotation(hypothesisId, evidenceId, stance, notes)
        updated[key] = annotation
        _annotations.value = updated

        // Persist to Room SQLite
        forensicDao?.let { dao ->
            scope.launch {
                dao.insertAnnotation(annotation.toEntity())
            }
        }
    }

    fun completeTask(taskId: String, notes: String = "") {
        _tasks.value = _tasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = true, userNotes = notes) else it
        }

        // Persist to Room SQLite
        forensicDao?.let { dao ->
            scope.launch {
                dao.updateTaskCompletion(taskId, isCompleted = true, notes = notes)
            }
        }
    }

    fun resetChallenge() {
        _tasks.value = seedTasks

        // Persist to Room SQLite
        forensicDao?.let { dao ->
            scope.launch {
                dao.resetChallengeTasks()
            }
        }
    }

    fun resetCase() {
        _tasks.value = seedTasks
        _annotations.value = emptyMap()
        _useHypothetical.value = false
        _scrubIndex.value = seedEvents.size - 1

        // Persist to Room SQLite
        forensicDao?.let { dao ->
            scope.launch {
                dao.resetChallengeTasks()
                dao.clearAnnotations()
                dao.clearAllVerifications()
            }
        }
    }

    fun calculateSha256(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifySampleOriginal(): IntegrityVerificationResult {
        val originalText = "# TraceIQ Nexus — Educational Digital Forensics Investigation Lab\nConnect the Clues. Challenge the Story. Verify the Evidence.\n\nCase ID: TQN-2026-001 (The Missing Research Files)\nSynthetic educational demonstration data only.\n"
        val calculated = calculateSha256(originalText)
        val reference = "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0"
        val isMatch = calculated.equals(reference, ignoreCase = true)
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

        val result = IntegrityVerificationResult(
            actionId = "VERIF-ORIG-01",
            filename = "quantum_protocol_v3.pdf",
            calculatedSha256 = calculated,
            referenceSha256 = reference,
            matchStatus = if (isMatch) "MATCHES_REFERENCE" else "DIFFERS_FROM_REFERENCE",
            statusText = if (isMatch) "Matches reference digest exactly" else "Calculated digest differs from reference",
            isMatch = isMatch,
            verifiedAt = now,
            forensicGuidance = "A matching digest indicates byte-level identity with supplied reference. It does not establish collection validity or author trustworthiness."
        )

        // Persist action to Room
        forensicDao?.let { dao ->
            scope.launch { dao.insertVerificationAction(result.toEntity()) }
        }

        return result
    }

    fun verifySampleTampered(): IntegrityVerificationResult {
        val tamperedText = "# TraceIQ Nexus — Educational Digital Forensics Investigation Lab\nConnect the Clues. Challenge the Story. Verify the Evidence.\n\nCase ID: TQN-2026-001 (The Missing Research Files)\nSynthetic educational demonstration data only.\n[MODIFIED HEADER: Block 0x4F overwritten with unauthorized payload string]\n"
        val calculated = calculateSha256(tamperedText)
        val reference = "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0"
        val isMatch = calculated.equals(reference, ignoreCase = true)
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

        val result = IntegrityVerificationResult(
            actionId = "VERIF-TAMP-02",
            filename = "quantum_protocol_v3_tampered.pdf",
            calculatedSha256 = calculated,
            referenceSha256 = reference,
            matchStatus = "DIFFERS_FROM_REFERENCE",
            statusText = "Calculated digest differs from reference manifest",
            isMatch = false,
            verifiedAt = now,
            forensicGuidance = "The recovered copy failed SHA-256 verification. Byte-level differences exist between target file and reference manifest."
        )

        // Persist action to Room
        forensicDao?.let { dao ->
            scope.launch { dao.insertVerificationAction(result.toEntity()) }
        }

        return result
    }

    companion object {
        fun createWithDatabase(context: Context): CaseRepository {
            val db = TraceIQDatabase.getInstance(context)
            return CaseRepository(forensicDao = db.forensicDao())
        }
    }
}
