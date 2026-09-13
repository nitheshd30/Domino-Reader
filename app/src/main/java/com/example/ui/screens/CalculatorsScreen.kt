package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

enum class CalculatorType(val title: String, val icon: ImageVector) {
    USP("USP Calculator", Icons.Default.AttachMoney),
    SHELF_LIFE("Shelf Life", Icons.Default.DateRange),
    KG_TO_POUCH("Kg to Pouch", Icons.Default.Scale),
    POUCH_TO_KG("Pouch to Kg", Icons.Default.FitnessCenter),
    OVERTIME("Overtime", Icons.Default.Timer),
    NORMAL("Normal", Icons.Default.Calculate)
}

@Composable
fun CalculatorsScreen() {
    var selectedCalculator by remember { mutableStateOf<CalculatorType?>(null) }

    BackHandler(enabled = selectedCalculator != null) {
        selectedCalculator = null
    }

    AnimatedContent(
        targetState = selectedCalculator,
        label = "CalculatorScreenAnimation",
        transitionSpec = {
            if (targetState != null && initialState == null) {
                slideInHorizontally { width -> width } + fadeIn() togetherWith slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith slideOutHorizontally { width -> width } + fadeOut()
            }
        }
    ) { targetCalculator ->
        if (targetCalculator == null) {
            CalculatorGrid(onSelect = { selectedCalculator = it })
        } else {
            CalculatorDetail(
                type = targetCalculator,
                onBack = { selectedCalculator = null }
            )
        }
    }
}

