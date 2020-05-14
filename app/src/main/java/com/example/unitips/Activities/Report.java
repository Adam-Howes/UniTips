package com.example.unitips.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unitips.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Report extends AppCompatActivity {

    // Activity
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private EditText mEditText;

    // Firebase
    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private Long reportID;

    // Other
    private long userID;
    private long postID;
    private String reportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Get the user or post id from the previous activity
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", userID);
        Intent intent1 = getIntent();
        postID = intent1.getLongExtra("postID", postID);
        reportType = String.valueOf((userID + postID));

        if (!String.valueOf(userID).isEmpty()) {
            setTitle("Report Post");
            reportType = String.valueOf(userID);
        }
        if (!String.valueOf(postID).isEmpty()) {
            setTitle("Report User");
            reportType = String.valueOf(postID);
        }

        mRadioGroup = findViewById(R.id.report_radio_group);
        mEditText = findViewById(R.id.report_edit_text);
        Button mButton = findViewById(R.id.report_submit_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void submitReport() {
        String reportDescription = mEditText.getText().toString().trim();
        if (mRadioButton == null) {
            Toast.makeText(Report.this, "Enter Category", Toast.LENGTH_SHORT).show();
        } else if (reportDescription.isEmpty()) {
            Toast.makeText(Report.this, "Enter A Reason", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("report_type", reportType);
            stringMap.put("report_category", mRadioButton.getText().toString());
            stringMap.put("report_description", reportDescription);

            mFireStore.collection("reports").document(String.valueOf(reportID = System.currentTimeMillis())).set(stringMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Report.this, "Report Sent", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Report.this, "Report Failed, please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Checks what category has been selected
    public void checkButton(View view) {
        int radioId = mRadioGroup.getCheckedRadioButtonId();
        mRadioButton = findViewById(radioId);
    }
}
