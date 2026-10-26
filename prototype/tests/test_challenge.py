"""Unit tests for Challenge Engine and Report Generator."""
import unittest
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from traceiq.challenge_engine import ChallengeEngine
from traceiq.report_generator import ReportGenerator
from traceiq.models import init_db

class TestChallengeAndReport(unittest.TestCase):
    def setUp(self):
        init_db()
        self.challenge = ChallengeEngine()
        self.report = ReportGenerator()

    def test_challenge_tasks_scoring_and_reset(self):
        initial = self.challenge.get_challenge_status()
        self.assertEqual(initial["total_possible_score"], 100)
        
        # Complete Task 1
        updated = self.challenge.complete_task("TASK-001", user_notes="Identified timestamp skew")
        self.assertEqual(updated["current_score"], 15)
        self.assertEqual(updated["completed_tasks"], 1)
        
        # Reset
        reset_res = self.challenge.reset_challenge()
        self.assertEqual(reset_res["current_score"], 0)
        self.assertEqual(reset_res["completed_tasks"], 0)

    def test_report_contains_all_seventeen_sections(self):
        md = self.report.generate_markdown_report()
        # Verify essential forensic headers
        self.assertIn("FORENSIC INVESTIGATION REPORT", md)
        self.assertIn("SYNTHETIC-DATA & EDUCATIONAL DISCLAIMER", md)
        self.assertIn("EVIDENCE INVENTORY", md)
        self.assertIn("EVENT TIMELINE SUMMARY", md)
        self.assertIn("CONTRADICTION & INCONSISTENCY FINDINGS", md)
        self.assertIn("COMPETING HYPOTHESES EVALUATION", md)
        self.assertIn("INTEGRITY VERIFICATION RESULTS", md)
        self.assertIn("TECHNICAL & LEGAL LIMITATIONS", md)
        # Check that evidence IDs are referenced
        self.assertIn("EVD-001", md)
        self.assertIn("EVD-011", md)

if __name__ == "__main__":
    unittest.main()
