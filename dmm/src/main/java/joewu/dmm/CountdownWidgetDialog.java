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
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by joewu on 11/06/13.
 */
public class CountdownWidgetDialog extends DialogFragment {

    private Spinner countdownSpinner;
    private EditText textAlias;

    public interface CountdownWidgetDialogListener {
        public void onDialogPositiveClick();
        public void onDialogNegativeClick();
    }

    CountdownWidgetDialogListener mListener;

    public CountdownWidgetDialog() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppDialogTheme));
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_widget_countdown, null);

        countdownSpinner = (Spinner) dialogView.findViewById(R.id.dialog_widget_spinner);
        textAlias = (EditText) dialogView.findViewById(R.id.dialog_widget_alias);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick();
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
}