@Composable
fun CalculatorGrid(onSelect: (CalculatorType) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(CalculatorType.values()) { type ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { onSelect(type) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = type.title,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = type.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorDetail(type: CalculatorType, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {

            Text(
                text = type.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        when (type) {
            CalculatorType.USP -> UspCalculator()
            CalculatorType.SHELF_LIFE -> ShelfLifeCalculator()
            CalculatorType.KG_TO_POUCH -> KgToPouchCalculator()
            CalculatorType.POUCH_TO_KG -> PouchToKgCalculator()
            CalculatorType.OVERTIME -> OvertimeCalculator()
            CalculatorType.NORMAL -> NormalCalculator()
        }
    }
}

@Composable
fun UspCalculator() {
    var mrp by remember { mutableStateOf("") }
    var weightStr by remember { mutableStateOf("") }
    
    val mrpVal = mrp.toDoubleOrNull() ?: 0.0
    val weightVal = weightStr.toDoubleOrNull() ?: 0.0
    val result = if (weightVal > 0) mrpVal / weightVal else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = mrp,
            onValueChange = { mrp = it },
            label = { Text("MRP") },
            leadingIcon = { Text("₹", modifier = Modifier.padding(start = 12.dp, end = 4.dp), fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = weightStr,
            onValueChange = { weightStr = it },
            label = { Text("Weight (grams)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Unit Sale Price (USP)", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = if (result > 0) "₹ %.2f per g".format(result) else "-",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfLifeCalculator() {
    var mfgDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    var durationMonths by remember { mutableStateOf<Int?>(null) }
    var inputDays by remember { mutableStateOf("") }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = mfgDate?.atStartOfDay(java.time.ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        mfgDate = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val useByDate = remember(mfgDate, durationMonths, inputDays) {
        if (mfgDate == null) return@remember null
        
        if (durationMonths != null) {
            mfgDate!!.plusMonths(durationMonths!!.toLong()).minusDays(1)
        } else {
            val d = inputDays.toIntOrNull()
            if (d != null) {
                mfgDate!!.plusDays(d.toLong())
            } else {
                null
            }
        }
    }
    
    val resultText = remember(mfgDate, useByDate) {
        if (mfgDate != null && useByDate != null) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val days = ChronoUnit.DAYS.between(mfgDate, useByDate)
            val formattedDate = useByDate.format(formatter)
            "Use By: $formattedDate\n($days Days)"
        } else {
            "-"
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = mfgDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
            onValueChange = { },
            label = { Text("Mfg Date (dd/mm/yyyy)") },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", modifier = Modifier.clickable { showDatePicker = true })
            },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
        )
        
        Text("Select Duration (Months)", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(3, 6, 9, 12).forEach { months ->
                FilterChip(
                    selected = durationMonths == months,
                    onClick = {
                        if (durationMonths == months) {
                            durationMonths = null
                        } else {
                            durationMonths = months
                            inputDays = ""
                        }
                    },
                    label = { Text("$months") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Text("OR Enter Custom Days", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = inputDays,
            onValueChange = { 
                inputDays = it
                if (it.isNotEmpty()) durationMonths = null
            },
            label = { Text("Duration (Days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Result", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun KgToPouchCalculator() {
    var totalKg by remember { mutableStateOf("") }
    var pouchWeightG by remember { mutableStateOf("") }
    
    val kgVal = totalKg.toDoubleOrNull() ?: 0.0
    val weightVal = pouchWeightG.toDoubleOrNull() ?: 0.0
    val totalGrams = kgVal * 1000
    val pouches = if (weightVal > 0) totalGrams / weightVal else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = totalKg,
            onValueChange = { totalKg = it },
            label = { Text("Total Quantity (Kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pouchWeightG,
            onValueChange = { pouchWeightG = it },
            label = { Text("Weight per Pouch (grams)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Pouches", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = if (pouches > 0) "%,d".format(pouches.roundToInt()) else "-",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PouchToKgCalculator() {
    var totalPouches by remember { mutableStateOf("") }
    var pouchWeightG by remember { mutableStateOf("") }
    
    val pouchesVal = totalPouches.toDoubleOrNull() ?: 0.0
    val weightVal = pouchWeightG.toDoubleOrNull() ?: 0.0
    val totalKg = (pouchesVal * weightVal) / 1000.0

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = totalPouches,
            onValueChange = { totalPouches = it },
            label = { Text("Total Pouches") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pouchWeightG,
            onValueChange = { pouchWeightG = it },
            label = { Text("Weight per Pouch (grams)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Quantity (Kg)", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = if (totalKg > 0) "%.3f Kg".format(totalKg) else "-",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class TimeEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val hours: Int = 0,
    val mins: Int = 0
)




@Composable
fun OvertimeCalculator() {
    val context = LocalContext.current
    var costPerHour by remember { mutableStateOf("") }
    var inputHours by remember { mutableStateOf("") }
    var inputMins by remember { mutableStateOf("") }
    
    val entries = remember { mutableStateListOf<TimeEntry>() }
    var editingEntryId by remember { mutableStateOf<String?>(null) }
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }
    
    var savedItems by remember { mutableStateOf(OvertimePrefs.getSaved(context)) }
    var showSavedList by remember { mutableStateOf(false) }
    var loadedId by remember { mutableStateOf<String?>(null) }
    
    val totalMinutes = entries.sumOf { (it.hours * 60) + it.mins }
    
    val rate = costPerHour.toDoubleOrNull() ?: 0.0
    val totalCost = (totalMinutes / 60.0) * rate

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TIME & OVERTIME CALCULATOR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = {
                entries.clear()
                inputHours = ""
                inputMins = ""
                costPerHour = ""
                editingEntryId = null
            }) {
                Text("CLEAR ALL", color = MaterialTheme.colorScheme.error)
            }
        }

        OutlinedTextField(
            value = costPerHour,
            onValueChange = { costPerHour = it },
            label = { Text("Set Cost Per Hour (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputHours,
                onValueChange = { inputHours = it },
                label = { Text("Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = inputMins,
                onValueChange = { inputMins = it },
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    val h = inputHours.toIntOrNull() ?: 0
                    val m = inputMins.toIntOrNull() ?: 0
                    if (h > 0 || m > 0 || editingEntryId != null) {
                        if (editingEntryId != null) {
                            val idx = entries.indexOfFirst { it.id == editingEntryId }
                            if (idx != -1) {
                                entries[idx] = TimeEntry(id = editingEntryId!!, hours = h, mins = m)
                            }
                            editingEntryId = null
                        } else {
                            entries.add(TimeEntry(hours = h, mins = m))
                        }
                        inputHours = ""
                        inputMins = ""
                    }
                },
                modifier = Modifier.height(56.dp).padding(top = 8.dp)
            ) {
                Text(if (editingEntryId != null) "Update" else "+ Add")
            }
        }
        
        if (entries.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Entries", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                entries.forEach { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${entry.hours} hrs ${entry.mins} mins",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row {
                                IconButton(onClick = {
                                    editingEntryId = entry.id
                                    inputHours = entry.hours.toString()
                                    inputMins = entry.mins.toString()
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = {
                                    entries.removeAll { it.id == entry.id }
                                    if (editingEntryId == entry.id) {
                                        editingEntryId = null
                                        inputHours = ""
                                        inputMins = ""
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total Time", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                val displayHrs = totalMinutes / 60
                val displayMins = totalMinutes % 60
                Text(
                    text = "$displayHrs : ${displayMins.toString().padStart(2, '0')} hrs",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Cost (₹ ${costPerHour.ifBlank { "0" }} / hr):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "₹ %.2f".format(totalCost),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("SAVE")
                    }
                    OutlinedButton(
                        onClick = { /* Could implement clipboard copy */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("COPY")
                    }
                    Button(
                        onClick = { 
                            entries.clear()
                            inputHours = ""
                            inputMins = ""
                            editingEntryId = null
                            costPerHour = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("NEW")
                    }
                }
            }
        }
        
        if (savedItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showSavedList = !showSavedList },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saved Calculations (${savedItems.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Icon(if (showSavedList) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
            
            AnimatedVisibility(visible = showSavedList) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    savedItems.forEach { saved ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(saved.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Cost/hr: ₹${saved.costPerHour} | Entries: ${saved.entries.size}", style = MaterialTheme.typography.bodySmall)
                                }
                                Row {
                                    IconButton(onClick = {
                                        entries.clear()
                                        entries.addAll(saved.entries)
                                        costPerHour = saved.costPerHour
                                    }) {
                                        Icon(Icons.Default.Restore, contentDescription = "Load", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        OvertimePrefs.delete(context, saved.id)
                                        savedItems = OvertimePrefs.getSaved(context)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Calculation") },
            text = {
                OutlinedTextField(
                    value = saveName,
                    onValueChange = { saveName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (saveName.isNotBlank()) {
                        val newSaved = SavedOvertime(
                            id = java.util.UUID.randomUUID().toString(),
                            name = saveName.trim(),
                            costPerHour = costPerHour,
                            entries = entries.toList()
                        )
                        OvertimePrefs.save(context, newSaved)
                        savedItems = OvertimePrefs.getSaved(context)
                        showSaveDialog = false
                        saveName = ""
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun NormalCalculator() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    
    fun evaluate(expr: String): String {
        try {
            if (expr.isBlank()) return ""
            // Split by operators, keeping operators
            val tokens = Regex("(?<=[-+*/])|(?=[-+*/])").split(expr).map { it.trim() }.filter { it.isNotEmpty() }
            if (tokens.isEmpty()) return ""
            
            var current = tokens[0].toDoubleOrNull() ?: return ""
            var i = 1
            while (i < tokens.size - 1) {
                val op = tokens[i]
                val next = tokens[i+1].toDoubleOrNull() ?: return ""
                when (op) {
                    "+" -> current += next
                    "-" -> current -= next
                    "*" -> current *= next
                    "/" -> {
                        if (next == 0.0) return "Err: Div/0"
                        current /= next
                    }
                }
                i += 2
            }
            // Format to remove trailing .0
            val resStr = current.toString()
            return if (resStr.endsWith(".0")) resStr.dropLast(2) else resStr
        } catch (e: Exception) {
            return "Error"
        }
    }

    LaunchedEffect(expression) {
        result = evaluate(expression)
    }

    val buttons = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", ".", "+")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "= ${result.ifEmpty { "0" }}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { btn ->
                        Button(
                            onClick = {
                                when (btn) {
                                    "C" -> {
                                        expression = ""
                                        result = ""
                                    }
                                    else -> {
                                        expression += btn
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.2f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (btn in listOf("/", "*", "-", "+", "C")) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surface,
                                contentColor = if (btn in listOf("/", "*", "-", "+", "C")) 
                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = btn,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Delete / Backspace button and Equals button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (expression.isNotEmpty()) {
                            expression = expression.dropLast(1)
                        }
                    },
                    modifier = Modifier.weight(1f).aspectRatio(2.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace")
                }
                
                Button(
                    onClick = {
                        if (result.isNotEmpty() && !result.startsWith("Err")) {
                            expression = result
                            result = ""
                        }
                    },
                    modifier = Modifier.weight(1f).aspectRatio(2.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("=", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
