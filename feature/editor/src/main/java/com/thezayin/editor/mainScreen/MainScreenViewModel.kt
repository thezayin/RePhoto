package com.thezayin.editor.mainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var permissionsGranted = mutableStateOf(false)
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (isGranted.not() && visiblePermissionDialogQueue.contains(permission).not()) {
            visiblePermissionDialogQueue.add(0, permission)
        }
    }

}