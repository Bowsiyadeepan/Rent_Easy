package com.example.renteasy.data.model

import com.example.renteasy.utils.Constants

data class Property(
    val propertyId: String = "",
    val ownerId: String = "",
    val title: String = "",
    val rent: Double = 0.0,
    val deposit: Double = 0.0,
    val address: String = "",
    val city: String = "",
    val bedrooms: Int = 1,
    val bathrooms: Int = 1,
    val description: String = "",
    val type: String = "Apartment",
    val status: String = Constants.STATUS_PENDING,
    val imageUrls: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val estimatedUtilityCost: Double = 0.0,
    val estimatedMaintenanceCost: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
