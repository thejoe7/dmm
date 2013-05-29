package joewu.dmm;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fima.cardsui.objects.Card;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormatter;


/**
 * Created by joe7wu on 2013-05-25.
 */
public class CountdownCard extends Card {

    private DateTimeFormatter format;
    private Countdown countdown;

    public CountdownCard(Countdown countdown, DateTimeFormatter format, boolean hasOverflow, boolean isClickable) {
        super(countdown.title, countdown.description, countdown.color, countdown.color, hasOverflow, isClickable);
        this.countdown = countdown;
        this.format = format;
    }

    @Override
    public View getCardContent(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_countdown, null);

        ((ImageView) v.findViewById(R.id.card_stripe)).setBackgroundColor(Color.parseColor(countdown.color));

        ((TextView) v.findViewById(R.id.card_title)).setText(countdown.title);
        ((TextView) v.findViewById(R.id.card_title)).setTextColor(Color.parseColor(countdown.color));

        DateTime today = DateTime.now();
        int daysDiff = countdown.getDaysDiff(today);
        if (daysDiff >= 0) {
            ((TextView) v.findViewById(R.id.card_countdown)).setText(Integer.toString(daysDiff));
            ((TextView) v.findViewById(R.id.card_days_left)).setText(context.getString(R.string.card_days_left));
        } else {
            ((TextView) v.findViewById(R.id.card_countdown)).setText(Integer.toString(-daysDiff));
            ((TextView) v.findViewById(R.id.card_days_left)).setText(context.getString(R.string.card_days_past));
        }
        ((TextView) v.findViewById(R.id.card_date)).setText(format.print(countdown.date));

        if (description != "") {
            ((TextView) v.findViewById(R.id.card_description)).setText(countdown.description);
            ((TextView) v.findViewById(R.id.card_description)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) v.findViewById(R.id.card_description)).setVisibility(View.GONE);
        }

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
