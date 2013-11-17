package joewu.dmm.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import joewu.dmm.R;
import joewu.dmm.objects.DaysCountdown;

/**
 * Created by joewu on 9/30/13.
 */
public class DaysItemAdapter extends BaseAdapter<DaysCountdown> {
    private DateTimeFormatter format;

    public DaysItemAdapter(Context context, DateTimeFormatter format, List<DaysCountdown> objects) {
        super(context, R.layout.cell_countdown, objects);
        this.format = format;
    }

    private static class ViewHolder {
        View stripe;
        TextView title;
        TextView value;
        TextView daysLeft;
        TextView date;
        TextView description;
        ImageButton overflow;
    }

    public void setFormat(DateTimeFormatter format) {
        if (!this.format.equals(format)) {
            this.format = format;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_countdown, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.stripe = convertView.findViewById(R.id.iv_card_stripe);
            holder.title = (TextView) convertView.findViewById(R.id.tv_card_title);
            holder.value = (TextView) convertView.findViewById(R.id.tv_card_countdown);
            holder.daysLeft = (TextView) convertView.findViewById(R.id.tv_card_days_left);
            holder.date = (TextView) convertView.findViewById(R.id.tv_card_date);
            holder.description = (TextView) convertView.findViewById(R.id.tv_card_description);
            holder.overflow = (ImageButton) convertView.findViewById(R.id.ib_card_overflow);
            convertView.setTag(holder);
        }

        DaysCountdown countdown = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.stripe.setBackgroundColor(context.getResources().getColor(countdown.color));
        holder.title.setText(countdown.title);
        holder.title.setTextColor(context.getResources().getColor(countdown.color));

        DateTime today = DateTime.now();
        int daysDiff = countdown.getDaysDiff(today);
        if (daysDiff >= 0) {
            holder.value.setText(String.valueOf(daysDiff));
            holder.daysLeft.setText(context.getString(R.string.card_days_left));
        } else {
            holder.value.setText(String.valueOf(-daysDiff));
            holder.daysLeft.setText(context.getString(R.string.card_days_past));
        }
        holder.date.setText(format.print(countdown.date));

        if (countdown.description == null || countdown.description.isEmpty()) {
            holder.description.setText("");
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setText(countdown.description);
            holder.description.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
