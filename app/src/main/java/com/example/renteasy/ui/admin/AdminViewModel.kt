package com.example.renteasy.ui.admin

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminStats(
    val totalUsers: Int = 0,
    val totalTenants: Int = 0,
    val totalOwners: Int = 0,
    val totalProperties: Int = 0,
    val pendingApprovals: Int = 0,
    val approvedProperties: Int = 0,
    val totalRequests: Int = 0
)

class AdminViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val propertyRepository: PropertyRepository = PropertyRepository(),
    private val rentalRequestRepository: RentalRequestRepository = RentalRequestRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser

    val allUsers: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProperties: StateFlow<List<Property>> = propertyRepository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<RentalRequest>> = rentalRequestRepository.getAllRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingProperties: StateFlow<List<Property>> = allProperties.combine(allProperties) { list, _ ->
        list.filter { it.status.equals(Constants.STATUS_PENDING, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<AdminStats> = combine(
        allUsers,
        allProperties,
        allRequests
    ) { users, props, reqs ->
        AdminStats(
            totalUsers = users.size,
            totalTenants = users.count { it.role.equals(Constants.ROLE_TENANT, ignoreCase = true) },
            totalOwners = users.count { it.role.equals(Constants.ROLE_OWNER, ignoreCase = true) },
            totalProperties = props.size,
            pendingApprovals = props.count { it.status.equals(Constants.STATUS_PENDING, ignoreCase = true) },
            approvedProperties = props.count { it.status.equals(Constants.STATUS_APPROVED, ignoreCase = true) },
            totalRequests = reqs.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    fun approveProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.updatePropertyStatus(propertyId, Constants.STATUS_APPROVED)
        }
    }

    fun rejectProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.updatePropertyStatus(propertyId, Constants.STATUS_REJECTED)
        }
    }
}
