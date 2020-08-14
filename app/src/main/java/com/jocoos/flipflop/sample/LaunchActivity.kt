package com.jocoos.flipflop.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jocoos.flipflop.FFResult
import com.jocoos.flipflop.FlipFlop
import com.jocoos.flipflop.sample.main.MainActivity
import com.jocoos.flipflop.sample.util.MainCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LaunchActivity : AppCompatActivity() {
    private val scope = MainCoroutineScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launch_activity)

        // authorize user to get FlipFlop SDK instance
        scope.launch {
            when (val result = FlipFlop.authorize(FlipFlopSampleApp.userManager.user.userId, FlipFlopSampleApp.userManager.user.username)) {
                is FFResult.Success -> {
                    // store FlipFlop SDK instance
                    FlipFlopSampleApp.flipFlopInstance = result.value
                    withContext(Dispatchers.Main) {
                        initialized()
                    }
                }
                is FFResult.Failure -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LaunchActivity,
                            "FlipFlop authorization failed",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun initialized() {
        if (FlipFlopSampleApp.userManager.user.username == UserManager.GUEST_NAME) {
            // not logged in yet
            showLogin()
        } else {
            showFront()
        }

        finish()
    }

    private fun showLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showFront() {
        val intent = Intent(this, FrontActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}