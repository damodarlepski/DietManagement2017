package com.example.trzeszczu.dietmanagement2017

import android.app.Activity
import android.util.Log
import android.widget.Toast
import okhttp3.HttpUrl
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

class Discipline(private val activity: Activity) : CommunicationWithAPI() {

    private var diaryId = 0
    private var disciplineId = 0
    private var disciplineName = ""
    private var time = ""

    fun addDiscipline(date: String, userId: Int) {
        createDiary(date, userId)
        if (diaryId != 0) {
            createActivity()
        }
    }

    fun isNotEmpty(): Boolean {
        return disciplineId != 0
    }

    private fun displayError() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Error while sending data to server!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayReprimend() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Please enter proper data!", Toast.LENGTH_SHORT).show()
        }
    }

    fun choseDiscipline(searchedDiscipline: String, time: String) { // Can't run on Ui thread
        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/disciplines/").newBuilder()
        urlBuilder.addQueryParameter("name", searchedDiscipline)
        val url = urlBuilder.build().toString()
        synchronousHttpGet(url,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val jsonArray = JSONArray(responseString)
                    if (jsonArray.length() == 1) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(0)

                        disciplineId = jsonObject.getInt("id")
                        disciplineName = jsonObject.getString("name")
                        this.time = time
                    } else {
                        displayReprimend()
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun createDiary(date: String, userId: Int) {

        val url = "http://outqer.pythonanywhere.com/api/diary/"
        val body = RequestBody.create(FORM, "user_id=$userId&date=$date")

        synchronousHttpPost(url, body,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val json = JSONObject(responseString)
                    if (json.has("diary_id")) {
                        diaryId = json.getInt("diary_id")
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun createActivity() {
        val url = "http://outqer.pythonanywhere.com/api/activity/"
        val body = RequestBody.create(FORM, "diary_id=$diaryId&discipline_id=$disciplineId&time=$time")

        httpPost(url, body,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    activity.runOnUiThread {
                        Toast.makeText(activity, "Successfully added activity", Toast.LENGTH_SHORT).show()
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }
}
