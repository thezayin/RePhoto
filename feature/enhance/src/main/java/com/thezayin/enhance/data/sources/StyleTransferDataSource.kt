package com.thezayin.enhance.data.sources

import com.thezayin.enhance.data.manager.ModelManager
import com.thezayin.enhance.domain.model.Image

interface StyleTransferDataSource {
    suspend fun applyStyle(image: Image): Image
}

class StyleTransferDataSourceImpl(
    private val modelManager: ModelManager
) : StyleTransferDataSource {
    override suspend fun applyStyle(image: Image): Image {
        // Preprocess image
        val input = modelManager.preprocess(image.bitmap)

        // Run inference using Style Transfer model
        val outputBitmap = modelManager.runStyleTransferInference(input)

        // Postprocess image
        return image.copy(bitmap = outputBitmap, clarityLevel = calculateClarity(outputBitmap))
    }
}
