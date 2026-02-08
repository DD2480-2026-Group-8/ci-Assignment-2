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

### CI workflow (P1–P3, overview)

- The CI server exposes a webhook endpoint at `POST /webhook`.
- GitHub is configured to send `push` events to this endpoint.
- The payload is inspected for the `ref` field; pushes to the `assessment` branch are treated specially.
- For pushes to `assessment`, the server is responsible for:
  - **P1 – Compilation**: triggering a build (e.g. `mvn compile`) of the target project.
  - **P2 – Testing**: running the project’s automated tests (e.g. `mvn test`) on the same branch.
  - **P3 – Notification**: sending the build result back to developers (via GitHub commit status).

Implementation details and tests for these steps are found under `ci-app/src/main/java/com/group8` and `ci-app/src/test/java/com/group8`.

### Commit Message Convention

- `feat:` - New feature
- `test:` - Adding or updating tests
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `docs:` - Documentation changes
- `style:` - Code style/formatting changes
- `chore:` - Maintenance (configuration, tooling, cleanup)

## Statement of Contributions

| Team Member        | Contributions |
| ------------------ | ------------- |
| Filip Dimitrijevic |               |
| Anna Likhanova     |               |
| Jingze Guo         |               |
| Erik Sirborg       |               |
| Louisa Zhang       |               |

### Documentation (P5)

Public classes and methods in the main application code are documented with Javadoc comments.  
API documentation can be generated with:

```bash
cd ci-app
mvn javadoc:javadoc
```

The generated HTML documentation will be available under `ci-app/target/site/apidocs`.

### Team SEMAT reflection (P6)

_To be completed by the team:_ a short paragraph assessing the current Team state according to the Essence standard (p.51–52), describing:
