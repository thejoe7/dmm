package joewu.dmm;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.fima.cardsui.objects.Card;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.values.HoloColor;


/**
 * Created by joe7wu on 2013-05-25.
 */
public class CountdownCard extends Card {

	private Context context;
    private DateTimeFormatter format;
    private DaysCountdown countdown;
	private int arrayIndex = -1;
	private ImageView overflow;

	public interface CardActionListener {
		public void onCardEdit(int i);
		public void onCardDelete(int i);
	}

    public CountdownCard(Context context, DaysCountdown countdown, DateTimeFormatter format, boolean hasOverflow, boolean isClickable) {
        super(countdown.title, countdown.description, colorToString(countdown.color), colorToString(countdown.color), hasOverflow, isClickable);
	    this.context = context;
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
        TextView countdownText = (TextView) v.findViewById(R.id.card_countdown);
        TextView daysLeftText = (TextView) v.findViewById(R.id.card_days_left);
        TextView dateText = (TextView) v.findViewById(R.id.card_date);
        if (daysDiff >= 0) {
            countdownText.setText(Integer.toString(daysDiff));
//            countdownText.setTextColor(context.getResources().getColor(android.R.color.black));
            daysLeftText.setText(context.getString(R.string.card_days_left));
//            daysLeftText.setTextColor(context.getResources().getColor(android.R.color.black));
//            dateText.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            countdownText.setText(Integer.toString(-daysDiff));
//            countdownText.setTextColor(context.getResources().getColor(R.color.dark_gray));
            daysLeftText.setText(context.getString(R.string.card_days_past));
//            daysLeftText.setTextColor(context.getResources().getColor(R.color.dark_gray));
//            dateText.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }
        dateText.setText(format.print(countdown.date));

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
	    overflow = (ImageView) v.findViewById(R.id.card_overflow);
        if (hasOverflow) {
            overflow.setVisibility(View.VISIBLE);
        } else {
            overflow.setVisibility(View.GONE);
        }

        return v;
    }

	public int getArrayIndex() {
		return arrayIndex;
	}

	private void onOptionDelete() {
		((MainActivity) context).deleteCountdown(this.arrayIndex);
	}

	private void onOptionEdit() {
		((MainActivity) context).editCountdown(this.arrayIndex);
	}

	public void setArrayIndex(int i) {
		this.arrayIndex = i;
		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PopupMenu popup = new PopupMenu(context, CountdownCard.this.overflow);
				popup.getMenuInflater().inflate(R.menu.menu_card, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						switch (menuItem.getItemId()) {
							case R.id.action_edit:
								onOptionEdit();
								return true;
							case R.id.action_delete:
								onOptionDelete();
								return true;
							default:
								return false;
						}
					}
				});
				popup.show();
			}
		});
	}

	public static String colorToString(int c) {
		String colorString = null;
		switch (c) {
			case HoloColor.RedLight:
				colorString = "#ff4444";
				break;
			case HoloColor.YellowLight:
				colorString = "#ffbb33";
				break;
			case HoloColor.GreenLight:
				colorString = "#99cc00";
				break;
			case HoloColor.BlueLight:
				colorString = "#33b5e5";
				break;
			case HoloColor.PurpleLight:
				colorString = "#aa66cc";
				break;
			default:
				break;
		}
		return colorString;
	}
}
