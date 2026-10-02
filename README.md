<div align="center">

# 🏡 RentEasy
### *Smart Rental Decision Support System*

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-2024.10.00-green?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material_3-1.3.0-blue?style=for-the-badge&logo=materialdesign)](https://m3.material.io/)
[![Firebase](https://img.shields.io/badge/Firebase-33.5.1-orange?style=for-the-badge&logo=firebase)](https://firebase.google.com/)
[![Android](https://img.shields.io/badge/Android-SDK_24+-brightgreen?style=for-the-badge&logo=android)](https://developer.android.com/)

**RentEasy** is an intelligent, full-stack Android application built to revolutionize the rental journey for tenants, property owners, and administrators. Powered by a multi-factor **Smart Rental Score** algorithm, an upfront **Total Cost Estimator**, side-by-side property comparison matrix, and atomic rental request locking.

</div>

---

## ✨ Key Features & Capabilities

### 🏢 1. Tenant Experience
- **Smart Rental Score (0–100):** Weighted multi-factor evaluation combining affordability vs. city market rates (35%), space/room value (20%), amenities completeness (20%), deposit friendliness (10%), and hidden cost ratio (15%), with real-time explainability text.
- **Total Cost Estimator:** Clear breakdown of monthly rent, estimated utilities, society maintenance fees, refundable security deposit, first-month cash outlay, and annual cost forecast.
- **Property Comparison Matrix:** Side-by-side evaluation of up to 4 listings across pricing, specs, score breakdowns, and amenities checklist.
- **Interactive Search & Multi-Filters:** Search by title, locality, or city with dynamic rent range sliders, bedroom filters, and property type tabs.
- **Secure Rental Applications:** Send rental applications with built-in duplicate-pending protection.
- **Contact Reveal Protection:** Owner contact details (Phone & Email with 1-tap Call/Email buttons) are strictly protected and only revealed after an application is accepted.

### 🔑 2. Property Owner Experience
- **Live Listing Dashboard:** Overview of all owned properties with live status badges (`PENDING`, `APPROVED`, `REJECTED`, `RENTED`).
- **Rich Listing Editor:** Multi-image uploads, dynamic amenity picker, utility/maintenance estimations, and location mapping.
- **Atomic Request Acceptance:** Review tenant applications with 1-tap Accept/Reject. Acceptance triggers an atomic Firestore batch write that simultaneously sets the request to `Accepted` and the property to `RENTED` to prevent double-renting.

### 🛡️ 3. Administrator Experience
- **Control Center Analytics:** System metrics for total users, active listings, pending moderation queues, and total requests.
- **Moderation Queue:** One-tap approval or rejection of pending property submissions.
- **System Audit:** Directory of all registered users, listings, and applications across the platform.

---

## 🏛️ System Architecture

RentEasy is built following the official Android **MVVM + Repository Pattern** with reactive **StateFlows** and sealed UI states (`Loading`, `Success`, `Error`, `Empty`):

```
+-------------------------------------------------------------+
|                      UI Layer (Compose)                     |
|  [Tenant Screens]   [Owner Screens]   [Admin Screens]      |
+-------------------------------------------------------------+
                              | (StateFlow / Events)
                              v
+-------------------------------------------------------------+
|                     ViewModel Layer                         |
|  [TenantViewModel]   [OwnerViewModel]   [AdminViewModel]    |
+-------------------------------------------------------------+
                              | (Flows / Suspend Calls)
                              v
+-------------------------------------------------------------+
|                    Repository Layer                         |
|  [AuthRepo] [UserRepo] [PropertyRepo] [RequestRepo] [Wishlist]|
+-------------------------------------------------------------+
               |                               |
               v (Cloud Sync)                  v (Offline Fallback)
+-------------------------------+ +---------------------------+
|    Cloud Backend (Firebase)   | |       DemoDataStore       |
|  Auth | Firestore | Storage   | | In-Memory Seeded Flows    |
+-------------------------------+ +---------------------------+
```

---

## 🛠️ Tech Stack & Pinned Dependencies

| Component | Library / Version | Purpose |
| :--- | :--- | :--- |
| **Language** | Kotlin `2.0.21` | Modern, concise language with Compose compiler support |
| **UI Toolkit** | Jetpack Compose BOM `2024.10.00` | Declarative UI framework |
| **Design System** | Material 3 `1.3.0` | Light theme with custom hex palette |
| **Navigation** | Navigation Compose `2.8.3` | Type-safe single-activity role-based routing |
| **Backend & Auth** | Firebase BoM `33.5.1` | Authentication, Cloud Firestore, Cloud Storage |
| **Image Loading** | Coil Compose `2.7.0` | Asynchronous image loading with cached placeholders |
| **Concurrency** | Kotlin Coroutines `1.9.0` | Reactive StateFlows and asynchronous tasks |
| **Target SDK** | compileSdk `35`, minSdk `24` | Compatible with 95%+ of active Android devices |

---

## 🚀 Quick Start Guide

### 1. Clone the Repository
```bash
git clone https://github.com/Bowsiyadeepan/Rent_Easy.git
cd Rent_Easy
```

### 2. Build & Test
```bash
# Run unit tests (Smart Rental Score & Cost Estimator test suites)
./gradlew test

# Assemble Debug APK
./gradlew assembleDebug
```

### 3. 1-Tap Offline Demo Logins
RentEasy includes instant in-memory seeded demo accounts on the login screen:
- **Tenant:** `tenant@renteasy.com` | `password123`
- **Owner:** `owner@renteasy.com` | `password123`
- **Admin:** `admin@renteasy.com` | `password123`

---

## 🔒 Security Rules & Permissions

The repository includes production-ready [firestore.rules](firestore.rules) and [storage.rules](storage.rules):
- **Tenants** can only read `APPROVED` properties.
- **Owners** can only create and update their own properties.
- **Admins** have exclusive authority to set property statuses to `APPROVED` or `REJECTED`.
- **Tenant Applications** require `tenantId == auth.uid`.
- **Owner Contact Information** is hidden from unauthenticated or pending applicants.

---

<div align="center">
Developed with ❤️ using Jetpack Compose & Firebase.
</div>
