package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConsumableCategory
import com.example.data.model.ConsumableItem
import com.example.data.model.ConsumableSortOption
import com.example.ui.components.ConsumableCard
import com.example.ui.theme.DominoAmber
import com.example.ui.theme.DominoCyan
import com.example.ui.theme.DominoGreen
import com.example.ui.theme.DominoRed

@Composable
fun ConsumablesScreen(
    consumables: List<ConsumableItem>,
    allConsumables: List<ConsumableItem>,
    selectedCategory: ConsumableCategory?,
    onCategorySelect: (ConsumableCategory?) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isLowStockOnly: Boolean,
    onToggleLowStockOnly: () -> Unit,
    sortOption: ConsumableSortOption,
    onSortOptionChange: (ConsumableSortOption) -> Unit,
    onIncreaseStock: (Long) -> Unit,
    onDecreaseStock: (Long) -> Unit,
    onAddConsumable: () -> Unit,
    onEditConsumable: (ConsumableItem) -> Unit,
    onDeleteConsumable: (ConsumableItem) -> Unit,
    onRestoreDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemToDelete by remember { mutableStateOf<ConsumableItem?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    // Dashboard Statistics Calculation
    val totalItemsCount = allConsumables.size
    val lowStockCount = allConsumables.count { it.isLowStock }
    val outOfStockCount = allConsumables.count { it.isOutOfStock }
    val totalAlertCount = lowStockCount + outOfStockCount

    val inkCount = allConsumables.count { it.categoryEnum == ConsumableCategory.INK }
    val makeupCount = allConsumables.count { it.categoryEnum == ConsumableCategory.MAKE_UP }
    val washCount = allConsumables.count { it.categoryEnum == ConsumableCategory.WASH }
    val filterCount = allConsumables.count { it.categoryEnum == ConsumableCategory.FILTER }
    val itmCount = allConsumables.count { it.categoryEnum == ConsumableCategory.ITM }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("consumables_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dashboard Summary Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Stock Items Card
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "TOTAL SKUs",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalItemsCount",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ink, Wash, Make-up, ITM",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }

                    // Low Stock Alert Card (Clickable to toggle filter)
                    OutlinedCard(
                        onClick = onToggleLowStockOnly,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_low_stock_filter"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isLowStockOnly) DominoAmber.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isLowStockOnly) 1.5.dp else 1.dp,
                            color = if (totalAlertCount > 0) DominoAmber else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "REORDER ALERTS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalAlertCount > 0) DominoAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (totalAlertCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = DominoAmber
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalAlertCount",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = if (totalAlertCount > 0) DominoAmber else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (outOfStockCount > 0) "$outOfStockCount Out of Stock" else if (lowStockCount > 0) "$lowStockCount Low Stock" else "All levels nominal",
                                fontSize = 10.sp,
                                color = if (outOfStockCount > 0) DominoRed else if (lowStockCount > 0) DominoAmber else DominoGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Category Filter Chips (All, Ink, Make-up, Wash, Filter, ITM)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelect(null) },
                        label = { Text("All ($totalItemsCount)") },
                        leadingIcon = {
                            Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.testTag("filter_all_consumables")
                    )

                    // Ink Chip
                    FilterChip(
                        selected = selectedCategory == ConsumableCategory.INK,
                        onClick = { onCategorySelect(ConsumableCategory.INK) },
                        label = { Text("Ink ($inkCount)") },
                        leadingIcon = {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = DominoCyan)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DominoCyan.copy(alpha = 0.2f),
                            selectedLabelColor = DominoCyan
                        ),
                        modifier = Modifier.testTag("filter_ink")
                    )

                    // Make-up Chip
                    FilterChip(
                        selected = selectedCategory == ConsumableCategory.MAKE_UP,
                        onClick = { onCategorySelect(ConsumableCategory.MAKE_UP) },
                        label = { Text("Make-up ($makeupCount)") },
                        leadingIcon = {
                            Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFA855F7))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFA855F7).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFFA855F7)
                        ),
                        modifier = Modifier.testTag("filter_makeup")
                    )

                    // Wash Chip
                    FilterChip(
                        selected = selectedCategory == ConsumableCategory.WASH,
                        onClick = { onCategorySelect(ConsumableCategory.WASH) },
                        label = { Text("Wash ($washCount)") },
                        leadingIcon = {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF06B6D4))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF06B6D4).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFF06B6D4)
                        ),
                        modifier = Modifier.testTag("filter_wash")
                    )

                    // Filter Chip
                    FilterChip(
                        selected = selectedCategory == ConsumableCategory.FILTER,
                        onClick = { onCategorySelect(ConsumableCategory.FILTER) },
                        label = { Text("Filter ($filterCount)") },
                        leadingIcon = {
                            Icon(Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = DominoAmber)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DominoAmber.copy(alpha = 0.2f),
                            selectedLabelColor = DominoAmber
                        ),
                        modifier = Modifier.testTag("filter_filter")
                    )

                    // ITM Chip
                    FilterChip(
                        selected = selectedCategory == ConsumableCategory.ITM,
                        onClick = { onCategorySelect(ConsumableCategory.ITM) },
                        label = { Text("ITM ($itmCount)") },
                        leadingIcon = {
                            Icon(Icons.Default.BuildCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = DominoGreen)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DominoGreen.copy(alpha = 0.2f),
                            selectedLabelColor = DominoGreen
                        ),
                        modifier = Modifier.testTag("filter_itm")
                    )
                }
            }

            // Search & Sort Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("consumable_search_input"),
                        placeholder = { Text("Search ink, make-up, wash, filter, ITM...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Sort Menu Button
                    Box {
                        OutlinedButton(
                            onClick = { sortMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                            modifier = Modifier.testTag("btn_sort_consumables")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort Options", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(sortOption.displayName, fontSize = 11.sp, maxLines = 1)
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            ConsumableSortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        onSortOptionChange(option)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Results count banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STOCK INVENTORY (${consumables.size} ITEMS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    if (isLowStockOnly) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DominoAmber.copy(alpha = 0.15f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filtered: Low Stock Only",
                                    fontSize = 11.sp,
                                    color = DominoAmber,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = onToggleLowStockOnly,
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear low stock filter", modifier = Modifier.size(12.dp), tint = DominoAmber)
                                }
                            }
                        }
                    }
                }
            }

            // Consumable Item Cards
            if (consumables.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || isLowStockOnly || selectedCategory != null)
                                    "No matching consumables found"
                                else
                                    "Consumables stock list is empty",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || isLowStockOnly || selectedCategory != null)
                                    "Try adjusting filters or clearing your search term."
                                else
                                    "Stock list is clean and ready. Add ink, make-up, wash, filter, and ITM stock items.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onAddConsumable,
                                    modifier = Modifier.testTag("btn_add_first_consumable")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Consumable")
                                }
                            }
                        }
                    }
                }
            } else {
                items(consumables, key = { it.id }) { item ->
                    ConsumableCard(
                        item = item,
                        onIncreaseStock = { onIncreaseStock(item.id) },
                        onDecreaseStock = { onDecreaseStock(item.id) },
                        onEdit = { onEditConsumable(item) },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }

        // Floating Action Button to Add New Consumable
        ExtendedFloatingActionButton(
            onClick = onAddConsumable,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Add Consumable") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_consumable"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = DominoRed
                )
            },
            title = { Text("Remove Consumable?") },
            text = {
                Text("Are you sure you want to remove '${item.name}' from your stock list? This will remove its inventory tracking record.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteConsumable(item)
                        itemToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = DominoRed
                    ),
                    modifier = Modifier.testTag("btn_confirm_delete_consumable")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
