"""Unit tests for Evidence Time Machine Engine."""
import unittest
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from traceiq.timeline_engine import TimelineEngine

class TestTimelineEngine(unittest.TestCase):
    def setUp(self):
        self.engine = TimelineEngine()

    def test_chronological_event_order(self):
        events = self.engine.get_timeline_events(use_hypothetical=False)
        self.assertGreaterEqual(len(events), 25)
        
        # Verify monotonically non-decreasing timestamps
        for i in range(len(events) - 1):
            t1 = events[i]["effective_timestamp"]
            t2 = events[i+1]["effective_timestamp"]
            self.assertLessEqual(t1, t2)

    def test_hypothetical_time_adjustment(self):
        raw_events = {e["id"]: e for e in self.engine.get_timeline_events(use_hypothetical=False)}
        hyp_events = {e["id"]: e for e in self.engine.get_timeline_events(use_hypothetical=True)}
        
        # EVT-022 should be adjusted in hypothetical model
        self.assertIn("EVT-022", raw_events)
        self.assertNotEqual(
            raw_events["EVT-022"]["effective_timestamp"],
            hyp_events["EVT-022"]["effective_timestamp"]
        )

    def test_replay_frame_bounds(self):
        frame = self.engine.get_replay_frame(5)
        self.assertEqual(len(frame["events"]), 6)
        self.assertEqual(frame["current_index"], 5)
        self.assertIn("progress_percent", frame)

if __name__ == "__main__":
    unittest.main()
