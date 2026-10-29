export type DelayReason =
  | 'MISSED_TRANSPORT'
  | 'TRANSPORT_LATE'
  | 'HEAVY_TRAFFIC';

export interface TransportLocation {
  latitude: number;
  longitude: number;
  vehicleLabel: string;
  routeName: string;
  etaMinutes: number | null;
  updatedAt: string;
}

export interface DelayReportRequest {
  employeeNumber: string;
  company: string;
  reason: DelayReason;
}

export interface AccidentReportRequest {
  employeeNumber: string;
  company: string;
  details: string;
}

export interface ReportReceipt {
  reportId: string;
  status: string;
  createdAt: string;
}
