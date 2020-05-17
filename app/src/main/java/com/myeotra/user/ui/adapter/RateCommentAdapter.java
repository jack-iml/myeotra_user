package com.myeotra.user.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myeotra.user.R;
import com.myeotra.user.data.network.model.RateCommentResponse;

import java.util.List;

public class RateCommentAdapter extends RecyclerView.Adapter<RateCommentAdapter.MyViewHolder> {

    Activity activity;
    List<RateCommentResponse.RateComment> rateComment;

    CommentClickListner ratingDialogFragment;

    public RateCommentAdapter(Activity activity, List<RateCommentResponse.RateComment> rateComment) {
        this.activity = activity;
        this.rateComment = rateComment;
    }

    public void RegisterInterface(CommentClickListner ratingDialogFragment) {
        this.ratingDialogFragment = ratingDialogFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_rate_comment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RateCommentResponse.RateComment mode = rateComment.get(position);
        holder.txtComment.setText(mode.getComment());

        holder.txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingDialogFragment != null) {
                    ratingDialogFragment.commentListClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rateComment.size();
    }

    public interface CommentClickListner {
        void commentListClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }
}
