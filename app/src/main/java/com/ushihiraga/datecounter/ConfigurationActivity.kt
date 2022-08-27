package com.ushihiraga.datecounter

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    fun absoluteDistanceBetweenDates(newDate: LocalDate): Int {
        return abs(ChronoUnit.DAYS.between(newDate, LocalDate.now()).toInt())
    }

    fun distanceBetweenLabel(newDate: LocalDate): String {
        val difference = absoluteDistanceBetweenDates(newDate)
        val nowTime = LocalDate.now()

        return if (newDate.isEqual(nowTime)) {
            //"It's today, $difference days"
            getString(R.string.event_today)

        } else if (newDate.isBefore(nowTime)) {
            //"El evento fue hace $difference días"
            resources.getQuantityString(R.plurals.event_past, difference)
        } else {
            //"El evento será en  $difference días"
            resources.getQuantityString(R.plurals.event_future, difference)
        }
    }

    fun encodeMapToString(map: Map<String, String>): String {
        return map.entries.joinToString(separator = ";")
    }

    fun decodeStringToMap(text: String): Map<String, String> {
        return text.split(";")
            .map { it.split("=") }
            .associate { it.first() to it.last() }
    }

    fun saveWidgetData(view: View) {
        val eventTitle: String = findViewById<EditText>(R.id.eventTitleInput).text.toString()
        val eventDescription: String = findViewById<EditText>(R.id.eventDescriptionInput).text.toString()

        val widgetData = mapOf(
            "title" to eventTitle,
            "description" to eventDescription,
            "date" to eventDate.toString(),
            "hour" to eventHour.toString()
        )

        val widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (eventTitle.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty.", Toast.LENGTH_LONG).show()
            return
        }

        if (!isDateSet || !isHourSet) {
            Toast.makeText(this, "Hour and date must be set.", Toast.LENGTH_LONG).show()
            return
        }

        // The required data is complete
        Toast.makeText(this, "Is not void", Toast.LENGTH_LONG).show()

        val views = RemoteViews(this.packageName, R.layout.layout_widget)
        views.setTextViewText(R.id.eventTitle, eventTitle)
        views.setTextViewText(R.id.eventDays, absoluteDistanceBetweenDates(eventDate).toString())
        views.setTextViewText(R.id.eventDistance, distanceBetweenLabel(eventDate))

        val storage = getSharedPreferences(this.packageName + "widgetsInfo", Context.MODE_PRIVATE)
        with(storage.edit()) {
            putString("widget-$widgetId", encodeMapToString(widgetData))
            apply()
        }

        Toast.makeText(this, "Data written", Toast.LENGTH_LONG).show()

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