package com.example.unitips.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitips.Adapters.ItemDataAdapter;
import com.example.unitips.Constructors.ItemData;
import com.example.unitips.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dataRef = db.collection("posts");
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference mUserDatabaseRef = db.document("users/" + currentFirebaseUser.getUid());
    private DatabaseReference mUserImageDatabaseRef = FirebaseDatabase.getInstance().getReference("profile_pictures");
    // Activity
    private ItemDataAdapter recyclerViewAdapter;
    long postID;

    private ImageView userProfilePicture;
    private TextView userEmailAddress;
    private TextView userName;

    // Navigation Menu
    private DrawerLayout navigationDrawer;

    private Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpRecyclerView();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationDrawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, navigationDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationDrawer.addDrawerListener(toggle);
        toggle.syncState();


        // Handles post navigation when user presses a card
        recyclerViewAdapter.setOnItemClickListener(new ItemDataAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                String id = documentSnapshot.getId();
                postID = Long.parseLong(id);

                Intent intent = new Intent(HomePage.this, ViewContent.class);
                intent.putExtra("postID", postID);
                startActivity(intent);
            }
        });


        // TODO: Recycler view should load a thumbnail for each post
    }

    private void setUpRecyclerView() {
        // This is the order in which the data is gathered
        Query query = dataRef.orderBy("category", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ItemData> options = new FirestoreRecyclerOptions
                .Builder<ItemData>()
                .setQuery(query, ItemData.class)
                .build();
        recyclerViewAdapter = new ItemDataAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.home_page_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerViewAdapter.stopListening();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // TODO: think of items to add to the nav drawer

        switch (item.getItemId()) {

            case R.id.nav_header_profile_icon:
                // case R.id.nav_header_profile_username:
                // case R.id.nav_header_profile_email:
                startActivity(new Intent(this, ViewProfile.class));
                Toast.makeText(HomePage.this, "view profile", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_sign_out:
                // TODO: on success listener
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignIn.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, UserSettings.class));
                break;

            case R.id.nav_favourites:
                startActivity(new Intent(this, Favourites.class));
                break;

            // TODO: implement activities
            case R.id.nav_legal:
                Toast.makeText(HomePage.this, "Legal Stuff", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_report_problem:
                Toast.makeText(HomePage.this, "Report Problem", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_rate_app:
                Toast.makeText(HomePage.this, "Rate App", Toast.LENGTH_LONG).show();
                break;
        }

        navigationDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {

        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START);
        }
        // Minimise app
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }
}
