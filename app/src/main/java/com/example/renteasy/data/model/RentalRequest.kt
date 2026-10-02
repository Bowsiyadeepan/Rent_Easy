package com.example.renteasy.data.model

import com.example.renteasy.utils.Constants
import java.util.Date

data class RentalRequest(
    val requestId: String = "",
    val tenantId: String = "",
    val tenantName: String = "",
    val tenantEmail: String = "",
    val ownerId: String = "",
    val propertyId: String = "",
    val propertyTitle: String = "",
    val rent: Double = 0.0,
    val status: String = Constants.REQUEST_PENDING,
    val timestamp: Date? = Date()
)
