"""Unit tests for Multiple-Hypothesis Lab Engine."""
import unittest
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from traceiq.hypothesis_engine import HypothesisEngine
from traceiq.models import init_db

class TestHypothesisEngine(unittest.TestCase):
    def setUp(self):
        init_db()
        self.engine = HypothesisEngine()

    def test_get_competing_hypotheses(self):
        hypotheses = self.engine.get_hypotheses()
        self.assertGreaterEqual(len(hypotheses), 2)
        h_ids = [h["id"] for h in hypotheses]
        self.assertIn("HYP-A", h_ids)
        self.assertIn("HYP-B", h_ids)

    def test_save_and_retrieve_reviewer_annotation(self):
        res = self.engine.save_reviewer_annotation(
            case_id="TQN-2026-001",
            hypothesis_id="HYP-A",
            evidence_id="EVD-011",
            stance="SUPPORTS",
            notes="Documented research protocol draft"
        )
        self.assertEqual(res["status"], "success")
        
        hyp_a = self.engine.get_hypothesis_by_id("HYP-A")
        self.assertIsNotNone(hyp_a)
        stances = [a["stance"] for a in hyp_a["reviewer_annotations"] if a["evidence_id"] == "EVD-011"]
        self.assertIn("SUPPORTS", stances)

if __name__ == "__main__":
    unittest.main()
