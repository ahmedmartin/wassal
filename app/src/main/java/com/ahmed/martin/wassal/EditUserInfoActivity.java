package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import afu.org.checkerframework.checker.oigj.qual.O;

public class EditUserInfoActivity extends AppCompatActivity {

    private EditText fname, lname, pnumber, password, address;
    private Button Done;

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
                if(TextUtils.isEmpty(address.getText().toString())){
                    mDatabase.child("first_name").setValue(fname.getText().toString().trim());
                    mDatabase.child("last_name").setValue(lname.getText().toString().trim());
                    mDatabase.child("phoneNumber").setValue(pnumber.getText().toString().trim());
                    if(!TextUtils.isEmpty(password.getText().toString())) {
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
                }else{
                    mDatabase.child("first_name").setValue(fname.getText().toString().trim());
                    mDatabase.child("last_name").setValue(lname.getText().toString().trim());
                    mDatabase.child("phoneNumber").setValue(pnumber.getText().toString().trim());
                    mDatabase.child("address").setValue(user.getAddress());
                    mDatabase.child("address_lat").setValue(user.getAddress_lat());
                    mDatabase.child("address_long").setValue(user.getAddress_long());
                    if(!TextUtils.isEmpty(password.getText().toString())) {
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
        }
    }
}
