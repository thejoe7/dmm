package joewu.dmm;

import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.views.CardUI;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements CountdownDialog.CountdownDialogListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private CardUI cardsView;
	private TextView textView;
    private List<Countdown> countdowns;
    private boolean foldPastEvents;
    private DateTimeFormatter format;

    private boolean firstLaunch;

	public final String PREF_COUNTDOWN_SIZE = "COUNTDOWN_SIZE";
	public final String PREF_COUNTDOWN_PREFIX = "COUNTDOWN_ITEM_";
    public final String APP_FIRST_LAUNCH = "KEY_FIRST_LAUNCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(false);

	    textView = (TextView) findViewById(R.id.main_text_view);

        countdowns = new ArrayList<Countdown>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.firstLaunch = sharedPref.getBoolean(APP_FIRST_LAUNCH, true);

        loadData();

    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		foldPastEvents = sharedPref.getBoolean(SettingsActivity.FOLD_PAST_EVENTS, false);
		format = DateTimeFormat.forPattern(sharedPref.getString(SettingsActivity.DATE_FORMAT, getString(R.string.default_date_format)));

		loadCards();
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
	}

	public void onDialogPositiveClick(Countdown countdown, boolean isNew) {
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
		CountdownDialog fragment = new CountdownDialog(new Countdown("", "", Color.RED, DateTime.now()), true, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void editCountdown(int index) {
		Countdown countdown = countdowns.get(index);
		CountdownDialog fragment = new CountdownDialog(countdown, false, format);
		fragment.show(getFragmentManager(), "countdownDialog");
	}

	public void deleteCountdown(int index) {
		countdowns.remove(index);
		loadCards();
		saveData();
	}

	public void saveData() {
		List<String> serializedCountdowns = new ArrayList<String>();
		for (Countdown c : countdowns) {
			String cs = c.toString();
			if (cs != null && !cs.isEmpty()) {
				serializedCountdowns.add(cs);
			}
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(PREF_COUNTDOWN_SIZE, serializedCountdowns.size());
		for (int i = 0; i < serializedCountdowns.size(); i++) {
			editor.putString(PREF_COUNTDOWN_PREFIX + i, serializedCountdowns.get(i));
		}
		editor.commit();
	}

	public void loadData() {
		List<String> serializedCountdowns = new ArrayList<String>();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int size = sharedPref.getInt(PREF_COUNTDOWN_SIZE, 0);
		for (int i = 0; i < size; i++) {
			String serialData = sharedPref.getString(PREF_COUNTDOWN_PREFIX + i, "");
			if (!serialData.isEmpty()) {
				serializedCountdowns.add(serialData);
			}
		}

		countdowns.clear();
		for (String s : serializedCountdowns) {
			Countdown c = Countdown.fromString(s);
			if (c != null) {
				countdowns.add(c);
			}
		}
	}

    private void addSampleCountdowns() {
        // add default countdown for next Christmas
        DateTime today = DateTime.now();
        int year = today.getYear();
        if (today.isAfter(new DateTime(year, 12, 25, 0, 0))) {
            year += 1;
        }
        countdowns.add(new Countdown("Christmas Day", "", Color.PURPLE, year, 12, 25));

        // add default countdown for change log
        DateTime update = new DateTime(Integer.parseInt(getString(R.string.build_year)), Integer.parseInt(getString(R.string.build_month)), Integer.parseInt(getString(R.string.build_date)), 0, 0);
        countdowns.add(new Countdown("Days-- New Updates", getString(R.string.change_log), Color.GREEN, update));

        saveData();
    }

    private void loadCards() {
	    cardsView.clearCards();
        if (this.firstLaunch) {
            addSampleCountdowns();
            this.firstLaunch = false;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(APP_FIRST_LAUNCH, false);
            editor.commit();
        }
	    if (countdowns.size() > 0) {
		    textView.setVisibility(View.GONE);
            Collections.sort(countdowns, new Countdown.CountdownComparator());
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
    
}
