package joewu.dmm;

import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
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

    private CardUI cardsView;
	private TextView textView;
    private List<CountdownItem> countdowns;
    private boolean foldPastEvents;
    private boolean noChangelog;
    private DateTimeFormatter format;

    private boolean firstLaunch;
    private boolean newlyUpdate;

    public static final String APP_FIRST_LAUNCH = "KEY_FIRST_LAUNCH";
    public static final String APP_VERSION_CODE = "KEY_VERSION_CODE";
    public static final String PREF_COUNTDOWN_IDS = "COUNTDOWN_IDS";
    public static final String PREF_COUNTDOWN_ITEM_PREFIX = "COUNTDOWN_WITH_ID_";

    // legacy pref keys
    public static final String PREF_COUNTDOWN_SIZE = "COUNTDOWN_SIZE";
    public static final String PREF_COUNTDOWN_PREFIX = "COUNTDOWN_ITEM_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(false);

	    textView = (TextView) findViewById(R.id.main_text_view);

        countdowns = new ArrayList<CountdownItem>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        this.firstLaunch = sharedPref.getBoolean(APP_FIRST_LAUNCH, true);
        this.newlyUpdate = (sharedPref.getInt(APP_VERSION_CODE, 0) < getVersion());
        editor.putBoolean(APP_FIRST_LAUNCH, false);
        editor.putInt(APP_VERSION_CODE, getVersion());
        editor.commit();
        loadData();
    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		foldPastEvents = sharedPref.getBoolean(SettingsActivity.FOLD_PAST_EVENTS, false);
        noChangelog = sharedPref.getBoolean(SettingsActivity.NO_CHANGELOG, false);
		format = DateTimeFormat.forPattern(sharedPref.getString(SettingsActivity.DATE_FORMAT, getString(R.string.default_date_format)));

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

	public void onDialogPositiveClick(CountdownItem countdown, boolean isNew) {
		if (isNew) {
			countdowns.add(countdown);
		} else {
			// countdown is updated automatically
		}
		loadCards();
		saveData();
	}

    private void showSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

	private void createCountdown() {
		CountdownDialog fragment = new CountdownDialog(new CountdownItem("", "", Color.RED, DateTime.now()), true, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void editCountdown(int index) {
		CountdownItem countdown = countdowns.get(index);
		CountdownDialog fragment = new CountdownDialog(countdown, false, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void deleteCountdown(int index) {
		countdowns.remove(index);
		loadCards();
		saveData();
	}

	public void saveData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> ids = new HashSet<String>();
        for (CountdownItem c : countdowns) {
            String cs = c.toString();
            if (cs != null && !cs.isEmpty()) {
                editor.putString(PREF_COUNTDOWN_ITEM_PREFIX + c.getUuid(), cs);
                ids.add(c.getUuid());
            }
        }
        editor.putStringSet(PREF_COUNTDOWN_IDS, ids);
        editor.commit();
	}

	public void loadData() {
        countdowns.clear();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        // new countdown item format
        Set<String> ids = sharedPref.getStringSet(PREF_COUNTDOWN_IDS, new HashSet<String>());
        for (String id : ids) {
            String serialCountdown = sharedPref.getString(PREF_COUNTDOWN_ITEM_PREFIX + id, "");
            if (!serialCountdown.isEmpty()) {
                CountdownItem c = CountdownItem.fromString(serialCountdown);
                countdowns.add(c);
            }
        }

        // legacy hack
        int size = sharedPref.getInt(PREF_COUNTDOWN_SIZE, 0);
        editor.remove(PREF_COUNTDOWN_SIZE);
        if (size > 0) {
            List<String> oldCountdowns = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                String serialData = sharedPref.getString(PREF_COUNTDOWN_PREFIX + i, "");
                editor.remove(PREF_COUNTDOWN_PREFIX + i);
                if (!serialData.isEmpty()) {
                    oldCountdowns.add(serialData);
                }
            }
            for (String s : oldCountdowns) {
                Countdown c = Countdown.fromString(s);
                if (c != null) {
                    CountdownItem ci = new CountdownItem(c.title, c.description, c.color, c.date);
                    countdowns.add(ci);
                    String sci = ci.toString();
                    if (sci != null && !sci.isEmpty()) {
                        editor.putString(PREF_COUNTDOWN_ITEM_PREFIX + ci.getUuid(), sci);
                        ids.add(ci.getUuid());
                    }
                }
            }
            editor.putStringSet(PREF_COUNTDOWN_IDS, ids);
            editor.commit();
        }
	}

    private void addSampleCountdowns() {
        // add default countdown for next Christmas
        DateTime today = DateTime.now();
        int year = today.getYear();
        if (today.isAfter(new DateTime(year, 12, 25, 0, 0))) {
            year += 1;
        }
        countdowns.add(new CountdownItem(getString(R.string.christmas_day), "", Color.PURPLE, year, 12, 25));
        saveData();
    }

    private void addChangeLogCountdowns() {
        // add default countdown for change log
        DateTime update = new DateTime(Integer.parseInt(getString(R.string.build_year)), Integer.parseInt(getString(R.string.build_month)), Integer.parseInt(getString(R.string.build_date)), 0, 0);
        countdowns.add(new CountdownItem(getString(R.string.app_name) + " v" + getVersionString(), getString(R.string.change_log), Color.GREEN, update));
        saveData();
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
