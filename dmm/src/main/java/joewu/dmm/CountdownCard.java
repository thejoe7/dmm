package joewu.dmm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fima.cardsui.objects.Card;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by joe7wu on 2013-05-25.
 */
public class CountdownCard extends Card {

    private Date countdownDate;
    private DateFormat df;

    public CountdownCard(String color, String title, String date,
        String description, Boolean hasOverflow, Boolean isClickable) {
        super(title, description, color, color, hasOverflow, isClickable);
        try {
//            countdownDate = DateFormat.getDateInstance().parse(date);
            df = new SimpleDateFormat("MMM dd, yyyy");
            countdownDate = df.parse(date);
        } catch (ParseException pe) {
            Log.e("Card", "Failed to parse date string.");
        }
    }

    @Override
    public View getCardContent(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_countdown, null);

        ((ImageView) v.findViewById(R.id.card_stripe)).setBackgroundColor(Color.parseColor(titleColor));

        ((TextView) v.findViewById(R.id.card_title)).setText(titlePlay);
        ((TextView) v.findViewById(R.id.card_title)).setTextColor(Color.parseColor(titleColor));

        Calendar c = Calendar.getInstance();
        try {
            Date today = df.parse(df.format(c.getTime()));
            int days = Days.daysBetween(new DateTime(today), new DateTime(countdownDate)).getDays();
            if (days >= 0) {
                ((TextView) v.findViewById(R.id.card_countdown)).setText(Integer.toString(days));
                ((TextView) v.findViewById(R.id.card_days_left)).setText(context.getString(R.string.card_days_left));
                ((TextView) v.findViewById(R.id.card_date)).setText(context.getString(R.string.card_date_until) + df.format(countdownDate));
            } else {
                ((TextView) v.findViewById(R.id.card_countdown)).setText(Integer.toString(-days));
                ((TextView) v.findViewById(R.id.card_days_left)).setText(context.getString(R.string.card_days_past));
                ((TextView) v.findViewById(R.id.card_date)).setText(context.getString(R.string.card_date_since) + df.format(countdownDate));
            }
        } catch (ParseException pe) {
            Log.e("Card", "Failed to parse today's date string.");
        }

        ((TextView) v.findViewById(R.id.card_description)).setText(description);

        if (isClickable) {
            ((LinearLayout) v.findViewById(R.id.card_content_layout))
                    .setBackgroundResource(R.drawable.selectable_background_cardbank);
        }
        if (hasOverflow) {
            ((ImageView) v.findViewById(R.id.card_overflow)).setVisibility(View.VISIBLE);
        } else {
            ((ImageView) v.findViewById(R.id.card_overflow)).setVisibility(View.GONE);
        }

        return v;
    }
}
