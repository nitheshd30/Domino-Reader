package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DominoLabel
import com.example.data.model.LabelFormatType

enum class QuickStepDisplayMode {
    PRINTER_SCREEN,  // Exact replica of Domino Ax QuickStep LCD screen from photos
    INKJET_SUBSTRATE // CIJ Continuous Inkjet print on packaging substrate
}

/**
 * High-fidelity Domino Ax QuickStep UI Screen Simulation.
 * Accurately models the exact layout, header bar, navigation arrows,
 * dot-matrix line renderer, and file path footer from the user's printer images.
 */
@Composable
fun DominoQuickStepScreenView(
    label: DominoLabel,
    modifier: Modifier = Modifier,
    activeFormat: LabelFormatType = LabelFormatType.fromId(label.formatType),
    overrideBatch: String? = null,
    overrideMfd: String? = null,
    overrideUseBy: String? = null,
    overrideMrp: String? = null,
    overrideUsp: String? = null,
    customLines: List<String>? = null,
    onFormatChange: ((LabelFormatType) -> Unit)? = null
) {
    var displayMode by remember { mutableStateOf(QuickStepDisplayMode.PRINTER_SCREEN) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Print, 1 = Production, 2 = Setup
    var isPrinterRunning by remember { mutableStateOf(true) }

    val printLines = label.getPrintLines(
        overrideFormat = activeFormat,
        overrideBatch = overrideBatch,
        overrideMfd = overrideMfd,
        overrideUseBy = overrideUseBy,
        overrideMrp = overrideMrp,
        overrideUsp = overrideUsp,
        customLines = customLines
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("domino_quickstep_screen_view"),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E222B), // Industrial dark casing of Domino machine
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF333B4A)),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // ================= 1. DOMINO QUICKSTEP TOP HEADER BAR =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF13171F))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Head / Drop Counter Icon (e.g. 2860 or 1265)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp))
                ) {
                    Surface(
                        color = Color(0xFF262D3D),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Head drops",
                                tint = Color(0xFF4FC3F7),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = label.printerHeadCode.ifBlank { "2860" },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Center-Left: START BUTTON
                Surface(
                    onClick = { isPrinterRunning = true },
                    shape = RoundedCornerShape(6.dp),
                    color = if (isPrinterRunning) Color(0xFF1E3A2F) else Color(0xFF2C3240),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPrinterRunning) Color(0xFF2E7D32) else Color(0xFF424B5D)
                    ),
                    modifier = Modifier.testTag("quickstep_start_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = if (isPrinterRunning) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "START",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPrinterRunning) Color(0xFF81C784) else Color(0xFFCFD8DC)
                        )
                    }
                }

                // Center: STATUS PILL (Green "STATUS" with "Ready" underneath, matching real screen)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isPrinterRunning) Color(0xFF1B5E20) else Color(0xFF5D4037))
                        .border(
                            1.dp,
                            if (isPrinterRunning) Color(0xFF4CAF50) else Color(0xFFFFB74D),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "STATUS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPrinterRunning) Color(0xFFA5D6A7) else Color(0xFFFFCC80),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (isPrinterRunning) "Ready" else "Standby",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // Center-Right: STOP BUTTON
                Surface(
                    onClick = { isPrinterRunning = false },
                    shape = RoundedCornerShape(6.dp),
                    color = if (!isPrinterRunning) Color(0xFF3E2723) else Color(0xFF2C3240),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!isPrinterRunning) Color(0xFFE53935) else Color(0xFF424B5D)
                    ),
                    modifier = Modifier.testTag("quickstep_stop_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = if (!isPrinterRunning) Color(0xFFEF5350) else Color(0xFFB0BEC5),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "STOP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isPrinterRunning) Color(0xFFFF8A80) else Color(0xFFCFD8DC)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= 2. TOP NAVIGATION ARROWS (as seen in photo 1 & 2) =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Arrow [ < ]
                Surface(
                    onClick = { /* Navigation step */ },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF333B4A),
                    modifier = Modifier
                        .width(60.dp)
                        .height(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous message",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Active Format Indicator Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF282E3D),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A5568))
                ) {
                    Text(
                        text = activeFormat.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64B5F6),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                // Right Arrow [ > ]
                Surface(
                    onClick = { /* Navigation step */ },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF333B4A),
                    modifier = Modifier
                        .width(60.dp)
                        .height(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next message",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ================= 3. MAIN PREVIEW CANVAS =================
            val isScreenMode = displayMode == QuickStepDisplayMode.PRINTER_SCREEN
            val canvasBackground = if (isScreenMode) Color(0xFFECEFF1) else Color(0xFF0A0D12)
            val inkColor = if (isScreenMode) Color(0xFF1A1A1A) else Color(0xFF00E5FF)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(canvasBackground)
                    .border(
                        width = 1.5.dp,
                        color = if (isScreenMode) Color(0xFFB0BEC5) else Color(0xFF00E5FF).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy((4 * zoomScale).dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    printLines.forEachIndexed { index, lineText ->
                        Text(
                            text = lineText,
                            color = inkColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = (15 * zoomScale).sp,
                            letterSpacing = 1.6.sp,
                            lineHeight = (22 * zoomScale).sp
                        )
                    }
                }
            }

            // ================= 4. CANVAS FOOTER (Path & QuickStep Controls) =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Real File Path as shown on Domino screen
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ".../Labels/${label.fileName}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF90A4AE),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right: QuickStep Action Buttons (Document, Zoom In, Zoom Out)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Document icon
                    Surface(
                        onClick = { },
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2B3242),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Label file",
                                tint = Color(0xFFB0BEC5),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Zoom In
                    Surface(
                        onClick = { if (zoomScale < 1.4f) zoomScale += 0.15f },
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2B3242),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Zoom Out
                    Surface(
                        onClick = { if (zoomScale > 0.85f) zoomScale -= 0.15f },
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2B3242),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // ================= 5. MACHINE BOTTOM NAVIGATION BAR =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF13171F))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    Triple(0, "Print", Icons.Default.Print),
                    Triple(1, "Production line", Icons.Default.PrecisionManufacturing),
                    Triple(2, "Setup", Icons.Default.Settings)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    tabs.forEach { (index, title, icon) ->
                        val isSelected = activeTab == index
                        Surface(
                            onClick = { activeTab = index },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Color(0xFF283548) else Color.Transparent
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (isSelected) Color(0xFF64B5F6) else Color(0xFF78909C),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF90A4AE)
                                )
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF546E7A),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= 6. FORMAT PRESET SWITCHER CHIPS =================
            Text(
                text = "LABEL PRINTING FORMAT PRESETS:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF90A4AE),
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FormatChip(
                    title = "Bolas Std",
                    subtitle = "ICH026 (DD/MM/YYYY)",
                    isSelected = activeFormat == LabelFormatType.BOLAS_STANDARD,
                    onClick = { onFormatChange?.invoke(LabelFormatType.BOLAS_STANDARD) },
                    modifier = Modifier.weight(1f)
                )

                FormatChip(
                    title = "Bolas Box",
                    subtitle = "IARS026 (Mon.YYYY)",
                    isSelected = activeFormat == LabelFormatType.BOLAS_BOX,
                    onClick = { onFormatChange?.invoke(LabelFormatType.BOLAS_BOX) },
                    modifier = Modifier.weight(1f)
                )

                FormatChip(
                    title = "Tata Style",
                    subtitle = "MRP First (DD/MM/YY)",
                    isSelected = activeFormat == LabelFormatType.TATA_STYLE,
                    onClick = { onFormatChange?.invoke(LabelFormatType.TATA_STYLE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FormatChip(
                    title = "Prefixed Stream",
                    subtitle = "BATCH NO: ...",
                    isSelected = activeFormat == LabelFormatType.PREFIXED,
                    onClick = { onFormatChange?.invoke(LabelFormatType.PREFIXED) },
                    modifier = Modifier.weight(1f)
                )

                FormatChip(
                    title = "Custom Format",
                    subtitle = "Edit lines freely",
                    isSelected = activeFormat == LabelFormatType.CUSTOM,
                    onClick = { onFormatChange?.invoke(LabelFormatType.CUSTOM) },
                    modifier = Modifier.weight(1f)
                )

                // Substrate / LCD toggle button
                Surface(
                    onClick = {
                        displayMode = if (displayMode == QuickStepDisplayMode.PRINTER_SCREEN) {
                            QuickStepDisplayMode.INKJET_SUBSTRATE
                        } else {
                            QuickStepDisplayMode.PRINTER_SCREEN
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF263238),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF455A64)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isScreenMode) "LCD Screen" else "Substrate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80D8FF)
                        )
                        Text(
                            text = "Toggle View",
                            fontSize = 9.sp,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormatChip(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFF1E3A5F) else Color(0xFF1D222B)
    val borderColor = if (isSelected) Color(0xFF42A5F5) else Color(0xFF374151)
    val textColor = if (isSelected) Color(0xFF90CAF9) else Color(0xFFB0BEC5)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 8.5.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF78909C),
                maxLines = 1
            )
        }
    }
}
