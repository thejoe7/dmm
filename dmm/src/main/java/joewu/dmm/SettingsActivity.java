package joewu.dmm;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by joe7wu on 2013-05-24.
 */
public class SettingsActivity extends PreferenceActivity {

	public static final String FOLD_PAST_EVENTS = "KEY_FOLD_PAST_EVENTS";
	public static final String DATE_FORMAT = "KEY_DATE_FORMAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set navigation-up using app icon
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set setting content
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	    // private Preference foldPastEventsPref;
	    private Preference dateFormatPref;
	    private Preference devPref;

	    private static final DateTime date = new DateTime(2014, 1, 31, 0, 0);

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.layout.fragment_settings);

	        // foldPastEventsPref = findPreference(FOLD_PAST_EVENTS);
	        dateFormatPref = findPreference(DATE_FORMAT);
            devPref = findPreference("KEY_DEVELOPER");

	        DateTimeFormatter format = DateTimeFormat.forPattern(getPreferenceManager().getSharedPreferences().getString(DATE_FORMAT, "MMM d, yyyy"));
	        dateFormatPref.setSummary(format.print(date));
            devPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://plus.google.com/b/112703451623046920651/112703451623046920651/about"));
                    startActivity(intent);
                    return true;
                }
            });
        }

	    @Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		    if (key.equals(SettingsActivity.DATE_FORMAT)) {
			    DateTimeFormatter format = DateTimeFormat.forPattern(sharedPreferences.getString(DATE_FORMAT, "MMM d, yyyy"));
			    dateFormatPref.setSummary(format.print(date));
		    }
	    }

	    @Override
	    public void onResume() {
		    super.onResume();
		    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    }

	    @Override
	    public void onPause() {
		    super.onPause();
		    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    }
    }

}
