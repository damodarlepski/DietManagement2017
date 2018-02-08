package com.example.trzeszczu.dietmanagement2017

import android.app.Activity
import android.app.DatePickerDialog
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class DateSelector {

    var returnDate: String = "Chose date!"

    fun selectDateUsingTextView (pickDateView: TextView, activity: Activity, activateButtons : () -> Unit) {

        pickDateView.text = returnDate
        val pickedDate = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            pickedDate.set(Calendar.YEAR, year)
            pickedDate.set(Calendar.MONTH, monthOfYear)
            pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            pickDateView.text = sdf.format(pickedDate.time)

            val returnFormat = "yyyy-MM-dd"
            val returnSfd =  SimpleDateFormat(returnFormat, Locale.ENGLISH)
            returnDate = returnSfd.format(pickedDate.time).toString()

            activateButtons()
        }

        pickDateView.setOnClickListener {
            DatePickerDialog(activity, dateSetListener,
                    pickedDate.get(Calendar.YEAR),
                    pickedDate.get(Calendar.MONTH),
                    pickedDate.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}