# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Suncar Trabajador is an Android mobile application built with Kotlin and Jetpack Compose for field workers to create and manage service reports. The app supports three types of reports: Inversión (Investment), Mantenimiento (Maintenance), and Avería (Breakdowns/Complaints).

## Build Commands

- **Build project**: `./gradlew build`
- **Build debug APK**: `./gradlew assembleDebug`
- **Build release APK**: `./gradlew assembleRelease`
- **Clean project**: `./gradlew clean`
- **Run tests**: `./gradlew test`
- **Run Android instrumented tests**: `./gradlew connectedAndroidTest`

## Architecture

The application follows a clean architecture pattern with clear separation of concerns:

### Package Structure
- `app/service_implementations/` - Service layer implementations for business logic
- `data/` - Data layer containing repositories, API services, schemas, and local storage
  - `local/` - Local data management (SessionManager)
  - `repositories/` - Repository pattern implementations
  - `schemas/` - Data transfer objects and API models
  - `services/` - Retrofit API interface definitions
  - `http/` - HTTP configuration and Retrofit client setup
- `domain/models/` - Domain models representing core business entities
- `singleton/` - Application-wide singleton objects (Auth, Trabajadores)
- `ui/` - UI layer organized by feature/screen
  - `features/` - Reusable UI components organized by functionality
  - `screens/` - Full screen composables
  - `reportes/` - Report-specific screens (Averia, Mantenimiento, Inversion)
  - `shared/` - Shared UI components
  - `theme/` - Material Design theming
- `utils/` - Utility classes and helper functions

### Key Architectural Patterns
- **MVVM Pattern**: Each UI component has associated State, ViewModel, and Validator classes
- **Repository Pattern**: Data access is abstracted through repository classes
- **Service Layer**: Business logic is encapsulated in service implementations
- **Singleton Pattern**: Auth and Trabajadores are managed as application-wide singletons

### Navigation
The app uses Jetpack Navigation Compose with a bottom navigation bar containing:
- Reportes (Reports listing)
- Nuevo (New report creation)
- Cuenta (Account settings)

Report creation flows are handled through nested navigation to specific report types.

## Key Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern Android UI toolkit
- **Retrofit**: HTTP client for API communication
- **Material Design 3**: UI design system
- **Lottie**: Animations
- **Coil**: Image loading
- **Google Play Services**: Location and Maps integration

## Development Notes
- Minimum SDK: 28 (Android 9)
- Target SDK: 36
- Uses Compose BOM for version alignment
- Includes signature pad functionality for client signatures
- Supports location services for address completion
- Implements custom animations for screen transitions