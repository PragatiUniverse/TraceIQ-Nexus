"""Unit tests for Provenance and Missing Evidence Engines."""
import unittest
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from traceiq.provenance import ProvenanceEngine, GraphBuilder
from traceiq.missing_evidence import MissingEvidenceEngine

class TestProvenanceAndMissing(unittest.TestCase):
    def setUp(self):
        self.prov = ProvenanceEngine()
        self.graph = GraphBuilder()
        self.missing = MissingEvidenceEngine()

    def test_provenance_chain_structure(self):
        chains = self.prov.get_provenance_chains()
        self.assertGreaterEqual(len(chains), 25)
        
        c1 = chains[0]
        self.assertIn("provenance_pipeline", c1)
        self.assertEqual(len(c1["provenance_pipeline"]), 5)

    def test_graph_elements_cytoscape_format(self):
        elements = self.graph.build_cytoscape_elements()
        self.assertIn("nodes", elements)
        self.assertIn("edges", elements)
        self.assertGreater(len(elements["nodes"]), 10)
        self.assertGreater(len(elements["edges"]), 10)

    def test_missing_evidence_taxonomy(self):
        items = self.missing.get_missing_items()
        self.assertGreaterEqual(len(items), 4)
        m1 = items[0]
        self.assertIn("taxonomy", m1)
        self.assertIn("impact_level", m1)

if __name__ == "__main__":
    unittest.main()
