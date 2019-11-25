package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity {

    private TextView personalInfo, firstName, lastName, phoneNumber, Address;
    private Button Edit;

    private String firstN, lastN,phoneN, ssnN;

    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        personalInfo = findViewById(R.id.pInfo);
        firstName = findViewById(R.id.fName);
        lastName = findViewById(R.id.lName);
        phoneNumber = findViewById(R.id.pNumber);
        Address = findViewById(R.id.address);
        Edit = findViewById(R.id.editBtn);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("person").child(mUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        firstName.setText(dataSnapshot.child("first_name").getValue().toString());
                        lastName.setText(dataSnapshot.child("last_name").getValue().toString());
                        phoneNumber.setText(dataSnapshot.child("phoneNumber").getValue().toString());
                        Address.setText(dataSnapshot.child("address").getValue().toString());
                        firstN = dataSnapshot.child("first_name").getValue().toString();
                        lastN = dataSnapshot.child("last_name").getValue().toString();
                        phoneN = dataSnapshot.child("phoneNumber").getValue().toString();
                        ssnN = dataSnapshot.child("ssnNumber").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editInfo = new Intent(getApplicationContext(), EditUserInfoActivity.class);
                editInfo.putExtra("fn", firstN);
                editInfo.putExtra("ln", lastN);
                editInfo.putExtra("pn", phoneN);
                editInfo.putExtra("sn", ssnN);
                startActivity(editInfo);
                finish();
            }
        });

    }
}
