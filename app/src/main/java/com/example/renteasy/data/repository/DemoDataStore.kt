package com.example.renteasy.data.repository

import android.content.Context
import com.example.renteasy.data.model.Property
import com.example.renteasy.data.model.RentalRequest
import com.example.renteasy.data.model.User
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.UUID

object DemoDataStore {
    private const val TAG = "DemoDataStore"

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _rentalRequests = MutableStateFlow<List<RentalRequest>>(emptyList())
    val rentalRequests: StateFlow<List<RentalRequest>> = _rentalRequests.asStateFlow()

    private val _wishlists = MutableStateFlow<Map<String, Set<String>>>(emptyMap()) // tenantId -> Set<propertyId>
    val wishlists: StateFlow<Map<String, Set<String>>> = _wishlists.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun initialize(context: Context) {
        if (_users.value.isEmpty()) {
            seedInitialData()
        }
    }

    private fun seedInitialData() {
        RentEasyLog.i(TAG, "Seeding comprehensive demo data...")

        val tenantUser = User(
            uid = Constants.DEMO_TENANT_UID,
            name = "Alice Tenant",
            email = Constants.DEMO_TENANT_EMAIL,
            phone = "9876543210",
            role = Constants.ROLE_TENANT
        )
        val ownerUser = User(
            uid = Constants.DEMO_OWNER_UID,
            name = "Bob Owner",
            email = Constants.DEMO_OWNER_EMAIL,
            phone = "9876543211",
            role = Constants.ROLE_OWNER
        )
        val adminUser = User(
            uid = Constants.DEMO_ADMIN_UID,
            name = "Admin System",
            email = Constants.DEMO_ADMIN_EMAIL,
            phone = "9876543212",
            role = Constants.ROLE_ADMIN
        )

        _users.value = listOf(tenantUser, ownerUser, adminUser)

        val seededProperties = listOf(
            Property(
                propertyId = "prop_101",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Modern 2BHK Apartment in Indiranagar",
                rent = 28000.0,
                deposit = 60000.0,
                address = "100ft Road, Near Metro Station, Indiranagar",
                city = "Bengaluru",
                bedrooms = 2,
                bathrooms = 2,
                description = "Spacious, well-ventilated modern 2BHK with modular kitchen, premium fixtures, 24/7 security, covered car parking, and power backup. Located 2 minutes away from the metro station.",
                type = "Apartment",
                status = Constants.STATUS_APPROVED,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "Car Parking", "24/7 Security / CCTV", "Elevator / Lift", "Gym", "Geyser / Water Heater"),
                estimatedUtilityCost = 2500.0,
                estimatedMaintenanceCost = 2000.0,
                latitude = 12.9716,
                longitude = 77.5946
            ),
            Property(
                propertyId = "prop_102",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Luxury 3BHK Penthouse with Lake View",
                rent = 45000.0,
                deposit = 100000.0,
                address = "Lakefront Heights, Sarjapur Road",
                city = "Bengaluru",
                bedrooms = 3,
                bathrooms = 3,
                description = "Panoramic lake views from the private balcony, Italian marble flooring, central air conditioning, infinity pool access, and premium clubhouse facilities.",
                type = "Apartment",
                status = Constants.STATUS_APPROVED,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "Car Parking", "24/7 Security / CCTV", "Elevator / Lift", "Gym", "Swimming Pool", "Balcony", "Furnished"),
                estimatedUtilityCost = 4000.0,
                estimatedMaintenanceCost = 3500.0,
                latitude = 12.9249,
                longitude = 77.6800
            ),
            Property(
                propertyId = "prop_103",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Cozy 1BHK Studio near Bandra West",
                rent = 22000.0,
                deposit = 45000.0,
                address = "Hill Road, Bandra West",
                city = "Mumbai",
                bedrooms = 1,
                bathrooms = 1,
                description = "Perfect for young professionals or couples. Fully furnished with high-speed fiber internet, AC, smart TV, and refrigerator. Walkable to cafes and seaside promenade.",
                type = "Studio",
                status = Constants.STATUS_APPROVED,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1502005229762-ee152da915ba?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "24/7 Security / CCTV", "Furnished", "Geyser / Water Heater"),
                estimatedUtilityCost = 2000.0,
                estimatedMaintenanceCost = 1000.0,
                latitude = 19.0596,
                longitude = 72.8295
            ),
            Property(
                propertyId = "prop_104",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Independent 4BHK Villa with Private Garden",
                rent = 65000.0,
                deposit = 150000.0,
                address = "Palm Meadows, Whitefield",
                city = "Bengaluru",
                bedrooms = 4,
                bathrooms = 4,
                description = "Exclusive gated community villa featuring a private lawn, solar power, servant quarters, automated garage, and world-class sports amenities.",
                type = "Villa",
                status = Constants.STATUS_APPROVED,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "Car Parking", "24/7 Security / CCTV", "Gym", "Swimming Pool", "Balcony", "Furnished", "Water Supply (24/7)"),
                estimatedUtilityCost = 5000.0,
                estimatedMaintenanceCost = 4500.0,
                latitude = 12.9698,
                longitude = 77.7500
            ),
            Property(
                propertyId = "prop_105",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Affordable 2BHK in Kothrud",
                rent = 18000.0,
                deposit = 36000.0,
                address = "Paud Road, Kothrud",
                city = "Pune",
                bedrooms = 2,
                bathrooms = 1,
                description = "Peaceful residential neighborhood close to schools, markets, and IT hubs. Well-maintained society with low maintenance costs.",
                type = "Apartment",
                status = Constants.STATUS_APPROVED,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1560185127-6ed189bf02f4?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("Power Backup", "Car Parking", "Elevator / Lift", "Water Supply (24/7)"),
                estimatedUtilityCost = 1500.0,
                estimatedMaintenanceCost = 800.0,
                latitude = 18.5074,
                longitude = 73.8077
            ),
            // PENDING status property (for Admin approval workflow)
            Property(
                propertyId = "prop_106_pending",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Spacious 3BHK Builder Floor in Saket",
                rent = 35000.0,
                deposit = 70000.0,
                address = "J-Block, Saket",
                city = "Delhi",
                bedrooms = 3,
                bathrooms = 2,
                description = "Newly renovated builder floor with reserved stilt parking, modular wardrobes, and private terrace access.",
                type = "Independent House",
                status = Constants.STATUS_PENDING,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("WiFi / Internet", "Air Conditioning", "Car Parking", "Balcony", "Geyser / Water Heater"),
                estimatedUtilityCost = 3000.0,
                estimatedMaintenanceCost = 1500.0,
                latitude = 28.5244,
                longitude = 77.2167
            ),
            // REJECTED status property
            Property(
                propertyId = "prop_107_rejected",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Unfurnished Studio Flat",
                rent = 12000.0,
                deposit = 24000.0,
                address = "Old Town Alley, Central Road",
                city = "Pune",
                bedrooms = 1,
                bathrooms = 1,
                description = "Basic compact room with shared bathroom facilities. Incomplete documentation.",
                type = "Studio",
                status = Constants.STATUS_REJECTED,
                imageUrls = emptyList(),
                amenities = emptyList(),
                estimatedUtilityCost = 1000.0,
                estimatedMaintenanceCost = 500.0,
                latitude = 18.5204,
                longitude = 73.8567
            ),
            // RENTED status property
            Property(
                propertyId = "prop_108_rented",
                ownerId = Constants.DEMO_OWNER_UID,
                title = "Cozy 2BHK in Koramangala 4th Block",
                rent = 32000.0,
                deposit = 64000.0,
                address = "80ft Road, Koramangala 4th Block",
                city = "Bengaluru",
                bedrooms = 2,
                bathrooms = 2,
                description = "Fully furnished home rented out recently to verified professionals.",
                type = "Apartment",
                status = Constants.STATUS_RENTED,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80"
                ),
                amenities = listOf("WiFi / Internet", "Air Conditioning", "Power Backup", "Car Parking", "Furnished"),
                estimatedUtilityCost = 2800.0,
                estimatedMaintenanceCost = 1800.0,
                latitude = 12.9352,
                longitude = 77.6245
            )
        )

        _properties.value = seededProperties

        val seededRequests = listOf(
            RentalRequest(
                requestId = "req_201_pending",
                tenantId = Constants.DEMO_TENANT_UID,
                tenantName = "Alice Tenant",
                tenantEmail = Constants.DEMO_TENANT_EMAIL,
                ownerId = Constants.DEMO_OWNER_UID,
                propertyId = "prop_101",
                propertyTitle = "Modern 2BHK Apartment in Indiranagar",
                rent = 28000.0,
                status = Constants.REQUEST_PENDING,
                timestamp = Date(System.currentTimeMillis() - 3600000 * 4)
            ),
            RentalRequest(
                requestId = "req_202_accepted",
                tenantId = Constants.DEMO_TENANT_UID,
                tenantName = "Alice Tenant",
                tenantEmail = Constants.DEMO_TENANT_EMAIL,
                ownerId = Constants.DEMO_OWNER_UID,
                propertyId = "prop_108_rented",
                propertyTitle = "Cozy 2BHK in Koramangala 4th Block",
                rent = 32000.0,
                status = Constants.REQUEST_ACCEPTED,
                timestamp = Date(System.currentTimeMillis() - 86400000 * 2)
            ),
            RentalRequest(
                requestId = "req_203_rejected",
                tenantId = Constants.DEMO_TENANT_UID,
                tenantName = "Alice Tenant",
                tenantEmail = Constants.DEMO_TENANT_EMAIL,
                ownerId = Constants.DEMO_OWNER_UID,
                propertyId = "prop_102",
                propertyTitle = "Luxury 3BHK Penthouse with Lake View",
                rent = 45000.0,
                status = Constants.REQUEST_REJECTED,
                timestamp = Date(System.currentTimeMillis() - 86400000 * 5)
            )
        )

        _rentalRequests.value = seededRequests

        // Seed initial wishlist
        _wishlists.value = mapOf(
            Constants.DEMO_TENANT_UID to setOf("prop_101", "prop_103")
        )

        // Set default current user
        _currentUser.value = tenantUser
    }

    // --- User Management ---
    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun getUser(uid: String): User? {
        return _users.value.find { it.uid == uid }
    }

    fun getUserByEmail(email: String): User? {
        return _users.value.find { it.email.equals(email.trim(), ignoreCase = true) }
    }

    fun saveUser(user: User) {
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == user.uid }
        if (index >= 0) {
            currentList[index] = user
        } else {
            currentList.add(user)
        }
        _users.value = currentList

        if (_currentUser.value?.uid == user.uid) {
            _currentUser.value = user
        }
    }

    // --- Property Management ---
    fun addProperty(property: Property): String {
        val finalId = if (property.propertyId.isBlank()) "prop_${UUID.randomUUID().toString().take(8)}" else property.propertyId
        val newProperty = property.copy(propertyId = finalId)
        _properties.value = listOf(newProperty) + _properties.value
        return finalId
    }

    fun updateProperty(property: Property) {
        _properties.value = _properties.value.map {
            if (it.propertyId == property.propertyId) property else it
        }
    }

    fun deleteProperty(propertyId: String) {
        _properties.value = _properties.value.filterNot { it.propertyId == propertyId }
    }

    fun updatePropertyStatus(propertyId: String, status: String) {
        _properties.value = _properties.value.map {
            if (it.propertyId == propertyId) it.copy(status = status) else it
        }
    }

    // --- Rental Request Management ---
    fun createRentalRequest(request: RentalRequest): Result<String> {
        // Enforce duplicate-pending block
        val existingPending = _rentalRequests.value.any {
            it.tenantId == request.tenantId &&
                    it.propertyId == request.propertyId &&
                    it.status == Constants.REQUEST_PENDING
        }
        if (existingPending) {
            return Result.failure(IllegalStateException("A pending rental request already exists for this property."))
        }

        val finalId = if (request.requestId.isBlank()) "req_${UUID.randomUUID().toString().take(8)}" else request.requestId
        val newRequest = request.copy(requestId = finalId, timestamp = request.timestamp ?: Date())
        _rentalRequests.value = listOf(newRequest) + _rentalRequests.value
        return Result.success(finalId)
    }

    fun acceptRentalRequest(requestId: String, propertyId: String): Result<Unit> {
        // Check if property is already rented
        val property = _properties.value.find { it.propertyId == propertyId }
        if (property == null) {
            return Result.failure(IllegalArgumentException("Property not found"))
        }
        if (property.status == Constants.STATUS_RENTED) {
            return Result.failure(IllegalStateException("Property is already rented out."))
        }

        // Atomic update: Set request to Accepted AND property to RENTED
        _rentalRequests.value = _rentalRequests.value.map {
            if (it.requestId == requestId) it.copy(status = Constants.REQUEST_ACCEPTED) else it
        }
        _properties.value = _properties.value.map {
            if (it.propertyId == propertyId) it.copy(status = Constants.STATUS_RENTED) else it
        }
        return Result.success(Unit)
    }

    fun rejectRentalRequest(requestId: String): Result<Unit> {
        _rentalRequests.value = _rentalRequests.value.map {
            if (it.requestId == requestId) it.copy(status = Constants.REQUEST_REJECTED) else it
        }
        return Result.success(Unit)
    }

    // --- Wishlist Management ---
    fun toggleWishlist(tenantId: String, propertyId: String): Boolean {
        val currentMap = _wishlists.value.toMutableMap()
        val currentSet = currentMap[tenantId]?.toMutableSet() ?: mutableSetOf()
        val isNowWishlisted = if (currentSet.contains(propertyId)) {
            currentSet.remove(propertyId)
            false
        } else {
            currentSet.add(propertyId)
            true
        }
        currentMap[tenantId] = currentSet
        _wishlists.value = currentMap
        return isNowWishlisted
    }

    fun getWishlistForTenant(tenantId: String): Set<String> {
        return _wishlists.value[tenantId] ?: emptySet()
    }
}
