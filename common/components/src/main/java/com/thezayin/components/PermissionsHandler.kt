//package com.thezayin.components
//
//import android.Manifest
//import android.app.AlarmManager
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.provider.Settings
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//
//@Composable
//fun PermissionsHandler(
//    onAllPermissionsGranted: () -> Unit
//) {
//    val context = LocalContext.current
//
//    // Notification Permission (API 33+)
//    val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
//    } else {
//        true
//    }
//
//    // Exact Alarm Permission (API 31+)
//    val exactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.canScheduleExactAlarms()
//    } else {
//        true
//    }
//
//    // Launchers
//    val notificationLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (isGranted) {
//                // Proceed to check exact alarm permission
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    // No direct launcher for S's exact alarm permission, guide user to settings
//                    // Show a dialog or Snackbar to inform the user
//                } else {
//                    onAllPermissionsGranted()
//                }
//            } else {
//                // Inform user that notification permission is necessary
//                // Show a dialog or Snackbar
//            }
//        }
//    )
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
//        AlertDialog(
//            onDismissRequest = { /* Handle dismiss */ },
//            title = { Text("Notification Permission") },
//            text = { Text("This app requires notification permissions to remind you of birthdays.") },
//            confirmButton = {
//                TextButton(onClick = { notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
//                    Text("Grant Permission")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { /* Handle dismissal */ }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !exactAlarmGranted) {
//        AlertDialog(
//            onDismissRequest = { /* Handle dismiss */ },
//            title = { Text("Exact Alarm Permission") },
//            text = { Text("This app requires permission to set exact alarms for birthday reminders. Please enable it in settings.") },
//            confirmButton = {
//                TextButton(onClick = {
//                    // Navigate to exact alarm settings
//                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
//                        data = android.net.Uri.parse("package:${context.packageName}")
//                    }
//                    context.startActivity(intent)
//                }) {
//                    Text("Open Settings")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { /* Handle dismissal */ }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//
//    // Check if all permissions are granted
//    if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || notificationPermissionGranted) &&
//        (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || exactAlarmGranted)
//    ) {
//        onAllPermissionsGranted()
//    }
//}