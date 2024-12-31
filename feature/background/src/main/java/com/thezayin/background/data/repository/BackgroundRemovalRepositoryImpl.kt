package com.thezayin.background.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.thezayin.background.data.blur.BlurUtils
import com.thezayin.background.data.segmentation.SubjectSegmentationHelper
import com.thezayin.background.domain.model.BackgroundImageCategory
import com.thezayin.background.domain.model.BackgroundImages
import com.thezayin.background.domain.repository.BackgroundRemovalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Implementation of [BackgroundRemovalRepository].
 * 1. Converts the URI -> Bitmap.
 * 2. Removes background using [SubjectSegmentationHelper].
 */
class BackgroundRemovalRepositoryImpl(
    private val appContext: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
) : BackgroundRemovalRepository {

    // Remote Config parameter key for the JSON data
    private val jsonParamKey = "bg_images"

    override suspend fun removeBackground(baseImage: Uri?): Bitmap? {
        val baseImageUri: Uri = baseImage ?: return null
        val originalBitmap = uriToBitmap(baseImageUri) ?: run {
            return null
        }

        return SubjectSegmentationHelper.removeBackgroundFromBitmap(originalBitmap)
    }

    /**
     * Safely convert a [Uri] to a [Bitmap].
     */
    private suspend fun uriToBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(appContext.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getOriginalBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(appContext.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Smooths the bitmap edges based on the provided smoothing value.
     *
     * @param bitmap The bitmap to process.
     * @param value The smoothing value (0-100). Higher values result in smoother edges.
     * @return The smoothed bitmap.
     */
    override suspend fun smoothBitmap(bitmap: Bitmap, value: Int): Bitmap =
        withContext(Dispatchers.Default) {
            applySmoothingToBitmap(bitmap, value)
        }

    /**
     * Applies smoothing to the bitmap edges based on the smoothing value.
     *
     * @param bitmap The bitmap to process.
     * @param value The smoothing value (0-100).
     * @return The smoothed bitmap.
     */
    private fun applySmoothingToBitmap(bitmap: Bitmap, value: Int): Bitmap {
        val clampedValue = clamp(value, 0, 100)

        if (clampedValue == 0) {
            return bitmap
        }

        val maxBlurRadius = 20
        val blurRadius = (clampedValue / 100f * maxBlurRadius).toInt()
        val smoothedBitmap = BlurUtils.blurAlphaChannel(bitmap, blurRadius)
        return smoothedBitmap
    }

    /**
     * Clamps the value within the specified range.
     */
    private fun clamp(value: Int, min: Int, max: Int): Int {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

    /**
     * Retrieves a categorized list of background images from Firebase Remote Config.
     *
     * @return An instance of [BackgroundImages] containing categories and their image URLs.
     */
    override suspend fun getBackgroundImages(): BackgroundImages = withContext(Dispatchers.IO) {
        Log.d("BackgroundRemovalRepo", "getBackgroundImages")
        try {
            Log.d("BackgroundRemovalRepo", "fetchAndActivate")
            // Fetch the latest Remote Config parameters from the server
            remoteConfig.fetchAndActivate().await()

            // Get the JSON string from Remote Config
            val jsonString: String? =
                remoteConfig.getString(jsonParamKey).takeIf { it.isNotBlank() }
            Log.d("BackgroundRemovalRepo", "jsonString: $jsonString")
            if (jsonString.isNullOrEmpty()) {
                Log.d("BackgroundRemovalRepo", "jsonString is null or empty")
                // Handle the case where the JSON string is not available
                throw Exception("Remote Config parameter '$jsonParamKey' is empty or not set.")

            }

            // Parse the JSON string into BackgroundImages
            val backgroundImages: BackgroundImages = parseJsonToBackgroundImages(jsonString)
            Log.d("BackgroundRemovalRepo", "backgroundImages: $backgroundImages")
            backgroundImages
        } catch (e: Exception) {
            Log.e("BackgroundRemovalRepo", "Failed to fetch background images.", e)
            e.printStackTrace()
            // Return an empty BackgroundImages instance in case of failure
            BackgroundImages(categories = emptyList())
        }
    }

    /**
     * Parses the JSON string into [BackgroundImages].
     *
     * @param jsonString The JSON string representing background images.
     * @return An instance of [BackgroundImages].
     */
    private fun parseJsonToBackgroundImages(jsonString: String): BackgroundImages {
        val jsonObject = gson.fromJson(jsonString, Map::class.java)
        val categoriesMap = jsonObject["categories"] as? Map<*, *>
        Log.d("BackgroundRemovalRepo", "categoriesMap: $categoriesMap")
        Log.d("BackgroundRemovalRepo", "jsonObject: $jsonObject")
        val categoriesList = categoriesMap?.mapNotNull { entry ->
            val categoryName = entry.key as? String
            val imageUrls = entry.value as? List<*>
            Log.d("BackgroundRemovalRepo", "categoryName: $categoryName, imageUrls: $imageUrls")
            if (categoryName != null && imageUrls != null) {
                val urls = imageUrls.filterIsInstance<String>()
                BackgroundImageCategory(name = categoryName, imageUrls = urls)
            } else {
                null
            }
        } ?: emptyList()

        return BackgroundImages(categories = categoriesList)
    }
}