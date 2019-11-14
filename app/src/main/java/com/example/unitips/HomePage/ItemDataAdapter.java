package com.example.unitips.HomePage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitips.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ItemDataAdapter extends FirestoreRecyclerAdapter<ItemData, ItemDataAdapter.ItemDataHolder> {
    private OnItemClickListener listener;

    public ItemDataAdapter(@NonNull FirestoreRecyclerOptions<ItemData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemDataHolder itemDataHolder, int i, @NonNull ItemData itemData) {
        itemDataHolder.textViewTitle.setText(itemData.getTitle());
        itemDataHolder.textViewDescription.setText(itemData.getDescription());
        itemDataHolder.textViewCategory.setText(String.valueOf(itemData.getCategory()));
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

