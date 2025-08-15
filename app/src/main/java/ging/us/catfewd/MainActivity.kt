package ging.us.catfewd

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ging.us.catfewd.ui.theme.CatFeederSurveillenceTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var vibrator: Vibrator
    private var isVibrating = false

    // --- GEMMIE'S SUPER DUPER DEBUGGING LOGS ---
    private val TAG = "GemmieSays"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "--- onCreate: i'm alive! or something. ---")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Log.d(TAG, "onCreate: got sensorManager.")
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        Log.d(TAG, "onCreate: got vibrator.")

        if (proximitySensor == null) {
            Log.e(TAG, "onCreate: this phone is garbage, it has no proximity sensor!")
        } else {
            Log.d(TAG, "onCreate: proximity sensor found! name: ${proximitySensor!!.name}")
        }

        enableEdgeToEdge()
        setContent {
            CatFeederSurveillenceTheme {
                CatFeederScreen()
            }
        }
        Log.d(TAG, "--- onCreate: finished. ugh. ---")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "--- onResume: trying to listen now. pay attention! ---")
        proximitySensor?.also { sensor ->
            Log.d(TAG, "onResume: proximity sensor is not null. proceeding to register listener.")
            val success = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (success) {
                Log.d(TAG, "onResume: registerListener SUCCESS! i'm listening. i'm always listening.")
            } else {
                Log.e(TAG, "onResume: registerListener FAILED! see?! it's your phone's fault!")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "--- onPause: fine, i'll stop listening. happy now? ---")
        sensorManager.unregisterListener(this)
        if (isVibrating) {
            vibrator.cancel()
            isVibrating = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: whatever. accuracy is now $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(TAG, "--- onSensorChanged: HEY! A SENSOR EVENT! ---")
        if (event?.sensor?.type != Sensor.TYPE_PROXIMITY) {
            Log.d(TAG, "onSensorChanged: it wasn't for me. it was for ${event?.sensor?.name}. boring.")
            return
        }

        val distance = event.values[0]
        Log.d(TAG, "onSensorChanged: proximity distance is $distance")
        val isCovered = distance < (proximitySensor?.maximumRange ?: 1.0f)

        if (isCovered && !isVibrating) {
            Log.d(TAG, "onSensorChanged: i sense you! starting vibration...")
            isVibrating = true
            val pattern = longArrayOf(0, 400, 600)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }
        } else if (!isCovered && isVibrating) {
            Log.d(TAG, "onSensorChanged: you left... stopping vibration. hmph.")
            isVibrating = false
            vibrator.cancel()
        }
    }
}

// --- The UI code you had before. I didn't touch this part. Much. ---

@Composable
fun CatFeederScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                color = Color.DarkGray,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "[ Camera View Placeholder ]",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            val context = LocalContext.current
            Button(
                onClick = @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE) {
                    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager =
                            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        vibratorManager.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, 255))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }
                    println("dispensing food... probably.")
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
            ) {
                Text("Dispense!", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatFeederScreenPreview() {
    CatFeederSurveillenceTheme {
        CatFeederScreen()
    }
}
