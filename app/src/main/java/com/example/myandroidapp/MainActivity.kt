package com.example.myandroidapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myandroidapp.services.utils.createNotificationChannel
import com.example.myandroidapp.services.utils.showSimpleNotification
import com.example.myandroidapp.ui.theme.MyAndroidAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showSimpleNotification(this, "My Channel", "Permission Granted", "You can now receive notifications!")
            } else {
                Toast.makeText(this, "Permission denied. Notifications will not be shown.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyAndroidApp {
                MyAppNavHost()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }

        createNotificationChannel("My Channel", this)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            (application as MyApplication).container.movieRepository.openWsClient()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            (application as MyApplication).container.movieRepository.closeWsClient()
        }
    }
}

@Composable
fun MyAndroidApp(content: @Composable () -> Unit) {
    Log.d("MyAndroidApp", "recompose")
    MyAndroidAppTheme {
        Surface {
            content()
        }
    }
}

@Preview
@Composable
fun PreviewMyApp() {
    MyAndroidApp {
        MyAppNavHost()
    }
}
