package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ConsumableItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumableDao {

    @Query("SELECT * FROM consumable_items ORDER BY category ASC, name ASC")
    fun getAllConsumables(): Flow<List<ConsumableItem>>

    @Query("SELECT * FROM consumable_items WHERE category = :category ORDER BY name ASC")
    fun getConsumablesByCategory(category: String): Flow<List<ConsumableItem>>

    @Query("SELECT * FROM consumable_items WHERE id = :id")
    fun getConsumableById(id: Long): Flow<ConsumableItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumable(item: ConsumableItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumables(items: List<ConsumableItem>)

    @Update
    suspend fun updateConsumable(item: ConsumableItem)

    @Delete
    suspend fun deleteConsumable(item: ConsumableItem)

    @Query("DELETE FROM consumable_items WHERE id = :id")
    suspend fun deleteConsumableById(id: Long)

    @Query("UPDATE consumable_items SET quantity = MAX(0, quantity + :delta), lastUpdated = :now WHERE id = :id")
    suspend fun adjustQuantity(id: Long, delta: Int, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM consumable_items")
    suspend fun countConsumables(): Int

    @Query("DELETE FROM consumable_items")
    suspend fun clearAllConsumables()
}
