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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by joe7wu on 2013-05-25.
 */
public class CountdownCard extends Card {

    private Date countdownDate;

    public CountdownCard(String color, String title, String date,
        String description, Boolean hasOverflow, Boolean isClickable) {
        super(title, description, color, color, hasOverflow, isClickable);
        try {
//            countdownDate = DateFormat.getDateInstance().parse(date);
            DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
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
