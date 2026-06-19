package com.example.pasturecontrol

import androidx.room.*

@Dao
interface PastureDao {
    @Query("SELECT * FROM pastures ORDER BY name")
    suspend fun getAll(): List<Pasture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pasture: Pasture)

    @Update
    suspend fun update(pasture: Pasture)

    @Delete
    suspend fun delete(pasture: Pasture)
}

@Dao
interface RotationRecordDao {
    @Query("SELECT * FROM rotation_records WHERE startDateMillis <= :dateMillis ORDER BY startDateMillis DESC")
    suspend fun getRotationsUpTo(dateMillis: Long): List<RotationRecord>

    @Query("SELECT * FROM rotation_records WHERE pastureId = :pastureId ORDER BY startDateMillis DESC")
    suspend fun getRotationsForPasture(pastureId: Int): List<RotationRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<RotationRecord>)
}
