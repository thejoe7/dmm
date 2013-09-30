package joewu.dmm.objects;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.UUID;

import joewu.dmm.values.HoloColor;
import joewu.dmm.values.RepeatMode;

/**
 * Created by joewu on 9/29/13.
 */
public class DaysCountdown {
    public static final String TAG = DaysCountdown.class.getName();

    private String uuid;
    public String title;
    public String description;
    public int color;
    public DateTime date;
    public int repeat;

    public DaysCountdown(String title, String description, int color, int year, int month, int day, int repeat) {
        this(UUID.randomUUID().toString(), title, description, color, year, month, day, repeat);
    }

    public DaysCountdown(String title, String description, int color, DateTime date, int repeat) {
        this(UUID.randomUUID().toString(), title, description, color, date, repeat);
    }

    public DaysCountdown(String uuid, String title, String description, int color, int year, int month, int day, int repeat) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.date = new DateTime(year, month, day, 0, 0);
        this.repeat = repeat;
        this.uuid = uuid;
    }

    public DaysCountdown(String uuid, String title, String description, int color, DateTime date, int repeat) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.date = date;
        this.repeat = repeat;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public int getDaysDiff(DateTime d2) {
        DateTime d3 = new DateTime(d2.getYear(), d2.getMonthOfYear(), d2.getDayOfMonth(), 0, 0);
        return Days.daysBetween(d3, date).getDays();
    }

    public boolean isPast() {
        return (getDaysDiff(DateTime.now()) < 0);
    }
}
