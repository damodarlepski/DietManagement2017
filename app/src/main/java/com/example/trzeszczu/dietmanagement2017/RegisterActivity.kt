package com.example.trzeszczu.dietmanagement2017

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private val communication = CommunicationWithAPI()

    private fun register(username: String, password: String, email: String) {
        val url = "http://outqer.pythonanywhere.com/api/user/"
        val body = RequestBody.create(communication.FORM, "username=$username&password=$password&email=$email")
        communication.httpPost(url, body,
                fun ( response: Response){

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)
                    val json = JSONObject(responseString)

                    if (json.has("user_id")) {
                        this.runOnUiThread {
                            Toast.makeText(this,"Successfully registered!", Toast.LENGTH_SHORT).show()

                            val userId = json["user_id"] as Int
                            val intent = android.content.Intent(this, MainActivity::class.java)
                            intent.putExtra("user_id", userId)
                            startActivity(intent)
                        }
                    } else {
                        this.runOnUiThread {
                            Toast.makeText(this, "Wrong user data!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                fun (){
                    Log.v("INFO", "Failed")
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (username_register_editText.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        register_button.setOnClickListener {
            val username = username_register_editText.text.toString()
            val password = password_register_editText.text.toString()
            val email = email_register_editText.text.toString()
            register(username,password,email)
        }
    }
}
