"""Integrity Verification Engine for Feature 5."""
import hashlib
import os
import re
import uuid
import datetime
from typing import Dict, Any, Optional
from traceiq.models import get_db_connection

MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024  # 10 MB strict limit
HEX_DIGEST_REGEX = re.compile(r"^[a-fA-F0-9]{64}$")

class IntegrityEngine:
    def __init__(self):
        pass

    def get_sample_dir(self) -> str:
        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        return os.path.join(base_dir, "data", "sample_evidence")

    def calculate_sha256_bytes(self, data: bytes) -> str:
        h = hashlib.sha256()
        h.update(data)
        return h.hexdigest().lower()

    def calculate_sha256_file(self, file_path: str) -> Dict[str, Any]:
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"File not found: {file_path}")
        
        file_size = os.path.getsize(file_path)
        if file_size > MAX_FILE_SIZE_BYTES:
            raise ValueError(f"File size ({file_size} bytes) exceeds safety limit of {MAX_FILE_SIZE_BYTES} bytes.")
            
        h = hashlib.sha256()
        with open(file_path, "rb") as f:
            while chunk := f.read(65536):
                h.update(chunk)
                
        return {
            "file_size_bytes": file_size,
            "sha256": h.hexdigest().lower()
        }

    def verify_file(
        self,
        filename: str,
        data_bytes: Optional[bytes] = None,
        file_path: Optional[str] = None,
        reference_sha256: Optional[str] = None
    ) -> Dict[str, Any]:
        action_id = f"VERIF-{uuid.uuid4().hex[:8].upper()}"
        now = datetime.datetime.now(datetime.timezone.utc).isoformat()
        
        if data_bytes is not None:
            file_size = len(data_bytes)
            if file_size > MAX_FILE_SIZE_BYTES:
                raise ValueError("Payload exceeds safety size limit (10MB).")
            calculated_hash = self.calculate_sha256_bytes(data_bytes)
        elif file_path is not None:
            res = self.calculate_sha256_file(file_path)
            file_size = res["file_size_bytes"]
            calculated_hash = res["sha256"]
        else:
            raise ValueError("Either data_bytes or file_path must be supplied.")

        # Clean reference hash
        ref_clean = reference_sha256.strip().lower() if reference_sha256 else None
        if ref_clean and not HEX_DIGEST_REGEX.match(ref_clean):
            raise ValueError("Invalid reference digest format. SHA-256 digest must be exactly 64 hexadecimal characters.")

        if not ref_clean:
            match_status = "REFERENCE_UNAVAILABLE"
            status_text = "Reference digest not provided"
            is_match = None
        elif calculated_hash == ref_clean:
            match_status = "MATCHES_REFERENCE"
            status_text = "Matches reference digest exactly"
            is_match = True
        else:
            match_status = "DIFFERS_FROM_REFERENCE"
            status_text = "Calculated digest differs from reference"
            is_match = False

        # Record in database
        try:
            conn = get_db_connection()
            cur = conn.cursor()
            cur.execute("""
            INSERT INTO verification_actions (action_id, filename, file_size_bytes, calculated_sha256, reference_sha256, match_status, verified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, (action_id, filename, file_size, calculated_hash, ref_clean, match_status, now))
            conn.commit()
            conn.close()
        except Exception:
            pass

        return {
            "action_id": action_id,
            "filename": filename,
            "file_size_bytes": file_size,
            "calculated_sha256": calculated_hash,
            "reference_sha256": ref_clean,
            "match_status": match_status,
            "status_text": status_text,
            "is_match": is_match,
            "verified_at": now,
            "forensic_guidance": "A matching digest indicates byte-level identity with the supplied reference. It does not establish the trustworthiness of the reference, creator identity, creation time, or collection integrity."
        }

    def verify_sample_original(self) -> Dict[str, Any]:
        path = os.path.join(self.get_sample_dir(), "quantum_protocol_v3.pdf")
        ref = "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0"
        return self.verify_file(filename="quantum_protocol_v3.pdf", file_path=path, reference_sha256=ref)

    def verify_sample_tampered(self) -> Dict[str, Any]:
        path = os.path.join(self.get_sample_dir(), "quantum_protocol_v3_tampered.pdf")
        # Reference is the original authentic hash, so this will detect mismatch
        ref = "2e6c0ed1e24cb07944da1d22a2c9d0c7334837cb1c14dd308e46d17e0dd0b6b0"
        return self.verify_file(filename="quantum_protocol_v3_tampered.pdf", file_path=path, reference_sha256=ref)
