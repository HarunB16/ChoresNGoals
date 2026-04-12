import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";

import type { RootStackParamList } from "../../../navigation/RootNavigator";
import { API_BASE_URL } from "../../../services/apiClient";
import { getBackendHealth } from "../../../services/healthApi";

type Props = NativeStackScreenProps<RootStackParamList, "Welcome">;
type HealthStatus = "idle" | "loading" | "success" | "failure";

export function WelcomeScreen({ navigation }: Props) {
  const [healthStatus, setHealthStatus] = useState<HealthStatus>("idle");
  const [healthMessage, setHealthMessage] = useState(
    "Backend health has not been checked yet."
  );

  async function handleCheckBackend() {
    setHealthStatus("loading");
    setHealthMessage("Checking backend health...");

    try {
      const health = await getBackendHealth();
      setHealthStatus("success");
      setHealthMessage(`Backend is ${health.status}: ${health.service}`);
    } catch (error) {
      setHealthStatus("failure");
      const detail = error instanceof Error ? error.message : "Unknown error";
      setHealthMessage(`Backend check failed for ${API_BASE_URL}. ${detail}`);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Chores N Goals</Text>
      <Text style={styles.body}>
        A simple place for families to track tasks, choices, and points.
      </Text>
      <Pressable
        style={[styles.button, styles.secondaryButton]}
        onPress={handleCheckBackend}
        disabled={healthStatus === "loading"}
      >
        <Text style={[styles.buttonText, styles.secondaryButtonText]}>
          {healthStatus === "loading" ? "Checking..." : "Check backend"}
        </Text>
      </Pressable>
      <Text style={styles.apiText}>API: {API_BASE_URL}</Text>
      <Text style={[styles.healthText, styles[healthStatus]]}>{healthMessage}</Text>
      <Pressable style={styles.button} onPress={() => navigation.navigate("Login")}>
        <Text style={styles.buttonText}>Continue</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 24,
    backgroundColor: "#ffffff"
  },
  title: {
    fontSize: 32,
    fontWeight: "700",
    marginBottom: 12,
    color: "#1f2937"
  },
  body: {
    fontSize: 16,
    lineHeight: 24,
    textAlign: "center",
    marginBottom: 20,
    color: "#4b5563"
  },
  button: {
    borderRadius: 8,
    backgroundColor: "#2563eb",
    paddingHorizontal: 20,
    paddingVertical: 12,
    marginTop: 8
  },
  buttonText: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "600"
  },
  secondaryButton: {
    backgroundColor: "#ffffff",
    borderColor: "#2563eb",
    borderWidth: 1
  },
  secondaryButtonText: {
    color: "#2563eb"
  },
  healthText: {
    fontSize: 14,
    lineHeight: 20,
    marginTop: 12,
    marginBottom: 12,
    textAlign: "center",
    color: "#4b5563"
  },
  apiText: {
    fontSize: 12,
    lineHeight: 18,
    marginTop: 10,
    textAlign: "center",
    color: "#6b7280"
  },
  idle: {
    color: "#4b5563"
  },
  loading: {
    color: "#2563eb"
  },
  success: {
    color: "#047857"
  },
  failure: {
    color: "#b91c1c"
  }
});
