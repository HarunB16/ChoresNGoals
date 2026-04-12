import { createNativeStackNavigator } from "@react-navigation/native-stack";

import { LoginScreen } from "../features/auth/screens/LoginScreen";
import { RegisterScreen } from "../features/auth/screens/RegisterScreen";
import { AddChildScreen } from "../features/children/screens/AddChildScreen";
import { ChildrenScreen } from "../features/children/screens/ChildrenScreen";
import { ParentDashboardScreen } from "../features/parent/screens/ParentDashboardScreen";
import { TasksScreen } from "../features/tasks/screens/TasksScreen";
import { WelcomeScreen } from "../features/welcome/screens/WelcomeScreen";

export type RootStackParamList = {
  Welcome: undefined;
  Login: { registeredEmail?: string } | undefined;
  Register: undefined;
  ParentDashboard: undefined;
  Children: undefined;
  AddChild: undefined;
  Tasks: { childId: string; childName: string };
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
        name="Register"
        component={RegisterScreen}
        options={{ title: "Create account" }}
      />
      <Stack.Screen
        name="ParentDashboard"
        component={ParentDashboardScreen}
        options={{ title: "Parent Dashboard" }}
      />
      <Stack.Screen
        name="Children"
        component={ChildrenScreen}
        options={{ title: "Children" }}
      />
      <Stack.Screen
        name="AddChild"
        component={AddChildScreen}
        options={{ title: "Add Child" }}
      />
      <Stack.Screen
        name="Tasks"
        component={TasksScreen}
        options={{ title: "Tasks" }}
      />
    </Stack.Navigator>
  );
}
