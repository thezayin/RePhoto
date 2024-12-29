package com.thezayin.start_up.splash.event

sealed class SplashEvent {
    data object LoadSplash : SplashEvent()
    data object CheckInternet : SplashEvent()
    data object NavigateNext : SplashEvent()
}
