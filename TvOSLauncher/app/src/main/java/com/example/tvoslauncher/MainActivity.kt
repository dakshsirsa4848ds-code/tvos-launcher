package com.example.tvoslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private var apps by mutableStateOf<List<AppItem>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reload()
        setContent {
            HomeScreen(
                apps = apps,
                onLaunch = { item ->
                    runCatching { startActivity(item.launchIntent) }
                },
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Pick up apps installed or removed while we were away.
        reload()
    }

    private fun reload() {
        apps = AppRepository.load(this) + listOfNotNull(AppRepository.settingsItem(this))
    }
}
