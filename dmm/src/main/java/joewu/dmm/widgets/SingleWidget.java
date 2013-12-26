package joewu.dmm.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import joewu.dmm.R;
import joewu.dmm.activities.MainActivity;
import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.services.WidgetUpdateService;
import joewu.dmm.utility.HoloColor;
import joewu.dmm.utility.PreferencesUtils;

/**
 * Created by joewu on 11/06/13.
 */
public class SingleWidget extends AppWidgetProvider {
    public static String COUNTDOWN_WIDGET_SIZE_1X1 = "COUNTDOWN_WIDGET_SIZE_1X1";
    public static String COUNTDOWN_WIDGET_SIZE_2X2 = "COUNTDOWN_WIDGET_SIZE_2X2";
    public static String COUNTDOWN_WIDGET_SIZE_1X3 = "COUNTDOWN_WIDGET_SIZE_1X3";

    private static int unitHeight = 58;
    private static int unitWidth = 64;

    private static AlarmManager sAlarmManager;
    private static PendingIntent sPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context.getPackageName(), SingleWidget.class.getName());
        int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
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
            String uuid = PreferencesUtils.getWidgetUuid(sharedPref, appWidgetId);
            PreferencesUtils.removeWidgetForDaysCountdown(sharedPref, uuid, appWidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (minWidth >= 3 * unitWidth) {
            PreferencesUtils.setWidgetSize(sharedPref, appWidgetId, COUNTDOWN_WIDGET_SIZE_1X3);
        } else if (minHeight >= 2 * unitHeight && minWidth >= 2 * unitWidth) {
            PreferencesUtils.setWidgetSize(sharedPref, appWidgetId, COUNTDOWN_WIDGET_SIZE_2X2);
        } else {
            PreferencesUtils.setWidgetSize(sharedPref, appWidgetId, COUNTDOWN_WIDGET_SIZE_1X1);
        }
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final RemoteViews views = buildRemoteViews(context, appWidgetId,
                PreferencesUtils.getWidgetSize(sharedPref, appWidgetId),
                PreferencesUtils.getWidgetUuid(sharedPref, appWidgetId),
                PreferencesUtils.getWidgetAlias(sharedPref, appWidgetId));
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent openAppIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_frame, openAppIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void saveAlarmManager(AlarmManager am, PendingIntent pi) {
        sAlarmManager = am;
        sPendingIntent = pi;
    }

    public static RemoteViews buildRemoteViews(final Context context, final int appWidgetId, String widgetSize, String uuid, String alias) {
        if (widgetSize.equals(COUNTDOWN_WIDGET_SIZE_1X1)) {
            return getRemoteViewSmall(context, R.layout.widget_single_1x1, uuid, alias);
        } else if (widgetSize.equals(COUNTDOWN_WIDGET_SIZE_2X2)) {
            return getRemoteViewSmall(context, R.layout.widget_single_2x2, uuid, alias);
        } else if (widgetSize.equals(COUNTDOWN_WIDGET_SIZE_1X3)) {
            return RemoteViewsgetRemoteViewLarge(context, R.layout.widget_single_1x3, uuid, alias);
        } else {
            // return 1x1 widget by default
            return getRemoteViewSmall(context, R.layout.widget_single_1x1, uuid, alias);
        }
    }

    private static RemoteViews getRemoteViewSmall(final Context context, int layout, String uuid, String alias) {
        final RemoteViews rv = new RemoteViews(context.getPackageName(), layout);
        DaysCountdown countdown = PreferencesUtils.getDaysCountdownById(PreferenceManager.getDefaultSharedPreferences(context), uuid);

        if (uuid.isEmpty() || countdown == null) {
            rv.setTextViewText(R.id.widget_countdown, "0");
            rv.setImageViewResource(R.id.widget_stripe, R.drawable.tile_gray);
        } else {
            int daysDiff = countdown.getDaysDiff(DateTime.now());
            if (daysDiff >= 0) {
                rv.setTextViewText(R.id.widget_countdown, Integer.toString(daysDiff));
                rv.setTextColor(R.id.widget_countdown, context.getResources().getColor(R.color.dark_gray));
            } else {
                rv.setTextViewText(R.id.widget_countdown, Integer.toString(-daysDiff));
                rv.setTextColor(R.id.widget_countdown, context.getResources().getColor(android.R.color.darker_gray));
            }
            int colorRes;
            switch (countdown.color) {
                case HoloColor.RedLight:
                    colorRes = R.drawable.tile_red;
                    break;
                case HoloColor.YellowLight:
                    colorRes = R.drawable.tile_yellow;
                    break;
                case HoloColor.GreenLight:
                    colorRes = R.drawable.tile_green;
                    break;
                case HoloColor.BlueLight:
                    colorRes = R.drawable.tile_blue;
                    break;
                case HoloColor.PurpleLight:
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

    private static RemoteViews RemoteViewsgetRemoteViewLarge(final Context context, int layout, String uuid, String alias) {
        final RemoteViews rv = new RemoteViews(context.getPackageName(), layout);
        DaysCountdown countdown = PreferencesUtils.getDaysCountdownById(PreferenceManager.getDefaultSharedPreferences(context), uuid);

        if (uuid.isEmpty() || countdown == null) {
            rv.setTextViewText(R.id.widget_countdown, "0");
            rv.setTextViewText(R.id.widget_date, context.getString(R.string.widget_days_left) + " " + context.getString(R.string.widget_date_unknown));
            rv.setTextColor(R.id.widget_alias, context.getResources().getColor(R.color.dark_gray));
            rv.setImageViewResource(R.id.widget_stripe, R.drawable.tile_gray);
        } else {
            DateTimeFormatter format = PreferencesUtils.getDateFormat(PreferenceManager.getDefaultSharedPreferences(context), context.getString(R.string.default_date_format));
            int daysDiff = countdown.getDaysDiff(DateTime.now());
            if (daysDiff >= 0) {
                rv.setTextViewText(R.id.widget_countdown, Integer.toString(daysDiff));
                rv.setTextViewText(R.id.widget_date, context.getString(R.string.widget_days_left) + " " + format.print(countdown.getNextDate()));
            } else {
                rv.setTextViewText(R.id.widget_countdown, Integer.toString(-daysDiff));
                rv.setTextViewText(R.id.widget_date, context.getString(R.string.widget_days_past) + " " + format.print(countdown.getNextDate()));
            }
            int colorRes;
            int tileRes;
            switch (countdown.color) {
                case HoloColor.RedLight:
                    colorRes = R.color.ics_red;
                    tileRes = R.drawable.tile_red;
                    break;
                case HoloColor.YellowLight:
                    colorRes = R.color.ics_yellow;
                    tileRes = R.drawable.tile_yellow;
                    break;
                case HoloColor.GreenLight:
                    colorRes = R.color.ics_green;
                    tileRes = R.drawable.tile_green;
                    break;
                case HoloColor.BlueLight:
                    colorRes = R.color.ics_blue;
                    tileRes = R.drawable.tile_blue;
                    break;
                case HoloColor.PurpleLight:
                    colorRes = R.color.ics_purple;
                    tileRes = R.drawable.tile_purple;
                    break;
                default:
                    colorRes = R.color.dark_gray;
                    tileRes = R.drawable.tile_gray;
                    break;
            }
            rv.setTextColor(R.id.widget_alias, context.getResources().getColor(colorRes));
            rv.setImageViewResource(R.id.widget_stripe, tileRes);
        }
        rv.setTextViewText(R.id.widget_alias, alias);

        return rv;
    }
}
