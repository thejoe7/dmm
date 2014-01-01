package joewu.dmm.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import joewu.dmm.services.WidgetUpdateService;
import joewu.dmm.utility.PreferencesUtils;
import joewu.dmm.R;

/**
 * Created by joe7wu on 2013-05-24.
 */
public class SettingsActivity extends PreferenceActivity {

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

        private Preference pastEventsAtTailPref;
	    private ListPreference dateFormatPref;
	    private Preference devPref;
        private Preference verPref;

	    private static final DateTime today = new DateTime(DateTime.now().getYear(), DateTime.now().getMonthOfYear(), DateTime.now().getDayOfMonth(), 0, 0);
        private String[] dateFormatValues;
        private String[] dateFormatEntries;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            dateFormatValues = getResources().getStringArray(R.array.dateFormatValues);
            dateFormatEntries = new String[dateFormatValues.length];
            for (int i = 0; i < dateFormatValues.length; i++) {
                dateFormatEntries[i] = DateTimeFormat.forPattern(dateFormatValues[i]).print(today);
            }

            addPreferencesFromResource(R.layout.activity_settings);

            pastEventsAtTailPref = findPreference("KEY_PAST_EVENTS_AT_TAIL");
	        dateFormatPref = (ListPreference) findPreference("KEY_DATE_FORMAT");
            devPref = findPreference("KEY_DEVELOPER");
            verPref = findPreference("KEY_VERSION");

            DateTimeFormatter format = PreferencesUtils.getDateFormat(getPreferenceManager().getSharedPreferences(), getResources().getString(R.string.default_date_format));
	        dateFormatPref.setSummary(format.print(today));
            dateFormatPref.setDefaultValue(dateFormatValues[4]);
            dateFormatPref.setEntryValues(dateFormatValues);
            dateFormatPref.setEntries(dateFormatEntries);

            devPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://plus.google.com/+JoeWu/about"));
                startActivity(intent);
                return true;
                }
            });
            String version = "1.0.0";
            try {
                version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("SettingsActivity", e.getLocalizedMessage());
            }
            verPref.setSummary(version);
        }

	    @Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		    if (key.equals(PreferencesUtils.DATE_FORMAT)) {
                DateTimeFormatter format = PreferencesUtils.getDateFormat(getPreferenceManager().getSharedPreferences(), getResources().getString(R.string.default_date_format));
			    dateFormatPref.setSummary(format.print(today));
		    } else if (key.equals(PreferencesUtils.HIDE_PAST_EVENTS)) {
                boolean hidePastEvent = PreferencesUtils.hidePastEvents(getPreferenceManager().getSharedPreferences());
                pastEventsAtTailPref.setEnabled(!hidePastEvent);
            }
            if (key.equals(PreferencesUtils.DATE_FORMAT) || key.equals(PreferencesUtils.HIDE_PAST_EVENTS) || key.equals(PreferencesUtils.PAST_EVENTS_AT_TAIL)) {
                WidgetUpdateService.WidgetUpdateHelper.updateListWidget(getActivity());
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
