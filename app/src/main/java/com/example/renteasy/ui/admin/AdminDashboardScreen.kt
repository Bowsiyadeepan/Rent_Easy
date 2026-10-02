package com.example.renteasy.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.renteasy.components.RentEasyBottomBar
import com.example.renteasy.navigation.Routes
import com.example.renteasy.ui.theme.CardBorder
import com.example.renteasy.ui.theme.DeepBlue
import com.example.renteasy.ui.theme.PrimaryBlue
import com.example.renteasy.ui.theme.StatusApproved
import com.example.renteasy.ui.theme.StatusPending
import com.example.renteasy.ui.theme.SurfaceWhite
import com.example.renteasy.ui.theme.TealSecondary
import com.example.renteasy.ui.theme.TextPrimary
import com.example.renteasy.ui.theme.TextSecondary
import com.example.renteasy.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val pendingProps by viewModel.pendingProperties.collectAsState()

    Scaffold(
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_ADMIN,
                currentRoute = Routes.ADMIN_DASHBOARD,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Modern Hero Header
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
                            text = "Admin Control Center 🛡️",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Platform Analytics & Moderation Hub",
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
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Pending Moderation Alert Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Routes.ADMIN_APPROVALS) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (stats.pendingApprovals > 0) StatusPending.copy(alpha = 0.12f) else SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (stats.pendingApprovals > 0) StatusPending else CardBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (stats.pendingApprovals > 0) StatusPending.copy(alpha = 0.2f) else TealSecondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (stats.pendingApprovals > 0) StatusPending else TealSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${stats.pendingApprovals} Pending Approvals",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (stats.pendingApprovals > 0) "Moderation action required" else "All listings reviewed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigate(Routes.ADMIN_APPROVALS) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (stats.pendingApprovals > 0) StatusPending else PrimaryBlue)
                        ) {
                            Text("Review", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "System Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2x2 Grid of Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        title = "Registered Users",
                        value = "${stats.totalUsers}",
                        subtitle = "${stats.totalTenants} Tenants • ${stats.totalOwners} Owners",
                        icon = Icons.Default.People,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Routes.ADMIN_USERS) }
                    )

                    AdminMetricCard(
                        title = "All Properties",
                        value = "${stats.totalProperties}",
                        subtitle = "${stats.approvedProperties} Approved Listings",
                        icon = Icons.Default.Apartment,
                        color = TealSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Routes.ADMIN_PROPERTIES) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        title = "Rental Requests",
                        value = "${stats.totalRequests}",
                        subtitle = "Applications submitted",
                        icon = Icons.Default.ListAlt,
                        color = DeepBlue,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Routes.ADMIN_REQUESTS) }
                    )

                    AdminMetricCard(
                        title = "Moderations",
                        value = "${stats.pendingApprovals}",
                        subtitle = "Awaiting decision",
                        icon = Icons.Default.CheckCircle,
                        color = StatusPending,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Routes.ADMIN_APPROVALS) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TextSecondary, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(17.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
