package com.jocoos.flipflop.sample.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.jocoos.flipflop.FFPlayer

class FFPlayerLifecycleWrapper(lifecycle: Lifecycle, private val player: FFPlayer) :
    LifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    // lifecycle
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        player.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        player.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        player.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        player.onStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        player.onDestroy()
    }
}