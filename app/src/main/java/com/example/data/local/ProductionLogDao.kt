package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProductionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductionLogDao {
    @Query("SELECT * FROM production_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<ProductionLog>>

    @Query("SELECT * FROM production_logs WHERE printerBackupId = :printerBackupId ORDER BY id DESC")
    fun getLogsByPrinter(printerBackupId: Long): Flow<List<ProductionLog>>

    @Query("""
        SELECT * FROM production_logs 
        WHERE (labelName LIKE '%' || :query || '%' 
           OR batchNumber LIKE '%' || :query || '%' 
           OR shiftName LIKE '%' || :query || '%'
           OR operatorName LIKE '%' || :query || '%'
           OR logDate LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun searchAllLogs(query: String): Flow<List<ProductionLog>>

    @Query("""
        SELECT * FROM production_logs 
        WHERE printerBackupId = :printerBackupId
          AND (labelName LIKE '%' || :query || '%' 
           OR batchNumber LIKE '%' || :query || '%' 
           OR shiftName LIKE '%' || :query || '%'
           OR operatorName LIKE '%' || :query || '%'
           OR logDate LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun searchLogsByPrinter(printerBackupId: Long, query: String): Flow<List<ProductionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ProductionLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<ProductionLog>): List<Long>

    @Update
    suspend fun updateLog(log: ProductionLog)

    @Delete
    suspend fun deleteLog(log: ProductionLog)

    @Query("SELECT SUM(packsPrinted) FROM production_logs")
    fun getTotalPacksPrintedAll(): Flow<Long?>

    @Query("SELECT SUM(packsPrinted) FROM production_logs WHERE printerBackupId = :printerBackupId")
    fun getTotalPacksPrintedByPrinter(printerBackupId: Long): Flow<Long?>
}
