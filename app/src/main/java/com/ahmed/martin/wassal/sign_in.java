package com.ahmed.martin.wassal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class sign_in extends AppCompatActivity {


    TextView email ,password;

    FirebaseAuth mauth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mauth= FirebaseAuth.getInstance();
        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_password);

        // Gmail.....
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton button = findViewById(R.id.google_sign_in);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_sign_in();
            }
        });
        //.....
    }

    public void google_sign_in() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 11);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Gmail.....
        if (requestCode == 11) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                String s = e.getMessage().toString();
                Toast.makeText(sign_in.this, s, Toast.LENGTH_SHORT).show();
            }
        }
        //......
    }
    //Gmail.....
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            order_data order_details = new order_data();
                            Intent main = new Intent(sign_in.this,MainActivity.class);
                            main.putExtra("order", order_details);
                            startActivity(main);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //.....

        @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mauth.getCurrentUser();

        if(currentUser!=null){
            order_data order_details = new order_data();
            Intent main = new Intent(sign_in.this,MainActivity.class);
            main.putExtra("order", order_details);
            startActivity(main);
            finish();
        }


    }

    // if user forget his password
    public void forget_password(View view) {
        String Email = email.getText().toString();
        // check if email is empty
        if(TextUtils.isEmpty(Email))
            email.setError("please enter your email !!");
        else{
            // reset your password
            mauth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    // if sent email to reset show message (check your email to reset password)
                    if(task.isSuccessful())
                        Toast.makeText(sign_in.this, "check your email to reset password", Toast.LENGTH_SHORT).show();
                        // if have error
                    else
                        Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // sign in with email and password
    public void sign_in(View view) {
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        //check if password is empty or not
        if(TextUtils.isEmpty(Email))
            email.setError("please enter your email !!");
        else{
            // check if password is empty or not
            if(TextUtils.isEmpty(Password))
                password.setError("please enter your password !!");
            else {
                // sign in with email and password
                mauth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        // if signed in start main activity
                        if (task.isSuccessful()) {
                            order_data order_details = new order_data();
                            Intent main = new Intent(sign_in.this,MainActivity.class);
                            main.putExtra("order", order_details);
                            startActivity(main);
                            finish();
                            // if not show error message
                        }else
                            Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    public void sign_up(View view) {
        startActivity(new Intent(sign_in.this,sign_up.class));
    }
}
