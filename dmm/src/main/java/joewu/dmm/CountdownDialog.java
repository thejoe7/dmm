package joewu.dmm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

import joewu.dmm.objects.DaysCountdown;
import joewu.dmm.values.HoloColor;
import joewu.dmm.values.RepeatMode;
import mirko.android.datetimepicker.date.DatePickerDialog;

/**
 * Created by joew on May 150.
 */
public class CountdownDialog extends DialogFragment implements View.OnClickListener, TextWatcher {

	private int index;

	private DaysCountdown countdown;
	private DateTimeFormatter format;

	private Map<Integer, ImageView> selectors = new HashMap<Integer, ImageView>();
	private EditText textDate;
	private EditText textTitle;
	private EditText textDescription;

	public interface CountdownDialogListener {
		public void onDialogPositiveClick(DaysCountdown countdown, int index);
	}

	CountdownDialogListener mListener;

	public CountdownDialog(DaysCountdown countdown, int index, DateTimeFormatter format) {
		super();
		this.index = index;
		this.countdown = countdown;
		this.format = format;
	}

    // should not be used
    public CountdownDialog() {
        super();
        this.index = -1;
        this.countdown = new DaysCountdown("", "", HoloColor.RedLight, 1970, 1, 1, RepeatMode.None);
        this.format = DateTimeFormat.forPattern(MainActivity.sharedMainActivity.getResources().getString(R.string.default_date_format));
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialogView = inflater.inflate(R.layout.dialog_countdown, null);

		selectors.put(HoloColor.RedLight, (ImageView) dialogView.findViewById(R.id.dialog_selector_red));
		selectors.put(HoloColor.YellowLight, (ImageView) dialogView.findViewById(R.id.dialog_selector_yellow));
		selectors.put(HoloColor.GreenLight, (ImageView) dialogView.findViewById(R.id.dialog_selector_green));
		selectors.put(HoloColor.BlueLight, (ImageView) dialogView.findViewById(R.id.dialog_selector_blue));
		selectors.put(HoloColor.PurpleLight, (ImageView) dialogView.findViewById(R.id.dialog_selector_purple));

		textDate = (EditText) dialogView.findViewById(R.id.dialog_date_text);
		textTitle = (EditText) dialogView.findViewById(R.id.dialog_title_text);
		textDescription = (EditText) dialogView.findViewById(R.id.dialog_description_text);

		selectors.get(HoloColor.RedLight).setOnClickListener(this);
		selectors.get(HoloColor.YellowLight).setOnClickListener(this);
		selectors.get(HoloColor.GreenLight).setOnClickListener(this);
		selectors.get(HoloColor.BlueLight).setOnClickListener(this);
		selectors.get(HoloColor.PurpleLight).setOnClickListener(this);

		textDate.setOnClickListener(this);
		textDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					showDatePicker();
				}
			}
		});

		for (Integer c : HoloColor.colors) {
			if (countdown.color == c) {
				setColorChecked(c);
			} else {
				setColorUnchecked(c);
			}
		}

		textTitle.setText(countdown.title);
		textDate.setText(format.print(countdown.date));
		textDescription.setText(countdown.description);

		builder.setView(dialogView)
				.setPositiveButton(((index == MainActivity.INVALID_COUNTDOWN_INDEX) ? R.string.dialog_create : R.string.dialog_done), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						countdown.title = textTitle.getText().toString();
						countdown.description = textDescription.getText().toString();
						mListener.onDialogPositiveClick(countdown, index);
					}
				})
				.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						CountdownDialog.this.getDialog().cancel();
					}
				});

		AlertDialog dialog = builder.create();
		textTitle.addTextChangedListener(this);
		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (countdown.title == null || countdown.title.trim().isEmpty()) {
			enableCreateButton(false);
		} else {
			enableCreateButton(true);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (CountdownDialogListener) activity;
		} catch (ClassCastException cce) {
			throw new ClassCastException(activity.toString() + " must implement CountdownDialogListener.");
		}
	}

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.dialog_selector_red:
				if (countdown.color != HoloColor.RedLight) {
					setColorUnchecked(countdown.color);
					countdown.color = HoloColor.RedLight;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_yellow:
				if (countdown.color != HoloColor.YellowLight) {
					setColorUnchecked(countdown.color);
					countdown.color = HoloColor.YellowLight;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_green:
				if (countdown.color != HoloColor.GreenLight) {
					setColorUnchecked(countdown.color);
					countdown.color = HoloColor.GreenLight;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_blue:
				if (countdown.color != HoloColor.BlueLight) {
					setColorUnchecked(countdown.color);
					countdown.color = HoloColor.BlueLight;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_purple:
				if (countdown.color != HoloColor.PurpleLight) {
					setColorUnchecked(countdown.color);
					countdown.color = HoloColor.PurpleLight;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_date_text:
				showDatePicker();
				break;
			default:
				break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.toString().trim().isEmpty()) {
			enableCreateButton(false);
		} else {
			enableCreateButton(true);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public void enableCreateButton(boolean enabled) {
		AlertDialog dialog = (AlertDialog) getDialog();
		Button btnCreate = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		btnCreate.setEnabled(enabled);
	}

	private void showDatePicker() {
		DatePickerDialog fragment = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                countdown.date = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                textDate.setText(format.print(countdown.date));
            }
        }, countdown.date.getYear(), countdown.date.getMonthOfYear() - 1, countdown.date.getDayOfMonth());
		fragment.show(getFragmentManager(), "datePickerDialog");
	}

	private void setColorUnchecked(Integer c) {
		switch (c) {
			case HoloColor.RedLight:
				selectors.get(HoloColor.RedLight).setImageResource(R.drawable.red_unselected);
				break;
			case HoloColor.YellowLight:
				selectors.get(HoloColor.YellowLight).setImageResource(R.drawable.yellow_unselected);
				break;
			case HoloColor.GreenLight:
				selectors.get(HoloColor.GreenLight).setImageResource(R.drawable.green_unselected);
				break;
			case HoloColor.BlueLight:
                selectors.get(HoloColor.BlueLight).setImageResource(R.drawable.blue_unselected);
				break;
			case HoloColor.PurpleLight:
				selectors.get(HoloColor.PurpleLight).setImageResource(R.drawable.purple_unselected);
				break;
			default:
				break;
		}
	}

	private void setColorChecked(Integer c) {
		switch (c) {
			case HoloColor.RedLight:
				selectors.get(HoloColor.RedLight).setImageResource(R.drawable.red_selected);
				break;
			case HoloColor.YellowLight:
				selectors.get(HoloColor.YellowLight).setImageResource(R.drawable.yellow_selected);
				break;
			case HoloColor.GreenLight:
				selectors.get(HoloColor.GreenLight).setImageResource(R.drawable.green_selected);
				break;
			case HoloColor.BlueLight:
                selectors.get(HoloColor.BlueLight).setImageResource(R.drawable.blue_selected);
				break;
			case HoloColor.PurpleLight:
				selectors.get(HoloColor.PurpleLight).setImageResource(R.drawable.purple_selected);
				break;
			default:
				break;
		}
	}

}
