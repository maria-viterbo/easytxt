package com.example.easytxt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hari on 24/03/2018.
 */

class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> messages;
    private List<Boolean> switchStates;
    private Boolean isDeleteMode = false;


    public CustomAdapter(Context context, List<Map<String, String>> messages, List<Boolean> switchStates) {
        this.context = context;
        this.messages = messages;
        this.switchStates = switchStates;
    }

    public void addNewMessage(String header, String body){
        if (getCount() == 9) return;

        Map<String, String> map = new HashMap<>();

        map.put("header", header);
        map.put("body", body);

        this.messages.add(map);

        switchStates.add(true);
    }

    public void toggleDeleteMode() {
        isDeleteMode = ! isDeleteMode;
    }

    public void removeMessage(int i) {
        messages.remove(i);
        switchStates.remove(i);
    }

    @Override
    public int getCount() {
        return messages.size();
    }


    @Override
    public Object getItem(int i) {
        return null;
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {

            // Get list view
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View customView = layoutInflater.inflate(R.layout.activity_list_view, parent, false);

            // If delete mode
            if (isDeleteMode) {
                // Make check box visible
                CheckBox checkBox = (CheckBox) customView.findViewById(R.id.checkbox);
                checkBox.setVisibility(View.VISIBLE);
            }

            // Edit header text
            TextView headerText = (TextView) customView.findViewById(R.id.messageHeader);
            headerText.setText(messages.get(i).get("header"));

            // Edit body text
            TextView bodyText = (TextView) customView.findViewById(R.id.messageBody);
            bodyText.setText(messages.get(i).get("body"));

            // Turn on switch
            Switch toggleSwitch = (Switch) customView.findViewById(R.id.messageSwitch);

            // If saved switch state is true, turn on switch
            if (i < switchStates.size() && switchStates.get(i) == true)
                toggleSwitch.toggle();

            return customView;
        }

        return view;
    }
}
