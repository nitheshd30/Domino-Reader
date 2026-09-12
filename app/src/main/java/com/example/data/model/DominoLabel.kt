package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(
    tableName = "domino_labels",
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
        Index("labelName"),
        Index("batchNumber")
    ]
)
data class DominoLabel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val printerBackupId: Long,
    val fileName: String, // e.g. "Bolas pista salted 200g (ps200).lbl"
    val labelName: String, // e.g. "Bolas pista salted 200g (ps200)"
    val brand: String, // e.g. "Bolas", "Tata", "Molsis", "Runutz", "Vedaka"
    val productCategory: String, // "Walnuts", "Pista", "Cashew", "Almonds", "Honey", "Sweets", "Seeds"
    val batchNumber: String, // e.g. "IPRS026"
    val mfgDate: String, // e.g. "10/09/2026"
    val useBy: String, // e.g. "09/06/2026"
    val mrp: String, // e.g. "475.00"
    val expiryDate: String = "", // e.g. "09/06/2026"
    val weightDetails: String, // e.g. "200g"
    val unitSalePrice: String = "", // e.g. "(USP ₹ 2.38/g)"
    val rasterDropSize: String = "16 Drop (100mm 25)",
    val associatedImage: String = "", // e.g. "BOLAS NEW.bmp", "RS.bmp"
    val barcodeData: String = "", // e.g. "8906012345678"
    val rawLabelContent: String = "",
    val printCount: Long = 0,
    val isFavorite: Boolean = false
) {
    fun getEffectiveUsp(): String {
        if (unitSalePrice.isNotBlank()) {
            return if (unitSalePrice.startsWith("(") && unitSalePrice.endsWith(")")) unitSalePrice
            else if (unitSalePrice.startsWith("USP", ignoreCase = true)) "($unitSalePrice)"
            else "(USP $unitSalePrice)"
        }
        val mrpVal = mrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return ""
        val weightVal = weightDetails.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return ""
        if (weightVal <= 0.0) return ""
        val isKg = weightDetails.contains("kg", ignoreCase = true)
        val weightG = if (isKg) weightVal * 1000.0 else weightVal
        val perG = mrpVal / weightG
        val formatted = String.format(Locale.US, "₹ %.2f/g", perG)
        return "(USP $formatted)"
    }

    fun getDisplayMrp(): String {
        val cleanMrp = mrp.trim()
        val usp = getEffectiveUsp()
        return if (cleanMrp.contains("USP", ignoreCase = true)) {
            cleanMrp
        } else if (usp.isNotBlank()) {
            "$cleanMrp $usp"
        } else {
            cleanMrp
        }
    }
}

