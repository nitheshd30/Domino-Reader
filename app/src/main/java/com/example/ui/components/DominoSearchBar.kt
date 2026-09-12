package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DominoCyan

@Composable
fun DominoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedBrand: String?,
    onBrandSelect: (String?) -> Unit,
    selectedWeight: String?,
    onWeightSelect: (String?) -> Unit,
    placeholderText: String = "Search label name, batch, weight, MRP...",
    showFilterChips: Boolean = true,
    activePrinterName: String? = null,
    availableBrands: List<String> = emptyList(),
    availableWeights: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val hasChips = availableBrands.isNotEmpty() || availableWeights.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("domino_search_bar_container")
    ) {
        // Search Input Field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_input"),
            placeholder = {
                Text(
                    text = placeholderText,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            singleLine = true
        )

        // Active scope badge if specific printer is selected
        if (activePrinterName != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DominoCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Scope: $activePrinterName",
                        color = DominoCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Quick filter chips (Brands & Weights) - only shown when relevant data exists
        if (showFilterChips && hasChips) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All Reset chip
                FilterChip(
                    selected = selectedBrand == null && selectedWeight == null,
                    onClick = {
                        onBrandSelect(null)
                        onWeightSelect(null)
                    },
                    label = { Text("All", fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_all"),
                    shape = RoundedCornerShape(8.dp)
                )

                // Brands
                availableBrands.forEach { brand ->
                    FilterChip(
                        selected = selectedBrand.equals(brand, ignoreCase = true),
                        onClick = {
                            onBrandSelect(if (selectedBrand.equals(brand, ignoreCase = true)) null else brand)
                        },
                        label = { Text(brand, fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_brand_$brand"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Weights
                availableWeights.forEach { weight ->
                    FilterChip(
                        selected = selectedWeight.equals(weight, ignoreCase = true),
                        onClick = {
                            onWeightSelect(if (selectedWeight.equals(weight, ignoreCase = true)) null else weight)
                        },
                        label = { Text(weight, fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_weight_$weight"),
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DominoCyan.copy(alpha = 0.25f),
                            selectedLabelColor = DominoCyan
                        )
                    )
                }
            }
        }
    }
}
