import os
from fastapi import FastAPI, UploadFile, File, BackgroundTasks, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from app.agent.context_store import ContextStore
from app.agent.agent_core import QATestAgent
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title='Browser-Use-NG Backend')

app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_methods=['*'],
    allow_headers=['*']
)

context_store = ContextStore()
agent = QATestAgent(context_store=context_store)

@app.post('/ingest_kt')
async def ingest_kt(file: UploadFile = File(...)):
    content = await file.read()
    try:
        text = content.decode('utf-8')
    except Exception:
        text = str(content)
    context_store.add_document(text, metadata={'source': file.filename})
    return {'status': 'ok', 'message': 'KT ingested'}

@app.post('/run_tests')
async def run_tests(background_tasks: BackgroundTasks):
    # kickoff background run
    background_tasks.add_task(agent.run_test_cycle)
    return {'status': 'started'}

@app.get('/status')
async def status():
    return agent.get_status()

@app.get('/recon')
async def recon():
    # quick DOM reconnaissance on a provided URL is not supported in this simple endpoint;
    # use run_tests which performs reconnaissance as part of planning.
    return {'status': 'ok', 'message': 'recon available in run_tests flow'}
