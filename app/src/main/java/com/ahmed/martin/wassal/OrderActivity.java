package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {

    private ListView savedList;
    private Spinner citySpinner, dateSpinner;

    private ArrayAdapter<String> savedListAdapter;
    private ArrayAdapter<String> dateSpinAdapter;
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> order_reciver_name = new ArrayList<>();
    private ArrayList<order_data> orders_Array = new ArrayList<>();
    private ArrayList<String> orderKyes = new ArrayList<>();
    private ArrayList<String> ordr_city = new ArrayList<>();
    private ArrayList<String> order_delivery_type = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        savedList = findViewById(R.id.savedOrder);
        dateSpinner = findViewById(R.id.dateSpin);

        savedListAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_list_item_1, order_reciver_name);
        savedList.setAdapter(savedListAdapter);
        savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(OrderActivity.this, "hereeeeeeeeee", Toast.LENGTH_SHORT).show();
                Intent ditals = new Intent(OrderActivity.this, SavedOrderDetailsActivity.class);
                ditals.putExtra("keys" ,orderKyes.get(position));
                ditals.putExtra("order", orders_Array.get(position));
                ditals.putExtra("comefrom", "saved");
                ditals.putExtra("city", ordr_city.get(position));
                ditals.putExtra("deliverytype", order_delivery_type.get(position));
                ditals.putExtra("date",date.get(dateSpinner.getSelectedItemPosition()));
                startActivity(ditals);
            }
        });
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(date!= null)
                get_orders(date.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        get_date();

    }
    ValueEventListener datelistener;

    DatabaseReference dateref;

    public void get_date(){
        dateref = FirebaseDatabase.getInstance().getReference().child("person").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("saved");
        datelistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                date.clear();
                int i = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    date.add(d.getKey());
                    i++;
                }
                if(i == dataSnapshot.getChildrenCount()){
                    dateSpinAdapter = new ArrayAdapter<>(OrderActivity.this, R.layout.spinner_item, date);
                    dateSpinAdapter.setDropDownViewResource(R.layout.spinner_item);
                    dateSpinner.setAdapter(dateSpinAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dateref.addValueEventListener(datelistener);
    }

    DatabaseReference orderref ;
    ValueEventListener orderlistener;
    public void get_orders(String date_string){
        orderref = FirebaseDatabase.getInstance().getReference().child("person").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("saved").child(date_string);
        orderlistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order_reciver_name.clear();
                orders_Array.clear();
                orderKyes.clear();
                ordr_city.clear();
                order_delivery_type.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    for (DataSnapshot data : d.getChildren()){
                        for (DataSnapshot dd : data.getChildren()) {
                            order_data order = dd.getValue(order_data.class);
                            ordr_city.add(d.getKey());
                            order_delivery_type.add(data.getKey());
                            orderKyes.add(dd.getKey());
                            orders_Array.add(order);
                            order_reciver_name.add(order.getR_name());
                            savedListAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        orderref.addValueEventListener(orderlistener);
    }
}
