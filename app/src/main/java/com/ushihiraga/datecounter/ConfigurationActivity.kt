package com.ushihiraga.datecounter

import android.appwidget.AppWidgetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.time.LocalDate

class ConfigurationActivity : AppCompatActivity() {
    private var widgetId: Int? = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    private var eventDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        setResult(RESULT_CANCELED)


    }

    fun saveWidget(view: View) {
        val eventTitle: String = findViewById<EditText>(R.id.eventTitleInput).text.toString()
        val eventDescription: String = findViewById<EditText>(R.id.eventDescriptionInput).text.toString()
        val widgetData = mapOf("title" to eventTitle, "description" to eventDescription, "time" to eventDate.toString())

        val b: Button = findViewById(view.id)
        b.text = widgetData.toString()

    }

    private fun onDateSelected(selectedDate: LocalDate) {
        val selectDateButton: Button = findViewById(R.id.eventDateInput)
        Toast.makeText(this, selectedDate.toString(), Toast.LENGTH_LONG).show()
        eventDate = selectedDate
        selectDateButton.text = selectedDate.toString()
    }

    fun showDatePicker(view: View) {
        val fragment = DatePickerFragment(::onDateSelected)
        fragment.show(supportFragmentManager, "datePicker")
    }


}