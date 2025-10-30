import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HttpClientModule, MatButtonModule, MatCardModule, MatIconModule],
  providers: [ApiService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Browser-Use-NG';
  status = signal<any>({});
  ktFile: File | null = null;
  polling = signal(false);
  pollHandle: any = null;

  constructor(private api: ApiService) {}

  onFileChange(e: any) {
    const f = e.target.files[0];
    this.ktFile = f;
  }

  async uploadKT() {
    if (!this.ktFile) return alert('Select a KT file first');
    const res = await this.api.uploadKT(this.ktFile);
    alert(JSON.stringify(res));
  }

  async runTests() {
    const res = await this.api.runTests();
    alert(JSON.stringify(res));
    this.startPolling();
  }

  async pollStatusOnce() {
    const s = await this.api.getStatus();
    this.status.set(s);
  }

  startPolling() {
    if (this.polling()) return;
    this.polling.set(true);
    this.pollHandle = setInterval(() => this.pollStatusOnce(), 3000);
  }

  stopPolling() {
    if (!this.polling()) return;
    clearInterval(this.pollHandle);
    this.polling.set(false);
  }
}
