package com.thezayin.framework.session

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManagerImpl : SessionManager {
    private val _baseImage = MutableStateFlow<Uri?>(null)
    override fun getBaseImage(): StateFlow<Uri?> = _baseImage

    override fun setBaseImage(uri: Uri) {
        _baseImage.value = uri
    }

    override fun clearBaseImage() {
        _baseImage.value = null
    }
}