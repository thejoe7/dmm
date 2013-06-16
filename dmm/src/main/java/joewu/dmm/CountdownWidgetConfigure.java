package joewu.dmm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.List;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidgetConfigure extends Activity implements CountdownWidgetDialog.CountdownWidgetDialogListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<CountdownItem> countdowns;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load countdown items and date format setting
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DateTimeFormatter format = AppPreferences.getDateFormat(sharedPref, getString(R.string.default_date_format));
        this.countdowns = AppPreferences.loadCountdownItems(sharedPref);


        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent cancelResultValue = new Intent();
        cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, cancelResultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        } else {
            setContentView(R.layout.widget_countdown_configure);
            CountdownWidgetDialog fragment = new CountdownWidgetDialog(countdowns, format);
            fragment.show(getFragmentManager(), "countdownWidgetDialog");
        }
    }

    public void onDialogPositiveClick(int index, String alias) {
        CountdownItem c = countdowns.get(index);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        AppPreferences.setWidgetUuid(sharedPref, appWidgetId, c.getUuid());
        AppPreferences.setWidgetAlias(sharedPref, appWidgetId, alias);
        AppPreferences.addWidgetForCountdownItem(sharedPref, c.getUuid(), appWidgetId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
//        appWidgetManager.updateAppWidget(appWidgetId, CountdownWidget.buildRemoteViews(getApplicationContext(), appWidgetId, c.getUuid(), alias));
        CountdownWidget.updateAppWidget(this, appWidgetManager, appWidgetId);

        Intent intent = new Intent(CountdownWidget.COUNTDOWN_WIDGET_UPDATE_TOKEN);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400, pendingIntent);

        CountdownWidget.saveAlarmManager(alarmManager, pendingIntent);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public void onDialogNegativeClick() {
        finish();
    }
}
