package com.ushihiraga.datecounter

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalTime

class TimePickerFragment(val onActionListener: (LocalTime) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val currentTime = LocalTime.now()
        return TimePickerDialog(requireActivity(), this, currentTime.hour, currentTime.minute, true)
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        onActionListener(LocalTime.of(hour, minute))
    }
}