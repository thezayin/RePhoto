package com.thezayin.components.beforeafter

import androidx.compose.ui.graphics.GraphicsLayerScope

/**
 * Update graphic layer with [zoomState]
 */
internal fun GraphicsLayerScope.update(zoomState: ZoomState) {
    // Set zoom
    val zoom = zoomState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    // Set pan
    val pan = zoomState.pan
    val translationX = pan.x
    val translationY = pan.y
    this.translationX = translationX
    this.translationY = translationY

    // Set rotation
    this.rotationZ = zoomState.rotation
}
