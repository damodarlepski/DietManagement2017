package com.example.trzeszczu.dietmanagement2017

import android.app.Activity
import android.widget.Toast

class Menu {

    fun goToUserProfile(activity: Activity, userId: Int) {
        Toast.makeText(activity, "Opening user profile", Toast.LENGTH_SHORT).show()
        val intent = android.content.Intent(activity, UserProfileActivity::class.java)
        intent.putExtra("user_id", userId)
        activity.startActivity(intent)
    }

    fun logOut(activity: Activity) {
        Toast.makeText(activity, "Logging out!", Toast.LENGTH_SHORT).show()
        activity.finish()
    }
}