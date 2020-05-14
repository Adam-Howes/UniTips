package com.example.unitips.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.unitips.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewProfile extends AppCompatActivity {

    // Class
    private static final String TAG = "ViewProfile";

    // Activity
    private ImageView mImageView;
    private TextView mTextViewUsername;
    private TextView mTextViewEmail;

    // FireBase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference mUserDatabaseRef = db.document("users/" + currentFirebaseUser.getUid());
    private DatabaseReference mUserImageDatabaseRef = FirebaseDatabase.getInstance().getReference("profile_pictures");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private Uri downloadUrl;
    private String username;
    private String email;
    private List<ImageUpload> mImageUploads;
    private Context context = ViewProfile.this;
    private String profilePicturePath;
    StorageReference getProfilePicGSRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loadData();

        mImageView = findViewById(R.id.view_profile_profile_picture_image_view);
        mTextViewUsername = findViewById(R.id.view_profile_text_view_username);
        mTextViewEmail = findViewById(R.id.view_profile_text_view_email);
        mImageUploads = new ArrayList<>();

        mUserImageDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ImageUpload imageUpload = postSnapshot.getValue(ImageUpload.class);
                    mImageUploads.add(imageUpload);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageByUrl() {

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(context)
                .load(downloadUrl.toString())
                .apply(options)
                .into(mImageView);
    }

    private void loadData() {
        mUserDatabaseRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            username = documentSnapshot.getString("username");
                            email = documentSnapshot.getString("email");

                            profilePicturePath = documentSnapshot.getString("profile_picture");

                            getProfilePicGSRef = storage.getReferenceFromUrl(profilePicturePath);
                            getProfilePicGSRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    downloadUrl = uri;
                                    loadImageByUrl();
                                }
                            });

                            setTitle(username + "'s Profile");
                            mTextViewUsername.setText(username);
                            mTextViewEmail.setText(email);

                        } else {
                            Toast.makeText(ViewProfile.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewProfile.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_profile_menu, menu);
        return true;
    }

    // Options menu for home page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(ViewProfile.this, Report.class);
                intent.putExtra("userID", String.valueOf(currentFirebaseUser));
                startActivity(intent);
                return true;

            case android.R.id.home:
                Intent intent1 = new Intent(this, SplashScreen.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
