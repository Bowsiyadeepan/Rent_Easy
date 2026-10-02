# RentEasy - Smart Rental Decision Support System

RentEasy is a comprehensive, production-grade Android application designed to streamline and empower the rental decision-making process for tenants, property owners, and administrators. Built entirely in Kotlin using Jetpack Compose, Material 3, and Firebase, RentEasy features an explainable multi-factor Smart Rental Score, a detailed Total Cost Estimator, side-by-side property comparison matrix, atomic rental transaction handling, and robust offline capability.

---

## 🌟 Key Features

### 👤 1. Role-Based Experiences
- **Tenant Experience:**
  - **Explore Homes:** Browse verified listings filtered by city, rent range, bedrooms, and property type.
  - **Smart Rental Score (0–100):** Weighted multi-factor evaluation combining affordability vs city average (35%), space/room value (20%), amenities completeness (20%), deposit friendliness (10%), and hidden cost ratio (15%), with clear explainability text.
  - **Total Cost Estimator:** Transparent breakdown of monthly rent, estimated utilities, society maintenance dues, refundable deposit, first-month cash outlay, and annual cost forecast.
  - **Side-by-Side Comparison:** Compare 2 to 4 properties concurrently across pricing, scores, and amenities.
  - **Wishlist:** Quick 1-tap favoriting persisted both locally and in Cloud Firestore.
  - **Rental Applications:** Submit applications with duplicate-pending checks; contact details of the property owner are revealed with direct **Call** and **Email** intent actions only after the owner accepts the application.

- **Property Owner Experience:**
  - **Listing Dashboard:** Overview of all owned properties with live status badges (`PENDING`, `APPROVED`, `REJECTED`, `RENTED`).
  - **Add & Edit Property:** Multi-image upload to Firebase Storage, detailed amenity selections, utility/maintenance estimations, and location mapping.
  - **Application Management:** Review incoming tenant applications with **Accept** and **Reject** actions. Acceptance triggers an atomic batch write in Firestore, transitioning the request to `Accepted` and the property to `RENTED` to strictly prevent double-renting.

- **Administrator Experience:**
  - **System Control Center:** Live analytics cards for Total Users, Active Listings, Pending Approvals, and Applications.
  - **Moderation Queue:** One-tap **Approve** and **Reject** controls for pending property submissions.
  - **System Audit:** Comprehensive directory of all registered users, listings, and applications.

---

## 🏗️ Architecture & Tech Stack

RentEasy follows the standard **MVVM (Model-View-ViewModel) + Repository** architecture with unidirectional data flow and sealed UI states:

```
com.example.renteasy
 ├── MainActivity.kt
 ├── RentEasyApplication.kt
 ├── navigation/
 │    ├── Routes.kt
 │    └── RentEasyNavigation.kt
 ├── components/
 │    ├── LoadingView.kt
 │    ├── RentEasySearchBar.kt
 │    ├── PropertyCard.kt
 │    ├── RentalRequestCard.kt
 │    ├── RentEasyTextField.kt
 │    ├── RentEasyTopAppBar.kt
 │    ├── RentEasyBottomBar.kt
 │    └── StatusChip.kt
 ├── data/
 │    ├── model/
 │    │    ├── User.kt
 │    │    ├── Property.kt
 │    │    └── RentalRequest.kt
 │    └── repository/
 │         ├── AuthRepository.kt
 │         ├── UserRepository.kt
 │         ├── PropertyRepository.kt
 │         ├── RentalRequestRepository.kt
 │         ├── WishlistRepository.kt
 │         └── DemoDataStore.kt
 ├── ui/
 │    ├── auth/ (LoginScreen, RegisterScreen, AuthViewModel)
 │    ├── splash/ (SplashScreen)
 │    ├── tenant/ (TenantHomeScreen, PropertyDetailScreen, WishlistScreen, ComparePropertiesScreen, TenantRequestsScreen, TenantViewModel)
 │    ├── owner/ (OwnerDashboardScreen, AddEditPropertyScreen, OwnerRequestsScreen, OwnerViewModel)
 │    ├── admin/ (AdminDashboardScreen, AdminApprovalsScreen, AdminPropertiesScreen, AdminRequestsScreen, AdminUsersScreen, AdminViewModel)
 │    ├── profile/ (ProfileScreen)
 │    └── theme/ (Color, Theme, Type)
 └── utils/
      ├── Constants.kt
      ├── RentEasyLog.kt
      ├── SmartRentalScore.kt
      └── TotalCostEstimator.kt
```

### Pinned Versions
- **Language & Compiler:** Kotlin 2.0.21 (`org.jetbrains.kotlin.plugin.compose`)
- **Build Tools:** Android Gradle Plugin (AGP) 8.7.2, Gradle 8.9, JDK 17
- **Target Platform:** minSdk 24, compileSdk 35, targetSdk 35
- **UI Toolkit:** Jetpack Compose BOM 2024.10.00, Material 3 1.3.0, Material Icons Extended
- **Navigation:** Navigation Compose 2.8.3
- **Cloud & Backend:** Firebase BoM 33.5.1 (Auth, Cloud Firestore, Cloud Storage)
- **Image Loading:** Coil Compose 2.7.0
- **Asynchronous:** Kotlin Coroutines 1.9.0 + `kotlinx-coroutines-play-services`

---

## 📴 Offline Demo Mode & Seeded Data

RentEasy includes a complete in-memory fallback layer (`DemoDataStore`) that mirrors all writes and merges seamlessly with live Firestore streams via `combine` and `distinctBy`.

### Quick 1-Tap Demo Credentials
On the Login screen, use the quick login buttons or enter:
- **Tenant:** `tenant@renteasy.com` | Password: `password123`
- **Owner:** `owner@renteasy.com` | Password: `password123`
- **Admin:** `admin@renteasy.com` | Password: `password123`

---

## 🚀 Firebase Setup & Deployment Guide

When you are ready to connect your production Firebase project:

### 1. Place `google-services.json`
Download your `google-services.json` from the Firebase Console and place it at:
```
RentEasy/app/google-services.json
```
*(Note: This file is ignored by Git in `.gitignore` for security).*

### 2. Enable Firebase Services
In the [Firebase Console](https://console.firebase.google.com/):
1. **Authentication:** Enable the **Email/Password** sign-in provider.
2. **Cloud Firestore:** Create a database in production mode.
3. **Cloud Storage:** Enable Storage for property photo uploads.

### 3. Deploy Security Rules
Deploy the included security rules using the Firebase CLI:
```bash
firebase deploy --only firestore:rules,storage
```
- [firestore.rules](file:///e:/3rd%20Semester%20Projects/RentEasy/firestore.rules): Authenticated reads on approved properties, owner-scoped write permissions, admin-only approval transitions, and protected contact visibility.
- [storage.rules](file:///e:/3rd%20Semester%20Projects/RentEasy/storage.rules): Authenticated uploads limited to image MIME types under 5MB.

### 4. Create the Admin User in Firebase
1. Register a new user in the app with your admin email (or create one in the Firebase Auth console).
2. Go to Cloud Firestore → `users` collection → Open the document with that user's `uid`.
3. Set the `role` field to `"ADMIN"`.
4. Sign in as that user to access the **Admin Control Center**.

---

## 🧪 Testing & Verification

Run the test suite via Gradle:
```bash
# Run unit tests (SmartRentalScore & TotalCostEstimator)
./gradlew test

# Assemble Debug APK
./gradlew assembleDebug
```
