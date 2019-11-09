package com.ahmed.martin.wassal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

   private TextView s_address,s_phone,r_name,r_phone,r_address,description,weight,provide_pay,delivery_estimate,date;
   private Spinner delivery_type_spinner;
   private ImageView order_photo;


   private String delivery_type_string;
   private ArrayAdapter<String> delivery_type_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s_address = findViewById(R.id.s_adderess);
        s_phone = findViewById(R.id.s_phone);
        r_name = findViewById(R.id.r_name);
        r_phone = findViewById(R.id.r_phone);
        r_address = findViewById(R.id.r_address);
        description = findViewById(R.id.description);
        weight = findViewById(R.id.weight);
        provide_pay = findViewById(R.id.provide_pay);
        delivery_estimate = findViewById(R.id.delivery_estimate);
        date = findViewById(R.id.date);
        delivery_type_spinner = findViewById(R.id.delivery_type);
        order_photo = findViewById(R.id.order_photo);


        // delivey type spinner
        final String [] types = {"Motor cycle","Bicycle","Car","subway","train"};
        delivery_type_adapter = new ArrayAdapter<>(this,R.layout.spinner_item,types);
        delivery_type_adapter.setDropDownViewResource(R.layout.spinner_item);
        delivery_type_spinner.setAdapter(delivery_type_adapter);
        delivery_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                delivery_type_string = types[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });



    }

    public void take_photo_for_order(View view) {
        // request permission for get photo
        String [] permission ={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permission[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[1])== PackageManager.PERMISSION_GRANTED){

            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }else
            ActivityCompat.requestPermissions(MainActivity.this,permission,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // request get photos from galary
        if(requestCode==2&&resultCode==RESULT_OK) {
           Uri ur = data.getData();
            Picasso.with(MainActivity.this).load(ur).into(order_photo);
        }
        // request get photo permission
        if (requestCode==1&&resultCode== RESULT_OK){
            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }
    }

    public void submit(View view) {
    }

    public void get_sender_address(View view) {
        s_address.setText("h");
    }

    public void get_reciever_address(View view) {
        r_address.setText("g");
    }

    public void get_date(View view) {
        // date picker to make date pop up dialog
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        // check if user choose date after today or equal
                        // if date after or equal today print date and accept it
                        // else send message warning and refuse it
                      Calendar cc = Calendar.getInstance();
                      cc.set(year,month,day);
                      if(c.compareTo(cc)<0) {
                          date.setText(day + "/" + (month +1) + "/" + year);
                          date.setTextSize(22);
                      }else {
                          date.setText("sorry date must be greater than or equal "+ c.get(Calendar.YEAR) + "/" +
                                   c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH));
                          date.setTextSize(18);
                      }
                        Toast.makeText(MainActivity.this, c.compareTo(cc)+"", Toast.LENGTH_SHORT).show();
                    }
                },
               c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
