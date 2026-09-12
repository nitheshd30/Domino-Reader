package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConsumableCategory
import com.example.data.model.ConsumableItem
import com.example.ui.theme.DominoAmber
import com.example.ui.theme.DominoCyan
import com.example.ui.theme.DominoGreen
import com.example.ui.theme.DominoRed

@Composable
fun ConsumableCard(
    item: ConsumableItem,
    onIncreaseStock: () -> Unit,
    onDecreaseStock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (item.categoryEnum) {
        ConsumableCategory.INK -> DominoCyan
        ConsumableCategory.MAKE_UP -> Color(0xFFA855F7) // Purple
        ConsumableCategory.WASH -> Color(0xFF06B6D4) // Cyan / Teal
        ConsumableCategory.FILTER -> DominoAmber // Amber
        ConsumableCategory.ITM -> DominoGreen // Emerald Green
    }

    val categoryIcon = when (item.categoryEnum) {
        ConsumableCategory.INK -> Icons.Default.WaterDrop
        ConsumableCategory.MAKE_UP -> Icons.Default.Science
        ConsumableCategory.WASH -> Icons.Default.CleaningServices
        ConsumableCategory.FILTER -> Icons.Default.FilterAlt
        ConsumableCategory.ITM -> Icons.Default.BuildCircle
    }

    val statusContainerColor = when {
        item.isOutOfStock -> DominoRed.copy(alpha = 0.15f)
        item.isLowStock -> DominoAmber.copy(alpha = 0.15f)
        else -> DominoGreen.copy(alpha = 0.12f)
    }

    val statusContentColor = when {
        item.isOutOfStock -> DominoRed
        item.isLowStock -> DominoAmber
        else -> DominoGreen
    }

    val statusText = when {
        item.isOutOfStock -> "OUT OF STOCK"
        item.isLowStock -> "LOW STOCK"
        else -> "IN STOCK"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("consumable_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(
            width = if (item.isLowStock || item.isOutOfStock) 1.5.dp else 1.dp,
            color = if (item.isOutOfStock) DominoRed.copy(alpha = 0.6f)
            else if (item.isLowStock) DominoAmber.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Category Badge + Status Badge + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = categoryColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = item.categoryEnum.displayName,
                            modifier = Modifier.size(14.dp),
                            tint = categoryColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.categoryEnum.displayName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusContainerColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.isOutOfStock || item.isLowStock) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = statusContentColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusContentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_edit_consumable_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Consumable",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_delete_consumable_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Consumable",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Item Name & Description
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Part Number & Lot / Batch Number badges
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.partNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "P/N: ${item.partNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (item.batchLotNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "LOT: ${item.batchLotNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Counter and Quick Adjust Controls
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${item.quantity}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = if (item.isOutOfStock) DominoRed else MaterialTheme.colorScheme.onSurface,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.unit,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        Text(
                            text = "Min reorder threshold: ${item.minimumThreshold} ${item.unit}",
                            fontSize = 11.sp,
                            color = if (item.isLowStock || item.isOutOfStock) DominoAmber else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // Stepper buttons (+ and - with 48dp touch targets)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            onClick = onDecreaseStock,
                            enabled = item.quantity > 0,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("btn_stock_decrease_${item.id}"),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Stock",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        FilledIconButton(
                            onClick = onIncreaseStock,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("btn_stock_increase_${item.id}"),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = categoryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Stock",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Specs Row: Rack Location, Expiry/Service Life, Compatible Printers
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (item.locationRack.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Storage Location",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Storage: ${item.locationRack}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (item.expiryDate.isNotBlank() || item.serviceLifeOrHours.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Shelf Life / Service Life",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val lifeText = buildString {
                            if (item.expiryDate.isNotBlank()) append("Exp: ${item.expiryDate}")
                            if (item.expiryDate.isNotBlank() && item.serviceLifeOrHours.isNotBlank()) append(" • ")
                            if (item.serviceLifeOrHours.isNotBlank()) append(item.serviceLifeOrHours)
                        }
                        Text(
                            text = lifeText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (item.compatiblePrinters.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Compatible Printers",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Fits: ${item.compatiblePrinters}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (item.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.notes,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
