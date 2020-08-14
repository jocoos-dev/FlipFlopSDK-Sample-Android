package com.jocoos.flipflop.sample

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_activity.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var userName = "Guest"

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        editUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                userName = s.toString().trim()
            }
        })

        loginButton.setOnClickListener {
            if (!userName.isBlank()) {
                login(userName)
            }
        }
    }

    private fun login(name: String) {
        FlipFlopSampleApp.userManager.userId = createRandomId()
        FlipFlopSampleApp.userManager.username = name

        showMain()
        finish()
    }

    private fun showMain() {
        startActivity(Intent(this, LaunchActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun createRandomId(): String = "${Date().time}${Random().nextInt(9999)}".reversed()
}