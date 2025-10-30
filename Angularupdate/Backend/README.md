Backend FastAPI service for Browser-Use-NG.

Quick start (local):
1. Create and activate venv:
   python3 -m venv .venv
   source .venv/bin/activate
2. Install requirements:
   pip install -r requirements.txt
3. Install Playwright browsers:
   python -m playwright install
4. Copy .env.example -> .env and set MODEL_API_KEY and other vars.
5. Run server:
   uvicorn app.main:app --reload --port 8000

Notes:
- The backend uses the OpenAI Python SDK configured to point at a custom base URL (Mistral).
- ChromaDB is used in-memory for KT ingestion.
- Screenshots are saved to a local 'screenshots/' directory.


Note: Frontend requires Node 22+. Backend requires Python 3.10+.
