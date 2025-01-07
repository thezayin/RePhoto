package com.thezayin.background.domain.usecase

import com.thezayin.background.domain.repository.BackgroundBlurRepository
import android.graphics.Bitmap
import timber.log.Timber

/**
 * Use case for setting the base image.
 *
 * @param repository The [BackgroundBlurRepository] to interact with.
 */
class SetBaseImageUseCase(
    private val repository: BackgroundBlurRepository
) {
    suspend operator fun invoke(baseImage: Bitmap) {
        Timber.tag("jeje+SetBaseImageUseCase").d("Invoking SetBaseImageUseCase.")
        try {
            repository.setBaseImage(baseImage)
            Timber.tag("jeje+SetBaseImageUseCase").d("Base image set successfully.")
        } catch (e: Exception) {
            Timber.tag("jeje+SetBaseImageUseCase").e(e, "Error in SetBaseImageUseCase.")
            throw e
        }
    }
}
