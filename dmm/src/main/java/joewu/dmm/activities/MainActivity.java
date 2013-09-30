package joewu.dmm.activities;

import android.app.backup.BackupManager;
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

import com.fima.cardsui.views.CardUI;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import joewu.dmm.fragments.DaysCountdownDialog;
import joewu.dmm.ui.CountdownCard;
import joewu.dmm.widgets.SingleWidget;
import joewu.dmm.utility.PreferencesUtils;
import joewu.dmm.R;
import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.utility.HoloColor;
import joewu.dmm.utility.RepeatMode;

public class MainActivity extends Activity implements DaysCountdownDialog.CountdownDialogListener {

    public static MainActivity sharedMainActivity = null;

    public static int INVALID_COUNTDOWN_INDEX = -1;

    private CardUI cardsView;
	private TextView textView;
    private List<DaysCountdown> countdowns;
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

        countdowns = new ArrayList<DaysCountdown>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.firstLaunch = PreferencesUtils.isFirstLaunch(sharedPref);
        this.newlyUpdate = PreferencesUtils.getAppVersionCode(sharedPref) < getVersion();
        PreferencesUtils.setFirstLaunch(sharedPref, false);
        PreferencesUtils.setAppVersionCode(sharedPref, getVersion());
        this.countdowns = PreferencesUtils.loadDaysCountdowns(sharedPref);
        sharedMainActivity = this;
    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.foldPastEvents = PreferencesUtils.foldPastEvents(sharedPref);
        this.noChangelog = PreferencesUtils.noChangelog(sharedPref);
        this.format = PreferencesUtils.getDateFormat(sharedPref, getString(R.string.default_date_format));
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

	public void onDialogPositiveClick(DaysCountdown countdown, int index) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (index == INVALID_COUNTDOWN_INDEX) {
			countdowns.add(countdown);
            loadCards();
            PreferencesUtils.saveDaysCountdown(sharedPref, countdown);
		} else {
            loadCards();
            PreferencesUtils.saveDaysCountdown(sharedPref, countdown);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            for (int appWidgetId : PreferencesUtils.getWidgetsForDaysCountdown(sharedPref, countdown.getUuid())) {
                SingleWidget.updateAppWidget(this, appWidgetManager, appWidgetId);
//                appWidgetManager.updateAppWidget(appWidgetId,
//                        SingleWidget.buildRemoteViews(getApplicationContext(), appWidgetId, c.getUuid(), PreferencesUtils.getWidgetAlias(sharedPref, appWidgetId)));
            }
		}
	}

    private void showSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

	private void createCountdown() {
        DateTime now = DateTime.now();
        DateTime today = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);
		DaysCountdownDialog fragment = new DaysCountdownDialog(new DaysCountdown("", "", HoloColor.RedLight, today, RepeatMode.None), INVALID_COUNTDOWN_INDEX, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void editCountdown(int index) {
		DaysCountdown countdown = countdowns.get(index);
		DaysCountdownDialog fragment = new DaysCountdownDialog(countdown, index, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void deleteCountdown(int index) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        PreferencesUtils.removeAllWidgetsForDaysCountdown(sharedPref, countdowns.get(index).getUuid());
        PreferencesUtils.removeDaysCountdownById(sharedPref, countdowns.get(index).getUuid());
		countdowns.remove(index);
		loadCards();
        PreferencesUtils.saveDaysCountdowns(sharedPref, countdowns);
	}

    private void addSampleCountdowns() {
        // add default countdown for next Christmas
        DateTime today = DateTime.now();
        int year = today.getYear();
        if (today.isAfter(new DateTime(year, 12, 25, 0, 0))) {
            year += 1;
        }
        countdowns.add(new DaysCountdown(getString(R.string.christmas_day), "", HoloColor.PurpleLight, year, 12, 25, RepeatMode.None));
        PreferencesUtils.saveDaysCountdowns(PreferenceManager.getDefaultSharedPreferences(this), countdowns);
    }

    private void addChangeLogCountdowns() {
        // add default countdown for change log
        DateTime updateDate = new DateTime(Integer.parseInt(getString(R.string.build_year)), Integer.parseInt(getString(R.string.build_month)), Integer.parseInt(getString(R.string.build_date)), 0, 0);
        countdowns.add(new DaysCountdown(getString(R.string.app_name) + " v" + getVersionString(), getString(R.string.change_log), HoloColor.GreenLight, updateDate, RepeatMode.None));
        PreferencesUtils.saveDaysCountdowns(PreferenceManager.getDefaultSharedPreferences(this), countdowns);
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
            Collections.sort(countdowns, new Comparator<DaysCountdown>() {
                @Override
                public int compare(DaysCountdown c1, DaysCountdown c2) {
                    return c1.date.compareTo(c2.date);
                }
            });
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
