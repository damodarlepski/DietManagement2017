package com.example.trzeszczu.dietmanagement2017

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.*

internal class TimeSelector(private val editText: EditText, private val ctx: Context) : OnFocusChangeListener, OnTimeSetListener {
    private val myCalendar: Calendar

    init {
        this.editText.onFocusChangeListener = this
        this.myCalendar = Calendar.getInstance()
        myCalendar.set(Calendar.HOUR_OF_DAY, 0)
        myCalendar.set(Calendar.MINUTE, 0)
        myCalendar.set(Calendar.SECOND, 0)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            val hour = myCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = myCalendar.get(Calendar.MINUTE)
            TimePickerDialog(ctx, this, hour, minute, true).show()
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        myCalendar.set(Calendar.MINUTE, minute)

        val myFormat = "HH:mm:ss"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        this.editText.setText(sdf.format(myCalendar.time))
    }
}
