import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";

import type { RootStackParamList } from "../../../navigation/RootNavigator";
import { ApiError } from "../../../services/apiClient";
import { createChild } from "../services/childrenApi";

type Props = NativeStackScreenProps<RootStackParamList, "AddChild">;

export function AddChildScreen({ navigation }: Props) {
  const [name, setName] = useState("");
  const [birthYear, setBirthYear] = useState("");
  const [avatarColor, setAvatarColor] = useState("#2563eb");
  const [nameError, setNameError] = useState("");
  const [birthYearError, setBirthYearError] = useState("");
  const [avatarColorError, setAvatarColorError] = useState("");
  const [formError, setFormError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleCreateChild() {
    setNameError("");
    setBirthYearError("");
    setAvatarColorError("");
    setFormError("");

    const trimmedName = name.trim();
    const trimmedAvatarColor = avatarColor.trim();
    const parsedBirthYear = Number(birthYear);
    const currentYear = new Date().getFullYear();
    let hasValidationError = false;

    if (!trimmedName) {
      setNameError("Name is required.");
      hasValidationError = true;
    }

    if (!birthYear) {
      setBirthYearError("Birth year is required.");
      hasValidationError = true;
    } else if (!Number.isInteger(parsedBirthYear)) {
      setBirthYearError("Birth year must be a whole number.");
      hasValidationError = true;
    } else if (parsedBirthYear < 1900 || parsedBirthYear > currentYear) {
      setBirthYearError(`Birth year must be between 1900 and ${currentYear}.`);
      hasValidationError = true;
    }

    if (!trimmedAvatarColor) {
      setAvatarColorError("Avatar color is required.");
      hasValidationError = true;
    }

    if (hasValidationError) {
      return;
    }

    try {
      setIsSubmitting(true);
      await createChild({
        name: trimmedName,
        birthYear: parsedBirthYear,
        avatarColor: trimmedAvatarColor
      });

      navigation.goBack();
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        setFormError("Please log in again.");
      } else {
        setFormError("Could not add child. Check your connection and try again.");
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
      <View style={styles.form}>
        <Text style={styles.title}>Add child</Text>
        <Text style={styles.subtitle}>Create a child profile for task planning.</Text>

        {formError ? <Text style={styles.formError}>{formError}</Text> : null}

        <Text style={styles.label}>Name</Text>
        <TextInput
          style={[styles.input, nameError ? styles.inputError : null]}
          placeholder="Child name"
          value={name}
          onChangeText={setName}
        />
        {nameError ? <Text style={styles.fieldError}>{nameError}</Text> : null}

        <Text style={styles.label}>Birth year</Text>
        <TextInput
          style={[styles.input, birthYearError ? styles.inputError : null]}
          placeholder="2016"
          keyboardType="number-pad"
          value={birthYear}
          onChangeText={setBirthYear}
        />
        {birthYearError ? <Text style={styles.fieldError}>{birthYearError}</Text> : null}

        <Text style={styles.label}>Avatar color</Text>
        <TextInput
          style={[styles.input, avatarColorError ? styles.inputError : null]}
          placeholder="#2563eb"
          autoCapitalize="none"
          value={avatarColor}
          onChangeText={setAvatarColor}
        />
        {avatarColorError ? (
          <Text style={styles.fieldError}>{avatarColorError}</Text>
        ) : null}

        <Pressable
          style={({ pressed }) => [
            styles.button,
            pressed || isSubmitting ? styles.buttonPressed : null
          ]}
          disabled={isSubmitting}
          onPress={handleCreateChild}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#ffffff" />
          ) : (
            <Text style={styles.buttonText}>Add child</Text>
          )}
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    padding: 24,
    backgroundColor: "#ffffff"
  },
  form: {
    width: "100%"
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
    fontSize: 16,
    marginBottom: 8,
    paddingHorizontal: 14,
    paddingVertical: 12
  },
  inputError: {
    borderColor: "#dc2626"
  },
  fieldError: {
    color: "#b91c1c",
    fontSize: 13,
    marginBottom: 12
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
    marginTop: 8,
    paddingVertical: 12
  },
  buttonPressed: {
    opacity: 0.7
  },
  buttonText: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "600"
  }
});
