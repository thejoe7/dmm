package joewu.dmm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joew on May 150.
 */
public class CountdownDialog extends DialogFragment implements View.OnClickListener {

	private Color color;
	private String title;
	private DateTime date;
	private String description;
	private DateTimeFormatter format;

	private Map<Color, ImageView> selectors = new HashMap<Color, ImageView>();
	private EditText textDate;
	private EditText textTitle;
	private EditText textDescription;

	private Context context;

	public CountdownDialog(Context context, Color color, String title, DateTime date, String description, DateTimeFormatter format) {
		super();
		this.context = context;
		this.color = color;
		this.title = title;
		this.date = date;
		this.description = description;
		this.format = format;
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
			if (color == c) {
				setColorChecked(c);
			} else {
				setColorUnchecked(c);
			}
		}

		textTitle.setText(title);
		textDate.setText(format.print(date));
		textDescription.setText(description);

		builder.setView(dialogView)
				.setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						title = textTitle.getText().toString();
						description = textDescription.getText().toString();
						// Do something here to tell the MainActivity
					}
				})
				.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						CountdownDialog.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.dialog_selector_red:
				if (color != Color.RED) {
					setColorUnchecked(color);
					color = Color.RED;
					setColorChecked(color);
				}
				break;
			case R.id.dialog_selector_yellow:
				if (color != Color.YELLOW) {
					setColorUnchecked(color);
					color = Color.YELLOW;
					setColorChecked(color);
				}
				break;
			case R.id.dialog_selector_green:
				if (color != Color.GREEN) {
					setColorUnchecked(color);
					color = Color.GREEN;
					setColorChecked(color);
				}
				break;
			case R.id.dialog_selector_blue:
				if (color != Color.BLUE) {
					setColorUnchecked(color);
					color = Color.BLUE;
					setColorChecked(color);
				}
				break;
			case R.id.dialog_selector_purple:
				if (color != Color.PURPLE) {
					setColorUnchecked(color);
					color = Color.PURPLE;
					setColorChecked(color);
				}
				break;
			case R.id.dialog_date_text:
				showDatePicker();
				break;
			default:
				break;
		}
	}

	private void showDatePicker() {
		DatePickerDialog fragment = new DatePickerDialog(date);
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
}
