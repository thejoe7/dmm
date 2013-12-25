package joewu.dmm.activities;

import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import joewu.dmm.adapters.DaysItemAdapter;
import joewu.dmm.fragments.ContentDialogFragment;
import joewu.dmm.services.WidgetUpdateService;
import joewu.dmm.utility.PreferencesUtils;
import joewu.dmm.R;
import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.utility.HoloColor;
import joewu.dmm.utility.RepeatMode;

public class MainActivity extends Activity {

    public static MainActivity sharedMainActivity = null;

    private ListView cardList;
    private DaysItemAdapter adapter;

    private boolean hidePastEvents;
    private boolean noChangelog;
    private DateTimeFormatter format;

    private boolean firstLaunch;
    private boolean newlyUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardList = (ListView) findViewById(R.id.lv_countdowns);
        cardList.setEmptyView(findViewById(R.id.tv_empty_message));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        format = PreferencesUtils.getDateFormat(sharedPref, getString(R.string.default_date_format));
        adapter = new DaysItemAdapter(this, format, getFragmentManager(), new ArrayList<DaysCountdown>());
        cardList.setAdapter(adapter);

        firstLaunch = PreferencesUtils.isFirstLaunch(sharedPref);
        newlyUpdate = PreferencesUtils.getAppVersionCode(sharedPref) < getVersion();
        PreferencesUtils.setFirstLaunch(sharedPref, false);
        PreferencesUtils.setAppVersionCode(sharedPref, getVersion());

        sharedMainActivity = this;
    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        hidePastEvents = PreferencesUtils.hidePastEvents(sharedPref);
        noChangelog = PreferencesUtils.noChangelog(sharedPref);
        format = PreferencesUtils.getDateFormat(sharedPref, getString(R.string.default_date_format));

        adapter.clear();
        adapter.setFormat(format);
        if (firstLaunch) {
            adapter.add(getSampleCountdowns());
            firstLaunch = false;
        }
        if (newlyUpdate && !noChangelog) {
            adapter.add(getChangeLogCountdown());
            newlyUpdate = false;
        }
        List<DaysCountdown> filtered = new ArrayList<DaysCountdown>();
        List<DaysCountdown> objects = PreferencesUtils.loadDaysCountdowns(sharedPref);
        if (objects != null) {
            if (hidePastEvents) {
                for (DaysCountdown dc : objects) {
                    if (!dc.isPast()) filtered.add(dc);
                }
            } else {
                filtered = objects;
            }
            adapter.addObjects(filtered);
        }
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
            case R.id.action_rate:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                startActivity(intent);
                return true;
            default:
                return super.onMenuItemSelected(id, item);
        }
    }

    public void scrollCardListTo(DaysCountdown countdown) {
        cardList.smoothScrollToPosition(adapter.getPosition(countdown));
    }

    private void showSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

	private void createCountdown() {
        DateTime now = DateTime.now();
        DateTime today = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);
        ContentDialogFragment dialogFragment = new ContentDialogFragment(new DaysCountdown("", "", HoloColor.RedLight, today, RepeatMode.None), true, new ContentDialogFragment.CountdownDialogListener() {
            @Override
            public void onDialogPositiveClick(DaysCountdown countdown) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                adapter.add(countdown);
                PreferencesUtils.saveDaysCountdown(sharedPref, countdown);
                scrollCardListTo(countdown);
                WidgetUpdateService.WidgetUpdateHelper.updateListWidget(MainActivity.this);
            }
        });
        dialogFragment.show(getFragmentManager(), "countdownDialog");
	}

    private DaysCountdown getSampleCountdowns() {
        DateTime today = DateTime.now();
        int year = today.getYear();
        if (today.isAfter(new DateTime(year, 12, 25, 0, 0))) {
            year += 1;
        }
        DaysCountdown countdown = new DaysCountdown(getString(R.string.christmas_day), "", HoloColor.PurpleLight, year, 12, 25, RepeatMode.None);
        PreferencesUtils.saveDaysCountdown(PreferenceManager.getDefaultSharedPreferences(this), countdown);
        return countdown;
    }

    private DaysCountdown getChangeLogCountdown() {
        DateTime updateDate = new DateTime(Integer.parseInt(getString(R.string.build_year)), Integer.parseInt(getString(R.string.build_month)), Integer.parseInt(getString(R.string.build_date)), 0, 0);
        DaysCountdown countdown = new DaysCountdown(getString(R.string.app_name) + " v" + getVersionString(), getString(R.string.change_log), HoloColor.GreenLight, updateDate, RepeatMode.None);
        PreferencesUtils.saveDaysCountdown(PreferenceManager.getDefaultSharedPreferences(this), countdown);
        return countdown;
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
