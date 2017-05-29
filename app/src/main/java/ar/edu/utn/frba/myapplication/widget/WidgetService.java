package ar.edu.utn.frba.myapplication.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WidgetService extends IntentService {

    private static final String ACTION_CHANGE = "ar.edu.utn.frba.myapplication.widget.action.CHANGE";

    private static final String EXTRA_WID = "ar.edu.utn.frba.myapplication.widget.extra.WID";
    private static final String EXTRA_DELTA = "ar.edu.utn.frba.myapplication.widget.extra.DELTA";

    public WidgetService() {
        super("WidgetService");
    }

    /**
     * Starts this service to perform action change with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static Intent getChangeIntent(Context context, int widgetId, int delta) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_CHANGE);
        intent.putExtra(EXTRA_WID, widgetId);
        intent.putExtra(EXTRA_DELTA, delta);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHANGE.equals(action)) {
                final int widgetId = intent.getIntExtra(EXTRA_WID, 0);
                final int delta = intent.getIntExtra(EXTRA_DELTA, 0);
                handleActionChange(widgetId, delta);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionChange(int widgetId, int delta) {
        int current = HiAppWidgetConfigureActivity.loadPosPref(this, widgetId);
        current = (current + HiAppWidget.texts.length + delta) % HiAppWidget.texts.length;
        HiAppWidgetConfigureActivity.savePosPref(this, widgetId, current);
        HiAppWidget.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId);
    }
}
