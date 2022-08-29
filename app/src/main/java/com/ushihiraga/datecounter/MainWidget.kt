package com.ushihiraga.datecounter

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class MainWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, widgetManager: AppWidgetManager, widgetsId: IntArray) {
        val storage = context.getSharedPreferences(context.packageName + ".widgetsInfo", Context.MODE_PRIVATE)

        widgetsId.forEach { widgetId ->
            val widgetInfo = storage.getString("widget-$widgetId", "no-info") ?: "no-info"
            if (widgetInfo == "no-info") return

            val decodedInfo = decodeStringToMap(widgetInfo)
            val eventDate = LocalDate.parse(decodedInfo["date"])
            val views = RemoteViews(context.packageName, R.layout.layout_widget)

            views.setTextViewText(R.id.eventDays, absoluteDistanceBetweenDates(eventDate).toString())
            views.setTextViewText(R.id.eventDistance, distanceBetweenLabel(context, eventDate))
            widgetManager.partiallyUpdateAppWidget(widgetId, views)
        }
    }
}