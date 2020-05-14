package com.example.unitips.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitips.Constructors.Comment;
import com.example.unitips.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder> {

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder commentHolder, int i, @NonNull Comment comment) {
        commentHolder.textViewComment.setText(comment.getCommentDescription());
        commentHolder.textViewCommentUserName.setText(comment.getCommentUserName());
        commentHolder.textViewCommentDate.setText(String.valueOf(comment.getCommentPostDate()));
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(v);
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        TextView textViewComment;
        TextView textViewCommentDate;
        TextView textViewCommentUserName;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            textViewComment = itemView.findViewById(R.id.comment_item_comment);
            textViewCommentDate = itemView.findViewById(R.id.comment_date_posted);
            textViewCommentUserName = itemView.findViewById(R.id.comment_item_user_name);
        }
    }
}
