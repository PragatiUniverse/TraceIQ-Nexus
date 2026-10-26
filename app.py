"""TraceIQ Nexus Root Launcher.
Delegates directly to prototype/app.py for seamless developer onboarding.
"""
import os
import sys

# Add prototype to sys.path
prototype_dir = os.path.join(os.path.dirname(__file__), "prototype")
sys.path.insert(0, prototype_dir)

from app import create_app

if __name__ == "__main__":
    app = create_app()
    port = int(os.getenv("PORT", 5000))
    print(f"\n[TraceIQ Nexus Root Launcher] Launching prototype application...")
    print(f"URL: http://127.0.0.1:{port}\n")
    app.run(host="127.0.0.1", port=port, debug=False)
