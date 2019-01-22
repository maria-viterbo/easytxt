package com.example.easytxt;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<Map<String, String>> messages;
    List<Boolean> switchStates;
    ListView messageListView;
    CustomAdapter messageListAdapter;
    Toolbar toolbar;
    ActionMode deleteMode;
    public static DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Automatic Messages");


        // Initialise message switches
        switchStates = new ArrayList<>();

        // Database
        db = new DatabaseHelper(this);

        // Initialise message list
        messages = new ArrayList<>();
//        db.insert("Busy", "", "", "", "");

        Cursor cursor = db.getListContents();

        if (cursor.moveToFirst()){
            do {
                String message = cursor.getString(DatabaseHelper.COL_INDEX_MESSAGE);
                addToMessages(message, "");

            } while(cursor.moveToNext());
        }
        cursor.close();

//        message = data.getColumnName(0);

//        for (int i = 0; i < 6; i++)
//            addToMessages(data.getColumnName(i), "");

//        addToMessages("I will get back to you after work",
//                      "Mon Tue Wed Thu Fri   09:00-17:00");
//
//        addToMessages("I am currently in a meeting",
//                      "Mon Wed Fri   10:00-11:00");

        // Set adapter for list view
        messageListView = (ListView) findViewById(R.id.messageListView);

        messageListAdapter = new CustomAdapter(this.getApplicationContext(), messages, switchStates);
        messageListView.setAdapter(messageListAdapter);


        // Floating Add button
        FloatingActionButton floatingAddButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            // If floating add button is clicked,
            public void onClick(View view) {

                // Create an intent to change the current activity from MainActivity
                // (i.e. The 'Automatic Messages' screen) to EditMessageActivity
                Intent createMessageIntent = new Intent(MainActivity.this, EditMessageActivity.class);

                // Start EditMessageActivity
                startActivityForResult(createMessageIntent, EDIT_MESSAGE_REQUEST_CODE);
            }

        });


        // Request permission from the user

        // If the app doesn't already have permission to access sms messages,
        if (!isSmsPermissionGranted()) {

            // Request permission
            requestPermissionToReadAndSendSms();

        }


        // List view item click listener
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            // If the list of automatic messages is clicked,
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

/*                // Create an intent to take us to the 'Edit Message' screen
                Intent editMessageIntent = new Intent(MainActivity.this, EditMessageActivity.class);

                // Store the 'position' of the clicked message in the
                // intent so that it can be passed to the next activity
                editMessageIntent.putExtra("CLICKED_ITEM_POSITION", position);

                // Start EditMessageActivity
                startActivityForResult(editMessageIntent, EDIT_MESSAGE_REQUEST_CODE);*/

            }

        });


        // Sms listener
        SmsBroadcastReceiver smsBroadcastReceiver = new SmsBroadcastReceiver();
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

        smsBroadcastReceiver.setListener(new SmsBroadcastReceiver.Listener() {


            @Override
            // When text is received
            public void onTextReceived(String senderNumber) {

                // If not UK number
                if (! senderNumber.startsWith("+44")) {

                    // Don't continue
                    return;
                }

                int numberOfAutoMessages = messageListAdapter.getCount();

                // For each row in database
                for (int i = 0; i < numberOfAutoMessages; i++) {

                    // Get row from database
                    Cursor row = db.get(i);

                    // If row is not empty
                    if (row.moveToFirst()) {

                        // Get days of week and time range
                        String daysOfWeek = row.getString(DatabaseHelper.COL_INDEX_DAYS);
                        String startTime = row.getString(DatabaseHelper.COL_INDEX_START_TIME);
                        String endTime = row.getString(DatabaseHelper.COL_INDEX_START_TIME);


                        // If current time is within range
                        if (isCurrentTimeBetween(daysOfWeek, startTime, endTime)) {

                            // Get the automatic message
                            String automaticMessage = row.getString(DatabaseHelper.COL_INDEX_MESSAGE);

                            // Reply to sender
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(senderNumber, null, automaticMessage, null, null);
                        }
                    }

                }



            }


        });
    }

    private Boolean isCurrentTimeBetween(Calendar startTime, Calendar endTime, String days) {
        Calendar now = Calendar.getInstance();

        return now.getTimeInMillis() >= startTime.getTimeInMillis() &&
                now.getTimeInMillis() <= endTime.getTimeInMillis();

    }


    public static final int EDIT_MESSAGE_REQUEST_CODE = 1;
    public static final int MESSAGE_CREATED_RESULT_CODE = 1;
    public static final int MESSAGE_CREATE_CANCELLED_RESULT_CODE = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_MESSAGE_REQUEST_CODE)
        {
            if (resultCode == MESSAGE_CREATED_RESULT_CODE) {
//                String messageHeader = data.getStringExtra("MESSAGE");
//                String messageBody = data.getStringExtra("MESSAGE_DETAILS");
//
//                messageListAdapter.addNewMessage(messageHeader, messageBody);
//                messageListView.setAdapter(messageListAdapter);


            }
            else if (resultCode == MESSAGE_CREATE_CANCELLED_RESULT_CODE) {
                // Do nothing
            }

        }
    }


    private void addToMessages(String header, String body) {
        Map<String, String> map = new HashMap<>();

        map.put("header", header);
        map.put("body", body);

        this.messages.add(map);

        switchStates.add(true);
    }

    /**
     * Check if we have SMS permission
     */
    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    public static final int SMS_PERMISSION_CODE = 2;
    private void requestPermissionToReadAndSendSms() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    // If an option in the drop down menu is selected,
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the ID of that option
        int id = item.getItemId();

        // If ID of the chosen option matches the ID of the delete button
        if (id == R.id.delete) {

            // If delete mode is not already active
            if (deleteMode == null) {

                // Start delete mode
                deleteMode = startSupportActionMode(actionModeCallback);

            }

            // Activate delete mode
            messageListAdapter.toggleDeleteMode();

            // Reapply adapter
            messageListView.setAdapter(messageListAdapter);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            mode.setTitle("Select messages to delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.delete:
                    Toast.makeText(getApplicationContext(), "Deleted messages", Toast.LENGTH_SHORT);

                    List<Integer> checkedItemIndexes = new ArrayList<>();

                    for (int i = 0; i < messageListView.getCount(); i++) {
                        View view = messageListView.getChildAt(i);
                        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
                        if(checkbox.isChecked()) {
                            checkedItemIndexes.add(i);
                        }
                    }

                    for (int i = 0; i < checkedItemIndexes.size(); i++) {
                        int checkedItemIndex = checkedItemIndexes.get(i);

                        messageListAdapter.removeMessage(checkedItemIndex - i);
                    }

                    messageListView.setAdapter(messageListAdapter);

                    mode.finish();
                    return true;
            }

            return false;
        }

        @Override
        // If back button is clicked during delete mode or
        // if user is done with deleting items
        public void onDestroyActionMode(ActionMode mode) {

            // Deactivate delete mode
            messageListAdapter.toggleDeleteMode();
            deleteMode = null;

            // Reapply adapter
            messageListView.setAdapter(messageListAdapter);

        }
    };

}
