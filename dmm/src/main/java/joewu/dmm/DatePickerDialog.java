package joewu.dmm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTime;

/**
 * Created by joew on May 151.
 */
public class DatePickerDialog extends DialogFragment {

	private DateTime date;
	private DatePicker picker;

	public DatePickerDialog(DateTime date) {
		this.date = date;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialogView = inflater.inflate(R.layout.dialog_datepicker, null);

		picker = (DatePicker) dialogView.findViewById(R.id.dialog_datepicker);
		picker.updateDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());

		builder.setView(dialogView)
				.setPositiveButton(R.string.dialog_set, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						// Do something here to tell the MainActivity
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
