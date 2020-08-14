package com.jocoos.flipflop.sample

import android.content.SharedPreferences
import androidx.core.content.edit

class User(val userId: String, val username: String, val profileUrl: String? = null)

class UserManager(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val KEY_USER_ID = "id"
        const val KEY_USER_NAME = "name"

        const val GUEST_ID = "0"
        const val GUEST_NAME = "Guest"
    }

    var userId: String = GUEST_ID
        set(value) {
            sharedPreferences.edit {
                putString(KEY_USER_ID, value)
            }
            field = value
        }
    var username: String = GUEST_NAME
        set(value) {
            sharedPreferences.edit {
                putString(KEY_USER_NAME, value)
            }
            field = value
        }

    val user: User
        get() = User(userId, username, null)

    init {
        userId = sharedPreferences.getString(KEY_USER_ID, GUEST_ID)!!
        username = sharedPreferences.getString(KEY_USER_NAME, GUEST_NAME)!!
    }
}