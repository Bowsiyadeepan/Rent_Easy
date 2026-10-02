package com.example.renteasy.utils

object Constants {
    // Roles
    const val ROLE_TENANT = "TENANT"
    const val ROLE_OWNER = "OWNER"
    const val ROLE_ADMIN = "ADMIN"

    // Property Status
    const val STATUS_PENDING = "PENDING"
    const val STATUS_APPROVED = "APPROVED"
    const val STATUS_REJECTED = "REJECTED"
    const val STATUS_RENTED = "RENTED"

    // Rental Request Status
    const val REQUEST_PENDING = "Pending"
    const val REQUEST_ACCEPTED = "Accepted"
    const val REQUEST_REJECTED = "Rejected"

    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PROPERTIES = "properties"
    const val COLLECTION_RENTAL_REQUESTS = "rental_requests"
    const val COLLECTION_WISHLISTS = "wishlists"

    // Property Types
    val PROPERTY_TYPES = listOf("All", "Apartment", "Independent House", "Villa", "Studio", "PG / Shared")
    val PROPERTY_TYPES_INPUT = listOf("Apartment", "Independent House", "Villa", "Studio", "PG / Shared")

    // Default Amenities
    val AVAILABLE_AMENITIES = listOf(
        "WiFi / Internet",
        "Air Conditioning",
        "Power Backup",
        "Car Parking",
        "24/7 Security / CCTV",
        "Elevator / Lift",
        "Gym",
        "Swimming Pool",
        "Balcony",
        "Furnished",
        "Geyser / Water Heater",
        "Water Supply (24/7)"
    )

    // Demo user credentials for quick test
    const val DEMO_TENANT_EMAIL = "tenant@renteasy.com"
    const val DEMO_TENANT_UID = "demo_tenant_uid_101"

    const val DEMO_OWNER_EMAIL = "owner@renteasy.com"
    const val DEMO_OWNER_UID = "demo_owner_uid_202"

    const val DEMO_ADMIN_EMAIL = "admin@renteasy.com"
    const val DEMO_ADMIN_UID = "demo_admin_uid_303"
    
    const val DEMO_DEFAULT_PASSWORD = "password123"
}
