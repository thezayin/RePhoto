package com.thezayin.rephoto.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.thezayin.datemate.navigation.NavHost
import com.thezayin.rephoto.ui.theme.RePhotoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RePhotoTheme {
                val navController = rememberNavController()
                NavHost(navController = navController)
            }
        }
    }
}