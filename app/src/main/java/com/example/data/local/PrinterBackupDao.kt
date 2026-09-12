package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PrinterBackup
import kotlinx.coroutines.flow.Flow

@Dao
interface PrinterBackupDao {
    @Query("SELECT * FROM printer_backups ORDER BY id ASC")
    fun getAllBackups(): Flow<List<PrinterBackup>>

    @Query("SELECT * FROM printer_backups WHERE id = :id")
    fun getBackupById(id: Long): Flow<PrinterBackup?>

    @Query("SELECT * FROM printer_backups WHERE id = :id")
    suspend fun getBackupByIdDirect(id: Long): PrinterBackup?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(backup: PrinterBackup): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackups(backups: List<PrinterBackup>): List<Long>

    @Update
    suspend fun updateBackup(backup: PrinterBackup)

    @Delete
    suspend fun deleteBackup(backup: PrinterBackup)

    @Query("DELETE FROM printer_backups WHERE id = :id")
    suspend fun deleteBackupById(id: Long)

    @Query("SELECT COUNT(*) FROM printer_backups")
    suspend fun getBackupsCount(): Int
}
