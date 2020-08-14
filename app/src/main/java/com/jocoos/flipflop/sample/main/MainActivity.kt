package com.jocoos.flipflop.sample.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.live.LiveStreamingFrontActivity

import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        live.setOnClickListener {
            startLive()
        }
    }

    private fun startLive() {
        startActivity(Intent(this, LiveStreamingFrontActivity::class.java))
    }
}
