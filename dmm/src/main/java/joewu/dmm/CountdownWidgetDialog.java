package joewu.dmm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidgetDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private Spinner countdownSpinner;
    private EditText textAlias;
    private ImageView stripe;

    private List<CountdownItem> countdowns;
    private int selectedIndex;
    private DateTimeFormatter format;

    public interface CountdownWidgetDialogListener {
        public void onDialogPositiveClick(int index, String alias);
        public void onDialogNegativeClick();
    }

    CountdownWidgetDialogListener mListener;

    public CountdownWidgetDialog(List<CountdownItem> countdowns, DateTimeFormatter format) {
        super();
        this.countdowns = countdowns;
        this.format = format;
        this.selectedIndex = -1;
    }

    // default constructor, shouldn't be used
    public CountdownWidgetDialog() {
        super();
        this.countdowns = null;
        this.format = null;
        this.selectedIndex = -1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_widget_countdown, null);

        countdownSpinner = (Spinner) dialogView.findViewById(R.id.dialog_widget_spinner);
        countdownSpinner.setAdapter(getSpinnerDataAdapter());
        countdownSpinner.setOnItemSelectedListener(this);

        textAlias = (EditText) dialogView.findViewById(R.id.dialog_widget_alias);

        stripe = (ImageView) dialogView.findViewById(R.id.dialog_widget_stripe);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(CountdownWidgetDialog.this.selectedIndex, CountdownWidgetDialog.this.getCountdownAlias());
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick();
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CountdownWidgetDialogListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString() + " must implement CountdownWidgetDialogListener.");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        this.selectedIndex = (int) id;
        int colorRes;
        switch (countdowns.get(this.selectedIndex).color) {
            case RED:
                colorRes = R.color.ics_red;
                break;
            case YELLOW:
                colorRes = R.color.ics_yellow;
                break;
            case GREEN:
                colorRes = R.color.ics_green;
                break;
            case BLUE:
                colorRes = R.color.ics_blue;
                break;
            case PURPLE:
                colorRes = R.color.ics_purple;
                break;
            default:
                colorRes = R.color.grey;
                break;
        }
        stripe.setBackgroundColor(getResources().getColor(colorRes));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        this.selectedIndex = -1;
        stripe.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    private ArrayAdapter<String> getSpinnerDataAdapter() {
        List<String> titleList = new ArrayList<String>();
        for (CountdownItem c : countdowns) {
            titleList.add(c.title + " (" + format.print(c.date) + ")");
        }
        return new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, titleList);
    }

    private String getCountdownAlias() {
        if (textAlias.toString().isEmpty() && selectedIndex >= 0 && selectedIndex < countdowns.size()) {
            return countdowns.get(selectedIndex).title;
        } else {
            return textAlias.toString();
        }
    }
}
