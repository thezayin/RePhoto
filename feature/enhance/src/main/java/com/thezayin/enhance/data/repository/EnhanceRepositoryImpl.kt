package com.thezayin.enhance.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.SparseArray
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzer
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerFactory
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerSetting
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionResult
import com.thezayin.enhance.domain.model.EnhancementType
import com.thezayin.enhance.domain.repository.EnhanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EnhanceRepositoryImpl(
    private val context: Context
) : EnhanceRepository {

    // Default analyzer (1x super-resolution).
    private val analyzer: MLImageSuperResolutionAnalyzer by lazy {
        MLImageSuperResolutionAnalyzerFactory
            .getInstance()
            .imageSuperResolutionAnalyzer
    }

    override suspend fun processImage(inputBitmap: Bitmap, type: EnhancementType): Bitmap {
        return withContext(Dispatchers.IO) {
            try {
                // 1) Possibly downscale the input if using 3x super-resolution:
                val preparedBitmap = when (type) {
                    EnhancementType.PLUS, EnhancementType.PRO -> {
                        // 3x super-resolution limit is wide edge <= 800 px (per logs/docs).
                        ensureMaxEdge(inputBitmap, 800)
                    }
                    else -> {
                        // 1x / DEBLUR can usually handle up to 1024 px, or no limit if docs differ.
                        inputBitmap
                    }
                }

                // 2) Create MLFrame from the (possibly downscaled) bitmap.
                val frame = MLFrame.Creator().setBitmap(preparedBitmap).create()

                // 3) Analyze based on the type.
                val results: SparseArray<MLImageSuperResolutionResult> = when (type) {
                    EnhancementType.BASIC -> {
                        // 1x super-resolution
                        analyzer.analyseFrame(frame)
                    }
                    EnhancementType.PLUS, EnhancementType.PRO -> {
                        // 3x super-resolution
                        val setting = MLImageSuperResolutionAnalyzerSetting.Factory()
                            .setScale(MLImageSuperResolutionAnalyzerSetting.ISR_SCALE_3X)
                            .create()
                        val customAnalyzer = MLImageSuperResolutionAnalyzerFactory
                            .getInstance()
                            .getImageSuperResolutionAnalyzer(setting)

                        val customResult = customAnalyzer.analyseFrame(frame)
                        customAnalyzer.stop()
                        customResult
                    }
                    EnhancementType.DEBLUR -> {
                        // For demonstration, reuse the default 1x analyzer.
                        analyzer.analyseFrame(frame)
                    }
                }

                // 4) Extract the first MLImageSuperResolutionResult, or fallback to the original if empty.
                if (results.size() > 0) {
                    results.valueAt(0).bitmap
                } else {
                    inputBitmap // fallback if no results
                }
            } catch (e: MLException) {
                throw Exception("Error processing image: ${e.message}", e)
            } catch (e: Exception) {
                throw Exception("Unexpected error: ${e.message}", e)
            }
        }
    }

    override fun close() {
        // Stop the default analyzer to free resources.
        analyzer.stop()
    }

    /**
     * Ensures that [bitmap]'s widest edge does not exceed [maxEdge].
     * If it does, downscale proportionally. Otherwise returns the original.
     *
     * @param bitmap The original image.
     * @param maxEdge The maximum allowed width or height (whichever is larger).
     * @return A potentially downscaled Bitmap if the wide edge is > [maxEdge].
     */
    private fun ensureMaxEdge(bitmap: Bitmap, maxEdge: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val largestEdge = maxOf(width, height)

        return if (largestEdge <= maxEdge) {
            bitmap
        } else {
            // Downscale proportionally so the largest edge is exactly maxEdge
            val scale = maxEdge.toFloat() / largestEdge.toFloat()
            val matrix = Matrix().apply {
                postScale(scale, scale)
            }
            Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, /* filter= */ true)
        }
    }
}
