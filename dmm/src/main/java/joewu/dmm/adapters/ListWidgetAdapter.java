package joewu.dmm.adapters;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import joewu.dmm.R;
import joewu.dmm.activities.MainActivity;
import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.utility.PreferencesUtils;

/**
 * Created by joewu on 12/23/13.
 */
public class ListWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private List<DaysCountdown> objects;
    private Context context;
    private int appWidgetId;
    private DateTimeFormatter format;

    public ListWidgetAdapter(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.format = PreferencesUtils.getDateFormat(sharedPref, context.getString(R.string.default_date_format));
        List<DaysCountdown> countdowns = PreferencesUtils.loadDaysCountdowns(sharedPref);
        if (PreferencesUtils.hidePastEvents(sharedPref)) {
            this.objects = new ArrayList<DaysCountdown>();
            for (DaysCountdown dc : countdowns) {
                if (!dc.isPast()) this.objects.add(dc);
            }
        } else {
            this.objects = countdowns;
        }
    }

    @Override
    public void onCreate() {
        // Do nothing.
    }

    @Override
    public void onDestroy() {
        // Do nothing.
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        DaysCountdown dc = objects.get(position);

//        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_cell);
//        remoteViews.setInt(R.id.iv_card_stripe, "setBackgroundResource", dc.color);
//        remoteViews.setTextViewText(R.id.tv_card_title, dc.title);
//        remoteViews.setTextColor(R.id.tv_card_title, dc.color);
//        DateTime today = DateTime.now();
//        int daysDiff = dc.getDaysDiff(today);
//        if (daysDiff >= 0) {
//            remoteViews.setTextViewText(R.id.tv_card_countdown, String.valueOf(daysDiff));
//            remoteViews.setTextViewText(R.id.tv_card_days_left, context.getString(R.string.card_days_left));
//        } else {
//            remoteViews.setTextViewText(R.id.tv_card_countdown, String.valueOf(-daysDiff));
//            remoteViews.setTextViewText(R.id.tv_card_days_left, context.getString(R.string.card_days_past));
//        }
//        remoteViews.setTextViewText(R.id.tv_card_date, format.print(dc.getNextDate()));
//        if (dc.description == null || dc.description.isEmpty()) {
//            remoteViews.setTextViewText(R.id.tv_card_description, "");
//            remoteViews.setViewVisibility(R.id.tv_card_description, View.GONE);
//        } else {
//            remoteViews.setTextViewText(R.id.tv_card_description, dc.description);
//            remoteViews.setViewVisibility(R.id.tv_card_description, View.VISIBLE);
//        }
//
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent openAppIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.rl_container, openAppIntent);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_loading_view);
        remoteViews.setTextViewText(R.id.tv_loading_text, dc.title);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.widget_loading_view);
    }

    @Override
    public void onDataSetChanged() {
        Log.e("JoeTag", "onDataSetChanged");
        // Do nothing.
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
