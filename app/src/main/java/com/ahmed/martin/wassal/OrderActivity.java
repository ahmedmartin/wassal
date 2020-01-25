package com.ahmed.martin.wassal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private TextView saved, pending;
    private ListView savedList, pendingList;

    private ArrayAdapter<String> savedListAdapter;
    private ArrayAdapter<String> pendingListAdapter;
    private ArrayList<String> saved_r_name = new ArrayList<>();
    private ArrayList<String> pending_r_name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        saved = findViewById(R.id.savedorders);
        pending = findViewById(R.id.pending);
        savedList = findViewById(R.id.savedorderlist);
        pendingList = findViewById(R.id.pendinglist);

        savedListAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_list_item_1, saved_r_name);
        pendingListAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_list_item_1, pending_r_name);

    }
}
