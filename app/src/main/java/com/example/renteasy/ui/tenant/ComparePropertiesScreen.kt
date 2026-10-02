package com.example.renteasy.ui.tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.renteasy.components.EmptyStateView
import com.example.renteasy.components.RentEasyBottomBar
import com.example.renteasy.components.RentEasyTopAppBar
import com.example.renteasy.data.model.Property
import com.example.renteasy.navigation.Routes
import com.example.renteasy.ui.theme.CardBorder
import com.example.renteasy.ui.theme.PrimaryBlue
import com.example.renteasy.ui.theme.ScoreHigh
import com.example.renteasy.ui.theme.ScoreLow
import com.example.renteasy.ui.theme.ScoreMedium
import com.example.renteasy.ui.theme.StatusApproved
import com.example.renteasy.ui.theme.SurfaceWhite
import com.example.renteasy.ui.theme.TealSecondary
import com.example.renteasy.ui.theme.TextPrimary
import com.example.renteasy.ui.theme.TextSecondary
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.TotalCostEstimator
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ComparePropertiesScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: TenantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val allProperties by viewModel.approvedProperties.collectAsState()
    val comparisonIds by viewModel.comparisonIds.collectAsState()

    val comparedProperties = if (comparisonIds.isNotEmpty()) {
        allProperties.filter { comparisonIds.contains(it.propertyId) }
    } else {
        allProperties.take(2) // Default to first 2 properties if none explicitly selected
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            RentEasyTopAppBar(
                title = "Side-by-Side Comparison",
                actions = {
                    if (comparisonIds.isNotEmpty()) {
                        Text(
                            text = "Clear (${comparisonIds.size})",
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_TENANT,
                currentRoute = Routes.TENANT_COMPARE,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        if (allProperties.size < 2) {
            EmptyStateView(
                icon = Icons.Default.CompareArrows,
                title = "Not Enough Properties",
                description = "At least 2 approved properties are required for comparison."
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Horizontal Comparison Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                comparedProperties.forEach { property ->
                    val breakdown = TotalCostEstimator.calculateBreakdown(property)
                    val scoreResult = viewModel.getScoreForProperty(property)

                    Card(
                        modifier = Modifier.width(260.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header with title and remove button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = property.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = property.city,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                if (comparisonIds.contains(property.propertyId)) {
                                    IconButton(
                                        onClick = { viewModel.toggleCompare(property.propertyId) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Smart Rental Score Badge
                            val scoreColor = when {
                                scoreResult.totalScore >= 75 -> ScoreHigh
                                scoreResult.totalScore >= 50 -> ScoreMedium
                                else -> ScoreLow
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(scoreColor.copy(alpha = 0.12f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Smart Score: ${scoreResult.totalScore}/100",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = scoreColor
                                    )
                                    Text(
                                        text = scoreResult.explanation,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                            // Cost Breakdown Metrics
                            ComparisonRow(label = "Monthly Rent", value = currencyFormatter.format(property.rent), isPrimary = true)
                            ComparisonRow(label = "Security Deposit", value = currencyFormatter.format(property.deposit))
                            ComparisonRow(label = "Utilities", value = currencyFormatter.format(property.estimatedUtilityCost))
                            ComparisonRow(label = "Maintenance", value = currencyFormatter.format(property.estimatedMaintenanceCost))
                            ComparisonRow(label = "Total Monthly Cost", value = currencyFormatter.format(breakdown.totalMonthlyCost), isBold = true)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                            // Specifications
                            ComparisonRow(label = "Configuration", value = "${property.bedrooms} BHK / ${property.bathrooms} Baths")
                            ComparisonRow(label = "Property Type", value = property.type)
                            ComparisonRow(label = "Amenities Count", value = "${property.amenities.size} Amenities")

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                            // Amenities List
                            Text(
                                text = "Key Amenities:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Constants.AVAILABLE_AMENITIES.take(6).forEach { amenity ->
                                val hasAmenity = property.amenities.any { it.contains(amenity, ignoreCase = true) || amenity.contains(it, ignoreCase = true) }
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (hasAmenity) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (hasAmenity) StatusApproved else Color.Gray.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = amenity,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (hasAmenity) TextPrimary else TextSecondary.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { onNavigateToDetail(property.propertyId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("View Details")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(label: String, value: String, isPrimary: Boolean = false, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold || isPrimary) FontWeight.Bold else FontWeight.Medium,
            color = if (isPrimary) PrimaryBlue else TextPrimary
        )
    }
}
