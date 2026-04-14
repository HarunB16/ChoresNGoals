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
import {
  getCurrentFamily,
  inviteChildByEmail,
  type ChildInvitation
} from "../services/invitationsApi";

type Props = NativeStackScreenProps<RootStackParamList, "InviteChild">;

export function InviteChildScreen({ navigation }: Props) {
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [formError, setFormError] = useState("");
  const [invitation, setInvitation] = useState<ChildInvitation | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleInviteChild() {
    setEmailError("");
    setFormError("");
    setInvitation(null);

    const normalizedEmail = email.trim().toLowerCase();

    if (!normalizedEmail) {
      setEmailError("Email is required.");
      return;
    }

    if (!/^\S+@\S+\.\S+$/.test(normalizedEmail)) {
      setEmailError("Enter a valid email address.");
      return;
    }

    try {
      setIsSubmitting(true);
      const family = await getCurrentFamily();
      const createdInvitation = await inviteChildByEmail(family.id, normalizedEmail);

      setInvitation(createdInvitation);
      setEmail("");
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        setFormError("Please log in again.");
      } else if (error instanceof ApiError && error.status === 403) {
        setFormError("You can only invite children to your own family.");
      } else {
        setFormError("Could not send invitation. Check your connection and try again.");
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
        <Text style={styles.title}>Invite child</Text>
        <Text style={styles.subtitle}>Send an invitation to join your family.</Text>

        {formError ? <Text style={styles.formError}>{formError}</Text> : null}

        {invitation ? (
          <View style={styles.successBox}>
            <Text style={styles.successTitle}>Invitation created</Text>
            <Text style={styles.successText}>
              {invitation.invitedEmail} can use the invitation once child registration is ready.
            </Text>
          </View>
        ) : null}

        <Text style={styles.label}>Child email</Text>
        <TextInput
          style={[styles.input, emailError ? styles.inputError : null]}
          placeholder="child@example.com"
          autoCapitalize="none"
          autoCorrect={false}
          keyboardType="email-address"
          textContentType="emailAddress"
          value={email}
          onChangeText={setEmail}
        />
        {emailError ? <Text style={styles.fieldError}>{emailError}</Text> : null}

        <Pressable
          style={({ pressed }) => [
            styles.button,
            pressed || isSubmitting ? styles.buttonPressed : null
          ]}
          disabled={isSubmitting}
          onPress={handleInviteChild}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#ffffff" />
          ) : (
            <Text style={styles.buttonText}>Send invitation</Text>
          )}
        </Pressable>

        <Pressable
          style={styles.secondaryButton}
          disabled={isSubmitting}
          onPress={() => navigation.goBack()}
        >
          <Text style={styles.secondaryButtonText}>Back to children</Text>
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
  successBox: {
    backgroundColor: "#dcfce7",
    borderRadius: 8,
    marginBottom: 16,
    padding: 12
  },
  successTitle: {
    color: "#166534",
    fontSize: 15,
    fontWeight: "700",
    marginBottom: 4
  },
  successText: {
    color: "#166534",
    fontSize: 14,
    lineHeight: 20
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
  },
  secondaryButton: {
    alignItems: "center",
    paddingVertical: 14
  },
  secondaryButtonText: {
    color: "#2563eb",
    fontSize: 15,
    fontWeight: "600"
  }
});
