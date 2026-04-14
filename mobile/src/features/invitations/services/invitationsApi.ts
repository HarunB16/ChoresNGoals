import { ApiError, getJson, postJson } from "../../../services/apiClient";
import { getAccessToken } from "../../auth/services/tokenStorage";

export type Family = {
  id: string;
  name: string;
  createdAt: string;
};

export type ChildInvitation = {
  id: string;
  familyId: string;
  invitedEmail: string;
  token: string;
  status: "PENDING";
  expiresAt: string;
  createdAt: string;
};

export async function getCurrentFamily(): Promise<Family> {
  return getJson<Family>("/api/families/current", await getAuthHeaders());
}

export async function inviteChildByEmail(
  familyId: string,
  email: string
): Promise<ChildInvitation> {
  return postJson<ChildInvitation>(
    `/api/families/${familyId}/child-invitations`,
    { email },
    await getAuthHeaders()
  );
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
