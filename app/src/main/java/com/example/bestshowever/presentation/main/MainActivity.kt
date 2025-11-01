package com.example.bestshowever.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bestshowever.presentation.MainApplication
import com.example.bestshowever.presentation.ui.theme.BestShowEverTheme

class MainActivity : ComponentActivity() {
    private val component by lazy {
        (application as MainApplication).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BestShowEverTheme {
                MainPage(component)
            }
        }
    }
}
