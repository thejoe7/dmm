package joewu.dmm.objects;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.UUID;

import joewu.dmm.utility.RepeatMode;

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

    public DateTime getOriginalDate() {
        return date;
    }

    public DateTime getNextDate() {
        if (repeat == RepeatMode.None) {
            return date;
        } else {
            DateTime nextDate;
            DateTime now = DateTime.now();
            DateTime today = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
            if (repeat == RepeatMode.Month) {
                int maxDays = new DateTime(today.getYear(), today.getMonthOfYear(), 1, 0, 0).dayOfMonth().getMaximumValue();
                int days = Math.min(date.getDayOfMonth(), maxDays);
                nextDate = new DateTime(today.getYear(), today.getMonthOfYear(), days, 0, 0);
                if (!today.isBefore(nextDate)) {
                    nextDate = nextDate.plusMonths(1);
                }
            } else if (repeat == RepeatMode.Year) {
                int maxDays = new DateTime(today.getYear(), date.getMonthOfYear(), 1, 0, 0).dayOfMonth().getMaximumValue();
                int days = Math.min(date.getDayOfMonth(), maxDays);
                nextDate = new DateTime(today.getYear(), date.getMonthOfYear(), days, 0, 0);
                if (!today.isBefore(nextDate)) {
                    nextDate = nextDate.plusYears(1);
                }
            } else {
                nextDate = date.plusDays(repeat);
                while (!today.isBefore(nextDate)) {
                    nextDate = nextDate.plusDays(repeat);
                }
            }
            return nextDate;
        }
    }

    public int getDaysDiff(DateTime d2) {
        DateTime d3 = new DateTime(d2.getYear(), d2.getMonthOfYear(), d2.getDayOfMonth(), 0, 0);
        return Days.daysBetween(d3, getNextDate()).getDays();
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
