package com.jocoos.flipflop.sample.util

// duration: milliseconds
fun formatDuration(duration: Long): String {
    val totalSeconds = (duration + 500) / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    return when {
        hours > 0 -> {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        }
        else -> {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}