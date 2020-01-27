package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PendingActivity extends AppCompatActivity {

    private ListView pendingList;
    private Spinner citySpinner, dateSpinner;

    private ArrayAdapter<String> pendingListAdapter;
    private ArrayAdapter<String> dateSpinAdapter;
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> order_reciver_name = new ArrayList<>();
    private ArrayList<String> deliveryid_Array = new ArrayList<>();
    private ArrayList<String> orderKyes = new ArrayList<>();
    private ArrayList<String> ordr_city = new ArrayList<>();
    private ArrayList<String> order_delivery_type = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        pendingList = findViewById(R.id.pendingOrders);
        dateSpinner = findViewById(R.id.dateSpin);


        pendingListAdapter = new ArrayAdapter<>(PendingActivity.this, android.R.layout.simple_list_item_1, order_reciver_name);
        pendingList.setAdapter(pendingListAdapter);
        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(OrderActivity.this, "hereeeeeeeeee", Toast.LENGTH_SHORT).show();
                Intent ditals = new Intent(PendingActivity.this, SavedOrderDetailsActivity.class);
//                ditals.putExtra("keys" ,orderKyes.get(position));
                //ditals.putExtra("order", orders_Array.get(position));
                ditals.putExtra("comefrom", "pending");
//                ditals.putExtra("city", ordr_city.get(position));
//                ditals.putExtra("deliverytype", order_delivery_type.get(position));
//                ditals.putExtra("date",date.get(dateSpinner.getSelectedItemPosition()));
                startActivity(ditals);
            }
        });
        get_orders();


    }
    ValueEventListener datelistener;

    DatabaseReference dateref;

//    public void get_date(){
//        dateref = FirebaseDatabase.getInstance().getReference().child("person").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child("saved");
//        datelistener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                date.clear();
//                int i = 0;
//                for (DataSnapshot d : dataSnapshot.getChildren()){
//                    date.add(d.getKey());
//                    i++;
//                }
//                if(i == dataSnapshot.getChildrenCount()){
//                    dateSpinAdapter = new ArrayAdapter<>(PendingActivity.this, R.layout.spinner_item, date);
//                    dateSpinAdapter.setDropDownViewResource(R.layout.spinner_item);
//                    dateSpinner.setAdapter(dateSpinAdapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        dateref.addValueEventListener(datelistener);
//    }

    DatabaseReference orderref ;
    ValueEventListener orderlistener;
    public void get_orders(){
        String currentDate = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault()).format(new Date());

        orderref = FirebaseDatabase.getInstance().getReference().child("person").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pending").child(currentDate);
        orderlistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryid_Array.clear();
                order_delivery_type.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    for (DataSnapshot data : d.getChildren()){
                        for (DataSnapshot dd : data.getChildren()) {
                            String deliveryId = dd.getValue().toString();
                            ordr_city.add(d.getKey());
                            order_delivery_type.add(data.getKey());
                            orderKyes.add(dd.getKey());
                            //orders_Array.add(order);
                            //order_reciver_name.add(order.getR_name());
                            pendingListAdapter.notifyDataSetChanged();
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
    DatabaseReference delvref;
    ValueEventListener delvListener;
    public void get_order_from_delivery(String delvId){

        delvref = FirebaseDatabase.getInstance().getReference().child("delivery").child(delvId).child("order");
        delvListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    order_data order = dataSnapshot.getValue(order_data.class);
                    Intent t = new Intent(PendingActivity.this,SavedOrderDetailsActivity.class);
                    t.putExtra("order",order);
                    startActivity(t);
                }else
                    Toast.makeText(PendingActivity.this, "erooor", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
