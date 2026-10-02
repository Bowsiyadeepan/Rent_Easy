package com.example.renteasy.ui.tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.renteasy.components.LoadingView
import com.example.renteasy.components.StatusChip
import com.example.renteasy.data.model.Property
import com.example.renteasy.ui.theme.CardBorder
import com.example.renteasy.ui.theme.DeepBlue
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCompare: () -> Unit,
    viewModel: TenantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val properties by viewModel.approvedProperties.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val comparisonIds by viewModel.comparisonIds.collectAsState()
    val myRequests by viewModel.myRequests.collectAsState()
    val requestState by viewModel.requestState.collectAsState()

    val property = properties.find { it.propertyId == propertyId }
    val isWishlisted = wishlistIds.contains(propertyId)
    val isCompared = comparisonIds.contains(propertyId)

    val existingPendingRequest = myRequests.find {
        it.propertyId == propertyId && it.status.equals(Constants.REQUEST_PENDING, ignoreCase = true)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(requestState) {
        when (requestState) {
            is RequestSubmissionState.Success -> {
                showSuccessDialog = true
                viewModel.clearRequestState()
            }
            is RequestSubmissionState.Error -> {
                val msg = (requestState as RequestSubmissionState.Error).message
                snackbarHostState.showSnackbar(msg)
                viewModel.clearRequestState()
            }
            else -> {}
        }
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    if (property == null) {
        LoadingView(message = "Loading property details...")
        return
    }

    val costBreakdown = TotalCostEstimator.calculateBreakdown(property)
    val scoreResult = viewModel.getScoreForProperty(property)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(property.title, maxLines = 1, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleCompare(property.propertyId) }) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = "Compare",
                            tint = if (isCompared) PrimaryBlue else TextSecondary
                        )
                    }
                    IconButton(onClick = { viewModel.toggleWishlist(property.propertyId) }) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFE11D48) else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currencyFormatter.format(property.rent)} /mo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "Deposit: ${currencyFormatter.format(property.deposit)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    if (existingPendingRequest != null) {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFE2E8F0))
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request Pending", color = TextSecondary)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.submitRentalRequest(property) },
                            enabled = requestState !is RequestSubmissionState.Loading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (requestState is RequestSubmissionState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send Rental Request", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Gallery
            if (property.imageUrls.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(property.imageUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = property.title,
                            modifier = Modifier
                                .size(width = 300.dp, height = 200.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(DeepBlue.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(property.type, style = MaterialTheme.typography.titleLarge, color = PrimaryBlue)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Title and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(status = property.status)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Address & City
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${property.address}, ${property.city}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Spec Pills (Bedrooms, Bathrooms, Type)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecPill(icon = Icons.Default.Bed, label = "${property.bedrooms} Bedrooms")
                    SpecPill(icon = Icons.Default.Bathtub, label = "${property.bathrooms} Bathrooms")
                    SpecPill(icon = Icons.Default.Info, label = property.type)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SMART RENTAL SCORE CARD (Progress Ring + Explainability)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Smart Rental Score",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            val scoreColor = when {
                                scoreResult.totalScore >= 75 -> ScoreHigh
                                scoreResult.totalScore >= 50 -> ScoreMedium
                                else -> ScoreLow
                            }

                            // Score Ring / Circle Badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(scoreColor.copy(alpha = 0.15f))
                                    .border(2.5.dp, scoreColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${scoreResult.totalScore}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = scoreColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // One-line Explainability
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlue.copy(alpha = 0.08f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 ${scoreResult.explanation}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Score Breakdown Factors
                        Text(
                            text = "Weighted Evaluation Factors:",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ScoreFactorRow(label = "Affordability (vs City Avg)", score = scoreResult.affordabilityScore, max = 35)
                        ScoreFactorRow(label = "Space & Room Value", score = scoreResult.spaceScore, max = 20)
                        ScoreFactorRow(label = "Amenities Completeness", score = scoreResult.amenitiesScore, max = 20)
                        ScoreFactorRow(label = "Deposit Friendliness", score = scoreResult.depositScore, max = 10)
                        ScoreFactorRow(label = "Hidden Cost Ratio", score = scoreResult.hiddenCostScore, max = 15)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // TOTAL COST ESTIMATOR CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Total Cost Estimator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CostRow(label = "Base Monthly Rent", amount = currencyFormatter.format(costBreakdown.monthlyRent))
                        CostRow(label = "Estimated Utilities (Electricity, Gas, Water)", amount = currencyFormatter.format(costBreakdown.estimatedUtilityCost))
                        CostRow(label = "Estimated Maintenance & Society Dues", amount = currencyFormatter.format(costBreakdown.estimatedMaintenanceCost))

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                        CostRow(
                            label = "Estimated Total Monthly Outlay",
                            amount = currencyFormatter.format(costBreakdown.totalMonthlyCost),
                            isBold = true,
                            amountColor = PrimaryBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CostRow(label = "Refundable Security Deposit", amount = currencyFormatter.format(costBreakdown.securityDeposit))
                        CostRow(
                            label = "First Month Total Cash Outlay",
                            amount = currencyFormatter.format(costBreakdown.firstMonthTotalOutlay),
                            isBold = true
                        )
                        CostRow(
                            label = "Annual Estimated Expenditure",
                            amount = currencyFormatter.format(costBreakdown.annualEstimatedCost),
                            isSecondary = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Amenities Section
                if (property.amenities.isNotEmpty()) {
                    Text(
                        text = "Amenities (${property.amenities.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        property.amenities.forEach { amenity ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceWhite)
                                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = TealSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = amenity,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Description
                Text(
                    text = "About this Home",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Rental Request Submitted!") },
            text = {
                Text("Your application has been sent to the property owner. You can track status in 'My Requests'. Owner contact details will be revealed once approved.")
            },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }
}

@Composable
private fun SpecPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}

@Composable
private fun ScoreFactorRow(label: String, score: Double, max: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(
            text = "${String.format(Locale.US, "%.1f", score)} / $max pts",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun CostRow(
    label: String,
    amount: String,
    isBold: Boolean = false,
    isSecondary: Boolean = false,
    amountColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isSecondary) TextSecondary else TextPrimary
        )
        Text(
            text = amount,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = amountColor
        )
    }
}
