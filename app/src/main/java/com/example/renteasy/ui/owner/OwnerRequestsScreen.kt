package com.example.renteasy.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.renteasy.components.EmptyStateView
import com.example.renteasy.components.RentEasyBottomBar
import com.example.renteasy.components.RentEasyTopAppBar
import com.example.renteasy.components.RentalRequestCard
import com.example.renteasy.navigation.Routes
import com.example.renteasy.utils.Constants

@Composable
fun OwnerRequestsScreen(
    onNavigate: (String) -> Unit,
    viewModel: OwnerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val receivedRequests by viewModel.receivedRequests.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    Scaffold(
        topBar = {
            RentEasyTopAppBar(title = "Received Rental Requests (${receivedRequests.size})")
        },
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_OWNER,
                currentRoute = Routes.OWNER_REQUESTS,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (receivedRequests.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ListAlt,
                    title = "No Rental Requests Yet",
                    description = "When tenants apply for your listings, applications will show up here."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(receivedRequests, key = { it.requestId }) { request ->
                        val tenantUser = allUsers.find { it.uid == request.tenantId }

                        RentalRequestCard(
                            request = request,
                            isOwnerView = true,
                            tenantUser = tenantUser,
                            onAcceptClick = { viewModel.acceptRequest(request.requestId, request.propertyId) },
                            onRejectClick = { viewModel.rejectRequest(request.requestId) }
                        )
                    }
                }
            }
        }
    }
}
