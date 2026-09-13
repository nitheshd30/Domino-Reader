package com.example.ui.screens

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

data class SavedOvertime(
    val id: String = "",
    val name: String = "",
    val costPerHour: String = "",
    val entries: List<TimeEntry> = emptyList()
)

object OvertimePrefs {
    private const val PREFS_NAME = "overtime_prefs"
    private const val KEY_SAVED = "saved_calculations"

    private fun getFirestore(context: Context): FirebaseFirestore? {
        return try {
            if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("OvertimePrefs", "Firebase not initialized.", e)
            null
        }
    }

    fun save(context: Context, calculation: SavedOvertime) {
        // Save locally for sync UI
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = getSaved(context).toMutableList()
        existing.removeAll { it.id == calculation.id }
        existing.add(0, calculation)
        
        val serialized = existing.joinToString("||") { 
            "${it.id}::${it.name}::${it.costPerHour}::" + it.entries.joinToString(";") { e -> "${e.hours},${e.mins}" }
        }
        prefs.edit().putString(KEY_SAVED, serialized).apply()

        // Push to Firestore
        val firestore = getFirestore(context) ?: return
        firestore.collection("saved_calculations").document(calculation.id).set(calculation)
    }

    fun delete(context: Context, id: String) {
        // Delete locally
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = getSaved(context).toMutableList()
        existing.removeAll { it.id == id }
        val serialized = existing.joinToString("||") { 
            "${it.id}::${it.name}::${it.costPerHour}::" + it.entries.joinToString(";") { e -> "${e.hours},${e.mins}" }
        }
        prefs.edit().putString(KEY_SAVED, serialized).apply()

        // Delete from Firestore
        val firestore = getFirestore(context) ?: return
        firestore.collection("saved_calculations").document(id).delete()
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
