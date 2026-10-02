package com.example.renteasy.ui.admin

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
fun AdminRequestsScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val allRequests by viewModel.allRequests.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    Scaffold(
        topBar = {
            RentEasyTopAppBar(title = "All Rental Requests (${allRequests.size})")
        },
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_ADMIN,
                currentRoute = Routes.ADMIN_REQUESTS,
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
            if (allRequests.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ListAlt,
                    title = "No Requests Found",
                    description = "No rental requests have been placed yet."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allRequests, key = { it.requestId }) { req ->
                        val owner = allUsers.find { it.uid == req.ownerId }
                        val tenant = allUsers.find { it.uid == req.tenantId }

                        RentalRequestCard(
                            request = req,
                            isOwnerView = true,
                            ownerUser = owner,
                            tenantUser = tenant
                        )
                    }
                }
            }
        }
    }
}
