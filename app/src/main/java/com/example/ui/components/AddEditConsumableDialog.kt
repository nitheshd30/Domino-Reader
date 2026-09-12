package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ConsumableCategory
import com.example.data.model.ConsumableItem
import com.example.ui.theme.DominoAmber
import com.example.ui.theme.DominoCyan
import com.example.ui.theme.DominoGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditConsumableDialog(
    initialItem: ConsumableItem?,
    onDismiss: () -> Unit,
    onSave: (ConsumableItem) -> Unit
) {
    BackHandler {
        onDismiss()
    }

    val isEditing = initialItem != null && initialItem.id != 0L

    var category by remember {
        mutableStateOf(initialItem?.categoryEnum ?: ConsumableCategory.INK)
    }
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var partNumber by remember { mutableStateOf(initialItem?.partNumber ?: "") }
    var batchLotNumber by remember { mutableStateOf(initialItem?.batchLotNumber ?: "") }
    var quantity by remember { mutableIntStateOf(initialItem?.quantity ?: 1) }
    var unit by remember {
        mutableStateOf(initialItem?.unit ?: category.defaultUnit)
    }
    var minimumThreshold by remember { mutableIntStateOf(initialItem?.minimumThreshold ?: 2) }
    var locationRack by remember { mutableStateOf(initialItem?.locationRack ?: "") }
    var expiryDate by remember { mutableStateOf(initialItem?.expiryDate ?: "") }
    var serviceLifeOrHours by remember { mutableStateOf(initialItem?.serviceLifeOrHours ?: "") }
    var compatiblePrinters by remember {
        mutableStateOf(initialItem?.compatiblePrinters ?: "Domino Ax Series")
    }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
    var nameError by remember { mutableStateOf(false) }

    val categoryColor = when (category) {
        ConsumableCategory.INK -> DominoCyan
        ConsumableCategory.MAKE_UP -> Color(0xFFA855F7)
        ConsumableCategory.WASH -> Color(0xFF06B6D4)
        ConsumableCategory.FILTER -> DominoAmber
        ConsumableCategory.ITM -> DominoGreen
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 580.dp)
                .fillMaxWidth()
                .testTag("add_edit_consumable_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditing) "Edit Consumable Item" else "Add Consumable to Stock",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Domino Ax Continuous Inkjet Consumables & Spares",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_consumable_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selector (Ink, Make-up, Wash, Filter, ITM)
                Text(
                    text = "SELECT CONSUMABLE TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ConsumableCategory.entries.forEach { cat ->
                        val selected = category == cat
                        val catIcon = when (cat) {
                            ConsumableCategory.INK -> Icons.Default.WaterDrop
                            ConsumableCategory.MAKE_UP -> Icons.Default.Science
                            ConsumableCategory.WASH -> Icons.Default.CleaningServices
                            ConsumableCategory.FILTER -> Icons.Default.FilterAlt
                            ConsumableCategory.ITM -> Icons.Default.BuildCircle
                        }

                        FilterChip(
                            selected = selected,
                            onClick = {
                                category = cat
                                if (!isEditing && (unit.isBlank() || unit == "Cartridges" || unit == "Bottles" || unit == "Units" || unit == "Packs")) {
                                    unit = cat.defaultUnit
                                }
                            },
                            label = { Text(cat.displayName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = catIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = categoryColor.copy(alpha = 0.2f),
                                selectedLabelColor = categoryColor
                            ),
                            modifier = Modifier.testTag("chip_cat_${cat.id.lowercase()}")
                        )
                    }
                }

                // Preset Quick Suggestions (Only when adding or blank)
                if (!isEditing && name.isBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Quick Domino Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        when (category) {
                            ConsumableCategory.INK -> {
                                PresetChip("2BK001 Black Ink") {
                                    name = "Domino 2BK001 Black Ketone CIJ Ink"
                                    partNumber = "EP-2BK001-500"
                                    unit = "Cartridges"
                                    locationRack = "Cabinet A-1"
                                    compatiblePrinters = "Ax150i, Ax350i, Ax550i"
                                }
                                PresetChip("2BK024 Food Contact") {
                                    name = "Domino 2BK024 Food-Contact Grade Black Ink"
                                    partNumber = "EP-2BK024-500"
                                    unit = "Cartridges"
                                    locationRack = "Sanitary Storage B-2"
                                    compatiblePrinters = "Ax350i, Ax550i"
                                }
                                PresetChip("2RD001 Red Ink") {
                                    name = "Domino 2RD001 Red CIJ Accent Ink"
                                    partNumber = "EP-2RD001-500"
                                    unit = "Cartridges"
                                    locationRack = "Cabinet A-2"
                                    compatiblePrinters = "Ax350i"
                                }
                            }
                            ConsumableCategory.MAKE_UP -> {
                                PresetChip("2WL001 Make-up") {
                                    name = "Domino 2WL001 Standard Make-up Fluid"
                                    partNumber = "EP-2WL001-1200"
                                    unit = "Cartridges"
                                    locationRack = "Cabinet A-1"
                                    compatiblePrinters = "Ax150i, Ax350i, Ax550i"
                                }
                                PresetChip("2WL004 Fast-Dry Diluent") {
                                    name = "Domino 2WL004 Fast-Evaporating Diluent Make-up"
                                    partNumber = "EP-2WL004-1200"
                                    unit = "Cartridges"
                                    locationRack = "Cabinet A-1"
                                    compatiblePrinters = "Ax350i, Ax550i"
                                }
                            }
                            ConsumableCategory.WASH -> {
                                PresetChip("WL-200 Wash Fluid") {
                                    name = "Domino WL-200 Printhead Wash Fluid"
                                    partNumber = "WL-200-500ML"
                                    unit = "Bottles"
                                    locationRack = "Line 1 Tool Caddy"
                                    compatiblePrinters = "All Domino Ax Series"
                                }
                                PresetChip("WL-210 Auto-Flush") {
                                    name = "Domino WL-210 Auto-Flush Solvent"
                                    partNumber = "WL-210-1000ML"
                                    unit = "Bottles"
                                    locationRack = "Maintenance Bay Shelf 2"
                                    compatiblePrinters = "Ax350i, Ax550i"
                                }
                            }
                            ConsumableCategory.FILTER -> {
                                PresetChip("5µm Main Filter") {
                                    name = "Domino 5µm Main Ink System Filter"
                                    partNumber = "FIL-MAIN-5UM"
                                    unit = "Packs"
                                    serviceLifeOrHours = "2,000 Operating Hours"
                                    compatiblePrinters = "All Domino Ax Series"
                                }
                                PresetChip("Damper Filter") {
                                    name = "Domino Damper & Nozzle Filter Assembly"
                                    partNumber = "FIL-DAMP-AX"
                                    unit = "Packs"
                                    serviceLifeOrHours = "3,000 Operating Hours"
                                    compatiblePrinters = "Ax350i, Ax550i"
                                }
                                PresetChip("Cabinet Air Mat") {
                                    name = "Domino Ax Cabinet Air Intake Filter Mat"
                                    partNumber = "FIL-AIR-MAT5"
                                    unit = "Packs"
                                    serviceLifeOrHours = "6 Months"
                                    compatiblePrinters = "Ax150i, Ax350i"
                                }
                            }
                            ConsumableCategory.ITM -> {
                                PresetChip("Ax350i ITM Module") {
                                    name = "Domino Ax350i ITM Service Module"
                                    partNumber = "ITM-AX350-01"
                                    unit = "Units"
                                    serviceLifeOrHours = "4,000 Hours / 12 Months"
                                    compatiblePrinters = "Domino Ax350i"
                                    notes = "Contains main ink reservoir, pump filter, and RFID transponder."
                                }
                                PresetChip("Ax150i ITM Module") {
                                    name = "Domino Ax150i ITM Type-A Service Module"
                                    partNumber = "ITM-AX150-01"
                                    unit = "Units"
                                    serviceLifeOrHours = "4,000 Hours / 12 Months"
                                    compatiblePrinters = "Domino Ax150i"
                                }
                                PresetChip("Ax550i Marine ITM") {
                                    name = "Domino Ax550i Marine Grade ITM Module"
                                    partNumber = "ITM-AX550-HD"
                                    unit = "Units"
                                    serviceLifeOrHours = "4,000 Hours / 12 Months"
                                    compatiblePrinters = "Domino Ax550i"
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Domino 2BK001 Black CIJ Ink") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Item name is required", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_consumable_name"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Part Number & Batch/Lot Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = partNumber,
                        onValueChange = { partNumber = it },
                        label = { Text("Part Number") },
                        placeholder = { Text("EP-2BK001") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_consumable_part"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = batchLotNumber,
                        onValueChange = { batchLotNumber = it },
                        label = { Text("Batch / Lot #") },
                        placeholder = { Text("LOT-2609") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_consumable_lot"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stock Quantity & Unit Stepper
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CURRENT STOCK QUANTITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(
                                    onClick = { if (quantity > 0) quantity-- },
                                    enabled = quantity > 0,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag("btn_dialog_decrease_qty")
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }

                                Text(
                                    text = "$quantity",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                OutlinedIconButton(
                                    onClick = { quantity++ },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag("btn_dialog_increase_qty")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }

                            // Unit Selector
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val units = listOf("Cartridges", "Bottles", "Units", "Packs")
                                units.forEach { u ->
                                    FilterChip(
                                        selected = unit.equals(u, ignoreCase = true),
                                        onClick = { unit = u },
                                        label = { Text(u, fontSize = 11.sp) },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Reorder Threshold Stepper
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Low Stock Alert Threshold:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(
                                    onClick = { if (minimumThreshold > 0) minimumThreshold-- },
                                    enabled = minimumThreshold > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease Min", modifier = Modifier.size(14.dp))
                                }

                                Text(
                                    text = "$minimumThreshold $unit",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DominoAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                OutlinedIconButton(
                                    onClick = { minimumThreshold++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase Min", modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Storage Rack & Expiry / Service Life
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = locationRack,
                        onValueChange = { locationRack = it },
                        label = { Text("Storage Location") },
                        placeholder = { Text("e.g. Cabinet A-1") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_consumable_location"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = if (category == ConsumableCategory.ITM || category == ConsumableCategory.FILTER) serviceLifeOrHours else expiryDate,
                        onValueChange = {
                            if (category == ConsumableCategory.ITM || category == ConsumableCategory.FILTER) {
                                serviceLifeOrHours = it
                            } else {
                                expiryDate = it
                            }
                        },
                        label = {
                            Text(if (category == ConsumableCategory.ITM || category == ConsumableCategory.FILTER) "Service Hours / Life" else "Expiry Date")
                        },
                        placeholder = {
                            Text(if (category == ConsumableCategory.ITM || category == ConsumableCategory.FILTER) "4,000 Operating Hrs" else "2027-09-15")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_consumable_expiry"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Compatible Printers
                OutlinedTextField(
                    value = compatiblePrinters,
                    onValueChange = { compatiblePrinters = it },
                    label = { Text("Compatible Domino Models") },
                    placeholder = { Text("Ax150i, Ax350i, Ax550i") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_consumable_printers"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Operational Notes / Precautions") },
                    placeholder = { Text("Keep upright, store between 15-25°C. Auto-flush enabled.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_consumable_notes"),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_cancel_consumable")
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                                return@Button
                            }

                            val itemToSave = (initialItem ?: ConsumableItem(name = name, category = category.id)).copy(
                                name = name.trim(),
                                category = category.id,
                                partNumber = partNumber.trim(),
                                batchLotNumber = batchLotNumber.trim(),
                                quantity = quantity,
                                unit = unit.trim().ifBlank { category.defaultUnit },
                                minimumThreshold = minimumThreshold,
                                locationRack = locationRack.trim(),
                                expiryDate = expiryDate.trim(),
                                serviceLifeOrHours = serviceLifeOrHours.trim(),
                                compatiblePrinters = compatiblePrinters.trim(),
                                notes = notes.trim(),
                                lastUpdated = System.currentTimeMillis()
                            )

                            onSave(itemToSave)
                        },
                        modifier = Modifier.testTag("btn_save_consumable")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEditing) "Update Stock" else "Save to Stock")
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
