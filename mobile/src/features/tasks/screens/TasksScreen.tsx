import { useFocusEffect } from "@react-navigation/native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useCallback, useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";

import type { RootStackParamList } from "../../../navigation/RootNavigator";
import { ApiError } from "../../../services/apiClient";
import {
  createTaskForChild,
  type ChildTask,
  getTasksForChild,
  type TaskType
} from "../services/tasksApi";

type Props = NativeStackScreenProps<RootStackParamList, "Tasks">;

const taskTypes: Array<{ label: string; value: TaskType }> = [
  { label: "Daily", value: "daily" },
  { label: "Weekly", value: "weekly" },
  { label: "Monthly", value: "monthly" },
  { label: "One time", value: "one_time" }
];

export function TasksScreen({ route }: Props) {
  const { childId, childName } = route.params;
  const [tasks, setTasks] = useState<ChildTask[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [type, setType] = useState<TaskType>("daily");
  const [points, setPoints] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [formError, setFormError] = useState("");

  const loadTasks = useCallback(async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      setTasks(await getTasksForChild(childId));
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        setErrorMessage("Please log in again.");
      } else {
        setErrorMessage("Could not load tasks. Check your connection and try again.");
      }
    } finally {
      setIsLoading(false);
    }
  }, [childId]);

  useFocusEffect(
    useCallback(() => {
      void loadTasks();
    }, [loadTasks])
  );

  async function handleCreateTask() {
    setFormError("");

    const trimmedTitle = title.trim();
    const trimmedDescription = description.trim();
    const parsedPoints = Number(points);

    if (!trimmedTitle) {
      setFormError("Title is required.");
      return;
    }

    if (!points || !Number.isInteger(parsedPoints) || parsedPoints < 0) {
      setFormError("Points must be a whole number that is zero or higher.");
      return;
    }

    if (!isValidDateInput(dueDate)) {
      setFormError("Due date must use YYYY-MM-DD.");
      return;
    }

    try {
      setIsSubmitting(true);
      await createTaskForChild(childId, {
        title: trimmedTitle,
        description: trimmedDescription,
        type,
        points: parsedPoints,
        dueDate
      });

      setTitle("");
      setDescription("");
      setType("daily");
      setPoints("");
      setDueDate("");
      await loadTasks();
    } catch (error) {
      if (error instanceof ApiError) {
        setFormError(error.message);
      } else {
        setFormError("Could not create task. Check your connection and try again.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      style={styles.container}
    >
      <ScrollView contentContainerStyle={styles.content} keyboardShouldPersistTaps="handled">
        <Text style={styles.title}>{childName}</Text>
        <Text style={styles.subtitle}>Create tasks and review current planning.</Text>

        <View style={styles.form}>
          <Text style={styles.sectionTitle}>New task</Text>

          {formError ? <Text style={styles.formError}>{formError}</Text> : null}

          <Text style={styles.label}>Title</Text>
          <TextInput
            style={styles.input}
            placeholder="Clean room"
            value={title}
            onChangeText={setTitle}
          />

          <Text style={styles.label}>Description</Text>
          <TextInput
            multiline
            style={[styles.input, styles.textArea]}
            placeholder="Put toys away"
            value={description}
            onChangeText={setDescription}
          />

          <Text style={styles.label}>Type</Text>
          <View style={styles.typeRow}>
            {taskTypes.map((taskType) => (
              <Pressable
                key={taskType.value}
                style={[
                  styles.typeButton,
                  type === taskType.value ? styles.typeButtonSelected : null
                ]}
                onPress={() => setType(taskType.value)}
              >
                <Text
                  style={[
                    styles.typeButtonText,
                    type === taskType.value ? styles.typeButtonTextSelected : null
                  ]}
                >
                  {taskType.label}
                </Text>
              </Pressable>
            ))}
          </View>

          <Text style={styles.label}>Points</Text>
          <TextInput
            keyboardType="number-pad"
            style={styles.input}
            placeholder="10"
            value={points}
            onChangeText={setPoints}
          />

          <Text style={styles.label}>Due date</Text>
          <TextInput
            autoCapitalize="none"
            style={styles.input}
            placeholder="2026-04-13"
            value={dueDate}
            onChangeText={setDueDate}
          />

          <Pressable
            disabled={isSubmitting}
            style={({ pressed }) => [
              styles.button,
              pressed || isSubmitting ? styles.buttonPressed : null
            ]}
            onPress={handleCreateTask}
          >
            {isSubmitting ? (
              <ActivityIndicator color="#ffffff" />
            ) : (
              <Text style={styles.buttonText}>Create task</Text>
            )}
          </Pressable>
        </View>

        <View style={styles.tasksHeader}>
          <Text style={styles.sectionTitle}>Tasks</Text>
          <Pressable style={styles.refreshButton} onPress={loadTasks}>
            <Text style={styles.refreshButtonText}>Refresh</Text>
          </Pressable>
        </View>

        {isLoading ? (
          <View style={styles.centerState}>
            <ActivityIndicator color="#2563eb" />
            <Text style={styles.stateText}>Loading tasks...</Text>
          </View>
        ) : null}

        {!isLoading && errorMessage ? (
          <View style={styles.centerState}>
            <Text style={styles.errorText}>{errorMessage}</Text>
          </View>
        ) : null}

        {!isLoading && !errorMessage && tasks.length === 0 ? (
          <View style={styles.centerState}>
            <Text style={styles.stateTitle}>No tasks yet</Text>
            <Text style={styles.stateText}>Create the first task for this child.</Text>
          </View>
        ) : null}

        {!isLoading && !errorMessage
          ? tasks.map((task) => (
              <View key={task.id} style={styles.taskRow}>
                <View style={styles.taskTitleRow}>
                  <Text style={styles.taskTitle}>{task.title}</Text>
                  <Text style={styles.points}>{task.points} pts</Text>
                </View>
                {task.description ? (
                  <Text style={styles.taskDescription}>{task.description}</Text>
                ) : null}
                <Text style={styles.taskMeta}>
                  {formatTaskType(task.type)} - {task.status} - due {task.dueDate}
                </Text>
              </View>
            ))
          : null}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

function isValidDateInput(value: string): boolean {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return false;
  }

  const parsedDate = new Date(`${value}T00:00:00`);
  return !Number.isNaN(parsedDate.getTime()) && value === parsedDate.toISOString().slice(0, 10);
}

function formatTaskType(value: TaskType): string {
  return taskTypes.find((taskType) => taskType.value === value)?.label ?? value;
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#ffffff",
    flex: 1
  },
  content: {
    padding: 24,
    paddingBottom: 40
  },
  title: {
    color: "#1f2937",
    fontSize: 28,
    fontWeight: "700",
    marginBottom: 8
  },
  subtitle: {
    color: "#4b5563",
    fontSize: 16,
    lineHeight: 22,
    marginBottom: 24
  },
  form: {
    marginBottom: 28
  },
  sectionTitle: {
    color: "#1f2937",
    fontSize: 20,
    fontWeight: "700",
    marginBottom: 14
  },
  label: {
    color: "#374151",
    fontSize: 14,
    fontWeight: "600",
    marginBottom: 6
  },
  input: {
    borderColor: "#d1d5db",
    borderRadius: 8,
    borderWidth: 1,
    color: "#111827",
    fontSize: 16,
    marginBottom: 12,
    paddingHorizontal: 14,
    paddingVertical: 12
  },
  textArea: {
    minHeight: 84,
    textAlignVertical: "top"
  },
  typeRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginBottom: 12
  },
  typeButton: {
    borderColor: "#9ca3af",
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 12,
    paddingVertical: 9
  },
  typeButtonSelected: {
    backgroundColor: "#2563eb",
    borderColor: "#2563eb"
  },
  typeButtonText: {
    color: "#374151",
    fontSize: 14,
    fontWeight: "600"
  },
  typeButtonTextSelected: {
    color: "#ffffff"
  },
  formError: {
    backgroundColor: "#fee2e2",
    borderRadius: 8,
    color: "#991b1b",
    fontSize: 14,
    lineHeight: 20,
    marginBottom: 16,
    padding: 12
  },
  button: {
    alignItems: "center",
    backgroundColor: "#2563eb",
    borderRadius: 8,
    marginTop: 2,
    paddingVertical: 12
  },
  buttonPressed: {
    opacity: 0.7
  },
  buttonText: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "600"
  },
  tasksHeader: {
    alignItems: "center",
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 4
  },
  refreshButton: {
    borderColor: "#2563eb",
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 12,
    paddingVertical: 8
  },
  refreshButtonText: {
    color: "#2563eb",
    fontSize: 14,
    fontWeight: "600"
  },
  centerState: {
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 34
  },
  stateTitle: {
    color: "#1f2937",
    fontSize: 18,
    fontWeight: "700",
    marginBottom: 8
  },
  stateText: {
    color: "#4b5563",
    fontSize: 15,
    lineHeight: 22,
    marginTop: 10,
    textAlign: "center"
  },
  errorText: {
    color: "#b91c1c",
    fontSize: 15,
    lineHeight: 22,
    textAlign: "center"
  },
  taskRow: {
    borderColor: "#e5e7eb",
    borderRadius: 8,
    borderWidth: 1,
    marginTop: 12,
    padding: 14
  },
  taskTitleRow: {
    alignItems: "center",
    flexDirection: "row",
    gap: 12,
    justifyContent: "space-between"
  },
  taskTitle: {
    color: "#1f2937",
    flex: 1,
    fontSize: 17,
    fontWeight: "700"
  },
  points: {
    color: "#2563eb",
    fontSize: 14,
    fontWeight: "700"
  },
  taskDescription: {
    color: "#4b5563",
    fontSize: 15,
    lineHeight: 21,
    marginTop: 8
  },
  taskMeta: {
    color: "#6b7280",
    fontSize: 13,
    marginTop: 10
  }
});
