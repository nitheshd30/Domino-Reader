package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DominoLabel
import com.example.data.model.PrinterBackup
import com.example.data.model.ProductionLog

@Database(
    entities = [
        PrinterBackup::class,
        DominoLabel::class,
        ProductionLog::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DominoDatabase : RoomDatabase() {
    abstract fun printerBackupDao(): PrinterBackupDao
    abstract fun dominoLabelDao(): DominoLabelDao
    abstract fun productionLogDao(): ProductionLogDao

    companion object {
        @Volatile
        private var INSTANCE: DominoDatabase? = null

        fun getDatabase(context: Context): DominoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DominoDatabase::class.java,
                    "domino_ax_backup_reader.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
