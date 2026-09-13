package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DominoLabel
import com.example.ui.theme.DominoCyan

@Composable
fun AddEditLabelDialog(
    initialLabel: DominoLabel? = null,
    printerId: Long,
    onDismiss: () -> Unit,
    onSave: (DominoLabel) -> Unit
) {
    var labelName by remember { mutableStateOf(initialLabel?.labelName ?: "") }
    var batchNumber by remember { mutableStateOf(initialLabel?.batchNumber ?: "") }
    var mrp by remember { mutableStateOf(initialLabel?.mrp ?: "") }
    var usp by remember { mutableStateOf(initialLabel?.unitSalePrice ?: "") }
    var mfgDate by remember { mutableStateOf(initialLabel?.mfgDate ?: "") }
    var useBy by remember { mutableStateOf(initialLabel?.useBy ?: "") }
    var weightDetails by remember { mutableStateOf(initialLabel?.weightDetails ?: "") }
    var brand by remember { mutableStateOf(initialLabel?.brand ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialLabel == null) "Create Label" else "Edit Label",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = labelName,
                    onValueChange = { labelName = it },
                    label = { Text("Label Name (e.g. Bolas Pista 200g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch Number") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mrp,
                        onValueChange = { mrp = it },
                        label = { Text("MRP (e.g. 475.00)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = usp,
                        onValueChange = { usp = it },
                        label = { Text("USP (e.g. ₹ 2.38/g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mfgDate,
                        onValueChange = { mfgDate = it },
                        label = { Text("MFG Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = useBy,
                        onValueChange = { useBy = it },
                        label = { Text("EXP Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weightDetails,
                    onValueChange = { weightDetails = it },
                    label = { Text("Weight Details (e.g. 200g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val finalLabel = initialLabel?.copy(
                            labelName = labelName,
                            batchNumber = batchNumber,
                            mrp = mrp,
                            unitSalePrice = usp,
                            mfgDate = mfgDate,
                            useBy = useBy,
                            weightDetails = weightDetails,
                            brand = brand
                        ) ?: DominoLabel(
                            printerBackupId = printerId,
                            fileName = if (labelName.isNotBlank()) "$labelName.lbl" else "Custom.lbl",
                            labelName = labelName,
                            brand = brand,
                            productCategory = "Custom",
                            batchNumber = batchNumber,
                            mfgDate = mfgDate,
                            useBy = useBy,
                            mrp = mrp,
                            weightDetails = weightDetails,
                            unitSalePrice = usp
                        )
                        onSave(finalLabel)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DominoCyan, contentColor = androidx.compose.ui.graphics.Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Label", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
