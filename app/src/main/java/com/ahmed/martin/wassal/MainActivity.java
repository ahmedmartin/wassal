package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

   private TextView r_name,r_phone,r_address,description,weight,provide_pay,delivery_estimate,date;
   private Spinner delivery_type_spinner;
   private ImageView order_photo;
   private DrawerLayout drawer;

   private order_data order_details ;

   private String delivery_type_string;
   private ArrayAdapter<String> delivery_type_adapter;

   private boolean get_reciever_data;
   private Uri ur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

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


        // get order class from another activity
        order_details=(order_data) getIntent().getSerializableExtra("order");
        // used for submit activity for ask user do you need continue with same order data
        get_reciever_data = getIntent().getBooleanExtra("get_receiver_data",true);

        // get data from class order to show it
        get_data_from_class_order();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.person_info:
                startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                break;
            case R.id.about_us:
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, sign_in.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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
           ur = data.getData();
            Picasso.with(MainActivity.this).load(ur).into(order_photo);
        }
        // request get photo permission
        if (requestCode==1&&resultCode== RESULT_OK){
            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }
    }


    private boolean first_click = true;
    private DatabaseReference  d_ref;
    private AlertDialog.Builder alart;
    private AlertDialog dialog;
    public void submit(View view) {
        // used to make progress dialog "wait"
         alart = new AlertDialog.Builder(this);
        ProgressBar bar = new android.widget.ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyle);
        // change color of progress bar
        bar.getIndeterminateDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.MULTIPLY);
         alart.setView(bar);
         alart.setMessage("Wait");
         alart.setCancelable(false);
         dialog = alart.create();
         dialog.show();

        // check network and all data is entered then add data in database then fire activity submit
        if(connected_networt()) {

            if (check_for_empty_input()) {
                put_data_in_class_order();
                get_user_city();
            }
        }else
            Toast.makeText(this, "don't have network connection", Toast.LENGTH_SHORT).show();
    }




    // check if have network connection
    boolean connected_networt(){
        ConnectivityManager mang = (ConnectivityManager) this.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(mang!=null){
            NetworkInfo inf = mang.getActiveNetworkInfo();
            if(inf!=null){
                if(inf.isConnected())
                    return true;
            }
        }
        return false;
    }


    // mt5odsh el data bta3t el city ya 3mr 5las ana 7let el m4kla
    // get user city by it's address lat , long
    private String city;
    private String get_user_city() {

        String uid = FirebaseAuth.getInstance().getUid().toString();

        DatabaseReference d_ref = FirebaseDatabase.getInstance().getReference().child("person").child(uid);
        d_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             Double s_long = Double.parseDouble(dataSnapshot.child("address_long").getValue().toString());
             Double s_lat = Double.parseDouble(dataSnapshot.child("address_lat").getValue().toString());
                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(s_lat, s_long, 1);
                    if (addresses != null && addresses.size() > 0) {
                        city = addresses.get(0).getAdminArea();
                        upload_data();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return city;
    }

    private void upload_data(){

        //"first_click" used for if order have problem for upload or image
        // the key for both will be same
        // if i didn't do it if image was uploaded but order not uploaded i will correct my error
        // but image was uploaded 2 times with different key
        // so this check prevent redundant data or multiple key on every time click on button
        if(first_click) {
            d_ref = FirebaseDatabase.getInstance().getReference().child("order").child(date.getText().toString()).
                    //child(delivery_type_string).push();
                            child(city).child(delivery_type_string).push();
            first_click = false;
        }

        // upload photo by post key
        StorageReference pRef = FirebaseStorage.getInstance().getReference().child("order").child(date.getText().toString()).
                child(delivery_type_string).child(d_ref.getKey());
        // child(get_user_city()).child(delivery_type_string).child(d_ref.getKey());
        UploadTask up = pRef.putFile(Uri.parse(ur.toString()));
        // check photo was uploaded or not
        up.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    order_details.setS_uid(FirebaseAuth.getInstance().getUid().toString());
                    order_details.setDate("");
                    d_ref.child("r_long").setValue(order_details.getR_long());
                    d_ref.child("r_lat").setValue(order_details.getR_lat());
                    // check data was uploaded or not
                    d_ref.setValue(order_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( Task<Void> task) {
                            if(task.isSuccessful()){
                                // move to submit activity
                                Intent submit = new Intent(MainActivity.this, submit.class);
                                submit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //"putextra order " send order class with all data
                                // because in submit activity will use it for ask user
                                // do you need continue with same order details or not
                                submit.putExtra("order",order_details);
                                finish();
                                startActivity(submit);
                            }else
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }




    public void get_reciever_address(View view) {
        put_data_in_class_order();
        Intent map = new Intent(this,MapsActivity.class);
        map.putExtra("order",order_details);
        startActivity(map);
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
                          date.setText(day + "-" + (month +1) + "-" + year);
                          date.setTextSize(22);
                      }else {

                          Toast.makeText(MainActivity.this,
                                  "sorry date must be greater than or equal "+ c.get(Calendar.YEAR) + "-" +
                                          c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();
                      }
                    }
                },
               c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH) );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void put_data_in_class_order (){
        order_details.setDate(date.getText().toString());
        order_details.setDelivery_estimate(delivery_estimate.getText().toString());
        order_details.setDescription(description.getText().toString());
        order_details.setProvide_pay(provide_pay.getText().toString());
        order_details.setR_address(r_address.getText().toString());
        order_details.setR_name(r_name.getText().toString());
        order_details.setR_phone(r_phone.getText().toString());
        order_details.setWeight(weight.getText().toString());
    }

    private void get_data_from_class_order(){
        if(order_details != null) {
            date.setText(order_details.getDate());
            delivery_estimate.setText(order_details.getDelivery_estimate());
            description.setText(order_details.getDescription());
            provide_pay.setText(order_details.getProvide_pay());
            weight.setText(order_details.getWeight());
            if (get_reciever_data) {
                r_address.setText(order_details.getR_address());
                r_name.setText(order_details.getR_name());
                r_phone.setText(order_details.getR_phone());
            }
        }

    }

    private boolean check_for_empty_input(){
       // create new alart and be it ready for new one
        // pop up if error occur
        alart = new AlertDialog.Builder(this);
        alart.setCancelable(true);
        alart.setTitle("warning");
        alart.setIcon(R.drawable.about_us);

        if(TextUtils.isEmpty(date.getText().toString())) {
            dialog.dismiss();
            alart.setMessage("enter date");
            dialog = alart.create();
            dialog.show();
            date.setError("enter date");
            return false;
        }
        if(TextUtils.isEmpty(delivery_estimate.getText().toString())){
            alart.setMessage("enter delivery_estimate");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            date.setError("enter date");
            delivery_estimate.setError("enter this field");
            return false;
        }
        if(TextUtils.isEmpty(description.getText().toString())){
            alart.setMessage("enter description");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            description.setError("enter description");
            return false;
        }
        if(TextUtils.isEmpty(provide_pay.getText().toString())){
            alart.setMessage("enter provide pay");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            provide_pay.setError("enter provide pay");
            return false;
        }
        if(TextUtils.isEmpty(r_address.getText().toString())){
            alart.setMessage("enter receiver address");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            r_address.setError("enter receiver address");
            return false;
        }
        if(TextUtils.isEmpty(r_name.getText().toString())){
            alart.setMessage("enter receiver name");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            r_name.setError("enter receiver name");
            return false;
        }
        if(TextUtils.isEmpty(r_phone.getText().toString())){
            alart.setMessage("enter receiver phone");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            r_phone.setError("enter receiver phone");
            return false;
        }
        if(r_phone.getText().toString().length()!=11){
            alart.setMessage("receiver phone must be 11 number");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            r_phone.setError("receiver phone must be 11 number");
            return false;
        }
        if(TextUtils.isEmpty(weight.getText().toString())){
            alart.setMessage("enter estimated weight");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            weight.setError("enter estimated weight");
        }
        if(ur==null){
            order_photo.setBackgroundColor(Color.RED);
            alart.setMessage("select order photo");
            dialog.dismiss();
            dialog = alart.create();
            dialog.show();
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show();
            return false;
        }
          return true;
    }
}
