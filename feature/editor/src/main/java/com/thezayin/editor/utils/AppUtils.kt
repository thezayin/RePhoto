package com.thezayin.editor.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import com.thezayin.values.R
import kotlin.math.abs


object AppUtils {

    fun getScreenWidthAndHeight(context: Context): Pair<Int, Int> {
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        return Pair(width, height)
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        return width
    }

    fun getScreenHeight(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val height = displayMetrics.heightPixels
        return height
    }

    fun getConstrainOffsetScaleOnly(
        context: Context,
        aspectRatio: Float,
        verticalToolbarPaddingPx: Float,
        scale: Float
    ): Offset {

        val initialWidthPx: Float
        val initialHeightPx: Float

        if (aspectRatio >= 1) {
            initialWidthPx = getScreenWidth(context).toFloat()
            initialHeightPx = (initialWidthPx / aspectRatio)

        } else {
            initialHeightPx = getScreenHeight(context).toFloat() - verticalToolbarPaddingPx
            initialWidthPx = initialHeightPx * aspectRatio
        }

        val canvasWidth = initialWidthPx * scale
        val canvasHeight = canvasWidth / aspectRatio

        val outlierSpaceEachSideHorizontal = abs(canvasWidth - initialWidthPx) / 2
        val outlierSpaceEachSideVertical = abs(canvasHeight - initialHeightPx) / 2

        val horizontalConstrain = initialWidthPx * 0.3f
        val verticalConstrain = initialHeightPx * 0.2f

        val adjustedHorizontalConstrain = horizontalConstrain + outlierSpaceEachSideHorizontal
        val adjustedVerticalConstrain = verticalConstrain + outlierSpaceEachSideVertical

        return Offset(adjustedHorizontalConstrain, adjustedVerticalConstrain)
    }

    fun shareOnApp(
        context: Context,
        appName: String?,
        uri: Uri,
        type: String
    ) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = type
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            if (appName != null) {
                var appFound = false
                val pm: PackageManager = context.packageManager
                val activityList: List<ResolveInfo> = pm.queryIntentActivities(shareIntent, 0)
                for (app in activityList) {
                    if (app.activityInfo.name.contains(appName)) {
                        appFound = true
                        val activity: ActivityInfo = app.activityInfo
                        val name =
                            ComponentName(activity.applicationInfo.packageName, activity.name)
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                        shareIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        shareIntent.component = name
                        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
                        break
                    }
                }
                if (!appFound) {
                    Toast.makeText(
                        context,
                        "${appName} ${context.resources.getString(R.string.app_not_found)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    context.resources.getString(R.string.app_name)
                )
                context.startActivity(Intent.createChooser(shareIntent, "choose one"))
            }

        } catch (e: java.lang.Exception) {
            Log.e("AppUtils", "shareOnApp: ${e.message}")
        }
    }
}