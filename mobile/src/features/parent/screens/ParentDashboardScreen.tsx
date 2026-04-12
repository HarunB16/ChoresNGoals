import { StyleSheet, Text, View } from "react-native";

export function ParentDashboardScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Parent Dashboard</Text>
      <Text style={styles.body}>Tasks, children, points, and reminders will live here.</Text>
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
    color: "#4b5563"
  }
});
