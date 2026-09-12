package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DominoLabel
import kotlinx.coroutines.flow.Flow

@Dao
interface DominoLabelDao {
    @Query("SELECT * FROM domino_labels ORDER BY labelName ASC")
    fun getAllLabels(): Flow<List<DominoLabel>>

    @Query("SELECT * FROM domino_labels WHERE printerBackupId = :printerBackupId ORDER BY labelName ASC")
    fun getLabelsByPrinter(printerBackupId: Long): Flow<List<DominoLabel>>

    @Query("SELECT * FROM domino_labels WHERE id = :id")
    fun getLabelById(id: Long): Flow<DominoLabel?>

    @Query("""
        SELECT * FROM domino_labels 
        WHERE (labelName LIKE '%' || :query || '%' 
           OR batchNumber LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR weightDetails LIKE '%' || :query || '%' 
           OR mrp LIKE '%' || :query || '%'
           OR unitSalePrice LIKE '%' || :query || '%'
           OR mfgDate LIKE '%' || :query || '%'
           OR useBy LIKE '%' || :query || '%'
           OR expiryDate LIKE '%' || :query || '%')
        ORDER BY labelName ASC
    """)
    fun searchAllLabels(query: String): Flow<List<DominoLabel>>

    @Query("""
        SELECT * FROM domino_labels 
        WHERE printerBackupId = :printerBackupId
          AND (labelName LIKE '%' || :query || '%' 
           OR batchNumber LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR weightDetails LIKE '%' || :query || '%' 
           OR mrp LIKE '%' || :query || '%'
           OR unitSalePrice LIKE '%' || :query || '%'
           OR mfgDate LIKE '%' || :query || '%'
           OR useBy LIKE '%' || :query || '%'
           OR expiryDate LIKE '%' || :query || '%')
        ORDER BY labelName ASC
    """)
    fun searchLabelsByPrinter(printerBackupId: Long, query: String): Flow<List<DominoLabel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: DominoLabel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabels(labels: List<DominoLabel>): List<Long>

    @Update
    suspend fun updateLabel(label: DominoLabel)

    @Delete
    suspend fun deleteLabel(label: DominoLabel)

    @Query("SELECT COUNT(*) FROM domino_labels WHERE printerBackupId = :printerBackupId")
    suspend fun getLabelsCountByPrinter(printerBackupId: Long): Int

    @Query("SELECT COUNT(*) FROM domino_labels")
    suspend fun getTotalLabelsCount(): Int

    @Query("SELECT COUNT(*) FROM domino_labels WHERE batchNumber = :batch")
    suspend fun countLabelsWithBatch(batch: String): Int
}
