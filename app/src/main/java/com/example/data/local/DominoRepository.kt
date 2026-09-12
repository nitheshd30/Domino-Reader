package com.example.data.local

import android.content.Context
import android.net.Uri
import com.example.data.model.DominoLabel
import com.example.data.model.PrinterBackup
import com.example.data.model.ProductionLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DominoRepository(
    private val database: DominoDatabase,
    private val context: Context
) {
    private val printerDao = database.printerBackupDao()
    private val labelDao = database.dominoLabelDao()
    private val logDao = database.productionLogDao()

    val allBackups: Flow<List<PrinterBackup>> = printerDao.getAllBackups()
    val allLabels: Flow<List<DominoLabel>> = labelDao.getAllLabels()
    val allLogs: Flow<List<ProductionLog>> = logDao.getAllLogs()

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    suspend fun ensureCleanDatabase() {
        withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences("domino_app_prefs", Context.MODE_PRIVATE)
            val alreadyCleaned = prefs.getBoolean("cleared_all_example_entries_v2", false)
            if (!alreadyCleaned) {
                database.clearAllTables()
                prefs.edit().putBoolean("cleared_all_example_entries_v2", true).apply()
            }
        }
    }

    suspend fun resetDatabaseToDefaults() {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
            val backups = DominoSeedData.getInitialBackups()
            printerDao.insertBackups(backups)

            val labels = DominoSeedData.getInitialLabels()
            labelDao.insertLabels(labels)

            val logs = DominoSeedData.getInitialProductionLogs()
            logDao.insertLogs(logs)
        }
    }

    fun getLabelsForPrinter(printerId: Long, query: String = ""): Flow<List<DominoLabel>> {
        return if (query.isBlank()) {
            labelDao.getLabelsByPrinter(printerId)
        } else {
            labelDao.searchLabelsByPrinter(printerId, query)
        }
    }

    fun searchAllLabels(query: String): Flow<List<DominoLabel>> {
        return if (query.isBlank()) {
            labelDao.getAllLabels()
        } else {
            labelDao.searchAllLabels(query)
        }
    }

    fun getLogsForPrinter(printerId: Long, query: String = ""): Flow<List<ProductionLog>> {
        return if (query.isBlank()) {
            logDao.getLogsByPrinter(printerId)
        } else {
            logDao.searchLogsByPrinter(printerId, query)
        }
    }

    fun searchAllLogs(query: String): Flow<List<ProductionLog>> {
        return if (query.isBlank()) {
            logDao.getAllLogs()
        } else {
            logDao.searchAllLogs(query)
        }
    }

    fun getBackupById(id: Long): Flow<PrinterBackup?> {
        return printerDao.getBackupById(id)
    }

    fun getLabelById(id: Long): Flow<DominoLabel?> {
        return labelDao.getLabelById(id)
    }

    suspend fun insertBackup(backup: PrinterBackup): Long {
        return withContext(Dispatchers.IO) {
            printerDao.insertBackup(backup)
        }
    }

    suspend fun updateBackup(backup: PrinterBackup) {
        withContext(Dispatchers.IO) {
            printerDao.updateBackup(backup)
        }
    }

    suspend fun deleteBackup(backup: PrinterBackup) {
        withContext(Dispatchers.IO) {
            printerDao.deleteBackup(backup)
        }
    }

    suspend fun insertLabel(label: DominoLabel): Long {
        return withContext(Dispatchers.IO) {
            labelDao.insertLabel(label)
        }
    }

    suspend fun updateLabel(label: DominoLabel) {
        withContext(Dispatchers.IO) {
            labelDao.updateLabel(label)
        }
    }

    suspend fun deleteLabel(label: DominoLabel) {
        withContext(Dispatchers.IO) {
            labelDao.deleteLabel(label)
        }
    }

    suspend fun insertLog(log: ProductionLog): Long {
        return withContext(Dispatchers.IO) {
            logDao.insertLog(log)
        }
    }

    suspend fun deleteLog(log: ProductionLog) {
        withContext(Dispatchers.IO) {
            logDao.deleteLog(log)
        }
    }

    suspend fun importBackupFromUri(uri: Uri, customName: String? = null): PrinterBackup {
        return withContext(Dispatchers.IO) {
            // Create a placeholder backup first to get an ID
            val tempBackup = PrinterBackup(
                printerName = customName?.ifBlank { "Importing..." } ?: "Importing...",
                printerModel = "Ax350i",
                serialNumber = "PENDING",
                lineLocation = "Imported Line",
                backupDate = "Processing",
                totalLabelsCount = 0,
                totalPacksPrinted = 0
            )
            val newBackupId = printerDao.insertBackup(tempBackup)

            val parsed = DominoBackupParser.parseBackupFile(
                context = context,
                uri = uri,
                customPrinterName = customName,
                targetBackupId = newBackupId
            )

            // Update backup with parsed details
            val finalBackup = parsed.printerBackup.copy(id = newBackupId)
            printerDao.updateBackup(finalBackup)

            // Insert labels with newBackupId
            val labelsWithId = parsed.labels.map { it.copy(printerBackupId = newBackupId) }
            labelDao.insertLabels(labelsWithId)

            // Insert logs with newBackupId
            val logsWithId = parsed.logs.map { it.copy(printerBackupId = newBackupId) }
            logDao.insertLogs(logsWithId)

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
