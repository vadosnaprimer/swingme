package net.yura.android;

import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

public class TestTimePickerActivity extends Activity implements TimePickerDialog.OnTimeSetListener, OnDismissListener {
    private Vector result = new Vector();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dialog dialog = new TimePickerDialog(this, android.R.style.Theme_Dialog, this, 9, 15, true);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        result.add(hourOfDay);
        result.add(minute);
        Toast.makeText(this,
                "Time is=" + hourOfDay + ":" + minute, Toast.LENGTH_SHORT)
                .show();
    }

    public void onDismiss(DialogInterface dialoginterface) {
        Intent i = new Intent();
        i.putExtra("data", result);
        setResult(RESULT_OK, i);

        finish();
    }
}