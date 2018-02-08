package com.example.trzeszczu.dietmanagement2017

import android.util.Log
import okhttp3.RequestBody
import okhttp3.Response

class Weight(private val weight: String) : CommunicationWithAPI() {

    fun addWeight(userId: Int, date: String) {

        val url = "http://outqer.pythonanywhere.com/api/weight/"
        val body = RequestBody.create(FORM, "user_id=$userId&date=$date&value=$weight")

        httpPost(url, body,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)
                },
                fun() {
                    Log.v("INFO", "Failed")
                })
    }
}