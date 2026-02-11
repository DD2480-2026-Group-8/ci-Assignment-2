## Continuous Integration Server – Assignment 2

### Overview

This repository contains the code and configuration for our simple continuous integration (CI) server for DD2480 Assignment 2.  
The CI server is implemented in Java, built with Maven, and packaged as a Docker container. It is intended to be triggered by GitHub webhooks and to compile and test a target repository.

### Repository structure

- `ci-app/` – Java CI server (Jetty-based HTTP server, webhook endpoint, tests, Maven build).
- `.github/workflows/ci.yml` – GitHub Actions workflow that builds and tests the CI server on every push / PR to `main`.
- `Dockerfile` – Multi-stage Docker build for the CI server.
- `docker-compose.yml` – Convenience file to run the CI server container locally.

### Requirements and dependencies

- **Language / runtime**: Java 25 (Temurin).
- **Build tool**: Maven 3.9+.
- **Main libraries**:
  - Jetty 11 (HTTP server, `jakarta.servlet`).
  - org.json (parsing GitHub webhook payloads).
  - JUnit 5 and Mockito (testing).

All dependencies are declared in `ci-app/pom.xml`.

### How to build and run tests

From a fresh clone:

```bash
cd ci-app
mvn test         # run unit tests
mvn package      # build shaded JAR
```

GitHub Actions runs `mvn -B verify` via `.github/workflows/ci.yml` to validate each push.

### How to run the CI server locally

Run directly with Maven:

```bash
cd ci-app
mvn package
java -jar target/ci-app-*-shaded.jar
```

Or via Docker:

```bash
docker compose up --build
```

By default the server listens on port `8080` inside the container (mapped to a host port in `docker-compose.yml`).

### Exposing the server for GitHub webhooks

To allow GitHub to send webhook events to your locally running CI server, use **ngrok** to create a public tunnel:

1. **Start the CI server** (in one terminal):

   ```bash
   cd ci-app
   export GH_TOKEN=your_personal_access_token
   java -jar target/ci-app-*-shaded.jar
   ```

2. **Start ngrok** (in another terminal):

   ```bash
   ngrok http 8080
   ```

3. **Configure GitHub webhook**:
   - Copy the HTTPS forwarding URL from ngrok (e.g., `https://abc123.ngrok-free.app`)
   - In your GitHub repository settings, add a webhook with:
     - Payload URL: `https://abc123.ngrok-free.app/webhook`
     - Content type: `application/json`
     - Events: Select "Just the push event"

### CI workflow (P1–P3, overview)

- The CI server exposes a webhook endpoint at `POST /webhook`.
- GitHub is configured to send `push` events to this endpoint.
- The payload is inspected for the `ref` field; pushes to the `assessment` or `main` branches trigger CI builds.
- For qualifying pushes, the server is responsible for:
  - **P1 – Compilation**: triggering a build (e.g. `mvn compile`) of the target project.
  - **P2 – Testing**: running the project’s automated tests (e.g. `mvn test`) on the same branch.
  - **P3 – Notification**: sending the build result back to developers (via GitHub commit status API).

Implementation details and tests for these steps are found under `ci-app/src/main/java/com/group8` and `ci-app/src/test/java/com/group8`.

**Key implementation classes:**

- `ContinuousIntegrationServer.java` – Main webhook handler and HTTP server
- `CIrunner.java` – Core CI pipeline (clone, compile, test)
- `StatusToGithub.java` – GitHub commit status API client
- `BuildHistoryManager.java` – Build record persistence and retrieval

### Build History (P7)

The CI server maintains a persistent history of all past builds. Build records are stored as JSON files in `~/ci-build-history/` and persist even if the server is rebooted.

**URLs** (when running locally):

- **Build list**: `http://localhost:8080/builds` — Lists all builds with links to details
- **Individual build**: `http://localhost:8080/build/<buildId>` — Shows detailed information for a specific build (buildId is the timestamp)

When using ngrok, replace `localhost:8080` with your ngrok URL (e.g., `https://abc123.ngrok-free.app/builds`).

**Each build record includes:**

- Commit identifier (SHA)
- Build date and timestamp
- Status (SUCCESS or FAILURE)
- Complete build logs (compilation and test output)

### Commit Message Convention

- `feat:` - New feature
- `test:` - Adding or updating tests
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `docs:` - Documentation changes
- `style:` - Code style/formatting changes
- `chore:` - Maintenance (configuration, tooling, cleanup)

## Statement of Contributions

| Team Member        | Contributions                                                                                                                                                                                     |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Anna Likhanova     | Essence team evaluation (P6), CI skeleton setup help, unit tests for `BuildHistoryManager`, code review                                                                                           |
| Erik Sirborg       | CI runner implementation (`CIrunner.java`) with compilation and testing logic (P1, P2)                                                                                                            |
| Filip Dimitrijevic | Docker setup and configuration, Maven project structure, README documentation, integration between CI runner and commit status, GitHub Actions workflows, Javadoc deployment to GitHub Pages (P5) |
| Jingze Guo         | Assessment webhook implementation, Maven project structure, build history feature (`BuildHistoryManager.java`, P7), code review                                                                   |
| Louisa Zhang       | Commit status implementation with unit tests (`StatusToGithub.java`, `StatusToGithubTest.java`, P3)                                                                                               |

### Documentation (P5)

Public classes and methods in the main application code are documented with Javadoc comments.  
API documentation can be generated locally with:

```bash
cd ci-app
mvn javadoc:javadoc
```

The generated HTML documentation will be available under `ci-app/target/site/apidocs`.

**Live Javadoc**: The API documentation is automatically deployed to GitHub Pages at:  
**https://dd2480-2026-group-8.github.io/ci-Assignment-2/**

### Team SEMAT reflection (P6)

**Current State:**
Collaborating

**Why:**

- The communcation is open and honest - we have regular check-ins and syncs on discord and in person and ask for code reviews consistently;
- The team is focused on achieving the team mission - we are all commited to doing our part when it comes to the assignments;
- The team members know and trust each other - we divide the work and trust each other to complete it or ask for help if and when is necessary; the trust has not been breached

**Obstacles to next state**

While we each team members meets their commitments eventually, it sometimes takes longer than was communicated, causing delays in the workflow. To move to the next state the team needs to clearly communicate personal deadlines and stick to them, or inform of the inablity to do so in advance.

While the team works quite efficiently, there is currently no active efforts to continiously identify potential waste of work. To move to the next state the team needs to identify and eliminate potential waste of work through regular, designated check-ins.
