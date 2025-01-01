package com.thezayin.enhance.data.sources
import com.thezayin.enhance.data.manager.ModelManager
import com.thezayin.enhance.domain.model.Image
interface RealEsrganDataSource {
    suspend fun enhanceImage(image: Image): Image
}
// data/datasources/RealEsrganDataSourceImpl.kt
class RealEsrganDataSourceImpl(
    private val modelManager: ModelManager // Handles model loading and inference
) : RealEsrganDataSource {
    override suspend fun enhanceImage(image: Image): Image {
        // Preprocess image
        val input = modelManager.preprocess(image.bitmap)

        // Run inference using Real-ESRGAN model
        val outputBitmap = modelManager.runRealEsrganInference(input)

        // Postprocess image
        return image.copy(bitmap = outputBitmap, clarityLevel = calculateClarity(outputBitmap))
    }
}
