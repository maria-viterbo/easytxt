package com.example.easytxt;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Hari on 26/03/2018.
 */

class TextTimePicker implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

    private TextView timeTextView;
    private Context context;


    public TextTimePicker(TextView textView, final Context context){
        this.timeTextView = textView;

        // Listen for clicks on time text view
        this.timeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            // If the time text view is clicked,
            public void onClick(View view) {

                // Get current time from the device calendar
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int minute = Calendar.getInstance().get(Calendar.MINUTE);

                // Open new time picker dialog box with the current time
                new TimePickerDialog(context, TextTimePicker.this, hour, minute, true).show();
            }

        });

        this.context = context;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){

            int hour = Calendar.getInstance().get(Calendar.HOUR);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);

            new TimePickerDialog(context, this, hour, minute, true).show();
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {

        // If hour is single digit, add 0 before it
        String hourString = String.valueOf(hour);

        if (hourString.length() == 1) {
            hourString = "0" + hourString;
        }


        // If minute is single digit, add 0 before it

        String minuteString = String.valueOf(minute);
        if (minuteString.length() == 1) {
            minuteString = "0" + minuteString;
        }

        this.timeTextView.setText( hourString + ":" + minuteString);
    }

}