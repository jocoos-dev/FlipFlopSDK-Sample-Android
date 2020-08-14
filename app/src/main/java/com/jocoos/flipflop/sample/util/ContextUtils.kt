package com.jocoos.flipflop.sample.util

import android.content.Context
import android.util.TypedValue

fun Context.getDimensionSize(size: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        size.toFloat(), resources.displayMetrics).toInt()
}