package com.example.unitips.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unitips.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    // Activity items
    private EditText mEmail;
    private EditText mPassword;
    public ProgressBar mProgressbar;
    private Button mSignInButton;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button testButton;

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

        // findViewById
        mProgressbar = findViewById(R.id.sign_in_progress_bar);
        mProgressbar.setVisibility(View.GONE);
        mEmail = findViewById(R.id.sign_in_email_text);
        mPassword = findViewById(R.id.sign_in_password_text);
        mSignInButton = findViewById(R.id.sign_in_button);

        // setOnClickListener
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString();

                signIn(email, password);
            }
        });
    }

    public boolean checkInternet() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    // TODO: Add timeout for slow internet/disconnection
    private void signIn(String email, String password) {

        if (email == null) {
            Toast.makeText(SignIn.this, "Enter Email", Toast.LENGTH_LONG).show();
        } else if (password == null) {
            Toast.makeText(SignIn.this, "Enter Password", Toast.LENGTH_LONG).show();
        } else if (checkInternet() == false) {
            Toast.makeText(SignIn.this, "Please connect to the internet and try again", Toast.LENGTH_LONG).show();
        } else {
            Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        mProgressbar.setVisibility(View.GONE);

                        // TODO: Make better error messages
                        Toast.makeText(SignIn.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                    } else {
                        mProgressbar.setVisibility(View.GONE);
                        startActivity(new Intent(SignIn.this, HomePage.class));
                    }
                }
            });
        }
    }

    // To SignUp activity
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