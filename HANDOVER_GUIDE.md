# RentEasy - Senior Engineer Handover & Setup Guide

Welcome! This document provides a complete, step-by-step guide to cloning, setting up, executing, and connecting the production Firebase database for the **RentEasy (Smart Rental Decision Support System)** Android application.

---

## 📋 Table of Contents
1. [Repository Setup & Antigravity Prompt (For Your Environment)](#1-repository-setup--antigravity-prompt)
2. [Project Overview & Architecture](#2-project-overview--architecture)
3. [Building & Running the App Locally](#3-building--running-the-app-locally)
4. [Step-by-Step Production Firebase & Database Setup](#4-step-by-step-production-firebase--database-setup)
5. [Firestore Schema & Data Seeding Reference](#5-firestore-schema--data-seeding-reference)
6. [Testing & Verification Commands](#6-testing--verification-commands)

---

## 1. Repository Setup & Antigravity Prompt

### 📥 A. Cloning the Repository to Your Local Machine
Run the following commands in your terminal:
```bash
git clone https://github.com/Bowsiyadeepan/Rent_Easy.git
cd Rent_Easy
```

### 🤖 B. Prompt to Give Antigravity in Your IDE / Workspace
If you are opening this workspace in Antigravity, copy and paste the following prompt to let Antigravity inspect, index, and prepare the project:

```text
ROLE & TASK:
You are assisting me as a Senior Android Engineer in this workspace. I have cloned the "RentEasy" repository (https://github.com/Bowsiyadeepan/Rent_Easy.git). 

Please inspect the existing codebase, verify that all Gradle dependencies, Kotlin Compose UI screens, and repositories are intact, and check whether my local environment (JDK 17, Android SDK 35) is configured properly.

Next, guide me through or assist in:
1. Placing the real `google-services.json` in `app/google-services.json`.
2. Verifying the Cloud Firestore connection, Firebase Authentication, and Cloud Storage integration.
3. Running `./gradlew test` and `./gradlew assembleDebug` to confirm clean builds.
4. Continuing development for any live cloud functions, real-time push notifications, or additional database listeners.

Let's begin by checking the project structure and running a quick build verification.
```

---

## 2. Project Overview & Architecture

RentEasy is built with modern Android standards:
- **Language:** Kotlin 2.0.21 (`org.jetbrains.kotlin.plugin.compose`)
- **UI Toolkit:** Jetpack Compose + Material 3 (Light Theme, `#2563EB` brand blue)
- **Architecture:** MVVM + Repository Pattern with Kotlin `StateFlow` and sealed UI states (`Loading`, `Success`, `Error`, `Empty`)
- **Offline / Demo Resilience:** Built-in `DemoDataStore.kt` that mirrors all database writes in-memory and automatically acts as a zero-config offline fallback if Firebase credentials are not yet configured.
- **Backend Stack:** Firebase Authentication, Cloud Firestore, Firebase Cloud Storage

### Package Hierarchy (`com.example.renteasy`):
```
app/src/main/java/com/example/renteasy/
 ├── MainActivity.kt               # Root Activity hosting Jetpack Compose navigation
 ├── RentEasyApplication.kt        # App entrypoint initializing DemoDataStore & logging
 ├── navigation/
 │    ├── Routes.kt                # Type-safe navigation destination constants
 │    └── RentEasyNavigation.kt    # Navigation host routing between Tenant, Owner & Admin flows
 ├── components/                   # Reusable UI components (SearchBar, PropertyCard, RequestCard, etc.)
 ├── data/
 │    ├── model/                   # Data classes (User, Property, RentalRequest)
 │    └── repository/              # Repositories merging Firestore flows & local DemoDataStore
 ├── ui/
 │    ├── auth/                    # Login, Register, AuthViewModel (1-tap demo logins)
 │    ├── splash/                  # Pulsing logo animated splash (1.8s) + session routing
 │    ├── tenant/                  # TenantHome, PropertyDetail, Wishlist, Compare, MyRequests
 │    ├── owner/                   # OwnerDashboard, AddEditProperty, OwnerRequests
 │    ├── admin/                   # AdminDashboard, Approvals, Properties, Requests, Users
 │    ├── profile/                 # ProfileScreen (editable details + logout)
 │    └── theme/                   # Color, Theme, Type
 └── utils/
      ├── Constants.kt             # Global constants, roles, and status enums
      ├── SmartRentalScore.kt      # 0–100 explainable multi-factor scoring algorithm
      └── TotalCostEstimator.kt    # Monthly, deposit, outlay, and annual cost breakdown
```

---

## 3. Building & Running the App Locally

### Prerequisites
- **JDK:** OpenJDK 17 or 21 (Gradle target is Java 17 / JVM Target `17`).
- **Android SDK:** `compileSdk 35`, `targetSdk 35`, `minSdk 24`.
- **Android Studio:** Android Studio Koala / Ladybug or later recommended.

### Build via Command Line
```bash
# On Windows (PowerShell / Command Prompt):
.\gradlew.bat test
.\gradlew.bat assembleDebug

# On macOS / Linux:
./gradlew test
./gradlew assembleDebug
```

### Running on Emulator / Physical Device
1. Open the project folder in **Android Studio**.
2. Ensure your `local.properties` points to your Android SDK directory:
   ```properties
   sdk.dir=/Users/your-username/Library/Android/sdk   # macOS
   # OR
   sdk.dir=C\:\\Users\\your-username\\AppData\\Local\\Android\\Sdk  # Windows
   ```
3. Select a target emulator or connected USB device (API 24+).
4. Click **Run 'app'** (`Shift + F10`).

> 💡 **Offline Demo Mode:** The app works right out of the box even without Firebase configured. On the Login screen, click any of the **1-Tap Demo Login** chips (**Tenant**, **Owner**, or **Admin**) to immediately test full functionality.

---

## 4. Step-by-Step Production Firebase & Database Setup

To hook up your real Firebase project and migrate off demo mode:

### Step 1: Create a Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add Project** and name it (e.g., `RentEasy-App`).
3. Add an Android app with package name:
   ```
   com.example.renteasy
   ```
4. Download the resulting `google-services.json` file.

### Step 2: Place `google-services.json`
Copy the downloaded file to your local project directory:
```
RentEasy/app/google-services.json
```
*(Note: `app/google-services.json` is already listed in `.gitignore` to prevent leaking API keys).*

---

### Step 3: Enable Firebase Authentication
1. In Firebase Console, go to **Build** → **Authentication**.
2. Click **Get Started** → Select **Email/Password**.
3. Enable **Email/Password** and click **Save**.

---

### Step 4: Set Up Cloud Firestore
1. In Firebase Console, go to **Build** → **Firestore Database**.
2. Click **Create Database** → Choose your preferred cloud region.
3. Start in **Production Mode** (rules will be deployed in Step 6).

---

### Step 5: Set Up Cloud Storage
1. In Firebase Console, go to **Build** → **Storage**.
2. Click **Get Started** → Accept default bucket configuration.

---

### Step 6: Deploy Security Rules
The repository contains production-ready security rules in the root folder.
Install Firebase CLI (if not already installed):
```bash
npm install -g firebase-tools
firebase login
firebase use --add <your-firebase-project-id>
```

Deploy the rules:
```bash
firebase deploy --only firestore:rules,storage
```

#### What the Rules Enforce:
- **`firestore.rules`:**
  - Authenticated tenants can only read properties with `status == 'APPROVED'`.
  - Owners can only create/update/delete their own property listings.
  - Newly created properties are forced to `status == 'PENDING'`.
  - Only users with `role == 'ADMIN'` can change status to `APPROVED` or `REJECTED`.
  - In rental requests, applicants can only create requests where `tenantId == auth.uid`.
  - Owners can accept/reject requests.
- **`storage.rules`:**
  - Limits property photo uploads to authenticated users with images < 5MB.

---

### Step 7: Create the Initial Admin User
1. Open the app on your device and click **Sign Up** (or create a user in Firebase Auth Console).
2. Enter your admin email and details, select any role to register.
3. Open **Firebase Console** → **Firestore Database** → collection **`users`**.
4. Find the document matching your user's `uid`.
5. Edit the **`role`** field value to:
   ```json
   "role": "ADMIN"
   ```
6. Sign in with those credentials on the app. The app will automatically route you to the **Admin Control Center** with moderation queues and system-wide audits.

---

## 5. Firestore Schema & Data Seeding Reference

### 1. `users` Collection (`/users/{uid}`)
```json
{
  "uid": "string",
  "name": "string",
  "email": "string",
  "phone": "string (10 digits)",
  "role": "TENANT | OWNER | ADMIN"
}
```

### 2. `properties` Collection (`/properties/{propertyId}`)
```json
{
  "propertyId": "string",
  "ownerId": "string",
  "title": "string",
  "rent": 28000.0,
  "deposit": 60000.0,
  "address": "string",
  "city": "Bengaluru",
  "bedrooms": 2,
  "bathrooms": 2,
  "description": "string",
  "type": "Apartment | Independent House | Villa | Studio | PG / Shared",
  "status": "PENDING | APPROVED | REJECTED | RENTED",
  "imageUrls": ["https://..."],
  "amenities": ["WiFi / Internet", "Air Conditioning", "Power Backup", "Car Parking"],
  "estimatedUtilityCost": 2500.0,
  "estimatedMaintenanceCost": 2000.0,
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

### 3. `rental_requests` Collection (`/rental_requests/{requestId}`)
```json
{
  "requestId": "string",
  "tenantId": "string",
  "tenantName": "string",
  "tenantEmail": "string",
  "ownerId": "string",
  "propertyId": "string",
  "propertyTitle": "string",
  "rent": 28000.0,
  "status": "Pending | Accepted | Rejected",
  "timestamp": "Timestamp"
}
```

### 4. `wishlists` Collection (`/wishlists/{tenantId}`)
```json
{
  "propertyIds": ["prop_101", "prop_103"]
}
```

---

## 6. Testing & Verification Commands

All unit tests and build targets have been validated locally. Run them at any time to guarantee code integrity:

```bash
# Run unit tests for SmartRentalScore and TotalCostEstimator algorithms
.\gradlew.bat test

# Verify debug APK build
.\gradlew.bat assembleDebug
```
