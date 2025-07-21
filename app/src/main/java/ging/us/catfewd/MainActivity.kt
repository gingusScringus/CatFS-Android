package ging.us.catfewd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import ging.us.catfewd.ui.theme.CatFeederSurveillenceTheme

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatFeederSurveillenceTheme {
                    CatFeederScreen()
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatFeederScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KATFOODULUL") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // Add some padding around the content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder for your camera view
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // A nice 16:9 aspect ratio
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
            // Get context
            val context = LocalContext.current
            // The big, important button
            Button(
                onClick = @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE) {
                    // Vibrate the device
                    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager =
                            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        vibratorManager.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    }
                    // Vibrate for 50 millisecond
                    // The old way is deprecated, so we have to do it like this
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }

                    // you'll put your blynk/mqtt logic here later, baka!
                    println("dispensing food... probably.")
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
            ) {
                Text("Feed the Cat!", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// This lets you see a preview in Android Studio without running the app!
@Preview(showBackground = true)
@Composable
fun CatFeederScreenPreview() {
    CatFeederSurveillenceTheme {
        CatFeederScreen()
    }
}
