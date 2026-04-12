import { getJson } from "./apiClient";

export type HealthResponse = {
  status: string;
  service: string;
  timestamp: string;
};

export function getBackendHealth(): Promise<HealthResponse> {
  return getJson<HealthResponse>("/api/health");
}
