package com.thezayin.background_changer

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.background.domain.usecase.GetBackgroundImagesUseCase
import com.thezayin.background.domain.usecase.MergeImagesUseCase
import com.thezayin.background.domain.usecase.RemoveBackgroundUseCase
import com.thezayin.background.domain.usecase.SaveImageUseCase
import com.thezayin.background.domain.usecase.SmoothImageUseCase
import com.thezayin.background_changer.action.ChangerIntent
import com.thezayin.background_changer.state.ChangerViewState
import com.thezayin.framework.session.SessionManager
import com.thezayin.values.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class BackgroundChangerViewModel(
    private val sessionManager: SessionManager,
    private val removeBackgroundUseCase: RemoveBackgroundUseCase,
    private val getBackgroundImagesUseCase: GetBackgroundImagesUseCase,
    private val smoothImageUseCase: SmoothImageUseCase,
    private val mergeImagesUseCase: MergeImagesUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(ChangerViewState())
    val state: StateFlow<ChangerViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            handleIntent(ChangerIntent.LoadInitialData)
            handleIntent(ChangerIntent.MoreOptions)
            simulateLoadingSteps()
        }
    }

    fun handleIntent(intent: ChangerIntent) {
        when (intent) {
            is ChangerIntent.LoadInitialData -> loadInitialData()
            is ChangerIntent.SelectFromGallery -> setBackgroundFromGallery(intent.uri)
            is ChangerIntent.SelectDrawableImage -> setDrawableBackground(intent.drawableBitmap)
            is ChangerIntent.MoreOptions -> fetchBackgroundImages()
            is ChangerIntent.SelectCategoryImage -> setCategoryImageBackground(intent.imageUrl)
            is ChangerIntent.ResetBackground -> resetBackground()
            is ChangerIntent.ShowBaseImage -> showBaseImage()
            is ChangerIntent.HideBaseImage -> hideBaseImage()
            is ChangerIntent.ChangeSmoothing -> changeSmoothing(intent.smoothingValue)
            is ChangerIntent.ApplyChanges -> applyChanges()
            ChangerIntent.NavigateToNextScreen -> navigateToNextScreen()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            sessionManager.getBaseImage().collect { uri ->
                if (uri != null) {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            statusText = application.getString(R.string.status_loading_base_image)
                        )
                    }
                    // Load base image
                    val baseBitmap = loadOriginalBitmap(uri)
                    // Process image
                    val processedBitmap = removeBackgroundUseCase(uri)
                    if (processedBitmap != null && baseBitmap != null) {
                        _state.update {
                            it.copy(
                                baseImage = baseBitmap,
                                processedImageOriginal = processedBitmap,
                                processedImage = processedBitmap,
                                isLoading = false,
                                statusText = application.getString(R.string.status_image_processing_complete)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                statusText = application.getString(R.string.status_failed_load_process_image),
                                isError = true,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            statusText = application.getString(R.string.error_no_base_image),
                            isError = true
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadOriginalBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = application.contentResolver.openInputStream(uri)
                inputStream?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_load_original_image),
                        isError = true
                    )
                }
                null
            }
        }
    }

    private fun setBackgroundFromGallery(uri: Uri) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    statusText = application.getString(R.string.status_applying_background)
                )
            }
            val backgroundBitmap = loadBitmapFromUri(uri)
            if (backgroundBitmap != null) {
                _state.update {
                    it.copy(
                        backgroundImage = backgroundBitmap,
                        isLoading = false,
                        statusText = application.getString(R.string.status_background_applied_successfully)
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_load_gallery_image),
                        isError = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun setDrawableBackground(drawableBitmap: Bitmap) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    statusText = application.getString(R.string.status_applying_drawable_background)
                )
            }
            _state.update {
                it.copy(
                    backgroundImage = drawableBitmap,
                    isLoading = false,
                    statusText = application.getString(R.string.status_background_applied_successfully)
                )
            }
        }
    }

    private fun setCategoryImageBackground(imageUrl: String) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isLoading = true,
                        statusText = application.getString(R.string.status_applying_background)
                    )
                }
                val bitmap = fetchBitmapFromUrl(imageUrl)
                if (bitmap != null) {
                    _state.update {
                        it.copy(
                            backgroundImage = bitmap,
                            isLoading = false,
                            statusText = application.getString(R.string.status_background_applied_successfully)
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            errorMessage = application.getString(R.string.error_set_background),
                            isError = true,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_set_background),
                        isError = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun fetchBackgroundImages() {
        viewModelScope.launch {
            try {
                val backgrounds = getBackgroundImagesUseCase()
                _state.update {
                    it.copy(backgroundImages = backgrounds.categories)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_fetch_background_images),
                        isError = true
                    )
                }
            }
        }
    }

    private fun resetBackground() {
        viewModelScope.launch {
            _state.update { it.copy(backgroundImage = null) }
        }
    }

    private suspend fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = application.contentResolver.openInputStream(uri)
                inputStream?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_load_gallery_image),
                        isError = true
                    )
                }
                null
            }
        }
    }

    private suspend fun fetchBitmapFromUrl(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_set_background),
                        isError = true
                    )
                }
                null
            }
        }
    }

    /**
     * Show the base image by setting the flag.
     */
    private fun showBaseImage() {
        viewModelScope.launch {
            if (_state.value.baseImage != null) {
                _state.update {
                    it.copy(
                        isShowingBaseImage = true,
                        statusText = application.getString(R.string.status_showing_base_image)
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_no_base_image),
                        isError = true
                    )
                }
            }
        }
    }

    /**
     * Hide the base image by resetting the flag.
     */
    private fun hideBaseImage() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isShowingBaseImage = false,
                    statusText = application.getString(R.string.status_showing_processed_image)
                )
            }
        }
    }

    /**
     * Simulate loading steps with different status messages.
     */
    private suspend fun simulateLoadingSteps() {
        val steps = withContext(Dispatchers.IO) {
            application.resources.getStringArray(R.array.loading_steps).toList()
        }
        _state.update { it.copy(isLoading = true) }
        for (msg in steps) {
            _state.update { it.copy(statusText = msg) }
            delay(1000)
        }
        _state.update {
            it.copy(
                isLoading = false,
                statusText = application.getString(R.string.status_image_processing_complete)
            )
        }
    }

    // Functions to update image transformation states
    fun updateImageScale(newScale: Float) {
        _state.update { it.copy(imageScale = newScale) }
    }

    fun updateImageOffset(newX: Float, newY: Float) {
        _state.update { it.copy(imageOffsetX = newX, imageOffsetY = newY) }
    }

    fun updateImageRotation(newRotation: Float) {
        _state.update { it.copy(imageRotation = newRotation) }
    }

    /**
     * Change the smoothing value and apply smoothing to the processed image.
     */
    private fun changeSmoothing(smoothingValue: Float) {
        viewModelScope.launch {
            _state.update { it.copy(smoothingValue = smoothingValue) }
            applySmoothing(smoothingValue)
        }
    }

    /**
     * Apply smoothing to the processed image based on the smoothing value.
     */
    private suspend fun applySmoothing(smoothingValue: Float) {
        val originalProcessedImage = _state.value.processedImageOriginal
        if (originalProcessedImage != null) {
            _state.update {
                it.copy(
                    isLoading = true,
                    statusText = application.getString(R.string.status_applying_smoothing)
                )
            }
            val smoothedBitmap = withContext(Dispatchers.Default) {
                smoothImageUseCase(originalProcessedImage, smoothingValue.toInt())
            }
            _state.update {
                it.copy(
                    processedImage = smoothedBitmap,
                    isLoading = false,
                    statusText = application.getString(R.string.status_smoothing_applied)
                )
            }
        } else {
            _state.update {
                it.copy(
                    errorMessage = application.getString(R.string.error_apply_smoothing),
                    isError = true,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Apply the changes by merging images, saving, and navigating.
     */
    private fun applyChanges() {
        viewModelScope.launch {
            val background = _state.value.backgroundImage
            val processed = _state.value.processedImage

            if (processed == null) {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_generic),
                        isError = true
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true,
                    statusText = application.getString(R.string.status_applying_changes)
                )
            }

            // Merge images with transformations if background is present
            val mergedBitmap = mergeImagesUseCase(
                background = background,
                processed = processed,
                scale = _state.value.imageScale,
                offsetX = _state.value.imageOffsetX,
                offsetY = _state.value.imageOffsetY,
                rotation = _state.value.imageRotation
            )

            if (mergedBitmap != null) {
                // Save the merged bitmap to internal storage
                val filename = "merged_image_${System.currentTimeMillis()}"
                val savedUri = saveImageUseCase(
                    bitmap = mergedBitmap,
                    filename = filename
                )

                if (savedUri != null) {
                    // Update the base image in SessionManager
                    sessionManager.setBaseImage(savedUri)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            statusText = application.getString(R.string.status_changes_applied),
                            processedImage = null,
                            backgroundImage = null,
                            isShowingBaseImage = false
                        )
                    }

                    // Emit navigation event
                    handleIntent(ChangerIntent.NavigateToNextScreen)
                } else {
                    _state.update {
                        it.copy(
                            errorMessage = application.getString(R.string.error_saving_image),
                            isError = true,
                            isLoading = false
                        )
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        errorMessage = application.getString(R.string.error_merging_images),
                        isError = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun navigateToNextScreen() {
        _state.update { it.copy(navigateToNextScreen = true) }
    }
}