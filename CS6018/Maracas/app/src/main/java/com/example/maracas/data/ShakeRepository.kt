package com.example.maracas.data

import kotlinx.coroutines.flow.Flow

//the repo wraps DAO and exposes operations to ViewModel
class ShakeRepository(private val dao: ShakeDao) {
    //flow: keep listening all shakes record in db
    val shakes: Flow<List<Shake>> = dao.getAllRecords()
    suspend fun add(timestamp: Long, intensityG: Float) {
        dao.insert(Shake(timestamp = timestamp, intensity = intensityG))
    }

   //Delete all shakes older than
    suspend fun deleteOlderThan(durationMs: Long) {
        val cutoff = System.currentTimeMillis() - durationMs
        dao.deleteOlderThan(cutoff)
    }
}