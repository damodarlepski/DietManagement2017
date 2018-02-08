package com.example.trzeszczu.dietmanagement2017

import android.app.Activity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.HttpUrl
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

class DataLoader(private val activity: Activity) : CommunicationWithAPI() {

    private var diaryId = 0

    private var dailyCarbs = 0f
    private var dailyFat = 0f
    private var dailyProteins = 0f

    private var totalBurned = 0f

    private var totalKcal = 0f
    private var totalCarbs = 0f
    private var totalFat = 0f
    private var totalProteins = 0f

    private var weight = 0f

    fun loadAndPopulate(userId: Int, date: String) {

        getDiary(userId, date)
        cleanTables()
        if (diaryId != 0) {
            getDailyGoal(userId)
            getTotal()
            getBurned()
            getWeight(userId, date)
            populateTables()
        }
        activateButtons()
    }

    private fun displayError() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Error while getting data from server!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDiary(userId: Int, date: String) {
        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/diary/").newBuilder()
        urlBuilder.addQueryParameter("user_id", userId.toString())
        urlBuilder.addQueryParameter("date", date)
        val url = urlBuilder.build().toString()
        synchronousHttpGet(url,
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
                    activity.runOnUiThread {
                        Toast.makeText(activity, "No data on chosen date!", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun getTotal() {

        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/meal-types/").newBuilder()
        urlBuilder.addQueryParameter("diary_id", diaryId.toString())
        val url = urlBuilder.build().toString()

        synchronousHttpGet(url,
                fun(response: Response) {
                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val jsonArray = JSONArray(responseString)
                    val size: Int = jsonArray.length()

                    for (i in 0 until size) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        if (jsonObject.getInt("total_carbs").toString() != "null") {

                            totalKcal += jsonObject.getString("total_kcal").toFloat()
                            totalCarbs += jsonObject.getString("total_carbs").toFloat()
                            totalFat += jsonObject.getString("total_fat").toFloat()
                            totalProteins += jsonObject.getString("total_proteins").toFloat()
                        }
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun getDailyGoal(userId: Int) {

        val url = "http://outqer.pythonanywhere.com/api/profile/"
        val body = RequestBody.create(FORM, "id=" + userId)
        synchronousHttpPost(url, body,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val json = JSONObject(responseString)
                    if (json.has("username")) {
                        if (json["daily_carbs"].toString() != "null") {
                            dailyCarbs = json["daily_carbs"].toString().toFloat()
                            dailyFat = json["daily_fat"].toString().toFloat()
                            dailyProteins = json["daily_proteins"].toString().toFloat()
                        }
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun getBurned() {

        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/activities/").newBuilder()
        urlBuilder.addQueryParameter("diary_id", diaryId.toString())
        val url = urlBuilder.build().toString()

        synchronousHttpGet(url,
                fun(response: Response) {
                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val jsonArray = JSONArray(responseString)
                    val size: Int = jsonArray.length()

                    for (i in 0 until size) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        if (jsonObject.getInt("calories_burn").toString() != "null") {

                            val burned = jsonObject.getString("calories_burn").toFloat()
                            val time = jsonObject.getString("time")
                            val timeArray = time.split(":")
                            totalBurned += (burned * timeArray[0].toFloat() + burned * timeArray[1].toFloat() / 60 + burned * timeArray[1].toFloat() / 360)
                        }
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun getWeight(userId: Int, date: String) {

        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/weights/").newBuilder()
        urlBuilder.addQueryParameter("user_id", userId.toString())
        val url = urlBuilder.build().toString()

        synchronousHttpGet(url,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val jsonArray = JSONArray(responseString)
                    val size: Int = jsonArray.length()

                    (0 until size)
                            .map { jsonArray.getJSONObject(it) }
                            .filter { it.getString("date").toString() == date }
                            .forEach { weight = it.getString("value").toFloat() }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun activateButtons() {

        activity.runOnUiThread {
            activity.add_meal_button.isEnabled = true
            activity.add_activity_button.isEnabled = true
            activity.enter_weight_button.isEnabled = true
        }
    }

    private fun cleanTables() {

        activity.runOnUiThread {
            activity.daily_carbs_textView.text = activity.getString(R.string.no_data)
            activity.daily_fat_textView.text = activity.getString(R.string.no_data)
            activity.daily_proteins_textView.text = activity.getString(R.string.no_data)

            activity.total_carbs_textView.text = activity.getString(R.string.no_data)
            activity.total_fat_textView.text = activity.getString(R.string.no_data)
            activity.total_proteins_textView.text = activity.getString(R.string.no_data)

            activity.remaining_carbs_textView.text = activity.getString(R.string.no_data)
            activity.remaining_fat_textView.text = activity.getString(R.string.no_data)
            activity.remaining_proteins_textView.text = activity.getString(R.string.no_data)

            activity.consumed_kcal_textView.text = activity.getString(R.string.no_data)
            activity.burned_kcal_textView.text = activity.getString(R.string.no_data)

            activity.weight_value.text = activity.getString(R.string.no_data)
        }
    }

    private fun populateTables() {

        activity.runOnUiThread {
            activity.consumed_kcal_textView.text = totalKcal.toString()
            activity.burned_kcal_textView.text = totalBurned.toString()

            activity.total_carbs_textView.text = totalCarbs.toString()
            activity.total_fat_textView.text = totalFat.toString()
            activity.total_proteins_textView.text = totalProteins.toString()

            activity.daily_carbs_textView.text = dailyCarbs.toString()
            activity.daily_fat_textView.text = dailyFat.toString()
            activity.daily_proteins_textView.text = dailyProteins.toString()

            activity.remaining_carbs_textView.text = (dailyCarbs - totalCarbs).toString()
            activity.remaining_fat_textView.text = (dailyFat - totalFat).toString()
            activity.remaining_proteins_textView.text = (dailyProteins - totalProteins).toString()

            activity.weight_value.text = weight.toString()
        }
    }
}