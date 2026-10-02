# RentEasy - Implementation Plan

## Overview
RentEasy is a Smart Rental Decision Support System Android application built using Kotlin, Jetpack Compose, Material 3, and Firebase (Auth, Firestore, Storage) with a robust offline demo mode via DemoDataStore.

## Build Order & Milestones

### Milestone 1: Project Setup, Gradle, Theme & Constants
- [ ] Setup `gradle/libs.versions.toml`, `settings.gradle.kts`, `build.gradle.kts`, and `app/build.gradle.kts`
- [ ] Setup Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle-wrapper.properties` version 8.9)
- [ ] Add placeholder `app/google-services.json`
- [ ] Setup `AndroidManifest.xml` (INTERNET permission, Application, MainActivity)
- [ ] Setup theme: `Color.kt`, `Theme.kt`, `Type.kt` (Material 3 Light Theme, custom hex palette)
- [ ] Setup constants and logging: `Constants.kt`, `RentEasyLog.kt`
- [ ] Verification: `./gradlew assembleDebug`

### Milestone 2: Data Models, DemoDataStore & Repositories (Auth/User)
- [ ] Setup models: `User.kt`, `Property.kt`, `RentalRequest.kt`
- [ ] Setup `DemoDataStore.kt` with seeded demo data (Tenant, Owner, Admin; properties in PENDING, APPROVED, REJECTED, RENTED; requests)
- [ ] Setup `AuthRepository.kt` & `UserRepository.kt` with Firebase + Demo fallback & flow merging
- [ ] Verification: `./gradlew assembleDebug`

### Milestone 3: Navigation, Components, Splash, Login & Register
- [ ] Setup `Routes.kt` and `RentEasyNavigation.kt` (role-based routing for TENANT, OWNER, ADMIN)
- [ ] Common components: `LoadingView.kt`, `RentEasySearchBar.kt`, `PropertyCard.kt`, `RentalRequestCard.kt`, `CustomTextField.kt`, `ErrorBanner.kt`
- [ ] Auth & Entry UI: `SplashScreen.kt` (pulsing logo, gradient), `LoginScreen.kt` (show/hide password, demo quick-login chips), `RegisterScreen.kt` (role selector, phone validation)
- [ ] Verification: `./gradlew assembleDebug`

### Milestone 4: Tenant Features, Smart Rental Score & Cost Estimator
- [ ] Logic utilities: `SmartRentalScore.kt`, `TotalCostEstimator.kt`
- [ ] Repositories: `PropertyRepository.kt`, `WishlistRepository.kt`, `RentalRequestRepository.kt`
- [ ] Tenant UI:
  - `TenantHomeScreen.kt` (Filter by rent, bedrooms, type; search by city/title; wishlist toggle)
  - `PropertyDetailScreen.kt` (Image carousel, amenities, score ring + explainability, cost breakdown, request modal)
  - `WishlistScreen.kt`
  - `ComparePropertiesScreen.kt` (Side-by-side comparison of 2+ properties)
  - `TenantRequestsScreen.kt` (Status chips, reveals owner contact only if ACCEPTED with Call/Email intents)
- [ ] Verification: `./gradlew assembleDebug`

### Milestone 5: Owner Features & Request Management
- [ ] Owner UI:
  - `OwnerDashboardScreen.kt` (List own properties with status tags, stats)
  - `AddEditPropertyScreen.kt` (Multiple image picker, utility/maintenance cost estimators, location)
  - `OwnerRequestsScreen.kt` (Accept/Reject with single atomic batch write to set request Accepted and property RENTED)
- [ ] Storage handling with fallback to local URIs in Demo mode
- [ ] Verification: `./gradlew assembleDebug`

### Milestone 6: Admin Moderation, Security Rules & Polish
- [ ] Admin UI:
  - `AdminDashboardScreen.kt` (Metrics cards: users, tenants, owners, properties, pending approvals, requests)
  - `AdminApprovalsScreen.kt` (Approve/Reject pending property listings)
  - `AdminUsersScreen.kt` & `AdminRequestsScreen.kt`
- [ ] Security rules: `firestore.rules` and `storage.rules`
- [ ] Profile editing: `ProfileScreen.kt` (name, phone)
- [ ] Polish: Sealed UI states (`Loading`, `Success`, `Error`, `Empty`) across all screens
- [ ] Verification: `./gradlew assembleDebug`

### Milestone 7: Unit Tests & Verification
- [ ] Unit tests for `SmartRentalScoreTest.kt` (normal case, single-property city, zero rent, empty amenities, clamping at 0 and 100)
- [ ] Unit tests for `TotalCostEstimatorTest.kt`
- [ ] Full compilation and test execution: `./gradlew test` and `./gradlew assembleDebug`
- [ ] Update `README.md` with complete documentation, architecture, and manual Firebase setup guide
