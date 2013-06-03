package joewu.dmm;

import android.util.Base64;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by joew on May 147.
 */
public class Countdown implements Serializable {

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

	public String toString() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.close();
			return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
		} catch (IOException e) {
			Log.e("Countdown", e.getMessage());
			return null;
		}
	}

	public static Countdown fromString(String s) {
		try {
			byte [] bs = Base64.decode(s, Base64.DEFAULT);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bs));
			Countdown c = (Countdown) ois.readObject();
			ois.close();
			return c;
		} catch (IOException e) {
			Log.e("Countdown", e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			Log.e("Countdown", e.getMessage());
			return null;
		}
	}

    public static class CountdownComparator implements Comparator<Countdown> {

        @Override
        public int compare(Countdown c1, Countdown c2) {
            return c1.date.compareTo(c2.date);
        }
    }

}


