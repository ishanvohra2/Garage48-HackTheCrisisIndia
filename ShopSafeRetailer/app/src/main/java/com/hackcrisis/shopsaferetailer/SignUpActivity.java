package com.hackcrisis.shopsaferetailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hackcrisis.shopsaferetailer.Data.ShopDetails;
import com.hackcrisis.shopsaferetailer.Data.User;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputPassword, inputName, addressEt, pinCodeEt, phoneEt;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private TextView btnSignIn;
    private FirebaseAuth auth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.email_et);
        inputPassword = findViewById(R.id.password_et);
        inputName = findViewById(R.id.name_et);
        addressEt = findViewById(R.id.address_et);
        pinCodeEt = findViewById(R.id.pin_et);
        phoneEt = findViewById(R.id.phone_et);
        btnSignUp = findViewById(R.id.signup_btn);
        progressBar = findViewById(R.id.progress_bar);
        btnSignIn = findViewById(R.id.already_have_acc_tv);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                final String date = day + "/" + month + "/" + year;

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Please enter your email id");
                    return;
                }

                if (TextUtils.isEmpty(phoneEt.getText().toString())) {
                    inputEmail.setError("Please enter your phone number");
                    return;
                }

                if (TextUtils.isEmpty(addressEt.getText().toString())) {
                    addressEt.setError("Please enter your address");
                    return;
                }

                if (TextUtils.isEmpty(pinCodeEt.getText().toString())) {
                    pinCodeEt.setError("Please enter your PIN Code");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Please enter password");
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError("Password too short, enter minimum 6 characters!");
                    return;
                }

                if (inputName.getText().toString().length() == 0) {
                    inputName.setError("Enter name");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String id = auth.getCurrentUser().getUid();
                                    ShopDetails shopDetails = new ShopDetails(inputName.getText().toString(),
                                            addressEt.getText().toString(),
                                            phoneEt.getText().toString(),
                                            email,
                                            pinCodeEt.getText().toString(),
                                            id);

                                    writeNewUser(shopDetails, id);
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.putExtra("name", auth.getCurrentUser().getDisplayName());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    private void writeNewUser(ShopDetails shopDetails, String id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("shopDetails").child(id).setValue(shopDetails);
    }
}
