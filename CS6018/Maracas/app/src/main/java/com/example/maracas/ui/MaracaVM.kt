package com.example.maracas.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.maracas.data.AppDatabase
import com.example.maracas.data.Shake
import com.example.maracas.data.ShakeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MaracaVM(app: Application) : AndroidViewModel(app) {
    //build repository from Room database singleton
    private val repo = ShakeRepository(AppDatabase.get(app).shakeDao())
    //ui-facing data model
    data class UiShake(val id: Long, val timeText: String, val intensityG: Float, val timestamp: Long)
    //format time
    private val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val shakes: StateFlow<List<UiShake>> = repo.shakes //Flow<List<Shake>> from DAO
        .map { list ->  // MAP SHAKE FROM DATA --> uiSHAKE(time formatted)
            list.map { s: Shake ->
                UiShake(
                    id = s.id,
                    timeText = formatter.format(s.timestamp),
                    intensityG = s.intensity,
                    timestamp = s.timestamp
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    //when ui update data, call VM methods
    fun recordShake(intensityG: Float) = viewModelScope.launch { //background thread--launch:I/O
        repo.add(System.currentTimeMillis(), intensityG)
    }

    fun deleteOlderThan(durationMs: Long) = viewModelScope.launch {
        repo.deleteOlderThan(durationMs)
    }
}