package com.thezayin.framework.utils

import java.util.Stack

fun <T> Stack<T>.peekOrNull(): T? {
    return if (this.isNotEmpty()) this.peek() else null
}