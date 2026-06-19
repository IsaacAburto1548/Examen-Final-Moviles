package com.example.pasturecontrol

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "pastures")
data class Pasture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val area: Double,
    val creationDate: String,
    val photoUri: String? = null,
    val videoUri: String? = null
) : Serializable
