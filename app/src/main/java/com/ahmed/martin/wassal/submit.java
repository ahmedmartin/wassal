package com.ahmed.martin.wassal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class submit extends AppCompatActivity {

    private order_data order_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        // get order class from another activity
        order_details=(order_data) getIntent().getSerializableExtra("order");

    }

    public void same_order(View view) {
        Intent main = new Intent(this,MainActivity.class);
        main.putExtra("order",order_details);
        main.putExtra("get_receiver_data",false);
        finish();
        startActivity(main);
    }

    public void new_order(View view) {
        Intent main = new Intent(this,MainActivity.class);
        order_details = new order_data();
        main.putExtra("order",order_details);
        finish();
        startActivity(main);
    }
}
