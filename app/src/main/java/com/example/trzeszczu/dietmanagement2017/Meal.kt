package com.example.trzeszczu.dietmanagement2017

import android.app.Activity
import android.util.Log
import android.widget.Toast
import okhttp3.HttpUrl
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject

class Meal(private val activity: Activity) : CommunicationWithAPI() {

    private var diaryId = 0
    private var mealTypeId = 0
    private var mealId = 0

    private var totalKcal: Float = 0f
    private var totalCarbs = 0f
    private var totalFat = 0f
    private var totalProteins = 0f

    fun addMeal(date: String, userId: Int, mealName: String, listOfIngredients: MutableList<Ingredient>) {

        createDiary(date, userId)
        if (diaryId != 0) {
            createMealType(mealName)
            createMeal()
            putIngredients(listOfIngredients)
            getProductData(listOfIngredients)
            updateMealData()
        }
    }

    private fun displayError() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Error while sending data to server!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createDiary(date: String, userId: Int) {

        val url = "http://outqer.pythonanywhere.com/api/diary/"
        val body = RequestBody.create(FORM, "user_id=$userId&date=$date")

        synchronousHttpPost(url, body,
                fun (response: Response) {

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

    private fun createMealType(mealName: String) {

        val url = "http://outqer.pythonanywhere.com/api/meal-type/"
        val body = RequestBody.create(FORM, "diary_id=$diaryId&name=$mealName")

        synchronousHttpPost(url, body,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val json = JSONObject(responseString)
                    if (json.has("meal_type_id")) {
                        mealTypeId = json["meal_type_id"] as Int
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun createMeal() {

        val url = "http://outqer.pythonanywhere.com/api/meal/"
        val body = RequestBody.create(FORM, "meal_type_id=" + mealTypeId)

        synchronousHttpPost(url, body,
                fun(response: Response) {

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val json = JSONObject(responseString)
                    if (json.has("meal_id")) {
                        mealId = json["meal_id"] as Int
                    }
                },
                fun() {
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }

    private fun putIngredients(listOfIngredients: MutableList<Ingredient>) {

        val url = "http://outqer.pythonanywhere.com/api/ingredient/"

        listOfIngredients
                .map { RequestBody.create(FORM, "product_id=" + it.productId + "&meal_id=" + mealId + "&amount=" + it.amount) }
                .forEach {
                    synchronousHttpPost(url, it,
                            fun(response: Response) {

                                Log.v("INFO", "Succeeded")
                                val responseString = response.body()?.string()
                                Log.v("INFO", responseString)
                            },
                            fun() {
                                Log.v("INFO", "Failed")
                                displayError()
                            })
                }
    }

    private fun getProductData(listOfIngredients: MutableList<Ingredient>) {

        for (item in listOfIngredients) {

            val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/product/").newBuilder()
            urlBuilder.addQueryParameter("id", item.productId.toString())
            val url = urlBuilder.build().toString()

            synchronousHttpGet(url,
                    fun(response: Response) {

                        Log.v("INFO", "Succeeded")
                        val responseString = response.body()?.string()
                        Log.v("INFO", responseString)

                        val json = JSONObject(responseString)

                        if (json.has("kcal")) {
                            for (i in 0 until item.amount) {
                                totalKcal += json["kcal"].toString().toFloat()
                                totalCarbs += json["carbs"].toString().toFloat()
                                totalFat += json["fat"].toString().toFloat()
                                totalProteins += json["proteins"].toString().toFloat()
                            }
                        }
                    },
                    fun() {
                        Log.v("INFO", "Failed")
                        displayError()
                    })
        }
    }

    private fun updateMealData() {

        val url = "http://outqer.pythonanywhere.com/api/meal/"
        val put = "id=$mealId&total_kcal=$totalKcal&total_carbs=$totalCarbs&total_proteins=$totalProteins&total_fat=$totalFat"
        val body = RequestBody.create(FORM, put)
        httpPut(url, body,
                fun (response: Response){

                    Log.v("INFO", "Succeeded")
                    val responseString = response.body()?.string()
                    Log.v("INFO", responseString)

                    val json = JSONObject(responseString)
                    activity.runOnUiThread {
                        if (json.has("meal_id")) Toast.makeText(activity, "Successfully added meal", Toast.LENGTH_SHORT).show()
                    }
                },
                fun (){
                    Log.v("INFO", "Failed")
                    displayError()
                })
    }
}