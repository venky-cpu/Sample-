# Browser-Use-NG (Angular 20 + Backend)

This repo contains a modern Angular 20 frontend (Material + Tailwind) and a FastAPI backend
that uses Playwright for browser automation and ChromaDB for context storage. The LLM
integration uses the OpenAI Python SDK pointing to a Mistral-compatible endpoint.

## Requirements
- Node 22 (LTS) for frontend
- Python 3.10+ for backend
- Playwright browsers installed via `python -m playwright install`

## Setup Backend
1. cd backend
2. python3 -m venv .venv
3. source .venv/bin/activate
4. pip install -r requirements.txt
5. python -m playwright install
6. cp .env.example .env and set MODEL_API_KEY and other env vars
7. uvicorn app.main:app --reload --port 8000

## Setup Frontend
1. Ensure Node 22 is installed
2. cd frontend-angular20
3. npm install
4. npx ng serve --open
5. Frontend will open at http://localhost:4200 and communicate with backend at http://localhost:8000

## Flow
- Upload KT via frontend -> stored into ChromaDB
- Run tests -> backend performs reconnaissance and asks LLM for JSON test plan
- Playwright executes plan and saves screenshots; frontend polls /status every 3s to show logs

## Notes & Next Steps
- Add JSON schema validation for LLM responses
- Improve DOM mapping and selector heuristics
- Add screenshot viewer in frontend

