package joewu.dmm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * Created by joew on May 150.
 */
public class CountdownDialog extends DialogFragment implements View.OnClickListener, TextWatcher {

	private boolean isNew;

	private Countdown countdown;
	private DateTimeFormatter format;

	private Map<Color, ImageView> selectors = new HashMap<Color, ImageView>();
	private EditText textDate;
	private EditText textTitle;
	private EditText textDescription;

	public interface CountdownDialogListener {
		public void onDialogPositiveClick(Countdown countdown, boolean isNew);
	}

	CountdownDialogListener mListener;

	public CountdownDialog(Countdown countdown, boolean isNew, DateTimeFormatter format) {
		super();
		this.isNew = isNew;
		this.countdown = countdown;
		this.format = format;
	}

    public CountdownDialog() {
        super();
        this.isNew = true;
        this.countdown = new Countdown("", "", Color.RED, 1970, 1, 1);
        this.format = DateTimeFormat.forPattern(getString(R.string.default_date_format));
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialogView = inflater.inflate(R.layout.dialog_countdown, null);

		selectors.put(Color.RED, (ImageView) dialogView.findViewById(R.id.dialog_selector_red));
		selectors.put(Color.YELLOW, (ImageView) dialogView.findViewById(R.id.dialog_selector_yellow));
		selectors.put(Color.GREEN, (ImageView) dialogView.findViewById(R.id.dialog_selector_green));
		selectors.put(Color.BLUE, (ImageView) dialogView.findViewById(R.id.dialog_selector_blue));
		selectors.put(Color.PURPLE, (ImageView) dialogView.findViewById(R.id.dialog_selector_purple));

		textDate = (EditText) dialogView.findViewById(R.id.dialog_date_text);
		textTitle = (EditText) dialogView.findViewById(R.id.dialog_title_text);
		textDescription = (EditText) dialogView.findViewById(R.id.dialog_description_text);

		selectors.get(Color.RED).setOnClickListener(this);
		selectors.get(Color.YELLOW).setOnClickListener(this);
		selectors.get(Color.GREEN).setOnClickListener(this);
		selectors.get(Color.BLUE).setOnClickListener(this);
		selectors.get(Color.PURPLE).setOnClickListener(this);

		textDate.setOnClickListener(this);
		textDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					showDatePicker();
				}
			}
		});

		for (Color c : Color.values()) {
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
				.setPositiveButton((isNew ? R.string.dialog_create : R.string.dialog_done), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						countdown.title = textTitle.getText().toString();
						countdown.description = textDescription.getText().toString();
						mListener.onDialogPositiveClick(countdown, isNew);
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
		if (countdown.title == null || countdown.title.isEmpty()) {
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
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.dialog_selector_red:
				if (countdown.color != Color.RED) {
					setColorUnchecked(countdown.color);
					countdown.color = Color.RED;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_yellow:
				if (countdown.color != Color.YELLOW) {
					setColorUnchecked(countdown.color);
					countdown.color = Color.YELLOW;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_green:
				if (countdown.color != Color.GREEN) {
					setColorUnchecked(countdown.color);
					countdown.color = Color.GREEN;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_blue:
				if (countdown.color != Color.BLUE) {
					setColorUnchecked(countdown.color);
					countdown.color = Color.BLUE;
					setColorChecked(countdown.color);
				}
				break;
			case R.id.dialog_selector_purple:
				if (countdown.color != Color.PURPLE) {
					setColorUnchecked(countdown.color);
					countdown.color = Color.PURPLE;
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
		if (s.toString().isEmpty()) {
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
		DatePickerDialog fragment = new DatePickerDialog();
		fragment.show(getFragmentManager(), "datePickerDialog");
	}

	private void setColorUnchecked(Color c) {
		switch (c) {
			case RED:
				selectors.get(Color.RED).setImageResource(R.drawable.red_unchecked);
				break;
			case YELLOW:
				selectors.get(Color.YELLOW).setImageResource(R.drawable.yellow_unchecked);
				break;
			case GREEN:
				selectors.get(Color.GREEN).setImageResource(R.drawable.green_unchecked);
				break;
			case BLUE:
				selectors.get(Color.BLUE).setImageResource(R.drawable.blue_unchecked);
				break;
			case PURPLE:
				selectors.get(Color.PURPLE).setImageResource(R.drawable.purple_unchecked);
				break;
			default:
				break;
		}
	}

	private void setColorChecked(Color c) {
		switch (c) {
			case RED:
				selectors.get(Color.RED).setImageResource(R.drawable.red_checked);
				break;
			case YELLOW:
				selectors.get(Color.YELLOW).setImageResource(R.drawable.yellow_checked);
				break;
			case GREEN:
				selectors.get(Color.GREEN).setImageResource(R.drawable.green_checked);
				break;
			case BLUE:
				selectors.get(Color.BLUE).setImageResource(R.drawable.blue_checked);
				break;
			case PURPLE:
				selectors.get(Color.PURPLE).setImageResource(R.drawable.purple_checked);
				break;
			default:
				break;
		}
	}

	private class DatePickerDialog extends DialogFragment {
		private DatePicker picker;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View dialogView = inflater.inflate(R.layout.dialog_datepicker, null);

			picker = (DatePicker) dialogView.findViewById(R.id.dialog_datepicker);
			picker.updateDate(countdown.date.getYear(), countdown.date.getMonthOfYear() - 1, countdown.date.getDayOfMonth());

			builder.setView(dialogView)
					.setPositiveButton(R.string.dialog_set, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							countdown.date = new DateTime(picker.getYear(), picker.getMonth() + 1, picker.getDayOfMonth(), 0, 0);
							textDate.setText(format.print(countdown.date));
						}
					})
					.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							DatePickerDialog.this.getDialog().cancel();
						}
					});

			return builder.create();
		}
	}
}
