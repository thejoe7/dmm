package joewu.dmm.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import joewu.dmm.R;
import joewu.dmm.activities.MainActivity;
import joewu.dmm.services.ListWidgetService;
import joewu.dmm.services.WidgetUpdateService;
import joewu.dmm.utility.PreferencesUtils;

/**
 * Created by joewu on 11/06/13.
 */
public class ListWidget extends AppWidgetProvider {
    private static AlarmManager sAlarmManager;
    private static PendingIntent sPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context.getPackageName(), ListWidget.class.getName());
        int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
        Log.e("JoeTag", intent.getAction());
        if (WidgetUpdateService.LIST_WIDGET_UPDATE_TOKEN.equals(intent.getAction())) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_countdowns);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        if (sAlarmManager != null && sPendingIntent != null) {
            sAlarmManager.cancel(sPendingIntent);
        }
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        for (int appWidgetId : appWidgetIds) {
            String uuid = PreferencesUtils.getWidgetUuid(sharedPref, appWidgetId);
            PreferencesUtils.removeWidgetForDaysCountdown(sharedPref, uuid, appWidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        Intent serviceIntent = new Intent(context, ListWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list);
        views.setRemoteAdapter(R.id.lv_countdowns, serviceIntent);
        views.setEmptyView(R.id.lv_countdowns, R.id.tv_empty_message);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent openAppIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_countdowns, openAppIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void saveAlarmManager(AlarmManager am, PendingIntent pi) {
        sAlarmManager = am;
        sPendingIntent = pi;
    }
}
