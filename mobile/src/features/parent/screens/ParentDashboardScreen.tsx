import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { Pressable, StyleSheet, Text, View } from "react-native";

import type { RootStackParamList } from "../../../navigation/RootNavigator";

type Props = NativeStackScreenProps<RootStackParamList, "ParentDashboard">;

export function ParentDashboardScreen({ navigation }: Props) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Parent Dashboard</Text>
      <Text style={styles.body}>Tasks, children, points, and reminders will live here.</Text>
      <Pressable style={styles.button} onPress={() => navigation.navigate("Children")}>
        <Text style={styles.buttonText}>Manage children</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    backgroundColor: "#ffffff"
  },
  title: {
    fontSize: 28,
    fontWeight: "700",
    marginBottom: 12,
    color: "#1f2937"
  },
  body: {
    fontSize: 16,
    lineHeight: 24,
    color: "#4b5563",
    marginBottom: 20
  },
  button: {
    alignItems: "center",
    backgroundColor: "#2563eb",
    borderRadius: 8,
    paddingVertical: 12
  },
  buttonText: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "600"
  }
});
