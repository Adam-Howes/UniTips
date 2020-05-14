package com.example.unitips.Activities;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    // Activity
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mButton;
    private ProgressBar mProgressbar;

    // FireBase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFireStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mProgressbar = findViewById(R.id.sign_up_progress_bar);
        mProgressbar.setVisibility(View.GONE);
        mEmail = findViewById(R.id.signUpEmailText);
        mPassword = findViewById(R.id.signUpPasswordText);
        mConfirmPassword = findViewById(R.id.signUpPasswordConfirmText);

        mButton = findViewById(R.id.signUpButtonBTN);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    // Sign up the user by converting and storing email and passwords to a string and uploading
    // them to FireBase auth servers.
    private void signUp() {
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();

        // Check for user input errors
        if (email.isEmpty()) {
            Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(SignUp.this, "Please enter your password", Toast.LENGTH_LONG).show();
        } else if (confirmPassword.isEmpty()) {
            Toast.makeText(SignUp.this, "Please confirm your password", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUp.this, "Passwords must be the same", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(SignUp.this, "Password too short", Toast.LENGTH_LONG).show();
        }

        // Attempts to add a new user to FireBase login server
        else {
            mProgressbar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                mProgressbar.setVisibility(View.GONE);
                                Toast.makeText(SignUp.this, "Failed to create account:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            } else {
                                // Store user data to database
                                Map<String, String> userMap = new HashMap<>();
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                userMap.put("email", email);
                                userMap.put("useruid", currentFirebaseUser.getUid());
                                mFireStore.collection("users").document(currentFirebaseUser.getUid()).set(userMap)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUp.this, "Error", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(SignUp.this, SignUp2.class));
                                    }
                                });

                            }
                        }
                    });
        }
    }

    // Text View that takes user to Account activity
    public void toSignInTextView(View view) {
        startActivity(new Intent(SignUp.this, SignIn.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    // Minimises app
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
