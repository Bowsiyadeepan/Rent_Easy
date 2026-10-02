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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.renteasy.components.EmptyStateView
import com.example.renteasy.components.PropertyCard
import com.example.renteasy.components.RentEasyBottomBar
import com.example.renteasy.components.RentEasyTopAppBar
import com.example.renteasy.navigation.Routes
import com.example.renteasy.utils.Constants

@Composable
fun AdminPropertiesScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val allProperties by viewModel.allProperties.collectAsState()

    Scaffold(
        topBar = {
            RentEasyTopAppBar(title = "All Properties (${allProperties.size})")
        },
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_ADMIN,
                currentRoute = Routes.ADMIN_PROPERTIES,
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
            if (allProperties.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Apartment,
                    title = "No Properties Found",
                    description = "No properties exist in the database."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allProperties, key = { it.propertyId }) { prop ->
                        PropertyCard(
                            property = prop,
                            showStatus = true,
                            onCardClick = {}
                        )
                    }
                }
            }
        }
    }
}
