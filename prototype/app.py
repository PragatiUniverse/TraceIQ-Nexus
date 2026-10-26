"""TraceIQ Nexus Application Entrypoint."""
import os
import sys

# Ensure prototype root is in Python module search path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from flask import Flask
from traceiq.models import init_db
from traceiq.routes import bp

def create_app():
    app = Flask(
        __name__,
        template_folder=os.path.join(os.path.dirname(__file__), "templates"),
        static_folder=os.path.join(os.path.dirname(__file__), "static")
    )
    app.config["SECRET_KEY"] = os.getenv("SECRET_KEY", "traceiq-nexus-educational-key-2026")
    
    # Initialize SQLite database tables
    init_db()
    
    app.register_blueprint(bp)
    return app

if __name__ == "__main__":
    app = create_app()
    port = int(os.getenv("PORT", 5000))
    print(f"\n=======================================================")
    print(f"  TRACEIQ NEXUS — FORENSIC INVESTIGATION LAB")
    print(f"  Connect the Clues. Challenge the Story. Verify the Evidence.")
    print(f"  Listening on: http://127.0.0.1:{port}")
    print(f"=======================================================\n")
    app.run(host="127.0.0.1", port=port, debug=False)
