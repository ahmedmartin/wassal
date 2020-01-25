package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import afu.org.checkerframework.checker.oigj.qual.O;

public class EditUserInfoActivity extends AppCompatActivity {

    private EditText fname, lname, pnumber, password, address,ssnnum;
    private Button Done;
    private ImageView ssnImage;

    private boolean comefromgoogle;
    private Uri ur;
    private user_data user;
    private String firstname, lastname, phonenumber, ssnnumber;

    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        pnumber = findViewById(R.id.pnumber);
        password = findViewById(R.id.pass);
        address = findViewById(R.id.address);
        Done = findViewById(R.id.doneBtn);
        ssnnum = findViewById(R.id.ssnNum);
        ssnImage = findViewById(R.id.ssnImg);

        comefromgoogle = getIntent().getBooleanExtra("come_from_google_sign_in", false);

        firstname = getIntent().getStringExtra("fn");
        lastname = getIntent().getStringExtra("ln");
        phonenumber = getIntent().getStringExtra("pn");
        ssnnumber = getIntent().getStringExtra("sn");

        fname.setText(firstname);
        lname.setText(lastname);
        pnumber.setText(phonenumber);

        user = (user_data) getIntent().getSerializableExtra("user");

        get_data_from_class_user();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("person").child(mUser.getUid());

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    if (comefromgoogle) {
                        mDatabase.child("first_name").setValue(fname.getText().toString().trim());
                        mDatabase.child("last_name").setValue(lname.getText().toString().trim());
                        mDatabase.child("phoneNumber").setValue(pnumber.getText().toString().trim());
                        mDatabase.child("address").setValue(user.getAddress());
                        mDatabase.child("address_lat").setValue(user.getAddress_lat());
                        mDatabase.child("address_long").setValue(user.getAddress_long());
                        if (!TextUtils.isEmpty(password.getText().toString())) {
                            mUser.updatePassword(password.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                Toast.makeText(getApplicationContext(), "Data changed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        startActivity(new Intent(EditUserInfoActivity.this, MainActivity.class));
                        finish();
                    } else {
                        ssnnum.setVisibility(View.INVISIBLE);
                        ssnImage.setVisibility(View.INVISIBLE);
                        if (TextUtils.isEmpty(address.getText().toString())) {
                            mDatabase.child("first_name").setValue(fname.getText().toString().trim());
                            mDatabase.child("last_name").setValue(lname.getText().toString().trim());
                            mDatabase.child("phoneNumber").setValue(pnumber.getText().toString().trim());
                            if (!TextUtils.isEmpty(password.getText().toString())) {
                                mUser.updatePassword(password.getText().toString().trim())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(getApplicationContext(), "Data changed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            Intent useri = new Intent(getApplicationContext(), UserInfoActivity.class);
                            startActivity(useri);
                            finish();
                        } else {
                            mDatabase.child("first_name").setValue(fname.getText().toString().trim());
                            mDatabase.child("last_name").setValue(lname.getText().toString().trim());
                            mDatabase.child("phoneNumber").setValue(pnumber.getText().toString().trim());
                            mDatabase.child("address").setValue(user.getAddress());
                            mDatabase.child("address_lat").setValue(user.getAddress_lat());
                            mDatabase.child("address_long").setValue(user.getAddress_long());
                            if (!TextUtils.isEmpty(password.getText().toString())) {
                                mUser.updatePassword(password.getText().toString().trim())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(getApplicationContext(), "Data changed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            Intent useri = new Intent(getApplicationContext(), UserInfoActivity.class);
                            startActivity(useri);
                            finish();
                        }
                    }
                }
            }
        });
    }

    public void get_user_address(View view){
        put_data_in_user_class();
        Intent map = new Intent(EditUserInfoActivity.this, MapsActivity.class);
        map.putExtra("user", user);
        map.putExtra("whereFrom", true);
        map.putExtra("edit", "yes");
        startActivity(map);
    }

    public void put_data_in_user_class(){
        user = new user_data();
        user.setFirst_name(firstname);
        user.setLast_name(lastname);
        user.setPhoneNumber(phonenumber);
        user.setSsnNumber(ssnnumber);

    }
    public void get_data_from_class_user(){
        if(user != null) {
            fname.setText(user.getFirst_name());
            lname.setText(user.getLast_name());
            pnumber.setText(user.getPhoneNumber());
            address.setText(user.getAddress());
        }
    }

    public void upload_image(View view){
        String [] permission ={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permission[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[1])== PackageManager.PERMISSION_GRANTED){

            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }else
            ActivityCompat.requestPermissions(EditUserInfoActivity.this,permission,1);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // request get photos from galary
        if(requestCode==2&&resultCode==RESULT_OK) {
            ur = data.getData();
            Picasso.with(EditUserInfoActivity.this).load(ur).into(ssnImage);
        }
        // request get photo permission
        if (requestCode==1&&resultCode== RESULT_OK){
            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }
    }

    private Boolean check(){
        if(TextUtils.isEmpty(fname.getText().toString())){
            fname.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(lname.getText().toString())){
            lname.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(pnumber.getText().toString())){
            pnumber.setError("can't be empty");
            return false;
        }
        if(pnumber.getText().toString().length() != 11){
            pnumber.setError("Enter right number");
            return false;
        }
        if(TextUtils.isEmpty(ssnnum.getText().toString())){
            ssnnum.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("can't be empty");
            return false;
        }
        if(ur==null){
            ssnImage.setBackgroundColor(Color.RED);
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

/*
    // Sign in success, update UI with the signed-in user's information
    order_data order_details = new order_data();
    Intent main = new Intent(sign_in.this,MainActivity.class);
                            main.putExtra("order", order_details);
    startActivity(main);
    finish();*/
}
