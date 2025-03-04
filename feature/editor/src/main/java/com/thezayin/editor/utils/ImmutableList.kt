package com.thezayin.editor.utils

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(
    val items: List<T>
)