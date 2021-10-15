package com.jocoos.flipflop.sample

import android.app.Application
import android.content.Context
import com.jocoos.flipflop.FlipFlop

class FlipFlopSampleApp : Application() {
    companion object {
        const val TAG = "FlipFlopSampleApp"
        const val KEY_VIDEO = "video"
        const val TITLE = "UNTITLED"
        const val CONTENT = "No Content"
        const val STREAMING_RTMP = "rtmp"

        var flipFlopInstance: FlipFlop? = null

        lateinit var userManager: UserManager
    }

    override fun onCreate() {
        super.onCreate()

        userManager = UserManager(applicationContext.getSharedPreferences("UserManager", Context.MODE_PRIVATE))

        // initialize FlipFlop SDK with app key and app secret
        FlipFlop.initialize("AppKey", "AppSecret")
    }
}