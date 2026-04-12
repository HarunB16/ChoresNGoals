# Chores N Goals

Parent-child task tracking app monorepo.

## Project Structure

```text
.
├── backend   # Spring Boot API
├── mobile    # Expo React Native app
└── docs      # Architecture and API notes
```

## Requirements

- Java 21
- Maven
- Docker and Docker Compose
- Node 20.19.4 or newer
- npm
- Expo Go on your phone

The mobile app currently uses Expo SDK 54, which requires Node 20.19.4 or newer for the local development server.

## Backend

Start PostgreSQL:

```bash
docker compose up -d postgres
```

The local PostgreSQL defaults are:

```text
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=choresngoals
POSTGRES_USER=choresngoals
POSTGRES_PASSWORD=change-me
```

Start the Spring Boot backend:

```bash
cd backend
JWT_SECRET=replace-with-at-least-32-characters mvn spring-boot:run
```

Verify the backend health endpoint:

```bash
curl http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "ok",
  "service": "choresngoals-backend",
  "timestamp": "..."
}
```

## Mobile

Use Node 20 before starting Expo:

```bash
source ~/.nvm/nvm.sh
nvm use 20
```

Install dependencies:

```bash
cd mobile
npm install
```

Set the backend URL for Expo in `mobile/.env.local`:

```text
EXPO_PUBLIC_API_BASE_URL=http://YOUR_COMPUTER_LAN_IP:8080
```

For the current local setup this was:

```text
EXPO_PUBLIC_API_BASE_URL=http://192.168.10.190:8080
```

Start Expo with a clean cache:

```bash
npx expo start --clear --lan --port 19000
```

Open Expo Go on your phone and scan the QR code, or manually enter:

```text
exp://YOUR_COMPUTER_LAN_IP:19000
```

## Phone Connectivity Check

Before testing the app button, confirm your phone can reach the backend by opening this in the phone browser:

```text
http://YOUR_COMPUTER_LAN_IP:8080/api/health
```

If the phone browser shows `status: ok`, open the app in Expo Go and tap:

```text
Check backend
```

Expected app message:

```text
Backend is ok: choresngoals-backend
```

## Troubleshooting

If Expo shows a red error screen after SDK 54 upgrade, confirm the terminal running Expo uses Node 20:

```bash
node --version
```

If the app cannot reach the backend but the computer can, check:

- phone and computer are on the same Wi-Fi
- mobile data is disabled while testing
- VPN is disabled
- the Wi-Fi is not a guest network
- `mobile/.env.local` uses the computer LAN IP, not `localhost`

If `.env.local` changes, restart Expo:

```bash
npx expo start --clear --lan --port 19000
```

If `mvn spring-boot:run` fails with `Connection to localhost:5432 refused`, start PostgreSQL first:

```bash
docker compose up -d postgres
```
