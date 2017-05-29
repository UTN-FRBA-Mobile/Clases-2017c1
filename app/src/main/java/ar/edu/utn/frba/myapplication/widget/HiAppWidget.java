package ar.edu.utn.frba.myapplication.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ar.edu.utn.frba.myapplication.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link HiAppWidgetConfigureActivity HiAppWidgetConfigureActivity}
 */
public class HiAppWidget extends AppWidgetProvider {

    static final int REQUEST_CODE = 28351;

    static final int texts[] = {
            R.string.widget_0,
            R.string.widget_1,
            R.string.widget_2,
            R.string.widget_3,
            R.string.widget_4,
            R.string.widget_5,
    };

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = HiAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        int pos = HiAppWidgetConfigureActivity.loadPosPref(context, appWidgetId) % texts.length;
        // Construct the RemoteViews object
        int layoutId = pos == 3 ? R.layout.hi_app_widget_other : R.layout.hi_app_widget;
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
        views.setTextViewText(R.id.title, widgetText);
        views.setTextViewText(R.id.appwidget_text, context.getString(texts[pos]));

        views.setOnClickPendingIntent(R.id.back, getPendingIntent(context, appWidgetId, -1));
        views.setOnClickPendingIntent(R.id.next, getPendingIntent(context, appWidgetId, +1));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static PendingIntent getPendingIntent(Context context, int appWidgetId, int delta) {
        Intent intent = WidgetService.getChangeIntent(context, appWidgetId, delta);
        PendingIntent pendingIntent = PendingIntent.getService(context, REQUEST_CODE + appWidgetId * 4 + delta, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            HiAppWidgetConfigureActivity.deletePrefs(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

