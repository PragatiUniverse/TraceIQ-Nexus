"""Input validation helpers for TraceIQ Nexus."""
import re
from typing import Optional

HEX_64_REGEX = re.compile(r"^[a-fA-F0-9]{64}$")
ID_SAFE_REGEX = re.compile(r"^[A-Za-z0-9_\-]+$")

def validate_sha256_hex(digest: str) -> bool:
    if not digest or not isinstance(digest, str):
        return False
    return bool(HEX_64_REGEX.match(digest.strip()))

def validate_id(identifier: str) -> bool:
    if not identifier or not isinstance(identifier, str):
        return False
    return bool(ID_SAFE_REGEX.match(identifier.strip()))
