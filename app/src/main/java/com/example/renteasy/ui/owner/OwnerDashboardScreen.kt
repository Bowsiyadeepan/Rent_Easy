package com.example.renteasy.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.renteasy.components.EmptyStateView
import com.example.renteasy.components.PropertyCard
import com.example.renteasy.components.RentEasyBottomBar
import com.example.renteasy.data.model.Property
import com.example.renteasy.navigation.Routes
import com.example.renteasy.ui.theme.CardBorder
import com.example.renteasy.ui.theme.DeepBlue
import com.example.renteasy.ui.theme.PrimaryBlue
import com.example.renteasy.ui.theme.StatusApproved
import com.example.renteasy.ui.theme.StatusPending
import com.example.renteasy.ui.theme.StatusRejected
import com.example.renteasy.ui.theme.StatusRented
import com.example.renteasy.ui.theme.SurfaceWhite
import com.example.renteasy.ui.theme.TextPrimary
import com.example.renteasy.ui.theme.TextSecondary
import com.example.renteasy.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    onNavigateToAddProperty: () -> Unit,
    onNavigateToEditProperty: (String) -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: OwnerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val myProperties by viewModel.myProperties.collectAsState()
    val receivedRequests by viewModel.receivedRequests.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var propertyToDelete by remember { mutableStateOf<Property?>(null) }

    val pendingRequestsCount = receivedRequests.count { it.status.equals(Constants.REQUEST_PENDING, ignoreCase = true) }
    val approvedCount = myProperties.count { it.status.equals(Constants.STATUS_APPROVED, ignoreCase = true) }
    val pendingCount = myProperties.count { it.status.equals(Constants.STATUS_PENDING, ignoreCase = true) }
    val rentedCount = myProperties.count { it.status.equals(Constants.STATUS_RENTED, ignoreCase = true) }

    Scaffold(
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_OWNER,
                currentRoute = Routes.OWNER_DASHBOARD,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(8.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Listing", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Hero Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Owner Dashboard 🏢",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Manage your listings & tenant applications",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Apartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }

            // Metrics Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModernMetricItem(label = "Total", count = myProperties.size, color = PrimaryBlue, modifier = Modifier.weight(1f))
                ModernMetricItem(label = "Active", count = approvedCount, color = StatusApproved, modifier = Modifier.weight(1f))
                ModernMetricItem(label = "Pending", count = pendingCount, color = StatusPending, modifier = Modifier.weight(1f))
                ModernMetricItem(label = "Rented", count = rentedCount, color = StatusRented, modifier = Modifier.weight(1f))
            }

            // Pending Applications Banner
            if (pendingRequestsCount > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimaryBlue.copy(alpha = 0.25f)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = "$pendingRequestsCount Pending Applications",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = "Tenants awaiting your decision",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onNavigateToRequests,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Review", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Listings List
            if (myProperties.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Home,
                    title = "No Properties Listed Yet",
                    description = "Publish your first property listing to begin receiving tenant applications.",
                    actionLabel = "Add Property Now",
                    onActionClick = onNavigateToAddProperty
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 85.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myProperties, key = { it.propertyId }) { prop ->
                        Column {
                            PropertyCard(
                                property = prop,
                                showStatus = true,
                                onCardClick = { onNavigateToEditProperty(prop.propertyId) }
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, end = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onNavigateToEditProperty(prop.propertyId) }) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Listing", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                                }

                                TextButton(onClick = { propertyToDelete = prop }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusRejected)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", color = StatusRejected, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (propertyToDelete != null) {
        AlertDialog(
            onDismissRequest = { propertyToDelete = null },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to delete '${propertyToDelete?.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        propertyToDelete?.let { viewModel.deleteProperty(it.propertyId) }
                        propertyToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = StatusRejected)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ModernMetricItem(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$count", style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp), fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}
