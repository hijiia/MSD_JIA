package com.example.maracas

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maracas.sensors.ShakeDetector
import com.example.maracas.ui.MaracaScreen
import com.example.maracas.ui.MaracaVM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    // ViewModel
                    val vm: MaracaVM = viewModel()
                    //stateflow
                    val shakes by vm.shakes.collectAsStateWithLifecycle()

                    // Sensor manager
                    val context = LocalContext.current
                    val sensorManager = remember {
                        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                    }

                    // Shake detector (use named args)
                    val detector = remember(vm) {
                        ShakeDetector(
                            sensorManager = sensorManager,
                            onShake = { g -> vm.recordShake(g) }
                        )
                    }
                    DisposableEffect(Unit) {
                        detector.start()
                        onDispose { detector.stop() }
                    }

                    // Stateless UI
                    MaracaScreen(
                        shakes = shakes,
                        onDeleteOlderThan = vm::deleteOlderThan
                    )
                }
            }
        }
    }
}