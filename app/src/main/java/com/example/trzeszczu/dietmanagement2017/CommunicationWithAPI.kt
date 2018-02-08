package com.example.trzeszczu.dietmanagement2017

import okhttp3.*
import java.io.IOException

open class CommunicationWithAPI {
    private val client = OkHttpClient()
    val FORM = MediaType.parse("application/x-www-form-urlencoded")!!

    fun httpPost(url: String, body: RequestBody, success: (response: Response) -> Unit, failure: () -> Unit){
        val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failure()
            }

            override fun onResponse(call: Call, response: Response) {
                success(response)
            }
        })
    }

    fun httpGet(url: String, success: (response: Response) -> Unit, failure: () -> Unit) {
        val request = Request.Builder()
                .url(url)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failure()
            }

            override fun onResponse(call: Call, response: Response) {
                success(response)
            }
        })
    }

    fun httpPut(url: String, body: RequestBody, success: (response: Response) -> Unit, failure: () -> Unit){
        val request = Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Accept", "application/json")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failure()
            }

            override fun onResponse(call: Call, response: Response) {
                success(response)
            }
        })
    }

    fun synchronousHttpPost(url: String, body: RequestBody, success: (response: Response) -> Unit, failure: () -> Unit) {
        val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json")
                .build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            success(response)
        } else failure()
    }

    fun synchronousHttpGet(url: String, success: (response: Response) -> Unit, failure: () -> Unit) {
        val request = Request.Builder()
                .url(url)
                .build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            success(response)
        } else failure()
    }
}