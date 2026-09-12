package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBackupDialog(
    isImporting: Boolean,
    onDismiss: () -> Unit,
    onImportFile: (Uri, String?) -> Unit,
    onAddManualPrinter: (String, String, String) -> Unit,
    onDirectParseFile: ((Uri) -> Unit)? = null,
    onDirectParseText: ((String, String) -> Unit)? = null,
    directParsedLabel: com.example.data.model.DominoLabel? = null,
    onSaveDirectLabel: ((com.example.data.model.DominoLabel) -> Unit)? = null,
    onClearDirectLabel: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Read .LBL, 1 = Import .ZIP, 2 = New Section
    var customPrinterName by remember { mutableStateOf("") }
    var manualModel by remember { mutableStateOf("Ax350i") }
    var manualLocation by remember { mutableStateOf("") }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    // Direct .lbl text input state
    var lblRawInput by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val safeDismiss = {
        focusManager.clearFocus()
        keyboardController?.hide()
        onClearDirectLabel?.invoke()
        onDismiss()
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            if (selectedTab == 0 && onDirectParseFile != null) {
                onDirectParseFile(uri)
            } else {
                onImportFile(uri, customPrinterName.takeIf { it.isNotBlank() })
                safeDismiss()
            }
        }
    }

    Dialog(
        onDismissRequest = safeDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true)
    ) {
        BackHandler(enabled = true) {
            safeDismiss()
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("import_backup_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DOMINO Ax LABEL ENGINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = when (selectedTab) {
                                0 -> "Read & Inspect .LBL / .LNL File"
                                1 -> "Import Backup Archive"
                                else -> "New Printer Section"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = safeDismiss,
                        modifier = Modifier.testTag("close_import_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: 0 = Read .LBL / .LNL, 1 = Import .ZIP, 2 = New Section
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Read .LBL / .LNL") },
                        modifier = Modifier.testTag("tab_read_lbl")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Import .ZIP") },
                        modifier = Modifier.testTag("tab_import_file")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("New Section") },
                        modifier = Modifier.testTag("tab_manual_section")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // Direct .LBL / .LNL Reader
                        Text(
                            text = "Extract actual CIJ print streams (1-line, 2-line, 3-line, or 4-line), batch codes, dates, MRP, USP, and raster drop settings from Domino Ax .lbl and .lnl files.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Button to select .lbl / .lnl file
                        Button(
                            onClick = {
                                filePickerLauncher.launch(arrayOf("*/*"))
                            },
                            modifier = Modifier.fillMaxWidth().testTag("select_lbl_file_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FolderZip, contentDescription = "Select LBL / LNL")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open & Read .LBL / .LNL File From Storage")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Or Paste Raw Text
                        Text(
                            text = "Or paste raw .lbl / .lnl content or single line stream below:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = lblRawInput,
                            onValueChange = { lblRawInput = it },
                            label = { Text("Raw .LBL / .LNL Content or CIJ Stream") },
                            placeholder = {
                                Text("e.g. Single Line Stream:\nBATCH: IPRS026  MFD: 10/09/2026  EXP: 09/06/2026  MRP: Rs. 475.00\n\nOr 4-line format:\nITEM: BOLAS PISTA SALTED 200G\nBATCH NO: IPRS026\nDATE OF MFG: 10/09/2026\nUSE BY: 09/06/2026\nMRP: 475.00 (USP ₹ 2.38/g)")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("lbl_raw_text_input"),
                            maxLines = 6
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                if (lblRawInput.isNotBlank() && onDirectParseText != null) {
                                    onDirectParseText(lblRawInput, "Pasted File")
                                }
                            },
                            enabled = lblRawInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("decode_pasted_lbl_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Decode & Parse Content")
                        }

                        // Display Direct Parsed Output if available
                        directParsedLabel?.let { label ->
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "PARSED FILE OUTPUT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Print Preview Matrix
                            com.example.ui.components.PrintheadSimulationView(
                                label = label,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Extracted Specs Summary
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val isSingle = label.formatType == com.example.data.model.LabelFormatType.SINGLE_LINE.id ||
                                            (label.customLine1.isNotBlank() && label.customLine2.isBlank() && label.batchNumber.isBlank())
                                    val fmtObj = com.example.data.model.LabelFormatType.fromId(label.formatType)

                                    Text("Label: ${label.labelName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Format: ${fmtObj.title}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                                    if (isSingle) {
                                        Text(
                                            text = "CIJ Stream: ${label.customLine1.ifBlank { label.getPrintLines().firstOrNull() ?: label.labelName }}",
                                            color = com.example.ui.theme.DominoCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    } else {
                                        if (label.batchNumber.isNotBlank()) Text("Batch No: ${label.batchNumber}", color = com.example.ui.theme.DominoCyan, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        if (label.mfgDate.isNotBlank()) Text("Date of Mfg: ${label.mfgDate}", fontSize = 12.sp)
                                        if (label.useBy.isNotBlank()) Text("Use By: ${label.useBy}", fontSize = 12.sp)
                                        if (label.mrp.isNotBlank()) Text("MRP: ${label.getDisplayMrp()}", color = com.example.ui.theme.DominoAmber, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                    if (label.getEffectiveUsp().isNotBlank()) {
                                        Text("Unit Sale Price: ${label.getEffectiveUsp()}", color = com.example.ui.theme.DominoAmber, fontSize = 12.sp)
                                    }
                                    if (label.weightDetails.isNotBlank()) Text("Net Wt: ${label.weightDetails}", fontSize = 12.sp)
                                    Text("Raster: ${label.rasterDropSize}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Save to Active Section Button
                            if (onSaveDirectLabel != null) {
                                Button(
                                    onClick = {
                                        onSaveDirectLabel(label)
                                        safeDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("save_direct_label_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Save")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Label to Printer Section")
                                }
                            }
                        }
                    }

                    1 -> {
                        // Import File UI
                        Text(
                            text = "Extract production logs, labels, batch codes and raster specs directly from a Domino Ax USB or SD card backup archive (StorageCard2/).",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = customPrinterName,
                            onValueChange = { customPrinterName = it },
                            label = { Text("Custom Section Name (Optional)") },
                            placeholder = { Text("e.g. Packaging Line 4 - Cashew Cell") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_printer_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderZip,
                                    contentDescription = "ZIP file",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Select Domino Ax Backup Archive",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Supports .zip archives containing StorageCard2/Labels/ & Logs/ or single .lbl files",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isImporting) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Extracting labels and logs...")
                            }
                        } else {
                            Button(
                                onClick = {
                                    filePickerLauncher.launch(arrayOf("*/*"))
                                },
                                modifier = Modifier.fillMaxWidth().testTag("select_file_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = "Upload")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Choose Backup Archive (.ZIP)")
                            }
                        }
                    }

                    2 -> {
                        // Manual Printer Section Creation
                        Text(
                            text = "Create a separate printer profile to manage specific lines and backup archives.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = customPrinterName,
                            onValueChange = { customPrinterName = it },
                            label = { Text("Printer Line Name") },
                            placeholder = { Text("e.g. Line 4 - High-Speed Cartoning") },
                            modifier = Modifier.fillMaxWidth().testTag("manual_printer_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = modelDropdownExpanded,
                            onExpandedChange = { modelDropdownExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = manualModel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Domino Printer Model") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = modelDropdownExpanded,
                                onDismissRequest = { modelDropdownExpanded = false }
                            ) {
                                listOf("Ax150i", "Ax350i", "Ax550i", "Ax-Series CIJ").forEach { model ->
                                    DropdownMenuItem(
                                        text = { Text(model) },
                                        onClick = {
                                            manualModel = model
                                            modelDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = manualLocation,
                            onValueChange = { manualLocation = it },
                            label = { Text("Factory Location / Conveyor Line") },
                            placeholder = { Text("e.g. Floor 2 Packaging Bay") },
                            modifier = Modifier.fillMaxWidth().testTag("manual_location_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (customPrinterName.isNotBlank()) {
                                    onAddManualPrinter(
                                        customPrinterName,
                                        manualModel,
                                        manualLocation.ifBlank { "Production Hall" }
                                    )
                                    safeDismiss()
                                }
                            },
                            enabled = customPrinterName.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("create_printer_section_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Printer Section")
                        }
                    }
                }
            }
        }
    }
}
