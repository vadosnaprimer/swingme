package net.yura.android.datepicker;

import java.util.Calendar;
import java.util.List;
import net.yura.mobile.logging.Logger;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.DatePicker;

public class CalendarPickerActivity extends Activity {

	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    try {
    		super.onCreate(savedInstanceState);
    		Intent intent = getIntent();
    		Uri uri = intent.getData();
    		List<String> params = uri.getPathSegments();
    		// Date is in Badoo format yyyy-mm-dd
    		String date = params.get(0);
    		String fields[] = date.split("-");
    		mYear = 	Integer.parseInt(fields[0]);
    		mMonth = 	Integer.parseInt(fields[1]) - 1;
    		mDay = 		Integer.parseInt(fields[2]);
    		showDialog(0);
    	} catch (Throwable ex) {
    	    //#debug warn
            Logger.warn(null, ex);

            finish();
        }
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg = new DatePickerDialog(this, mDateSetListener, mYear,
				mMonth, mDay);
		dlg.setOnDismissListener(mDismissListener);
		return dlg;
	}

	private Dialog.OnDismissListener mDismissListener = new Dialog.OnDismissListener() {
		public void onDismiss(DialogInterface dialog) {
	    	Intent returnIntent = new Intent();

	    	    Calendar cal = Calendar.getInstance();
	    	    cal.set(mYear, mMonth, mDay);

		    returnIntent.putExtra("data", cal);
		    setResult(RESULT_OK, returnIntent);
		    finish();
		}
	};

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
		}
	};

}
