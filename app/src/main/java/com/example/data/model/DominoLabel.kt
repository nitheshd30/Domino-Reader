package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Locale

enum class LabelFormatType(
    val id: String,
    val title: String,
    val shortBadge: String,
    val description: String
) {
    SINGLE_LINE(
        id = "SINGLE_LINE",
        title = "Single Line Program (1 Line)",
        shortBadge = "1 Line",
        description = "Single line CIJ stream directly matching printer screen"
    ),
    TWO_LINE(
        id = "TWO_LINE",
        title = "2-Line Program",
        shortBadge = "2 Lines",
        description = "Line 1: Batch & MFD • Line 2: EXP & MRP"
    ),
    THREE_LINE(
        id = "THREE_LINE",
        title = "3-Line Program",
        shortBadge = "3 Lines",
        description = "Line 1: Batch • Line 2: MFD & EXP • Line 3: MRP (USP)"
    ),
    BOLAS_STANDARD(
        id = "BOLAS_STANDARD",
        title = "Bolas Standard (DD/MM/YYYY)",
        shortBadge = "Standard 4L",
        description = "Line 1: Batch • Line 2: MFD (DD/MM/YYYY) • Line 3: EXP (DD/MM/YYYY) • Line 4: MRP (USP)"
    ),
    BOLAS_BOX(
        id = "BOLAS_BOX",
        title = "Bolas Box (Month.Year)",
        shortBadge = "Box 4L",
        description = "Line 1: Batch • Line 2: MFD (Mon.YYYY) • Line 3: EXP (Mon.YYYY) • Line 4: MRP (USP)"
    ),
    TATA_STYLE(
        id = "TATA_STYLE",
        title = "Tata Style (MRP First)",
        shortBadge = "Tata 4L",
        description = "Line 1: MRP(USP) • Line 2: MFD (DD/MM/YY) • Line 3: EXP (DD/MM/YY) • Line 4: Batch"
    ),
    PREFIXED(
        id = "PREFIXED",
        title = "Prefixed CIJ Stream",
        shortBadge = "Prefixed 4L",
        description = "Line 1: BATCH NO : ... • Line 2: DATE OF MFG : ... • Line 3: USE BY : ... • Line 4: MRP : ..."
    ),
    CUSTOM(
        id = "CUSTOM",
        title = "Custom Line Format",
        shortBadge = "Custom",
        description = "User-defined continuous inkjet lines directly matching printer screen"
    );

    companion object {
        fun fromId(id: String?): LabelFormatType {
            if (id == null) return BOLAS_STANDARD
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: BOLAS_STANDARD
        }
    }
}

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
    val printerBackupId: Long = 0,
    val fileName: String = "", // e.g. "Bolas pista salted 200g (ps200).lbl"
    val labelName: String = "", // e.g. "Bolas pista salted 200g (ps200)"
    val brand: String = "", // e.g. "Bolas", "Tata", "Molsis", "Runutz", "Vedaka"
    val productCategory: String = "", // "Walnuts", "Pista", "Cashew", "Almonds", "Honey", "Sweets", "Seeds"
    val batchNumber: String = "", // e.g. "IPRS026", "B06H2735D1", "ICH026"
    val mfgDate: String = "", // e.g. "10/09/2026", "27/08/26", "Sep.2026"
    val useBy: String = "", // e.g. "09/06/2026", "26/08/27", "May.2027"
    val mrp: String = "", // e.g. "475.00", "830"
    val expiryDate: String = "", // e.g. "09/06/2026"
    val weightDetails: String = "", // e.g. "200g"
    val unitSalePrice: String = "", // e.g. "(USP ₹ 2.38/g)" or "(₹1.66/g)"
    val rasterDropSize: String = "16 Drop (100mm 25)",
    val associatedImage: String = "", // e.g. "BOLAS NEW.bmp", "RS.bmp"
    val barcodeData: String = "", // e.g. "8906012345678"
    val rawLabelContent: String = "",
    val printCount: Long = 0,
    val isFavorite: Boolean = false,
    val formatType: String = LabelFormatType.BOLAS_STANDARD.id,
    val printerHeadCode: String = "2860", // e.g. "2860", "1265"
    val customLine1: String = "",
    val customLine2: String = "",
    val customLine3: String = "",
    val customLine4: String = ""
) {
    fun getDisplayFileName(): String {
        val candidate = when {
            fileName.isNotBlank() && !fileName.contains("xmlns", ignoreCase = true) && !fileName.contains("<") -> fileName
            labelName.isNotBlank() && !labelName.contains("xmlns", ignoreCase = true) && !labelName.contains("<") -> labelName
            else -> {
                val full = "$fileName $labelName"
                Regex("""\bname="([^"]+)"""", RegexOption.IGNORE_CASE).find(full)?.groupValues?.get(1)
                    ?: "DominoLabel.lbl"
            }
        }
        val clean = candidate.substringAfterLast("/").substringAfterLast("\\").trim()
        val sanitized = clean.replace(Regex("""^[<"'\s]+|[>"'\s]+$"""), "")
        return if (sanitized.endsWith(".lbl", ignoreCase = true) || sanitized.endsWith(".lnl", ignoreCase = true)) {
            sanitized
        } else {
            "$sanitized.lbl"
        }
    }

    fun getCleanDisplayName(): String {
        return getDisplayFileName()
    }

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

    /**
     * Computes the exact lines that appear on the Domino Ax printer screen
     * depending on the active label format profile (Single Line, 2-Line, 3-Line, 4-Line).
     */
    fun getPrintLines(
        overrideFormat: LabelFormatType? = null,
        overrideBatch: String? = null,
        overrideMfd: String? = null,
        overrideUseBy: String? = null,
        overrideMrp: String? = null,
        overrideUsp: String? = null,
        customLines: List<String>? = null
    ): List<String> {
        val activeFormat = overrideFormat ?: LabelFormatType.fromId(formatType)

        // 1. If explicit custom lines provided (e.g. from live editor preview)
        if (customLines != null) {
            val nonBlank = customLines.filter { it.isNotBlank() }
            if (nonBlank.isNotEmpty()) return nonBlank
        }

        // Stored non-blank custom lines from the entity (e.g. read from .lbl file)
        val storedLines = listOf(customLine1, customLine2, customLine3, customLine4).filter { it.isNotBlank() }

        val activeBatch = (overrideBatch ?: batchNumber).trim()
        val rawMfd = (overrideMfd ?: mfgDate).trim()
        val rawExp = (overrideUseBy ?: useBy.ifBlank { expiryDate }).trim()
        val rawMrp = (overrideMrp ?: mrp).replace("Rs.", "").replace("₹", "").trim()
        val rawUsp = (overrideUsp ?: getEffectiveUsp()).trim()

        return when (activeFormat) {
            LabelFormatType.SINGLE_LINE -> {
                if (customLine1.isNotBlank()) {
                    listOf(customLine1)
                } else if (storedLines.isNotEmpty()) {
                    listOf(storedLines.first())
                } else {
                    val parts = mutableListOf<String>()
                    if (activeBatch.isNotBlank()) parts.add("B:$activeBatch")
                    if (rawMfd.isNotBlank()) parts.add("MFD:$rawMfd")
                    if (rawExp.isNotBlank()) parts.add("EXP:$rawExp")
                    if (rawMrp.isNotBlank()) parts.add("MRP:₹$rawMrp")
                    listOf(if (parts.isNotEmpty()) parts.joinToString("  ") else "SINGLE LINE CIJ STREAM")
                }
            }

            LabelFormatType.TWO_LINE -> {
                if (customLine1.isNotBlank() && customLine2.isNotBlank()) {
                    listOf(customLine1, customLine2)
                } else if (storedLines.size >= 2) {
                    listOf(storedLines[0], storedLines[1])
                } else if (customLine1.isNotBlank()) {
                    listOf(customLine1, "MFD: $rawMfd  EXP: $rawExp")
                } else {
                    val line1 = listOfNotNull(
                        activeBatch.takeIf { it.isNotBlank() }?.let { "BATCH: $it" },
                        rawMfd.takeIf { it.isNotBlank() }?.let { "MFD: $it" }
                    ).joinToString("  ")
                    val line2 = listOfNotNull(
                        rawExp.takeIf { it.isNotBlank() }?.let { "EXP: $it" },
                        rawMrp.takeIf { it.isNotBlank() }?.let { "MRP: ₹$it" }
                    ).joinToString("  ")
                    listOf(line1.ifBlank { "BATCH / MFD" }, line2.ifBlank { "EXP / MRP" })
                }
            }

            LabelFormatType.THREE_LINE -> {
                if (customLine1.isNotBlank() && customLine2.isNotBlank() && customLine3.isNotBlank()) {
                    listOf(customLine1, customLine2, customLine3)
                } else if (storedLines.size >= 3) {
                    listOf(storedLines[0], storedLines[1], storedLines[2])
                } else {
                    listOf(
                        customLine1.ifBlank { "BATCH NO : $activeBatch" },
                        customLine2.ifBlank { "MFD: $rawMfd  EXP: $rawExp" },
                        customLine3.ifBlank { "MRP: ₹$rawMrp $rawUsp".trim() }
                    )
                }
            }

            LabelFormatType.CUSTOM -> {
                if (storedLines.isNotEmpty()) {
                    storedLines
                } else {
                    listOf(
                        customLine1.ifBlank { activeBatch },
                        customLine2.ifBlank { rawMfd },
                        customLine3.ifBlank { rawExp },
                        customLine4.ifBlank { "$rawMrp $rawUsp".trim() }
                    ).filter { it.isNotBlank() }.ifEmpty { listOf("CUSTOM CIJ STREAM") }
                }
            }

            LabelFormatType.TATA_STYLE -> {
                // If the label had stored lines from a .lbl file and format wasn't explicitly forced
                if (overrideFormat == null && storedLines.isNotEmpty() && storedLines.size < 4) {
                    return storedLines
                }
                // Line 1: 830(₹1.66/g)
                val cleanUsp = rawUsp
                    .replace("USP", "", ignoreCase = true)
                    .replace(" ", "")
                    .trim()
                val line1 = if (cleanUsp.isNotBlank()) {
                    val parenUsp = if (cleanUsp.startsWith("(") && cleanUsp.endsWith(")")) cleanUsp else "($cleanUsp)"
                    "$rawMrp$parenUsp"
                } else {
                    rawMrp
                }
                // Line 2: MFD 27/08/26 (2-digit year)
                val line2 = convertToTwoDigitYear(rawMfd)
                // Line 3: EXP 26/08/27 (2-digit year)
                val line3 = convertToTwoDigitYear(rawExp)
                // Line 4: Batch B06H2735D1
                val line4 = activeBatch
                listOf(line1, line2, line3, line4)
            }

            LabelFormatType.BOLAS_BOX -> {
                if (overrideFormat == null && storedLines.isNotEmpty() && storedLines.size < 4) {
                    return storedLines
                }
                // Line 1: Batch IARS026
                val line1 = activeBatch
                // Line 2: MFD Sep.2026
                val line2 = convertToMonthYear(rawMfd)
                // Line 3: EXP May.2027
                val line3 = convertToMonthYear(rawExp)
                // Line 4: 392.00 (USP ₹1.96/g)
                val cleanUsp = rawUsp.replace(Regex("""^\(?(?:USP\s*)?"""), "").replace(Regex("""\)?$"""), "").trim()
                val line4 = if (cleanUsp.isNotBlank()) {
                    "$rawMrp (USP $cleanUsp)"
                } else {
                    rawMrp
                }
                listOf(line1, line2, line3, line4)
            }

            LabelFormatType.BOLAS_STANDARD -> {
                // If read from a 1-line, 2-line, or 3-line .lbl file and format wasn't explicitly changed
                if (overrideFormat == null && storedLines.isNotEmpty() && storedLines.size < 4) {
                    return storedLines
                }
                // Line 1: Batch ICH026
                val line1 = activeBatch
                // Line 2: MFD 10/09/2026 (4-digit year)
                val line2 = convertToFullYear(rawMfd)
                // Line 3: EXP 09/09/2027 (4-digit year)
                val line3 = convertToFullYear(rawExp)
                // Line 4: 439.00 (USP ₹1.76/g)
                val cleanUsp = rawUsp.replace(Regex("""^\(?(?:USP\s*)?"""), "").replace(Regex("""\)?$"""), "").trim()
                val line4 = if (cleanUsp.isNotBlank()) {
                    "$rawMrp (USP $cleanUsp)"
                } else {
                    rawMrp
                }
                listOf(line1, line2, line3, line4)
            }

            LabelFormatType.PREFIXED -> {
                if (overrideFormat == null && storedLines.isNotEmpty() && storedLines.size < 4) {
                    return storedLines
                }
                val line1 = "BATCH NO    : $activeBatch"
                val line2 = "DATE OF MFG : $rawMfd"
                val line3 = "USE BY      : $rawExp"
                val line4 = "MRP         : $rawMrp $rawUsp"
                listOf(line1, line2, line3, line4)
            }
        }
    }

    companion object {
        fun convertToTwoDigitYear(dateStr: String): String {
            if (dateStr.isBlank()) return "27/08/26"
            val parts = dateStr.split('/', '-', '.')
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2].takeLast(2)
                return "$day/$month/$year"
            }
            return dateStr
        }

        fun convertToFullYear(dateStr: String): String {
            if (dateStr.isBlank()) return "10/09/2026"
            val parts = dateStr.split('/', '-', '.')
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = if (parts[2].length == 2) "20${parts[2]}" else parts[2]
                return "$day/$month/$year"
            }
            return dateStr
        }

        fun convertToMonthYear(dateStr: String): String {
            if (dateStr.isBlank()) return "Sep.2026"
            // If already matches Month.Year e.g. Sep.2026, May.2027
            val monthRegex = Regex("""(?i)^(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[.\s\-/]+(20\d{2}|\d{2})$""")
            val match = monthRegex.find(dateStr.trim())
            if (match != null) {
                val mon = match.groupValues[1].lowercase().replaceFirstChar { it.uppercase() }
                val yr = match.groupValues[2].let { if (it.length == 2) "20$it" else it }
                return "$mon.$yr"
            }

            val parts = dateStr.split('/', '-', '.')
            if (parts.size >= 2) {
                val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val monthNum = parts.getOrNull(1)?.toIntOrNull()
                val yearPart = parts.getOrNull(2) ?: parts.getOrNull(1)
                if (monthNum != null && monthNum in 1..12 && yearPart != null) {
                    val fullYear = if (yearPart.length == 2) "20$yearPart" else yearPart
                    return "${monthNames[monthNum - 1]}.$fullYear"
                }
            }
            return dateStr
        }
    }
}

