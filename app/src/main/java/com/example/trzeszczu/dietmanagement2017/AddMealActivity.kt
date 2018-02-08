package com.example.trzeszczu.dietmanagement2017

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_meal.*
import okhttp3.HttpUrl
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class AddMealActivity : AppCompatActivity() {

    private val communication = CommunicationWithAPI()

    private fun findIngredient(searchedIngredient: String, adapter: MyAdapter<String>) {
        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/products/").newBuilder()
        urlBuilder.addQueryParameter("name", searchedIngredient)
        val url = urlBuilder.build().toString()
        communication.httpGet(url,
                fun ( response: Response) {

                    this.runOnUiThread {
                        Log.v("INFO", "Succeeded")
                        val responseString = response.body()?.string()
                        Log.v("INFO", responseString)

                        val jsonArray = JSONArray(responseString)
                        val size: Int = jsonArray.length()

                        this.runOnUiThread {
                            (0 until size)
                                    .map { jsonArray.getJSONObject(it) }
                                    .forEach { adapter.add(it.getString("name")) }
                        }
                    }
                },
                fun (){
                    Log.v("INFO", "Failed")
                })
    }

    private fun addIngredient(searchedIngredient: String, listOfIngredients: MutableList<Ingredient>, amount: Int) {
        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/products/").newBuilder()
        urlBuilder.addQueryParameter("name", searchedIngredient)
        val url = urlBuilder.build().toString()
        communication.httpGet(url,
                fun (response:Response) {
                    this.runOnUiThread {

                        Log.v("INFO", "Succeeded")
                        val responseString = response.body()?.string()
                        Log.v("INFO", responseString)

                        val jsonArray = JSONArray(responseString)

                        if (jsonArray.length() == 1) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(0)

                            val ingredient = Ingredient(jsonObject.getInt("product_id"), jsonObject.getString("name"), amount)

                            val tr = TableRow(this)
                            val ingredientName = TextView(this)
                            val ingredientAmount = TextView(this)
                            ingredientName.text = ingredient.name
                            ingredientAmount.text = amount.toString()
                            tr.addView(ingredientName)
                            tr.addView(ingredientAmount)
                            ingredients_tableLayout.addView(tr)

                            listOfIngredients.add(ingredient)
                        }
                    }
                },
                fun () {
                    Log.v("INFO","Failed")
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)
        val date = intent.getStringExtra("date")
        val userId = intent.getIntExtra("user_id", 0)
        val listOfIngredients = mutableListOf<Ingredient>()

        val adapter = MyAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        product_autoCompleteTextView.setAdapter<MyAdapter<String>>(adapter)

        product_autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {
                if ((product_autoCompleteTextView as AutoCompleteTextView).isPerformingCompletion) {
                    return
                }
                if (charSequence.length < 2) {
                    return
                }

                val query = charSequence.toString()
                adapter.clear()
                findIngredient(query,adapter)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        add_ingredient_button.setOnClickListener {

            if (product_autoCompleteTextView.text.isNotEmpty() && amount_editText.text.isNotEmpty()) {

                if (ingredients_tableLayout.childCount == 0) {
                    val tr = TableRow(this)
                    val ingredientName = TextView(this)
                    val ingredientAmount = TextView(this)
                    ingredientName.text = this.getString(R.string.ingredient)
                    ingredientAmount.text = this.getString(R.string.amount)
                    tr.addView(ingredientName)
                    tr.addView(ingredientAmount)
                    ingredients_tableLayout.addView(tr)
                }

                addIngredient(product_autoCompleteTextView.text.toString(), listOfIngredients, amount_editText.text.toString().toInt())
                product_autoCompleteTextView.text.clear()
                amount_editText.text.clear()
                finish_adding_meal_button.isEnabled = true
            }
            else Toast.makeText(this, "Chose ingredient and its amount!", Toast.LENGTH_SHORT).show()
        }

        finish_adding_meal_button.setOnClickListener {

            if (listOfIngredients.count() > 0 && meal_name_editText.text.isNotEmpty()) {
                val meal = Meal(this)
                thread {
                    meal.addMeal(date, userId, meal_name_editText.text.toString(), listOfIngredients)
                    this.runOnUiThread {
                        meal_name_editText.text.clear()
                        ingredients_tableLayout.removeAllViews()
                    }
                }

            } else this.runOnUiThread { Toast.makeText(this, "Chose name for your meal and ingredients!", Toast.LENGTH_SHORT).show() }
        }
    }
}
