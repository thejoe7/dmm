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
import org.joda.time.format.DateTimeFormatter;


/**
 * Created by joe7wu on 2013-05-25.
 */
public class CountdownCard extends Card {

    private DateTimeFormatter format;
    private Countdown countdown;
	private int arrayIndex = -1;

	public interface CardActionListener {
		public void onCardEdit(int i);
		public void onCardDelete(int i);
	}

    public CountdownCard(Countdown countdown, DateTimeFormatter format, boolean hasOverflow, boolean isClickable) {
        super(countdown.title, countdown.description, colorToString(countdown.color), colorToString(countdown.color), hasOverflow, isClickable);
        this.countdown = countdown;
        this.format = format;
    }

    @Override
    public View getCardContent(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_countdown, null);

        ((ImageView) v.findViewById(R.id.card_stripe)).setBackgroundColor(Color.parseColor(colorToString(countdown.color)));

	    TextView textTitle = (TextView) v.findViewById(R.id.card_title);
        textTitle.setText(countdown.title);
        textTitle.setTextColor(Color.parseColor(colorToString(countdown.color)));

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

	    TextView textDescription = (TextView) v.findViewById(R.id.card_description);
        if (countdown.description == null || countdown.description.isEmpty()) {
	        textDescription.setText("");
	        textDescription.setVisibility(View.GONE);
        } else {
	        textDescription.setText(countdown.description);
	        textDescription.setVisibility(View.VISIBLE);
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

	public int getArrayIndex() {
		return arrayIndex;
	}

	public void setArrayIndex(int i) {
		this.arrayIndex = i;
	}

	public static String colorToString(joewu.dmm.Color c) {
		String colorString = null;
		switch (c) {
			case RED:
				colorString = "#ff4444";
				break;
			case YELLOW:
				colorString = "#ffbb33";
				break;
			case GREEN:
				colorString = "#99cc00";
				break;
			case BLUE:
				colorString = "#33b5e5";
				break;
			case PURPLE:
				colorString = "#aa66cc";
				break;
			default:
				break;
		}
		return colorString;
	}
}
