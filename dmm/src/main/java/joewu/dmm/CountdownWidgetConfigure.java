package joewu.dmm;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidgetConfigure extends Activity implements CountdownWidgetDialog.CountdownWidgetDialogListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Intent cancelResultValue = new Intent();
            cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_CANCELED, cancelResultValue);
        } else {
            finish();
        }

        setContentView(R.layout.widget_countdown_configure);
        CountdownWidgetDialog fragment = new CountdownWidgetDialog();
        fragment.show(getFragmentManager(), "countdownWidgetDialog");
    }

    public void onDialogPositiveClick() {
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
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
