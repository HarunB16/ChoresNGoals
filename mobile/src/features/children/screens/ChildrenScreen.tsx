import { useFocusEffect } from "@react-navigation/native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useCallback, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  View
} from "react-native";

import type { RootStackParamList } from "../../../navigation/RootNavigator";
import { ApiError } from "../../../services/apiClient";
import { type Child, getChildren } from "../services/childrenApi";

type Props = NativeStackScreenProps<RootStackParamList, "Children">;

export function ChildrenScreen({ navigation }: Props) {
  const [children, setChildren] = useState<Child[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const loadChildren = useCallback(async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      setChildren(await getChildren());
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        setErrorMessage("Please log in again.");
      } else {
        setErrorMessage("Could not load children. Check your connection and try again.");
      }
    } finally {
      setIsLoading(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      void loadChildren();
    }, [loadChildren])
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <View style={styles.headerText}>
          <Text style={styles.title}>Children</Text>
          <Text style={styles.subtitle}>Manage the children linked to your parent account.</Text>
        </View>
        <Pressable style={styles.addButton} onPress={() => navigation.navigate("AddChild")}>
          <Text style={styles.addButtonText}>Add</Text>
        </Pressable>
      </View>

      {isLoading ? (
        <View style={styles.centerState}>
          <ActivityIndicator color="#2563eb" />
          <Text style={styles.stateText}>Loading children...</Text>
        </View>
      ) : null}

      {!isLoading && errorMessage ? (
        <View style={styles.centerState}>
          <Text style={styles.errorText}>{errorMessage}</Text>
          <Pressable style={styles.retryButton} onPress={loadChildren}>
            <Text style={styles.retryButtonText}>Try again</Text>
          </Pressable>
        </View>
      ) : null}

      {!isLoading && !errorMessage && children.length === 0 ? (
        <View style={styles.centerState}>
          <Text style={styles.stateTitle}>No children yet</Text>
          <Text style={styles.stateText}>Add your first child to start planning tasks.</Text>
        </View>
      ) : null}

      {!isLoading && !errorMessage && children.length > 0 ? (
        <FlatList
          data={children}
          keyExtractor={(child) => child.id}
          contentContainerStyle={styles.list}
          renderItem={({ item }) => (
            <Pressable
              style={({ pressed }) => [
                styles.childRow,
                pressed ? styles.childRowPressed : null
              ]}
              onPress={() => navigation.navigate("Tasks", {
                childId: item.id,
                childName: item.name
              })}
            >
              <View
                style={[
                  styles.avatar,
                  { backgroundColor: normalizeColor(item.avatarColor) }
                ]}
              >
                <Text style={styles.avatarText}>{item.name.charAt(0).toUpperCase()}</Text>
              </View>
              <View style={styles.childDetails}>
                <Text style={styles.childName}>{item.name}</Text>
                <Text style={styles.childMeta}>Born {item.birthYear}</Text>
              </View>
              <Text style={styles.openText}>Tasks</Text>
            </Pressable>
          )}
        />
      ) : null}
    </View>
  );
}

function normalizeColor(color: string): string {
  if (/^#[0-9a-fA-F]{6}$/.test(color)) {
    return color;
  }

  return "#2563eb";
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#ffffff",
    padding: 24
  },
  header: {
    alignItems: "flex-start",
    flexDirection: "row",
    gap: 16,
    justifyContent: "space-between",
    marginBottom: 24
  },
  headerText: {
    flex: 1
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
    lineHeight: 22
  },
  addButton: {
    backgroundColor: "#2563eb",
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 10
  },
  addButtonText: {
    color: "#ffffff",
    fontSize: 15,
    fontWeight: "600"
  },
  centerState: {
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 48
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
  retryButton: {
    borderColor: "#2563eb",
    borderRadius: 8,
    borderWidth: 1,
    marginTop: 16,
    paddingHorizontal: 16,
    paddingVertical: 10
  },
  retryButtonText: {
    color: "#2563eb",
    fontSize: 15,
    fontWeight: "600"
  },
  list: {
    gap: 12
  },
  childRow: {
    alignItems: "center",
    borderColor: "#e5e7eb",
    borderRadius: 8,
    borderWidth: 1,
    flexDirection: "row",
    padding: 14
  },
  childRowPressed: {
    opacity: 0.7
  },
  avatar: {
    alignItems: "center",
    borderRadius: 8,
    height: 44,
    justifyContent: "center",
    marginRight: 12,
    width: 44
  },
  avatarText: {
    color: "#ffffff",
    fontSize: 18,
    fontWeight: "700"
  },
  childDetails: {
    flex: 1
  },
  childName: {
    color: "#1f2937",
    fontSize: 17,
    fontWeight: "700"
  },
  childMeta: {
    color: "#6b7280",
    fontSize: 14,
    marginTop: 4
  },
  openText: {
    color: "#2563eb",
    fontSize: 14,
    fontWeight: "700"
  }
});
