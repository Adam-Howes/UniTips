package com.example.unitips.ViewContent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unitips.R;
import com.example.unitips.Report;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewContent extends AppCompatActivity {

    // Activity Items
    private TextView textViewTitle;
    private TextView textViewVoteCount;
    private TextView textViewDescription;
    private String navBarTitle;

    private ImageView imageView0;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;

    private TextView mDescription0;
    private TextView mDescription1;
    private TextView mDescription2;
    private TextView mDescription3;
    private TextView mDescription4;

    private String description0;
    private String description1;
    private String description2;
    private String description3;
    private String description4;

    private Button mStartWritingCommentButton;
    private EditText mStartWritingCommentEditText;
    private Button mPostCommentButton;

    String voteCount;

    // Previous activity
    long postID;

    // Firebase
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference postRef;
    private CollectionReference commentRef;
    private CommentAdapter adapter;

    private String imageUrl0;
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    private String imageUrl4;

    // Firebase Data
    private Long currentVote;
    private String postTitle;
    private String postDescription;

    // Action Bar logic
    private boolean upVote = false;
    private boolean downVote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_content);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        textViewTitle = findViewById(R.id.view_content_post_title);
        textViewDescription = findViewById(R.id.view_content_post_description);
        textViewVoteCount = findViewById(R.id.view_content_post_vote_count);

        imageView0 = findViewById(R.id.view_content_image_view_0);
        imageView1 = findViewById(R.id.view_content_image_view_1);
        imageView2 = findViewById(R.id.view_content_image_view_2);
        imageView3 = findViewById(R.id.view_content_image_view_3);
        imageView4 = findViewById(R.id.view_content_image_view_4);

        mDescription0 = findViewById(R.id.view_content_description_0);
        mDescription1 = findViewById(R.id.view_content_description_1);
        mDescription2 = findViewById(R.id.view_content_description_2);
        mDescription3 = findViewById(R.id.view_content_description_3);
        mDescription4 = findViewById(R.id.view_content_description_4);

        mStartWritingCommentButton = findViewById(R.id.view_content_add_comment_button);
        mPostCommentButton = findViewById(R.id.view_content_post_comment_button);
        mStartWritingCommentEditText = findViewById(R.id.view_content_add_comment_edit_text);

        // Loads the location of the post from firebase
        Intent intent = getIntent();
        postID = intent.getLongExtra("postID", postID);
        postRef = db.document("posts/" + postID);
        commentRef = db.collection("posts/" + postID + "/comments");

        setUpRecyclerView();
        onStart();

        loadInfo();
        loadImages();

        mStartWritingCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartWritingCommentButton.setVisibility(View.GONE);
                mPostCommentButton.setVisibility(View.VISIBLE);
                mStartWritingCommentEditText.setVisibility(View.VISIBLE);
            }
        });
        mPostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    public void loadInfo() {
        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    postTitle = documentSnapshot.getString("title");
                    textViewTitle.setText(postTitle);

                    postDescription = documentSnapshot.getString("description");
                    textViewDescription.setText(postDescription);

                    currentVote = documentSnapshot.getLong("voteCount");
                    voteCount = "Current Votes: " + currentVote.toString();
                    textViewVoteCount.setText(voteCount);

                    navBarTitle = documentSnapshot.getString("category");
                    setTitle(navBarTitle);

                    description0 = documentSnapshot.getString("description0");
                    if (description0 != null) {
                        mDescription0.setText(description0);
                        mDescription0.setVisibility(View.VISIBLE);
                    }
                    description1 = documentSnapshot.getString("description1");
                    if (description1 != null) {
                        mDescription1.setText(description1);
                        mDescription1.setVisibility(View.VISIBLE);
                    }
                    description2 = documentSnapshot.getString("description2");
                    if (description2 != null) {
                        mDescription2.setText(description2);
                        mDescription2.setVisibility(View.VISIBLE);
                    }
                    description3 = documentSnapshot.getString("description3");
                    if (description3 != null) {
                        mDescription3.setText(description3);
                        mDescription3.setVisibility(View.VISIBLE);
                    }
                    description4 = documentSnapshot.getString("description4");
                    if (description4 != null) {
                        mDescription4.setText(description4);
                        mDescription4.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ViewContent.this, "Post error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void loadImages() {
        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                imageUrl0 = documentSnapshot.getString("image_file_url_0");
                if (!String.valueOf(imageUrl0).isEmpty())
                    Glide.with(ViewContent.this)
                            .load(imageUrl0)
                            .into(imageView0);

                imageUrl1 = documentSnapshot.getString("image_file_url_1");
                if (!String.valueOf(imageUrl1).isEmpty())
                    Glide.with(ViewContent.this)
                            .load(imageUrl1)
                            .into(imageView1);

                imageUrl2 = documentSnapshot.getString("image_file_url_2");
                if (!String.valueOf(imageUrl2).isEmpty())
                    Glide.with(ViewContent.this)
                            .load(imageUrl2)
                            .into(imageView2);

                imageUrl3 = documentSnapshot.getString("image_file_url_3");
                if (!String.valueOf(imageUrl3).isEmpty())
                    Glide.with(ViewContent.this)
                            .load(imageUrl3)
                            .into(imageView3);

                imageUrl4 = documentSnapshot.getString("image_file_url_4");
                if (!String.valueOf(imageUrl4).isEmpty())
                    Glide.with(ViewContent.this)
                            .load(imageUrl4)
                            .into(imageView4);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewContent.this, "Image loading error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // Recycler View For comments
    private void setUpRecyclerView() {
        Query query = commentRef.orderBy("commentPostDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions
                .Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.view_content_recycler_view_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    // User Can post a comment, uploading it to firebase
    private void postComment() {
        String comment = mStartWritingCommentEditText.getText().toString().trim();
        String commentDate = String.valueOf(System.currentTimeMillis());
        if (comment.isEmpty()) {
            Toast.makeText(ViewContent.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, String> postComment = new HashMap<>();
            postComment.put("commentPostDate", commentDate);
            postComment.put("commentDescription", comment);
            postComment.put("commentUserName", currentFirebaseUser.getUid());

            db.collection("posts").document(String.valueOf(postID))
                    .collection("comments")
                    .document(commentDate)
                    .set(postComment);

            mStartWritingCommentButton.setVisibility(View.VISIBLE);
            mStartWritingCommentEditText.setVisibility(View.GONE);
            mPostCommentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_content_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Map<String, Object> favourites = new HashMap<>();
                favourites.put("description", postDescription);
                favourites.put("category",navBarTitle);
                favourites.put("title", postTitle);
                favourites.put("post_id", postID);
                favourites.put("date_added", String.valueOf(System.currentTimeMillis()));
                db.collection("users/" + currentFirebaseUser.getUid() + "/favourites")
                        .document(String.valueOf(postID))
                        .set(favourites)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ViewContent.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewContent.this, "There was an error, please try again", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            case R.id.item2:
                if (!upVote) {
                    currentVote++;
                    postRef.update("voteCount", currentVote);
                    upVote = true;
                    voteCount = "Current Votes: " + currentVote.toString();
                    textViewVoteCount.setText(voteCount);
                }
                return true;
            case R.id.item3:

                if (!downVote) {
                    currentVote--;
                    postRef.update("voteCount", currentVote);
                    downVote = true;
                    voteCount = "Current Votes: " + currentVote.toString();
                    textViewVoteCount.setText(voteCount);
                }

                return true;
            case R.id.item4:
                Intent intent = new Intent(ViewContent.this, Report.class);
                intent.putExtra("postID", postID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
