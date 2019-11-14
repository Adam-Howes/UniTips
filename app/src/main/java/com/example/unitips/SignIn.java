package com.example.unitips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.unitips.HomePage.HomePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    // Activity items
    private EditText mEmail;
    private EditText mPassword;
    private Button mButton;
    private ProgressBar mProgressbar;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomePage.class));
        }

        mProgressbar = findViewById(R.id.sign_in_progress_bar);
        mProgressbar.setVisibility(View.GONE);
        mEmail = findViewById(R.id.sign_in_email_text);
        mPassword = findViewById(R.id.sign_in_password_text);
        mButton = findViewById(R.id.sign_in_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    // Signs the user into Firebase authentication servers
    private void signIn() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignIn.this, "Enter Email", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignIn.this, "Enter Password", Toast.LENGTH_LONG).show();
        } else {
            mProgressbar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        mProgressbar.setVisibility(View.GONE);
                        Toast.makeText(SignIn.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                    } else {
                        mProgressbar.setVisibility(View.GONE);
                        startActivity(new Intent(SignIn.this, HomePage.class));
                    }
                }
            });
        }
    }

    // Text View that takes user to SignUp activity
    public void toSignUpTextView(View view) {
        startActivity(new Intent(SignIn.this, SignUp.class));
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);

    }

    // Minimises app
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}

