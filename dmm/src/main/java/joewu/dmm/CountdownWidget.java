package joewu.dmm;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_countdown);

            // set up widget view here
            remoteView.setTextViewText(R.id.widget_countdown, "100");

            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }
    }

}
