package com.example.trzeszczu.dietmanagement2017

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.NumberPicker

class NumberSelector : DialogFragment() {

    private var valueChangeListener: NumberPicker.OnValueChangeListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val numberPicker = NumberPicker(activity)

        numberPicker.minValue = 20
        numberPicker.maxValue = 150

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Set Weight")
        builder.setMessage("Set today's weight :")

        builder.setPositiveButton("OK", { _, _ ->
            valueChangeListener!!.onValueChange(numberPicker,
                    numberPicker.value, numberPicker.value)
        })

        builder.setNegativeButton("CANCEL", { _, _ ->
            valueChangeListener!!.onValueChange(numberPicker,
                    numberPicker.value, numberPicker.value)
        })

        builder.setView(numberPicker)
        return builder.create()
    }

    fun getValueChangeListener(): NumberPicker.OnValueChangeListener? {
        return valueChangeListener
    }

    fun setValueChangeListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        this.valueChangeListener = valueChangeListener
    }
}