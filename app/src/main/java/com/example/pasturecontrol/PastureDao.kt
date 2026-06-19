package com.example.pasturecontrol

import androidx.room.*

@Dao
interface PastureDao {
    @Query("SELECT * FROM pastures")
    suspend fun getAll(): List<Pasture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pasture: Pasture)

    @Update
    suspend fun update(pasture: Pasture)

    @Delete
    suspend fun delete(pasture: Pasture)
}
