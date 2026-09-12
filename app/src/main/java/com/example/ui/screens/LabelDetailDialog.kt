package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DominoLabel
import com.example.ui.components.PrintheadSimulationView
import com.example.ui.theme.DominoAmber
import com.example.ui.theme.DominoCyan

@Composable
fun LabelDetailDialog(
    label: DominoLabel,
    onDismiss: () -> Unit,
    onUpdateLabel: (DominoLabel) -> Unit
) {
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }

    var editedBatch by remember { mutableStateOf(label.batchNumber) }
    var editedMfd by remember { mutableStateOf(label.mfgDate) }
    var editedExp by remember { mutableStateOf(label.expiryDate) }
    var editedMrp by remember { mutableStateOf(label.mrp) }
    var editedUsp by remember { mutableStateOf(label.getEffectiveUsp()) }
    var editedUseBy by remember { mutableStateOf(label.useBy) }
    var editedWeight by remember { mutableStateOf(label.weightDetails) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("label_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DOMINO Ax LABEL INSPECTOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = label.labelName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Source: StorageCard2/Labels/${label.fileName}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Continuous Inkjet Printhead Simulation
                Text(
                    text = "PRINTHEAD SIMULATION (DOT MATRIX CIJ)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                PrintheadSimulationView(
                    label = label,
                    overrideBatch = if (isEditing) editedBatch else null,
                    overrideMfd = if (isEditing) editedMfd else null,
                    overrideUseBy = if (isEditing) editedUseBy else null,
                    overrideMrp = if (isEditing) editedMrp else null,
                    overrideUsp = if (isEditing) editedUsp else null,
                    overrideWeight = if (isEditing) editedWeight else null
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Extracted Label Contents Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXTRACTED LABEL CONTENTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.testTag("toggle_edit_button")
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isEditing) "Cancel Edit" else "Edit Values")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isEditing) {
                    // Edit Mode fields
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editedBatch,
                            onValueChange = { editedBatch = it },
                            label = { Text("Batch Number") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_batch_input"),
                            singleLine = true
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editedMfd,
                                onValueChange = { editedMfd = it },
                                label = { Text("Date of Mfg (MFD)") },
                                modifier = Modifier.weight(1f).testTag("edit_mfd_input"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editedUseBy,
                                onValueChange = { editedUseBy = it },
                                label = { Text("Use By Date") },
                                modifier = Modifier.weight(1f).testTag("edit_useby_input"),
                                singleLine = true
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editedMrp,
                                onValueChange = { editedMrp = it },
                                label = { Text("MRP (e.g. 475.00)") },
                                modifier = Modifier.weight(1f).testTag("edit_mrp_input"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editedWeight,
                                onValueChange = { editedWeight = it },
                                label = { Text("Weight Details (e.g. 200g)") },
                                modifier = Modifier.weight(1f).testTag("edit_weight_input"),
                                singleLine = true
                            )
                        }
                        OutlinedTextField(
                            value = editedUsp,
                            onValueChange = { editedUsp = it },
                            label = { Text("Unit Sale Price (e.g. (USP ₹ 2.38/g))") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_usp_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editedExp,
                            onValueChange = { editedExp = it },
                            label = { Text("Expiry Date Note") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_exp_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val updated = label.copy(
                                    batchNumber = editedBatch,
                                    mfgDate = editedMfd,
                                    useBy = editedUseBy,
                                    expiryDate = editedExp,
                                    mrp = editedMrp,
                                    unitSalePrice = editedUsp,
                                    weightDetails = editedWeight
                                )
                                onUpdateLabel(updated)
                                isEditing = false
                                Toast.makeText(context, "Label updated in database", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_label_changes_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Label Changes")
                        }
                    }
                } else {
                    // Display Cards
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Batch Number with Copy action
                            DetailRowWithCopy(
                                title = "BATCH NUMBER",
                                value = label.batchNumber,
                                isMonospace = true,
                                highlightColor = MaterialTheme.colorScheme.primary,
                                onCopy = {
                                    copyToClipboard(context, "Batch Number", label.batchNumber)
                                }
                            )

                            // Date of Manufacturing
                            DetailRowWithCopy(
                                title = "DATE OF MFG",
                                value = label.mfgDate,
                                onCopy = {
                                    copyToClipboard(context, "Date of Mfg", label.mfgDate)
                                }
                            )

                            // Use By
                            DetailRowWithCopy(
                                title = "USE BY",
                                value = label.useBy,
                                onCopy = {
                                    copyToClipboard(context, "Use By", label.useBy)
                                }
                            )

                            // MRP
                            DetailRowWithCopy(
                                title = "MRP (MAX RETAIL PRICE)",
                                value = "${label.getDisplayMrp()} (INCL. OF ALL TAXES)",
                                highlightColor = DominoAmber,
                                onCopy = {
                                    copyToClipboard(context, "MRP", label.getDisplayMrp())
                                }
                            )

                            // Unit Sale Price (USP)
                            if (label.getEffectiveUsp().isNotBlank()) {
                                DetailRowWithCopy(
                                    title = "UNIT SALE PRICE (USP)",
                                    value = label.getEffectiveUsp(),
                                    highlightColor = DominoAmber,
                                    onCopy = {
                                        copyToClipboard(context, "USP", label.getEffectiveUsp())
                                    }
                                )
                            }

                            // Expiry Date / Shelf Life
                            DetailRowWithCopy(
                                title = "EXPIRY DATE / SHELF LIFE",
                                value = label.expiryDate,
                                onCopy = {
                                    copyToClipboard(context, "Expiry Date", label.expiryDate)
                                }
                            )

                            // Weight Details
                            DetailRowWithCopy(
                                title = "WEIGHT DETAILS",
                                value = label.weightDetails,
                                highlightColor = DominoCyan,
                                onCopy = {
                                    copyToClipboard(context, "Weight", label.weightDetails)
                                }
                            )

                            // Raster Drop Size
                            DetailRowWithCopy(
                                title = "PRINTHEAD RASTER FORMAT",
                                value = label.rasterDropSize,
                                isMonospace = true
                            )

                            // Associated Bitmaps
                            if (label.associatedImage.isNotBlank()) {
                                DetailRowWithCopy(
                                    title = "ASSOCIATED BITMAP GRAPHIC",
                                    value = "StorageCard2/Labels/IMAGE/${label.associatedImage}",
                                    isMonospace = true
                                )
                            }

                            // Barcode
                            if (label.barcodeData.isNotBlank()) {
                                DetailRowWithCopy(
                                    title = "BARCODE / 2D CODE DATA",
                                    value = label.barcodeData,
                                    isMonospace = true
                                )
                            }
                        }
                    }
                }

                // Raw .LBL File Decoded Stream
                if (label.rawLabelContent.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("raw_lbl_stream_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RAW .LBL FILE STREAM & DECODED TOKENS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DominoCyan,
                                    letterSpacing = 0.5.sp
                                )

                                IconButton(
                                    onClick = {
                                        copyToClipboard(context, "Raw .LBL Content", label.rawLabelContent)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Raw Stream",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = label.rawLabelContent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Copy Complete Batch Spec Button
                Button(
                    onClick = {
                        val fullSpec = """
                            Label: ${label.labelName}
                            Batch Number: ${label.batchNumber}
                            Date of Mfg: ${label.mfgDate}
                            Use By: ${label.useBy}
                            MRP: ${label.getDisplayMrp()} (INCL. OF ALL TAXES)
                            ${if (label.getEffectiveUsp().isNotBlank()) "Unit Sale Price: ${label.getEffectiveUsp()}" else ""}
                            Expiry / Shelf Life: ${label.expiryDate}
                            Net Wt: ${label.weightDetails}
                        """.trimIndent()
                        copyToClipboard(context, "Label Specification", fullSpec)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("copy_full_spec_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Spec", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy All Label Details")
                }
            }
        }
    }
}

@Composable
private fun DetailRowWithCopy(
    title: String,
    value: String,
    isMonospace: Boolean = false,
    highlightColor: Color? = null,
    onCopy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                color = highlightColor ?: MaterialTheme.colorScheme.onSurface
            )
        }

        if (onCopy != null) {
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $title",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
}
