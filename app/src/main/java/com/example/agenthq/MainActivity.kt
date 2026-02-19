package com.example.agenthq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.agenthq.ui.navigation.AgentHQNavHost
import com.example.agenthq.ui.theme.AgentHQTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgentHQTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AgentHQNavHost()
                }
            }
        }
    }
}
