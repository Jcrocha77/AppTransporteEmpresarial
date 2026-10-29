import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { Subscription } from 'rxjs';
import { AccidentReportRequest, DelayReason, ReportReceipt, TransportLocation } from './models';
import { TransportApiService } from './transport-api.service';

type MainView = 'trip' | 'reports';
type ReportType = 'delay' | 'accident' | 'dirty' | null;
type LocationState = 'idle' | 'loading' | 'loaded' | 'unavailable' | 'error';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnDestroy {
  @ViewChild('mapCanvas') private mapCanvas?: ElementRef<HTMLDivElement>;

  private readonly api = inject(TransportApiService);
  private map?: L.Map;
  private marker?: L.Marker;
  private readonly subscriptions = new Subscription();
  readonly activeView = signal<MainView>('trip');
  readonly activeReport = signal<ReportType>(null);
  readonly location = signal<TransportLocation | null>(null);
  readonly locationState = signal<LocationState>('idle');
  readonly locationMessage = signal('Ingresa tus datos para consultar la ubicación de tu transporte.');
  readonly lastChecked = signal<Date | null>(null);
  readonly reportMessage = signal('');
  readonly reportError = signal('');
  readonly reportBusy = signal(false);
  readonly selectedImages = signal<File[]>([]);
  readonly imagePreviewUrls = signal<string[]>([]);

  employeeNumber = '';
  company = '';
  delayReason: DelayReason | '' = '';
  accidentDetails = '';
  dirtyDescription = '';

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.disposeImages();
    this.map?.remove();
  }

  findTransport(): void {
    if (!this.employeeNumber.trim() || !this.company.trim()) {
      this.locationMessage.set('Completa tu número de empleado y empresa para continuar.');
      this.locationState.set('error');
      return;
    }

    this.map?.remove();
    this.map = undefined;
    this.marker = undefined;
    this.location.set(null);
    this.locationState.set('loading');
    this.locationMessage.set('Consultando ubicación actual…');
    this.subscriptions.add(this.api.getLocation(this.employeeNumber, this.company).subscribe({
      next: (result) => {
        this.location.set(result);
        this.locationState.set('loaded');
        this.locationMessage.set('Ubicación recibida.');
        this.lastChecked.set(new Date());
        window.setTimeout(() => this.renderMap(result), 0);
      },
      error: (error: unknown) => {
        this.location.set(null);
        if (error instanceof HttpErrorResponse && error.status === 503) {
          this.locationState.set('unavailable');
          this.locationMessage.set('La conexión con el servicio de ubicación todavía no está habilitada.');
        } else {
          this.locationState.set('error');
          this.locationMessage.set('No pudimos consultar el transporte. Revisa tus datos e inténtalo nuevamente.');
        }
      }
    }));
  }

  openView(view: MainView): void {
    this.activeView.set(view);
    this.activeReport.set(null);
    this.clearReportFeedback();
    if (view === 'reports') {
      this.map?.remove();
      this.map = undefined;
      this.marker = undefined;
    } else if (this.location()) {
      window.setTimeout(() => {
        const currentLocation = this.location();
        if (currentLocation) this.renderMap(currentLocation);
      }, 0);
    }
  }

  openReport(type: ReportType): void {
    this.activeReport.set(type);
    this.clearReportFeedback();
    this.delayReason = '';
    if (type !== 'dirty') this.clearImages();
  }

  submitDelay(): void {
    if (!this.validateIdentity() || !this.delayReason) {
      this.reportError.set('Completa tus datos y selecciona el motivo del retraso.');
      return;
    }

    this.sendReport(this.api.reportDelay({
      employeeNumber: this.employeeNumber.trim(),
      company: this.company.trim(),
      reason: this.delayReason
    }), 'Tu reporte de retraso quedó registrado.');
  }

  submitAccident(): void {
    if (!this.validateIdentity()) return;
    const report: AccidentReportRequest = {
      employeeNumber: this.employeeNumber.trim(),
      company: this.company.trim(),
      details: this.accidentDetails.trim()
    };
    this.sendReport(this.api.reportAccident(report), 'El aviso de accidente fue enviado.');
  }

  submitDirtyUnit(): void {
    if (!this.validateIdentity()) return;
    if (!this.dirtyDescription.trim()) {
      this.reportError.set('Describe brevemente el estado de la unidad.');
      return;
    }
    if (this.dirtyDescription.length > 150) {
      this.reportError.set('La descripción debe tener 150 caracteres o menos.');
      return;
    }
    if (this.selectedImages().length > 3) {
      this.reportError.set('Puedes adjuntar hasta 3 imágenes.');
      return;
    }

    this.sendReport(this.api.reportDirtyUnit(
      this.employeeNumber,
      this.company,
      this.dirtyDescription,
      this.selectedImages()
    ), 'El reporte de unidad sucia quedó registrado.');
  }

  onImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const incoming = Array.from(input.files ?? []);
    const invalidFile = incoming.some((file) => !file.type.startsWith('image/'));
    const availableSlots = Math.max(0, 3 - this.selectedImages().length);
    const accepted = incoming.filter((file) => file.type.startsWith('image/')).slice(0, availableSlots);

    if (invalidFile) this.reportError.set('Solo puedes seleccionar archivos de imagen.');
    else if (incoming.length > availableSlots) this.reportError.set('El límite es de 3 imágenes por reporte.');
    else this.reportError.set('');

    const urls = accepted.map((file) => URL.createObjectURL(file));
    this.selectedImages.update((files) => [...files, ...accepted]);
    this.imagePreviewUrls.update((previews) => [...previews, ...urls]);
    input.value = '';
  }

  removeImage(index: number): void {
    const urls = this.imagePreviewUrls();
    if (urls[index]) URL.revokeObjectURL(urls[index]);
    this.selectedImages.update((files) => files.filter((_, fileIndex) => fileIndex !== index));
    this.imagePreviewUrls.update((previews) => previews.filter((_, previewIndex) => previewIndex !== index));
    this.reportError.set('');
  }

  delayLabel(reason: DelayReason): string {
    const labels: Record<DelayReason, string> = {
      MISSED_TRANSPORT: 'se me hizo tarde y no alcancé el transporte',
      TRANSPORT_LATE: 'el transporte llegó tarde',
      HEAVY_TRAFFIC: 'tráfico denso'
    };
    return labels[reason];
  }

  private validateIdentity(): boolean {
    if (this.employeeNumber.trim() && this.company.trim()) return true;
    this.reportError.set('Ingresa tu número de empleado y empresa antes de enviar el reporte.');
    return false;
  }

  private sendReport(request: ReturnType<TransportApiService['reportDelay']> | ReturnType<TransportApiService['reportAccident']> | ReturnType<TransportApiService['reportDirtyUnit']>, successMessage: string): void {
    this.reportBusy.set(true);
    this.clearReportFeedback();
    this.subscriptions.add(request.subscribe({
      next: (_receipt: ReportReceipt) => {
        this.reportBusy.set(false);
        this.reportMessage.set(successMessage);
        this.delayReason = '';
        this.accidentDetails = '';
        this.dirtyDescription = '';
        this.clearImages();
      },
      error: (error: unknown) => {
        this.reportBusy.set(false);
        this.reportError.set(error instanceof HttpErrorResponse && error.status === 503
          ? 'Esta integración todavía no está habilitada. Tu reporte no se envió.'
          : 'No pudimos enviar el reporte. Inténtalo nuevamente.');
      }
    }));
  }

  private renderMap(location: TransportLocation): void {
    const element = this.mapCanvas?.nativeElement;
    if (!element) return;

    const point: L.LatLngExpression = [location.latitude, location.longitude];
    if (!this.map) {
      this.map = L.map(element, { zoomControl: false, scrollWheelZoom: false }).setView(point, 15);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap'
      }).addTo(this.map);
      L.control.zoom({ position: 'bottomright' }).addTo(this.map);
    } else {
      this.map.setView(point, 15);
    }

    this.marker?.remove();
    this.marker = L.marker(point, {
      icon: L.divIcon({
        className: 'vehicle-marker-shell',
        html: '<span class="vehicle-marker"><span></span></span>',
        iconSize: [34, 42],
        iconAnchor: [17, 38]
      })
    }).addTo(this.map).bindPopup(location.vehicleLabel || 'Transporte asignado');
    window.setTimeout(() => this.map?.invalidateSize(), 100);
  }

  private clearReportFeedback(): void {
    this.reportMessage.set('');
    this.reportError.set('');
  }

  private clearImages(): void {
    this.disposeImages();
    this.selectedImages.set([]);
    this.imagePreviewUrls.set([]);
  }

  private disposeImages(): void {
    this.imagePreviewUrls().forEach((url) => URL.revokeObjectURL(url));
  }
}
