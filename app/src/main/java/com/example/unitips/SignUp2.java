package com.example.unitips;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unitips.HomePage.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SignUp2 extends AppCompatActivity {

    // Activity
    private Button mChooseImage;
    private Button mCompleteSignUp;
    private EditText mUserName;
    private EditText mUniversity;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    // Firebase
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseFirestore mFireStore;

    // Other
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private String mCurrentUser;
    private String profilePicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        // Firebase
        mFireStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("profile_pictures");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Activity
        mProgressBar = findViewById(R.id.sign_up_2_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mChooseImage = findViewById(R.id.sign_up_2_choose_image_btn);
        mCompleteSignUp = findViewById(R.id.sign_up_2_complete_sign_up_btn);
        mImageView = findViewById(R.id.sign_up_2_image_view);
        mUserName = findViewById(R.id.sign_up_2_user_name_edit_text);
        mUniversity = findViewById(R.id.sign_up_2_university_edit_text);

        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mCompleteSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    // Uploads the users info to the FireStore database
    private void uploadInfo() {
        String username = mUserName.getText().toString().trim();
        String university = mUniversity.getText().toString().trim();

        if (username.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(SignUp2.this, "Please create A username", Toast.LENGTH_LONG).show();
        } else if (university.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(SignUp2.this, "Please enter your university", Toast.LENGTH_LONG).show();
        }

        // Check Database Uploads Correctly
        else {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("university", university);
            userMap.put("profile_picture", profilePicturePath);
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mFireStore.collection("users").document(currentFirebaseUser.getUid()).set(userMap, SetOptions.merge())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUp2.this, "Firebase Error!", Toast.LENGTH_LONG).show();
                        }
                    });
            mProgressBar.setVisibility(View.GONE);
            startActivity(new Intent(SignUp2.this, HomePage.class));
        }
    }

    // Gets image from device
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Loads selected image to ImageView if correct image is selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(mImageView);
        }
    }

    // Gets the file extension for the image
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Uploads image files to the database
    private void uploadFile() {
        if (mImageUri != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // The name of the image
            final StorageReference fileReference = mStorageRef.child(mCurrentUser + "_profile_picture" + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageUpload imageUpload = new ImageUpload(mUserName.getText().toString().trim(), taskSnapshot.getStorage().getDownloadUrl().toString());
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(imageUpload);
                    mStorageRef.getDownloadUrl();
                    profilePicturePath = fileReference.toString();
                    uploadInfo();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp2.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

    // Minimises app
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
