from chromadb import Client
from chromadb.config import Settings

class ContextStore:
    def __init__(self):
        # in-memory ChromaDB (non-persistent)
        self.client = Client(settings=Settings(is_persistent=False))
        try:
            self.col = self.client.create_collection(name='kt_collection')
        except Exception:
            self.col = self.client.get_collection(name='kt_collection')

    def add_document(self, text: str, metadata: dict = None):
        _id = str(abs(hash(text)))[0:32]
        try:
            self.col.add(ids=[_id], documents=[text], metadatas=[metadata or {}])
        except Exception:
            # fallback: upsert
            self.col.add(ids=[_id], documents=[text], metadatas=[metadata or {}])

    def query(self, query_text: str, k: int = 4):
        res = self.col.query(query_texts=[query_text], n_results=k)
        return res
