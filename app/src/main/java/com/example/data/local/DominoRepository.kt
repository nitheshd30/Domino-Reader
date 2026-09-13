package com.example.data.local

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.data.model.ConsumableItem
import com.example.data.model.DominoLabel
import com.example.data.model.PrinterBackup
import com.example.data.model.ProductionLog
import com.example.ui.screens.SavedOvertime
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DominoRepository(
    private val database: DominoDatabase, // Kept for compatibility but unused
    private val context: Context
) {
    private val firestore: FirebaseFirestore? = try {
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(context)
            }
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.e("DominoRepository", "Firebase not initialized. Ensure google-services.json is present.", e)
        null
    }

    private fun <T> collectionFlow(collectionName: String, clazz: Class<T>): Flow<List<T>> {
        if (firestore == null) return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection(collectionName)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.toObjects(clazz)
                        trySend(items)
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    val allBackups: Flow<List<PrinterBackup>> = collectionFlow("printer_backups", PrinterBackup::class.java)
    val allLabels: Flow<List<DominoLabel>> = collectionFlow("domino_labels", DominoLabel::class.java)
    val allLogs: Flow<List<ProductionLog>> = collectionFlow("production_logs", ProductionLog::class.java)
    val allConsumables: Flow<List<ConsumableItem>> = collectionFlow("consumable_items", ConsumableItem::class.java)

    suspend fun clearAllData() {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            val collections = listOf("printer_backups", "domino_labels", "production_logs", "consumable_items", "saved_calculations")
            for (collectionName in collections) {
                try {
                    val snapshot = firestore.collection(collectionName).get().await()
                    if (snapshot.documents.isNotEmpty()) {
                        val batch = firestore.batch()
                        for (document in snapshot.documents) {
                            batch.delete(document.reference)
                        }
                        batch.commit().await()
                    }
                } catch (e: Exception) {
                    Log.e("DominoRepository", "Error clearing $collectionName", e)
                }
            }
        }
    }

    suspend fun ensureCleanDatabase() {
        // No-op for Firestore
    }

    suspend fun resetDatabaseToDefaults() {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            DominoSeedData.getInitialBackups().forEach { insertBackup(it) }
            DominoSeedData.getInitialLabels().forEach { insertLabel(it) }
            DominoSeedData.getInitialProductionLogs().forEach { insertLog(it) }
        }
    }

    suspend fun clearAllConsumables() {}
    
    suspend fun seedDefaultConsumables() {}

    suspend fun insertConsumable(item: ConsumableItem): Long {
        if (firestore == null) return 0L
        return withContext(Dispatchers.IO) {
            val id = if (item.id == 0L) System.currentTimeMillis() else item.id
            val newItem = item.copy(id = id)
            firestore.collection("consumable_items").document(id.toString()).set(newItem).await()
            id
        }
    }

    suspend fun updateConsumable(item: ConsumableItem) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            val updated = item.copy(lastUpdated = System.currentTimeMillis())
            firestore.collection("consumable_items").document(item.id.toString()).set(updated).await()
        }
    }

    suspend fun deleteConsumable(item: ConsumableItem) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            firestore.collection("consumable_items").document(item.id.toString()).delete().await()
        }
    }

    suspend fun adjustConsumableQuantity(id: Long, delta: Int) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            val docRef = firestore.collection("consumable_items").document(id.toString())
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentQuantity = snapshot.getLong("quantity")?.toInt() ?: 0
                transaction.update(docRef, "quantity", currentQuantity + delta)
                transaction.update(docRef, "lastUpdated", System.currentTimeMillis())
            }.await()
        }
    }

    fun getLabelsForPrinter(printerId: Long, query: String = ""): Flow<List<DominoLabel>> {
        if (firestore == null) return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("domino_labels")
                .whereEqualTo("printerBackupId", printerId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    var items = snapshot?.toObjects(DominoLabel::class.java) ?: emptyList()
                    if (query.isNotBlank()) {
                        items = items.filter { it.labelName.contains(query, ignoreCase = true) || it.fileName.contains(query, ignoreCase = true) }
                    }
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    fun searchAllLabels(query: String): Flow<List<DominoLabel>> {
        if (firestore == null) return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("domino_labels")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    var items = snapshot?.toObjects(DominoLabel::class.java) ?: emptyList()
                    if (query.isNotBlank()) {
                        items = items.filter { it.labelName.contains(query, ignoreCase = true) || it.fileName.contains(query, ignoreCase = true) }
                    }
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    fun getLogsForPrinter(printerId: Long, query: String = ""): Flow<List<ProductionLog>> {
        if (firestore == null) return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("production_logs")
                .whereEqualTo("printerBackupId", printerId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    var items = snapshot?.toObjects(ProductionLog::class.java) ?: emptyList()
                    if (query.isNotBlank()) {
                        items = items.filter { it.labelName.contains(query, ignoreCase = true) || it.batchNumber.contains(query, ignoreCase = true) }
                    }
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    fun searchAllLogs(query: String): Flow<List<ProductionLog>> {
        if (firestore == null) return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore.collection("production_logs")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    var items = snapshot?.toObjects(ProductionLog::class.java) ?: emptyList()
                    if (query.isNotBlank()) {
                        items = items.filter { it.labelName.contains(query, ignoreCase = true) || it.batchNumber.contains(query, ignoreCase = true) }
                    }
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    fun getBackupById(id: Long): Flow<PrinterBackup?> {
        if (firestore == null) return flowOf(null)
        return callbackFlow {
            val listener = firestore.collection("printer_backups").document(id.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    trySend(snapshot?.toObject(PrinterBackup::class.java))
                }
            awaitClose { listener.remove() }
        }
    }

    fun getLabelById(id: Long): Flow<DominoLabel?> {
        if (firestore == null) return flowOf(null)
        return callbackFlow {
            val listener = firestore.collection("domino_labels").document(id.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    trySend(snapshot?.toObject(DominoLabel::class.java))
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun insertBackup(backup: PrinterBackup): Long {
        if (firestore == null) return 0L
        return withContext(Dispatchers.IO) {
            val id = if (backup.id == 0L) System.currentTimeMillis() else backup.id
            val newBackup = backup.copy(id = id)
            firestore.collection("printer_backups").document(id.toString()).set(newBackup).await()
            id
        }
    }

    suspend fun updateBackup(backup: PrinterBackup) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            firestore.collection("printer_backups").document(backup.id.toString()).set(backup).await()
        }
    }

    suspend fun deleteBackup(backup: PrinterBackup) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            firestore.collection("printer_backups").document(backup.id.toString()).delete().await()
        }
    }

    suspend fun insertLabel(label: DominoLabel): Long {
        if (firestore == null) return 0L
        return withContext(Dispatchers.IO) {
            val id = if (label.id == 0L) System.currentTimeMillis() else label.id
            val newLabel = label.copy(id = id)
            firestore.collection("domino_labels").document(id.toString()).set(newLabel).await()
            id
        }
    }

    suspend fun updateLabel(label: DominoLabel) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            firestore.collection("domino_labels").document(label.id.toString()).set(label).await()
        }
    }

    suspend fun deleteLabel(label: DominoLabel) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            firestore.collection("domino_labels").document(label.id.toString()).delete().await()
        }
    }

    suspend fun insertLog(log: ProductionLog): Long {
        if (firestore == null) return 0L
        return withContext(Dispatchers.IO) {
            val id = if (log.id == 0L) System.currentTimeMillis() else log.id
            val newLog = log.copy(id = id)
            firestore.collection("production_logs").document(id.toString()).set(newLog).await()
            id
        }
    }

    suspend fun deleteLog(log: ProductionLog) {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            firestore.collection("production_logs").document(log.id.toString()).delete().await()
        }
    }

    suspend fun importBackupFromUri(uri: Uri, customName: String? = null): PrinterBackup {
        if (firestore == null) return PrinterBackup(0, "Error", "Error", "Error", "Error", "Error", "Error", 0, 0)
        return withContext(Dispatchers.IO) {
            val newBackupId = System.currentTimeMillis()
            val tempBackup = PrinterBackup(
                id = newBackupId,
                printerName = customName?.ifBlank { "Importing..." } ?: "Importing...",
                printerModel = "Ax350i",
                serialNumber = "PENDING",
                lineLocation = "Imported Line",
                backupDate = "Processing",
                totalLabelsCount = 0,
                totalPacksPrinted = 0
            )
            firestore.collection("printer_backups").document(newBackupId.toString()).set(tempBackup).await()
            
            val parsed = DominoBackupParser.parseBackupFile(
                context = context,
                uri = uri,
                customPrinterName = customName,
                targetBackupId = newBackupId
            )
            
            val finalBackup = parsed.printerBackup.copy(id = newBackupId)
            firestore.collection("printer_backups").document(newBackupId.toString()).set(finalBackup).await()
            
            // Insert labels in batches if many
            parsed.labels.forEachIndexed { index, label ->
                val id = System.currentTimeMillis() + index
                firestore.collection("domino_labels").document(id.toString()).set(label.copy(id = id, printerBackupId = newBackupId)).await()
            }
            
            parsed.logs.forEachIndexed { index, log ->
                val id = System.currentTimeMillis() + index
                firestore.collection("production_logs").document(id.toString()).set(log.copy(id = id, printerBackupId = newBackupId)).await()
            }
            
            finalBackup
        }
    }

    suspend fun parseDirectLabelFromUri(uri: Uri): DominoLabel {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open file from selected URI")
            val bytes = inputStream.use { it.readBytes() }
            val fileName = DominoBackupParser.getFileName(context, uri)
            DominoBackupParser.parseLabelFromBytes(bytes, fileName, 0, 1L)
        }
    }
}
