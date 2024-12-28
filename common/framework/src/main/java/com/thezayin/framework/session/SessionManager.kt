package com.thezayin.framework.session

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    fun setBaseImage(uri: Uri)
    fun getBaseImage(): Flow<Uri?>
    fun clearBaseImage()
}