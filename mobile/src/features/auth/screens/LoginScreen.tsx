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
import { loginParent } from "../services/authApi";
import { saveAccessToken } from "../services/tokenStorage";

type Props = NativeStackScreenProps<RootStackParamList, "Login">;

export function LoginScreen({ navigation }: Props) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [formError, setFormError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleLogin() {
    setEmailError("");
    setPasswordError("");
    setFormError("");

    const normalizedEmail = email.trim().toLowerCase();
    let hasValidationError = false;

    if (!normalizedEmail) {
      setEmailError("Email is required.");
      hasValidationError = true;
    } else if (!/^\S+@\S+\.\S+$/.test(normalizedEmail)) {
      setEmailError("Enter a valid email address.");
      hasValidationError = true;
    }

    if (!password) {
      setPasswordError("Password is required.");
      hasValidationError = true;
    }

    if (hasValidationError) {
      return;
    }

    try {
      setIsSubmitting(true);
      const response = await loginParent({
        email: normalizedEmail,
        password
      });

      await saveAccessToken(response.accessToken);
      navigation.replace("ParentDashboard");
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        setFormError("Email or password is incorrect.");
      } else {
        setFormError("Could not log in. Check your connection and try again.");
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
        <Text style={styles.title}>Log in</Text>
        <Text style={styles.subtitle}>Use your parent account to continue.</Text>

        {formError ? <Text style={styles.formError}>{formError}</Text> : null}

        <Text style={styles.label}>Email</Text>
        <TextInput
          style={[styles.input, emailError ? styles.inputError : null]}
          placeholder="parent@example.com"
          autoCapitalize="none"
          autoCorrect={false}
          keyboardType="email-address"
          textContentType="emailAddress"
          value={email}
          onChangeText={setEmail}
        />
        {emailError ? <Text style={styles.fieldError}>{emailError}</Text> : null}

        <Text style={styles.label}>Password</Text>
        <TextInput
          style={[styles.input, passwordError ? styles.inputError : null]}
          placeholder="Password"
          autoCapitalize="none"
          secureTextEntry
          textContentType="password"
          value={password}
          onChangeText={setPassword}
        />
        {passwordError ? <Text style={styles.fieldError}>{passwordError}</Text> : null}

        <Pressable
          style={({ pressed }) => [
            styles.button,
            pressed || isSubmitting ? styles.buttonPressed : null
          ]}
          disabled={isSubmitting}
          onPress={handleLogin}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#ffffff" />
          ) : (
            <Text style={styles.buttonText}>Log in</Text>
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
    fontSize: 28,
    fontWeight: "700",
    marginBottom: 8,
    color: "#1f2937"
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
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
    marginBottom: 8
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
    borderRadius: 8,
    backgroundColor: "#2563eb",
    paddingVertical: 12,
    marginTop: 8
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
