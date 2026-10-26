"""Unit tests for SHA-256 Integrity Engine."""
import unittest
import os
import sys

# Ensure prototype is in path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from traceiq.integrity import IntegrityEngine

class TestIntegrityEngine(unittest.TestCase):
    def setUp(self):
        self.engine = IntegrityEngine()

    def test_sha256_known_bytes(self):
        # Known hash of b"TraceIQ"
        res = self.engine.calculate_sha256_bytes(b"TraceIQ")
        self.assertEqual(len(res), 64)
        self.assertTrue(all(c in "0123456789abcdef" for c in res))

    def test_verify_original_sample_matches(self):
        res = self.engine.verify_sample_original()
        self.assertEqual(res["match_status"], "MATCHES_REFERENCE")
        self.assertTrue(res["is_match"])

    def test_verify_tampered_sample_detects_mismatch(self):
        res = self.engine.verify_sample_tampered()
        self.assertEqual(res["match_status"], "DIFFERS_FROM_REFERENCE")
        self.assertFalse(res["is_match"])

    def test_invalid_reference_hex_format(self):
        with self.assertRaises(ValueError):
            self.engine.verify_file(filename="test.txt", data_bytes=b"data", reference_sha256="invalid_hex_string")

    def test_oversize_payload_boundary(self):
        oversized = b"A" * (11 * 1024 * 1024)
        with self.assertRaises(ValueError):
            self.engine.verify_file(filename="big.txt", data_bytes=oversized)

if __name__ == "__main__":
    unittest.main()
