package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DominoLabel
import com.example.ui.theme.InkjetAmberDot
import com.example.ui.theme.InkjetBlackSubstrate
import com.example.ui.theme.InkjetCyanDot
import com.example.ui.theme.InkjetGreenDot

import com.example.data.model.LabelFormatType

@Composable
fun PrintheadSimulationView(
    label: DominoLabel,
    modifier: Modifier = Modifier,
    activeFormat: LabelFormatType = LabelFormatType.fromId(label.formatType),
    overrideBatch: String? = null,
    overrideMfd: String? = null,
    overrideUseBy: String? = null,
    overrideMrp: String? = null,
    overrideUsp: String? = null,
    overrideWeight: String? = null,
    customLines: List<String>? = null
) {
    var selectedInkColor by remember { mutableStateOf(InkjetCyanDot) }

    val printLines = remember(
        label, activeFormat, overrideBatch, overrideMfd,
        overrideUseBy, overrideMrp, overrideUsp, customLines
    ) {
        label.getPrintLines(
            overrideFormat = activeFormat,
            overrideBatch = overrideBatch,
            overrideMfd = overrideMfd,
            overrideUseBy = overrideUseBy,
            overrideMrp = overrideMrp,
            overrideUsp = overrideUsp,
            customLines = customLines
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("printhead_simulation_view"),
        shape = RoundedCornerShape(12.dp),
        color = InkjetBlackSubstrate,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "CIJ Printhead",
                        tint = selectedInkColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DOMINO Ax CIJ PRINT STREAM",
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Format badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFF222831), RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color(0xFF444444), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = activeFormat.shortBadge,
                        color = Color(0xFF64B5F6),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inkjet Dot Matrix Display Area (Substrate)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07090C), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    printLines.forEach { line ->
                        Text(
                            text = line,
                            color = selectedInkColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.4.sp,
                            lineHeight = 20.sp
                        )
                    }

                    if (label.associatedImage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "[LOGO: ${label.associatedImage}]",
                            color = selectedInkColor.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ink drop color selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ink Tone Preview:",
                    color = Color(0xFF888888),
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InkColorDot(
                        color = InkjetCyanDot,
                        isSelected = selectedInkColor == InkjetCyanDot,
                        onClick = { selectedInkColor = InkjetCyanDot }
                    )
                    InkColorDot(
                        color = InkjetAmberDot,
                        isSelected = selectedInkColor == InkjetAmberDot,
                        onClick = { selectedInkColor = InkjetAmberDot }
                    )
                    InkColorDot(
                        color = InkjetGreenDot,
                        isSelected = selectedInkColor == InkjetGreenDot,
                        onClick = { selectedInkColor = InkjetGreenDot }
                    )
                    InkColorDot(
                        color = Color.White,
                        isSelected = selectedInkColor == Color.White,
                        onClick = { selectedInkColor = Color.White }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrintMatrixLine(
    prefix: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            color = color.copy(alpha = 0.65f),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            letterSpacing = 1.2.sp
        )
        Text(
            text = value,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun InkColorDot(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = color,
        modifier = Modifier
            .size(18.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .testTag("ink_color_${color.value}")
    ) {}
}
