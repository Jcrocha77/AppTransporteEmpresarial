import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccidentReportRequest,
  DelayReportRequest,
  ReportReceipt,
  TransportLocation
} from './models';

@Injectable({ providedIn: 'root' })
export class TransportApiService {
  private readonly http = inject(HttpClient);

  getLocation(employeeNumber: string, company: string): Observable<TransportLocation> {
    return this.http.post<TransportLocation>('/api/transport/location', {
      employeeNumber: employeeNumber.trim(),
      company: company.trim()
    });
  }

  reportDelay(report: DelayReportRequest): Observable<ReportReceipt> {
    return this.http.post<ReportReceipt>('/api/reports/delay', report);
  }

  reportAccident(report: AccidentReportRequest): Observable<ReportReceipt> {
    return this.http.post<ReportReceipt>('/api/reports/accident', report);
  }

  reportDirtyUnit(employeeNumber: string, company: string, description: string, images: File[]): Observable<ReportReceipt> {
    const formData = new FormData();
    formData.append('employeeNumber', employeeNumber.trim());
    formData.append('company', company.trim());
    formData.append('description', description.trim());
    images.forEach((image) => formData.append('images', image, image.name));
    return this.http.post<ReportReceipt>('/api/reports/dirty-unit', formData);
  }
}
