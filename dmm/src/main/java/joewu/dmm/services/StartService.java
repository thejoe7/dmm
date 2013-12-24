package joewu.dmm.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

import joewu.dmm.widgets.ListWidget;
import joewu.dmm.widgets.SingleWidget;

/**
 * Created by joewu on 16/06/13.
 */
public class StartService extends Service {
    public static String SINGLE_WIDGET_UPDATE_TOKEN = "SINGLE_WIDGET_UPDATED_BY_ALARM";
    public static String LIST_WIDGET_UPDATE_TOKEN = "LIST_WIDGET_UPDATE_BY_ALARM";

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        WidgetUpdateHelper.setSingleWidgetUpdateAlarm(this);
        WidgetUpdateHelper.setListWidgetUpdateAlarm(this);
        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class WidgetUpdateHelper {
        public static void setSingleWidgetUpdateAlarm(Context context) {
            Intent updateIntent = new Intent(SINGLE_WIDGET_UPDATE_TOKEN);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400, pendingIntent);

            SingleWidget.saveAlarmManager(alarmManager, pendingIntent);
        }

        public static void setListWidgetUpdateAlarm(Context context) {
            Intent updateIntent = new Intent(LIST_WIDGET_UPDATE_TOKEN);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400, pendingIntent);

            ListWidget.saveAlarmManager(alarmManager, pendingIntent);
        }
    }
}
