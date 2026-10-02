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
import androidx.compose.material.icons.filled.FavoriteBorder
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
fun WishlistScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: TenantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val allProperties by viewModel.approvedProperties.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()

    val wishlistedProperties = allProperties.filter { wishlistIds.contains(it.propertyId) }

    Scaffold(
        topBar = {
            RentEasyTopAppBar(title = "My Wishlist (${wishlistedProperties.size})")
        },
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_TENANT,
                currentRoute = Routes.TENANT_WISHLIST,
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
            if (wishlistedProperties.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.FavoriteBorder,
                    title = "No Saved Properties",
                    description = "Properties you favorite will appear here for quick access.",
                    actionLabel = "Explore Homes",
                    onActionClick = { onNavigate(Routes.TENANT_HOME) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(wishlistedProperties, key = { it.propertyId }) { prop ->
                        val scoreResult = viewModel.getScoreForProperty(prop)
                        PropertyCard(
                            property = prop,
                            isWishlisted = true,
                            score = scoreResult.totalScore,
                            onCardClick = { onNavigateToDetail(prop.propertyId) },
                            onWishlistToggle = { viewModel.toggleWishlist(prop.propertyId) }
                        )
                    }
                }
            }
        }
    }
}
