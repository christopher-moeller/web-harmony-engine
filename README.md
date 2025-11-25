# web-harmony-engine
This project is the public version of web-harmony-engine-private. It contains all functionalities from the derived project without the original git history.

## Overview
**WebHarmony** is a framework designed to accelerate the development of large web applications with minimal code. It provides a robust backend and frontend foundation while offering extensive default functionality, allowing developers to focus on business-specific logic rather than reinventing common features.

The framework includes a powerful library that generates backend classes and TypeScript layouts, enabling you to create custom applications efficiently. At the same time, WebHarmony comes with a wide range of prebuilt pages and modules for essential application features, such as user management, task tracking, email handling, and other administrative functionality.

All default features are fully functional out of the box but can be easily customized or overridden with your own logic. This approach allows you to launch sophisticated web applications quickly while maintaining full flexibility to adapt the system to your specific requirements.

WebHarmony is ideal for teams looking to build scalable, maintainable, and feature-rich web applications without the overhead of implementing repetitive boilerplate code.

## Project structure (short overview)

- `harmony-demo/`
  - `api/` - Java/Maven backend demo (contains `mvnw`, `pom.xml`, `src/`)
  - `ui/` - frontend demo (Nuxt, TypeScript, `package.json`, `nuxt.config.ts`)

- `harmony-starter/`
  - A starter project with a structure similar to `harmony-demo`.

- `lib/`
  - `api/` - shared libraries and utilities for the API
  - `templates/` - templates for projects, Dockerfiles, Compose files and CI definitions
  - `ui/` - shared UI components, configurations and build scripts

- `LICENSE`, `README.md` - project license and documentation

(Note: The full folder structure is larger; see the top-level folders for details.)

## Requirements

Install the following tools on your system (macOS, zsh):

- Java JDK 11+ (or the version required by the project/CI)
- Maven (optional, since each subfolder includes the Maven wrapper `mvnw`)
- Node.js 16+ (or the version required for Nuxt)
- npm or pnpm/yarn (the projects use `package.json` in the `ui` folders)
- Optional: Docker & Docker Compose (for containerized deployment)

Check the installation with:

```bash
java -version
./harmony-demo/api/mvnw -v
node -v
npm -v
```

## Quick start (local)

These steps describe a quick local start for the demo application (API + UI).

1) Start the backend API (example `harmony-demo/api`):

```bash
# change into the API directory
cd harmony-demo/api
# build and package using the Maven wrapper
./mvnw clean package -DskipTests
# start the app locally (depending on project configuration):
./mvnw spring-boot:run
```

By default the API starts on a port defined in `src/main/resources/application.yaml` or in `LOCAL_DEV_ONLY_PROPERTY_VALUES.json`. Check these configurations if you encounter port conflicts.

2) Start the frontend UI (example `harmony-demo/ui`):

```bash
cd harmony-demo/ui
npm install
# development mode (hot reload)
npm run dev
```

The frontend normally communicates with the local API (CORS must be configured accordingly). The UI URL is printed in the console (usually http://localhost:3000).

3) Access

- Frontend: http://localhost:3000 (or the port shown in the console)
- API: http://localhost:8080 (or the port defined in configuration)

## Development workflow

- Backend
  - Use `./mvnw` in the relevant API folders for build/run/tests.
  - Unit and integration tests are configured via Maven. Run tests locally with `./mvnw test`.

- Frontend
  - In the UI folder: `npm install` and `npm run dev` for development.
  - Run lint/type checks with `npm run lint` or run a production build with `npm run build`.

- Shared libraries
  - `lib/` contains templates and shared modules. When you change these, check dependencies in the demo and starter projects.

## Build & tests

- Full build (example):

```bash
# Backend build
cd harmony-demo/api
./mvnw clean package

# Frontend build
cd ../../harmony-demo/ui
npm install
npm run build
```

- Tests
  - Backend tests: `./mvnw test` in the respective API module
  - Frontend tests: usually `npm run test` (if configured)

## Deployment notes

- Docker: The `lib/templates` folder contains Dockerfile and compose templates (`api.Dockerfile.template`, `ui-app.vue.template`, `server-docker-compose.yml.template`). These can serve as a base for container builds.
- CI/CD: Jenkins pipeline templates can be found under `lib/templates/jenkins-*`.
- Production: Create production builds (`mvn package`, `npm run build`) and package artifacts into container images or deploy them to your infrastructure.

## Configuration & environment files

- API: `application.yaml`, `LOCAL_DEV_ONLY_PROPERTY_VALUES.json` (local overrides) or environment variables
- UI: `nuxt.config.ts`, `project-configuration.json`, `.env` files (if used)

Be careful not to commit sensitive values to Git. Example or template files are available under `lib/templates`.

## Troubleshooting

- Maven wrapper issues
  - Ensure `mvnw` is executable: `chmod +x mvnw`
  - For network/proxy issues, check Maven settings (`~/.m2/settings.xml`).

- Node/NPM issues
  - Verify Node version, reinstall node modules: `rm -rf node_modules && npm ci`

- Ports in use
  - Check with `lsof -i :3000` or `lsof -i :8080` and stop the processes or adjust ports.

- CORS issues between UI and API
  - Enable or configure CORS policy in the API during development.

## Further notes

- Templates in `lib/templates` contain examples for Docker, CI, and common configuration files.
- Compare `harmony-demo` and `harmony-starter` to see different entry points.

## License
See `LICENSE` in the project root.

---
