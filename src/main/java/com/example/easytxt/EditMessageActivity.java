package com.example.easytxt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class EditMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);


        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Message input

        final EditText messageText = (EditText) findViewById(R.id.messageInputText);

        // Clear focus when done
        messageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE) {

                    textView.clearFocus();
                    hideKeyboard();
                }

                return false;
            }
        });




        // Time
        TextView startTimeText = (TextView) findViewById(R.id.startTimeText);
        new TextTimePicker(startTimeText, this);

        TextView endTimeText = (TextView) findViewById(R.id.endTimeText);
        new TextTimePicker(endTimeText, this);


        // Initialise day names
        final List<String> dayNames = new ArrayList<>();
        dayNames.add("Mon");
        dayNames.add("Tue");
        dayNames.add("Wed");
        dayNames.add("Thu");
        dayNames.add("Fri");
        dayNames.add("Sat");
        dayNames.add("Sun");


        // Save button
        Button saveButton=(Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();

                // Message
                EditText messageInputText = (EditText)findViewById(R.id.messageInputText);
                String message = String.valueOf(messageInputText.getText());
                intent.putExtra("MESSAGE", message);

                // Body

                String messageDetails = "";
                String days = "";

                // Days
                LinearLayout daysButtons = (LinearLayout) findViewById(R.id.dayButtons);

                final int dayButtonCount = daysButtons.getChildCount();

                for (int i = 0; i < dayButtonCount; i++) {
                    ToggleButton dayButton = (ToggleButton) daysButtons.getChildAt(i);

                    if (dayButton.isChecked()) {

                        // If message body is not empty, add space
                        if (!messageDetails.isEmpty()) {
                            messageDetails += " ";
                        }

                        // Add day name to message body
                        messageDetails += dayNames.get(i);
                        days += i + 1;
                    }
                }

                // Time
                TextView startTimeText = (TextView) findViewById(R.id.startTimeText);
                TextView endTimeText = (TextView) findViewById(R.id.endTimeText);

                String startTime = String.valueOf(startTimeText.getText());
                String endTime = String.valueOf(endTimeText.getText());

                messageDetails += "   "  + startTime + "-" + endTime;

                intent.putExtra("MESSAGE_DETAILS", messageDetails);

                // Extra
                intent.putExtra("DAYS", days);
                intent.putExtra("START_TIME", startTime);
                intent.putExtra("END_TIME", endTime);

                setResult(MainActivity.MESSAGE_CREATED_RESULT_CODE, intent);
                finish();
            }
        });
    }


    @Override
    // If back button is clicked,
    public boolean onSupportNavigateUp(){

        // Let main activity know that message creation was cancelled
        setResult(MainActivity.MESSAGE_CREATE_CANCELLED_RESULT_CODE);


        // End this activity and return to the previous one
        // (i.e. goes back to the 'Automatic messages' screen )
        finish();


        return true;
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
