package com.thezayin.enhance.data.sources
import com.thezayin.enhance.data.manager.ModelManager
import com.thezayin.enhance.domain.model.Image
interface DenoisingDataSource {
    suspend fun denoiseImage(image: Image): Image
}
class DenoisingDataSourceImpl(
    private val modelManager: ModelManager
) : DenoisingDataSource {
    override suspend fun denoiseImage(image: Image): Image {
        // Preprocess image
        val input = modelManager.preprocess(image.bitmap)

        // Run inference using Denoising model
        val outputBitmap = modelManager.runDenoisingInference(input)

        // Postprocess image
        return image.copy(bitmap = outputBitmap, clarityLevel = calculateClarity(outputBitmap))
    }
}