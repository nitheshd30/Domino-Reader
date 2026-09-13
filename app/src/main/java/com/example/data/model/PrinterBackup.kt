package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "printer_backups")
data class PrinterBackup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val printerName: String = "",
    val printerModel: String = "Ax350i", // e.g. Ax350i, Ax150i, Ax550i
    val serialNumber: String = "",
    val lineLocation: String = "", // e.g. Packaging Line 1, Sweets Line, etc.
    val firmwareVersion: String = "QuickStep v5.4.1",
    val backupDate: String = "", // formatted date e.g. "2026-09-11 10:30"
    val totalLabelsCount: Int = 0,
    val totalPacksPrinted: Long = 0,
    val status: String = "Active", // "Active", "Standby", "Archived"
    val nozzleSizeDrop: String = "16 Drop / 60μm",
    val inkType: String = "2BK001 Black Ketone",
    val notes: String = ""
)
