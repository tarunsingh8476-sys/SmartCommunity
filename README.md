
# Smart Community Backend

Spring Boot 3, Java 17, Spring Data JPA/Hibernate, PostgreSQL, Spring Security, JWT, Brevo transactional email and Google OAuth2. This is a student-friendly monolith. WebSocket delivery is **not** implemented; channel and conversation messages are REST persistence only.

## Run locally
1. Install JDK 17+ and PostgreSQL. Maven is downloaded automatically by the included Maven Wrapper when needed.
2. Create a database: `CREATE DATABASE smart_community;`
3. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in your shell or IDE run configuration. Spring Boot does not automatically load `.env` files.
4. Set `JWT_SECRET` to a random secret with at least 32 bytes. Set the Brevo API key and a sender address verified in Brevo. Google OAuth credentials are needed only for Google login.
5. From `backend/`, run `./mvnw spring-boot:run` on macOS/Linux or `\.\mvnw.cmd spring-boot:run` in Windows PowerShell.

Hibernate uses `spring.jpa.hibernate.ddl-auto=update`; no migration framework is included. Back up important data before schema changes.

## Google OAuth2
Register an OAuth client in Google Cloud Console. Set the authorized redirect URI to `http://localhost:8080/login/oauth2/code/google`. Configure `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`. After successful Google authentication the server redirects to `${FRONTEND_URL}/oauth/callback`. This initial scaffold does not place an application JWT in the redirect, so the frontend must be paired with a secure server-side handoff before using Google login as an API bearer-token flow. Do not add tokens to URL query parameters.

## Brevo
Create a Brevo account, verify the sender address, create an API key, and set `BREVO_API_KEY` and `BREVO_SENDER_EMAIL`. If these are missing, OTP delivery fails intentionally; the app does not pretend an email was sent.

## Main API routes
- `POST /api/auth/signup` — `{ "name":"Tarun", "email":"you@example.com", "password":"at-least-8-chars" }`
- `POST /api/auth/verify-otp` — `{ "email":"you@example.com", "otp":"123456" }`
- `POST /api/auth/resend-otp` — `{ "email":"you@example.com" }`
- `POST /api/auth/login` — `{ "email":"you@example.com", "password":"..." }`
- `GET /api/auth/me`, `GET /api/users/me`, `GET /api/dashboard` — Bearer JWT
- `/api/servers` — create/list/get/update/delete
- `/api/servers/{serverId}/members` — join/list members; `DELETE .../me` to leave
- `/api/servers/{serverId}/channels` — channel CRUD
- `/api/channels/{channelId}/messages` — send/list paginated/edit/delete
- `/api/conversations` — create/list/get; `/{id}/messages` send/list
- `/api/notifications` — list, `PATCH /{id}/read`, `PATCH /read-all`

## Important implementation notes
- JWT access tokens expire after 15 minutes. Refresh-token rotation is not included.
- OTPs are stored as BCrypt hashes, expire after five minutes, have a resend cooldown and five-attempt limit.
- Do not use production credentials in source control. `.env` is intentionally not committed.
- Review the OAuth2 callback flow and add a secure one-time code handoff for production.
- Authorization is enforced for server membership, channel access, message ownership, conversation participation, and notification ownership.


## Deploy to Render with Docker
1. Push this repository to GitHub.
2. In Render, create **New → Web Service**, connect the repository, and choose **Docker** as the runtime.
3. Set the **Root Directory** to `backend` if the repository root contains the `backend` folder. If the repository itself is the backend folder, leave Root Directory blank.
4. Add the required environment variables in Render: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and a strong `JWT_SECRET`. Add `FRONTEND_URL`, `BREVO_API_KEY`, `BREVO_SENDER_EMAIL`, `GOOGLE_CLIENT_ID`, and `GOOGLE_CLIENT_SECRET` as needed by enabled features.
5. For Neon, use the JDBC URL format `jdbc:postgresql://YOUR_NEON_HOST/YOUR_DATABASE?sslmode=require`. Copy the exact host, database, and role from the Neon Connect panel.
6. Render supplies `PORT`; the application reads it automatically. The Dockerfile builds the JAR and runs it as a non-root user.

Do not commit credentials or add an `.env.example` file. Configure secrets in Render's Environment settings and locally through your IDE or shell.
