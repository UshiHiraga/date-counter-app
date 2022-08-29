package com.ushihiraga.datecounter

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class ConfigurationActivity : AppCompatActivity() {
    private var eventDate = LocalDate.now()
    private var eventHour = LocalTime.now()
    private var isDateSet = false
    private var isHourSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        setResult(Activity.RESULT_CANCELED)
    }

    private fun absoluteDistanceBetweenDates(newDate: LocalDate): Int {
        return abs(ChronoUnit.DAYS.between(newDate, LocalDate.now()).toInt())
    }

    private fun distanceBetweenLabel(newDate: LocalDate): String {
        val difference = absoluteDistanceBetweenDates(newDate)
        val nowTime = LocalDate.now()

        return if (newDate.isEqual(nowTime)) {
            getString(R.string.event_today)
        } else if (newDate.isBefore(nowTime)) {
            resources.getQuantityString(R.plurals.event_past, difference)
        } else {
            //"The event will be in $difference days"
            resources.getQuantityString(R.plurals.event_future, difference)
        }
    }

    fun saveWidgetData(view: View) {
        val eventTitle: String = findViewById<EditText>(R.id.eventTitleInput).text.toString()
        val eventDescription: String = findViewById<EditText>(R.id.eventDescriptionInput).text.toString()
        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val widgetData = mapOf(
            "title" to eventTitle,
            "description" to eventDescription,
            "date" to eventDate.toString(),
            "hour" to eventHour.toString()
        )

        if (eventTitle.isEmpty()) {
            Toast.makeText(this, R.string.alert_title, Toast.LENGTH_LONG).show()
            return
        }

        if (!isDateSet || !isHourSet) {
            Toast.makeText(this, R.string.alert_hourdate, Toast.LENGTH_LONG).show()
            return
        }

        // The required data is complete
        val views = RemoteViews(this.packageName, R.layout.layout_widget)
        views.setTextViewText(R.id.eventTitle, eventTitle)
        views.setTextViewText(R.id.eventDays, absoluteDistanceBetweenDates(eventDate).toString())
        views.setTextViewText(R.id.eventDistance, distanceBetweenLabel(eventDate))

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
            isDateSet = true
            findViewById<Button>(R.id.eventDateInput).text = selectedDate.toString()
        }

        val fragment = DatePickerFragment(onDateSelected)
        fragment.show(supportFragmentManager, "datePicker")
    }

    fun showHourPicker(view: View) {
        val onHourSelected = { selectedHour: LocalTime ->
            eventHour = selectedHour
            isHourSet = true
            findViewById<Button>(R.id.eventHourInput).text = selectedHour.toString()
        }

        val fragment = TimePickerFragment(onHourSelected)
        fragment.show(supportFragmentManager, "timePicker")
    }
}