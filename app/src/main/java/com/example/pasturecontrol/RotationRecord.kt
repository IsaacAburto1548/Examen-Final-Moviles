package com.example.pasturecontrol

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rotation_records",
    foreignKeys = [
        ForeignKey(
            entity = Pasture::class,
            parentColumns = ["id"],
            childColumns = ["pastureId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["pastureId", "startDateMillis"], unique = true)
    ]
)
data class RotationRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pastureId: Int,
    val startDateMillis: Long,
    val createdAtMillis: Long
)
