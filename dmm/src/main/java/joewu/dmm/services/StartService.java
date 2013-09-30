package joewu.dmm.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

import joewu.dmm.widgets.SingleWidget;

/**
 * Created by joewu on 16/06/13.
 */
public class StartService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        setWidgetUpdateAlarm();
        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setWidgetUpdateAlarm() {
        Intent updateIntent = new Intent(SingleWidget.COUNTDOWN_WIDGET_UPDATE_TOKEN);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, updateIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400, pendingIntent);

        SingleWidget.saveAlarmManager(alarmManager, pendingIntent);
    }
}
