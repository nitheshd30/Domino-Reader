package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DominoDatabase
import com.example.data.local.DominoRepository
import com.example.data.model.DominoLabel
import com.example.data.model.PrinterBackup
import com.example.data.model.ProductionLog
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DominoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DominoRepository
    private val themePrefs = application.getSharedPreferences("domino_theme_prefs", android.content.Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        when (themePrefs.getString("theme_mode", "SYSTEM")) {
            "LIGHT" -> AppThemeMode.LIGHT
            "DARK" -> AppThemeMode.DARK
            else -> AppThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    init {
        val db = DominoDatabase.getDatabase(application)
        repository = DominoRepository(db, application)
        viewModelScope.launch {
            repository.ensureCleanDatabase()
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        themePrefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun toggleTheme() {
        val nextMode = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
        setThemeMode(nextMode)
    }

    // 0 = Labels & Search, 1 = Production Logs, 2 = Printer Sections
    private val _currentScreenTab = MutableStateFlow(0)
    val currentScreenTab: StateFlow<Int> = _currentScreenTab.asStateFlow()

    // null means "All Printers", otherwise specific printerBackupId
    private val _selectedPrinterId = MutableStateFlow<Long?>(null)
    val selectedPrinterId: StateFlow<Long?> = _selectedPrinterId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBrandFilter = MutableStateFlow<String?>(null)
    val selectedBrandFilter: StateFlow<String?> = _selectedBrandFilter.asStateFlow()

    private val _selectedWeightFilter = MutableStateFlow<String?>(null)
    val selectedWeightFilter: StateFlow<String?> = _selectedWeightFilter.asStateFlow()

    private val _inspectingLabel = MutableStateFlow<DominoLabel?>(null)
    val inspectingLabel: StateFlow<DominoLabel?> = _inspectingLabel.asStateFlow()

    private val _importStatusMessage = MutableStateFlow<String?>(null)
    val importStatusMessage: StateFlow<String?> = _importStatusMessage.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _directParsedLabel = MutableStateFlow<DominoLabel?>(null)
    val directParsedLabel: StateFlow<DominoLabel?> = _directParsedLabel.asStateFlow()

    val backups: StateFlow<List<PrinterBackup>> = repository.allBackups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered labels based on selected printer, search query, brand filter, weight filter
    val labels: StateFlow<List<DominoLabel>> = combine(
        repository.allLabels,
        _selectedPrinterId,
        _searchQuery,
        _selectedBrandFilter,
        _selectedWeightFilter
    ) { allLabels, printerId, query, brand, weight ->
        allLabels.filter { label ->
            val matchPrinter = printerId == null || label.printerBackupId == printerId
            val matchQuery = query.isBlank() ||
                    label.labelName.contains(query, ignoreCase = true) ||
                    label.batchNumber.contains(query, ignoreCase = true) ||
                    label.brand.contains(query, ignoreCase = true) ||
                    label.weightDetails.contains(query, ignoreCase = true) ||
                    label.mrp.contains(query, ignoreCase = true) ||
                    label.unitSalePrice.contains(query, ignoreCase = true) ||
                    label.mfgDate.contains(query, ignoreCase = true) ||
                    label.useBy.contains(query, ignoreCase = true) ||
                    label.expiryDate.contains(query, ignoreCase = true)
            val matchBrand = brand == null || label.brand.equals(brand, ignoreCase = true)
            val matchWeight = weight == null || label.weightDetails.contains(weight, ignoreCase = true)

            matchPrinter && matchQuery && matchBrand && matchWeight
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered production logs based on selected printer and search query
    val productionLogs: StateFlow<List<ProductionLog>> = combine(
        repository.allLogs,
        _selectedPrinterId,
        _searchQuery
    ) { allLogs, printerId, query ->
        allLogs.filter { log ->
            val matchPrinter = printerId == null || log.printerBackupId == printerId
            val matchQuery = query.isBlank() ||
                    log.labelName.contains(query, ignoreCase = true) ||
                    log.batchNumber.contains(query, ignoreCase = true) ||
                    log.shiftName.contains(query, ignoreCase = true) ||
                    log.operatorName.contains(query, ignoreCase = true) ||
                    log.logDate.contains(query, ignoreCase = true)

            matchPrinter && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setScreenTab(tabIndex: Int) {
        _currentScreenTab.value = tabIndex
    }

    fun setSelectedPrinterId(id: Long?) {
        _selectedPrinterId.value = id
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBrandFilter(brand: String?) {
        _selectedBrandFilter.value = if (_selectedBrandFilter.value == brand) null else brand
    }

    fun setWeightFilter(weight: String?) {
        _selectedWeightFilter.value = if (_selectedWeightFilter.value == weight) null else weight
    }

    fun inspectLabel(label: DominoLabel?) {
        _inspectingLabel.value = label
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedBrandFilter.value = null
        _selectedWeightFilter.value = null
    }

    fun clearStatusMessage() {
        _importStatusMessage.value = null
    }

    fun importBackupFile(uri: Uri, customName: String? = null) {
        viewModelScope.launch {
            _isImporting.value = true
            try {
                val backup = repository.importBackupFromUri(uri, customName)
                _importStatusMessage.value = "Imported '${backup.printerName}' with ${backup.totalLabelsCount} labels!"
                // Automatically switch to the newly imported printer
                _selectedPrinterId.value = backup.id
                _currentScreenTab.value = 0 // Show labels
            } catch (e: Exception) {
                _importStatusMessage.value = "Import failed: ${e.localizedMessage ?: "Invalid file format"}"
            } finally {
                _isImporting.value = false
            }
        }
    }

    fun updateLabel(label: DominoLabel) {
        viewModelScope.launch {
            repository.updateLabel(label)
            if (_inspectingLabel.value?.id == label.id) {
                _inspectingLabel.value = label
            }
        }
    }

    fun deleteBackup(backup: PrinterBackup) {
        viewModelScope.launch {
            repository.deleteBackup(backup)
            if (_selectedPrinterId.value == backup.id) {
                _selectedPrinterId.value = null
            }
            _importStatusMessage.value = "Deleted backup '${backup.printerName}'"
        }
    }

    fun addManualPrinterBackup(name: String, model: String, location: String) {
        viewModelScope.launch {
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            val backup = PrinterBackup(
                printerName = name,
                printerModel = model,
                serialNumber = "AX-" + model.takeLast(4) + "-" + (1000..9999).random(),
                lineLocation = location,
                backupDate = dateStr,
                totalLabelsCount = 0,
                totalPacksPrinted = 0,
                status = "Active"
            )
            val newId = repository.insertBackup(backup)
            _selectedPrinterId.value = newId
            _importStatusMessage.value = "Created new printer section '$name'"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedPrinterId.value = null
            _inspectingLabel.value = null
            _directParsedLabel.value = null
            _searchQuery.value = ""
            _selectedBrandFilter.value = null
            _selectedWeightFilter.value = null
            _importStatusMessage.value = "All entries cleared. Database is now empty."
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetDatabaseToDefaults()
            _importStatusMessage.value = "Sample printer backups & labels restored"
        }
    }

    fun parseDirectLblFile(uri: Uri) {
        viewModelScope.launch {
            _isImporting.value = true
            try {
                val label = repository.parseDirectLabelFromUri(uri)
                _directParsedLabel.value = label
                _importStatusMessage.value = "Successfully read '${label.fileName}' - Batch: ${label.batchNumber}"
            } catch (e: Exception) {
                _importStatusMessage.value = "Error reading .lbl: ${e.localizedMessage}"
            } finally {
                _isImporting.value = false
            }
        }
    }

    fun parseDirectLblText(text: String, customName: String = "Direct Label") {
        try {
            val label = com.example.data.local.DominoBackupParser.parseLabelFromText(
                text = text,
                defaultName = customName,
                index = 0,
                targetBackupId = _selectedPrinterId.value ?: 1L
            )
            _directParsedLabel.value = label
            _importStatusMessage.value = "Parsed .lbl content - Batch: ${label.batchNumber}"
        } catch (e: Exception) {
            _importStatusMessage.value = "Error parsing text: ${e.localizedMessage}"
        }
    }

    fun clearDirectParsedLabel() {
        _directParsedLabel.value = null
    }

    fun saveDirectLabelToActivePrinter(label: DominoLabel) {
        viewModelScope.launch {
            var targetPrinterId = _selectedPrinterId.value ?: backups.value.firstOrNull()?.id
            if (targetPrinterId == null) {
                // If no printer exists yet in the empty app, automatically create a primary printer section
                val newPrinter = PrinterBackup(
                    printerName = "Domino Ax Conveyor Line",
                    printerModel = "Ax350i",
                    serialNumber = "AX-350-001",
                    lineLocation = "Primary Line",
                    backupDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                    totalLabelsCount = 0,
                    totalPacksPrinted = 0,
                    status = "Active"
                )
                targetPrinterId = repository.insertBackup(newPrinter)
                _selectedPrinterId.value = targetPrinterId
            }
            val labelToSave = label.copy(id = 0, printerBackupId = targetPrinterId)
            repository.insertLabel(labelToSave)
            _importStatusMessage.value = "Saved '${label.labelName}' to printer section!"
            _inspectingLabel.value = labelToSave
            _directParsedLabel.value = null
            _currentScreenTab.value = 0
        }
    }
}
