package com.example.renteasy.ui.tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.renteasy.components.EmptyStateView
import com.example.renteasy.components.PropertyCard
import com.example.renteasy.components.RentEasyBottomBar
import com.example.renteasy.components.RentEasySearchBar
import com.example.renteasy.navigation.Routes
import com.example.renteasy.ui.theme.CardBorder
import com.example.renteasy.ui.theme.DeepBlue
import com.example.renteasy.ui.theme.PrimaryBlue
import com.example.renteasy.ui.theme.PrimaryDarkBlue
import com.example.renteasy.ui.theme.PrimaryLightBlue
import com.example.renteasy.ui.theme.SurfaceWhite
import com.example.renteasy.ui.theme.TealSecondary
import com.example.renteasy.ui.theme.TextPrimary
import com.example.renteasy.ui.theme.TextSecondary
import com.example.renteasy.utils.Constants
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantHomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: TenantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val properties by viewModel.filteredProperties.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val comparisonIds by viewModel.comparisonIds.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedPropertyType.collectAsState()
    val selectedBeds by viewModel.selectedBedrooms.collectAsState()
    val minRent by viewModel.minRent.collectAsState()
    val maxRent by viewModel.maxRent.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    Scaffold(
        bottomBar = {
            RentEasyBottomBar(
                role = Constants.ROLE_TENANT,
                currentRoute = Routes.TENANT_HOME,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            if (comparisonIds.size >= 2) {
                FloatingActionButton(
                    onClick = onNavigateToCompare,
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Compare (${comparisonIds.size})",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
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
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentUser != null) "Welcome back, ${currentUser?.name?.substringBefore(" ")} 👋" else "Find Your Dream Home 🏡",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Smart Decision Support with Transparent Pricing",
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
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Embedded Search Bar
                    RentEasySearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.searchQuery.value = it },
                        onFilterClick = { showFilterSheet = true },
                        isFilterActive = selectedType != "All" || selectedBeds > 0 || minRent > 0 || maxRent < 100000.0
                    )
                }
            }

            // Quick Type Filter Chips Row
            Surface(
                color = SurfaceWhite,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    items(Constants.PROPERTY_TYPES) { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                .clickable { viewModel.selectedPropertyType.value = type }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color.White else TextPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Results Counter & Clear Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${properties.size} Verified Properties",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (selectedType != "All" || selectedBeds > 0 || minRent > 0 || maxRent < 100000.0) {
                    Text(
                        text = "Reset Filters ✕",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.selectedPropertyType.value = "All"
                            viewModel.selectedBedrooms.value = 0
                            viewModel.minRent.value = 0.0
                            viewModel.maxRent.value = 100000.0
                        }
                    )
                }
            }

            // Property Listings
            if (properties.isEmpty()) {
                EmptyStateView(
                    title = "No Properties Match",
                    description = "Try adjusting your price range or bedroom filters to see more results."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 85.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(properties, key = { it.propertyId }) { prop ->
                        val isFav = wishlistIds.contains(prop.propertyId)
                        val scoreResult = viewModel.getScoreForProperty(prop)

                        PropertyCard(
                            property = prop,
                            isWishlisted = isFav,
                            score = scoreResult.totalScore,
                            onCardClick = { onNavigateToDetail(prop.propertyId) },
                            onWishlistToggle = { viewModel.toggleWishlist(prop.propertyId) }
                        )
                    }
                }
            }
        }
    }

    // Filter Modal Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Criteria",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rent Range
                Text(
                    text = "Monthly Rent Budget",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${currencyFormatter.format(minRent)} - ${currencyFormatter.format(maxRent)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )

                RangeSlider(
                    value = minRent.toFloat()..maxRent.toFloat(),
                    onValueChange = { range ->
                        viewModel.minRent.value = range.start.toDouble()
                        viewModel.maxRent.value = range.endInclusive.toDouble()
                    },
                    valueRange = 0f..100000f,
                    steps = 20,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlue,
                        activeTrackColor = PrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bedrooms Selector
                Text(
                    text = "Bedrooms Required",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0 to "Any", 1 to "1 BHK", 2 to "2 BHK", 3 to "3 BHK", 4 to "4+ BHK").forEach { (bedCount, label) ->
                        val isSelected = selectedBeds == bedCount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                .clickable { viewModel.selectedBedrooms.value = bedCount }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Show Results", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}
