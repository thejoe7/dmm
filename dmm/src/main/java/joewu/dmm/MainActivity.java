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

	public final String PREF_COUNTDOWN_SIZE = "COUNTDOWN_SIZE";
	public final String PREF_COUNTDOWN_PREFIX = "COUNTDOWN_ITEM_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(false);

	    textView = (TextView) findViewById(R.id.main_text_view);

        countdowns = new ArrayList<Countdown>();
	    loadData();
//        addSampleCountdowns();

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
        countdowns.add(new Countdown("App Release", "", Color.GREEN, 2050, 8, 27));
        countdowns.add(new Countdown("Work Term Ends", "", Color.YELLOW, 2013, 8, 30));
        countdowns.add(new Countdown("Google I/O 2013", "", Color.BLUE, 2013, 5, 15));
        countdowns.add(new Countdown("Flight to China", "AC025 @ 11:15am", Color.GREEN, 2013, 4, 23));
        countdowns.add(new Countdown("Flight to Vancouver", "AC026 @ 3:40pm, delayed till 19:00pm", Color.YELLOW, 2013, 5, 4));
        countdowns.add(new Countdown("Study Permit Expiry", "Remember to apply for renewal 90 days before the expiry. Consult advisors at SFU International if necessary.", Color.RED, 2013, 12, 30));
        countdowns.add(new Countdown("Passport Expiry", "That's still quite a while from now; but the Chinese Consulate needs to be contacted two years before the expiry in maximum, so that they could perform a background check in China beforehand, which also accelerate the entire process. Ask somebody for his or her related experience, that could help a lot.", Color.PURPLE, 2020, 11, 15));
    }

    private void loadCards() {
	    cardsView.clearCards();
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
