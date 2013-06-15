package joewu.dmm;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import org.joda.time.DateTime;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId: appWidgetIds) {
            RemoteViews rv = buildRemoteViews(context, appWidgetId, "", "");
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
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
                    colorRes = R.color.ics_red;
                    break;
                case YELLOW:
                    colorRes = R.color.ics_yellow;
                    break;
                case GREEN:
                    colorRes = R.color.ics_green;
                    break;
                case BLUE:
                    colorRes = R.color.ics_blue;
                    break;
                case PURPLE:
                    colorRes = R.color.ics_purple;
                    break;
                default:
                    colorRes = R.color.grey;
                    break;
            }
            rv.setImageViewResource(R.id.widget_stripe, colorRes);
        }
        rv.setTextViewText(R.id.widget_alias, alias);

        return rv;
    }

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        final RemoteViews views = buildRemoteViews(context, appWidgetId, "", "");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
