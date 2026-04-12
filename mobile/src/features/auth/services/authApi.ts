import { postJson } from "../../../services/apiClient";

export type LoginCredentials = {
  email: string;
  password: string;
};

export type AuthenticatedParent = {
  id: string;
  fullName: string;
  email: string;
  role: "PARENT";
  createdAt: string;
};

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresInSeconds: number;
  user: AuthenticatedParent;
};

export function loginParent(credentials: LoginCredentials): Promise<LoginResponse> {
  return postJson<LoginResponse>("/api/auth/parents/login", credentials);
}
