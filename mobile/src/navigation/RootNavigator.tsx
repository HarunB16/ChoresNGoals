import { createNativeStackNavigator } from "@react-navigation/native-stack";

import { LoginScreen } from "../features/auth/screens/LoginScreen";
import { ParentDashboardScreen } from "../features/parent/screens/ParentDashboardScreen";
import { WelcomeScreen } from "../features/welcome/screens/WelcomeScreen";

export type RootStackParamList = {
  Welcome: undefined;
  Login: undefined;
  ParentDashboard: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export function RootNavigator() {
  return (
    <Stack.Navigator initialRouteName="Welcome">
      <Stack.Screen
        name="Welcome"
        component={WelcomeScreen}
        options={{ title: "Welcome" }}
      />
      <Stack.Screen
        name="Login"
        component={LoginScreen}
        options={{ title: "Log in" }}
      />
      <Stack.Screen
        name="ParentDashboard"
        component={ParentDashboardScreen}
        options={{ title: "Parent Dashboard" }}
      />
    </Stack.Navigator>
  );
}
