import { ApiError, getJson, postJson } from "../../../services/apiClient";
import { getAccessToken } from "../../auth/services/tokenStorage";

export type TaskType = "daily" | "weekly" | "monthly" | "one_time";

export type TaskStatus = "pending";

export type ChildTask = {
  id: string;
  childId: string;
  title: string;
  description: string;
  type: TaskType;
  status: TaskStatus;
  points: number;
  dueDate: string;
  createdAt: string;
};

export type CreateTaskInput = {
  title: string;
  description: string;
  type: TaskType;
  points: number;
  dueDate: string;
};

export async function getTasksForChild(childId: string): Promise<ChildTask[]> {
  return getJson<ChildTask[]>(`/api/children/${childId}/tasks`, await getAuthHeaders());
}

export async function createTaskForChild(
  childId: string,
  input: CreateTaskInput
): Promise<ChildTask> {
  return postJson<ChildTask>(`/api/children/${childId}/tasks`, input, await getAuthHeaders());
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
