package joewu.dmm.utility;

import android.content.SharedPreferences;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import joewu.dmm.Countdown;
import joewu.dmm.CountdownItem;
import joewu.dmm.widgets.SingleWidget;
import joewu.dmm.objects.DaysCountdown;

/**
 * Created by joewu on 14/06/13.
 */
public class PreferencesUtils {

    public static final String APP_FIRST_LAUNCH = "KEY_FIRST_LAUNCH";
    public static final String APP_VERSION_CODE = "KEY_VERSION_CODE";
    public static final String FOLD_PAST_EVENTS = "KEY_FOLD_PAST_EVENTS";
    public static final String DATE_FORMAT = "KEY_DATE_FORMAT";
    public static final String NO_CHANGELOG = "KEY_NO_CHANGELOG";

    public static final String PREF_ITEM_IDS = "ITEM_IDS";
    public static final String PREF_ITEM_YEAR_PREFIX = "ITEM_YEAR_";
    public static final String PREF_ITEM_MONTH_PREFIX = "ITEM_MONTH_";
    public static final String PREF_ITEM_DAY_PREFIX = "ITEM_DAY_";
    public static final String PREF_ITEM_TITLE_PREFIX = "ITEM_TITLE_";
    public static final String PREF_ITEM_DESC_PREFIX = "ITEM_DESC_";
    public static final String PREF_ITEM_COLOR_PREFIX = "ITEM_COLOR_";
    public static final String PREF_ITEM_REPEAT_PREFIX = "ITEM_REPEAT_";

    public static final String PREF_WIDGET_UUID_PREFIX = "WIDGET_UUID_WITH_ID_";
    public static final String PREF_WIDGET_ALIAS_PREFIX = "WIDGET_ALIAS_WITH_ID_";
    public static final String PREF_WIDGET_SIZE_PREFIX = "WIDGET_SIZE_WITH_ID_";

    // legacy pref keys
    public static final String PREF_COUNTDOWN_IDS = "COUNTDOWN_IDS";
    public static final String PREF_COUNTDOWN_ITEM_PREFIX = "COUNTDOWN_WITH_ID_";
    public static final String PREF_COUNTDOWN_WIDGETS_PREFIX = "COUNTDOWN_WIDGETS_WITH_ID";

    public static final String PREF_COUNTDOWN_SIZE = "COUNTDOWN_SIZE";
    public static final String PREF_COUNTDOWN_PREFIX = "COUNTDOWN_ITEM_";

