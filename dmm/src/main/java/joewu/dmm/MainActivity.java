package joewu.dmm;

import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fima.cardsui.views.CardUI;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements CountdownDialog.CountdownDialogListener {

    public static int INVALID_COUNTDOWN_INDEX = -1;

    private CardUI cardsView;
	private TextView textView;
    private List<CountdownItem> countdowns;
    private boolean foldPastEvents;
    private boolean noChangelog;
    private DateTimeFormatter format;

    private boolean firstLaunch;
    private boolean newlyUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(false);

	    textView = (TextView) findViewById(R.id.main_text_view);

        countdowns = new ArrayList<CountdownItem>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.firstLaunch = AppPreferences.isFirstLaunch(sharedPref);
        this.newlyUpdate = AppPreferences.getAppVersionCode(sharedPref) < getVersion();
        AppPreferences.setFirstLaunch(sharedPref, false);
        AppPreferences.setAppVersionCode(sharedPref, getVersion());
        this.countdowns = AppPreferences.loadCountdownItems(sharedPref);
    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.foldPastEvents = AppPreferences.foldPastEvents(sharedPref);
        this.noChangelog = AppPreferences.noChangelog(sharedPref);
        this.format = AppPreferences.getDateFormat(sharedPref, getString(R.string.default_date_format));
		loadCards();
	}

    @Override
    public void onStop() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

	@Override
    public boolean onMenuItemSelected(int id, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                createCountdown();
                return true;
            case R.id.action_settings:
                showSettings();
                return true;
            default:
                return super.onMenuItemSelected(id, item);
        }
    }

	public void onDialogPositiveClick(CountdownItem countdown, int index) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        CountdownItem c = countdowns.get(index);
		if (index == INVALID_COUNTDOWN_INDEX) {
			countdowns.add(countdown);
		} else {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            for (int appWidgetId : AppPreferences.getWidgetsForCountdownItem(sharedPref, c.getUuid())) {
                appWidgetManager.updateAppWidget(appWidgetId,
                        CountdownWidget.buildRemoteViews(getApplicationContext(), appWidgetId, c.getUuid(), AppPreferences.getWidgetAlias(sharedPref, appWidgetId)));
            }
		}
		loadCards();
		AppPreferences.saveCountdownItems(sharedPref, countdowns);
	}

    private void showSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

	private void createCountdown() {
		CountdownDialog fragment = new CountdownDialog(new CountdownItem("", "", Color.RED, DateTime.now()), INVALID_COUNTDOWN_INDEX, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void editCountdown(int index) {
		CountdownItem countdown = countdowns.get(index);
		CountdownDialog fragment = new CountdownDialog(countdown, index, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void deleteCountdown(int index) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        AppPreferences.removeAllWidgetsForCountdownItem(sharedPref, countdowns.get(index).getUuid());
		countdowns.remove(index);
		loadCards();
        AppPreferences.saveCountdownItems(sharedPref, countdowns);
	}

    private void addSampleCountdowns() {
        // add default countdown for next Christmas
        DateTime today = DateTime.now();
        int year = today.getYear();
        if (today.isAfter(new DateTime(year, 12, 25, 0, 0))) {
            year += 1;
        }
        countdowns.add(new CountdownItem(getString(R.string.christmas_day), "", Color.PURPLE, year, 12, 25));
        AppPreferences.saveCountdownItems(PreferenceManager.getDefaultSharedPreferences(this), countdowns);
    }

    private void addChangeLogCountdowns() {
        // add default countdown for change log
        DateTime update = new DateTime(Integer.parseInt(getString(R.string.build_year)), Integer.parseInt(getString(R.string.build_month)), Integer.parseInt(getString(R.string.build_date)), 0, 0);
        countdowns.add(new CountdownItem(getString(R.string.app_name) + " v" + getVersionString(), getString(R.string.change_log), Color.GREEN, update));
        AppPreferences.saveCountdownItems(PreferenceManager.getDefaultSharedPreferences(this), countdowns);
    }

    private void loadCards() {

	    cardsView.clearCards();
        if (this.firstLaunch) {
            addSampleCountdowns();
            this.firstLaunch = false;
        }
        if (this.newlyUpdate && !this.noChangelog) {
            addChangeLogCountdowns();
            this.newlyUpdate = false;
        }
	    if (countdowns.size() > 0) {
		    textView.setVisibility(View.GONE);
            Collections.sort(countdowns, new CountdownItem.CountdownComparator());
	        for (int i = 0; i < countdowns.size(); i++) {
	            CountdownCard card = new CountdownCard(MainActivity.this, countdowns.get(i), format, true, false);
		        card.setArrayIndex(i);
	            if (i == 0) {
	                cardsView.addCard(card);
	            } else {
	                if (foldPastEvents && countdowns.get(i).isPast()) {
	                    cardsView.addCardToLastStack(card);
	                } else {
	                    cardsView.addCard(card);
	                }
	            }
	        }
	    } else {
		    textView.setVisibility(View.VISIBLE);
	    }
        cardsView.refresh();
    }

    public int getVersion() {
        int v = 0;
        try {
            v = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MainActivity", e.getLocalizedMessage());
        }
        return v;
    }

    public String getVersionString() {
        String s = "1.0";
        try {
            s = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MainActivity", e.getLocalizedMessage());
        }
        return s;
    }
    
}
