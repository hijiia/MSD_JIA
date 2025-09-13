package com.example.maracas.data
import androidx.room.Entity
import androidx.room.PrimaryKey

//database structure of shake, room实体
@Entity(tableName = "Shakes")
data class Shake (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,  // epoch time when shake happens
    val intensity: Float // intensity of shaking
)