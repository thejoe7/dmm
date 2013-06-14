package joewu.dmm;

import android.util.Base64;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by joewu on 13/06/13.
 */
public class CountdownItem extends Countdown {

    private static final long serialVersionUID = 777L;

    private String uuid;

    // legacy constructor
    public CountdownItem(String title, String description, Color color, int year, int month, int day) {
        super(title, description, color, year, month, day);
        this.uuid = UUID.randomUUID().toString();
    }

    // legacy constructor
    public CountdownItem(String title, String description, Color color, DateTime date) {
        super(title, description, color, date);
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
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

    public static CountdownItem fromString(String s) {
        try {
            byte [] bs = Base64.decode(s, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bs));
            CountdownItem c = (CountdownItem) ois.readObject();
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

    public static class CountdownComparator implements Comparator<CountdownItem> {

        @Override
        public int compare(CountdownItem c1, CountdownItem c2) {
            return c1.date.compareTo(c2.date);
        }
    }
}
