package com.example.renteasy.ui.tenant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.renteasy.data.model.Property
import com.example.renteasy.data.model.RentalRequest
import com.example.renteasy.data.model.User
import com.example.renteasy.data.repository.AuthRepository
import com.example.renteasy.data.repository.PropertyRepository
import com.example.renteasy.data.repository.RentalRequestRepository
import com.example.renteasy.data.repository.UserRepository
import com.example.renteasy.data.repository.WishlistRepository
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import com.example.renteasy.utils.SmartRentalScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class RequestSubmissionState {
    object Idle : RequestSubmissionState()
    object Loading : RequestSubmissionState()
    data class Success(val requestId: String) : RequestSubmissionState()
    data class Error(val message: String) : RequestSubmissionState()
}

class TenantViewModel(
    private val propertyRepository: PropertyRepository = PropertyRepository(),
    private val wishlistRepository: WishlistRepository = WishlistRepository(),
    private val rentalRequestRepository: RentalRequestRepository = RentalRequestRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val tag = "TenantViewModel"

    val currentUser: StateFlow<User?> = authRepository.currentUser

    // All approved properties
    val approvedProperties: StateFlow<List<Property>> = propertyRepository.getApprovedProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist IDs for current tenant
    val wishlistIds: StateFlow<Set<String>> = combine(
        currentUser,
        propertyRepository.getApprovedProperties()
    ) { user, _ ->
        user?.uid ?: ""
    }.combine(wishlistRepository.getWishlistPropertyIds(currentUser.value?.uid ?: Constants.DEMO_TENANT_UID)) { _, ids ->
        ids
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Tenant rental requests
    val myRequests: StateFlow<List<RentalRequest>> = combine(
        currentUser,
        rentalRequestRepository.getAllRequests()
    ) { user, allReqs ->
        val uid = user?.uid ?: Constants.DEMO_TENANT_UID
        allReqs.filter { it.tenantId == uid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All users map for resolving owner contact info
    val allUsers: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter and Search States
    var searchQuery = MutableStateFlow("")
    var selectedPropertyType = MutableStateFlow("All")
    var selectedBedrooms = MutableStateFlow(0) // 0 means Any
    var minRent = MutableStateFlow(0.0)
    var maxRent = MutableStateFlow(100000.0)

    // Selected comparison property IDs (max 3)
    private val _comparisonIds = MutableStateFlow<Set<String>>(emptySet())
    val comparisonIds: StateFlow<Set<String>> = _comparisonIds.asStateFlow()

    // Request submission state
    private val _requestState = MutableStateFlow<RequestSubmissionState>(RequestSubmissionState.Idle)
    val requestState: StateFlow<RequestSubmissionState> = _requestState.asStateFlow()

    // Filtered Properties
    val filteredProperties: StateFlow<List<Property>> = combine(
        approvedProperties,
        searchQuery,
        selectedPropertyType,
        selectedBedrooms,
        minRent
    ) { props, query, type, beds, minR ->
        props.filter { prop ->
            val matchesQuery = query.isBlank() ||
                    prop.title.contains(query, ignoreCase = true) ||
                    prop.city.contains(query, ignoreCase = true) ||
                    prop.address.contains(query, ignoreCase = true)

            val matchesType = type == "All" || prop.type.equals(type, ignoreCase = true)
            val matchesBeds = beds == 0 || prop.bedrooms == beds
            val matchesRent = prop.rent in minR..maxRent.value

            matchesQuery && matchesType && matchesBeds && matchesRent
        }
    }.combine(maxRent) { list, maxR ->
        list.filter { it.rent <= maxR }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleWishlist(propertyId: String) {
        val tenantId = currentUser.value?.uid ?: Constants.DEMO_TENANT_UID
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(tenantId, propertyId)
        }
    }

    fun toggleCompare(propertyId: String) {
        val current = _comparisonIds.value.toMutableSet()
        if (current.contains(propertyId)) {
            current.remove(propertyId)
        } else {
            if (current.size < 4) {
                current.add(propertyId)
            }
        }
        _comparisonIds.value = current
    }

    fun setComparisonIds(ids: Set<String>) {
        _comparisonIds.value = ids
    }

    fun submitRentalRequest(property: Property) {
        val user = currentUser.value ?: User(
            uid = Constants.DEMO_TENANT_UID,
            name = "Alice Tenant",
            email = Constants.DEMO_TENANT_EMAIL,
            phone = "9876543210",
            role = Constants.ROLE_TENANT
        )

        val request = RentalRequest(
            tenantId = user.uid,
            tenantName = user.name,
            tenantEmail = user.email,
            ownerId = property.ownerId,
            propertyId = property.propertyId,
            propertyTitle = property.title,
            rent = property.rent,
            status = Constants.REQUEST_PENDING
        )

        viewModelScope.launch {
            _requestState.value = RequestSubmissionState.Loading
            val result = rentalRequestRepository.createRentalRequest(request)
            if (result.isSuccess) {
                _requestState.value = RequestSubmissionState.Success(result.getOrThrow())
            } else {
                _requestState.value = RequestSubmissionState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Failed to submit rental request."
                )
            }
        }
    }

    fun clearRequestState() {
        _requestState.value = RequestSubmissionState.Idle
    }

    fun getScoreForProperty(property: Property): SmartRentalScore.ScoreResult {
        val cityProps = approvedProperties.value.filter { it.city.equals(property.city, ignoreCase = true) }
        return SmartRentalScore.calculate(property, cityProps)
    }
}
