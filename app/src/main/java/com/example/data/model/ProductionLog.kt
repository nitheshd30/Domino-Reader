package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "production_logs",
    foreignKeys = [
        ForeignKey(
            entity = PrinterBackup::class,
            parentColumns = ["id"],
            childColumns = ["printerBackupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("printerBackupId"),
        Index("logDate"),
        Index("batchNumber")
    ]
)
data class ProductionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val printerBackupId: Long = 0,
    val labelName: String = "",
    val batchNumber: String = "",
    val shiftName: String = "", // e.g. "Shift A (06:00 - 14:00)"
    val logDate: String = "", // e.g. "2026-09-11"
    val startTime: String = "", // e.g. "06:15"
    val endTime: String = "", // e.g. "13:45"
    val packsPrinted: Int = 0,
    val packsRejected: Int = 0,
    val lineSpeedMPerMin: Double = 45.0, // Packaging conveyor line speed
    val inkPressureBar: Double = 2.85, // Domino CIJ operating pressure ~2.8 - 3.0 bar
    val viscosityCps: Double = 4.2, // Ink viscosity in cPs
    val inkLevelPercent: Int = 88,
    val makeupLevelPercent: Int = 74,
    val operatorName: String = "Line Operator",
    val status: String = "Completed", // "Completed", "In Progress", "Warning", "Alarm"
    val notes: String = ""
)
