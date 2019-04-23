package io.github.nikkoes.pesantrenar.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DialogUtils {

    public static ProgressDialog progressDialog;

    public static void showSnack(Activity context, String message, View.OnClickListener listener) {
        Snackbar.make(context.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Close", listener)
                .show();
    }

    public static void openDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading . . . ");
        progressDialog.show();
    }

    public static void closeDialog() {
        progressDialog.dismiss();
    }

}