    public static void saveDaysCountdowns(SharedPreferences sharedPref, List<DaysCountdown> countdowns) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> ids = new HashSet<String>();
        for (DaysCountdown dc : countdowns) {
            String uuid = dc.getUuid();
            ids.add(uuid);
            editor.putInt(PREF_ITEM_YEAR_PREFIX + uuid, dc.date.getYear());
            editor.putInt(PREF_ITEM_MONTH_PREFIX + uuid, dc.date.getMonthOfYear());
            editor.putInt(PREF_ITEM_DAY_PREFIX + uuid, dc.date.getDayOfMonth());
            editor.putString(PREF_ITEM_TITLE_PREFIX + uuid, dc.title);
            editor.putString(PREF_ITEM_DESC_PREFIX + uuid, dc.description);
            editor.putInt(PREF_ITEM_COLOR_PREFIX + uuid, dc.color);
            editor.putInt(PREF_ITEM_REPEAT_PREFIX + uuid, dc.repeat);
        }
        editor.putStringSet(PREF_ITEM_IDS, ids);
        editor.commit();
    }

    public static ArrayList<DaysCountdown> loadDaysCountdowns(SharedPreferences sharedPref) {
        ArrayList<DaysCountdown> countdowns = new ArrayList<DaysCountdown>();
        SharedPreferences.Editor editor = sharedPref.edit();

        // new days countdown items
        Set<String> ids = sharedPref.getStringSet(PREF_ITEM_IDS, new HashSet<String>());
        for (String id : ids) {
            int year = sharedPref.getInt(PREF_ITEM_YEAR_PREFIX + id, -1);
            int month = sharedPref.getInt(PREF_ITEM_MONTH_PREFIX + id, -1);
            int day = sharedPref.getInt(PREF_ITEM_DAY_PREFIX + id, -1);
            String title = sharedPref.getString(PREF_ITEM_TITLE_PREFIX + id, null);
            String desc = sharedPref.getString(PREF_ITEM_DESC_PREFIX + id, "");
            int color = sharedPref.getInt(PREF_ITEM_COLOR_PREFIX + id, HoloColor.Gray);
            int repeat = sharedPref.getInt(PREF_ITEM_REPEAT_PREFIX + id, RepeatMode.None);
            if (year != -1 && month != -1 && day != -1 && title != null) {
                DaysCountdown dc = new DaysCountdown(id, title, desc, color, year, month, day, repeat);
                countdowns.add(dc);
            }
        }

        // legacy hack for CountdownItem
        Set<String> oldIds = sharedPref.getStringSet(PREF_COUNTDOWN_IDS, null);
        if (oldIds != null) {
            editor.remove(PREF_COUNTDOWN_IDS);
            for (String id : oldIds) {
                String serialCountdown = sharedPref.getString(PREF_COUNTDOWN_ITEM_PREFIX + id, null);
                editor.remove(PREF_COUNTDOWN_ITEM_PREFIX + id);
                if (serialCountdown != null) {
                    CountdownItem c = CountdownItem.fromString(serialCountdown);
                    if (c != null) {
                        DaysCountdown dc = new DaysCountdown(id, c.title, c.description, HoloColor.convertFromColor(c.color), c.date.getYear(), c.date.getMonthOfYear(), c.date.getDayOfMonth(), RepeatMode.None);
                        countdowns.add(dc);
                        // TODO: save back dc
                        ids.add(id);
                    }
                }
            }
            editor.putStringSet(PREF_ITEM_IDS, ids);
            editor.commit();
        }

        // legacy hack for Countdown
        int size = sharedPref.getInt(PREF_COUNTDOWN_SIZE, -1);
        if (size >= 0) {
            editor.remove(PREF_COUNTDOWN_SIZE);
            for (int i = 0; i < size; i++) {
                String serialData = sharedPref.getString(PREF_COUNTDOWN_PREFIX + i, null);
                editor.remove(PREF_COUNTDOWN_PREFIX + i);
                if (serialData != null) {
                    Countdown c = Countdown.fromString(serialData);
                    if (c != null) {
                        DaysCountdown dc = new DaysCountdown(c.title, c.description, HoloColor.convertFromColor(c.color), c.date.getYear(), c.date.getMonthOfYear(), c.date.getDayOfMonth(), RepeatMode.None);
                        countdowns.add(dc);
                        // TODO: save back dc
                        ids.add(dc.getUuid());
                    }
                }
            }
            editor.putStringSet(PREF_ITEM_IDS, ids);
            editor.commit();
        }
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

    public static String getWidgetSize(SharedPreferences sharedPref, int appWidgetId) {
        return sharedPref.getString(PREF_WIDGET_SIZE_PREFIX + appWidgetId, SingleWidget.COUNTDOWN_WIDGET_SIZE_1X1);
    }

    public static void setWidgetSize(SharedPreferences sharedPref, int appWidgetId, String widgetSize) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_WIDGET_SIZE_PREFIX + appWidgetId, widgetSize);
        editor.commit();
    }

    public static void addWidgetForDaysCountdown(SharedPreferences sharedPref, String uuid, int appWidgetId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> appWidgetIds = sharedPref.getStringSet(PREF_COUNTDOWN_WIDGETS_PREFIX + uuid, new HashSet<String>());
        appWidgetIds.add(Integer.toString(appWidgetId));
        editor.putStringSet(PREF_COUNTDOWN_WIDGETS_PREFIX + uuid, appWidgetIds);
        editor.commit();
    }

    public static void removeWidgetForDaysCountdown(SharedPreferences sharedPref, String uuid, int appWidgetId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> appWidgetIds = sharedPref.getStringSet(PREF_COUNTDOWN_WIDGETS_PREFIX + uuid, new HashSet<String>());
        appWidgetIds.remove(Integer.toString(appWidgetId));
        editor.putStringSet(PREF_COUNTDOWN_WIDGETS_PREFIX + uuid, appWidgetIds);
        editor.commit();
    }

    public static ArrayList<Integer> getWidgetsForDaysCountdown(SharedPreferences sharedPref, String uuid) {
        Set<String> appWidgetIdStrs = sharedPref.getStringSet(PREF_COUNTDOWN_WIDGETS_PREFIX + uuid, new HashSet<String>());
        ArrayList<Integer> appWidgetIds = new ArrayList<Integer>();
        for (String idString : appWidgetIdStrs) {
            appWidgetIds.add(Integer.parseInt(idString));
        }
        return appWidgetIds;
    }

    public static void removeAllWidgetsForDaysCountdown(SharedPreferences sharedPref, String uuid) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(PREF_COUNTDOWN_WIDGETS_PREFIX + uuid);
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

    public static DaysCountdown getDaysCountdownById(SharedPreferences sharedPref, String uuid) {
        int year = sharedPref.getInt(PREF_ITEM_YEAR_PREFIX + uuid, -1);
        int month = sharedPref.getInt(PREF_ITEM_MONTH_PREFIX + uuid, -1);
        int day = sharedPref.getInt(PREF_ITEM_DAY_PREFIX + uuid, -1);
        String title = sharedPref.getString(PREF_ITEM_TITLE_PREFIX + uuid, null);
        String desc = sharedPref.getString(PREF_ITEM_DESC_PREFIX + uuid, "");
        int color = sharedPref.getInt(PREF_ITEM_COLOR_PREFIX + uuid, HoloColor.Gray);
        int repeat = sharedPref.getInt(PREF_ITEM_REPEAT_PREFIX + uuid, RepeatMode.None);
        if (year != -1 && month != -1 && day != -1 && title != null) {
            return new DaysCountdown(uuid, title, desc, color, year, month, day, repeat);
        } else {
            return null;
        }
    }

    public static void removeDaysCountdownById(SharedPreferences sharedPref, String uuid) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(PREF_ITEM_YEAR_PREFIX + uuid);
        editor.remove(PREF_ITEM_MONTH_PREFIX + uuid);
        editor.remove(PREF_ITEM_DAY_PREFIX + uuid);
        editor.remove(PREF_ITEM_TITLE_PREFIX + uuid);
        editor.remove(PREF_ITEM_DESC_PREFIX + uuid);
        editor.remove(PREF_ITEM_COLOR_PREFIX + uuid);
        editor.remove(PREF_ITEM_REPEAT_PREFIX + uuid);
        editor.commit();
    }

    public static void saveDaysCountdown(SharedPreferences sharedPref, DaysCountdown dc) {
        SharedPreferences.Editor editor = sharedPref.edit();
        String uuid = dc.getUuid();
        editor.putInt(PREF_ITEM_YEAR_PREFIX + uuid, dc.date.getYear());
        editor.putInt(PREF_ITEM_MONTH_PREFIX + uuid, dc.date.getMonthOfYear());
        editor.putInt(PREF_ITEM_DAY_PREFIX + uuid, dc.date.getDayOfMonth());
        editor.putString(PREF_ITEM_TITLE_PREFIX + uuid, dc.title);
        editor.putString(PREF_ITEM_DESC_PREFIX + uuid, dc.description);
        editor.putInt(PREF_ITEM_COLOR_PREFIX + uuid, dc.color);
        editor.putInt(PREF_ITEM_REPEAT_PREFIX + uuid, dc.repeat);
        Set<String> ids = sharedPref.getStringSet(PREF_ITEM_IDS, new HashSet<String>());
        ids.add(uuid);
        editor.putStringSet(PREF_ITEM_IDS, ids);
        editor.commit();
    }

}
