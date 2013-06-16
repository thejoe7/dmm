package joewu.dmm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.PushbackInputStream;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidget extends AppWidgetProvider {

    public static String COUNTDOWN_WIDGET_UPDATE_TOKEN = "COUNTDOWN_WIDGET_UPDATED_BY_ALARM";

    private static AlarmManager sAlarmManager;
    private static PendingIntent sPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (COUNTDOWN_WIDGET_UPDATE_TOKEN.equals(intent.getAction()) && intent.getExtras() != null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context.getPackageName(), CountdownWidget.class.getName());
            int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
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
            String uuid = AppPreferences.getWidgetUuid(sharedPref, appWidgetId);
            AppPreferences.removeWidgetForCountdownItem(sharedPref, uuid, appWidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }

    public static RemoteViews buildRemoteViews(final Context context, final int appWidgetId, String uuid, String alias) {
        final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_countdown);
        CountdownItem countdown = AppPreferences.getCountdownItemById(PreferenceManager.getDefaultSharedPreferences(context), uuid);
        if (uuid.isEmpty() || countdown == null) {
            rv.setTextViewText(R.id.widget_countdown, "0");
            rv.setImageViewResource(R.id.widget_stripe, R.color.grey);
        } else {
            int daysDiff = countdown.getDaysDiff(DateTime.now());
            if (daysDiff >= 0) {
                rv.setTextViewText(R.id.widget_countdown, Integer.toString(daysDiff));
                rv.setTextColor(R.id.widget_countdown, context.getResources().getColor(android.R.color.black));
            } else {
                rv.setTextViewText(R.id.widget_countdown, Integer.toString(-daysDiff));
                rv.setTextColor(R.id.widget_countdown, context.getResources().getColor(R.color.dark_gray));
            }
            int colorRes;
            switch (countdown.color) {
                case RED:
                    colorRes = R.drawable.tile_red;
                    break;
                case YELLOW:
                    colorRes = R.drawable.tile_yellow;
                    break;
                case GREEN:
                    colorRes = R.drawable.tile_green;
                    break;
                case BLUE:
                    colorRes = R.drawable.tile_blue;
                    break;
                case PURPLE:
                    colorRes = R.drawable.tile_purple;
                    break;
                default:
                    colorRes = R.drawable.tile_gray;
                    break;
            }
            rv.setImageViewResource(R.id.widget_stripe, colorRes);
        }
        rv.setTextViewText(R.id.widget_alias, alias);

        return rv;
    }

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final RemoteViews views = buildRemoteViews(context, appWidgetId, AppPreferences.getWidgetUuid(sharedPref, appWidgetId), AppPreferences.getWidgetAlias(sharedPref, appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void saveAlarmManager(AlarmManager am, PendingIntent pi) {
        sAlarmManager = am;
        sPendingIntent = pi;
    }
}
