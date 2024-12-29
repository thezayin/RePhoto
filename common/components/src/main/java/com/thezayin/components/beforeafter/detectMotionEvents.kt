package com.thezayin.components.beforeafter

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun PointerInputScope.detectMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true
) {
    coroutineScope {
        forEachGesture {
            awaitPointerEventScope {
                // Wait for at least one pointer to press down, and set first contact position
                val down: PointerInputChange = awaitFirstDown(requireUnconsumed)
                onDown(down)

                var pointer = down
                // Main pointer is the one that is down initially
                var pointerId = down.id

                // If a move event is followed fast enough down is skipped, especially by Canvas
                // to prevent it we add delay after first touch
                var waitedAfterDown = false

                launch {
                    delay(delayAfterDownInMillis)
                    waitedAfterDown = true
                }

                while (true) {
                    val event: PointerEvent = awaitPointerEvent()

                    val anyPressed = event.changes.any { it.pressed }

                    // There are at least one pointer pressed
                    if (anyPressed) {
                        // Get pointer that is down, if first pointer is up
                        // get another and use it if other pointers are also down
                        // event.changes.first() doesn't return same order
                        val pointerInputChange =
                            event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.first()

                        // Next time will check same pointer with this id
                        pointerId = pointerInputChange.id
                        pointer = pointerInputChange

                        if (waitedAfterDown) {
                            onMove(pointer)
                        }
                    } else {
                        // All of the pointers are up
                        onUp(pointer)
                        break
                    }
                }
            }
        }
    }
}
