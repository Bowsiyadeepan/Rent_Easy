package com.example.renteasy.ui.tenant

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
fun TenantRequestsScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: TenantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val requests by viewModel.myRequests.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    Scaffold(
        topBar = {
            RentEasyTopAppBar(title = "My Rental Requests (${requests.size})")
        },
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_TENANT,
                currentRoute = Routes.TENANT_REQUESTS,
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
            if (requests.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ListAlt,
                    title = "No Rental Requests Yet",
                    description = "When you send a request for a property, you can track owner response here.",
                    actionLabel = "Browse Homes",
                    onActionClick = { onNavigate(Routes.TENANT_HOME) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(requests, key = { it.requestId }) { req ->
                        val ownerUser = allUsers.find { it.uid == req.ownerId }

                        RentalRequestCard(
                            request = req,
                            isOwnerView = false,
                            ownerUser = ownerUser,
                            onPropertyClick = { onNavigateToDetail(req.propertyId) }
                        )
                    }
                }
            }
        }
    }
}
