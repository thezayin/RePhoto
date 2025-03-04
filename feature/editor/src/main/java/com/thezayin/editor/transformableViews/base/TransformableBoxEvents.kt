package com.thezayin.editor.transformableViews.base

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

sealed class TransformableBoxEvents {
    abstract val id: String

    data class UpdateTransformation(
        override val id: String,
        val dragAmount: Offset,
        val zoomAmount: Float,
        val rotationChange: Float,
    ): TransformableBoxEvents()

    data class UpdateBoxBorder(
        override val id: String,
        val innerBoxSize: Size,
    ): TransformableBoxEvents()

    data class OnCloseClicked(override val id: String): TransformableBoxEvents()
    data class OnTapped(override val id: String, val textViewState: TransformableTextBoxState?): TransformableBoxEvents()
}