package com.thezayin.framework.extension

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.share(string: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, string)
    startActivity(Intent.createChooser(shareIntent, "Share Via"))
}

fun Context.copyText(text: String) {
    val clipboard: ClipboardManager? =
        this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("joke", text)
    clipboard?.setPrimaryClip(clip)
}

fun textToSpeech(text: String, textToSpeech: TextToSpeech) {
    textToSpeech.setSpeechRate(0.7f)
    if (textToSpeech.isSpeaking) {
        textToSpeech.stop()
    } else {
        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }
}


fun Context.sendMail() {
    val i = Intent(Intent.ACTION_SEND)
    i.setType("message/rfc822")
    i.putExtra(Intent.EXTRA_EMAIL, arrayOf("zainshahidbuttt@gmail.com"))
    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email")
    i.putExtra(Intent.EXTRA_TEXT, "body of email")
    try {
        startActivity(Intent.createChooser(i, "Send mail..."))
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT)
            .show()
    }
}

fun Context.openLink(link: String) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(link)
    )
    this.startActivity(intent)
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun createImageFile(context: Context): File {
    // Create an image file name with timestamp
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

/**
 * Writes the [bitmap] to device Pictures using MediaStore.
 * If [asPng] = true, uses PNG (with transparency), else JPG.
 */
suspend fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    asPng: Boolean
) = withContext(Dispatchers.IO) {
    val format = if (asPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
    val mimeType = if (asPng) "image/png" else "image/jpeg"
    val extension = if (asPng) "png" else "jpg"

    val filename = "bg_removed_${System.currentTimeMillis()}.$extension"
    val fos: OutputStream?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android Q and above
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val imageCollection = MediaStore.Images.Media
            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val contentResolver = context.contentResolver

        val imageUri = contentResolver.insert(imageCollection, contentValues)
            ?: throw RuntimeException("MediaStore failed to create new record.")
        fos = contentResolver.openOutputStream(imageUri)
            ?: throw RuntimeException("Failed to open output stream.")

        bitmap.compress(format, 100, fos)
        fos.flush()
        fos.close()

        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(imageUri, contentValues, null, null)
    } else {
        // Pre-Android Q
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val imageFile = File(imagesDir, filename)

        fos = FileOutputStream(imageFile)
        bitmap.compress(format, 100, fos)
        fos.flush()
        fos.close()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, imageFile.absolutePath)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
}