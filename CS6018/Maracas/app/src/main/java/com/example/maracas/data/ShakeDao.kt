package com.example.maracas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao //data access object: room db's operation interface
interface ShakeDao {
    //insert (I/O)： make it suspend cuz main thread is for ui
    @Insert
    suspend fun insert(shake : Shake)
    //get all records in desc
    @Query("SELECT * FROM shakes ORDER BY timestamp DESC")
    //when the shakes table in the database changes, Room automatically emits a new List<Shake>, UI layer collects this Flow, UI auto refreshes
    fun getAllRecords(): Flow<List<Shake>>
    //delete records before certain time
    @Query("DELETE FROM shakes WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
    //delete all records
    @Query("DELETE FROM shakes")
    suspend fun deleteAll()

}