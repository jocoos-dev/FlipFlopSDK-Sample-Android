package com.jocoos.flipflop.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jocoos.flipflop.sample.main.MainActivity
import kotlinx.android.synthetic.main.front_activity.*

class FrontActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.front_activity)

        liveDemo.setOnClickListener {
            startLiveDemo()
        }

        playerDemo.setOnClickListener {
            startPlayDemo()
        }
    }

    private fun startLiveDemo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun startPlayDemo() {
        val intent = Intent(this, AVPlayerActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}