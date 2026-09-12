package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ConsumableCategory(
    val id: String,
    val displayName: String,
    val defaultUnit: String,
    val description: String
) {
    INK(
        id = "INK",
        displayName = "Ink",
        defaultUnit = "Cartridges",
        description = "CIJ coding and marking ink cartridges & fluids"
    ),
    MAKE_UP(
        id = "MAKE_UP",
        displayName = "Make-up",
        defaultUnit = "Cartridges",
        description = "Viscosity control diluent & solvent cartridges"
    ),
    WASH(
        id = "WASH",
        displayName = "Wash",
        defaultUnit = "Bottles",
        description = "Printhead nozzle cleaning & gutter wash solvent"
    ),
    FILTER(
        id = "FILTER",
        displayName = "Filter",
        defaultUnit = "Packs",
        description = "Main 5µm ink system, damper & air cabinet filters"
    ),
    ITM(
        id = "ITM",
        displayName = "ITM",
        defaultUnit = "Units",
        description = "Integrated Technology Module (Ax-Series service unit)"
    );

    companion object {
        fun fromId(id: String): ConsumableCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: INK
        }
    }
}

enum class ConsumableSortOption(val displayName: String) {
    NAME("Name (A-Z)"),
    STOCK_LOW_FIRST("Lowest Stock First"),
    STOCK_HIGH_FIRST("Highest Stock First"),
    EXPIRY_DATE("Expiry Date"),
    CATEGORY("Category")
}

@Entity(tableName = "consumable_items")
data class ConsumableItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // INK, MAKE_UP, WASH, FILTER, ITM
    val partNumber: String = "",
    val batchLotNumber: String = "",
    val quantity: Int = 0,
    val unit: String = "Cartridges", // Cartridges, Bottles, Units, Packs, Liters
    val minimumThreshold: Int = 2, // Low stock warning threshold
    val locationRack: String = "", // e.g. "Flammables Cabinet A1"
    val expiryDate: String = "", // e.g. "2027-09-15"
    val serviceLifeOrHours: String = "", // e.g. "4,000 Operating Hours"
    val compatiblePrinters: String = "Domino Ax Series",
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val categoryEnum: ConsumableCategory
        get() = ConsumableCategory.fromId(category)

    val isOutOfStock: Boolean
        get() = quantity <= 0

    val isLowStock: Boolean
        get() = quantity in 1..minimumThreshold

    val isAdequate: Boolean
        get() = quantity > minimumThreshold
}
