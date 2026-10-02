package com.example.renteasy.ui.owner

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.renteasy.data.model.Property
import com.example.renteasy.data.model.RentalRequest
import com.example.renteasy.data.model.User
import com.example.renteasy.data.repository.AuthRepository
import com.example.renteasy.data.repository.PropertyRepository
import com.example.renteasy.data.repository.RentalRequestRepository
import com.example.renteasy.data.repository.UserRepository
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed class PropertyFormState {
    object Idle : PropertyFormState()
    object Loading : PropertyFormState()
    data class Success(val propertyId: String) : PropertyFormState()
    data class Error(val message: String) : PropertyFormState()
}

class OwnerViewModel(
    private val propertyRepository: PropertyRepository = PropertyRepository(),
    private val rentalRequestRepository: RentalRequestRepository = RentalRequestRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val storage: FirebaseStorage = try { FirebaseStorage.getInstance() } catch (e: Exception) { FirebaseStorage.getInstance() }
) : ViewModel() {

    private val tag = "OwnerViewModel"

    val currentUser: StateFlow<User?> = authRepository.currentUser

    // Owner's properties
    val myProperties: StateFlow<List<Property>> = combine(
        currentUser,
        propertyRepository.getAllProperties()
    ) { user, allProps ->
        val uid = user?.uid ?: Constants.DEMO_OWNER_UID
        allProps.filter { it.ownerId == uid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Received requests for owner's properties
    val receivedRequests: StateFlow<List<RentalRequest>> = combine(
        currentUser,
        rentalRequestRepository.getAllRequests()
    ) { user, allReqs ->
        val uid = user?.uid ?: Constants.DEMO_OWNER_UID
        allReqs.filter { it.ownerId == uid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formState = MutableStateFlow<PropertyFormState>(PropertyFormState.Idle)
    val formState: StateFlow<PropertyFormState> = _formState.asStateFlow()

    fun saveProperty(
        existingId: String?,
        title: String,
        rent: Double,
        deposit: Double,
        address: String,
        city: String,
        bedrooms: Int,
        bathrooms: Int,
        description: String,
        type: String,
        amenities: List<String>,
        estimatedUtilityCost: Double,
        estimatedMaintenanceCost: Double,
        selectedImageUris: List<Uri>,
        existingImageUrls: List<String>
    ) {
        if (title.isBlank() || address.isBlank() || city.isBlank()) {
            _formState.value = PropertyFormState.Error("Please fill in title, address, and city.")
            return
        }
        if (rent <= 0) {
            _formState.value = PropertyFormState.Error("Monthly rent must be greater than 0.")
            return
        }

        val ownerId = currentUser.value?.uid ?: Constants.DEMO_OWNER_UID

        viewModelScope.launch {
            _formState.value = PropertyFormState.Loading
            try {
                // Upload images if any, fallback to local uri strings in demo mode
                val uploadedUrls = mutableListOf<String>()
                uploadedUrls.addAll(existingImageUrls)

                for (uri in selectedImageUris) {
                    try {
                        val ref = storage.reference.child("properties/${UUID.randomUUID()}.jpg")
                        ref.putFile(uri).await()
                        val downloadUrl = ref.downloadUrl.await().toString()
                        uploadedUrls.add(downloadUrl)
                    } catch (e: Exception) {
                        RentEasyLog.w(tag, "Firebase image upload failed/offline, using uri directly: ${e.localizedMessage}")
                        uploadedUrls.add(uri.toString())
                    }
                }

                // If no images provided, supply a high-quality default
                if (uploadedUrls.isEmpty()) {
                    uploadedUrls.add("https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80")
                }

                val property = Property(
                    propertyId = existingId ?: "",
                    ownerId = ownerId,
                    title = title.trim(),
                    rent = rent,
                    deposit = deposit,
                    address = address.trim(),
                    city = city.trim(),
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    description = description.trim(),
                    type = type,
                    status = Constants.STATUS_PENDING, // New/edited properties go to PENDING for admin review
                    imageUrls = uploadedUrls,
                    amenities = amenities,
                    estimatedUtilityCost = estimatedUtilityCost,
                    estimatedMaintenanceCost = estimatedMaintenanceCost
                )

                if (existingId.isNullOrBlank()) {
                    val result = propertyRepository.addProperty(property)
                    if (result.isSuccess) {
                        _formState.value = PropertyFormState.Success(result.getOrThrow())
                    } else {
                        _formState.value = PropertyFormState.Error(result.exceptionOrNull()?.localizedMessage ?: "Failed to add property.")
                    }
                } else {
                    val result = propertyRepository.updateProperty(property)
                    if (result.isSuccess) {
                        _formState.value = PropertyFormState.Success(existingId)
                    } else {
                        _formState.value = PropertyFormState.Error(result.exceptionOrNull()?.localizedMessage ?: "Failed to update property.")
                    }
                }
            } catch (e: Exception) {
                _formState.value = PropertyFormState.Error(e.localizedMessage ?: "Failed to save property.")
            }
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.deleteProperty(propertyId)
        }
    }

    fun acceptRequest(requestId: String, propertyId: String) {
        viewModelScope.launch {
            rentalRequestRepository.acceptRentalRequest(requestId, propertyId)
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            rentalRequestRepository.rejectRentalRequest(requestId)
        }
    }

    fun clearFormState() {
        _formState.value = PropertyFormState.Idle
    }
}
