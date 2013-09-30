package joewu.dmm.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.List;

import joewu.dmm.fragments.SingleWidgetDialog;
import joewu.dmm.widgets.SingleWidget;
import joewu.dmm.utility.PreferencesUtils;
import joewu.dmm.R;
import joewu.dmm.objects.DaysCountdown;

/**
 * Created by joewu on 11/06/13.
 */
public class SingleWidgetConfigureActivity extends Activity implements SingleWidgetDialog.CountdownWidgetDialogListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<DaysCountdown> countdowns;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load countdown items and date format setting
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DateTimeFormatter format = PreferencesUtils.getDateFormat(sharedPref, getString(R.string.default_date_format));
        this.countdowns = PreferencesUtils.loadDaysCountdowns(sharedPref);


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
            SingleWidgetDialog fragment = new SingleWidgetDialog(countdowns, format);
            fragment.show(getFragmentManager(), "countdownWidgetDialog");
        }
    }

    public void onDialogPositiveClick(int index, String alias) {
        if (index != -1) {
            DaysCountdown c = countdowns.get(index);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            PreferencesUtils.setWidgetUuid(sharedPref, appWidgetId, c.getUuid());
            PreferencesUtils.setWidgetAlias(sharedPref, appWidgetId, alias);
            PreferencesUtils.setWidgetSize(sharedPref, appWidgetId, SingleWidget.COUNTDOWN_WIDGET_SIZE_1X1);
            PreferencesUtils.addWidgetForDaysCountdown(sharedPref, c.getUuid(), appWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            SingleWidget.updateAppWidget(this, appWidgetManager, appWidgetId);

            Intent intent = new Intent(SingleWidget.COUNTDOWN_WIDGET_UPDATE_TOKEN);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400, pendingIntent);

            SingleWidget.saveAlarmManager(alarmManager, pendingIntent);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
        }
        finish();
    }

    public void onDialogNegativeClick() {
        finish();
    }
}
