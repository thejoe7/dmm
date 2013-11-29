package joewu.dmm.adapters;

import android.app.FragmentManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import joewu.dmm.R;
import joewu.dmm.fragments.ContentDialogFragment;
import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.utility.PreferencesUtils;
import joewu.dmm.widgets.SingleWidget;

/**
 * Created by joewu on 9/30/13.
 */
public class DaysItemAdapter extends BaseAdapter<DaysCountdown> {
    private DateTimeFormatter format;
    private FragmentManager fragmentManager;

    public DaysItemAdapter(Context context, DateTimeFormatter format, FragmentManager fragmentManager, List<DaysCountdown> objects) {
        super(context, R.layout.cell_countdown, objects);
        this.format = format;
        this.fragmentManager = fragmentManager;
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

        final DaysCountdown countdown = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

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
        holder.date.setText(format.print(countdown.getNextDate()));

        if (countdown.description == null || countdown.description.isEmpty()) {
            holder.description.setText("");
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setText(countdown.description);
            holder.description.setVisibility(View.VISIBLE);
        }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.overflow);
                popup.getMenuInflater().inflate(R.menu.menu_card, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_edit:
                                onOptionEdit(countdown);
                                return true;
                            case R.id.action_delete:
                                onOptionDelete(countdown);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        return convertView;
    }

    private void onOptionEdit(DaysCountdown countdown) {
        ContentDialogFragment dialogFragment = new ContentDialogFragment(countdown, false, new ContentDialogFragment.CountdownDialogListener() {
            @Override
            public void onDialogPositiveClick(DaysCountdown countdown) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                DaysItemAdapter.this.add(countdown);
                PreferencesUtils.saveDaysCountdown(sharedPref, countdown);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                for (int appWidgetId : PreferencesUtils.getWidgetsForDaysCountdown(sharedPref, countdown.getUuid())) {
                    SingleWidget.updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }
        });
        dialogFragment.show(fragmentManager, "countdownDialog");
    }

    private void onOptionDelete(DaysCountdown countdown) {
        remove(countdown);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        PreferencesUtils.removeAllWidgetsForDaysCountdown(sharedPref, countdown.getUuid());
        PreferencesUtils.removeDaysCountdownById(sharedPref, countdown.getUuid());
    }

    @Override
    protected void sortObjects() {
        Collections.sort(this.objects, new Comparator<DaysCountdown>() {
            @Override
            public int compare(DaysCountdown c1, DaysCountdown c2) {
                return c1.getNextDate().compareTo(c2.getNextDate());
            }
        });
    }

    @Override
    public void add(DaysCountdown object) {
        this.objects.remove(object);
        super.add(object);
    }
}
