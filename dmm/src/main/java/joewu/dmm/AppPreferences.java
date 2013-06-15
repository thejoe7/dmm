package joewu.dmm;

import android.content.SharedPreferences;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by joewu on 14/06/13.
 */
public class AppPreferences {

    public static final String APP_FIRST_LAUNCH = "KEY_FIRST_LAUNCH";
    public static final String APP_VERSION_CODE = "KEY_VERSION_CODE";
    public static final String FOLD_PAST_EVENTS = "KEY_FOLD_PAST_EVENTS";
    public static final String DATE_FORMAT = "KEY_DATE_FORMAT";
    public static final String NO_CHANGELOG = "KEY_NO_CHANGELOG";

    public static final String PREF_COUNTDOWN_IDS = "COUNTDOWN_IDS";
    public static final String PREF_COUNTDOWN_ITEM_PREFIX = "COUNTDOWN_WITH_ID_";

    public static final String PREF_WIDGET_UUID_PREFIX = "WIDGET_UUID_WITH_ID_";
    public static final String PREF_WIDGET_ALIAS_PREFIX = "WIDGET_ALIAS_WITH_ID_";

    // legacy pref keys
    public static final String PREF_COUNTDOWN_SIZE = "COUNTDOWN_SIZE";
    public static final String PREF_COUNTDOWN_PREFIX = "COUNTDOWN_ITEM_";

    public static void saveCountdownItems(SharedPreferences sharedPref, List<CountdownItem> countdowns) {
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

    public static ArrayList<CountdownItem> loadCountdownItems(SharedPreferences sharedPref) {
        ArrayList<CountdownItem> countdowns = new ArrayList<CountdownItem>();
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

        Collections.sort(countdowns, new CountdownItem.CountdownComparator());
        return countdowns;
    }

    public static String getWidgetUuid(SharedPreferences sharedPref, int appWidgetId) {
        return sharedPref.getString(PREF_WIDGET_UUID_PREFIX + appWidgetId, "");
    }

    public static void setWidgetUuid(SharedPreferences sharedPref, int appWidgetId, String uuid) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_WIDGET_UUID_PREFIX + appWidgetId, uuid);
        editor.commit();
    }

    public static String getWidgetAlias(SharedPreferences sharedPref, int appWidgetId) {
        return sharedPref.getString(PREF_WIDGET_ALIAS_PREFIX + appWidgetId, "");
    }

    public static void setWidgetAlias(SharedPreferences sharedPref, int appWidgetId, String alias) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_WIDGET_ALIAS_PREFIX + appWidgetId, alias);
        editor.commit();
    }

    public static boolean isFirstLaunch(SharedPreferences sharedPref) {
        return sharedPref.getBoolean(APP_FIRST_LAUNCH, true);
    }

    public static void setFirstLaunch(SharedPreferences sharedPref, boolean firstLaunch) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(APP_FIRST_LAUNCH, firstLaunch);
        editor.commit();
    }

    public static int getAppVersionCode(SharedPreferences sharedPref) {
        return sharedPref.getInt(APP_VERSION_CODE, 0);
    }

    public static void setAppVersionCode(SharedPreferences sharedPref, int appVersionCode) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(APP_VERSION_CODE, appVersionCode);
        editor.commit();
    }

    public static boolean foldPastEvents(SharedPreferences sharedPref) {
        return sharedPref.getBoolean(FOLD_PAST_EVENTS, false);
    }

    public static boolean noChangelog(SharedPreferences sharedPref) {
        return sharedPref.getBoolean(NO_CHANGELOG, false);
    }

    public static DateTimeFormatter getDateFormat(SharedPreferences sharedPref, String defaultFormat) {
        return DateTimeFormat.forPattern(sharedPref.getString(DATE_FORMAT, defaultFormat));
    }

    public static CountdownItem getCountdownItemById(SharedPreferences sharedPref, String uuid) {
        String serialCountdown = sharedPref.getString(PREF_COUNTDOWN_ITEM_PREFIX + uuid, "");
        if (serialCountdown.isEmpty()) {
            return null;
        } else {
            return CountdownItem.fromString(serialCountdown);
        }
    }
}
