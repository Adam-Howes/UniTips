package com.example.unitips;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unitips.HomePage.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateContent extends AppCompatActivity {

    // Activity Items
    private EditText mTitle;
    private EditText mDescription;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private Button mAddImage;
    private Button mStartUpload;
    private ProgressBar mProgressBar;
    private ImageView mImageView0;
    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageView4;
    private EditText description0;
    private EditText description1;
    private EditText description2;
    private EditText description3;
    private EditText description4;

    // Is created when the user uploads a file to FireStore, should be the same as the document ID
    // in the database and can be passed on to the next activity
    long postID;

    // Image upload handlers
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri0;
    private Uri mImageUri1;
    private Uri mImageUri2;
    private Uri mImageUri3;
    private Uri mImageUri4;

    private int imageCount = 0;

    // Firebase
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("image_posts");
    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

    // Upload Sucess Checkers
    boolean textUploadSucess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("New Post");

        mProgressBar = findViewById(R.id.view_content_progress_bar);
        mTitle = findViewById(R.id.new_content_title_edit_text);
        mDescription = findViewById(R.id.new_content_description_edit_text);
        mRadioGroup = findViewById(R.id.new_content_radio_group);
        mAddImage = findViewById(R.id.new_content_add_image_button);
        mStartUpload = findViewById(R.id.new_content_start_upload_button);

        mImageView0 = findViewById(R.id.new_content_image_view_0);
        mImageView1 = findViewById(R.id.new_content_image_view_1);
        mImageView2 = findViewById(R.id.new_content_image_view_2);
        mImageView3 = findViewById(R.id.new_content_image_view_3);
        mImageView4 = findViewById(R.id.new_content_image_view_4);

        description0 = findViewById(R.id.new_content_edit_text_0);
        description1 = findViewById(R.id.new_content_edit_text_1);
        description2 = findViewById(R.id.new_content_edit_text_2);
        description3 = findViewById(R.id.new_content_edit_text_3);
        description4 = findViewById(R.id.new_content_edit_text_4);

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        mStartUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload();
                mStartUpload.setVisibility(View.GONE);

            }
        });
        mProgressBar.setVisibility(View.GONE);
    }

    private void startUpload(){
        uploadInfo();
        uploadFile();

        Toast.makeText(CreateContent.this, "Upload Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateContent.this, HomePage.class);
        startActivity(intent);
    }

    // Gets an image from the system file chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (imageCount == 0) {
                mImageUri0 = data.getData();
                Glide.with(this).load(mImageUri0).into(mImageView0);
                mImageView0.setVisibility(View.VISIBLE);
                description0.setVisibility(View.VISIBLE);
            }
            if (imageCount == 1) {
                mImageUri1 = data.getData();
                Glide.with(this).load(mImageUri1).into(mImageView1);
                mImageView1.setVisibility(View.VISIBLE);
                description1.setVisibility(View.VISIBLE);
            }
            if (imageCount == 2) {
                mImageUri2 = data.getData();
                Glide.with(this).load(mImageUri2).into(mImageView2);
                mImageView2.setVisibility(View.VISIBLE);
                description2.setVisibility(View.VISIBLE);
            }
            if (imageCount == 3) {
                mImageUri3 = data.getData();
                Glide.with(this).load(mImageUri3).into(mImageView3);
                mImageView3.setVisibility(View.VISIBLE);
                description3.setVisibility(View.VISIBLE);
            }
            if (imageCount == 4) {
                mImageUri4 = data.getData();
                Glide.with(this).load(mImageUri4).into(mImageView4);
                mImageView4.setVisibility(View.VISIBLE);
                description4.setVisibility(View.VISIBLE);
                mAddImage.setVisibility(View.GONE);
            }
            imageCount++;
        }
    }

    private void uploadFile() {

        // Creates the folder to store the posts in
        mStorageRef = FirebaseStorage.getInstance().getReference("image_posts/" + postID);

        if (mImageUri0 != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // The name of the image
            final StorageReference fileReference = mStorageRef.child("0" + "." + getFileExtension0(mImageUri0));
            fileReference.putFile(mImageUri0).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Gets the download url
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // imageLink is download Link
                            String imageLink = uri.toString();
                            Map<String, String> imagePathMap = new HashMap<>();
                            imagePathMap.put("image_file_url_0", imageLink);
                            mFireStore.collection("posts").document(String.valueOf(postID)).set(imagePathMap, SetOptions.merge());
                        }
                    });

                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateContent.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        if (mImageUri1 != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // The name of the image
            final StorageReference fileReference = mStorageRef.child("1" + "." + getFileExtension1(mImageUri1));
            fileReference.putFile(mImageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Gets the download url
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();
                            Map<String, String> imagePathMap = new HashMap<>();
                            imagePathMap.put("image_file_url_1", imageLink);
                            mFireStore.collection("posts").document(String.valueOf(postID)).set(imagePathMap, SetOptions.merge());
                        }
                    });
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateContent.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        if (mImageUri2 != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // The name of the image
            final StorageReference fileReference = mStorageRef.child("2" + "." + getFileExtension2(mImageUri2));
            fileReference.putFile(mImageUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Gets the download url
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();
                            Map<String, String> imagePathMap = new HashMap<>();
                            imagePathMap.put("image_file_url_2", imageLink);
                            mFireStore.collection("posts").document(String.valueOf(postID)).set(imagePathMap, SetOptions.merge());
                        }
                    });

                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateContent.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        if (mImageUri3 != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // The name of the image
            final StorageReference fileReference = mStorageRef.child("3" + "." + getFileExtension3(mImageUri3));
            fileReference.putFile(mImageUri3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Gets the download url
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();
                            Map<String, String> imagePathMap = new HashMap<>();
                            imagePathMap.put("image_file_url_3", imageLink);
                            mFireStore.collection("posts").document(String.valueOf(postID)).set(imagePathMap, SetOptions.merge());
                        }
                    });
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateContent.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (mImageUri4 != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // The name of the image
            final StorageReference fileReference = mStorageRef.child("4" + "." + getFileExtension4(mImageUri4));
            fileReference.putFile(mImageUri4).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Gets the download url
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();
                            Map<String, String> imagePathMap = new HashMap<>();
                            imagePathMap.put("image_file_url_4", imageLink);
                            mFireStore.collection("posts").document(String.valueOf(postID)).set(imagePathMap, SetOptions.merge());
                        }
                    });

                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateContent.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Uploads the users info to the FireStore database
    private void uploadInfo() {
        String title = mTitle.getText().toString().trim();
        String description = mDescription.getText().toString().trim();
        long voteCount = 1;

        if (title.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(CreateContent.this, "Enter Title", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(CreateContent.this, "Enter Description", Toast.LENGTH_SHORT).show();
        } else if (mRadioButton == null) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(CreateContent.this, "Enter Category", Toast.LENGTH_SHORT).show();
        } else {
            postID = System.currentTimeMillis();

            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("title", title);
            stringMap.put("description", description);
            stringMap.put("category", mRadioButton.getText().toString());
            stringMap.put("description0", description0.getText().toString());
            stringMap.put("description1", description1.getText().toString());
            stringMap.put("description2", description2.getText().toString());
            stringMap.put("description3", description3.getText().toString());
            stringMap.put("description4", description4.getText().toString());

            mFireStore.collection("posts").document(String.valueOf(postID)).set(stringMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            textUploadSucess = true;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            mFireStore.collection("posts").document(String.valueOf(postID)).update("voteCount", voteCount);
            mProgressBar.setVisibility(View.GONE);

        }
    }

    // Gets the file extension for the image
    private String getFileExtension0(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Gets the file extension for the image
    private String getFileExtension1(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Gets the file extension for the image
    private String getFileExtension2(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Gets the file extension for the image
    private String getFileExtension3(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Gets the file extension for the image
    private String getFileExtension4(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void checkButton(View view) {
        int radioId = mRadioGroup.getCheckedRadioButtonId();
        mRadioButton = findViewById(radioId);
    }
}
