package com.example.renteasy.ui.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.renteasy.components.ErrorBanner
import com.example.renteasy.components.RentEasyTextField
import com.example.renteasy.ui.theme.CardBorder
import com.example.renteasy.ui.theme.PrimaryBlue
import com.example.renteasy.ui.theme.SurfaceWhite
import com.example.renteasy.ui.theme.TextPrimary
import com.example.renteasy.ui.theme.TextSecondary
import com.example.renteasy.utils.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditPropertyScreen(
    propertyId: String?,
    onNavigateBack: () -> Unit,
    viewModel: OwnerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val myProperties by viewModel.myProperties.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val existingProperty = if (!propertyId.isNullOrBlank()) {
        myProperties.find { it.propertyId == propertyId }
    } else null

    var title by remember(existingProperty) { mutableStateOf(existingProperty?.title ?: "") }
    var rentText by remember(existingProperty) { mutableStateOf(existingProperty?.rent?.let { if (it > 0) it.toInt().toString() else "" } ?: "") }
    var depositText by remember(existingProperty) { mutableStateOf(existingProperty?.deposit?.let { if (it > 0) it.toInt().toString() else "" } ?: "") }
    var address by remember(existingProperty) { mutableStateOf(existingProperty?.address ?: "") }
    var city by remember(existingProperty) { mutableStateOf(existingProperty?.city ?: "Bengaluru") }
    var bedrooms by remember(existingProperty) { mutableStateOf(existingProperty?.bedrooms ?: 2) }
    var bathrooms by remember(existingProperty) { mutableStateOf(existingProperty?.bathrooms ?: 2) }
    var description by remember(existingProperty) { mutableStateOf(existingProperty?.description ?: "") }
    var selectedType by remember(existingProperty) { mutableStateOf(existingProperty?.type ?: "Apartment") }
    var selectedAmenities by remember(existingProperty) { mutableStateOf(existingProperty?.amenities ?: emptyList()) }
    var utilityCostText by remember(existingProperty) { mutableStateOf(existingProperty?.estimatedUtilityCost?.let { if (it > 0) it.toInt().toString() else "" } ?: "2000") }
    var maintenanceCostText by remember(existingProperty) { mutableStateOf(existingProperty?.estimatedMaintenanceCost?.let { if (it > 0) it.toInt().toString() else "" } ?: "1500") }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var existingImageUrls by remember(existingProperty) { mutableStateOf(existingProperty?.imageUrls ?: emptyList()) }

    var typeDropdownExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImageUris = selectedImageUris + uris
    }

    LaunchedEffect(formState) {
        if (formState is PropertyFormState.Success) {
            viewModel.clearFormState()
            onNavigateBack()
        }
    }

    val isLoading = formState is PropertyFormState.Loading
    val errorMessage = (formState as? PropertyFormState.Error)?.message

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (propertyId.isNullOrBlank()) "Add Property Listing" else "Edit Property", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .imePadding()
        ) {
            if (errorMessage != null) {
                ErrorBanner(errorMessage = errorMessage, onDismiss = { viewModel.clearFormState() })
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Image Picker Section
            Text(
                text = "Property Images",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceWhite)
                            .border(1.5.dp, PrimaryBlue, RoundedCornerShape(12.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Add Photo", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(existingImageUrls) { url ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                items(selectedImageUris) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImageUris = selectedImageUris.filterNot { it == uri } },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Basic Details
            RentEasyTextField(
                value = title,
                onValueChange = { title = it },
                label = "Property Title",
                placeholder = "e.g. Elegant 2BHK Apartment in Koramangala"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Property Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Property Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = typeDropdownExpanded,
                    onDismissRequest = { typeDropdownExpanded = false }
                ) {
                    Constants.PROPERTY_TYPES_INPUT.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedType = item
                                typeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Deposit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RentEasyTextField(
                    value = rentText,
                    onValueChange = { rentText = it },
                    label = "Monthly Rent (₹)",
                    placeholder = "25000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                RentEasyTextField(
                    value = depositText,
                    onValueChange = { depositText = it },
                    label = "Security Deposit (₹)",
                    placeholder = "50000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Address & City
            RentEasyTextField(
                value = address,
                onValueChange = { address = it },
                label = "Street Address / Locality",
                placeholder = "12th Main, 4th Block"
            )

            Spacer(modifier = Modifier.height(12.dp))

            RentEasyTextField(
                value = city,
                onValueChange = { city = it },
                label = "City",
                placeholder = "Bengaluru"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bedrooms & Bathrooms
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RentEasyTextField(
                    value = bedrooms.toString(),
                    onValueChange = { bedrooms = it.toIntOrNull() ?: 1 },
                    label = "Bedrooms",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                RentEasyTextField(
                    value = bathrooms.toString(),
                    onValueChange = { bathrooms = it.toIntOrNull() ?: 1 },
                    label = "Bathrooms",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Utility & Maintenance Estimations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RentEasyTextField(
                    value = utilityCostText,
                    onValueChange = { utilityCostText = it },
                    label = "Est. Utilities /mo (₹)",
                    placeholder = "2000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                RentEasyTextField(
                    value = maintenanceCostText,
                    onValueChange = { maintenanceCostText = it },
                    label = "Est. Maint. /mo (₹)",
                    placeholder = "1500",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amenities Selector
            Text(
                text = "Select Amenities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Constants.AVAILABLE_AMENITIES.forEach { amenity ->
                    val isChecked = selectedAmenities.contains(amenity)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isChecked) PrimaryBlue else SurfaceWhite)
                            .border(1.dp, if (isChecked) PrimaryBlue else CardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedAmenities = if (isChecked) {
                                    selectedAmenities - amenity
                                } else {
                                    selectedAmenities + amenity
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isChecked) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = amenity,
                                color = if (isChecked) Color.White else TextPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            RentEasyTextField(
                value = description,
                onValueChange = { description = it },
                label = "Property Description",
                placeholder = "Highlight key features, connectivity, society rules, etc.",
                singleLine = false,
                minLines = 3,
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save / Submit Button
            Button(
                onClick = {
                    val rent = rentText.toDoubleOrNull() ?: 0.0
                    val deposit = depositText.toDoubleOrNull() ?: (rent * 2)
                    val utility = utilityCostText.toDoubleOrNull() ?: 0.0
                    val maint = maintenanceCostText.toDoubleOrNull() ?: 0.0

                    viewModel.saveProperty(
                        existingId = existingProperty?.propertyId,
                        title = title,
                        rent = rent,
                        deposit = deposit,
                        address = address,
                        city = city,
                        bedrooms = bedrooms,
                        bathrooms = bathrooms,
                        description = description,
                        type = selectedType,
                        amenities = selectedAmenities,
                        estimatedUtilityCost = utility,
                        estimatedMaintenanceCost = maint,
                        selectedImageUris = selectedImageUris,
                        existingImageUrls = existingImageUrls
                    )
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        text = if (propertyId.isNullOrBlank()) "Submit for Approval" else "Save Changes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
