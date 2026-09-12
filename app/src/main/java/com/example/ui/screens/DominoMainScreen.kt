package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
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
    val logs by viewModel.productionLogs.collectAsStateWithLifecycle()
    val backups by viewModel.backups.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentScreenTab.collectAsStateWithLifecycle()
    val selectedPrinterId by viewModel.selectedPrinterId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedBrand by viewModel.selectedBrandFilter.collectAsStateWithLifecycle()
    val selectedWeight by viewModel.selectedWeightFilter.collectAsStateWithLifecycle()
    val inspectingLabel by viewModel.inspectingLabel.collectAsStateWithLifecycle()
    val importMessage by viewModel.importStatusMessage.collectAsStateWithLifecycle()
    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()
    val directParsedLabel by viewModel.directParsedLabel.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var showImportDialog by remember { mutableStateOf(false) }
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

    val activePrinter: PrinterBackup? = backups.find { it.id == selectedPrinterId }
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
        } else if (showImportDialog) {
            showImportDialog = false
            viewModel.clearDirectParsedLabel()
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
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (viewModel.canNavigateBack() || currentTab != 0 || selectedPrinterId != null) {
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                viewModel.handleBackNavigation()
                            },
                            modifier = Modifier.testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to previous screen"
                            )
                        }
                    }
                },
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
                                text = if (activePrinter != null) activePrinter.printerName else "All Printers",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Theme Quick Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
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
                            if (backups.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (selectedPrinterId == null) "Section: All Printers ✓" else "Section: All Printers"
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.FilterList,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        viewModel.setSelectedPrinterId(null)
                                        moreMenuExpanded = false
                                    },
                                    modifier = Modifier.testTag("switch_printer_menu_button")
                                )

                                backups.forEach { backup ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (selectedPrinterId == backup.id) "${backup.printerName} ✓" else backup.printerName
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Print,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSelectedPrinterId(backup.id)
                                            moreMenuExpanded = false
                                        }
                                    )
                                }

                                HorizontalDivider()
                            }

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
                                text = { Text("Restore Sample Data") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    moreMenuExpanded = false
                                    viewModel.resetToDefaults()
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

                // Tab 1: Production Logs
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setScreenTab(1) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (logs.isNotEmpty()) {
                                    Badge { Text("${logs.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = "Production Logs")
                        }
                    },
                    label = { Text("Logs", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_item_logs")
                )

                // Tab 2: Printer Sections
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setScreenTab(2) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (backups.isNotEmpty()) {
                                    Badge { Text("${backups.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Printers")
                        }
                    },
                    label = { Text("Printers", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_item_printers")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> LabelsScreen(
                    labels = labels,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedBrand = selectedBrand,
                    onBrandSelect = { viewModel.setBrandFilter(it) },
                    selectedWeight = selectedWeight,
                    onWeightSelect = { viewModel.setWeightFilter(it) },
                    selectedPrinter = activePrinter,
                    onLabelClick = { viewModel.inspectLabel(it) },
                    onClearFilters = { viewModel.clearFilters() },
                    onOpenImportDialog = { showImportDialog = true }
                )

                1 -> ProductionLogsScreen(
                    logs = logs,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedPrinter = activePrinter
                )

                2 -> PrinterSectionsScreen(
                    backups = backups,
                    selectedPrinterId = selectedPrinterId,
                    onSelectPrinter = { viewModel.setSelectedPrinterId(it) },
                    onDeleteBackup = { viewModel.deleteBackup(it) },
                    onViewLabelsForPrinter = { printerId ->
                        viewModel.navigateToLabelsForPrinter(printerId)
                    },
                    onOpenImportDialog = { showImportDialog = true }
                )
            }
        }

        // Label Inspection Dialog
        inspectingLabel?.let { label ->
            LabelDetailDialog(
                label = label,
                onDismiss = { viewModel.inspectLabel(null) },
                onUpdateLabel = { viewModel.updateLabel(it) }
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
