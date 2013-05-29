package joewu.dmm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.fima.cardsui.views.CardUI;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity /* implements SharedPreferences.OnSharedPreferenceChangeListener */ {

    private CardUI cardsView;
    private List<Countdown> countdowns;
    private boolean foldPastEvents;
    private DateTimeFormatter format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(false);

        countdowns = new ArrayList<Countdown>();
        addSampleCountdowns();

    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		foldPastEvents = sharedPref.getBoolean(SettingsActivity.FOLD_PAST_EVENTS, false);
		format = DateTimeFormat.forPattern(sharedPref.getString(SettingsActivity.DATE_FORMAT, SettingsActivity.DATE_FORMAT_DEFAULT));

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
                // Do something for create
                return true;
            case R.id.action_settings:
                showSettingsActivity();
                return true;
            default:
                return super.onMenuItemSelected(id, item);
        }
    }

//	@Override
//	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//		Log.d("Pref", "sharedPreferences changed: " + key);
//		if (key.equals(SettingsActivity.FOLD_PAST_EVENTS)) {
//			foldPastEvents = sharedPreferences.getBoolean(SettingsActivity.FOLD_PAST_EVENTS, false);
//		} else if (key.equals(SettingsActivity.DATE_FORMAT)) {
//			format = DateTimeFormat.forPattern(sharedPreferences.getString(SettingsActivity.DATE_FORMAT, SettingsActivity.DATE_FORMAT_DEFAULT));
//		}
//		loadCards();
//	}

//	@Override
//	public void onStart() {
//		super.onResume();
//		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
//	}
//
//	@Override
//	public void onStop() {
//		super.onPause();
//		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
//	}

    private void showSettingsActivity() {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void addSampleCountdowns() {
        countdowns.add(new Countdown("App Release", "", "#99cc00", 2050, 8, 27));
        countdowns.add(new Countdown("Work Term Ends", "", "#ffbb33", 2013, 8, 30));
        countdowns.add(new Countdown("Google I/O 2013", "", "#33b5e5", 2013, 5, 15));
        countdowns.add(new Countdown("Flight to China", "AC025 @ 11:15am", "#99cc00", 2013, 4, 23));
        countdowns.add(new Countdown("Flight to Vancouver", "AC026 @ 3:40pm, delayed till 19:00pm", "#ffbb33", 2013, 5, 4));
        countdowns.add(new Countdown("Study Permit Expiry", "Remember to apply for renewal 90 days before the expiry. Consult advisors at SFU International if necessary.", "#ff4444", 2013, 12, 30));
        countdowns.add(new Countdown("Passport Expiry", "That's still quite a while from now; but the Chinese Consulate needs to be contacted two years before the expiry in maximum, so that they could perform a background check in China beforehand, which also accelerate the entire process. Ask somebody for his or her related experience, that could help a lot.", "#aa66cc", 2020, 11, 15));
    }

    private void loadCards() {
	    cardsView.clearCards();
        Collections.sort(countdowns, new Countdown.CountdownComparator());
        for (int i = 0; i < countdowns.size(); i++) {
            CountdownCard card = new CountdownCard(countdowns.get(i), format, true, false);
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
        cardsView.refresh();
    }
    
}
