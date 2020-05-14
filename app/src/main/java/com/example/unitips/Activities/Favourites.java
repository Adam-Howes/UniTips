package com.example.unitips.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitips.Adapters.FavouritesItemDataAdapter;
import com.example.unitips.Constructors.FavouritesItemData;
import com.example.unitips.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class Favourites extends AppCompatActivity {

    // Firebase
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference favouriteRef = db.collection("users/" + currentFirebaseUser.getUid() + "/favourites");

    // Adapter
    private FavouritesItemDataAdapter adapter;
    long postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setTitle("Favourites");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setUpRecyclerView();

        // Handles post navigation when user presses a card
        adapter.setOnItemClickListener(new FavouritesItemDataAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                String id = documentSnapshot.getId();
                postID = Long.parseLong(id);

                Intent intent1 = new Intent(getApplicationContext(), ViewContent.class);
                intent1.putExtra("postID", postID);
                startActivity(intent1);
            }
        });
    }

    private void setUpRecyclerView() {
        Query query = favouriteRef.orderBy("category", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<FavouritesItemData> options = new FirestoreRecyclerOptions.Builder<FavouritesItemData>()
                .setQuery(query, FavouritesItemData.class)
                .build();

        adapter = new FavouritesItemDataAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.favourites_recycler_view);
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
}
