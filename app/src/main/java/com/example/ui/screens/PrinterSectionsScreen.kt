package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrinterBackup
import com.example.ui.components.PrinterBackupCard
import com.example.ui.theme.DominoCyan

@Composable
fun PrinterSectionsScreen(
    backups: List<PrinterBackup>,
    selectedPrinterId: Long?,
    onSelectPrinter: (Long?) -> Unit,
    onDeleteBackup: (PrinterBackup) -> Unit,
    onViewLabelsForPrinter: (Long) -> Unit,
    onOpenImportDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sectionSearchQuery by remember { mutableStateOf("") }

    val filteredBackups = backups.filter { backup ->
        sectionSearchQuery.isBlank() ||
                backup.printerName.contains(sectionSearchQuery, ignoreCase = true) ||
                backup.printerModel.contains(sectionSearchQuery, ignoreCase = true) ||
                backup.lineLocation.contains(sectionSearchQuery, ignoreCase = true) ||
                backup.serialNumber.contains(sectionSearchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize().testTag("printer_sections_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search & Action Header
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = sectionSearchQuery,
                    onValueChange = { sectionSearchQuery = it },
                    modifier = Modifier.fillMaxWidth().testTag("printer_search_input"),
                    placeholder = {
                        Text(
                            text = "Search printer sections (Ax350i, Line 1, Bolas)...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )

                if (backups.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Unified "All Printers" Section Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPrinterId == null) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            } else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FolderShared,
                                    contentDescription = "All Printers",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "All Printer Backups (Unified)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${backups.size} Printer Sections • ${backups.sumOf { it.totalLabelsCount }} Total Labels",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectPrinter(null) },
                                shape = RoundedCornerShape(8.dp),
                                colors = if (selectedPrinterId == null) {
                                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                } else {
                                    ButtonDefaults.outlinedButtonColors()
                                },
                                modifier = Modifier.testTag("select_all_printers_button")
                            ) {
                                Text(
                                    text = if (selectedPrinterId == null) "Active" else "View All",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            if (backups.isNotEmpty()) {
                // Header info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredBackups.size} PRINTER SECTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    OutlinedButton(
                        onClick = onOpenImportDialog,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("import_new_backup_button")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Import", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Backup", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredBackups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FolderShared,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (sectionSearchQuery.isNotBlank()) "No printers match '$sectionSearchQuery'" else "No Printer Sections",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (sectionSearchQuery.isNotBlank())
                                    "Try searching with a different model or line name."
                                else
                                    "No printer lines configured yet. Import a Domino Ax printer backup archive or add a printer section to get started.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onOpenImportDialog,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("empty_state_add_printer_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Backup (.ZIP)")
                            }
                        }
                    }
                }
            } else {
                // Backups List
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredBackups, key = { it.id }) { backup ->
                        PrinterBackupCard(
                            backup = backup,
                            isSelected = selectedPrinterId == backup.id,
                            onSelect = { onSelectPrinter(backup.id) },
                            onDelete = { onDeleteBackup(backup) },
                            onViewLabels = { onViewLabelsForPrinter(backup.id) }
                        )
                    }
                }
            }
        }
    }
}
