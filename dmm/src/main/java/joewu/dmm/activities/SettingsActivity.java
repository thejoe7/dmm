package joewu.dmm.activities;

import android.app.ActionBar;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import joewu.dmm.services.StartService;
import joewu.dmm.utility.PreferencesUtils;
import joewu.dmm.R;
import joewu.dmm.widgets.ListWidget;

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

	    // private Preference hidePastEventsPref;
        // private Preference noChangelogPref;
	    private Preference dateFormatPref;
	    private Preference devPref;
        private Preference verPref;

	    private static final DateTime date = new DateTime(2014, 1, 31, 0, 0);

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.layout.activity_settings);

	        // foldPastEventsPref = hidePreference(HIDE_PAST_EVENTS);
	        dateFormatPref = findPreference("KEY_DATE_FORMAT");
            devPref = findPreference("KEY_DEVELOPER");
            verPref = findPreference("KEY_VERSION");

            DateTimeFormatter format = PreferencesUtils.getDateFormat(getPreferenceManager().getSharedPreferences(), getResources().getString(R.string.default_date_format));
	        dateFormatPref.setSummary(format.print(date));

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
			    dateFormatPref.setSummary(format.print(date));
		    }
            if (key.equals(PreferencesUtils.DATE_FORMAT) || key.equals(PreferencesUtils.HIDE_PAST_EVENTS)) {
                getActivity().sendBroadcast(new Intent(StartService.LIST_WIDGET_UPDATE_TOKEN));
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
