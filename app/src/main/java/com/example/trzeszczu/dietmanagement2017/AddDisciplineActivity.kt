package com.example.trzeszczu.dietmanagement2017

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_discipline.*
import okhttp3.HttpUrl
import okhttp3.Response
import org.json.JSONArray
import kotlin.concurrent.thread

class AddDisciplineActivity : AppCompatActivity() {

    private var communication = CommunicationWithAPI()

    fun findDiscipline(searchedDiscipline: String, adapter: MyAdapter<String>) {
        val urlBuilder = HttpUrl.parse("http://outqer.pythonanywhere.com/api/disciplines/").newBuilder()
        urlBuilder.addQueryParameter("name", searchedDiscipline)
        val url = urlBuilder.build().toString()
        communication.httpGet(url,
                fun(response: Response) {

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
                },
                fun() {
                    Log.v("INFO", "Failed")
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_discipline)

        val date = intent.getStringExtra("date")
        val userId = intent.getIntExtra("user_id", 0)
        val activity = Discipline(this)

        TimeSelector(time_editText, this)

        val adapter = MyAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        discipline_autoCompleteTextView.setAdapter<MyAdapter<String>>(adapter)

        discipline_autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {
                if ((discipline_autoCompleteTextView as AutoCompleteTextView).isPerformingCompletion) {
                    return
                }
                if (charSequence.length < 2) {
                    return
                }

                val query = charSequence.toString()

                adapter.clear()
                findDiscipline(query, adapter)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        finish_adding_activity_button.setOnClickListener {

            thread {
                if (discipline_autoCompleteTextView.text.isNotEmpty() && time_editText.text.isNotEmpty()) {
                    activity.choseDiscipline(discipline_autoCompleteTextView.text.toString(), time_editText.text.toString())
                    if (activity.isNotEmpty()) {
                        activity.addDiscipline(date, userId)
                    }
                    this.runOnUiThread {
                        discipline_autoCompleteTextView.text.clear()
                        time_editText.text.clear()
                    }
                } else this.runOnUiThread { Toast.makeText(this, "Chose your discipline and time spent!", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}
