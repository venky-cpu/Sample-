import { Injectable } from '@angular/core';

@Injectable()
export class ApiService {
  base = 'http://localhost:8000';

  async uploadKT(file: File) {
    const fd = new FormData();
    fd.append('file', file, file.name);
    const res = await fetch(`${this.base}/ingest_kt`, { method: 'POST', body: fd });
    return res.json();
  }

  async runTests() {
    const res = await fetch(`${this.base}/run_tests`, { method: 'POST' });
    return res.json();
  }

  async getStatus() {
    const res = await fetch(`${this.base}/status`);
    return res.json();
  }
}
