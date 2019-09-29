package com.team33.meetingmate.ui.authentication;

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
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.team33.meetingmate.R;

public class ForgotPasswordActivity extends AppCompatActivity{
    private Button resetBtn;
    private Button signupBtn;
    private TextView emailTV;
    private TextView textTV;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeUI();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, RegistrationActivity.class));
            }
        });

    }

    private void initializeUI () {
        emailTV = findViewById(R.id.email);
        resetBtn = findViewById(R.id.reset);
        signupBtn = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressBar);
        textTV = findViewById(R.id.text);

        signupBtn.setVisibility(View.INVISIBLE);
        textTV.setVisibility(View.INVISIBLE);
    }

    private void resetPassword() {
        progressBar.setVisibility(View.VISIBLE);

        String email;
        email = emailTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "The password reset link is sent to your email. Please check it to login.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid email. Please enter the email again. If you have not registered, please click Sign Up to start a new account.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            signupBtn.setVisibility(View.VISIBLE);
                            textTV.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
