package com.example.trzeszczu.dietmanagement2017

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_user_profile.*
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject

class UserProfileActivity : AppCompatActivity() {

    private val communication = CommunicationWithAPI()

    private fun getUserStats(user_id: Int) {
        val url = "http://outqer.pythonanywhere.com/api/profile/"
        val body = RequestBody.create(communication.FORM,"id=" + user_id)
        communication.httpPost(url, body,
                fun (response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val json = JSONObject(responseString)
                    if(json.has("username")){
                        this.runOnUiThread {

                            if (json["daily_carbs"].toString() == "null") showSetup()
                            else carbs_textView.text = json["daily_carbs"].toString()

                            if (json["daily_fat"].toString() == "null") showSetup()
                            else fat_textView.text = json["daily_fat"].toString()

                            if (json["daily_proteins"].toString() == "null") showSetup()
                            else proteins_textView.text = json["daily_proteins"].toString()

                            if (json["height"].toString() == "null") showSetup()
                            else height_textView.text = json["height"].toString()

                            when (json["gender"].toString()) {
                                "F" -> gender_textView.text = getString(R.string.female)
                                "M" -> gender_textView.text = getString(R.string.male)
                                else -> gender_textView.text = getString(R.string.no_data)
                            }
                        }
                    }
                },
                fun (){
                    Log.v("INFO", "Failed")
                })
    }

    private fun updateProfile(user_id: Int) {

        var gender = 'O'
        when (gender_radioGroup.checkedRadioButtonId) {
            female_radioButton.id -> gender = 'F'
            male_radioButton.id -> gender = 'M'
        }

        val url = "http://outqer.pythonanywhere.com/api/user/"
        val put = "id=" + user_id + "&height=" + height_editText.text.toString() + "&gender="+ gender + "&daily_carbs=" + carbs_editText.text.toString() + "&daily_fat=" + fat_editText.text.toString() + "&daily_proteins=" + proteins_editText.text.toString()
        val body = RequestBody.create(communication.FORM, put)
        communication.httpPut(url, body,
                fun (response: Response){

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    this.runOnUiThread {
                        getUserStats(user_id)
                        showSetup()
                    }
                },
                fun (){
                    Log.v("INFO", "Failed")
                })
    }

    private fun showSetup() {
        carbs_switcher.showNext()
        fat_switcher.showNext()
        proteins_switcher.showNext()
        height_switcher.showNext()
        gender_switcher.showNext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val userId = intent.getIntExtra("user_id",0)
        getUserStats(userId)

        setup_button.setOnClickListener {
            if (setup_button.text == "Setup") {
                showSetup()
                setup_button.text = getString(R.string.ok)
            }
            else {
                updateProfile(userId)
                setup_button.text = getString(R.string.setup)
                carbs_editText.text.clear()
                fat_editText.text.clear()
                proteins_editText.text.clear()
                height_editText.text.clear()
                gender_radioGroup.clearFocus()
            }
        }
    }
}
