"""Unit tests for Contradiction Hunter Engine."""
import unittest
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from traceiq.contradiction_engine import ContradictionEngine

class TestContradictionEngine(unittest.TestCase):
    def setUp(self):
        self.engine = ContradictionEngine()

    def test_evaluate_all_rules_returns_seven_findings(self):
        findings = self.engine.evaluate_all_rules()
        self.assertEqual(len(findings), 7)

    def test_finding_contains_explainability_block(self):
        findings = self.engine.evaluate_all_rules()
        for f in findings:
            self.assertIn("explainability", f)
            self.assertIn("rule_condition", f["explainability"])
            self.assertIn("observed_values", f["explainability"])
            self.assertIn("limitations", f["explainability"])

    def test_get_finding_by_id(self):
        f = self.engine.get_finding_by_id("RULE-CONTR-001")
        self.assertIsNotNone(f)
        self.assertEqual(f["severity"], "HIGH")

if __name__ == "__main__":
    unittest.main()
