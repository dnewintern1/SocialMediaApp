package com.base.socialmedaapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword,Username;
    private Button signupButton;
    private TextView loginRedirectText;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupButton = findViewById(R.id.signup_button);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText );
        signupEmail = findViewById(R.id.signup_email);

        Username = findViewById(R.id.Username);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = Username.getText().toString().trim();

                if(user.isEmpty()){
                    signupEmail.setText("email can not be empty");
                }
                if(pass.isEmpty()){
                    signupPassword.setText("password field can not be empty");
                }

                else{
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                UserDetails userDetails = new UserDetails(name);

                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Twitter User");

                                referenceProfile.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                        }else{

                                        }

                                    }
                                });

                                Toast.makeText(SignUpActivity.this, "sign up is successful", Toast.LENGTH_SHORT).show();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(Username.getText().toString())
                                        .build();

                                FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "profile name updated" , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                startActivity(new Intent(SignUpActivity.this , LoginActivity.class));
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "Signup failed" + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this , LoginActivity.class));


            }
        });


    }
}