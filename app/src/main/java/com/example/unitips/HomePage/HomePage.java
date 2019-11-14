package com.example.unitips.HomePage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.unitips.CreateContent;
import com.example.unitips.Favourites.Favourites;
import com.example.unitips.R;
import com.example.unitips.SignIn;
import com.example.unitips.ViewContent.ViewContent;
import com.example.unitips.ViewProfile;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePage extends AppCompatActivity {

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dataRef = db.collection("posts");

    // Activity
    private ItemDataAdapter adapter;
    long postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        FloatingActionButton fab = findViewById(R.id.home_page_new_post_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, CreateContent.class));
            }
        });

        setUpRecyclerView();

        // Handles post navigation when user presses a card
        adapter.setOnItemClickListener(new ItemDataAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                String id = documentSnapshot.getId();
                postID = Long.parseLong(id);

                Intent intent = new Intent(HomePage.this, ViewContent.class);
                intent.putExtra("postID", postID);
                startActivity(intent);
            }
        });
    }

    private void setUpRecyclerView() {
        // This is the order in which the data is gathered
        Query query = dataRef.orderBy("category", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ItemData> options = new FirestoreRecyclerOptions
                .Builder<ItemData>()
                .setQuery(query, ItemData.class)
                .build();
        adapter = new ItemDataAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.home_page_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_page_menu, menu);
        return true;
    }

    // Options menu for home page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                // View Profile
                startActivity(new Intent(this, ViewProfile.class));
                return true;
            case R.id.item2:
                // Signs the user out
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignIn.class));
                return true;
            case R.id.item3:
                startActivity(new Intent(this, Favourites.class));
            default:
                return super.onOptionsItemSelected(item);
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
