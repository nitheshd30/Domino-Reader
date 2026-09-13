package com.example.ui.screens

import android.content.Context
import android.content.SharedPreferences

data class SavedOvertime(
    val id: String,
    val name: String,
    val costPerHour: String,
    val entries: List<TimeEntry>
)

object OvertimePrefs {
    private const val PREFS_NAME = "overtime_prefs"
    private const val KEY_SAVED = "saved_calculations"

    fun save(context: Context, calculation: SavedOvertime) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = getSaved(context).toMutableList()
        existing.removeAll { it.id == calculation.id }
        existing.add(0, calculation) // Add to top
        
        val serialized = existing.joinToString("||") { 
            "${it.id}::${it.name}::${it.costPerHour}::" + it.entries.joinToString(";") { e -> "${e.hours},${e.mins}" }
        }
        prefs.edit().putString(KEY_SAVED, serialized).apply()
    }

    fun delete(context: Context, id: String) {
        val existing = getSaved(context).toMutableList()
        existing.removeAll { it.id == id }
        val serialized = existing.joinToString("||") { 
            "${it.id}::${it.name}::${it.costPerHour}::" + it.entries.joinToString(";") { e -> "${e.hours},${e.mins}" }
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_SAVED, serialized).apply()
    }

    fun getSaved(context: Context): List<SavedOvertime> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val serialized = prefs.getString(KEY_SAVED, "") ?: ""
        if (serialized.isEmpty()) return emptyList()
        
        return serialized.split("||").mapNotNull { part ->
            val chunks = part.split("::")
            if (chunks.size == 4) {
                val id = chunks[0]
                val name = chunks[1]
                val cost = chunks[2]
                val entriesStr = chunks[3]
                val entries = if (entriesStr.isEmpty()) emptyList() else entriesStr.split(";").mapNotNull { eStr ->
                    val hM = eStr.split(",")
                    if (hM.size == 2) {
                        TimeEntry(hours = hM[0].toIntOrNull() ?: 0, mins = hM[1].toIntOrNull() ?: 0)
                    } else null
                }
                SavedOvertime(id, name, cost, entries)
            } else null
        }
    }
}
