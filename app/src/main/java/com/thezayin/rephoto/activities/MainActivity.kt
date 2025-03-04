package com.thezayin.rephoto.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.thezayin.framework.session.SessionManager
import com.thezayin.rephoto.navigation.AppNavHost
import com.thezayin.rephoto.ui.theme.RePhotoTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val sessionManager: SessionManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RePhotoTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, sessionManager = sessionManager)
            }
        }
    }
}