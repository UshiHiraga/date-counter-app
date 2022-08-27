package com.ushihiraga.datecounter

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

class DatePickerFragment(val onActionListener: (LocalDate) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val currentDate = LocalDate.now()
        return DatePickerDialog(requireActivity(), this, currentDate.year, currentDate.monthValue, currentDate.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onActionListener(LocalDate.of(year, month, day))
    }
}