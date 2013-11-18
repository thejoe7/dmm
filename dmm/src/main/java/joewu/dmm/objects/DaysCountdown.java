package joewu.dmm.objects;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.UUID;

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

    public DaysCountdown(DaysCountdown o) {
        this.title = o.title;
        this.description = o.description;
        this.color = o.color;
        this.date = o.date;
        this.repeat = o.repeat;
        this.uuid = o.uuid;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof DaysCountdown)) return false;
        DaysCountdown odc = (DaysCountdown) o;
        return this.uuid.equals(odc.uuid);
    }
}
