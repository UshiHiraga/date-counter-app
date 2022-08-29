package com.ushihiraga.datecounter

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class ConfigurationActivity : AppCompatActivity() {
    private var eventDate = LocalDate.now()
    private var eventHour = LocalTime.now()

    private val eventTitleInput = findViewById<EditText>(R.id.eventTitleInput)
    private val eventDescriptionInput = findViewById<EditText>(R.id.eventDescriptionInput)
    private val eventHourView = findViewById<TextView>(R.id.eventHourView)
    private val eventDateView = findViewById<TextView>(R.id.eventDateView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        setResult(Activity.RESULT_CANCELED)

        eventHourView.text = eventHour.toString()
        eventDateView.text = eventDate.toString()
    }

    fun saveWidgetData(view: View) {
        val eventTitle = eventTitleInput.text.toString()

        if (eventTitle.isEmpty()) {
            Toast.makeText(this, R.string.alert_title, Toast.LENGTH_LONG).show()
            return
        }

        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val widgetData = mapOf(
            "title" to eventTitle,
            "description" to eventDescriptionInput.text.toString(),
            "date" to eventDate.toString(),
            "hour" to eventHour.toString()
        )

        // The required data is complete
        val views = RemoteViews(this.packageName, R.layout.layout_widget)
        views.setTextViewText(R.id.eventTitle, eventTitle)
        views.setTextViewText(R.id.eventDays, absoluteDistanceBetweenDates(eventDate).toString())
        views.setTextViewText(R.id.eventDistance, distanceBetweenLabel(this, eventDate))

        val storage = getSharedPreferences(this.packageName + ".widgetsInfo", Context.MODE_PRIVATE)
        with(storage.edit()) {
            putString("widget-$widgetId", encodeMapToString(widgetData))
            apply()
        }

        AppWidgetManager.getInstance(this).updateAppWidget(widgetId, views)
        setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        finish()
    }

    // Date & Time Picker.
    fun showDatePicker(view: View) {
        val onDateSelected = { selectedDate: LocalDate ->
            eventDate = selectedDate
            eventDateView.text = selectedDate.toString()
        }

        val fragment = DatePickerFragment(onDateSelected)
        fragment.show(supportFragmentManager, "datePicker")
    }

    fun showHourPicker(view: View) {
        val onHourSelected = { selectedHour: LocalTime ->
            eventHour = selectedHour
            eventHourView.text = selectedHour.toString()
        }

        val fragment = TimePickerFragment(onHourSelected)
        fragment.show(supportFragmentManager, "timePicker")
    }
}