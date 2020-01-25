package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import afu.org.checkerframework.checker.igj.qual.I;

public class sign_up extends AppCompatActivity {

    private EditText firstName, lastName, phoneNumber, ssnNumber, Email, Password;
    private TextView Address;
    private ImageView ssnImage;
    private Button creatUser;

    private String userId;
    private boolean get_User_Data, comefrom;
    private user_data user;
    private Uri ur;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstName = findViewById(R.id.fName);
        lastName = findViewById(R.id.lName);
        phoneNumber = findViewById(R.id.pNumber);
        ssnNumber = findViewById(R.id.ssnNumber);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Address = findViewById(R.id.address);

        ssnImage = findViewById(R.id.ssnImage);

        creatUser = findViewById(R.id.creatBtn);



        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference().child("person");

        get_User_Data = getIntent().getBooleanExtra("finish",false);
        user = (user_data) getIntent().getSerializableExtra("user");

        String fname = firstName.getText().toString();
        String lname = lastName.getText().toString();
        String pnumber = phoneNumber.getText().toString();
        String ssnnumber = ssnNumber.getText().toString();
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        creatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()) {
                    mAuth.createUserWithEmailAndPassword(Email.getText().toString(), Password.getText().toString())
                        .addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    userId = mAuth.getCurrentUser().getUid();
                                    mDatabase.child("person").child(userId).setValue(user);
                                    UploadTask up = mStorage.child(userId).putFile(Uri.parse(ur.toString()));
                                    up.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(sign_up.this, "Auth succeed", Toast.LENGTH_SHORT).show();
                                                Intent main = new Intent(sign_up.this, MainActivity.class);
                                                startActivity(main);
                                                finish();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
            }
        });

        get_data_from_class_user();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    public void get_user_address(View view){
        put_data_in_user_class();
        Intent map = new Intent(sign_up.this, MapsActivity.class);
        map.putExtra("user", user);
        map.putExtra("whereFrom", true);
        map.putExtra("edit" , "no");
        startActivity(map);
    }

    public void put_data_in_user_class(){
        user = new user_data();
        user.setFirst_name(firstName.getText().toString().trim());
        user.setLast_name(lastName.getText().toString().trim());
        user.setPhoneNumber(phoneNumber.getText().toString().trim());
        user.setSsnNumber(ssnNumber.getText().toString().trim());
        user.setEmail(Email.getText().toString().trim());
    }

    public void get_data_from_class_user(){
        if(user != null) {
            firstName.setText(user.getFirst_name());
            lastName.setText(user.getLast_name());
            phoneNumber.setText(user.getPhoneNumber());
            ssnNumber.setText(user.getSsnNumber());
            Address.setText(user.getAddress());
            Email.setText(user.getEmail());
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
            ActivityCompat.requestPermissions(sign_up.this,permission,1);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // request get photos from galary
        if(requestCode==2&&resultCode==RESULT_OK) {
            ur = data.getData();
            Picasso.with(sign_up.this).load(ur).into(ssnImage);
        }
        // request get photo permission
        if (requestCode==1&&resultCode== RESULT_OK){
            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }
    }

    private Boolean check(){
        if(TextUtils.isEmpty(firstName.getText().toString())){
            firstName.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(lastName.getText().toString())){
            lastName.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(phoneNumber.getText().toString())){
            phoneNumber.setError("can't be empty");
            return false;
        }
        if(phoneNumber.getText().toString().length() != 11){
            phoneNumber.setError("Enter right number");
            return false;
        }
        if(TextUtils.isEmpty(ssnNumber.getText().toString())){
            ssnNumber.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(Email.getText().toString())){
            Email.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(Password.getText().toString())){
            Password.setError("can't be empty");
            return false;
        }
        if(ur==null){
            ssnImage.setBackgroundColor(Color.RED);
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
