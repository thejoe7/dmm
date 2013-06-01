package joewu.dmm;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Comparator;

/**
 * Created by joew on May 147.
 */
public class Countdown {

    public String title;
    public String description;
    public Color color;
    public DateTime date;

    public Countdown(String title, String description, Color color, int year, int month, int day) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.date = new DateTime(year, month, day, 0, 0);
    }

	public Countdown(String title, String description, Color color, DateTime date) {
		this.title = title;
		this.description = description;
		this.color = color;
		this.date = date;
	}

    public int getDaysDiff(DateTime d2) {
        return Days.daysBetween(d2, date).getDays();
    }

    public boolean isPast() {
        return (getDaysDiff(DateTime.now()) < 0);
    }

    public static class CountdownComparator implements Comparator<Countdown> {

        @Override
        public int compare(Countdown c1, Countdown c2) {
            return c1.date.compareTo(c2.date);
        }
    }

}


