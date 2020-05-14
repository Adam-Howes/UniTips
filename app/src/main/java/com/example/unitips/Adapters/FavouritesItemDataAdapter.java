package com.example.unitips.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitips.Constructors.FavouritesItemData;
import com.example.unitips.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class FavouritesItemDataAdapter extends FirestoreRecyclerAdapter<FavouritesItemData, FavouritesItemDataAdapter.ItemDataHolder> {
    private OnItemClickListener listener;

    public FavouritesItemDataAdapter(@NonNull FirestoreRecyclerOptions<FavouritesItemData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemDataHolder itemDataHolder, int i, @NonNull FavouritesItemData favouritesItemData) {
        itemDataHolder.textViewTitle.setText(favouritesItemData.getTitle());
        itemDataHolder.textViewDescription.setText(favouritesItemData.getDescription());
        itemDataHolder.textViewCategory.setText(String.valueOf(favouritesItemData.getCategory()));
    }

    @NonNull
    @Override
    public ItemDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        return new ItemDataHolder(v);
    }

    class ItemDataHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewCategory;

        public ItemDataHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewCategory = itemView.findViewById(R.id.text_view_category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), (position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

