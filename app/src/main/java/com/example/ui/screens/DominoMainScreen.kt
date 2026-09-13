package com.example.ui.screens

import androidx.compose.animation.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import com.example.ui.components.AddEditConsumableDialog
import com.example.ui.theme.DominoAmber
import com.example.ui.theme.DominoGreen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.ui.theme.AppThemeMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PrinterBackup
import com.example.ui.theme.DominoCyan
import com.example.ui.viewmodel.DominoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DominoMainScreen(
    viewModel: DominoViewModel,
    modifier: Modifier = Modifier
) {
    val labels by viewModel.labels.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentScreenTab.collectAsStateWithLifecycle()
    val selectedPrinterId by viewModel.selectedPrinterId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedBrand by viewModel.selectedBrandFilter.collectAsStateWithLifecycle()
    val selectedWeight by viewModel.selectedWeightFilter.collectAsStateWithLifecycle()
    val inspectingLabel by viewModel.inspectingLabel.collectAsStateWithLifecycle()
    val showAddEditLabelDialog by viewModel.showAddEditLabelDialog.collectAsStateWithLifecycle()
    val editingLabel by viewModel.editingLabel.collectAsStateWithLifecycle()
    val importMessage by viewModel.importStatusMessage.collectAsStateWithLifecycle()
    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()
    val directParsedLabel by viewModel.directParsedLabel.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    // Consumables stock states
    val consumables by viewModel.consumables.collectAsStateWithLifecycle()
    val allConsumablesRaw by viewModel.allConsumablesRaw.collectAsStateWithLifecycle()
    val selectedConsumableCat by viewModel.selectedConsumableCategory.collectAsStateWithLifecycle()
    val consumableSearchQuery by viewModel.consumableSearchQuery.collectAsStateWithLifecycle()
    val filterLowStockOnly by viewModel.filterLowStockOnly.collectAsStateWithLifecycle()
    val consumableSortOption by viewModel.consumableSortOption.collectAsStateWithLifecycle()
    val editingConsumable by viewModel.editingConsumable.collectAsStateWithLifecycle()
    val showAddEditConsumableDialog by viewModel.showAddEditConsumableDialog.collectAsStateWithLifecycle()

    var showImportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var printerDropdownExpanded by remember { mutableStateOf(false) }
    var moreMenuExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val systemDark = isSystemInDarkTheme()
    val isCurrentlyDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // Intercept system back gestures and back button:
    // 1. Closes any open menus or dialogs
    // 2. Navigates back screen/tab/filter-wise
    // 3. Double-tap to exit only at root screen to prevent accidental closing
    BackHandler(enabled = true) {
        focusManager.clearFocus()
        keyboardController?.hide()
        val currentTime = System.currentTimeMillis()
        if (moreMenuExpanded) {
            moreMenuExpanded = false
        } else if (printerDropdownExpanded) {
            printerDropdownExpanded = false
        } else if (showThemeDialog) {
            showThemeDialog = false
        } else if (showClearConfirmDialog) {
            showClearConfirmDialog = false
        } else if (showAddEditConsumableDialog) {
            viewModel.closeAddEditConsumable()
        } else if (showImportDialog) {
            showImportDialog = false
            viewModel.clearDirectParsedLabel()
        } else if (inspectingLabel != null) {
            viewModel.inspectLabel(null)
        } else {
            val handled = viewModel.handleBackNavigation()
            if (!handled) {
                if (currentTime - lastBackPressTime < 2000L) {
                    (context as? android.app.Activity)?.finish()
                } else {
                    lastBackPressTime = currentTime
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(importMessage) {
        importMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("domino_main_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentTab == 0) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { viewModel.openAddLabel() },
                    containerColor = com.example.ui.theme.DominoCyan
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Label", tint = androidx.compose.ui.graphics.Color.Black)
                }
            }
        },
        topBar = {
            TopAppBar(
                                title = {
                    Column {
                        Text(
                            text = "Domino Ax Reader",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = DominoCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "CIJ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DominoCyan,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cloud Synced",
                                fontSize = 11.sp,
                                color = DominoGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Theme Quick Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleTheme(isCurrentlyDark) },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isCurrentlyDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isCurrentlyDark) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isCurrentlyDark) DominoCyan else MaterialTheme.colorScheme.primary
                        )
                    }

                    // More options menu (Theme settings, Clear All Data, Import, etc.)
                    Box {
                        IconButton(
                            onClick = { moreMenuExpanded = true },
                            modifier = Modifier.testTag("more_options_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }

                        DropdownMenu(
                            expanded = moreMenuExpanded,
                            onDismissRequest = { moreMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Read .LBL File") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    moreMenuExpanded = false
                                    showImportDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Import Backup (.ZIP)") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    moreMenuExpanded = false
                                    showImportDialog = true
                                },
                                modifier = Modifier.testTag("top_import_backup_button")
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (themeMode) {
                                            AppThemeMode.LIGHT -> "Theme: Light ☀️"
                                            AppThemeMode.DARK -> "Theme: Dark 🌙"
                                            AppThemeMode.SYSTEM -> "Theme: System ⚙️"
                                        }
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    moreMenuExpanded = false
                                    showThemeDialog = true
                                }
                            )

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Clear All Entries",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    moreMenuExpanded = false
                                    showClearConfirmDialog = true
                                }
                            )



                            DropdownMenuItem(
                                text = { Text("About") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    moreMenuExpanded = false
                                    showAboutDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Tab 0: Labels & Search
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setScreenTab(0) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (labels.isNotEmpty()) {
                                    Badge { Text("${labels.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Description, contentDescription = "Labels")
                        }
                    },
                    label = { Text("Labels", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_item_labels")
                )

                // Tab 1: Calculators
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setScreenTab(1) },
                    icon = {
                        Icon(Icons.Default.Calculate, contentDescription = "Calculators")
                    },
                    label = { Text("Calculators", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_item_calculators")
                )

                // Tab 2: Consumables Stock (Ink, Make-up, Wash, Filter, ITM)
                val lowStockTotal = allConsumablesRaw.count { it.isLowStock || it.isOutOfStock }
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setScreenTab(2) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (lowStockTotal > 0) {
                                    Badge(containerColor = DominoAmber) { Text("$lowStockTotal") }
                                } else if (allConsumablesRaw.isNotEmpty()) {
                                    Badge { Text("${allConsumablesRaw.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Inventory2, contentDescription = "Consumables Stock")
                        }
                    },
                    label = { Text("Stock", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_item_stock")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            androidx.compose.animation.AnimatedContent(
                targetState = currentTab,
                label = "TabSwitchAnimation",
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (androidx.compose.animation.slideInHorizontally { width -> direction * width } + androidx.compose.animation.fadeIn()) togetherWith 
                    (androidx.compose.animation.slideOutHorizontally { width -> -direction * width } + androidx.compose.animation.fadeOut())
                }
            ) { targetTab ->
                when (targetTab) {
                    0 -> LabelsScreen(
                        labels = labels,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        selectedBrand = selectedBrand,
                        onBrandSelect = { viewModel.setBrandFilter(it) },
                        selectedWeight = selectedWeight,
                        onWeightSelect = { viewModel.setWeightFilter(it) },
                        onLabelClick = { viewModel.inspectLabel(it) },
                        onClearFilters = { viewModel.clearFilters() },
                        onOpenImportDialog = { showImportDialog = true }
                    )

                    1 -> CalculatorsScreen()

                    2 -> ConsumablesScreen(
                        consumables = consumables,
                        allConsumables = allConsumablesRaw,
                        selectedCategory = selectedConsumableCat,
                        onCategorySelect = { viewModel.setConsumableCategory(it) },
                        searchQuery = consumableSearchQuery,
                        onSearchQueryChange = { viewModel.setConsumableSearchQuery(it) },
                        isLowStockOnly = filterLowStockOnly,
                        onToggleLowStockOnly = { viewModel.toggleFilterLowStockOnly() },
                        sortOption = consumableSortOption,
                        onSortOptionChange = { viewModel.setConsumableSortOption(it) },
                        onIncreaseStock = { viewModel.adjustConsumableStock(it, 1) },
                        onDecreaseStock = { viewModel.adjustConsumableStock(it, -1) },
                        onAddConsumable = { viewModel.openAddConsumable() },
                        onEditConsumable = { viewModel.openEditConsumable(it) },
                        onDeleteConsumable = { viewModel.deleteConsumable(it) },
                        onRestoreDefaults = { viewModel.seedDefaultConsumables() }
                    )
                }
            }
        }

        
        // Add/Edit Label Dialog
        if (showAddEditLabelDialog) {
            AddEditLabelDialog(
                initialLabel = editingLabel,
                printerId = viewModel.selectedPrinterId.collectAsStateWithLifecycle().value ?: 1L,
                onDismiss = { viewModel.closeAddEditLabel() },
                onSave = { label -> viewModel.saveLabel(label) }
            )
        }

        // Label Inspection Dialog
        inspectingLabel?.let { label ->
            LabelDetailDialog(
                label = label,
                onDismiss = { viewModel.inspectLabel(null) },
                onUpdateLabel = { viewModel.updateLabel(it) },
                onEdit = { viewModel.openEditLabel(label) }
            )
        }

        // Import Backup Dialog
        if (showImportDialog) {
            ImportBackupDialog(
                isImporting = isImporting,
                onDismiss = {
                    showImportDialog = false
                    viewModel.clearDirectParsedLabel()
                },
                onImportFile = { uri, name ->
                    viewModel.importBackupFile(uri, name)
                },
                onAddManualPrinter = { name, model, loc ->
                    viewModel.addManualPrinterBackup(name, model, loc)
                },
                onDirectParseFile = { uri ->
                    viewModel.parseDirectLblFile(uri)
                },
                onDirectParseText = { text, name ->
                    viewModel.parseDirectLblText(text, name)
                },
                directParsedLabel = directParsedLabel,
                onSaveDirectLabel = { label ->
                    viewModel.saveDirectLabelToActivePrinter(label)
                },
                onClearDirectLabel = {
                    viewModel.clearDirectParsedLabel()
                }
            )
        }

        // Add / Edit Consumable Dialog
        if (showAddEditConsumableDialog) {
            AddEditConsumableDialog(
                initialItem = editingConsumable,
                onDismiss = { viewModel.closeAddEditConsumable() },
                onSave = { viewModel.saveConsumable(it) }
            )
        }


        // About Dialog
        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false }
            )
        }

        // Clear All Entries Confirmation Dialog
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Clear All Entries?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "This will wipe all existing labels, printer lines, and production logs. The app will be completely empty and ready for your fresh Domino Ax .lbl files or backup imports.",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData()
                            showClearConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Clear Everything")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Theme Selection Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Theme Appearance",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select your preferred industrial interface theme:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        listOf(
                            Triple(AppThemeMode.LIGHT, "Light Mode ☀️", "Crisp daylight styling with clean high-contrast elements"),
                            Triple(AppThemeMode.DARK, "Dark Mode 🌙", "Deep industrial low-glare dark styling with CIJ glow"),
                            Triple(AppThemeMode.SYSTEM, "System Default ⚙️", "Automatically matches your device system appearance")
                        ).forEach { (mode, label, description) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = themeMode == mode,
                                        onClick = {
                                            viewModel.setThemeMode(mode)
                                            showThemeDialog = false
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = themeMode == mode,
                                    onClick = {
                                        viewModel.setThemeMode(mode)
                                        showThemeDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = label,
                                        fontWeight = if (themeMode == mode) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
