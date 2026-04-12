import { ApiError, getJson, postJson } from "../../../services/apiClient";
import { getAccessToken } from "../../auth/services/tokenStorage";

export type Child = {
  id: string;
  name: string;
  birthYear: number;
  avatarColor: string;
  createdAt: string;
};

export type CreateChildInput = {
  name: string;
  birthYear: number;
  avatarColor: string;
};

export async function getChildren(): Promise<Child[]> {
  return getJson<Child[]>("/api/children", await getAuthHeaders());
}

export async function createChild(input: CreateChildInput): Promise<Child> {
  return postJson<Child>("/api/children", input, await getAuthHeaders());
}

async function getAuthHeaders(): Promise<HeadersInit> {
  const accessToken = await getAccessToken();

  if (!accessToken) {
    throw new ApiError("Login is required", 401);
  }

  return {
    Authorization: `Bearer ${accessToken}`
  };
}
