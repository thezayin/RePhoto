// SplashViewModel.kt
package com.thezayin.start_up.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.analytics.analytics.Analytics
import com.thezayin.framework.pref.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.start_up.splash.event.SplashEvent
import com.thezayin.start_up.splash.state.SplashState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class SplashViewModel(
    private val remoteConfig: RemoteConfig,
    val analytics: Analytics,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        // Initialize state based on SharedPreferences
        _state.value = _state.value.copy(
            isFirstTime = preferencesManager.isFirstTime.value
        )
        // Start the splash and onboarding flow
        sendEvent(SplashEvent.LoadSplash)
    }

    fun sendEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.LoadSplash -> handleLoadSplash()
            is SplashEvent.CheckInternet -> handleCheckInternet()
            is SplashEvent.NavigateNext -> handleNavigateNext()
        }
    }

    private fun handleLoadSplash() {
        viewModelScope.launch {
            // Cycle through splash texts over 5 seconds
            val totalTime = 5000L
            val interval = totalTime / _state.value.splashTexts.size

            for (i in _state.value.splashTexts.indices) {
                _state.update {
                    it.copy(
                        currentSplashText = it.splashTexts[i],
                        currentSplashIndex = i
                    )
                }
                delay(interval)
            }

            // After splash, check internet
            sendEvent(SplashEvent.CheckInternet)
        }
    }

    private fun handleCheckInternet() {
        viewModelScope.launch {
            val isConnected = checkInternetConnection()
            _state.update { it.copy(isConnected = isConnected, isLoading = false) }

            if (!isConnected) {
                // Handle no internet scenario
                _state.update { it.copy(error = "No internet connection.") }
            } else {
                sendEvent(SplashEvent.NavigateNext)
            }
        }
    }

    private fun handleNavigateNext() {
        _state.update { it.copy(navigateToNextScreen = true) }
    }


    /**
     * Checks if the device has an active internet connection by attempting to connect to a reliable server.
     * @return True if connected, false otherwise.
     */
    private suspend fun checkInternetConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val timeoutMs = 1500
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), timeoutMs) // Google DNS
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}
