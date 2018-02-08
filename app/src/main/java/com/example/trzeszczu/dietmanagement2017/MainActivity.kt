package com.example.trzeszczu.dietmanagement2017

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.NumberPicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener {

    private var userId = 0
    private val choseDate = DateSelector()

    private fun showNumberPicker() {
        val newFragment = NumberSelector()
        newFragment.setValueChangeListener(this)
        newFragment.show(fragmentManager, "number picker")
    }

    override fun onValueChange(p0: NumberPicker, p1: Int, p2: Int) {
        Toast.makeText(this, "Selected weight: " + p0.value + " kg.", Toast.LENGTH_SHORT).show()
        weight_value.text = p0.value.toString()
        val weight = Weight(p0.value.toString())
        weight.addWeight(userId, choseDate.returnDate)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val menu = Menu()
        when (item.itemId)
        {
            R.id.menu_profile -> menu.goToUserProfile(this, userId)
            R.id.menu_log_out -> menu.logOut(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userId = intent.getIntExtra("user_id", 0)

        choseDate.selectDateUsingTextView(pick_date_view,this,
                fun () {
                    thread {
                        val dataLoader = DataLoader(this)
                        dataLoader.loadAndPopulate(userId, choseDate.returnDate)
                    }
                })

        add_meal_button.setOnClickListener {
            val intent = android.content.Intent(this, AddMealActivity::class.java)
            val date = choseDate.returnDate
            intent.putExtra("date", date)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        add_activity_button.setOnClickListener {
            val intent = android.content.Intent(this, AddDisciplineActivity::class.java)
            val date = choseDate.returnDate
            intent.putExtra("date", date)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        enter_weight_button.setOnClickListener {
            showNumberPicker()
        }
    }

    override fun onResume() {
        super.onResume()
        if (userId != 0 && choseDate.returnDate != "Chose date!") {
            thread {
                val dataLoader = DataLoader(this)
                dataLoader.loadAndPopulate(userId, choseDate.returnDate)
            }
        }
    }
}
